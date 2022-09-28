package com.example.qcaudioad.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.corpize.sdk.ivoice.AdAttr;
import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.admanager.QcAdManager;
import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;
import com.corpize.sdk.ivoice.listener.AudioCustomQcAdListener;
import com.example.qcaudioad.R;
import com.example.qcaudioad.base.BaseActivity;
import com.example.qcaudioad.bean.AdBean;
import com.example.qcaudioad.common.ADIDConstants;
import com.example.qcaudioad.utils.CommonLabelUtils;
import com.example.qcaudioad.widgets.CustomListLayoutManager;
import com.example.qcaudioad.widgets.OnViewPagerListener;

import java.util.ArrayList;
import java.util.List;


/**
 * author ：yh
 * date : 2020-12-18 18:21
 * description :
 */
public class CustomListActivity extends BaseActivity {
    private String                  TAG              = CustomListActivity.class.toString();
    private RecyclerView            recyclerView;
    private CustomListLayoutManager customLayoutManager;
    private MyListAdapter           mAdapter;
    private List<AdBean>            dataList         = new ArrayList<>();
    private int                     mCurrentPosition = 0;
    private int                     mLastPosition    = -1;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list);
        setTitle("自定义嵌入式(列表)");
        initData();
        initView();
        initAd(1);
        initAd(3);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (QCiVoiceSdk.get() != null) {
            QCiVoiceSdk.get().onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume () {
        super.onResume();
        //必须调用
        if (QCiVoiceSdk.get() != null) {
            QCiVoiceSdk.get().onResume();
        }
    }

    @Override
    protected void onPause () {
        super.onPause();
        //必须调用
        if (QCiVoiceSdk.get() != null) {
            QCiVoiceSdk.get().onPause();
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        //释放内存
        if (QCiVoiceSdk.get() != null) {
            QCiVoiceSdk.get().onDestroy();
        }
    }

    /**
     * 获取广告数据,并插入数据
     */
    private void initAd (final int position) {

        List<UserPlayInfoBean> userPlayInfoBeanDataList = CommonLabelUtils.getCommonLabels();

        String adid = QCiVoiceSdk.get().isDebug() ? ADIDConstants.TestEnum.INFO_ADID : ADIDConstants.ReleaseEnum.INFO_ADID;
        //未设置自定义参数,具体设置请参考MainActivity中的调用
        AdAttr attr = AdAttr.newBuild()
                .setAdid(adid)//设置广告id
                .setMid(ADIDConstants.MID)//设置广告的mid
                .setCoverSize(200)//设置封面图片的大小 ,单位dp 默认
                .setShowBarrage(true)//是否展示弹幕
                .setTitleColor(Color.parseColor("#FFFFFFFF"))//设置标题的颜色
                .setContentColor(Color.parseColor("#FFFFFFFF"))//设置展示内容的颜色
                .setLabel(userPlayInfoBeanDataList)
                //********设置跳过********//
                .setSkipIsEnable(true)//启用跳过
                .setSkipGravity(RelativeLayout.ALIGN_PARENT_RIGHT)//设置跳过控件位置,具体参见RelativeLayout.LayoutParams.addRule()方法
                .setSkipMargin(0, 15, 15, 0)
                .setSkipAutoClose(false)//设置跳过倒计时结束后是否自动关闭该广告
                ;

        //创建广告,必须调用
        QCiVoiceSdk.get().createAdNative(this);
        QcAdManager qcCustomAdManager = QCiVoiceSdk.get().addCustomAudioAd(position, attr, new AudioCustomQcAdListener() {

            @Override
            public void onAdReceive (QcAdManager manager, View adView) {
                Log.e(TAG, "onAdReceive");
                if (adView != null) {
                    AdBean adBean = new AdBean(adView, manager, true);
                    dataList.add(position, adBean);
                    mAdapter.setData(dataList);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onAdExposure () {
                Log.e(TAG, "onAdExposure");
            }

            @Override
            public void onAdUserInfo (String userId, String avater) {
                Log.e(TAG, "onAdUserInfo" + " |userId=" + userId + " |avater=" + avater);
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

    /**
     * 初始化数据
     */
    private void initData () {
        dataList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            dataList.add(new AdBean(null, false));
        }
    }

    /**
     * 初始化界面
     */
    private void initView () {
        customLayoutManager = new CustomListLayoutManager(this, OrientationHelper.VERTICAL, false);
        mAdapter = new MyListAdapter();
        mAdapter.setData(dataList);
        recyclerView = findViewById(R.id.recyclerView_dy);
        recyclerView.setLayoutManager(customLayoutManager);
        recyclerView.setAdapter(mAdapter);

        customLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onPageSelected (boolean isInit, View view) {
                int currentPosition = ((RecyclerView.LayoutParams) recyclerView.getChildAt(0).getLayoutParams()).getViewAdapterPosition();
                if (currentPosition == mAdapter.getItemCount() - 1 && mAdapter.getItemCount() != 0) {
                    //在列表的最后插入一条广告数据和两条普通数据。做到数据永不拉到最后
                    dataList.add(new AdBean(null, false));
                    dataList.add(new AdBean(null, false));
                    initAd(currentPosition + 1);
                    mAdapter.setData(dataList);
                    mAdapter.notifyDataSetChanged();
                }
                if (currentPosition != mCurrentPosition) {
                    mCurrentPosition = currentPosition;
                    AdBean adBean = dataList.get(mCurrentPosition);
                    if (adBean.isAd()) {
                        //再次返回页面的时候,自动播放广告音频
                        adBean.getQcAdManager().startPlayAd();
                    }
                }
                //Log.e(TAG, "当前页面 返回了onPageSelected" + "|isInit=" + isInit + "|当前position=" + currentPosition);
            }

            @Override
            public void onPageRelease (boolean isUp, View view) {
                //LogUtils.e("上一个页面 返回了onPageRelease" + "|isUp=" + isUp + "|");
                int currentPosition = ((RecyclerView.LayoutParams) recyclerView.getChildAt(0).getLayoutParams()).getViewAdapterPosition();
                //LogUtils.e("释放的页面currentPosition=" + currentPosition);

                if (currentPosition < 0) {
                    return;
                }

                int lastPosition = 0;
                if (isUp) {
                    lastPosition = currentPosition;
                } else {
                    lastPosition = currentPosition + 1;
                }
                if (lastPosition != mLastPosition) {
                    //LogUtils.e("释放的页面lastPosition=" + lastPosition);
                    mLastPosition = lastPosition;
                    AdBean adBean = dataList.get(mLastPosition);
                    if (adBean.isAd()) {
                        adBean.getQcAdManager().skipPlayAd();
                    }
                }
            }
        });
    }

    class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
        private List<AdBean> mDataList;

        public MyListAdapter () {
        }

        public void setData (List<AdBean> dataList) {
            this.mDataList = dataList;
        }

        @Override
        public int getItemCount () {
            return mDataList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_view_pager, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder (ViewHolder holder, int position) {
            if (position % 3 == 0) {
                holder.mAdFrameLayout.setBackgroundColor(Color.GREEN);
            } else if (position % 3 == 1) {
                holder.mAdFrameLayout.setBackgroundColor(Color.YELLOW);
            } else if (position % 3 == 2) {
                holder.mAdFrameLayout.setBackgroundColor(Color.RED);
            }
            AdBean adBean = mDataList.get(position);
            if (adBean.isAd()) {
                holder.mAdFrameLayout.removeAllViews();
                if (adBean.getAdView() != null) {
                    holder.mAdFrameLayout.addView(adBean.getAdView());
                }
                holder.mAdFrameLayout.setBackgroundColor(Color.TRANSPARENT);
                holder.mTvCommon.setVisibility(View.GONE);
            } else {
                holder.mAdFrameLayout.removeAllViews();
                holder.mTvCommon.setVisibility(View.VISIBLE);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout rootView;
            FrameLayout    mAdFrameLayout;
            TextView       mTvCommon;

            public ViewHolder (View itemView) {
                super(itemView);
                mAdFrameLayout = itemView.findViewById(R.id.fl_ad);
                rootView = itemView.findViewById(R.id.root_view);
                mTvCommon = itemView.findViewById(R.id.tv_common);
            }
        }
    }
}
