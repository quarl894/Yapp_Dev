package yapp.dev_diary.Calendar.Activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import yapp.dev_diary.Calendar.adapters.AdapterFrgCalendar;
import yapp.dev_diary.Calendar.adapters.AdapterRCVBase;
import yapp.dev_diary.Calendar.adapters.AdapterRcvSimple;
import yapp.dev_diary.Calendar.view.SimpleViewBinder;
import yapp.dev_diary.DB.MyDBHelper;
import yapp.dev_diary.DB.MyItem;
import yapp.dev_diary.Detail.DetailActivity;
import yapp.dev_diary.List.ListDActivity;
import yapp.dev_diary.List.MyData;
import yapp.dev_diary.MainActivity;
import yapp.dev_diary.R;
import yapp.dev_diary.Setting.SetActivity;

public class MultiCalendarActivity extends BaseActivity implements FrgCalendar.OnFragmentListener {

    private static final int COUNT_PAGE = 12;
    ViewPager pager;
    private AdapterFrgCalendar adapter;
    public static AdapterRcvSimple adapterHourLine;
    private RecyclerView rcv;
    private ArrayList mList;
    private TextView textView_title;
    private MyDBHelper DBHelper;
    private SQLiteDatabase db;
    public static List<MyItem> itemList;
    public static HashMap<String, Integer> calendar_Month_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_calendar);

        initialize();
        initToolbar();
    }

    @Override
    public void initData() {
        super.initData();
        mList = new ArrayList();

        //오늘 날짜 구하는 것
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String strToDay = simpleDateFormat.format(calendar.getTime());
        int intToDay = Integer.valueOf(strToDay);

        ArrayList<MyData> dataset = new ArrayList<>();
        DBHelper = new MyDBHelper(MultiCalendarActivity.this);
        db = DBHelper.getWritableDatabase();
        itemList = DBHelper.calendarSelect(intToDay);
        Log.d("체크", ""+intToDay);
        if (itemList != null)
        {
            for (int i = 0; i < itemList.size(); i++)
            {
                Log.e("itemList test:",itemList.get(i).getTitle());
                mList.add(itemList.get(i).getTitle());
            }
        }
        else
        {
            mList.add("일기를 추가해주세요");
        }

        if (calendar_Month_List == null)
        {
            calendar_Month_List = new HashMap<>();
            ArrayList tmpList = DBHelper.monthSelect(Integer.valueOf(strToDay.substring(0, 6).toString()), false);
            String tmpListItem = "";
            if (tmpList != null)
            {
                for (int i = 0; i < tmpList.size(); i++) {
                    tmpListItem = tmpList.get(i).toString();
                    calendar_Month_List.put(tmpListItem, Integer.valueOf(tmpListItem));
                }
            }
        }
    }

    @Override
    public void initView() {
        super.initView();
        pager = (ViewPager) findViewById(R.id.pager);
    }

    public void initControl() {
        adapter = new AdapterFrgCalendar(getSupportFragmentManager());
        pager.setAdapter(adapter);
        adapter.setOnFragmentListener(this);
        adapter.setNumOfMonth(COUNT_PAGE);
        pager.setCurrentItem(COUNT_PAGE);
        String title = adapter.getMonthDisplayed(COUNT_PAGE);

        //타이틀 textview
        textView_title = (TextView)findViewById(R.id.textview_title);
        textView_title.setText(title);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                String title = adapter.getMonthDisplayed(position);
                //getSupportActionBar().setTitle(title);
                textView_title.setText(title);

                Calendar calendar = Calendar.getInstance();

//                MyDBHelper DBHelper = new MyDBHelper(MultiCalendarActivity.this);
//                SQLiteDatabase db = DBHelper.getWritableDatabase();
//                String tmpStr = ""+calendar.get(Calendar.YEAR);
//
//                if (adapter.getMonthDisplayed(position).length() < 3)
//                    tmpStr += ("0"+adapter.getMonthDisplayed(position).substring(0,1));
//                else
//                    tmpStr += adapter.getMonthDisplayed(position).substring(0,2);
//                Log.d("체크", "확인~~~ : "+tmpStr);
//                calendar_Month_List = DBHelper.monthSelect(Integer.valueOf(tmpStr), false);

                if (position == 0) {
                    adapter.addPrev();
                    pager.setCurrentItem(COUNT_PAGE, false);
                } else if (position == adapter.getCount() - 1) {
                    adapter.addNext();
                    pager.setCurrentItem(adapter.getCount() - (COUNT_PAGE + 1), false);
                }
                Log.d("체크", "페이지 좌우");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        adapterHourLine = new AdapterRcvSimple(R.layout.item_rcv_simple);
        SimpleViewBinder.RecyclerViewBuilder builder =  new SimpleViewBinder.RecyclerViewBuilder(getWindow()).setAdapter(adapterHourLine, getSupportFragmentManager()).setList(mList);
        rcv = builder.build();

        adapterHourLine.setOnItemClickListener(new AdapterRCVBase.OnRCVItemListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = null;
                if (itemList != null)
                {
                    MyItem tmpItem = DBHelper.oneSelect(itemList.get(position).get_Index());
                    intent = new Intent(MultiCalendarActivity.this, DetailActivity.class);
                    int p_Check = tmpItem.getP_path().isEmpty() ? 0 : 1;
                    intent.putExtra("chk_num", p_Check);
                    intent.putExtra("rowID", tmpItem.get_Index());

                }
                else
                {
                    intent = new Intent(MultiCalendarActivity.this, MainActivity.class);
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public void onFragmentListener(View view) {
        resizeHeight(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.menu_calendar_search :
                return true;
            case R.id.menu_calendar_setting :
                Intent i3 = new Intent(this, SetActivity.class);
                startActivity(i3);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.calendar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MultiCalendarActivity.this, ListDActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void resizeHeight(View mRootView) {
        if (mRootView.getHeight() < 1) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = pager.getLayoutParams();
        if (layoutParams.height <= 0) {
            layoutParams.height = mRootView.getHeight();
            pager.setLayoutParams(layoutParams);
            return;
        }
        ValueAnimator anim = ValueAnimator.ofInt(pager.getLayoutParams().height, mRootView.getHeight());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = pager.getLayoutParams();
                layoutParams.height = val;
                pager.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(200);
        anim.start();
    }
}
