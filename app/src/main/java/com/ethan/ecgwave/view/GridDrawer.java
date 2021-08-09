package com.ethan.ecgwave.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.Log;
import android.view.MotionEvent;

public class GridDrawer extends BaseDrawer{
    private static final String TAG = "BackgroundDrawer";
    private int lineColor = Color.GREEN;
    private PathEffect pathEffect = new DashPathEffect(new float[]{2 , 3} , 0);
    // 每小格的实际长度
    private float gridSpace = 30f;
    // 水平方向和垂直方向的线条数
    private int hLineCount , vLineCount;

    public GridDrawer(ECGRealTimeChart baseChart){
        this.mBaseChart = baseChart;
        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setAntiAlias(true);
        linePaint.setDither(true);
        linePaint.setStrokeWidth(1f);
    }

    /**
     * @return 每小格边长
     */
    public float getGridSpace(){
        return this.gridSpace;
    }

    public void setGridSpace(float space){
        this.gridSpace = space;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChange() {
        super.onSizeChange();
        initBitmap();
    }

    @Override
    protected boolean onTouchEvent(MotionEvent event) {

        return false;
    }

    @Override
    protected void computeScroll() {

    }

    /**
     * 准备好画网格的 Bitmap
     */
    private void initBitmap(){
        // 计算横线和竖线条数
        hLineCount = (int) (viewHeight / gridSpace);
        vLineCount = (int) (viewWidth / gridSpace);
        // 画横线
        for (int h = 0; h < hLineCount; h ++){
            float startX = 0f;
            float startY = gridSpace * h;
            float stopX = viewWidth;
            float stopY = gridSpace * h;
            // 每个 5根画一条粗线
            if (h % 5 != 0){
                linePaint.setPathEffect(pathEffect);
                linePaint.setStrokeWidth(1.5f);
            }else {
                linePaint.setPathEffect(null);
                linePaint.setStrokeWidth(3f);
            }
            // 画线
            bitmapCanvas.drawLine(startX, startY, stopX,stopY, linePaint);
        }
        // 画竖线
        for (int v = 0; v < vLineCount; v ++){
            float startX = gridSpace * v;
            float startY = 0f;
            float stopX = gridSpace * v;
            float stopY = viewHeight;
            // 每隔 5根画一条竖线
            if (v % 5 != 0){
                linePaint.setPathEffect(pathEffect);
                linePaint.setStrokeWidth(1.5f);
            }else {
                linePaint.setPathEffect(null);
                linePaint.setStrokeWidth(3f);
                Log.d(TAG, "v = " + v);
            }
            // 画线
            bitmapCanvas.drawLine(startX, startY, stopX,stopY, linePaint);
        }
    }
}
