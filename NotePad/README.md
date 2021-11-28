# Notepad - 安卓期中作业
### 总览

因为是自己从头实现的Notepad，从新构建了数据表已经增删改查代码。

基本要求实现：增删改查，时间戳，笔记查询（查询所有文本关键字）

拓展要求实现：UI美化，笔记背景更改，笔记分类及查询

## 详情

### SQLite数据库表格实现

<table>
  <tr>
    <th>列名</th>
    <th>类型</th>
    <th>描述</th>
  </tr>
  <tr>
    <td>id</td>
    <td>int primary key</td>
    <td>主键，标识文本号</td>
  </tr>
  <tr>
    <td>note_title</td>
    <td>text</td>
    <td>标题</td>
  </tr>
  <tr>
    <td>note_text</td>
    <td>text</td>
    <td>内容</td>
  </tr>
  <tr>
    <td>note_tag</td>
    <td>text</td>
    <td>分类</td>
  </tr>
  <tr>
    <td>note_time</td>
    <td>datetime</td>
    <td>时间戳</td>
  </tr>
  <tr>
    <td>background_color</td>
    <td>int</td>
    <td>背景色</td>
  </tr>
</table>
 
操作API(自封装：)
```Java

//主页
public class MainActivity extends AppCompatActivity
{
    //SQLite数据库
    protected SQLiteDatabase db;

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

    //生成表
    {
        String create_table = "create table if not exists notepad(" +
                              "id integer primary key," +
                              "note_title text," +
                              "note_text text," +
                              "note_tag text default '默认'," +
                              "note_time datetime," +
                              "background_color integer)";
        db.execSQL(create_table);
    }
}
```
  
### 主界面 

##### 基本功能：（查找与时间戳）

##### 拓展功能：（界面美化，笔记分类）

<img src='https://github.com/ZeroNinx/AS_Dev/blob/master/NotePad/screenshot/main.png' width='350px' />

主要是参考安卓5.0以后的风格做了界面美化，由于安卓5.0后更新的扁平化Api更加美观，同时加上系统的支持，一体性更加强。

主要代码（style.xml）：

```xml
    <resources>
        <!-- Base application theme. -->
        <style name="AppTheme" parent="Theme.AppCompat.DayNight.DarkActionBar">
            <!-- Customize your theme here. -->
            <item name="colorPrimary">#208ADF</item>
            <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
            <item name="colorAccent">@color/colorAccent</item>
            <item name="android:windowTranslucentStatus">true</item>
        </style>
    </resources>
```

<img src='https://github.com/ZeroNinx/AS_Dev/blob/master/NotePad/screenshot/multiselect.png' width='300px' />

菜单包括分类筛选，搜索，以及多选。其中分类筛选和搜索功能用MenuItem的回调函数实现，多选使用ActionMode实现，主要列表是用了SimpleAdaptor装配器实现。

关键代码：

```Java
//主页
public class MainActivity extends AppCompatActivity
{
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
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
        {
            //删除按钮
            if (menuItem.getItemId() == R.id.mi_delete)
            {
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
}
```

<img src='https://github.com/ZeroNinx/AS_Dev/blob/master/NotePad/screenshot/search.png' width='300px' /><img src='https://github.com/ZeroNinx/AS_Dev/blob/master/NotePad/screenshot/select_tag.png' width='300px' />

考虑到用户的体验，为了避免关键字漏查的情况，搜索功能对标题、文本、分类等涉及的字段都进行查找。多选操作可以长按，迎合传统习惯。

关键代码：

```Java
//主页
public class MainActivity extends AppCompatActivity
{
    //初始化列表
    protected void initList(String keyword)
    {
        //......
        //构造组件映射列表
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

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
            //.....

            //设置映射
            HashMap<String, Object> mp = new HashMap<String, Object>();
            mp.put("tv_id", result.getInt(idColumn));
            mp.put("iv_icon", R.drawable.icon_notepad);
            //.....

            list.add(mp);
        }

        //设定装配器
        SimpleAdapter sa = new SimpleAdapter(this, list, R.layout.lv_index_unit,
                new String[]{"tv_id", "iv_icon", "tv_title", "tv_text", "tv_time", "tv_tag"},
                new int[]{R.id.tv_id, R.id.iv_icon, R.id.tv_title, R.id.tv_text, R.id.tv_time, R.id.tv_tag});

        //装配
        lv_index.setAdapter(sa);
        //......
    }
}
```

图标和文字也统一使用了黑白配的风格，加强一体性。

关键代码：

```xml

    <TextView
        android:id="@+id/tv_tag"
        android:textSize="12sp"
        android:textStyle="italic" />

    <TextView
        android:id="@+id/tv_title"
        android:textColor="#000000"
        android:textSize="22sp"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/tv_text"
        android:textSize="12sp" />

    <TextView
        android:id="@+id/tv_time"
        android:textSize="12sp"
        android:textStyle="italic" />

    <TextView
        android:id="@+id/tv_id"
        android:textColor="#00FF0000" />

```
 
### 笔记界面

笔记界面以易用性为目的，主要是用字体区分标题栏，统一位置的分类筛选，同时在菜单上利用Layout实现了自定义的图标显示以及点击操作。

美化工作集中在文本编辑背景下划线（代码适配）以及更改背景颜色两个方面。

<img src='https://github.com/ZeroNinx/AS_Dev/blob/master/NotePad/screenshot/set_tag.png' width='300px' />

核心代码：

```Java

//主页
public class ActivityNotepad extends AppCompatActivity
{
    //创建时
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //...
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
                //...

                //还原参数
                et_title.setText(rows.getString(titleColumn));
                et_text.setText(rows.getString(textColumn));
                //...
            }
            rows.close();
        }
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
            //...

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
}

```

背景颜色更改使用了SeekBar提及其回调函数实现，利用了单独实现的Activity而不是弹出框加强定制化。

<img src='https://github.com/ZeroNinx/AS_Dev/blob/master/NotePad/screenshot/palette.png' width='300px' />

核心代码：

```xml

    <!--activity作为dialog-->
    <style name="dialog_style" parent="Theme.AppCompat.DayNight.NoActionBar">
        <!--是否悬浮在activity上-->
        <item name="android:windowIsFloating">true</item>
        <!--透明是否-->
        <item name="android:windowIsTranslucent">true</item>
        <item name="android:background">@null</item>
        <!--设置没有窗口标题、dialog标题等各种标题-->
        <item name="android:windowNoTitle">true</item>
        <item name="android:title">@null</item>
        <item name="android:dialogTitle">@null</item>
    </style>

```

```Java

public class PaletteActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //...
        //获取id
        id = getIntent().getIntExtra("id", 0);
        int color = getIntent().getIntExtra("background_color", 0);

        //分离颜色
        red = (color & 0xff0000) >> 16;
        green = (color & 0x00ff00) >> 8;
        blue = (color & 0x0000ff);

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
        });
        //...

        //提交按钮
        tv_submit.setOnClickListener(view ->
        {
            //存入数据库
            db = SQLiteDatabase.openOrCreateDatabase(getFilesDir().toString() + "/notepad.db", null);

            ContentValues cv = new ContentValues();
            cv.put("background_color", Color.rgb(red, green, blue));
            db.update("notepad", cv, "id=" + id, null);
        });

    }
}

```

笔记编辑改善了的行高和间距，下划线功能有安卓原生Bug，于是自己重写了TextEdit得以解决，这个Bug似乎在高版本中解决了。

<img src='https://github.com/ZeroNinx/AS_Dev/blob/master/NotePad/screenshot/notepad.png' width='300px' />

```Java

public class MultilineTextEditWithUnderLine extends androidx.appcompat.widget.AppCompatEditText
{

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

    }

}

```

