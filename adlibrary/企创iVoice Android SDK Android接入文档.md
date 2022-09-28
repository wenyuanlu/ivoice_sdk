# Android SDK 接入文档

 

**2020-04-29**

| 版本号 | 日期       | 字段 | 说明                            |
| ------ | ---------- | ---- | --------------------------- |
| 1.0.0  | -          | -    | beta版本                     |
| 1.0.0  | 2021/1/6   | -    | release                     |
| 1.0.1  | 2021/3/8   | -    | 支持4.0，支持support          |
| 1.0.2  | 2021/3/12  | -    | 优化了焦点的请求               |
| 1.0.3  | 2021/03/22 | -    | 优化了默认状态及焦点请求         |
| 1.0.4  | 2021-04-29 | -    | 同时兼容androidx  及support    |
| 1.0.5  | 2021-05-13 | -    | 根据权限设置落地页              |
| 1.0.6  | 2021-07-19 | -    | 添加首听/冠名                  |
| 1.0.7  | 2021-08-10 | -    | 更新oaid调用方式               |
| 1.1.1  | 2021-12-1  | -    | 添加dnt / 自定义渲染模板样式添加  |
| 1.1.3  | 2022-2-28  | -    | 添加车载自动轮播广告形式          |
| 1.1.8  | 2022-4-22  | -    | 提供接口设置定位、麦克风、设备号权限 |

# 一、背景 

## 1.1 开发环境

| 字段      | 说明                                      |
| -------- | ----------------------------------------  |
| 开发工具  | Android  Studio + Gradle 3.4.1 + JDK 1.8  |
| 部署目标  | Android  4.0(14)及以上的版本                |
| 支持设备  | Android  4.0(14)系统及以上的手机             |
| 开发语言  | java                                      |
| 开发环境  | 支持androidX和support的编译                 |

 

## 1.2 版本依赖
|        | androidx    | support | Api >= 21   | Api < 21 |
| ------ | ----------- | ------- | ----------- | -------- |
| glide  | lastRelease | 4.9.0   | lastRelease | 4.9.0    |
| okhttp | lastRelease | 4.9.0   | lastRelease | 3.9.1    |
```
Android x与support版本依赖组件不同,请根据不同需求进行版本依赖
```


## 1.3 OAID
新版本更新oaid依赖,请参考本文档及simple进行调用集成,oaid的申请及配置方式请参考[移动安全联盟官网](http://www.msa-alliance.cn)



## 1.4 其他 
1.本文档建议阅读器为[Typora](https://www.typora.io/)
**2.请仔细阅读本文档并参考simple进行sdk集成**


# 二、项目集成

## 2.1、平台接入流程
```
对于想要嵌入企创广告功能的用户，需要开发者在企创系统里注册会员 
```
## 2.2、接入配置 (详情见Demo)

### 2.2.1 导包
```
1.把com.corpize.sdk.ivoice.aar包导入工程libs下方；
2.根据[移动安全联盟官网](http://www.msa-alliance.cn)集成oaid
3.导入libcorpize-lib.so文件(导入需要使用的so架构)。
```
###  

### 2.2.2 接入build.gradle配置

在build.gradle中配置aar

```
android{
    defaultConfig{
        ndk{
            //加载需要的so库架构
            abiFilters 'armeabi-v7a', 'x86', 'arm64-v8a', 'x86_64'/*, 'armeabi'*/
        }
}

sourceSets {
    main{
        jniLibs.srcDirs = ['libs']
        }
    }
 
    repositories {
        flatDir {
            dirs 'libs'
        }
    }
}
 
Dependencies{
    //ad sdk need implementation start
    implementation 'com.github.bumptech.glide:glide:4.9.0'
    implementation 'com.github.bumptech.glide:annotations:4.9.0'
    implementation 'com.github.bumptech.glide:okhttp3-integration:4.9.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.9.0'
    implementation 'com.squareup.okhttp3:okhttp:3.9.1'
    implementation 'com.google.code.gson:gson:2.8.6'
 
    //library
    //api project(':adlibrary')
    implementation(name: 'com.corpize.sdk.ivoice_test', ext: 'aar')
    //ad sdk need implementation end
    implementation 'com.android.support:multidex:1.0.3'
}
```

**注:以上依赖版本请参考实际开发环境及1.2版本依赖进行添加**

### 2.2.3 配置AndroidManifest.xml文件 配置对应的权限
```
<!--需要权限 -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WAKE_LOCK" /> 
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<!-- 录音权限 -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<!-- 安装apk -->
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />

<application>
    <provider
         android:name="com.corpize.sdk.ivoice.utils.downloadinstaller.QcDownloadProvider"
         android:authorities="${applicationId}.QcDownloadProvider"
         android:exported="false"
         android:grantUriPermissions="true"
         tools:replace="android:authorities">
     
    <meta-data
          android:name="android.support.FILE_PROVIDER_PATHS"
          android:resource="@xml/qc_paths"
          tools:replace="android:resource" />
    </provider>
</application>
```
### 2.2.4 通用的方法
| 字段                | 说明                                                    |
| --------           | ------------------------------------------------        |
| UserPlayInfoBean   | 类名                                                     |
| title              | 音频的主要信息，如歌曲名,非必填,可为null                       |
| url                | 音频地址，如歌曲的文件地址,非必填,可为null                     |
| desc               | 音频描述,非必填,可为null                                    |
| image              | 音频封面,非必填,可为null                                    |
| progress           | 播放进度,0 - 100 ,100为完整播放,非必填,可为null               |

### 2.2.5 广告管理类ADManger通用的方法
| 字段                | 说明                                                     |
| --------           | ------------------------------------------------        |
| onResume           | 手动绑定生命周期                                           |
| onPause            | 手动绑定生命周期                                           |
| destroy            | 手动绑定生命周期                                           |
| startPlayAd        | 开始播放广告                                              |
| skipPlayAd         | 停止播放广告                                              |
| resumePlayAd       | 允许继续播放广告的前提下,继续播放广告                          |
| onActivityResult   | 手动绑定对于界面跳转的监听                                   |

## 2.3、接入代码

### 2.3.1 初始化 

1、在application的onCreate()方法中调用初始化方法

注:OAID的获取参考 simple 中的OAIDHelper,具体初始化代码参考simple 中BaseApplication类
1.mid为媒体ID,为应用专属id,详情咨询商务.
2.dnt为是否允许广告追踪,0不允许,1允许
```
//初始化广告
QCiVoiceSdk.get().init(this, oaid,mid,dnt);
```
2、在每一个使用广告的Activity中，在onResume()，onPause()，onDestroy(),
onActivityResult()四个方法中调用下列方法： 
```
@Override
protected void onResume () {
  super.onResume();
 	//必须调用
  QCiVoiceSdk.get().onResume();
}
    
@Override
protected void onPause () {
  super.onPause();
  //必须调用
  QCiVoiceSdk.get().onPause();
}
    
@Override
protected void onDestroy () {
  super.onDestroy();
  //释放内存
  QCiVoiceSdk.get().onDestroy();
}
    
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
  super.onActivityResult(requestCode, resultCode, data);
  //选择调用（如果需要点击广告返回立马回调则需要调用）
  QCiVoiceSdk.get().onActivityResult(requestCode,resultCode,data);
}
```

### 2.3.2 音频互动广告调用方法

1、调用方法：
```
QCiVoiceSdk.get().addAudioAd(adAttr, listener);
```
返回广告：1、息屏状态：音频形式广告

2、亮屏状态：音频+插屏形式广告，或展示全屏视频广告
示例如下：

```
String adId = "****************************************";//传入平台申请的adid，必传
//第一步,设置广告的adId
AdAttr adAttr = AdAttr.newBuild().setAdid(adId);
//第二步,创建广告调用,必须调用
QCiVoiceSdk.get().createAdNative(activity);
//第三步,获取广告
QCiVoiceSdk.get().addAudioAd(adAttr, new AudioQcAdListener() {
    @Override
    public void onAdReceive (QcAdManager manager) {
         Log.e(TAG, "onADReceive");
         //第四步,展示广告
         manager.startPlayAd();
    }
    
    @Override
    public void onAdExposure () {
    }
    
    @Override
    public void onAdClick () {
    }
    
    @Override
    public void onAdCompletion () {
    }
    
    @Override
    public void onAdClose () {
    }
    
    @Override
    public void onAdError (String fail) {
    }
});
```
3、listener 回调方法:

| **方法/参数名称** | **描述**         | **备注**                |
| ----------------- | ---------------- | ----------------------- |
| onAdReceive       | 返回广告管理对象 | 调用startPlayAd展示广告       |
| onAdExposure      | 广告的曝光       |                            |
| onAdClick         | 广告的点击       |                            |
| onAdCompletion    | 音频广告播放完成  | 视频广告没有此回调             |
| onAdClose         | 广告关闭         |                           |
| onAdError         | 错误回调         |                           |
####  

### 2.3.3 沉浸式自渲染互动音频广告调用方法

1.调用方法：

```
QCiVoiceSdk.get().addCustomAudioAd(adAttr, listener);
```
2.列表中接入多条广告需要使用下面这种方式调用（没添加onActivityResult可忽略），position为列表的index
```
QCiVoiceSdk.get().addCustomAudioAd(position，adAttr, listener);
```
3.在Actiity的布局中，预留广告的控件位置，把广告返回的view添加到预留的控件中
示例如下：
```
//参数的获取
Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
Drawable palyDrawable = getResources().getDrawable(R.mipmap.icon_play);
Drawable pauseDrawable = getResources().getDrawable(R.mipmap.icon_pause);
Drawable barrageDrawable = getResources().getDrawable(R.mipmap.icon_barrage);
Drawable priseChooseD= getResources().getDrawable(R.mipmap.prise_choose);
Drawable priseDefaultD = getResources().getDrawable(R.mipmap.prise_default);

List<String> barrageContentColorList = new ArrayList<>();
barrageContentColorList.add("#CE608C");
barrageContentColorList.add("#BC7D2F");
barrageContentColorList.add("#5C72D5");
barrageContentColorList.add("#23A69E");

String adIdCustom = "************************************";//传入平台申请的adid，必传
String midCustom = "************************************";//传入平台申请的mid，必传

List<UserPlayInfoBean> UserPlayInfoBeanDataList = getUserPlayInfoBeans();
//【可选功能，非必传】ivoice的sdk需要让媒体方在sdk方法中提交用户在广告请求前所听过的最近信息（1组起，最大不超过20组），
// 信息字段包含：标题（专辑／歌曲名称+作者+歌手等英文逗号分隔）、音频文件url、结束播放时的进度百分比，
//组信息在sdk发送广告请求时携带上来。推荐提交5-10组，最大不超过20组，超过20组将只截取前20条

//第一步,设置广告的adId和自定义的参数
AdAttr attr = AdAttr.newBuild()
.setAdid(adIdCustom)//设置广告adid 必传
.setMid(midCustom )//设置广告mid 必传
.setCanLeftTouch(true)//设置是否支持往左滑动跳出落地页等方法,默认false,设置true时,在list中会拦截原有的滑动事件
.setLabel(UserPlayInfoBeanDataList )//可选功能，非必传。 提交用户收听的上下文音频有助于提高表现单价金额

//********设置背景图片的方法********//
.setBackgroundLayout(AdLayout.TOP_LEFT)//设置背景图位置,默认是TOP_LEFT
.setBackgroundMagin(0, 0, 0, 0)//设置背景图片的magin值,单位dp
.setBackgroundSize(width, height)//设置背景图宽和高,单位px默认match_parent

//********设置Title的方法********//
.setTitleColor(Color.parseColor("#FFFFFFFF"))//设置标题的颜色#3D3B3B
.setTitleSize(18)//设置标题的大小,单位sp
.setTitleLayout(AdLayout.TOP_LEFT)//设置标题的位置,默认是TOP_LEFT
.setTitleMagin(15, 20, 0, 0)//设置标题的magin值,单位dp
.setTitleTextMaxSize(5)//设置title单行最大字数
.setTitleTextMaxLines(2)//设置title最大行数

//********设置Content的方法********//
.setContentColor(Color.parseColor("#FFFFFFFF"))//设置展示内容颜色6D6D6D
.setContentSize(14)//设置展示内容的大小,单位sp
.setContentLayout(AdLayout.TOP_LEFT)//设置展示内容的位置,默认在标题下方
.setContentMagin(15, 0, 130, 0)//设置内容的magin值,单位dp

//********设置左下角的Info窗口的方法********//
.setInfoTitleColor(Color.parseColor("#FF333333"))//设置信息的标题颜色
.setInfoContentColor(Color.parseColor("#FF666666"))//设置信息的内容颜色
.setInfoButtonColor(Color.parseColor("#FFFFFFFF"))//设置信息内部按钮颜色
.setInfoButtonBackgroundColor(Color.parseColor("#FF5BC0DE"))//设置信息内部按钮背景颜色
.setInfoLayout(AdLayout.BOTTOM_LEFT)//设置信息位置,默认是BOTTOM_LEFT
.setInfoMagin(0, 0, 15, 20)//设置信息的magin值,单位dp

//********设置右下角头像的方法********//
.setAdHeadSize(40)//设置logo的大小,宽高一致,默认40
.setAdHeadType(CoverType.OVAL)//设置logo的显示的样式
.setShowHeadLinkImage(true)//设置是否在logo上方显示logo图片,默认true显示
.setAdHeadLayout(AdLayout.BOTTOM_RIGHT)//设置logo位置
.setAdHeadMagin(0, 0, 15, 190)//设置Logo的magin值,单位dp

//********设置右下角点赞图片的方法********//
.setPraiseChooseImage(priseChooseD)//设置点赞图片,不设置显示默认图片
.setPraiseDefaultImage(priseDefaultD)//设置未点赞图片,不设置显示默认图片
.setPraiseImageSize(40)//设置点赞图片的大小,单位dp,默认40
.setPraiseImageLayout(AdLayout.BOTTOM_RIGHT)//设置点赞图片位置
.setPraiseImageMagin(0, 0, 15, 130)//设置点赞图片的magin值,单位dp

//********设置右下角点赞数量的方法********//
.setPraiseNumberColor(Color.parseColor("#FFFFFFFF"))//设置点赞数量的颜色
.setPraiseNumberSize(12)//设置点赞数量的大小,单位sp 默认12
.setPraiseNumberWidth(40)//设置点赞数量控件的宽度,单位dp 默认40
.setPraiseNumberLayout(AdLayout.BOTTOM_RIGHT)//设置点赞数量的位置
.setPraiseNumberMagin(0, 0, 15, 110)//设置点赞数量的的magin值,单位dp

//********设置右下角弹幕图片的方法********//
.setBarrageImage(barrageDrawable)//设置弹幕图片,不设置显示默认图片
.setBarrageImageSize(40)//设置弹幕图片的大小,单位dp,默认40
.setBarrageImageLayout(AdLayout.BOTTOM_RIGHT)
.setBarrageImageMagin(0, 0, 15, 60)//设置弹幕图片的magin值,单位dp

//********设置右下角弹幕数量的方法********//
.setBarrageNumberColor(Color.parseColor("#FFFFFF"))//设置弹幕数量颜色
.setBarrageNumberSize(12)//设置弹幕数量的大小,单位sp,默认12
.setBarrageNumberWidth(40)//设置弹幕数量控件的宽度,单位dp 默认40
.setBarrageNumberLayout(AdLayout.BOTTOM_RIGHT)//设置弹幕数量的位置
.setBarrageNumberMagin(0, 0, 15, 40)//设置弹幕数量的magin值,单位dp

//********设置中间封面图片的方法********//
.setCoverSize(200)//设置封面图片的大小 ,单位dp 默认
.setCoverType(CoverType.ROUND)//设置封面图片显示的样式,默认圆形
.setCoverLayout(AdLayout.CENTER_HORIZONTAL)//设置封面图片的位置
.setCoverMagin(0, 150, 0, 0)//设置封面图片的magin值,单位dp

//********设置封面上播放按钮的方法********//
.setMusicBtPlayImage(palyDrawable)//设置播放按钮的图片,不设置显示默认图片
.setMusicBtPauseImage(pauseDrawable)//设置暂停按钮的图片，不设置默认图
.setMusicBtSize(50)//设置播放按钮的大小 ,单位dp 默认50
.setMusicBtLayout(AdLayout.TOP_LEFT, false)//设置播放按钮的位置
.setMusicBtLayout(AdLayout.CENTER_IN_PARENT)//设置播放按钮的位置
.setMusicBtMagin(10, 60, 30, 10)//设置播放按钮的magin值,单位dp

//********设置滚动弹幕方法********//
.setShowBarrage(true)//是否展示弹幕
.setBarrageContentSize(12)//设置滚动弹幕内容的大小 单位sp 默认14
.setBarrageContentColor(barrageContentColorList)//设置滚动弹幕内容的颜色

.setBarrageBackColor("#FFFFFF")//设置滚动弹幕背景颜色
.setBarrageHeadSize(18)//设置滚动弹幕头像的大小 单位dp 默认ContentSize+6
.setBarrageMagin(0, 50, 0, 100)//设置滚动弹幕的magin值,单位dp
.setBarrageSpeed(3)//设置滚动弹幕的滚动速度，默认为3，建议3-10

//********设置跳过********//
.setSkipIsEnable(true)//是否启用跳过
.setSkipGravity(RelativeLayout.ALIGN_PARENT_RIGHT)//设置跳过控件位置,具体参见RelativeLayout.LayoutParams.addRule()方法
.setSkipMargin(0, 15, 15, 0)
.setSkipAutoClose(false)//设置跳过倒计时结束后是否自动关闭该广告
//********设置是否启用右下角********//
.setEnableRightView(false)
;
```
```
//第二步,创建广告，必须调用
QCiVoiceSdk.get().createAdNative(activity);
```
```
//第三步,获取广告
mQcCustomAdManager = QCiVoiceSdk.get().addCustomAudioAd(attr, new AudioCustomQcAdListener() {
        @Override
        public void onAdReceive (QcAdManager manager, View adView) {
          Log.e(TAG, "onADReceive");
         
          //第四步,展示广告
          //把返回的view添加到界面控件中
          mFlAd.setVisibility(View.VISIBLE);
          mFlAd.removeAllViews();
          mFlAd.addView(adView)
        
          //开始加载播放界面
          manager.startPlayAd();
        
          //跳过广告,广告停止播放
          manager.skipPlayAd();
        }
        
        @Override
        public void onAdExposure () {
           Log.e(TAG, "onADExposure");
        }
        
        @Override
        public void onAdUserInfo (String userId, String avater) {
           //弹幕头像点击的时候,返回上传的userId的信息
           Log.e(TAG, "onADUserInfo" + " |userId=" + userId + " |avater=" + avater);
        }
        
        @Override
        public void onAdClick () {
           Log.e(TAG, "onAdClick");
        }
        
        @Override
        public void onAdCompletion () {
           Log.e(TAG, "onAdCompletion");
        }
        
        @Override
        public void onAdError (String fail) {
           Log.e(TAG, fail);
        }
    });
}
```

## 2.4 AdAttr 传递参数说明：

| **方法/参数名称**                       | **描述**                                                     |
| -------------------------------------------- | ------------------------------------------------------------ |
| setAdid                                      | 传入平台申请的adid                                           |
| setMid                                       | 传入平台申请的mid                                            |
| setCanLeftTouch                              | 设置是否支持往左滑动跳出落地页等方法,默认false,设置true时,在list中会拦截原有的滑动事件 |
| setLabel                                     | 要让媒体方在sdk方法中提交用户在广告请求前所听过的最近信息,详情参见2.2.4,不传时需传入null       |
| setCanPause                                  | 是否支持暂停,默认支持                                            |
| **设置背景的方法**                              |                                                              |
| setBackgroundLayout                          | 设置背景图片的位置,默认是TOP_LEFT                            |
| setBackgroundMagin                           | 设置背景图片的magin值,单位dp（left,top,right,bottom）        |
| setBackgroundSize                            | 设置背景图片的宽度和高度,单位px  默认match_parent            |
| **设置Title的方法**                          |                                                              |
| setTitleColor                                | 设置标题的颜色，默认#FF3D3B3B                                |
| setTitleSize                                 | 设置标题的大小,单位sp                                        |
| setTitleLayout                               | 设置标题的位置,默认是TOP_LEFT                                |
| setTitleMagin                                | 设置标题的magin值,单位dp（left,top,right,bottom）            |
| setTitleTextMaxSize                          | 设置title单行最大字数                                          |
| setTitleTextMaxLines                         | 设置title最大行数                                            |
| **设置C****ontent****的方法**                |                                                              |
| setContentColor                              | 设置展示内容的颜色  默认6D6D6D                               |
| setContentSize                               | 设置展示内容的大小,单位sp                                    |
| setContentLayout                             | 设置展示内容的位置,默认是below,标题下方                      |
| setContentMagin                              | 设置内容的magin值,单位dp                                     |
| **设置左下角的Info窗口的方法**               |                                                              |
| setInfoTitleColor                            | 设置信息的标题颜色                                           |
| setInfoContentColor                          | 设置信息的内容颜色                                           |
| setInfoButtonColor                           | 设置信息内部按钮颜色                                         |
| setInfoButtonBackgroundColor                 | 设置信息内部按钮背景颜色                                     |
| setInfoLayout                                | 设置信息的位置,默认是BOTTOM_LEFT                             |
| setInfoMagin                                 | 设置内容的magin值,单位dp                                     |
| **设置右下角头像的方法**                     |                                                              |
| setAdHeadSize                                | 设置logo的大小,宽高一致,默认40，单位dp                       |
| setAdHeadType                                | 设置logo的显示的样式,默认圆形-CoverType.ROUND圆形,CoverType.OVAL圆角 |
| setShowHeadLinkImage                         | 设置是否在logo上方显示logo的图片,默认true显示                |
| setAdHeadLayout                              | 设置logo的位置,默认是BOTTOM_RIGHT                            |
| setAdHeadMagin                               | 设置Logo的magin值,单位dp                                     |
| **设置右下角点赞图片的方法**                 |                                                              |
| setPraiseChooseImage                         | 设置点赞图片,不设置显示默认图片，Drawable  类型              |
| setPraiseDefaultImage                        | 设置未点赞图片,不设置显示默认图片，Drawable  类型            |
| setPraiseImageSize                           | 设置点赞图片的大小,单位dp,默认40                             |
| setPraiseImageLayout                         | 设置点赞图片位置,默认是BOTTOM_RIGHT                          |
| setPraiseImageMagin                          | 设置点赞图片的magin值,单位dp                                 |
| **设置右下角点赞数量的方法**                 |                                                              |
| setPraiseNumberColor                         | 设置点赞数量的颜色#FF5555                                    |
| setPraiseNumberSize                          | 设置点赞数量的大小,单位sp 默认12                             |
| setPraiseNumberWidth                         | 设置点赞数量控件的宽度,单位dp 默认40                         |
| setPraiseNumberLayout                        | 设置点赞数量的位置,默认是BOTTOM_RIGHT                        |
| setPraiseNumberMagin                         | 设置点赞数量的的magin值,单位dp                               |
| **设置右下角弹幕****按钮****图片的方法**     |                                                              |
| setBarrageImage                              | 设置弹幕图片,不设置显示默认图片                              |
| setBarrageImageSize                          | 设置弹幕图片的大小,单位dp,默认40                             |
| setBarrageImageLayout                        | 设置右下角弹幕按钮图的位置,默认是BOTTOM_RIGHT                |
| setBarrageImageMagin                         | 设置弹幕图片的magin值,单位dp                                 |
| **设置右下角弹幕****按钮下方****数量的方法** |                                                              |
| setBarrageNumberColor                        | 设置弹幕数量的颜色#FFFF5555                                  |
| setBarrageNumberSize                         | 设置弹幕数量的大小,单位sp,默认12                             |
| setBarrageNumberWidth                        | 设置弹幕数量控件的宽度,单位dp 默认40                         |
| setBarrageNumberLayout                       | 设置弹幕数量的位置,默认是BOTTOM_RIGHT                        |
| setBarrageNumberMagin                        | 设置弹幕数量的magin值,单位dp                                 |
| **设置中间封面图片的方法**                   |                                                              |
| setCoverSize                                 | 设置封面图片的大小 ,单位dp 默认                              |
| setCoverType                                 | 设置封面图片显示的样式,默认圆形 - CoverType.ROUND圆形,CoverType.OVAL圆角 |
| setCoverLayout                               | 设置封面图片的位置,默认是CENTER_IN_PARENT 整体居中显示       |
| setCoverMagin                                | 设置封面图片的magin值,单位dp                                 |
| **设置封面上播放按钮的方法**                 |                                                              |
| setMusicBtPlayImage                          | 设置播放按钮的图片,不设置显示默认图片                        |
| setMusicBtPauseImage                         | 设置暂停按钮的图片,不设置显示默认图片                        |
| setMusicBtSize                               | 设置播放按钮的大小 ,单位dp 默认50                            |
| setMusicBtLayout（layout，isToCover）        | 设置播放按钮的位置,默认是CENTER_IN_PARENT(基于封面布局),也可设置isToCover=false(基于整个广告设置位置) |
| setMusicBtLayout（layout）                   | 设置播放按钮的位置,默认是CENTER_IN_PARENT(基于封面布局)      |
| setMusicBtMagin                              | 设置播放按钮的magin值,单位dp                                 |
| **设置滚动弹幕方法**                         |                                                              |
| setShowBarrage                               | 是否展示弹幕                                                 |
| setBarrageContentSize                        | 设置滚动弹幕内容的大小  单位sp 默认14                        |
| setBarrageContentColor(colorList)            | 设置滚动弹幕内容的颜色,颜色随机                              |
| setBarrageBackColor                          | 设置滚动弹幕背景颜色                                         |
| setBarrageHeadSize                           | 设置滚动弹幕头像的大小  单位dp 默认ContentSize+6             |
| setBarrageMagin                              | 设置滚动弹幕的magin值,单位dp                                 |
| setBarrageSpeed                              | 设置滚动弹幕的滚动速度，默认为3，建议3-10                    |
| **设置跳过**                               |                                                              |
| setSkipIsEnable                              | 是否启用跳过                                             |
| setSkipGravity                              |  设置跳过控件位置,具体参见RelativeLayout.LayoutParams.addRule()方法                |
| setSkipMargin                               |  设置跳过的margin值,单位dp                                   |
| setSkipAutoClose                            |  设置跳过倒计时结束后是否自动关闭该广告                   |
| **设置是否启用右下角**                           |                                                              |
| setEnableRightView                            |  设置是否启用右下角                   |

## 2.5 listener回调方法：

| **方法/参数名称** | **描述**                      | **备注**     |
| ----------------- | -------------------------- | --------    |
| onAdReceive       | 返回广告管理对象              |             |
| onAdExposure      | 广告的曝光                   |             |
| onAdUserInfo      | 弹幕点击头像返回的用户信息      |             |
| onAdClick         | 广告的点击                   |             |
| onAdClose         | 广告关闭                     |             |
| onAdCompletion    | 音频广告播放完成              |              |
| onAdError         | 错误回调                     |             |

## 2.6可选参数传递
1、可选参数，可以传递用户的userId，头像的imageUrl，用于用户发送弹幕后的点击展示，详情参考Demo
```
//传递用户信息
Map<String, String> map = new HashMap<>();
map.put("userId", userId);
map.put("avatar", avater);
QCiVoiceSdk.get().setUserInfo(map);
```

## 2.7 首听

### 2.7.1调用方法:
```
QCiVoiceSdk.get().addFirstVoiceAd(
       Activity,
       ADID, 
       labels,
       listener
)
```

| **方法/参数名称**                 | **描述**                   | **备注**                |
| ------------------------------- | --------------------------| ----------------------- |
| Activity                        | 当前activity               |   必填                  |
| ADID                            | 广告id                     |   必填                  |
| labels                          | 记录播放过的音频信息,详见2.2.4 |   非必填,不传时需传入null |
| listener                        | 广告回调                    |   必填                  |
### 2.7.2返回广告:
```
广告管理类及view控件
```
### 2.7.3 listener回调:

| **方法/参数名称**                 | **描述**                   | **备注**                |
| --------------------------------- | -------------------------- | ----------------------- |
| onAdClick                         | 广告的点击                 |                         |
| onAdCompletion                    | 广告播放结束               |                         |
| onAdError                         | 广告异常                   |                         |
| onFirstVoiceAdClose               | 倒计时结束后关闭广告的回调 |                         |
| onFirstVoiceAdCountDownCompletion | 倒计时结束后的回调         |                         |
| onAdExposure                      | 广告曝光                   |                         |
| onAdReceive                       | 返回广告管理对象           | 调用startPlayAd展示广告 |

### 2.7.4.用法:
```
在Actiity的布局中，预留广告的控件位置，把广告返回的view添加到预留的控件中
```

示例如下：

```
1.获取广告
QCiVoiceSdk.get()
   .addFirstVoiceAd(
     this,
     ADID, 
     labels,
     new QcFirstVoiceAdViewListener() {
    @Override
    public void onAdClick () {
        Log.e(TAG, "onAdClick:");
    }
    
    @Override
    public void onAdCompletion () {
        Log.e(TAG, "onAdCompletion:");
    }
    
    @Override
    public void onAdError (String fail) {
        Log.e(TAG, "onAdError:" + fail);
    }
    
    @Override
    public void onFirstVoiceAdClose () {
        //首听关闭
        Log.e(TAG, "onFirstVoiceAdViewClose");
        bottomLayout.setVisibility(View.GONE);
    }
    
    @Override
    public void onFirstVoiceAdCountDownCompletion () {
        //首听倒计时关闭
        Log.e(TAG, "onFirstVoiceAdViewCountDownCompletion");
    }
    
    @Override
    public void onAdExposure () {
        Log.e(TAG, "onAdExposure:");
    }
    
    @Override
    public void onAdReceive (QcAdManager manager, View adView) {
        Log.e(TAG, "onFirstVoiceAdView");
            2.获取广告返回类
        QCFirstVoiceActivity.this.qcAdManager = manager;
        QCFirstVoiceActivity.this.adView = adView;
    }
});
```

3.展示广告
```
qcAdManager.startPlayAd();
```
4.停止广告
```
qcAdManager.skipPlayAd();
```
5.释放广告
```
qcAdManager.destroy();
qcAdManager = null;
```
具体用法请参考demo 中 QCFirstVoiceActivity类

## 2.8冠名广告:
### 2.8.1调用方法:
```
QCiVoiceSdk.get().addVoiceAd(
    this, 
    ADID, 
    labels,
    listener
)
```

| **方法/参数名称**                 | **描述**                   | **备注**                |
| ------------------------------- | --------------------------| ----------------------- |
| Activity                        | 当前activity               |   必填                  |
| ADID                            | 广告id                     |   必填                  |
| labels                          | 记录播放过的音频信息,详见2.2.4 |   非必填,不传时需传入null |
| listener                        | 广告回调                    |   必填                  |
### 2.8.2返回广告:

```
广告管理类
```

### 2.8.3 listener回调:

| **方法/参数名称**   | **描述**                                | **备注**                              |
| ------------------- | --------------------------------------- | ---------------------------------- |
| onAdClick           | 广告的点击                              |                                      |
| onAdCompletion      | 广告及互动播放结束                      | 未配置互动,则广告播放结束直接触发该方法       |
| onAdError           | 广告异常                                |                                      |
| onAdExposure        | 广告曝光                                |                                      |
| onAdReceive         | 返回广告管理对象                        | 调用startPlayAd展示广告                     |
| onAdPlayEndListener | 广告播放结束,触发待播放互动音频等待时间 | 等待结束后,如果配置互动音频提示音则播放互动 |

### 2.8.4代码示例:

```
QCiVoiceSdk.get().addVoiceAd(
    this, 
    ADID,
    labels,
    new QcVoiceAdListener() {
    @Override
    public void onAdExposure () {
      Log.e(TAG, "onAdExposure");
    }

    @Override
    public void onAdReceive (QcAdManager manager) {
       Log.e(TAG, "onAdReceive");
       manager.startPlayAd();
    }

    @Override
    public void onAdClick () {//此处为触发摇一摇的回调
       Log.e(TAG, "onAdClick");
    }

    @Override
    public void onAdCompletion () {
       Log.e(TAG, "onAdCompletion");
    }

    @Override
    public void onAdError (String fail) {
       Log.e(TAG, "onAdError:" + fail);
    }
});
```

### 2.8.5方法参考:
```
addVoiceAd (
    Activity activity,
    String adId,
    List<UserPlayInfoBean> labels,
    QcVoiceAdListenercommonVoiceAdListener listener)
```
```
addVoiceAd (
    ​ Activity activity,
    ​ String adId,
    ​ int delayTimer,
     List<UserPlayInfoBean> labels,
    ​ QcVoiceAdListener commonVoiceAdListener)
```

| 参数                  | 说明                     | 备注                                               |
| --------------------- | ------------------------ | -------------------------------------------------- |
| Activity  activity    | 当前activity             |                                                    |
| String adId           | adid                     |                                                    |
| int  delayTimer       | 广告播放结束后的等待时间       | 该倒计时结束后播放互动,如果不启用互动,则该参数无效 |
| labels                | 记录播放过的音频信息,详见2.2.4 |   非必填,不传时需传入null |
| commonVoiceAdListener | 回调                     | 参考2.5.3                                          |

# 2.9 自定义模板
## 2.9.1 调用方法:
```
 QCiVoiceSdk.get().addCustomTemplateAd(
    activity,
    attr,
    ADID,
    labels,
    listener
)
```

| **方法/参数名称**                 | **描述**                   | **备注**                |
| ------------------------------- | --------------------------| ----------------------- |
| Activity                        | 当前activity               |   必填                  |
| attr                            | 自定义属性                  |   必填                  |
| ADID                            | 广告id                     |   必填                  |
| labels                          | 记录播放过的音频信息,详见2.2.4 |   非必填,不传时需传入null |
| listener                        | 广告回调                    |   必填                  |
## 2.9.2 返回广告:
```
广告管理类及view控件
```
### 2.9.3 listener回调:
| **方法/参数名称**   | **描述**                                | **备注**                                    |
| ------------------- | --------------------------------------- | ------------------------------------------- |
| onAdClick           | 广告的点击                               |                                             |
| onAdCompletion      | 广告及互动播放结束                        | 未配置互动,则广告播放结束直接触发该方法     |
| onAdError           | 广告异常                                |                                             |
| onAdExposure        | 广告曝光                                |                                             |
| onAdReceive         | 返回广告管理对象及view                    | 调用startPlayAd展示广告                     |
| fetchMainTitle      | 获取广告主标题                            | 可能为null,注意判空                            |
| onAdSkipClick       | 手动点击了广告的跳过                       |                                              |
| onFetchAdContentView| 获取内容控件                             |1.左上角 广告 标识,2.右上角跳过布局3.下方主标题,4.icon 右侧副标题,5.右下角了解详情 |

### 2.9.4 自定义属性:
```
        QcCustomTemplateAttr attr = new QcCustomTemplateAttr();
        //设置封面属性,单位dp,设置MATCH_PARENT 则交由外部容器控制
        QcCustomTemplateAttr.CoverStyle coverStyle = new QcCustomTemplateAttr.CoverStyle();
        coverStyle.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        coverStyle.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        coverStyle.setRadius(50);//设置封面圆角,单位dp
        attr.setCoverStyle(coverStyle);

        //设置广告icon,单位dp
        QcCustomTemplateAttr.IconStyle iconStyle = new QcCustomTemplateAttr.IconStyle();
        iconStyle.setWidth(30);
        iconStyle.setHeight(30);
        iconStyle.setRadius(10);//设置icon圆角,单位dp
        iconStyle.setLayoutGravity(Gravity.BOTTOM);//设置icon位置,具体参见Gravity方法
        iconStyle.setEnableMargin(true);
        iconStyle.setMarginLeft(15);
        iconStyle.setMarginBottom(13);

        attr.setIconStyle(iconStyle);

        attr.setEnableSkip(true);//是否启用右上角跳过,默认启用
        attr.setSkipAutoClose(false);//设置倒计时结束后是否自动关闭广告,默认false
        attr.setSkipTipValue("关闭");//设置右上角跳过文案,默认为跳过
```

| **方法/参数名称**                       | **描述**                                  |
| --------------------------------------| ---------------------------------------- |
| coverStyle                            | 封面样式                                   |
| iconStyle                             | 左下角icon 样式                            |
| isEnableSkip                          | 是否启用跳过,默认true                        |
| skipAutoClose                         | 倒计时结束后是否启用自动关闭广告,默认false       |
| skipTipValue                          | 启用跳过时按钮文案                           |

| **方法/参数名称**                       | **描述**                                  |
| --------------------------------------| ---------------------------------------- |
| coverStyle                            |                                          |
| width                                 | 封面宽度,单位dp                             |
| height                                | 封面高度,单位dp                             |
| radius                                | 圆角度数,单位dp                             |

| **方法/参数名称**                       | **描述**                                  |
| --------------------------------------| ---------------------------------------- |
| iconStyle                             |                                          |
| width                                 | 封面宽度,单位dp                             |
| height                                | 封面高度,单位dp                             |
| radius                                | 圆角度数,单位dp                             |
| isEnableMargin                        | 是否启用外部边距                            |
| marginLeft                            | 左侧外边距,单位dp                          |
| marginTop                             | 顶部外边距,单位dp                            |
| marginRight                           | 右侧外边距,单位dp                           |
| marginBottom                          | 底部外边距,单位dp                            |
| layoutGravity                         | 位于父布局内的位置,具体参见Gravity方法          |

### 2.9.5代码示例:
```
参考 simple 中 CustomTemplateActivity类
```
```
        //设置广告的adId和自定义的参数
        QcCustomTemplateAttr attr = fetchAdAttr();

        //获取广告
        QCiVoiceSdk.get().addCustomTemplateAd(
                this,
                attr,
                ADIDConstants.ReleaseEnum.INFO_ADID,
                labels,
                new QcCustomTemplateListener() {
                    @Override
                    public void onAdReceive (QcAdManager manager, View adView) {
                        Log.e(TAG, "onAdReceive");
                        status.setText(String.format("当前广告状态:%s", "onAdReceive"));

                        mQcCustomAdManager = manager;

                        //第四步,展示广告
                        //把返回的view添加到界面控件中
                        rootView.setVisibility(View.VISIBLE);
                        rootView.removeAllViews();
                        if (adView != null) {
                            rootView.addView(adView);
                        }
                    }

                    @Override
                    public void onAdExposure () {
                        Log.e(TAG, "onAdExposure");
                        status.setText(String.format("当前广告状态:%s", "onAdExposure"));
                    }

                    @Override
                    public void fetchMainTitle (String title) {
                        Log.e(TAG, "fetchMainTitle");
                        status.setText(String.format("当前广告状态:%s", "fetchMainTitle:" + title));
                    }

                    @Override
                    public void onAdSkipClick () {
                        Log.e(TAG, "onAdSkipClick");
                        status.setText(String.format("当前广告状态:%s", "onAdSkipClick"));
                    }

                    @Override
                    public void onFetchAdContentView (
                            TextView adTipView,//左上角 广告 标识
                            LinearLayout skipLayout,//右上角跳过布局
                            TextView mainTitleView,//下方主标题
                            TextView subtitleView,//icon 右侧副标题
                            TextView understandDescView//右下角了解详情
                    ) {

                    }

                    @Override
                    public void onAdClick () {
                        Log.e(TAG, "onAdClick");
                        status.setText(String.format("当前广告状态:%s", "onAdClick"));
                    }

                    @Override
                    public void onAdCompletion () {
                        Log.e(TAG, "onAdCompletion");
                        status.setText(String.format("当前广告状态:%s", "onAdCompletion"));
                    }

                    @Override
                    public void onAdError (String fail) {
                        Log.e(TAG, fail);
                        status.setText(String.format("当前广告状态:%s", "onAdError:" + fail));
                    }
                });
```
# 2.10 自定义车载广告轮播形式
## 2.10.1 调用方法
```
 QCiVoiceSdk.get().addAutoRotationAd(
                activity,
                adId,
                labels,
                QcAutoRotationListener)
```
| **参数**                       | **说明**                    | **备注**                        |
| ---------------------         | ------------------------   | --------------------------------|
| Activity  activity            | 当前activity                |     必填                         |
| String adId                   | adid                       |必填,广告id                       |
| List<UserPlayInfoBean> labels | 记录播放过的音频信息,详见2.2.4  | 非必填,不传时需传入null            |
| QcAutoRotationListener        | 广告回调                     | 必填,参考2.10.3                   |

## 2.10.2 返回广告
```
广告管理类及view控件
```

## 2.10.3 listener 回调
| **方法/参数名称**   | **描述**                                | **备注**                                     |
| ------------------- | --------------------------------------- | ---------------------------------------- |
| onAdClick           | 广告的点击                               |                                           |
| onAdCompletion      | 广告及互动播放结束                        | 未配置互动,则广告播放结束直接触发该方法           |
| onAdError           | 广告异常                                |                                            |
| onAdExposure        | 广告曝光                                |                                            |
| onAdReceive         | 返回广告管理对象及view                    | 调用startPlayAd展示广告                      |

## 2.10.4 代码示例
```
参考simle 中 CustomAutoRotationAdStyleActivity 类
```

```
        //设置广告的adId和自定义的参数
        List<UserPlayInfoBean> userPlayInfoBeans = CommonLabelUtils.getCommonLabels();

        String adId =  QCiVoiceSdk.get().isDebug() ?
                ADIDConstants.TestEnum.AUTO_ROTATION_ADID :
                ADIDConstants.ReleaseEnum.INFO_ADID;
        QCiVoiceSdk.get().addAutoRotationAd(
                this,
                adId,
                userPlayInfoBeans,
                new QcAutoRotationListener() {
            @Override
            public void onAdReceive (QcAdManager manager, View adView) {
                mQcCustomAdManager = manager;
                rootView.removeAllViews();
                rootView.addView(adView);
            }

            @Override
            public void onAdClick () {
                status.setText(String.format("当前广告状态:%s", "onAdClick"));
            }

            @Override
            public void onAdCompletion () {
                status.setText(String.format("当前广告状态:%s", "onAdCompletion"));
            }

            @Override
            public void onAdError (String fail) {
                status.setText(String.format("当前广告状态:%s", "onAdError:" + fail));
            }

            @Override
            public void onAdExposure () {
                status.setText(String.format("当前广告状态:%s", "onAdExposure"));
            }
        });
```

# 2.11 权限设置：若关闭该权限，将极大影响填充率和收益
## 2.11.1 定位权限：若关闭定位权限将不再上报位置信息
            QCiVoiceSdk.get().setLocationPermission(boolean);
## 2.11.2 麦克风权限：若关闭麦克风权限将没有广告结束语音互动
            QCiVoiceSdk.get().setMicrophonePermission(boolean);
## 2.11.3 设备号权限：若关闭设备号权限将不再上报设备信息（OAID、IMEI、MAC、AndroidID等）
            QCiVoiceSdk.get().setDevicePermission(boolean);



# 三、注 意
```
因为广告的获取需要用到手机设备信息，定位权限等权限，App版本在Android 6.0以上的需要动态申请权限。
SDK中只包含了最基本的权限申请，Demo代码里是一个基本的权限申请示例，
请开发者根据自己的场景合理地编写代码来实现权限申请。

App版本在Android6.0以下的忽略本条提示。
```

# 四、混淆

```
# ------------------------------------------通用区域----------------------------------------------------

#----------------------基本指令------------------------

#代码混淆压缩比，在0和7之间，默认为5，一般不需要改
-optimizationpasses 5
#混淆时不使用大小写混合，混淆后的类名为小写
-dontusemixedcaseclassnames
#指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclasses
#指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers
#不做预校验，preverify是proguard的4个步骤之一
#Android不需要preverify，去掉这一步可加快混淆速度
-dontpreverify
#有了verbose这句话，混淆后就会生成映射文件
#包含有类名->混淆后类名的映射关系
#然后使用printmapping指定映射文件的名称
-verbose
-printmapping proguardMapping.txt
#指定混淆时采用的算法，后面的参数是一个过滤器
#这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/cast,!field/*,!class/merging/*
#保护代码中的Annotation不被混淆，这在JSON实体映射时非常重要，比如fastJson
-keepattributes *Annotation*,InnerClasses
#避免混淆泛型，这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature
#抛出异常时保留代码行号，在异常分析中可以方便定位
-keepattributes SourceFile,LineNumberTable
#-----------------------全局混淆-----------------------
#除了项目目录，其他都不混淆，这种写法只能有一行，请自行改成自己的包路径，如果再加一行com.li.*会导致全部都不混淆②
#-keep class !com.corpize.sdk.mobads.** {*;}
#-keep class !com.li.** {*;}写2行会导致全部不混淆
-dontwarn **
#---------------------默认保留-------------------------

#基础保留
-keep class * extends android.app.Activity
-keep class * extends android.app.Application
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.content.ContentProvider
-keep class * extends android.app.backup.BackupAgentHelper
-keep class * extends android.preference.Preference
-keep class * extends android.view.View {
    <init>(...);
}

#序列化
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#EventBus的注解
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}

#WebView
-keepclassmembers class * extends android.webkit.WebView {*;}
-keepclassmembers class * extends android.webkit.WebViewClient {*;}
-keepclassmembers class * extends android.webkit.WebChromeClient {*;}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-dontwarn com.tencent.smtt.sdk.WebView
-dontwarn com.tencent.smtt.sdk.WebChromeClient
-dontwarn com.qichuang.annotation.Nullable
-dontwarn com.qichuang.annotation.NonNull
-dontwarn com.google.android.gms.ads.identifier.AdvertisingIdClient
-dontwarn com.google.android.gms.ads.identifier.AdvertisingIdClient$Info
-dontwarn androidx.appcompat.app.AlertDialog
-dontwarn androidx.appcompat.view.menu.ListMenuItemView
-dontwarn androidx.recyclerview.widget.RecyclerView
-dontwarn androidx.swiperefreshlayout.widget.SwipeRefreshLayout
-dontwarn androidx.viewpager.widget.ViewPager
-dontwarn androidx.recyclerview.widget.RecyclerView
-dontwarn com.qichuang.annotation.RequiresApi
-dontwarn androidx.fragment.app.FragmentActivity
-dontwarn androidx.fragment.app.Fragment
-dontwarn com.qichuang.annotation.AnyThread
-dontwarn com.qichuang.annotation.WorkerThread

# SecSDK Interface
-keep public class com.pgl.sys.ces.out.* {
    public *;
}

# SecSDK Main Function
-keep public class com.pgl.sys.ces.a {*;}

# ------------------------------------------------------------ 动态混淆 ------------------------------------------------------------
# oaid中的设置的混淆
-keep public class com.netease.nis.sdkwrapper.Utils {public <methods>;}
-keep class a.**{*;}

# sdk
-keep class com.bun.miitmdid.** { *; }
# asus
-keep class com.asus.msa.SupplementaryDID.** { *; }
-keep class com.asus.msa.sdid.** { *; }
# freeme
-keep class com.android.creator.** { *; }
-keep class com.android.msasdk.** { *; }
# huawei
-keep class com.huawei.hms.ads.identifier.** { *; }
#-keep class com.uodis.opendevice.aidl.** { *; }
# lenovo
-keep class com.zui.deviceidservice.** { *; }
-keep class com.zui.opendeviceidlibrary.** { *; }
# meizu
-keep class com.meizu.flyme.openidsdk.** { *; }
# nubia
-keep class com.bun.miitmdid.provider.nubia.NubiaIdentityImpl
# oppo
-keep class com.heytap.openid.** { *; }
# samsung
-keep class com.samsung.android.deviceidservice.** { *; }
# vivo
-keep class com.vivo.identifier.** { *; }
# xiaomi
-keep class com.bun.miitmdid.provider.xiaomi.IdentifierManager
# zte
-keep class com.bun.lib.** { *; }
# coolpad
-keep class com.coolpad.deviceidsupport.** { *; }

# ------------------------------------------------------------ 自定义不混淆的类 ------------------------------------------------------------
# bean网络获取数据 http为网络层 防混淆 TODO：包的路径要自己修改
-keep class com.corpize.sdk.ivoice.bean.** { *; }
-keep class com.corpize.sdk.ivoice.http.callback.** { *; }
-keep class com.corpize.sdk.ivoice.http.util.** { *; }
-keep class com.corpize.sdk.ivoice.http.QcErrorCode {*;}
-keep class com.corpize.sdk.ivoice.http.** { *; }
-keep class com.corpize.sdk.ivoice.listener.** {*;}
-keep class com.corpize.sdk.ivoice.view.** {*;}
-keep class com.corpize.sdk.ivoice.utils.downloadinstaller.** {*;}

# 对外方法 TODO：包的路径要自己修改
-keep class com.corpize.sdk.ivoice.QCiVoiceSdk {*;}
-keep class com.corpize.sdk.ivoice.AdAttr {*;}
-keep class com.corpize.sdk.ivoice.QcCustomTemplateAttr* {*;}
-keep class com.corpize.sdk.ivoice.AdLayout {*;}
-keep class com.corpize.sdk.ivoice.CoverType {*;}
-keep class com.corpize.sdk.ivoice.admanager.QcAdManager {*;}
-keep class com.corpize.sdk.ivoice.admanager.CustomAudioAdView {*;}
-keep class com.qichuang.**{*;}
```

 

 
