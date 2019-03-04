package com.example.customviewdemo.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.customviewdemo.R;


public class CountDownView extends View {
    private static final String TAG = "CountDownView";
    //宽度
    private int mWidth;
    //高度
    private int mHeight;
    //圆环颜色
    private int mRingColor;
    //圆环宽度
    private float mRingWidth;
    //文本大小
    private float mTextSize;
    //文本颜色
    private int mTextColor;
    //文本的矩形区域
    private RectF mRectF;


    private Paint mArcPaint;
    private Paint mCirclePaint;
    private Paint mTextPaint;

    private int mCountdownTime;

    private float mCurrentProgress;

    private OnCountDownFinishListener mListener;


    private float startAngle;
    private float sweepAngle;

    private DirectionType directionType = DirectionType.CW;

    public enum DirectionType {//方向
        /**
         * 顺时针
         * clockwise
         */
        CW,

        /**
         * 逆时针
         * counterclockwise
         */
        CCW
    }

    public CountDownView(Context context) {
        super(context, null);
    }

    public CountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        Log.d(TAG, "CountDownView:");
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountDownView);
        mRingColor = typedArray.getColor(R.styleable.CountDownView_ringColor, Color.BLUE);
        mRingWidth = typedArray.getFloat(R.styleable.CountDownView_ringWidth, 10);
        mTextSize = typedArray.getFloat(R.styleable.CountDownView_progressTextSize, 30);
        mTextColor = typedArray.getColor(R.styleable.CountDownView_progressTextColor, Color.BLACK);
        mCountdownTime = typedArray.getInteger(R.styleable.CountDownView_countdownTime, 5);
        typedArray.recycle();

        this.setWillNotDraw(false);//重写OnDraw 调用自定义布局
    }

    public void init() {
        Log.d(TAG, "init: ");

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//位图抗锯齿
        mArcPaint.setAntiAlias(true);//抗锯齿
        mArcPaint.setStyle(Paint.Style.STROKE); //空心
        startAngle = -90;
        sweepAngle = 360;

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setAntiAlias(true);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);//文本居中
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout: ");
        /**
         * 保证控件为宽高相等的矩形
         */
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (width < height) {
            height = width;
        } else {
            width = height;
        }
        //绘制位图占宽高的2/3
        mWidth = 2 * width / 3;
        mHeight = 2 * height / 3;

        //位图处于中间，左边距离View的左边1/6，顶部距离1/6，不设置默认位于左上角
        float x = getMeasuredWidth() / 6;
        float y = getMeasuredHeight() / 6;


        mRectF = new RectF(x + mRingWidth / 3, y + mRingWidth / 3, x + mWidth - mRingWidth / 3, y + mHeight - mRingWidth / 3);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.d(TAG, "onDraw: ");
        //绘制圆环
        mArcPaint.setColor(mRingColor);//颜色
        mArcPaint.setStrokeWidth(mRingWidth); //宽度
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, mArcPaint);//startAngle:起始角度，sweepAngle:扫过的角度

        //绘制圆形
        mCirclePaint.setColor(Color.GRAY);//颜色
        mCirclePaint.setAlpha(180);//透明度
        canvas.drawCircle(mRectF.centerX(), mRectF.centerY(), (mWidth / 2 - mRingWidth), mCirclePaint);

        //绘制文本
        String text = mCountdownTime - (int) (mCurrentProgress / 360f * mCountdownTime) + "S";
//        String text = "跳过";
        mTextPaint.setTextSize(mTextSize);//字体大小
        mTextPaint.setColor(mTextColor);//字体颜色
        //文字居中显示
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        int baseline = (int) ((mRectF.bottom + mRectF.top - fontMetrics.bottom - fontMetrics.top) / 2);
        canvas.drawText(text, mRectF.centerX(), baseline, mTextPaint);

    }

    private ValueAnimator valueAnimator;

    /**
     * 开始倒计时
     */
    public void startCountDown() {
        Log.d(TAG, "startCountDown: ");
        setClickable(false);
        valueAnimator = getValA(mCountdownTime * 1000);
        valueAnimator.start();
    }

    private ValueAnimator getValA(long countdownTime) {
        Log.d(TAG, "getValA: ");
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 100);
        valueAnimator.setDuration(countdownTime);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(0);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float i = Float.valueOf(String.valueOf(animation.getAnimatedValue()));
                mCurrentProgress = (int) (360 * (i / 100f));

                switch (directionType) {
                    case CW:
                        sweepAngle = mCurrentProgress - 360;
                        break;
                    case CCW:
                        sweepAngle = 360 - mCurrentProgress;
                        break;
                    default:
                        break;
                }

//                Log.e(TAG, "onAnimationUpdate: " + sweepAngle);
                invalidate();//触发绘制刷新
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //倒计时结束回调
                if (mListener != null) {
                    mListener.countDownFinished();
                }
                setClickable(true);
            }

        });

        return valueAnimator;
    }


    public void Reset() {
        valueAnimator.start();
    }

    public void setDirectionType(DirectionType type) {
        directionType = type;
    }

    public void setCountdownTime(int mCountdownTime) {
        this.mCountdownTime = mCountdownTime;
    }

    //倒计时监听，可在countDownFinished（）方法中进行倒计时结束后的逻辑
    public void setAddCountDownListener(OnCountDownFinishListener mListener) {
        this.mListener = mListener;
    }

    public interface OnCountDownFinishListener {
        void countDownFinished();
    }
}

