package com.example.kurstaskmanager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class SimpleChartView extends View {

    private int completed = 0;
    private int remaining = 0;

    private Paint completedPaint;
    private Paint remainingPaint;
    private Paint textPaint;

    public SimpleChartView(Context context) {
        super(context);
        init();
    }

    public SimpleChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        completedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        completedPaint.setColor(Color.parseColor("#4CAF50"));

        remainingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        remainingPaint.setColor(Color.parseColor("#F44336"));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(int completed, int remaining) {
        this.completed = completed;
        this.remaining = remaining;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        if (width <= 0 || height <= 0) return;

        int total = completed + remaining;
        if (total == 0) {
            Paint emptyPaint = new Paint();
            emptyPaint.setColor(Color.GRAY);
            canvas.drawRect(0, 0, width, height, emptyPaint);
            canvas.drawText("Нет данных", width / 2f, height / 2f, textPaint);
            return;
        }

        float completedWidth = (float) completed / total * width;

        canvas.drawRect(0, 0, completedWidth, height, completedPaint);
        canvas.drawRect(completedWidth, 0, width, height, remainingPaint);

        String text = completed + " / " + total;
        canvas.drawText(text, width / 2f, height / 2f + 10, textPaint);
    }
}