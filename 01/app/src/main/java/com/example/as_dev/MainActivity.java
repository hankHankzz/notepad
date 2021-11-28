package com.example.as_dev;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;
import android.view.View;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity
{
    //颜色列表
    int colorIndex = 0;
    int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.argb(0, 0, 0, 0)};

    //组件
    SeekBar sb_time;//滚动条
    TextView tv_background;//背景
    Button btn_main;//主按钮
    Button btn_left;//左按钮
    Button btn_right;//右按钮

    //定时器
    Timer timer;//定时器
    TimerTask task;//定时器任务
    int delay = 1000;//延迟
    boolean start = false;//启动

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //初始化
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化变量
        tv_background = findViewById(R.id.tv_background);
        btn_main = findViewById(R.id.btn_main);
        btn_left = findViewById(R.id.btn_red);
        btn_right = findViewById(R.id.btn_blue);
        sb_time = findViewById(R.id.sb_time);

        //初始化按钮外观
        btn_left.setTextColor(Color.RED);
        btn_right.setTextColor(Color.BLUE);

        //添加滑动事件
        sb_time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                //计算闪光评率
                delay = Math.max(1, 1001 - i * 10);
                Log.i("delay:", String.valueOf(delay));
            }

            //开始触摸时
            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            //松开触摸时
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                try
                {
                    //重设定时器
                    initTimer();
                    Log.i("", "initComplete");
                }
                catch (Exception e)
                {
                    Log.i("err", e.toString());
                }
            }
        });

        //主按钮点击
        btn_main.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //交换开关状态
                if (timer != null)
                    start = !start;
            }
        });

        //左右按钮点击
        View.OnClickListener btn_LR_click = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int leftColor = btn_left.getTextColors().getDefaultColor();
                btn_left.setTextColor(btn_right.getTextColors().getDefaultColor());
                btn_right.setTextColor(leftColor);
            }
        };
        btn_left.setOnClickListener(btn_LR_click);
        btn_right.setOnClickListener(btn_LR_click);
    }

    //设定定时器
    protected void initTimer()
    {
        //取消当前定时器
        if (timer != null)
            timer.cancel();

        //创建新的定时器和任务
        timer = new Timer();
        task = new TimerTask()
        {
            @Override
            public void run()
            {
                //主线程更新UI
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        timerTick();
                    }
                });
            }
        };
        timer.schedule(task, 0, delay);
    }

    //定时器执行的任务
    protected void timerTick()
    {
        if (start)
        {
            //交替闪光
            tv_background.setBackgroundColor(colors[colorIndex %= 3]);
            colorIndex++;
            Log.i("ColorIndex", String.valueOf(colorIndex));
        }
        else if (colorIndex != 3)
        {
            //隐藏闪光背景
            tv_background.setBackgroundColor(colors[3]);
            colorIndex = 3;
        }
    }

}