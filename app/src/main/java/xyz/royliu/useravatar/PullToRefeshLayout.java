package xyz.royliu.useravatar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Roy on 2016/12/28.
 * desc:
 */

public class PullToRefeshLayout extends LinearLayout {
    private static final String TAG = "PullToRefeshLayout";
    private int mHeaderOffsetTop = 0;
    private int mHeaderOffsetBottom = 0;

    private float mStratY;


    private OnPullRefreshListener mListener;

    public PullToRefeshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHeaderOffsetTop = ScreenUtil.dip2px(context, 100);
        mHeaderOffsetBottom = ScreenUtil.dip2px(context, 100);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int count = getChildCount();
        if (count != 2) {
            throw new RuntimeException("should have 2 child views!");
        }
        int headHeight = 0;
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);

            if (i == 0) {
                headHeight = childView.getMeasuredHeight();
                childView.layout(0, -mHeaderOffsetTop, childView.getMeasuredWidth(), headHeight - mHeaderOffsetTop);
            } else if (i == 1) {
                childView.layout(0, headHeight - mHeaderOffsetTop - mHeaderOffsetBottom, childView.getMeasuredWidth(), headHeight - mHeaderOffsetTop - mHeaderOffsetBottom + childView.getMeasuredHeight());
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStratY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                float mCurrentY = event.getY();
                if (mCurrentY - mStratY < 0) {
                    return true;
                }
                if ((mCurrentY - mStratY) / 4 > mHeaderOffsetTop) {
//                    getChildAt(0).animate().translationY(0);
//                    getChildAt(1).animate().translationY(0);
                } else {
                    getChildAt(0).setTranslationY((mCurrentY - mStratY) / 4);
                    getChildAt(1).setTranslationY((mCurrentY - mStratY) / 2);
                    if (mListener != null) {
                        mListener.onMoveY((mCurrentY - mStratY) / 2);
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
                getChildAt(0).animate().translationY(0);
                getChildAt(1).animate().translationY(0);
                if (mListener != null) {
                    mListener.onMoveEnd();
                }
                return true;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setmListener(OnPullRefreshListener mListener) {
        this.mListener = mListener;
    }

    interface OnPullRefreshListener {
        void onMoveY(float distance);

        void onMoveEnd();
    }
}
