package com.richzjc.shadowlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyShadowLayout extends FrameLayout {
    private boolean topShow;
    private boolean bottomShow;
    private boolean leftShow;
    private boolean rightShow;
    private int shadowColor;
    private int shadowSolidColor;
    private float shadowRadius;
    private float cardCornerRadius;
    private ShadowView shadowView;
    private MyCardView cardView;
    private final String TAG = MyShadowLayout.class.getSimpleName();
    private Rect cardViewRect = new Rect();
    private Rect shadowRect = new Rect();

    public void setShadowShow(boolean leftShow, boolean topShow, boolean rightShow, boolean bottomShow) {
        this.leftShow = leftShow;
        this.rightShow = rightShow;
        this.topShow = topShow;
        this.bottomShow = bottomShow;
        setShadowPadding();
        requestLayout();
    }

    public MyShadowLayout(@NonNull Context context) {
        this(context, null);
    }

    public MyShadowLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyShadowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(attrs);
        initShadowView();
        initCardView();
    }


    private void initAttributes(AttributeSet attrs) {
        TypedArray attr = getContext().obtainStyledAttributes(attrs, R.styleable.ShadowLayout);
        if (attr == null) {
            return;
        }

        try {
            bottomShow = attr.getBoolean(R.styleable.ShadowLayout_hl_bottomShow, true);
            topShow = attr.getBoolean(R.styleable.ShadowLayout_hl_topShow, true);
            leftShow = attr.getBoolean(R.styleable.ShadowLayout_hl_leftShow, true);
            rightShow = attr.getBoolean(R.styleable.ShadowLayout_hl_rightShow, true);
            shadowColor = attr.getColor(R.styleable.ShadowLayout_hl_shadowColor, Color.parseColor("#747f8c33"));
            shadowSolidColor = attr.getColor(R.styleable.ShadowLayout_hl_shadowSolidColor, Color.BLUE);
            shadowRadius = attr.getDimension(R.styleable.ShadowLayout_hl_shadowRadius, 0);
            cardCornerRadius = attr.getDimension(R.styleable.ShadowLayout_hl_cardCornerRadius, 0);
        } finally {
            attr.recycle();
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child == shadowView || child == cardView) {
            super.addView(child, index, params);
        } else if (cardView != null) {
            addToCardView(child, params);
        }
    }

    private void initShadowView() {
        shadowView = new ShadowView(getContext());
        shadowView.setAttrs(shadowColor, shadowRadius, cardCornerRadius, shadowSolidColor);
        addView(shadowView, 0);
    }

    private void initCardView() {
        cardView = new MyCardView(getContext());
        cardView.setElevation(0);
        cardView.setRadius(cardCornerRadius);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(cardView, params);
    }

    private void addToCardView(View child, ViewGroup.LayoutParams params) {
        cardView.removeAllViews();
        ViewGroup.MarginLayoutParams itemParams = new FrameLayout.LayoutParams(params.width, params.height);
        if (params instanceof MarginLayoutParams) {
            MarginLayoutParams mps = (MarginLayoutParams) params;
            itemParams.leftMargin = mps.leftMargin;
            itemParams.topMargin = mps.topMargin;
            itemParams.rightMargin = mps.rightMargin;
            itemParams.bottomMargin = mps.bottomMargin;
        }
        setShadowPadding();
        cardView.addView(child, itemParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int width = cardView.getMeasuredWidth();
        int height = cardView.getMeasuredHeight();
        Log.i(TAG, "height = " + height + ", width  = " + width);

        int cardViewWidth = width;
        int cardviewHeight = height;
        if (leftShow) {
            cardViewWidth = (int) (cardViewWidth - shadowRadius);
            cardViewRect.left = (int) (paddingLeft + shadowRadius);
        } else {
            cardViewWidth = (int) (cardViewWidth + paddingLeft + cardCornerRadius + shadowRadius);
            cardViewRect.left = (int) -(shadowRadius + cardCornerRadius);
        }

        if (rightShow) {
            cardViewWidth = (int) (cardViewWidth - shadowRadius);
            cardViewRect.right = cardViewRect.left + cardViewWidth;
        }else{
            cardViewWidth = (int) (cardViewWidth + paddingRight + cardCornerRadius + shadowRadius);
            cardViewRect.right = cardViewRect.left + cardViewWidth;
        }

        if (topShow) {
            cardviewHeight = height;
            cardViewRect.top = (int) shadowRadius + paddingTop;
        } else {
            cardviewHeight = (int) (cardviewHeight + paddingTop + cardCornerRadius  + shadowRadius);
            cardViewRect.top = (int) -(shadowRadius + cardCornerRadius);
        }

        if(bottomShow){
            cardViewRect.bottom = cardViewRect.top + cardviewHeight;
        }else{
            cardviewHeight = (int) (cardviewHeight + paddingBottom + cardCornerRadius  + shadowRadius);
            cardViewRect.bottom = cardViewRect.top + cardviewHeight;
        }

        cardView.measure(MeasureSpec.makeMeasureSpec(cardViewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(cardviewHeight, MeasureSpec.EXACTLY));

        int shadowWidth = width;
        int shadowHeight = height;
        int realHeight = height + paddingTop + paddingBottom;

        if (leftShow) {
            shadowRect.left = paddingLeft;
        } else {
            shadowWidth = (int) (shadowWidth + paddingLeft + shadowRadius + cardCornerRadius);
            shadowRect.left = (int) -(shadowRadius + cardCornerRadius);
        }

        if (rightShow) {
            shadowRect.right = shadowRect.left + shadowWidth;
        } else {
            shadowWidth = (int) (shadowWidth + paddingRight + shadowRadius + cardCornerRadius);
            shadowRect.right = shadowRect.left + shadowWidth;
        }

        if (topShow) {
            realHeight = (int) (realHeight + shadowRadius);
            shadowRect.top = paddingTop;
        } else {
            realHeight = height + paddingTop + paddingBottom;
            shadowHeight = (int) (shadowHeight + paddingTop + shadowRadius + cardCornerRadius);
            shadowRect.top = (int) -(shadowRadius + cardCornerRadius);
        }

        if (bottomShow) {
            realHeight = (int) (realHeight + shadowRadius);
            shadowRect.bottom = (int) (cardViewRect.bottom + shadowRadius);
        } else {
            shadowHeight = (int) (shadowHeight + paddingBottom + shadowRadius + cardCornerRadius);
            shadowRect.bottom = shadowRect.top + shadowHeight;
        }

        shadowView.measure(MeasureSpec.makeMeasureSpec(shadowWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(shadowHeight, MeasureSpec.EXACTLY));
        setMeasuredDimension(width + paddingLeft + paddingRight, realHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        shadowView.layout(shadowRect.left, shadowRect.top, shadowRect.right, shadowRect.bottom);
        cardView.layout(cardViewRect.left, cardViewRect.top, cardViewRect.right, cardViewRect.bottom);
    }

    private void setShadowPadding() {
        int topPadding = 0;
        int bottomPadding = 0;
        int leftPadding = 0;
        int rightPadding = 0;
        if (!topShow)
            topPadding = (int) (getPaddingTop() + shadowRadius + cardCornerRadius);

        if (!bottomShow)
            bottomPadding = (int) (getPaddingBottom() + shadowRadius + cardCornerRadius);

        if (!leftShow)
            leftPadding = (int) (getPaddingLeft() + shadowRadius + cardCornerRadius);

        if (!rightShow)
            rightPadding = (int) (getPaddingRight() + shadowRadius + cardCornerRadius);

        cardView.setMyPadding(leftPadding, topPadding, rightPadding, bottomPadding);
    }
}
