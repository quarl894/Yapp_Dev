package yapp.dev_diary.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import yapp.dev_diary.Calendar.Activity.MultiCalendarActivity;
import yapp.dev_diary.DB.MyDBHelper;
import yapp.dev_diary.DB.MyItem;
import yapp.dev_diary.Detail.DetailActivity;
import yapp.dev_diary.R;

public class ListDActivity extends AppCompatActivity implements TimeRecyclerAdapter.OnItemClickListener {
    private TimeRecyclerAdapter adapter;
    MyDBHelper     DBHelper;
    SQLiteDatabase db;
    static boolean cb_check;

    private LinearLayout buttonsBottom;
    private boolean     BUTTONS = false;
    private RecyclerView mTimeRecyclerView;
    ScrollView sv;

    static boolean allChecked = false;

    private Toolbar toolbar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        buttonsBottom = (LinearLayout)findViewById(R.id.btns_bottom);

        mTimeRecyclerView = (RecyclerView) findViewById(R.id.mTimeRecyclerView);
        sv = (ScrollView) findViewById(R.id.scroll_view);

        initToolbar();

        mTimeRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mTimeRecyclerView.setLayoutManager(layoutManager);

        adapter = new TimeRecyclerAdapter(getApplicationContext(), getDataset());
        adapter.setOnItemClickListener(this);
        mTimeRecyclerView.setAdapter(adapter);
        cb_check = false;

    }

    private Menu menu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.menu_start :
                Intent i = new Intent(this, SearchActivity.class);
                startActivity(i);
                Log.i("optionSelected", "R.id.menu_start");
                break;

            case R.id.menu_list_modify :
                Log.i("optionSelected", "R.id.menu_list_modify");

                // 하단 버튼들 (백업, 삭제)
                buttonsBottom.setVisibility(View.VISIBLE);

                Log.i("height4", Integer.toString((int)getResources().getDimension(R.dimen.buttons_height)) );
                animSlideUp(buttonsBottom, "menu_list_modify");
                BUTTONS = true;
                initToolbar();

                cb_check = true;
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

                sv.setPadding(0,0,0, (int)getResources().getDimension(R.dimen.buttons_height));
                break;

            case R.id.menu_list_setting :
                BUTTONS = true;
                break;

            case R.id.menu_select_all :
                mTimeRecyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                if( allChecked == false){
                    adapter.checkAll(true);
                    allChecked = true;
                    ( (ActionMenuItemView) findViewById(R.id.menu_select_all) ).setIcon(getResources().getDrawable(R.drawable.checked));
                }else{
                    adapter.checkAll(false);
                    allChecked = false;
                    ( (ActionMenuItemView) findViewById(R.id.menu_select_all) ).setIcon(getResources().getDrawable(R.drawable.unchecked));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

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
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.cancel_02);
            toolbar.setNavigationOnClickListener(new View.OnClickListener(){    // 취소 버튼 리스너
                @Override
                public void onClick(View v) {
                    cb_check = false;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    slideDownButtons("cancle");
                    initToolbar();
                }
            });
        }
        else
        {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.calendar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ListDActivity.this, MultiCalendarActivity.class);
                    startActivity(i);
                }
            });
        }
        onPrepareOptionsMenu(menu);
    }
    @Override
    public void onItemClick(int position) {
        /* 해당 postiion의 MyData 가져오기. DBIndex가져와서 rowID로 인텐트에 담아서 DetailActivity열기 */
        MyData selected = adapter.getItem(position);
        Intent i = new Intent(this, DetailActivity.class);
        MyItem tmpItem = DBHelper.oneSelect(selected.getDBIndex());
        int p_Check = tmpItem.getP_path().isEmpty() ? 0 : 1;

        i.putExtra("chk_num", p_Check);
        i.putExtra("rowID", selected.getDBIndex());
        startActivity(i);
    }

    private ArrayList<MyData> getDataset() {
        ArrayList<MyData> dataset = new ArrayList<>();

        DBHelper = new MyDBHelper(ListDActivity.this);
        db = DBHelper.getWritableDatabase();
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

    // 하단 버튼들에 대한 리스너 : 백업, 삭제
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_list_backup:
                Toast.makeText(this, "준비 중 입니다. :)", Toast.LENGTH_SHORT).show();
                slideDownButtons("btn_list_backup");
                cb_check = false;
                initToolbar();
                break;

            case R.id.btn_list_delete:
                deleteDialog(true);
                break;
        }
    }

    /**
     * 삭제시 보여주는 다이얼로그 메소드
     * @param allChecked true-전체선택 시 false-부분선택 시
     * */
    private void deleteDialog(boolean allChecked){
        String title, contents;
        int diarycnt = 0;//추후 몇개 선택했는지 이벤트에서 카운팅할 것.

        if (allChecked)
        {
            title = "선택된 일기 삭제";
            contents = "선택된 일기를 삭제 하시겠습니까?";
        }
        else
        { //사용자가 선택해서 삭제한 경우
            title = "일기 " + diarycnt + "개 삭제";
            contents = "의 일기를 삭제 하시겠습니까?";
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title)
                          .setMessage(contents)
                          .setCancelable(false)
                          .setPositiveButton("예",
                                  new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialogInterface, int i) {
                                          adapter.deleteSelected(ListDActivity.this);
                                          adapter.notifyDataSetChanged();
                                          slideDownButtons("btn_list_delete");
                                          cb_check = false;
                                          initToolbar();
                                      }
                                  })
                          .setNegativeButton("아니요",
                                  new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialogInterface, int i) {
                                          dialogInterface.cancel();
                                      }
                                  });
        //생성
        AlertDialog alertDialog = alertDialogBuilder.create();

        //활성화
        alertDialog.show();
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
    }

    // BUTTONS to false, visibility, 내리는 애니메이션, 스크롤뷰 패딩 설정, adapter 함수 호출
    private void slideDownButtons(String msg){
        BUTTONS = false;
        buttonsBottom.setVisibility(View.GONE);
        animSlideDown(buttonsBottom, msg);
        sv.setPadding(0,0,0,0);
    }

    // 하단에 버튼 올라와 있으면 버튼 내리기
    @Override
    public void onBackPressed(){
        if(BUTTONS) {
            slideDownButtons("btn_list_delete");
            cb_check = false;
        }else
            super.onBackPressed();
//        finish();
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        try{
            if(menu == null) Log.e("null", "menu is null");
            else {
                if (BUTTONS) {
                    menu.clear();
                    getMenuInflater().inflate(R.menu.menu_list2, menu);
                }else {
                    menu.clear();
                    getMenuInflater().inflate(R.menu.menu_list, menu);
                }
            }
        }catch(Exception e){
            Log.e("onPreareOptionsMenu", e.toString());
        }
        return super.onPrepareOptionsMenu(menu);
    }
    public void scrollToEnd(){
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
}