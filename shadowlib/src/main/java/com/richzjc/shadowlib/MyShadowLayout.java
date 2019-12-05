package com.richzjc.shadowlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
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
    private int shadowSolidColor = Color.TRANSPARENT;
    private float shadowRadius;
    private float cardCornerRadius;
    private ShadowView shadowView;
    private MyCardView cardView;
    private Rect cardViewRect = new Rect();
    private Rect shadowRect = new Rect();


    private int leftPadding;
    private int topPadding;
    private int rightPadding;
    private int bottomPadding;


    private int originLeftPadding;
    private int originTopPadding;
    private int originRightPadding;
    private int originBottomPadding;

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
        createCardView();
        createShadowView();
        initAttributes(attrs);
        initShadowView();
        initCardView();
    }

    private void createCardView() {
        if (cardView == null)
            cardView = new MyCardView(getContext());
    }

    private void createShadowView() {
        if (shadowView == null)
            shadowView = new ShadowView(getContext());
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
            shadowSolidColor = attr.getColor(R.styleable.ShadowLayout_hl_shadowSolidColor, Color.TRANSPARENT);
            shadowRadius = attr.getDimension(R.styleable.ShadowLayout_hl_shadowRadius, 0);
            cardCornerRadius = attr.getDimension(R.styleable.ShadowLayout_hl_cardCornerRadius, 0);
        } finally {
            attr.recycle();
        }
    }

    @Override
    public void setBackground(Drawable background) {
        if (background instanceof ColorDrawable) {
            createCardView();
            createShadowView();
            cardView.setCardBackgroundColor(((ColorDrawable) background).getColor());
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
        shadowView.setAttrs(shadowColor, shadowRadius, cardCornerRadius, shadowSolidColor);
        addView(shadowView, 0);
    }

    private void initCardView() {
        cardView.setElevation(0);
        cardView.setRadius(cardCornerRadius);
        addView(cardView);
    }

    private void addToCardView(View child, ViewGroup.LayoutParams params) {
        cardView.removeAllViews();
        ViewGroup.MarginLayoutParams itemParams = new FrameLayout.LayoutParams(params.width, params.height);
        if (params instanceof MarginLayoutParams) {
            MarginLayoutParams mps = (MarginLayoutParams) params;
            originLeftPadding = getPaddingLeft() + mps.leftMargin;
            originTopPadding = getPaddingTop() + mps.topMargin;
            originRightPadding = getPaddingRight() + mps.rightMargin;
            originBottomPadding = getPaddingBottom() + mps.bottomMargin;
        } else {
            originLeftPadding = getPaddingLeft();
            originTopPadding = getPaddingTop();
            originRightPadding = getPaddingRight();
            originBottomPadding = getPaddingBottom();
        }
        setShadowPadding();
        cardView.addView(child, itemParams);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        ViewGroup.LayoutParams params1 = cardView.getLayoutParams();
        if (params1 != null) {
            params1.width = params.width;
            params1.height = params.height;
            cardView.setLayoutParams(params1);
        }
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        originLeftPadding = left;
        originTopPadding = top;
        originRightPadding = right;
        originBottomPadding = bottom;
        if (cardView.getChildCount() > 0) {
            ViewGroup.LayoutParams params = cardView.getChildAt(0).getLayoutParams();
            if (params instanceof MarginLayoutParams) {
                MarginLayoutParams mps = (MarginLayoutParams) params;
                originLeftPadding = left + mps.leftMargin;
                originTopPadding = top + mps.topMargin;
                originRightPadding = right + mps.rightMargin;
                originBottomPadding = bottom + mps.bottomMargin;
            }
        }
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        cardView.setContentPadding(originLeftPadding, originTopPadding, originRightPadding, originBottomPadding);
        cardView.measure(widthMeasureSpec, heightMeasureSpec);
        setCurrentViewDimens(widthMeasureSpec, heightMeasureSpec);
        setShadowMeasure();
        setCardViewMeasure();
    }

    private void setCurrentViewDimens(int widthMeasureSpec, int heightMeasureSpec) {
        int realWidth = cardView.getMeasuredWidth();
        int realHeight = cardView.getMeasuredHeight();
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params != null && params.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            if (leftShow)
                realWidth += shadowRadius;
            if (rightShow)
                realWidth += shadowRadius;

            if(widthSize != 0){
                realWidth = Math.min(widthSize, realWidth);
            }
        }

        if (params != null && params.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            if (topShow)
                realHeight += shadowRadius;
            if (bottomShow)
                realHeight += shadowRadius;
            if(heightSize != 0) {
                realHeight = Math.min(heightSize, realHeight);
            }
        }
        setMeasuredDimension(realWidth, realHeight);
    }

    private void setShadowMeasure() {
        int shadowWidth = getMeasuredWidth();
        int shadowHeight = getMeasuredHeight();
        if (leftShow) {
            shadowRect.left = 0;
        } else {
            shadowWidth = (int) (shadowWidth + shadowRadius + cardCornerRadius);
            shadowRect.left = (int) -(shadowRadius + cardCornerRadius);
        }

        if (!rightShow) {
            shadowWidth = (int) (shadowWidth + shadowRadius + cardCornerRadius);
        }
        shadowRect.right = shadowRect.left + shadowWidth;

        if (topShow) {
            shadowRect.top = 0;
        } else {
            shadowHeight = (int) (shadowHeight + shadowRadius + cardCornerRadius);
            shadowRect.top = (int) -(shadowRadius + cardCornerRadius);
        }

        if (!bottomShow) {
            shadowHeight = (int) (shadowHeight + shadowRadius + cardCornerRadius);
        }
        shadowRect.bottom = shadowRect.top + shadowHeight;
        shadowView.measure(MeasureSpec.makeMeasureSpec(shadowWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(shadowHeight, MeasureSpec.EXACTLY));
    }

    private void setCardViewMeasure() {
        cardView.setContentPadding(originLeftPadding + leftPadding,
                originTopPadding + topPadding,
                originRightPadding + rightPadding,
                originBottomPadding + bottomPadding);
        int cardViewWidth = getMeasuredWidth();
        int cardviewHeight = getMeasuredHeight();

        if (leftShow) {
            cardViewWidth -= shadowRadius;
            cardViewRect.left = (int) shadowRadius;
        } else {
            cardViewWidth += (shadowRadius + cardCornerRadius);
            cardViewRect.left = (int) -(shadowRadius + cardCornerRadius);
        }

        if (rightShow) {
            cardViewWidth -= shadowRadius;
        } else {
            cardViewWidth += (shadowRadius + cardCornerRadius);
        }

        cardViewRect.right = cardViewRect.left + cardViewWidth;

        if (topShow) {
            cardviewHeight -= shadowRadius;
            cardViewRect.top = (int) shadowRadius;
        } else {
            cardviewHeight += (shadowRadius + cardCornerRadius);
            cardViewRect.top = (int) -(shadowRadius + cardCornerRadius);
        }

        if (bottomShow) {
            cardviewHeight -= shadowRadius;
        } else {
            cardviewHeight += (shadowRadius + cardCornerRadius);
        }

        cardViewRect.bottom = cardViewRect.top + cardviewHeight;
        int cardViewWidthSpec = MeasureSpec.makeMeasureSpec(cardViewWidth, MeasureSpec.EXACTLY);
        int cardViewHeightSpec = MeasureSpec.makeMeasureSpec(cardviewHeight, MeasureSpec.EXACTLY);
        cardView.measure(cardViewWidthSpec, cardViewHeightSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        shadowView.layout(shadowRect.left, shadowRect.top, shadowRect.right, shadowRect.bottom);
        cardView.layout(cardViewRect.left, cardViewRect.top, cardViewRect.right, cardViewRect.bottom);
    }

    private void setShadowPadding() {
        if (!topShow)
            topPadding = (int) (shadowRadius + cardCornerRadius);
        else
            topPadding = 0;

        if (!bottomShow)
            bottomPadding = (int) (shadowRadius + cardCornerRadius);
        else
            bottomPadding = 0;

        if (!leftShow)
            leftPadding = (int) (shadowRadius + cardCornerRadius);
        else
            leftPadding = 0;

        if (!rightShow)
            rightPadding = (int) (shadowRadius + cardCornerRadius);
        else
            rightPadding = 0;
    }
}
