package com.richzjc.shadowlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

class ShadowView extends View {

    private Paint mPaint;
    private int mShadowColor = 0xffe4e4e4;
    private float mShadowRadius = 100;
    private int shadowSolidColor = Color.WHITE;
    private float mRadius = 50;
    private int width;
    private int height;

    public ShadowView(Context context) {
        this(context, null);
    }

    public ShadowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    public void setAttrs(int mShadowColor, float mShadowRadius, float mRadius, int shadowSolidColor){
        this.mShadowColor = mShadowColor;
        this.mShadowRadius = mShadowRadius;
        this.mRadius = mRadius;
        this.shadowSolidColor = shadowSolidColor;
    }

    private void initPaint(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth() - getPaddingLeft() - getPaddingRight();//控件实际大小
        height = getHeight() - getPaddingTop() - getPaddingBottom();
        mPaint.setShader(null);
        mPaint.setColor(mShadowColor);
        mPaint.setShadowLayer(mShadowRadius, 1, 1, mShadowColor);
        Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas newCanvas = new Canvas(target);
        RectF rectF = new RectF(mShadowRadius, mShadowRadius, width - mShadowRadius, height - mShadowRadius);
        mPaint.setColor(Color.TRANSPARENT);
        newCanvas.drawRoundRect(rectF, mRadius, mRadius, mPaint);
        canvas.drawBitmap(target, getPaddingLeft(), getPaddingTop(), mPaint);
    }
}
