package com.example.countdwonprogressbar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * User : Blues
 * Date : 2019/6/27
 * Time : 17:09
 */

public class CountDownProgressBar extends View {

    //view的中心点X坐标
    private int centerX;

    //view中心点的Y坐标
    private int centerY;

    private int max = 100;

    //表示进度，值介于0~100之间
    private float progress = 0;

    private int radius = dp2px(15);

    //画笔
    private Paint mPaint;

    //进度颜色
    private int progressColor;

    //进度背景颜色
    private int progressBgColor;

    //倒计时颜色
    private int countDownTextColor;

    //是否显示倒计时
    private boolean showCountText;

    //倒计时字体大小
    private float countDownTextSize;

    //progressbar宽度
    private float strokeWidth;

    //倒计时
    private int countDownText;

    //形状,默认圆形
    private int progressShape;

    //计时器，用于更新文本
    private CountDownTimer countDownTimer;

    private CountDownTimerCallBack mCallBack;

    //倒计时间隔，默认1s
    private static final long INTERVAL_DEFAULT = 1000;

    public CountDownProgressBar(Context context) {
        this(context, null);
    }

    public CountDownProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        //抗锯齿
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        startCountDown(countDownText * 1000, INTERVAL_DEFAULT);
    }

    private void initAttrs(Context context, AttributeSet attrs) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CountDownProgressBar);

        //是否显示倒数文字
        showCountText = ta.getBoolean(R.styleable.CountDownProgressBar_showCountText, true);

        //倒计时
        countDownText = ta.getInt(R.styleable.CountDownProgressBar_countDownTime, 6);

        //倒数字体默认20sp
        countDownTextSize = ta.getDimension(R.styleable.CountDownProgressBar_countDownTextSize, 20);

        //倒计时颜色，默认黑色
        countDownTextColor = ta.getColor(R.styleable.CountDownProgressBar_countDownTextColor, 0xff000000);

        //进度条宽度默认 5dp
        strokeWidth = ta.getFloat(R.styleable.CountDownProgressBar_strokeWidth, 5);

        //进度条进度默认蓝色
        progressColor = ta.getColor(R.styleable.CountDownProgressBar_progressColor, 0xff4b749d);

        //背景默认灰色
        progressBgColor = ta.getColor(R.styleable.CountDownProgressBar_progressBgColor, 0xffededed);

        //默认圆形
        progressShape = ta.getInt(R.styleable.CountDownProgressBar_mode, 1);

        ta.recycle();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        centerX = getWidth() / 2;
        centerY = getHeight() / 2;

        radius = centerX - dp2px(strokeWidth) / 2;

        if (progressShape == 0) {
            drawLinearProgressBar(canvas, mPaint);
        } else {
            drawRoundProgressBar(canvas, mPaint);
        }
    }

    /**
     * 绘制圆形progressbar
     *
     * @param canvas
     * @param paint
     */
    private void drawRoundProgressBar(Canvas canvas, Paint paint) {
        paint.setColor(progressBgColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp2px(strokeWidth));

        canvas.drawCircle(centerX, centerY, radius, paint);

        paint.setColor(progressColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp2px(strokeWidth));
        RectF oval = new RectF(centerX - radius, centerY - radius, radius + centerX, radius + centerY);
        canvas.drawArc(oval, -90, getAngle(), false, paint);

        if (showCountText) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(countDownTextColor);
            paint.setTextSize(px2sp(countDownTextSize));

            Rect rect = new Rect();

            paint.getTextBounds(String.valueOf(countDownText), 0, String.valueOf(countDownText).length(), rect);
            float textWidth = rect.width();
            float textHeight = rect.height();
            if (textWidth >= radius * 2) {
                textWidth = radius * 2;
            }

            canvas.drawText(String.valueOf(countDownText), centerX - textWidth / 2, centerY + textHeight / 2, paint);
        }
    }

    /**
     * 绘制线型的progressbar
     *
     * @param canvas
     * @param paint
     */
    private void drawLinearProgressBar(Canvas canvas, Paint paint) {

        paint.setColor(progressBgColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(dp2px(strokeWidth));

        canvas.drawRoundRect(new RectF(0, 0, getWidth(), dp2px(strokeWidth)), 0, 0, paint);

        paint.setColor(progressColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(dp2px(strokeWidth));

        canvas.drawRoundRect(new RectF(0, 0, getProgress() / 100 * getWidth(), dp2px(strokeWidth)), 0, 0, paint);


    }

    /**
     * 手动设置倒计时text
     *
     * @param countDownText
     */
    private void setCountDownText(int countDownText) {
        this.countDownText = countDownText;
        postInvalidate();
    }

    /**
     * 设置进度，用于更新progressbar进度
     *
     * @param progress
     */
    private void setProgress(float progress) {
        if (progress > 100)
            this.progress = max;
        else {
            this.progress = progress;
            postInvalidate();
        }
    }

    /**
     * 获取当前进度，百分比
     *
     * @return 百分比
     */
    public float getProgress() {
        return progress;
    }

    /**
     * 获取当前进度对应圆形的角度
     *
     * @return 度数
     */
    public float getAngle() {
        float percent = 360 * (max - progress) / max;
        if (percent > 360)
            percent = 360;
        percent = 360 - percent;
        return percent;
    }

    /**
     * 开启倒计时,圆形倒计时progressbar需要用到
     */
    public void startCountDown(long millisInFuture, long countDownInterval) {
        countDownTimer = new CountDownTimerImpl(millisInFuture, countDownInterval);
        countDownTimer.start();
        //使用属性动画过度progress的更新，会更加圆滑
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "progress", 100.0f, 0.0f);
        animator.setDuration(millisInFuture);
        animator.start();
    }

    /**
     * onResume时候开启倒计时，主要用于界面生命周期变化恢复倒计时
     */
    public void countDownResume() {
        if (countDownTimer != null) {
            countDownTimer.start();
        }
    }

    /**
     * onStop或者是onPause的时候取消倒计时，主要用于界面生命周期变化取消倒计时
     */
    public void countDownCancel() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private class CountDownTimerImpl extends CountDownTimer {

        private CountDownTimerImpl(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            setCountDownText((int) (millisUntilFinished / 1000));
//            setProgress((float) (millisUntilFinished - 1000) / millisInFuture * 100);
        }

        @Override
        public void onFinish() {
            if (getCountDownTimerCallBack() != null)
                getCountDownTimerCallBack().onFinish();
        }
    }

    public interface CountDownTimerCallBack {
        void onFinish();
    }

    public void setCountDownTimerCallBack(CountDownTimerCallBack callBack) {
        mCallBack = callBack;
    }

    public CountDownTimerCallBack getCountDownTimerCallBack() {
        return mCallBack;
    }

    // --------------工具方法-------------------//

    int dp2px(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    int px2sp(float px) {
        return (int) (getResources().getDisplayMetrics().scaledDensity * px);
    }
}
