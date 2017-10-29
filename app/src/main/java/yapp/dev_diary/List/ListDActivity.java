package yapp.dev_diary.List;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import yapp.dev_diary.Calendar.Activity.MultiCalendarActivity;
import yapp.dev_diary.DB.MyDBHelper;
import yapp.dev_diary.DB.MyItem;
import yapp.dev_diary.Detail.DetailActivity;
import yapp.dev_diary.R;

public class ListDActivity extends AppCompatActivity implements TimeRecyclerAdapter.OnItemClickListener {
    private TimeRecyclerAdapter adapter;
    MyDBHelper     DBHelper;
    SQLiteDatabase db;

    private LinearLayout buttonsBottom;
    private Button       buttonBackup;
    private Button       buttonDelete;
    private boolean     BUTTONS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initToolbar();

        buttonsBottom = (LinearLayout)findViewById(R.id.btns_bottom);
        buttonBackup = (Button)findViewById(R.id.btn_list_backup);
        buttonDelete = (Button)findViewById(R.id.btn_list_delete);


        RecyclerView mTimeRecyclerView = (RecyclerView) findViewById(R.id.mTimeRecyclerView);
        mTimeRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mTimeRecyclerView.setLayoutManager(layoutManager);

        adapter = new TimeRecyclerAdapter(getApplicationContext(), getDataset());
        adapter.setOnItemClickListener(this);
        mTimeRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.menu_start :
                Log.i("optionSelected", "R.id.menu_start");
                break;
            case R.id.menu_list_modify :
                Log.i("optionSelected", "R.id.menu_list_modify");
                buttonsBottom.setVisibility(View.VISIBLE);
                animSlideUp(buttonsBottom, "menu_list_modify");
                BUTTONS = true;
                initToolbar();
                break;
            case R.id.menu_list_setting :
                Log.i("optionSelected", "R.id.menu_list_setting");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Toolbar toolbar = null;
    private void initToolbar() {
        if( toolbar==null ) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if( BUTTONS )
        {
            // 텍스트로 못해서 일단 아이콘으로 둡니다.................
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.reset);
            toolbar.setNavigationOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // 체크박스 선택해제, 체크박스 리스트 삭제, 체크박스 안보이게

                    slideDownButtons("cancle");
                }
            });
        }
        else
        {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.setting);


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ListDActivity.this, MultiCalendarActivity.class);
                    startActivity(i);
                }
            });
        }

    }

    @Override
    public void onItemClick(int position) {
            Toast.makeText(this, adapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();
        /* 해당 postiion의 MyData 가져오기. DBIndex가져와서 rowID로 인텐트에 담아서 DetailActivity열기 */
        MyData selected = adapter.getItem(position);
        Intent i = new Intent(this, DetailActivity.class);
        MyItem tmpItem = DBHelper.oneSelect(selected.getDBIndex());
        int p_Check = tmpItem.getP_path().isEmpty() ? 0 : 1;

        i.putExtra("chk_num", p_Check);      // 이렇게요
        i.putExtra("rowID", selected.getDBIndex());
        startActivity(i);
    }

    private ArrayList<MyData> getDataset() {
        ArrayList<MyData> dataset = new ArrayList<>();

        DBHelper = new MyDBHelper(ListDActivity.this);
        db = DBHelper.getWritableDatabase();
        Log.i("db", "DBHelper.allSelect()");
        List<MyItem> itemList = DBHelper.allSelect();
        MyItem tmpItem = null;
        for(int i = 0; i < itemList.size(); i++)
        {
            tmpItem = itemList.get(i);
            dataset.add(new MyData(tmpItem.getTitle(), tmpItem.getDate()/10000, (tmpItem.getDate()/100)%100, tmpItem.getDate()%100, tmpItem.get_Index()));
            Log.i("p_path:", tmpItem.getTitle() + ", "+tmpItem.getDate()/10000 +"," + (tmpItem.getDate()/100)%100 + ", "+tmpItem.getDate()%100 + ", " + tmpItem.get_Index());
        }
        return dataset;
    }



    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_list_backup:

                slideDownButtons("btn_list_backup");
                BUTTONS = false;
                initToolbar();
                break;
            case R.id.btn_list_delete:
                adapter.deleteSelected(this);
                adapter.notifyDataSetChanged();

                slideDownButtons("btn_list_delete");
                BUTTONS = false;
                initToolbar();
                break;
        }
    }


    private void animSlideDown(View view, String msg){
        Animation slide_down = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        slide_down.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.setAnimation(slide_down);
        Log.i("anim", "slide_down   / " + msg);
    }


    private void animSlideUp(View view, String msg){
        Animation slide_up = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slide_up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.setAnimation(slide_up);
        Log.i("anim", "slide_up / " + msg);
    }

    private void slideDownButtons(String msg)
    {
        buttonsBottom.setVisibility(View.INVISIBLE);
        animSlideDown(buttonsBottom, msg);
        initToolbar();
    }

    // 하단에 버튼 올라와 있으면 버튼 내리기
    @Override
    public void onBackPressed() {
        if(BUTTONS)
        {
            slideDownButtons("btn_list_delete");
            BUTTONS = false;
        }
        else super.onBackPressed();
    }
}
