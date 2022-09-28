package com.example.qcaudioad.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Switch;

import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.example.qcaudioad.R;
import com.example.qcaudioad.base.BaseActivity;
import com.example.qcaudioad.utils.SpUtils;

public class SettingActivity extends BaseActivity {

    private final String SWITCH_LOCATION = "switch_location";
    private final String SWITCH_MICROPHONE = "switch_microphone";
    private final String SWITCH_DEVICE = "switch_device";

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Switch location = findViewById(R.id.switch_location);
        Switch microphone = findViewById(R.id.switch_microphone);
        Switch device = findViewById(R.id.switch_device);

        location.setChecked(SpUtils.getBoolean(SWITCH_LOCATION, true));
        microphone.setChecked(SpUtils.getBoolean(SWITCH_MICROPHONE, true));
        device.setChecked(SpUtils.getBoolean(SWITCH_DEVICE, true));

        location.setOnCheckedChangeListener((buttonView, isChecked) -> {
            QCiVoiceSdk.get().setLocationPermission(isChecked);
            SpUtils.saveBoolean(SWITCH_LOCATION,isChecked);
        });

        microphone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            QCiVoiceSdk.get().setMicrophonePermission(isChecked);
            SpUtils.saveBoolean(SWITCH_MICROPHONE,isChecked);
        });

        device.setOnCheckedChangeListener((buttonView, isChecked) -> {
            QCiVoiceSdk.get().setDevicePermission(isChecked);
            SpUtils.saveBoolean(SWITCH_DEVICE,isChecked);
        });

    }
}