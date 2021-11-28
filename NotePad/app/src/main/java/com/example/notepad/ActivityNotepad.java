package com.example.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

//主页
public class ActivityNotepad extends AppCompatActivity
{
    //组件
    EditText et_title;
    EditText et_text;
    MenuItem mi_tag;
    ScrollView sv;

    //SQLite数据库
    SQLiteDatabase db;

    //笔记id
    int id;
    String tag="默认";

    //创建时
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);

        //初始化参数
        et_title = findViewById(R.id.tv_title);
        et_text = findViewById(R.id.et_text);
        sv = findViewById(R.id.sv);
        mi_tag = findViewById(R.id.mi_tag);

        //载入数据库
        db = SQLiteDatabase.openOrCreateDatabase(getFilesDir().toString() + "/notepad.db", null);

        //如果有数据
        if (getIntent().hasExtra("id"))
        {
            //获取id
            id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id")));

            Cursor rows = db.rawQuery("select * from notepad where id = " + id, null);
            if (rows.moveToNext())
            {
                //取得参数
                int titleColumn = rows.getColumnIndex("note_title");
                int textColumn = rows.getColumnIndex("note_text");
                int colorColumn = rows.getColumnIndex("background_color");
                int tagColumn = rows.getColumnIndex("note_tag");

                //还原参数
                et_title.setText(rows.getString(titleColumn));
                et_text.setText(rows.getString(textColumn));
                sv.setBackgroundColor(rows.getInt(colorColumn));
                tag=rows.getString(tagColumn);

            }
            rows.close();
        }
        else
        {

            //获取数据库条目
            Cursor rows = db.rawQuery("select * from notepad", null);
            id = rows.getCount();
            rows.close();

            //构造键值对
            ContentValues cv = new ContentValues();

            //获取时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            Date date = new Date(System.currentTimeMillis());

            //获取背景颜色
            ColorDrawable cd = (ColorDrawable) sv.getBackground();

            //构造数据
            cv.put("id", id);
            cv.put("note_title", et_title.getText().toString());
            cv.put("note_text", et_text.getText().toString());
            cv.put("note_time", sdf.format(date));
            cv.put("background_color", cd.getColor());
            cv.put("note_tag", "默认");

            //更新条目
            db.insert("notepad", null, cv);
        }

        //关闭数据库
        db.close();

        //设定焦点
        et_title.requestFocus();

        //临时解决安卓bug
        et_text.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                float add = et_text.getLineSpacingExtra();
                float mul = et_text.getLineSpacingMultiplier();
                et_text.setLineSpacing(0f, 1f);
                et_text.setLineSpacing(add, mul);
            }
        });
    }

    //创建菜单时
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_notepad, menu);

        mi_tag=menu.findItem(R.id.mi_tag);
        mi_tag.setTitle("分类："+tag);

        //遍历项目
        for (int i = 0; i < menu.size(); i++)
        {
            MenuItem item = menu.getItem(i);
            switch (item.getItemId())
            {
                //设置分类
                case R.id.mi_tag:
                    break;

                //调色板
                case R.id.mi_color_palette:
                    View v = item.getActionView();
                    v.setOnClickListener(view ->
                    {
                        hideKeyboard();
                        colorPalette();
                    });
                    break;

                //保存笔记
                case R.id.mi_save:
                    View v2 = item.getActionView();
                    v2.setOnClickListener(view ->
                    {
                        saveNote();
                    });
                    break;

                //返回
                case R.id.mi_return:
                    View v3 = item.getActionView();
                    v3.setOnClickListener(view ->
                    {
                        quit();
                    });
                    break;
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    //选择菜单项
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.mi_tag_default:
                tag="默认";
                mi_tag.setTitle("分类："+tag);
                break;
            case R.id.mi_tag_life:
                tag="生活";
                mi_tag.setTitle("分类："+tag);
                break;
            case R.id.mi_tag_note:
                mi_tag.setTitle("分类："+tag);
                tag="笔记";
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    //再次返回页面时
    @Override
    protected void onResume()
    {
        super.onResume();

        //载入数据库
        db = SQLiteDatabase.openOrCreateDatabase(getFilesDir().toString() + "/notepad.db", null);
        Cursor rows = db.rawQuery("select * from notepad where id = " + id, null);
        if (rows.moveToNext())
        {
            //还原颜色
            int colorColumn = rows.getColumnIndex("background_color");
            sv.setBackgroundColor(rows.getInt(colorColumn));
        }

        rows.close();
        db.close();
    }

    //保存笔记按钮
    public void saveNote()
    {
        try
        {
            //载入数据库
            db = SQLiteDatabase.openOrCreateDatabase(getFilesDir().toString() + "/notepad.db", null);
            ContentValues cv = new ContentValues();

            //获取时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            Date date = new Date(System.currentTimeMillis());

            //获取背景颜色
            ColorDrawable cd = (ColorDrawable) sv.getBackground();

            //构造数据
            cv.put("note_title", et_title.getText().toString());
            cv.put("note_text", et_text.getText().toString());
            cv.put("note_time", sdf.format(date));
            cv.put("note_tag",tag);
            cv.put("background_color", cd.getColor());


            //更新条目
            db.update("notepad", cv, "id=" + id, null);
            db.close();
        }
        catch (Exception e)
        {
            Log.i("Save note Failed", e.toString());
        }
        finish();
    }

    //退出界面
    public void quit()
    {

        if (!getIntent().hasExtra("id"))
        {
            db = SQLiteDatabase.openOrCreateDatabase(getFilesDir().toString() + "/notepad.db", null);

            //删除数据库
            db.execSQL("delete from notepad where id=" + id);

            db.close();
        }

        finish();
    }

    //切换背景颜色按钮
    public void colorPalette()
    {
        //获取背景颜色
        ColorDrawable cd = (ColorDrawable) sv.getBackground();

        //新建弹出框
        Intent intent = new Intent();
        intent.setClass(this, PaletteActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("background_color", cd.getColor());

        startActivity(intent);
    }

    // 隐藏软键盘
    public void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
}