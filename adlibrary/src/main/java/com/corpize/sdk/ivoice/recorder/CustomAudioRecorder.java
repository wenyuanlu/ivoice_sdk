package com.corpize.sdk.ivoice.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.corpize.sdk.ivoice.utils.ThreadManagerUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CustomAudioRecorder {

    private static CustomAudioRecorder mInstance;
    private        AudioRecord         recorder;
    //录音源
    private static int                 audioSource  = MediaRecorder.AudioSource.MIC;
    //录音的采样频率,采样率改成16000，减少音频体积
    private static int                 audioRate    = 16000;
    //录音的声道，单声道
    private static int                 audioChannel = AudioFormat.CHANNEL_IN_MONO;
    //量化的深度
    private static int                 audioFormat  = AudioFormat.ENCODING_PCM_16BIT;
    //缓存的大小
    private static int                 bufferSize   = AudioRecord.getMinBufferSize(audioRate, audioChannel, audioFormat);
    //记录播放状态
    private        boolean             isRecording  = false;
    //数字信号数组
    private        byte[]              noteArray;
    //PCM文件
    private        File                pcmFile;
    //WAV文件
    private        File                wavFile;
    //文件根目录
    private        String              basePath     = "";
    //wav文件目录
    private        String              outFileName  = ".wav";
    //pcm文件目录
    private        String              inFileName   = ".pcm";

    private OnRecorderListener onRecorderListener;

    public boolean isRecording () {
        return isRecording;
    }

    public void setBasePath (String basePath) {
        this.basePath = basePath;
    }

    public void setOnRecorderListener (OnRecorderListener onRecorderListener) {
        this.onRecorderListener = onRecorderListener;
    }

    private CustomAudioRecorder () {
        recorder = new AudioRecord(audioSource, audioRate, audioChannel, audioFormat, bufferSize);
    }

    public synchronized static CustomAudioRecorder getInstance () {
        if (mInstance == null) {
            mInstance = new CustomAudioRecorder();
        }
        return mInstance;
    }

    //读取录音数字数据线程
    private class WriteThread implements Runnable {
        public void run () {
            writeData();
        }
    }

    //开始录音
    public void startRecord () {
        try {
            createFile();

            isRecording = true;
            recorder.startRecording();
            recordData();
            if (onRecorderListener != null) {
                onRecorderListener.onStartRecorder();
            }
        } catch (Exception e) {
            e.printStackTrace();

            if (onRecorderListener != null) {
                onRecorderListener.onFailureRecorder();
            }
        }
    }

    //停止录音
    public void stopRecord () {
        try {
            isRecording = false;
            recorder.stop();

            convertWaveFile();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消录音
     */
    public void cancelRecord () {
        try {
            isRecording = false;
            recorder.stop();

            //删除pcm文件和wav文件
            if(pcmFile.exists()){
                pcmFile.delete();
            }
            if (wavFile.exists()){
                wavFile.delete();
            }

            onRecorderListener.onCancelRecorder();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    //将数据写入文件夹,文件的写入没有做优化
    public void writeData () {
        noteArray = new byte[bufferSize];
        //建立文件输出流
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(pcmFile))) {
            while (isRecording) {
                int recordSize = recorder.read(noteArray, 0, bufferSize);
                if (recordSize > 0) {
                    try {
                        os.write(noteArray);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 这里得到可播放的音频文件
    private void convertWaveFile () {
        long   totalAudioLen  = 0;
        long   totalDataLen   = totalAudioLen + 36;
        long   longSampleRate = CustomAudioRecorder.audioRate;
        int    channels       = 1;
        long   byteRate       = 16 * CustomAudioRecorder.audioRate * channels / 8;
        byte[] data           = new byte[bufferSize];
        try (FileInputStream in = new FileInputStream(pcmFile);
             FileOutputStream out = new FileOutputStream(wavFile)) {
            totalAudioLen = in.getChannel().size();
            //由于不包括RIFF和WAV
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }

            if(pcmFile.exists()){
                pcmFile.delete();
            }

            if (onRecorderListener != null) {
                onRecorderListener.onStopRecorder(wavFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 任何一种文件在头部添加相应的头文件才能够确定的表示这种文件的格式，wave是RIFF文件结构，每一部分为一个chunk，其中有RIFF WAVE chunk， FMT Chunk，Fact chunk,Data chunk,其中Fact chunk是可以选择的， */
    private void WriteWaveFileHeader (FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
            int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (1 * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

    //创建文件夹,首先创建目录，然后创建对应的文件
    private void createFile () {
        pcmFile = new File(basePath + inFileName);
        wavFile = new File(basePath + outFileName);

        if (pcmFile.exists()) {
            pcmFile.delete();
        }

        if (wavFile.exists()) {
            wavFile.delete();
        }

        try {
            pcmFile.createNewFile();
            wavFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //记录数据
    private void recordData () {
        ThreadManagerUtil.getDefaultProxy().execute(new WriteThread());
    }
}
