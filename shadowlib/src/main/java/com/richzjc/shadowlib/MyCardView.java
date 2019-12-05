package com.richzjc.shadowlib;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.lang.reflect.Field;

class MyCardView extends CardView {

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
    public void setContentPadding(int left, int top, int right, int bottom) {
        setRealPadding(left, top, right, bottom);
        super.setContentPadding(left, top, right, bottom);
    }


    private void setRealPadding(int left, int top, int right, int bottom) {
        try {
            Field leftField  = View.class.getDeclaredField("mPaddingLeft");
            Field topField  = View.class.getDeclaredField("mPaddingTop");
            Field rightField  = View.class.getDeclaredField("mPaddingRight");
            Field bottomField  = View.class.getDeclaredField("mPaddingBottom");
            leftField.setAccessible(true);
            topField.setAccessible(true);
            rightField.setAccessible(true);
            bottomField.setAccessible(true);
            leftField.set(this, left);
            topField.set(this, top);
            rightField.set(this, right);
            bottomField.set(this, bottom);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
