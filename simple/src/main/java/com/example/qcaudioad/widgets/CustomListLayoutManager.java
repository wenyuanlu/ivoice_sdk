package com.example.qcaudioad.widgets;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qcaudioad.helper.MyPagerSnapHelper;

/**
 * author ：yh
 * date : 2020-12-18 18:18
 * description :
 */
public class CustomListLayoutManager extends LinearLayoutManager implements RecyclerView.OnChildAttachStateChangeListener {

    //判断是否上滑还是下滑
    private int mDrift;

    private OnViewPagerListener onViewPagerListener;
    //吸顶，吸底
    private MyPagerSnapHelper   pagerSnapHelper;

    public OnViewPagerListener getOnViewPagerListener () {
        return onViewPagerListener;
    }

    public void setOnViewPagerListener (OnViewPagerListener onViewPagerListener) {
        this.onViewPagerListener = onViewPagerListener;
    }

    public CustomListLayoutManager (Context context) {
        super(context);
    }

    public CustomListLayoutManager (Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        pagerSnapHelper = new MyPagerSnapHelper();
    }

    /**
     * 当manager完全添加到recycleview中是会被调用
     *
     * @param view
     */
    @Override
    public void onAttachedToWindow (RecyclerView view) {
        view.addOnChildAttachStateChangeListener(this);
        pagerSnapHelper.attachToRecyclerView(view);
        super.onAttachedToWindow(view);
    }


    @Override
    public boolean canScrollVertically () {
        return super.canScrollVertically();
    }

    /**
     * 监听水平方向的相对偏移量
     *
     * @param dx
     * @param recycler
     * @param state
     * @return
     */
    @Override
    public int scrollHorizontallyBy (int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //this.mDrift = dx;
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    /**
     * 监听竖直方向的相对偏移量
     *
     * @param dy
     * @param recycler
     * @param state
     * @return
     */
    @Override
    public int scrollVerticallyBy (int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        mDrift = dy;
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    /**
     * 子视图添加到窗口
     *
     * @param view
     */
    @Override
    public void onChildViewAttachedToWindow (@NonNull View view) {
        if (mDrift > 0) {
            //向上滑
            if (onViewPagerListener != null && Math.abs(mDrift) == view.getHeight()) {
                onViewPagerListener.onPageSelected(false, view);
            }
        } else {
            //向下滑
            if (onViewPagerListener != null && Math.abs(mDrift) == view.getHeight()) {
                onViewPagerListener.onPageSelected(true, view);
            }
        }
    }

    /**
     * 子视图从窗口分离
     *
     * @param view
     */
    @Override
    public void onChildViewDetachedFromWindow (@NonNull View view) {
        if (mDrift >= 0) {
            //向上滑
            if (onViewPagerListener != null) {
                onViewPagerListener.onPageRelease(true, view);
            }
        } else {
            //向下滑
            if (onViewPagerListener != null) {
                onViewPagerListener.onPageRelease(false, view);
            }
        }
    }

    /**
     * 滑动状态变更
     *
     * @param state
     */
    @Override
    public void onScrollStateChanged (int state) {
        switch (state) {
            case RecyclerView.SCROLL_STATE_IDLE://停止滚动
                //当前显示的item
                View snapView = pagerSnapHelper.findSnapView(this);
                if (onViewPagerListener != null) {
                    onViewPagerListener.onPageSelected(false, snapView);
                }
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING://拖动
                break;
            case RecyclerView.SCROLL_STATE_SETTLING://惯性滑动
                break;
        }
        super.onScrollStateChanged(state);

    }
}