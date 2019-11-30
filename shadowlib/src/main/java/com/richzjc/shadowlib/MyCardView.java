package com.richzjc.shadowlib;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class MyCardView extends CardView {

    private int leftPadding;
    private int topPadding;
    private int rightPadding;
    private int bottomPadding;

    public MyCardView(@NonNull Context context) {
        this(context, null);
    }

    public MyCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (params != null) {
                getChildAt(0).layout(leftPadding + getContentPaddingLeft() + params.leftMargin,
                        topPadding + getContentPaddingTop() + params.topMargin,
                        leftPadding + getContentPaddingLeft() + params.leftMargin + child.getMeasuredWidth(),
                        topPadding + getContentPaddingTop() + params.topMargin + child.getMeasuredHeight());
            } else {
                getChildAt(0).layout(leftPadding + getContentPaddingLeft(),
                        topPadding + getContentPaddingTop(),
                        leftPadding + getContentPaddingLeft() + child.getMeasuredWidth(),
                        topPadding + getContentPaddingTop() + child.getMeasuredHeight());
            }
        }
    }

    public void setMyPadding(int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        this.leftPadding = leftPadding;
        this.topPadding = topPadding;
        this.rightPadding = rightPadding;
        this.bottomPadding = bottomPadding;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("当前子控件只能有一个子控件");
        } else {
            super.addView(child, index, params);
        }
    }
}
