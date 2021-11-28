package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

//主页
public class MainActivity extends AppCompatActivity
{
    //获取本页上下文
    Context context;

    //主页列表
    protected ListView lv_index;
    protected FloatingActionButton fab_add;
    protected SearchView sv_index;

    //分类菜单
    protected MenuItem mi_tag;

    //SQLite数据库
    protected SQLiteDatabase db;

    //ActionMode
    protected ActionMode am = null;
    protected HashMap<View, Boolean> vis;
    protected int selected_items;

    //权限申请
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    //申请权限
    public static void verifyStoragePermissions(Activity activity)
    {
        try
        {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED)
            {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //创建时
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //权限验证
        verifyStoragePermissions(this);

        //初始化参数
        context = this;
        lv_index = findViewById(R.id.lv_index);
        fab_add = findViewById(R.id.fab_add);

        //初始化列表
        initList(null);

        //添加按钮点击事件
        fab_add.setOnClickListener(view -> addNote());

    }

    //返回时
    @Override
    protected void onResume()
    {
        super.onResume();
        if (mi_tag != null)
            mi_tag.setTitle("分类：全部");
        initList(null);
    }

    //初始化列表
    protected void initList(String keyword)
    {
        //清除列表
        lv_index.setAdapter(null);

        //载入数据库
        loadDB();

        //生成表
        String create_table = "create table if not exists notepad(" +
                              "id integer primary key," +
                              "note_title text," +
                              "note_text text," +
                              "note_tag text default '默认'," +
                              "note_time datetime," +
                              "background_color integer)";
        db.execSQL(create_table);


        //构造组件映射列表
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        //查找并装配
        String sql = "select * from notepad;";

        //重载关键字
        if (keyword != null)
            sql = "select * from notepad where note_title like '%" + keyword + "%'" +
                  " or note_text like '%" + keyword + "%'" +
                  " or note_tag like '%" + keyword + "%';";

        Cursor result = db.rawQuery(sql, null);
        while (result.moveToNext())
        {
            //获取参数列
            int idColumn = result.getColumnIndex("id");
            int titleColumn = result.getColumnIndex("note_title");
            int textColumn = result.getColumnIndex("note_text");
            int timeColumn = result.getColumnIndex("note_time");
            int tagColumn = result.getColumnIndex("note_tag");

            //设置映射
            HashMap<String, Object> mp = new HashMap<String, Object>();
            mp.put("tv_id", result.getInt(idColumn));
            mp.put("iv_icon", R.drawable.icon_notepad);

            //过滤标题
            String title = result.getString(titleColumn);
            if (title.length() > 8)
                title = title.substring(0, 8) + "...";

            mp.put("tv_title", title);

            //过滤文本
            String text = result.getString(textColumn);
            if (text.length() > 20)
                text = text.substring(0, 20) + "...";

            mp.put("tv_text", text);
            mp.put("tv_time", result.getString(timeColumn));
            mp.put("tv_tag", "分类：" + result.getString(tagColumn));

            list.add(mp);
        }

        result.close();
        closeDB();

        //设定装配器
        SimpleAdapter sa = new SimpleAdapter(this, list, R.layout.lv_index_unit,
                new String[]{"tv_id", "iv_icon", "tv_title", "tv_text", "tv_time", "tv_tag"},
                new int[]{R.id.tv_id, R.id.iv_icon, R.id.tv_title, R.id.tv_text, R.id.tv_time, R.id.tv_tag})
        {
            //View事件
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                //列表项
                View item = super.getView(position, convertView, parent);
                ImageView iv_remove = item.findViewById(R.id.iv_remove);

                //重写点击事件
                item.setOnClickListener(view ->
                {
                    //在多选状态下
                    if (am != null)
                    {
                        if (vis.get(view) != null && vis.get(view))
                        {
                            //已选中，取消
                            Log.i("Alert", "Cancel");
                            view.setBackgroundColor(Color.WHITE);
                            selected_items--;
                            vis.put(view, false);
                        }
                        else
                        {
                            //未选中，选择
                            Log.i("Alert", "Select");
                            view.setBackgroundColor(Color.CYAN);
                            selected_items++;
                            vis.put(view, true);
                        }
                        updateActionModeTitle();
                    }
                    else
                    {
                        //获取id
                        TextView tv_id = item.findViewById(R.id.tv_id);
                        String id = tv_id.getText().toString();

                        //进入对应界面
                        Intent intent = new Intent(context, ActivityNotepad.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    }
                });

                //重写长按事件
                item.setOnLongClickListener(view ->
                {
                    if (am == null)
                    {
                        //进入多选界面并选择
                        multiSelect();
                        vis.put(view, true);
                        view.setBackgroundColor(Color.CYAN);
                        selected_items++;
                        updateActionModeTitle();
                    }
                    return true;
                });

                //重写删除按钮
                iv_remove.setOnClickListener(view ->
                        {
                            //载入数据库
                            loadDB();

                            //获取id
                            TextView tv_id = item.findViewById(R.id.tv_id);
                            String id = tv_id.getText().toString();

                            //删除数据库
                            db.execSQL("delete from notepad where id=" + id);

                            //关闭数据库
                            closeDB();

                            //重现载入列表
                            initList(null);
                        }
                );

                return item;
            }
        };

        //装配
        lv_index.setAdapter(sa);
    }

    //创建菜单时
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //设置菜单
        getMenuInflater().inflate(R.menu.menu_index, menu);
        sv_index = (SearchView) menu.findItem(R.id.sv_index).getActionView();
        mi_tag = menu.findItem(R.id.mi_tag);
        mi_tag.setTitle("分类：全部");

        //设置搜索框编辑行为
        sv_index.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                initList(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                initList(s);
                return true;
            }
        });
        return true;
    }

    //菜单项选择
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //切换选项
        switch (item.getItemId())
        {
            case R.id.mi_tag_all:
                initList("");
                mi_tag.setTitle("分类：全部");
                break;
            case R.id.mi_tag_default:
                initList("默认");
                mi_tag.setTitle("分类：默认");
                break;
            case R.id.mi_tag_life:
                initList("生活");
                mi_tag.setTitle("分类：生活");
                break;
            case R.id.mi_tag_note:
                initList("笔记");
                mi_tag.setTitle("分类：笔记");
                break;

            case R.id.mi_add://新建按钮
                addNote();
                break;
            case R.id.mi_multiselect://多选按钮
                if (am == null)
                    am = startActionMode(callback);
                break;
            case R.id.mi_exit://退出按钮
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    //ActionMode回调
    ActionMode.Callback callback = new ActionMode.Callback()
    {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
        {
            //设成多选标题栏
            getMenuInflater().inflate(R.menu.menu_multiselect, menu);

            //初始化参数
            vis = new HashMap<>();
            selected_items = 0;

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
        {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
        {
            //删除按钮
            if (menuItem.getItemId() == R.id.mi_delete)
            {
                //载入数据库
                loadDB();

                //遍历已选择的项目
                for (View v : vis.keySet())
                {
                    if (vis.get(v))
                    {
                        //获取id
                        TextView tv_id = v.findViewById(R.id.tv_id);
                        String id = tv_id.getText().toString();
                        db.execSQL("delete from notepad where id=" + id);
                        vis.remove(v);
                    }

                }

                //关闭数据库
                closeDB();

                //载入列表
                initList(null);
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode)
        {
            am = null;
            //还原背景色
            for (View v : vis.keySet())
            {
                v.setBackgroundColor(Color.WHITE);
            }
            vis.clear();
            selected_items = 0;
        }
    };

    //多选事件
    public void multiSelect()
    {
        if (am == null)
            am = startActionMode(callback);
    }

    //设置标题
    public void updateActionModeTitle()
    {
        if (am != null)
        {
            am.setTitle("已选择 " + selected_items + " 项");
        }
    }

    //载入数据库
    public void loadDB()
    {
        db = SQLiteDatabase.openOrCreateDatabase(getFilesDir().toString() + "/notepad.db", null);
    }

    //关闭数据库
    public void closeDB()
    {
        db.close();
    }

    //新建笔记
    public void addNote()
    {
        Intent intent = new Intent(this, ActivityNotepad.class);
        startActivity(intent);
    }

}