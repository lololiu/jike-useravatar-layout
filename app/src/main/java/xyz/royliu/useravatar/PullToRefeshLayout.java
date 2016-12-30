package xyz.royliu.useravatar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by liulou on 2016/12/28.
 * desc:
 */

public class PullToRefeshLayout extends LinearLayout {
    private static final String TAG = "PullToRefeshLayout";
    private int mHeaderOffsetTop = 0;
    private int mHeaderOffsetBottom = 0;

    private float mStratY;

    public PullToRefeshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHeaderOffsetTop = ScreenUtil.dip2px(context, 50);
        mHeaderOffsetBottom = ScreenUtil.dip2px(context, 50);
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
                Log.d(TAG, "onTouchEvent: start:" + mStratY);
                return true;
            case MotionEvent.ACTION_MOVE:
                float mCurrentY = event.getY();
                Log.d(TAG, "onTouchEvent: mHeaderOffsetTop-->"+mHeaderOffsetTop);
                if ((mCurrentY - mStratY) / 2 > mHeaderOffsetTop) {
                    getChildAt(0).animate().translationY(0);
                    getChildAt(1).animate().translationY(0);
                } else {
                    getChildAt(0).setTranslationY((mCurrentY - mStratY) / 2);
                    getChildAt(1).setTranslationY((mCurrentY - mStratY));
                }
                Log.d(TAG, "onTouchEvent: " + (mCurrentY - mStratY));
                return true;
            case MotionEvent.ACTION_UP:
                getChildAt(0).animate().translationY(0);
                getChildAt(1).animate().translationY(0);
                return true;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
