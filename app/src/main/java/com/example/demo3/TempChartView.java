package com.example.demo3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class TempChartView extends View {
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint nightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<ForecastItem> items = new ArrayList<>();
    private int selectedIndex = -1;

    public TempChartView(Context ctx) { super(ctx); init(); }
    public TempChartView(Context ctx, AttributeSet attrs) { super(ctx, attrs); init(); }
    public TempChartView(Context ctx, AttributeSet attrs, int defStyle) { super(ctx, attrs, defStyle); init(); }

    private void init() {
        axisPaint.setColor(0x88FFFFFF);
        axisPaint.setStrokeWidth(2f);
        dayPaint.setColor(0xFFFF6A00);
        dayPaint.setStrokeWidth(5f);
        nightPaint.setColor(0xFF00A0FF);
        nightPaint.setStrokeWidth(5f);
        textPaint.setColor(0xFFFFFFFF);
        textPaint.setTextSize(28f);
        gridPaint.setColor(0x33FFFFFF);
        gridPaint.setStrokeWidth(2f);
    }

    public void setItems(List<ForecastItem> list) {
        items = list != null ? list : new ArrayList<>();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        float left = 40f;
        float right = w - 20f;
        float top = 20f;
        float bottom = h - 30f;

        canvas.drawLine(left, bottom, right, bottom, axisPaint);
        canvas.drawLine(left, top, left, bottom, axisPaint);
        int grid = 4;
        for (int i = 1; i <= grid; i++) {
            float gy = top + (bottom - top) * i / (grid + 1f);
            canvas.drawLine(left, gy, right, gy, gridPaint);
        }

        if (items.isEmpty()) return;

        int n = items.size();
        float step = (right - left) / Math.max(1, n - 1);

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (ForecastItem it : items) {
            try {
                int d = Integer.parseInt(it.dayTemp);
                int ntemp = Integer.parseInt(it.nightTemp);
                min = Math.min(min, Math.min(d, ntemp));
                max = Math.max(max, Math.max(d, ntemp));
            } catch (Exception ignored) {}
        }
        if (min == Integer.MAX_VALUE || max == Integer.MIN_VALUE) return;
        if (max == min) { max += 1; }

        android.graphics.Path dayPath = new android.graphics.Path();
        android.graphics.Path nightPath = new android.graphics.Path();
        float prevX = 0, prevYd = 0, prevYn = 0;
        for (int i = 0; i < n; i++) {
            ForecastItem it = items.get(i);
            float x = left + step * i;
            try {
                int d = Integer.parseInt(it.dayTemp);
                int nt = Integer.parseInt(it.nightTemp);
                float yDay = bottom - (d - min) * (bottom - top) / (float)(max - min);
                float yNight = bottom - (nt - min) * (bottom - top) / (float)(max - min);
                if (i == 0) {
                    dayPath.moveTo(x, yDay);
                    nightPath.moveTo(x, yNight);
                } else {
                    float cx = (prevX + x) / 2f;
                    dayPath.quadTo(prevX, prevYd, cx, (prevYd + yDay) / 2f);
                    nightPath.quadTo(prevX, prevYn, cx, (prevYn + yNight) / 2f);
                    dayPath.lineTo(x, yDay);
                    nightPath.lineTo(x, yNight);
                }
                prevX = x; prevYd = yDay; prevYn = yNight;
                canvas.drawCircle(x, yDay, 5f, dayPaint);
                canvas.drawCircle(x, yNight, 5f, nightPaint);
                canvas.drawText(String.valueOf(d), x - 16f, yDay - 10f, textPaint);
                canvas.drawText(String.valueOf(nt), x - 16f, yNight - 10f, textPaint);
            } catch (Exception ignored) {}
        }
        canvas.drawPath(dayPath, dayPaint);
        canvas.drawPath(nightPath, nightPaint);

        if (selectedIndex >= 0 && selectedIndex < n) {
            ForecastItem it = items.get(selectedIndex);
            try {
                int d = Integer.parseInt(it.dayTemp);
                int nt = Integer.parseInt(it.nightTemp);
                float x = left + step * selectedIndex;
                float yDay = bottom - (d - min) * (bottom - top) / (float)(max - min);
                float yNight = bottom - (nt - min) * (bottom - top) / (float)(max - min);
                canvas.drawCircle(x, yDay, 8f, dayPaint);
                canvas.drawCircle(x, yNight, 8f, nightPaint);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        if (items.isEmpty()) return false;
        if (event.getAction() == android.view.MotionEvent.ACTION_DOWN || event.getAction() == android.view.MotionEvent.ACTION_MOVE) {
            float w = getWidth();
            float left = 40f;
            float right = w - 20f;
            int n = items.size();
            float step = (right - left) / Math.max(1, n - 1);
            float x = event.getX();
            int idx = Math.round((x - left) / step);
            idx = Math.max(0, Math.min(n - 1, idx));
            selectedIndex = idx;
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }
}
