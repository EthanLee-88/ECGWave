package com.ethan.ecgwave.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.util.TypedValue;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

public abstract class BaseDrawer extends Drawer{
    private static final String TAG = "BaseDrawer";

    protected ECGRealTimeChart mBaseChart;
    protected Paint linePaint;
    // 画 Bitmap
    protected Bitmap gridBitmap;
    // 画 Canvas
    protected Canvas bitmapCanvas;
    // 控件宽高
    protected int viewWidth, viewHeight;

    @Override
    protected void onSizeChange() {
        viewWidth = mBaseChart.getWidth();
        viewHeight = mBaseChart.getHeight();

        gridBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(gridBitmap);

        Log.d(TAG, "onSizeChange - " + "-- width = " +
                mBaseChart.getWidth() + "-- height = " + mBaseChart.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(gridBitmap, mBaseChart.getScrollX(), 0, null);
    }

    protected float spToPx(float sp){
       return TypedValue.applyDimension(COMPLEX_UNIT_SP, sp, mBaseChart.getResources().getDisplayMetrics());
    }
}
