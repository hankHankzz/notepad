package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PaletteActivity extends AppCompatActivity
{
    //控件列表
    SeekBar sb_red;
    SeekBar sb_green;
    SeekBar sb_blue;
    TextView tv_color;
    TextView tv_return;
    TextView tv_submit;

    //数据库
    SQLiteDatabase db;

    //颜色组件
    int red = 0;
    int green = 0;
    int blue = 0;

    //笔记id
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);

        //获取id
        id = getIntent().getIntExtra("id", 0);
        int color = getIntent().getIntExtra("background_color", 0);

        //分离颜色
        red = (color & 0xff0000) >> 16;
        green = (color & 0x00ff00) >> 8;
        blue = (color & 0x0000ff);

        //初始化控件
        sb_red = findViewById(R.id.sb_red);
        sb_green = findViewById(R.id.sb_green);
        sb_blue = findViewById(R.id.sb_blue);
        tv_color = findViewById(R.id.tv_color);
        tv_return = findViewById(R.id.tv_return);
        tv_submit = findViewById(R.id.tv_submit);
        tv_color.setBackgroundColor(Color.rgb(red, green, blue));

        //设置进度条
        sb_red.setProgress(red);
        sb_green.setProgress(green);
        sb_blue.setProgress(blue);

        //设置拖动监听器
        sb_red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                red = i;
                tv_color.setBackgroundColor(Color.rgb(red, green, blue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        sb_green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                green = i;
                tv_color.setBackgroundColor(Color.rgb(red, green, blue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        sb_blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                blue = i;
                tv_color.setBackgroundColor(Color.rgb(red, green, blue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        //设置按钮点击监听器

        //返回按钮
        tv_return.setOnClickListener(view ->
        {
            finish();
        });

        //提交按钮
        tv_submit.setOnClickListener(view ->
        {
            //存入数据库
            db = SQLiteDatabase.openOrCreateDatabase(getFilesDir().toString() + "/notepad.db", null);

            ContentValues cv = new ContentValues();
            cv.put("background_color", Color.rgb(red, green, blue));
            db.update("notepad", cv, "id=" + id, null);

            db.close();

            finish();
        });

    }


}