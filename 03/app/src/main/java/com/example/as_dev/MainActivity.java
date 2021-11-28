package com.example.as_dev;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{
    ListView lv_main;

    //名字
    String[] names = {"Lion", "Tiger", "Monkey", "Dog", "Cat", "Elephant"};

    //图片
    int[] pics =
            {R.drawable.lion,
             R.drawable.tiger,
             R.drawable.monkey,
             R.drawable.dog,
             R.drawable.cat,
             R.drawable.elephant};

    HashMap<View, Boolean> vis;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //初始化
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vis = new HashMap<>();

        //查找组件
        lv_main = findViewById(R.id.lv_main);

        //填充数据
        ArrayList<HashMap<String, Object>> lst = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < 6; i++)
        {
            HashMap<String, Object> mp = new HashMap<>();
            mp.put("name", names[i]);
            mp.put("pic", pics[i]);
            lst.add(mp);
        }

        //设置填充
        SimpleAdapter sa = new SimpleAdapter(this, lst, R.layout.list_unit, new String[]{"name", "pic"}, new int[]{R.id.tv_name2, R.id.iv_pic2});
        lv_main.setAdapter(sa);

    }

    //选中按钮
    public void click_select(View V)
    {
        LinearLayout ll = V.findViewById(R.id.ll);
        TextView tv_name = V.findViewById(R.id.tv_name2);
        if (vis.get(V) == null || !vis.get(V))
        {
            ll.setBackgroundColor(Color.RED);
            vis.put(V, true);
        }
        else
        {
            ll.setBackgroundColor(Color.WHITE);
            vis.put(V, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(View.inflate(this, R.layout.alert, null));
            builder.show();
        }
        Toast toast = Toast.makeText(MainActivity.this, tv_name.getText(), Toast.LENGTH_SHORT);
        toast.show();
    }

    //点击进入下一页
    public void click_to_next(View V)
    {
        Intent next = new Intent();
        next.setClass(this, Activity2.class);
        startActivity(next);
    }
}