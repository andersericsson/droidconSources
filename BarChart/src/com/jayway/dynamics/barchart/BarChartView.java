package com.jayway.dynamics.barchart;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

public class BarChartView extends View {

    private Paint paint = new Paint();
    private ArrayList<Dynamics> data;

    private Runnable barAnimator = new Runnable() {

        @Override
        public void run() {
            long now = AnimationUtils.currentAnimationTimeMillis();
            boolean needNewFrame = false;
            for (Dynamics dynamics : data) {
                dynamics.update(now);
                if (!dynamics.isAtRest()) {
                    needNewFrame = true;
                }
            }
            if (needNewFrame) {
                postDelayed(this, 16);
            }
            invalidate();
        }
    };

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
    }

    public void setData(ArrayList<Float> newData) {
        long now = AnimationUtils.currentAnimationTimeMillis();
        if (data == null || data.size() != newData.size()) {
            data = new ArrayList<Dynamics>();
            for (float value : newData) {
                Dynamics dynamics = new Dynamics(40, 0.8f);
                dynamics.setPosition(value, now);
                dynamics.setTargetPosition(value, now);
                data.add(dynamics);
            }
            invalidate();
        } else {
            for (int i = 0; i < data.size(); i++) {
                data.get(i).setTargetPosition(newData.get(i), now);
            }
        }

        removeCallbacks(barAnimator);
        post(barAnimator);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        float width = getWidth() - getPaddingLeft() - getPaddingRight();
        float height = getHeight() - getPaddingTop() - getPaddingBottom();

        drawBars(canvas, width, height, 40);
        drawAxes(canvas, width, height, 40);

        canvas.restore();
    }

    private void drawBars(Canvas canvas, float width, float height, float max) {
        paint.setColor(0xFF33B5E5);
        paint.setStyle(Style.FILL);
        int numberOfBars = data.size();
        float barDist = width / (numberOfBars);
        float barWidth = barDist * 0.8f;
        float barMargin = barDist - barWidth;
        for (int bar = 0; bar < numberOfBars; bar++) {
            float barValue = data.get(bar).getPosition();
            float barHeight = height * barValue / max;
            float barLeft = barMargin + bar * barDist;
            canvas.drawRect(barLeft, height - barHeight, barLeft + barWidth, height, paint);
        }
    }

    private void drawAxes(Canvas canvas, float width, float height, float max) {
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawLine(0, 0, 0, height, paint);
        canvas.drawLine(0, height, width, height, paint);

        drawVerticalTics(canvas, height, max);
    }

    private void drawVerticalTics(Canvas canvas, float height, float max) {
        for (int i = 1; i < max; i++) {
            float y = height - i * height / max - 2;
            boolean isEvenTen = i % 10 == 0;

            float xStart = isEvenTen ? -24 : 0;
            float xEnd = isEvenTen ? 10f : 5f;

            paint.setStyle(Style.STROKE);
            paint.setColor(isEvenTen ? Color.BLACK : Color.GRAY);
            canvas.drawLine(xStart, y, xEnd, y, paint);
            if (isEvenTen) {
                canvas.drawText("" + i, xStart, y, paint);
            }
        }
    }
}
