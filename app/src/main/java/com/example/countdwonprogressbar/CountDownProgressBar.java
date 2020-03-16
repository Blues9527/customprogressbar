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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;


/**
 * User : Blues
 * Date : 2019/6/27
 * Time : 17:09
 */

public class CountDownProgressBar extends View implements View.OnClickListener {


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
    private int textColor;

    //是否显示倒计时
    private boolean showCountText;

    //倒计时字体大小
    private float textSize;

    //progressbar宽度
    private float strokeWidth;

    //倒计时
    private String countDownText;

    //形状,默认圆形
    private int progressShape;

    //计时器，用于更新文本
    private CountDownTimer countDownTimer;

    private CountDownTimerCallBack mTimerCallBack;

    private SkipListener mSkipListener;

    private String drawText;

    private static final String skipText = "跳过";

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

        if (TextUtils.isEmpty(countDownText))
            throw new NullPointerException("countDownText not null");

        startCountDown(Integer.parseInt(countDownText) * 1000, INTERVAL_DEFAULT);

        setOnClickListener(this);
    }

    private void initAttrs(Context context, AttributeSet attrs) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CountDownProgressBar);

        //是否显示倒数文字
        showCountText = ta.getBoolean(R.styleable.CountDownProgressBar_showCountText, true);

        //倒计时
        countDownText = ta.getString(R.styleable.CountDownProgressBar_countDownTime);

        //倒数字体默认15px
        textSize = ta.getDimension(R.styleable.CountDownProgressBar_textSize, px2sp(15));

        //倒计时颜色，默认黑色
        textColor = ta.getColor(R.styleable.CountDownProgressBar_textColor, 0xff000000);

        //进度条宽度默认 5px
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

        //开始绘制文字
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(textColor);
        paint.setTextSize(px2sp(textSize));
        Rect rect = new Rect();
        drawText = showCountText && !TextUtils.equals(countDownText, skipText) ? String.valueOf(Integer.parseInt(countDownText) + 1) : skipText;

        paint.getTextBounds(drawText, 0, drawText.length(), rect);
        float textWidth = rect.width();
        float textHeight = rect.height();
        if (textWidth >= radius * 2) {
            textWidth = radius * 2;
        }
        //绘制倒计时
        canvas.drawText(drawText, centerX - textWidth / 2, centerY + textHeight / 2, paint);

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
    private void setCountDownText(String countDownText) {
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

    @Override
    public void onClick(View view) {
        //等到文字为跳过时再响应监听
        if (TextUtils.equals(drawText, skipText) && mSkipListener != null) {
            mSkipListener.onSkip();
            //跳过了，取消倒计时了
            countDownCancel();
        }
    }

    /**
     * 倒计时
     */
    private class CountDownTimerImpl extends CountDownTimer {

        private CountDownTimerImpl(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            setCountDownText(String.valueOf((int) (millisUntilFinished / 1000)));
//            setProgress((float) (millisUntilFinished - 1000) / millisInFuture * 100);
        }

        @Override
        public void onFinish() {
            //更新倒计时文本为:跳过
            if (!TextUtils.equals(drawText, skipText)) {
                setCountDownText(skipText);
            }
            if (mTimerCallBack != null)
                mTimerCallBack.onFinish();
        }
    }

    /**
     * 设置倒计时结束监听
     *
     * @param callBack
     */
    public void setCountDownTimerCallBack(CountDownTimerCallBack callBack) {
        mTimerCallBack = callBack;
    }

    /**
     * 设置跳过监听
     *
     * @param listener
     */
    public void setSkipListener(SkipListener listener) {
        mSkipListener = listener;
    }

    public interface CountDownTimerCallBack {
        void onFinish();
    }

    public interface SkipListener {
        void onSkip();
    }
    // --------------工具方法-------------------//

    /**
     * dp转px
     *
     * @param dp
     * @return
     */
    int dp2px(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    /**
     * px 转 sp
     *
     * @param px
     * @return
     */
    int px2sp(float px) {
        return (int) (getResources().getDisplayMetrics().scaledDensity * px);
    }
}