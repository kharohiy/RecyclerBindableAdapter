package com.danil.recyclerbindableadapter.sample.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

public class FourSquareCardView extends CardView {
    public FourSquareCardView(Context context) {
        super(context);
    }

    public FourSquareCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FourSquareCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
