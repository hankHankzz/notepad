package com.example.as_dev;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Activity2 extends AppCompatActivity
{
    TextView tv_test;


    //构造函数
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        //初始化变量
        tv_test = findViewById(R.id.tv_test);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.mi_small:
                smallFont();
                break;
            case R.id.mi_mid:
                midFont();
                break;
            case R.id.mi_big:
                bigFont();
                break;
            case R.id.mi_normal:
                toast();
                break;
            case R.id.mi_red:
                red();
                break;
            case R.id.mi_black:
                black();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //链接菜单
    @Override
    public boolean onCreateOptionsMenu(Menu m)
    {
        this.getMenuInflater().inflate(R.menu.menu_1, m);

        return true;
    }

    //小字体
    public void smallFont()
    {
        tv_test.setTextSize(10);
    }

    //中字体
    public void midFont()
    {
        tv_test.setTextSize(16);
    }

    //大字体
    public void bigFont()
    {
        tv_test.setTextSize(20);
    }

    //普通菜单项
    public void toast()
    {
        Toast toast = Toast.makeText(this, "这是普通菜单项", Toast.LENGTH_SHORT);
        toast.show();
    }

    //红色
    public void red()
    {
        tv_test.setTextColor(Color.RED);
    }

    //蓝色
    public void black()
    {
        tv_test.setTextColor(Color.BLACK);
    }

    //点击进入下一页
    public void click_to_next(View V)
    {
        Intent next = new Intent();
        next.setClass(this, Activity3.class);
        startActivity(next);
    }
}