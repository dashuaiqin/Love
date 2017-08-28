package com.qin.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016/2/18.
 */
public class ChatProgress extends View {
    private Context mContext;
    public static final int STROKE_WIDTH = 10;
    private int mStrokeWidth;
    private Paint mPaint;
    private int mCenterX;
    private boolean mShowProgress = true;// 是否显示
    private int mRadius = 15;// 圈圈半径
    private int currentPosition = 0;// 当前开始画的位置
    private int mCurrentCount = 5;// 转圈圈的块块的个数

    public ChatProgress(Context context) {
        super(context);
    }


    public ChatProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        init();
        drawOval(canvas,mCenterX,mRadius);
    }

    /**
     * 初始化
     */
    private void init() {
        mStrokeWidth = STROKE_WIDTH;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);// 消除锯齿


        mCenterX = getWidth() / 2;// 圆心x
    }

    public void startProgress() {
        mShowProgress = true;
    }

    public void stopProgress() {
        mShowProgress = false;
    }

    /**
     * 根据参数画出每个小块
     *
     * @param canvas
     * @param centre
     * @param radius
     */
    private void drawOval(Canvas canvas, int centre, int radius) {
        int mCount;// 块块的个数
        float mSplitSize = 8 * 1.0f;// 块块空隙的宽度
        float itemSize = 8 * 1.0f;// 每个块块的宽度

        // 根据块块的大小和空隙的大小求出块块的个数
        mCount = (int) (360 * 1.0f / (mSplitSize + itemSize));
        // 求余数，将余数分配到空隙
        float yu = (360 * 1.0f % (mSplitSize + itemSize));
        mSplitSize += yu / mCount;

        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius); // 用于定义的圆弧的形状和大小的界限

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(Color.parseColor("#B5B5B5")); // 设置圆环的颜色

        for (int i = 0; i < mCount; i++) {
            canvas.drawArc(oval, i * (itemSize + mSplitSize), itemSize, false,
                    mPaint); // 根据进度画圆弧
        }
        if (currentPosition == mCount - 1) {
            currentPosition = 0;
        }

        for (int i = 0; i < mCurrentCount; i++) {
            if (i==mCurrentCount-2) {
                mPaint.setColor(Color.parseColor("#50575757")); // 设置圆环的颜色
            }else if(i==mCurrentCount-1) {
                mPaint.setColor(Color.parseColor("#96575757")); // 设置圆环的颜色
            }else{
                mPaint.setColor(Color.parseColor("#35575757")); // 设置圆环的颜色
            }
            canvas.drawArc(oval, (i + currentPosition)
                    * (itemSize + mSplitSize), itemSize, false, mPaint); // 根据进度画圆弧
        }
        currentPosition++;
        invalidate();
    }

}
