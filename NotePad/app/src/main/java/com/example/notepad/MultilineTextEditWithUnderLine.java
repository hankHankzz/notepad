package com.example.notepad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

public class MultilineTextEditWithUnderLine extends androidx.appcompat.widget.AppCompatEditText
{
    private Paint linePaint;
    private int paperColor;


    //构造函数
    public MultilineTextEditWithUnderLine(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        //初始化
        linePaint = new Paint();
        paperColor = Color.argb(0, 0, 0, 0);
    }

    //绘制事件
    protected void onDraw(Canvas paramCanvas)
    {
        //绘制背景
        paramCanvas.drawColor(this.paperColor);

        //行数
        int lines = getLineCount();

        //编辑框高
        int height = getHeight();

        //行高
        int lineHeight = getLineHeight();

        //约束行数
        int m = height / lineHeight;
        lines = Math.max(lines, m);

        //下划线空间
        int underlineSpace = (int) (lineHeight * (getLineSpacingMultiplier() - 1.2d));

        //顶部间距
        int paddingTop = getCompoundPaddingTop();
        paddingTop -= underlineSpace;
        int currentLine = 1;

        //绘图
        while (currentLine < lines)
        {
            paddingTop += lineHeight;
            paramCanvas.drawLine(0.0f, paddingTop, getRight(), paddingTop, this.linePaint);
            paramCanvas.save();
            currentLine++;
        }

        super.onDraw(paramCanvas);
        paramCanvas.restore();
    }

}
