package com.ethan.ecgwave.view;

import android.graphics.Canvas;
import android.view.MotionEvent;

public abstract class Drawer {
   protected abstract void onDraw(Canvas canvas);
   protected abstract void onSizeChange();
   protected abstract boolean onTouchEvent(MotionEvent event);
   protected abstract void computeScroll();
}
