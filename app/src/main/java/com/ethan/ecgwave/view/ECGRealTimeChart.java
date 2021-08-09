package com.ethan.ecgwave.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.ethan.ecgwave.R;

import java.util.List;

public class ECGRealTimeChart extends BaseChart {
    private static final String TAG = "ECGRealTimeChart";
    // 画网格
    private GridDrawer mBackgroundDrawer;
    // 画曲线
    private PathDrawer mPathDrawer;
    // 画单位
    private NumericalUnitDrawer mNumericalUnitDrawer;
    // 没数据来时才去设置参数以及滑动
    private boolean noDataComing = false;

    public ECGRealTimeChart(Context context) {
        this(context, null);
    }

    public ECGRealTimeChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ECGRealTimeChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this.mBackgroundDrawer = new GridDrawer(this);
        this.mPathDrawer = new PathDrawer(this);
        this.mNumericalUnitDrawer = new NumericalUnitDrawer(this);
    }

    public void printLog(String log) {
        Log.d(TAG, log);
    }

    /**
     * @param noData 没数据来了
     */
    public void setNoDataComing(boolean noData) {
        this.noDataComing = noData;
    }

    public boolean getNoDataComing() {
        return this.noDataComing;
    }

    /**
     * 设置横坐标每大格内显示的数据点数，即 X轴方向的增益
     *
     * @param numbersPerGrid 数据点
     */
    public void setDataNumbersPerGrid(int numbersPerGrid){
        this.mPathDrawer.setDataNumbersPerGrid(numbersPerGrid);
    }

    /**
     * @return 横坐标每大格点数
     */
    public int getDataNumbersPerGrid(){
        return this.mPathDrawer.getDataNumbersPerGrid();
    }

    /**
     * 设置纵坐标每大格代表多少毫伏，即纵坐标增益
     *
     * @param mv 毫伏
     */
    public void setMvPerLargeGrid(float mv){
        this.mPathDrawer.setMvPerLargeGrid(mv);
    }

    /**
     * @return 纵坐标每大格毫伏数
     */
    public float getMvPerLargeGrid(){
        return this.mPathDrawer.getMvPerLargeGrid();
    }

    /**
     * @return 每小格边长
     */
    public float getGridSpace() {
        return mBackgroundDrawer.getGridSpace();
    }

    /**
     * 清除数据
     */
    public void clearData() {
        this.mPathDrawer.clearData();
        this.mPathDrawer.reset();
        scrollTo(0, 0);
    }

    /**
     * @param data 添加数据
     */
    public void addData(int[] data) {
        this.mPathDrawer.addData(data);
    }

    /**
     * @param data 添加数据
     */
    public void addData(int data) {
        this.mPathDrawer.addData(data);
    }
    /**
     * @param data 添加数据
     */
    public void addData(List data) {
        this.mPathDrawer.addData(data);
    }

    /**
     * 获取当前数据
     */
    public List<Integer> getData() {
        return this.mPathDrawer.getData();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBackgroundDrawer.onSizeChange();
        mPathDrawer.onSizeChange();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        setBackgroundColor(Color.parseColor("#dd000000"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.mBackgroundDrawer.onDraw(canvas);
        this.mPathDrawer.onDraw(canvas);
        this.mNumericalUnitDrawer.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!getNoDataComing()) return false;
        return mPathDrawer.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        this.mPathDrawer.computeScroll();
    }
}
