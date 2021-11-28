package com.example.as_dev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class Activity3 extends AppCompatActivity
{

    String[] names = {"One", "Two", "Three", "Four"};
    HashMap<View, Boolean> vis;
    int selected_items = 0;
    ActionMode am;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        vis = new HashMap<>();

        ListView lv_3 = findViewById(R.id.lv_3);
        ArrayList<HashMap<String, Object>> lst = new ArrayList<>();
        for (int i = 0; i <= 3; i++)
        {
            HashMap<String, Object> mp = new HashMap<>();
            mp.put("name", names[i]);
            mp.put("pic", R.drawable.cat);
            lst.add(mp);
        }

        SimpleAdapter sa = new SimpleAdapter(this, lst, R.layout.list_unit2, new String[]{"name", "pic"}, new int[]{R.id.tv_name2, R.id.iv_pic2});
        lv_3.setAdapter(sa);
    }

    public void click_select(View V)
    {
        if (am == null)
            am = startActionMode(callback);

        LinearLayout ll = (LinearLayout) V;
        if (vis.get(V) == null || !vis.get(V))
        {
            ll.setBackgroundColor(Color.CYAN);
            vis.put(V, true);
            selected_items++;
        }
        else
        {
            ll.setBackgroundColor(Color.WHITE);
            vis.put(V, false);
            selected_items--;
        }
        callback.onActionItemClicked(am, null);

    }

    ActionMode.Callback callback = new ActionMode.Callback()
    {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
        {
            getMenuInflater().inflate(R.menu.menu_2, menu);
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
            actionMode.setTitle(selected_items + " selected");
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode)
        {

        }
    };

    //点击进入下一页
    public void click_to_next(View V)
    {
        Intent next = new Intent();
        next.setClass(this, MainActivity.class);
        startActivity(next);
    }
}