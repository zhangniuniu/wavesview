package com.zhangniuniu.bolangview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Author：zhangyong on 2017/7/26 10:24
 * Email：zhangyonglncn@gmail.com
 * Description：水波浪效果
 */

public class WaveView extends View {

    private static final String TAG = "WaveView";

    private Path mPath;
    private Paint mLinePaint;

    /**
     * 第一个波浪的起始位置
     */
    private float firstWaveX;

    //一屏显示波浪的个数
    private float waveNum = 1.2f;
    //已经展示的数量，当完全铺满屏幕后为需铺满屏幕总数量，就不需要再增长了
    private int showedNum = 0;

    //屏幕宽高
    private int viewWidth;
    private int viewHeight;

    //水平线  改动此处可以造成水位上涨的效果
    float centerY;

    //波浪宽度
    private int waveWidth;
    //波浪控制点高度
    private float waveControlY;

    private ValueAnimator mValueAnimatior;


    public WaveView(Context context) {
        super(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPath = new Path();
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.RED);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = getWidth();
        viewHeight = getHeight();
        //根据一屏显示的波浪个数 计算波浪的宽度
        waveWidth = (int) (viewWidth / waveNum);
        //将第一个波长的起点设置为-waveWidth
        firstWaveX = -waveWidth;
        waveControlY = viewHeight / 4;
        showedNum = (int) (Math.ceil(waveNum) + 1);
        centerY = viewHeight / 2;
    }

    /**
     * 只需要保证第一个波浪的起始位置小于屏幕的0坐标即可，如果大于等于0，左侧加入新的波浪
     * 总体的波浪数量为 Math.ceil(waveNum)+1 即可保证当波浪铺满屏幕后不会出现断开的情况
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {

        mPath.reset();


        mPath.moveTo(firstWaveX, centerY);
        for (int i = 0; i < showedNum; i++) {
            //上凸波浪
            mPath.quadTo(firstWaveX + waveWidth * (i + 0.25f), centerY - waveControlY, firstWaveX + waveWidth * (i + 0.5f), centerY);
            //下凸波浪
            mPath.quadTo(firstWaveX + waveWidth * (i + 0.75f), centerY + waveControlY, firstWaveX + waveWidth * (i + 1), centerY);
        }
        mPath.lineTo(viewWidth, viewHeight);
        mPath.lineTo(0, viewHeight);
        mPath.close();
        canvas.drawPath(mPath, mLinePaint);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void startAnim() {
        mValueAnimatior = ValueAnimator.ofInt(-waveWidth, 0);
        mValueAnimatior.setDuration(1000);
        mValueAnimatior.setInterpolator(new LinearInterpolator());
        mValueAnimatior.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimatior.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                firstWaveX = (int) animation.getAnimatedValue();
                Log.i(TAG, "onAnimationUpdate: " + firstWaveX + "---" + animation.getAnimatedValue() + "---" + waveWidth);
                invalidate();
            }
        });
        mValueAnimatior.start();
    }

}
