package com.ethan.ecgwave.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

public class NumericalUnitDrawer extends BaseDrawer{
    private int lineColor = Color.RED;

    public NumericalUnitDrawer(ECGRealTimeChart baseChart){
        this.mBaseChart = baseChart;
        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setAntiAlias(true);
        linePaint.setDither(true);
        linePaint.setStrokeWidth(2f);
        linePaint.setTextSize(spToPx(12f));
    }

    @Override
    protected boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    protected void computeScroll() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
       canvas.drawText("Y : " + mBaseChart.getMvPerLargeGrid() + "mv / 大格",
               30f + mBaseChart.getScrollX(), 90f, linePaint);
        canvas.drawText("X : " + mBaseChart.getDataNumbersPerGrid() + "点 / 大格",
                30f + mBaseChart.getScrollX(), 60f, linePaint);
    }
}
