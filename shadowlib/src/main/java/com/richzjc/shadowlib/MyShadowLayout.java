package com.richzjc.shadowlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
    private int shadowSolidColor = Color.TRANSPARENT;
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
            if (cardView != null) {
                cardView.setCardBackgroundColor(((ColorDrawable) background).getColor());
            }else{
                createCardView();
                createShadowView();
                cardView.setCardBackgroundColor(((ColorDrawable) background).getColor());
            }
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
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
        cardView.setLayoutParams(itemParams);
        cardView.setContentPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        cardView.addView(child, itemParams);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        cardView.setContentPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, lp.height);
        cardView.measure(childWidthMeasureSpec, childHeightMeasureSpec);

        Log.i(TAG, cardView.getMeasuredWidth() + ", " + cardView.getMeasuredHeight());
        LayoutParams cardViewParams = (LayoutParams) cardView.getLayoutParams();
        int cardViewWidth = cardView.getMeasuredWidth();
        int cardViewHeight = cardView.getMeasuredHeight();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSizeSpec = cardViewWidth;
        int heightSizeSpec = cardViewHeight;
        int realWidth;
        int realHeight;

        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                if (widthSizeSpec < cardViewWidth) {
                    realWidth = widthSizeSpec;
                } else {
                    realWidth = cardViewWidth + cardViewParams.leftMargin + cardViewParams.rightMargin;
                    if (leftShow)
                        realWidth += shadowRadius;

                    if (rightShow)
                        realWidth += shadowRadius;
                    realWidth = Math.min(realWidth, widthSizeSpec);
                }
                break;

            default:
                realWidth = widthSizeSpec;
        }

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                if (heightSizeSpec < cardViewHeight) {
                    realHeight = heightSizeSpec;
                } else {
                    realHeight = cardViewHeight + cardViewParams.topMargin + cardViewParams.bottomMargin;

                    if (topShow)
                        realHeight += shadowRadius;

                    if (bottomShow)
                        realHeight += shadowRadius;
                    realHeight = Math.min(realHeight, heightSizeSpec);
                }
                break;

            default:
                realHeight = heightSizeSpec;
        }
        setMeasuredDimension(realWidth, realHeight);
        setSubChildMeasure();
    }

    private void setSubChildMeasure() {
        int shadowWidth = getMeasuredWidth();
        int shadowHeight = getMeasuredHeight();

        int cardViewWidth = getMeasuredWidth();
        int cardviewHeight = getMeasuredHeight();

        if (leftShow) {
            shadowRect.left = 0;
            cardViewWidth -= shadowRadius;
            cardViewRect.left = (int) shadowRadius;
        } else {
            cardViewWidth += (shadowRadius + cardCornerRadius);
            cardViewRect.left = (int) -(shadowRadius + cardCornerRadius);
            shadowWidth = (int) (shadowWidth + shadowRadius + cardCornerRadius);
            shadowRect.left = (int) -(shadowRadius + cardCornerRadius);
        }

        if (!rightShow) {
            shadowWidth = (int) (shadowWidth + shadowRadius + cardCornerRadius);
        }
        shadowRect.right = shadowRect.left + shadowWidth;

        if (rightShow) {
            cardViewWidth -= shadowRadius;
            cardViewRect.right = cardViewRect.left + cardViewWidth;
        } else {
            cardViewWidth += (shadowRadius + cardCornerRadius);
            cardViewRect.right = cardViewRect.left + cardViewWidth;
        }

        if (topShow) {
            shadowRect.top = 0;
            cardviewHeight -= shadowRadius;
            cardViewRect.top = (int) shadowRadius;
        } else {
            shadowHeight = (int) (shadowHeight + shadowRadius + cardCornerRadius);
            shadowRect.top = (int) -(shadowRadius + cardCornerRadius);
            cardviewHeight += (shadowRadius + cardCornerRadius);
            cardViewRect.top = (int) -(shadowRadius + cardCornerRadius);
        }

        if (!bottomShow) {
            shadowHeight = (int) (shadowHeight + shadowRadius + cardCornerRadius);
        }
        shadowRect.bottom = shadowRect.top + shadowHeight;

        if (bottomShow) {
            cardviewHeight -= shadowRadius;
            cardViewRect.bottom = cardViewRect.top + cardviewHeight;
        } else {
            cardviewHeight += (shadowRadius + cardCornerRadius);
            cardViewRect.bottom = cardViewRect.top + cardviewHeight;
        }

        shadowView.measure(MeasureSpec.makeMeasureSpec(shadowWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(shadowHeight, MeasureSpec.EXACTLY));
        cardView.measure(MeasureSpec.makeMeasureSpec(cardViewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(cardviewHeight, MeasureSpec.EXACTLY));
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
            topPadding = (int) (shadowRadius + cardCornerRadius);

        if (!bottomShow)
            bottomPadding = (int) (shadowRadius + cardCornerRadius);

        if (!leftShow)
            leftPadding = (int) (shadowRadius + cardCornerRadius);

        if (!rightShow)
            rightPadding = (int) (shadowRadius + cardCornerRadius);

        cardView.setMyPadding(leftPadding, topPadding, rightPadding, bottomPadding);
    }
}
