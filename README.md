IVoice音频广告sdk文档

一、项目介绍
    项目主要功能为adlibrary这个model文件，simple为sdk的示例工程

二、广告介绍
    1、广告分为音频互动弹窗广告，以及沉浸式自渲染音频互动广告（包含了50+可自定义参数）
    2、音频互动弹窗广告
        2.1 区分图文音频弹窗广告及激励视频广告
        2.2 图文广告，注意息屏亮屏的处理逻辑
        2.3 激励视频，包含了横竖屏的切换，后台前台切换等
        2.4 有音频互动模块，摇一摇等互动
    3、沉浸式自渲染音频互动广告
        3.1 50+可自定义参数的设置，用于界面的自定义
        3.2 有音频互动模块，摇一摇等互动
        3.3 包含了弹幕评论功能，点赞功能，用户头像展示等
        3.4 音频广告播放结束，结束页的展示，优惠券弹窗展示等
    
    详细的集成见 ———— 企创IVoice广告SDK 接入文档Android.docx
    
    注意：
        1、广告中包含常见的点击事件监听，展示监听，音视频播放进度监听，deepLink监听等
        2、点击事件区分下载、落地页，deeplink等等多种展示形式

三、工程介绍
    目录相关：
        1、admanager目录：不同广告的管理的类，落地页等
        2、bean目录：网络返回数据
        3、common目录：常用的工具，baseUrl的地址，打包区分生产和测试的设置
        4、danmuku目录：弹幕自定义view
        5、http目录：网络请求
        6、listener目录：不同广告回调
        7、utils目录：通用工具类
        8、video目录：视频播放的自定view及相关
        9、view目录：自定义的view
        
    单独的文件相关：
        1、AdAttr：对外暴露的参数
        2、AdLayout：对外暴露位置的参数
        3、CorpizeJ：网络加密的so库
        4、CoverType：对外暴露的图文的椭圆机圆形设置参数
        5、QCiVoiceSdk：对外暴露的方法，第三方集成时可见的类
    

四：三方集成
    1、glide-qc-3.7.0.jar 经过二次编辑的企创图片加载框架
    2、oaid_sdk_1.0.22.aar 移动联盟广告获取oaid,针对于AndroidQ的无法获取IMEI
    3、gson 动态编译

五：重点关注的工程文件
|-- com.corpize.sdk.ivoice
    |-- admanager
    |   |-- AudioAdManager.java                 音频互动弹窗广告管理类
    |   |-- AudioCustomAdManager.java           沉浸式自渲染音频互动广告管理类
    |   |-- CustomAudioAdView.java              沉浸式自渲染广告的view
    |   |-- QcAdDetialActivity.java             落地页，WebView展示url
    |   |-- QcAdManager.java                    对外生命周期调用的接口
    |   |-- QcAdVideoActivity.java              激励视频展示的落地页
    |   |-- RewardVideoManager.java             激励视频广告管理类
    |-- common
    |   |-- CommonUtils.java                    通用的方法
    |   |-- Constants.java                      baseurl的地址，打包前的参数变更，区分生产和测试
    |   |-- ErrorUtil.java                      常用的错误
    
六：如何打包arr
    点击右侧Gradle —— adlibrary —— Tasks —— other —— makeAar生成aar文件
    aar地址为目录下adlibrary —— src —— main —— outter ——aar 
    如需要修改生成路径，请在build.gradle中修改

七：混淆相关
    混淆文件proguard-rules.pro中有对应的混淆规则，后续添加的对外不需要混淆的文件，请在此处添加

