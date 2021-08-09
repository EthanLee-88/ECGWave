package com.ethan.ecgwave.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.Scroller;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class PathDrawer extends BaseDrawer {
    private static final String TAG = "PathDrawer";
    private List<Integer> data = new CopyOnWriteArrayList<>();
    private Path dataPath;
    // 两个数据 X轴方向的距离
    private float dataSpaceX;
    // 每小格的边长
    private float smallGridSpace;
    // 每大格 X方向画多少个数据点，可设
    private int dataNumbersPerGrid = 54;
    // Y是 0的位置，从上往下偏移多少大格
    private int offset = 5;
    // 每一大格代表多少毫伏，可设
    private float mvPerLargeGrid = 1000f;
    // 水平平移记录点
    private float lastX = 0;
    // 惯性滑动
    private Scroller mScroller;
    // 速度追踪
    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    // 记录两指按下的点
    private PointF pointOne, pointTwo;
    // 用于每次双指按下时，记录当次拉伸事件 Y增益初始值
    float mvPerLargeGridOnThisTime = 1000f;
    // 用于每次双指按下时，记录当次拉伸事件 X增益初始值
    private int numbersPerLargeGridOnThisTime = 54;

    /**
     * 重置
     */
    public void reset() {
        this.lastX = 0;
        this.dataNumbersPerGrid = 54;
        this.mvPerLargeGrid = 1000f;
        this.smallGridSpace = this.mBaseChart.getGridSpace();
        this.dataSpaceX = this.smallGridSpace * 5 / this.dataNumbersPerGrid;
        if (dataPath != null) dataPath.reset();
        if (data != null) data.clear();
        update();
    }

    public PathDrawer(ECGRealTimeChart baseChart) {
        this.mBaseChart = baseChart;
        this.linePaint = new Paint();
        this.linePaint.setColor(Color.RED);
        this.linePaint.setStrokeWidth(5f);
        this.linePaint.setStyle(Paint.Style.STROKE);
        this.linePaint.setDither(true);
        this.linePaint.setAntiAlias(true);
        this.dataPath = new Path();
        this.smallGridSpace = this.mBaseChart.getGridSpace();
        this.dataSpaceX = this.smallGridSpace * 5 / this.dataNumbersPerGrid;
        mScroller = new Scroller(mBaseChart.getContext());
    }

    public void setPaintWidth(float paintWidth){
        this.linePaint.setStrokeWidth(paintWidth);
        update();
    }

    /**
     * 重新设置采样率
     *
     * @param numbers
     */
    public void setDataNumbersPerGrid(int numbers) {
        if (numbers < 27) numbers = 27;
        if (numbers > 66) numbers = 66;

        this.dataNumbersPerGrid = numbers;
        this.dataSpaceX = this.smallGridSpace * 5 / this.dataNumbersPerGrid;
        update();
    }

    /**
     * 获取每小格显示的数据个数，再结合医疗版的采样率，就可以算出一格显示了多长时间的数据
     *
     * @return
     */
    public int getDataNumbersPerGrid() {
        return this.dataNumbersPerGrid;
    }

    /**
     * 设置每大格代表多少毫伏
     *
     * @param mv 毫伏
     */
    public void setMvPerLargeGrid(float mv) {
        if (mv < 500) mv = 500;
        if (mv > 1500) mv = 1500;

        this.mvPerLargeGrid = mv;
        update();
    }

    /**
     * @return 获取没大哥代表多少毫伏
     */
    public float getMvPerLargeGrid() {
        return this.mvPerLargeGrid;
    }

    public List<Integer> getData() {
        return this.data;
    }

    /**
     * @param dats 添加数据
     */
    public void addData(int[] dats) {
        if (dats == null) return;
        for (int dat : dats) {
            this.data.add(dat);
        }
        update();
    }

    /**
     * @param dats 添加数据
     */
    public void addData(List<Integer> dats) {
        if (dats == null) return;
        this.data.addAll(dats);
        update();
    }

    /**
     * @param dat 添加数据
     */
    public void addData(int dat) {
        this.data.add(dat);
        update();
    }

    /**
     * 清除数据
     */
    public void clearData() {
        this.data.clear();
        update();
    }

    /**
     * 更新 UI
     */
    private void update() {
        if (createPath()) {
            mBaseChart.postInvalidate();
        }
    }

    /**
     * 创建曲线
     */
    private boolean createPath() {
        // 曲线长度超过控件宽度，曲线起点往左移
        float startX = (this.data.size() * dataSpaceX > viewWidth) ?
                (viewWidth - (this.data.size() * dataSpaceX)) : 0f;
        dataPath.reset();
        for (int i = 0; i < this.data.size(); i++) {
            float x = startX + i * this.dataSpaceX;
            float y = getVisibleY(this.data.get(i));
            if (i == 0) {
                dataPath.moveTo(x, y);
            } else {
                dataPath.lineTo(x, y);
            }
        }
        return true;
    }

    private Path copyPath;
    private Path copyPath(){
        if (copyPath == null) copyPath = new Path();
        // 曲线长度超过控件宽度，曲线起点往左移
        float startX = (this.data.size() * dataSpaceX > viewWidth) ?
                (viewWidth - (this.data.size() * dataSpaceX)) : 0f;
        copyPath.reset();
        for (int i = 0; i < this.data.size(); i++) {
            float x = startX + i * this.dataSpaceX;
            float y = getVisibleY(this.data.get(i)) + smallGridSpace * 5 * 5;
            if (i == 0) {
                copyPath.moveTo(x, y);
            } else {
                copyPath.lineTo(x, y);
            }
        }
        return copyPath;
    }

    /**
     * 电压 mv（毫伏）在 Y轴方向的换算
     * 屏幕向上往下是 Y 轴正方向，所以电压值要乘以 -1进行翻转
     * 目前默认每一大格代表 1000 mv，而真正一大格的宽度只有 150,所以 data要以两数换算
     * Y == 0，是在 View的上边缘，所以要向下偏移将波形显示在中间
     *
     * @param data
     * @return
     */
    private float getVisibleY(int data) {
        // 电压值换算成 Y值
        float visibleY = -smallGridSpace * 5 / mvPerLargeGrid * data;
        // 向下偏移
        visibleY = visibleY + smallGridSpace * 5 * offset;
        return visibleY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(dataPath, linePaint);
        canvas.drawPath(copyPath(),linePaint);
    }

    @Override
    protected void onSizeChange() {
        super.onSizeChange();
        createPath();
    }

    /**
     * 处理onTouch事件
     *
     * @param event 事件
     * @return 拦截
     */
    @Override
    protected boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "pointerCount = " + event.getPointerCount());
        if (event.getPointerCount() == 1) {
            singlePoint(event);
        }
        if (event.getPointerCount() == 2) {
            doublePoint(event);
        }
        return true;
    }

    /**
     * @param event 单指事件
     */
    private void singlePoint(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - lastX;
                delWithActionMove(deltaX);
                lastX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                // 计算滑动速度
                computeVelocity();
                break;
        }
    }

    /**
     * @param event 双指事件
     */
    private void doublePoint(MotionEvent event) {
        if (pointOne == null) pointOne = new PointF();
        if (pointTwo == null) pointTwo = new PointF();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:  // 第二根手指按下
                Log.d(TAG, "ACTION_POINTER_DOWN");
                saveLastPoint(event);
                numbersPerLargeGridOnThisTime = getDataNumbersPerGrid();
                mvPerLargeGridOnThisTime = getMvPerLargeGrid();
                break;
            case MotionEvent.ACTION_MOVE:  // 双指拉伸
                Log.d(TAG, "ACTION_MOVE");
                getScaleX(event);
                getScaleY(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:  // 先离开的手指
                Log.d(TAG, "ACTION_POINTER_UP");
                break;
        }
    }

    /**
     * 处理 Y方向的拉伸
     *
     * @param event 事件
     * @return 拉伸量
     */
    private float getScaleY(MotionEvent event) {
        float pointOneY = event.getY(0);
        float pointTwoY = event.getY(1);
        // 计算 Y轴方向的拉伸量
        float deltaScaleY = Math.abs(pointOneY - pointTwoY) - Math.abs(pointOne.y - pointTwo.y);
        // 算出最终增益
        float perMV = mvPerLargeGridOnThisTime - deltaScaleY;
        setMvPerLargeGrid(perMV);
        return deltaScaleY;
    }

    /**
     * 处理 X方向的拉伸
     *
     * @param event 事件
     * @return 拉伸量
     */
    private float getScaleX(MotionEvent event) {
        float pointOneX = event.getX(0);
        float pointTwoX = event.getX(1);
        // 算出 X轴方向的拉伸量
        float deltaScaleX = Math.abs(pointOneX - pointTwoX) - Math.abs(pointOne.x - pointTwo.x);
        // 设置拉伸敏感度
        int inDevi = mBaseChart.getWidth() / 54;
        // 计算拉伸时增益偏移量
        int inDe = (int) deltaScaleX / inDevi;
        // 算出最终增益
        int perNumber = numbersPerLargeGridOnThisTime - inDe;
        // 设置增益
        setDataNumbersPerGrid(perNumber);
        return deltaScaleX;
    }

    /**
     * 记录双指按下的点
     *
     * @param event 事件
     */
    private void saveLastPoint(MotionEvent event) {
        pointOne.x = event.getX(0);
        pointOne.y = event.getY(0);
        pointTwo.x = event.getX(1);
        pointTwo.y = event.getY(1);
    }

    /**
     * @param deltaX 处理 MOVE事件
     */
    private void delWithActionMove(float deltaX) {
        if (this.data.size() * dataSpaceX <= viewWidth) return;
        int leftBorder = getLeftBorder(); // 左边界
        int rightBorder = getRightBorder(); // 右边界
        int scrollX = mBaseChart.getScrollX(); // X轴滑动偏移量

        if ((scrollX <= leftBorder) && (deltaX > 0)) {
            mBaseChart.scrollTo((int) (viewWidth - this.data.size() * dataSpaceX), 0);
        } else if ((scrollX >= rightBorder) && (deltaX < 0)) {
            mBaseChart.scrollTo(0, 0);
        } else {
            mBaseChart.scrollBy((int) -deltaX, 0);
        }
    }

    /**
     * 处理惯性滑动
     */
    @Override
    protected void computeScroll() {
        if (mScroller == null) return;
        if (mScroller.computeScrollOffset()) {
            mBaseChart.scrollTo(mScroller.getCurrX(), 0);
        }
    }

    /**
     * 计算滑动速度
     */
    private void computeVelocity() {
        mVelocityTracker.computeCurrentVelocity(500);
        float velocityX = mVelocityTracker.getXVelocity();
        // 初始化 Scroller
        Log.d(TAG, "velocityX = " + velocityX);
        fling(mBaseChart.getScrollX(), 0, -(int) velocityX, 0,
                getLeftBorder(), getRightBorder(), 0, 0);
    }

    /**
     * @param startX    起始 X
     * @param startY    起始 Y
     * @param velocityX X 方向速度
     * @param velocityY Y 方向速度
     * @param minX      左边界
     * @param maxX      右边界
     * @param minY      上边界
     * @param maxY      下边界
     */
    private void fling(int startX, int startY, int velocityX, int velocityY,
                       int minX, int maxX, int minY, int maxY) {
        if (mScroller == null) return;
        mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
    }

    /**
     * @return 左边界
     */
    private int getLeftBorder() {
        int left = 0;
        if (this.data.size() * dataSpaceX > viewHeight) {
            left = (int) (viewWidth - this.data.size() * dataSpaceX);
        }
        return left;
    }

    /**
     * @return 右边界
     */
    private int getRightBorder() {
        return 0;
    }
}
