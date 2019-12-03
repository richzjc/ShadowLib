package com.richzjc.shadowlib;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

class MyCardView extends CardView {

    public int leftPadding;
    public int topPadding;
    public int rightPadding;
    public int bottomPadding;

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

    public void measureChildren(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    public void setCardViewChildMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            ViewGroup.LayoutParams lp = child.getLayoutParams();
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            int realHeight = 0;
            int realWidth = 0;

            if (lp.width > 0) {
                realWidth = Math.min(lp.width, widthSpecSize -  getContentPaddingLeft() - leftPadding - rightPadding - getContentPaddingRight());
            }else if(lp.width == ViewGroup.LayoutParams.MATCH_PARENT){
                realWidth = widthSpecSize -  getContentPaddingLeft() - leftPadding - rightPadding - getContentPaddingRight();
            }else{
                measureChildren(child,
                        widthMeasureSpec,
                        leftPadding + rightPadding + getContentPaddingLeft() + getContentPaddingRight(),
                        heightMeasureSpec,
                        topPadding + bottomPadding + getContentPaddingBottom() + getContentPaddingTop());
                realWidth = child.getMeasuredWidth();
            }

            if (lp.height > 0) {
                realHeight = Math.min(lp.height, heightSpecSize -  getContentPaddingTop() - topPadding - bottomPadding - getContentPaddingBottom());
            }else if(lp.height == ViewGroup.LayoutParams.MATCH_PARENT){
                realHeight = heightSpecSize -  getContentPaddingTop() - topPadding - bottomPadding - getContentPaddingBottom();
            }else{
                measureChildren(child,
                        widthMeasureSpec,
                        leftPadding + rightPadding + getContentPaddingLeft() + getContentPaddingRight(),
                        heightMeasureSpec,
                        topPadding + bottomPadding + getContentPaddingBottom() + getContentPaddingTop());
                realHeight = child.getMeasuredHeight();
            }

            child.measure(MeasureSpec.makeMeasureSpec(realWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(realHeight, MeasureSpec.EXACTLY));
        }
    }
}
