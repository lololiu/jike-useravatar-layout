package xyz.royliu.useravatar;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roy on 2017/1/4.
 * desc:
 */

public class AvatarLayout extends RelativeLayout {
    private static final String TAG = "AvatarLayout";
    AnimateImageView circleImageView;
    Context mContext;
    PullToRefeshLayout mPullLayout;
    ViewDragHelper mDragHelper;

    List<AnimateImageView> mIvList;

    ViewTrackController mViewTrackController;

    public AvatarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AvatarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mDragHelper = ViewDragHelper.create(this, 1.0f, new MyViewDragHelper());
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);
        mViewTrackController = ViewTrackController.create();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        RelativeLayout.LayoutParams params = new LayoutParams(ScreenUtil.dip2px(mContext, 80), ScreenUtil.dip2px(mContext, 80));
        params.topMargin = ScreenUtil.dip2px(mContext, 180 - 80 / 2);
        params.leftMargin = ScreenUtil.getScreenWidth(mContext) / 2 - ScreenUtil.dip2px(mContext, 80) / 2;
        mIvList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            AnimateImageView circleImageView = new AnimateImageView(mContext);
            circleImageView.setImageResource(R.drawable.avatar);
            circleImageView.setBorderWidth(ScreenUtil.dip2px(mContext, 1));
            circleImageView.setBorderColor(Color.WHITE);
            circleImageView.setLayoutParams(params);
            addView(circleImageView);
            if (i == 4) {
                this.circleImageView = circleImageView;
            } else {
                circleImageView.setAlpha(0.3f);
            }
            mIvList.add(circleImageView);
        }

        mViewTrackController.init(mIvList);


        mPullLayout = (PullToRefeshLayout) getChildAt(0);
        mPullLayout.setmListener(new PullToRefeshLayout.OnPullRefreshListener() {
            @Override
            public void onMoveY(float distance) {
                if (circleImageView != null) {
                    circleImageView.setTranslationY(distance);
                    mViewTrackController.setOtherVisiable(false);
                }
            }

            @Override
            public void onMoveEnd() {
                if (circleImageView != null) {
                    circleImageView.animate().translationY(0);
                }
            }
        });

    }

    private class MyViewDragHelper extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            Log.d(TAG, "tryCaptureView: " + (child == circleImageView));
            if (child == circleImageView) {
                circleImageView.stopAnimation();
                return true;
            }
            return false;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return 1;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            Log.d(TAG, "clampViewPositionHorizontal: " + left);
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            Log.d(TAG, "clampViewPositionVertical: " + top);
            return top;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
//            if (releasedChild == circleImageView) {
//                Log.d(TAG, "onViewReleased: " + mAvatarX + " " + mAvatarY);
//                mDragHelper.settleCapturedViewAt(mAvatarX, mAvatarY);
//                invalidate();
//            }
            mViewTrackController.onRelease();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            Log.d(TAG, "onViewPositionChanged: dx:" + dx + " dy" + dy);
            mViewTrackController.setOtherVisiable(true);
            mViewTrackController.onTopViewPosChanged(left, top);
        }
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && clickInAvatarView(event)) {
            return true;
        }
        Log.d(TAG, "onInterceptTouchEvent: " + mDragHelper.shouldInterceptTouchEvent(event));
        return mDragHelper.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: ");
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mViewTrackController.setOriginPos(circleImageView.getLeft(), circleImageView.getTop());
    }

    /**
     * 判断点击位置是否位于头像圆形的里面
     */
    private boolean clickInAvatarView(MotionEvent event) {
        boolean isInCircle = true;
        int clickX = (int) event.getRawX();
        int clickY = (int) event.getRawY();

        //获取控件在屏幕的位置
        int[] location = new int[2];
        circleImageView.getLocationOnScreen(location);

        //控件相对于屏幕的x与y坐标
        int x = location[0];
        int y = location[1];

        //圆半径 通过左右坐标计算获得getLeft
        int r = (circleImageView.getRight() - circleImageView.getLeft()) / 2;

        //圆心坐标
        int vCenterX = x + r;
        int vCenterY = y + r;

        //点击位置x坐标与圆心的x坐标的距离
        int distanceX = Math.abs(vCenterX - clickX);
        //点击位置y坐标与圆心的y坐标的距离
        int distanceY = Math.abs(vCenterY - clickY);
        //点击位置与圆心的直线距离
        int distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
        if (distanceZ > r) {
            return false;
        }
        return isInCircle;
    }
}
