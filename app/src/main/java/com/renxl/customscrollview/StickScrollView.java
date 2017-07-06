package com.renxl.customscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by renxl
 * On 2017/7/6 12:26.
 */

public class StickScrollView extends ViewGroup {

    private int width = getResources().getDisplayMetrics().widthPixels;
    private int height = getResources().getDisplayMetrics().heightPixels - 50;

    public StickScrollView(Context context) {
        super(context);
    }

    public StickScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 将所有子 View 都放入合适的位置

        int nextTop = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).layout(0, nextTop, width, nextTop + height);
            nextTop += height;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 测量所有子 View
        // 将所有子 View 的高度叠加
        // 为自己的测量高度赋值

        int childCount = getChildCount();
        int totalHeight = height * childCount;

        for (int i = 0; i < childCount; i++) {
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }

        setMeasuredDimension(width, totalHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    private float oldY, indexY;
    private Scroller scroller = new Scroller(getContext());

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        float scrollY = y - oldY;
        float scrollIndex = y - indexY;

        // 事件来临时拿到触摸点
        // 新事件来临时根据触摸点偏移量进行滑动
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                scrollBy(0, (int) -(scrollY));
                oldY = y;
                break;
            case MotionEvent.ACTION_DOWN:
                oldY = indexY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                int scrollTo = 0;
                if (getScrollY() < 0)
                    scrollTo = -getScrollY();
                else if (getScrollY() > (getChildCount() - 1) * height)
                    scrollTo = -(getScrollY() - (getChildCount() - 1) * height);
                else if (scrollIndex > height / 2) {
                    scrollTo = (int) -(height - scrollIndex);
                } else if (scrollIndex < height / 2 && scrollIndex > 0) {
                    scrollTo = (int) scrollIndex;
                } else if (scrollIndex < (-height / 2)) {
                    scrollTo = (int) (height + scrollIndex);
                } else if (scrollIndex > (-height / 2) && scrollIndex < 0)
                    scrollTo = -(int) (height + scrollIndex);

                scroller.startScroll(0, getScrollY(), 0, scrollTo);
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (null != scroller && scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }
}
