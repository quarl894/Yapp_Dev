package yapp.dev_diary.List;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;


import yapp.dev_diary.DB.MyDBHelper;
import yapp.dev_diary.DB.MyItem;
import yapp.dev_diary.Detail.DetailActivity;
import yapp.dev_diary.R;

import static yapp.dev_diary.R.id.mTimeRecyclerView;

/**
 * Created by seoheepark on 2017-10-29.
 */

public class SearchActivity  extends AppCompatActivity implements TimeRecyclerAdapter.OnItemClickListener {
    private TimeRecyclerAdapter adapter;
    public static SQLiteDatabase db;
    public static int count;
    public  static MyItem[] myItems;
    public SearchView searchview;
    MyDBHelper helper;
    static boolean cb_check;
    ScrollView sv;
    RecyclerView mTimeRecyclerView2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        helper = new MyDBHelper(this);
        db= helper.getWritableDatabase();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTimeRecyclerView2 = (RecyclerView) findViewById(mTimeRecyclerView);
        sv = (ScrollView) findViewById(R.id.scroll_view);
        mTimeRecyclerView2.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
        mTimeRecyclerView2.setLayoutManager(layoutManager);

        adapter = new TimeRecyclerAdapter(getApplicationContext(), getDataset(null));
        adapter.setOnItemClickListener(SearchActivity.this);
        mTimeRecyclerView2.setAdapter(adapter);



        cb_check = false;

    }
    @Override
    public void onItemClick(int position) {
        /* 해당 postiion의 MyData 가져오기. DBIndex가져와서 rowID로 인텐트에 담아서 DetailActivity열기 */
        MyData selected = adapter.getItem(position);
        Intent i = new Intent(this, DetailActivity.class);
        MyItem tmpItem = helper.oneSelect(selected.getDBIndex());
        int p_Check = tmpItem.getP_path().isEmpty() ? 0 : 1;

        i.putExtra("chk_num", p_Check);      // 이렇게요
        i.putExtra("rowID", selected.getDBIndex());
        startActivity(i);
    }

    private ArrayList<MyData> getDataset(String query) {
        ArrayList<MyData> dataset = new ArrayList<>();

        helper = new MyDBHelper(SearchActivity.this);
        db = helper.getWritableDatabase();
        Log.i("db", "DBHelper.allSelect()");
        List<MyItem> itemList = helper.searchSelect(query);
        MyItem tmpItem = null;
        for(int i = 0; i < itemList.size(); i++)
        {
            tmpItem = itemList.get(i);
            dataset.add(new MyData(tmpItem.getTitle(), tmpItem.getDate()/10000, (tmpItem.getDate()/100)%100, tmpItem.getDate()%100, tmpItem.get_Index()));
            Log.i("p_path:", tmpItem.getTitle() + ", "+tmpItem.getDate()/10000 +"," + (tmpItem.getDate()/100)%100 + ", "+tmpItem.getDate()%100 + ", " + tmpItem.get_Index());
        }
        return dataset;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchview = (SearchView) menu.findItem(R.id.search).getActionView();
        searchview.setIconified(false);
        searchview.clearFocus();
        searchview.setQueryHint("검색어를 입력해주세요.");

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if(null!=searchManager ) {
            searchview.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        return true;

    }
    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener(){
        @Override
        public boolean onQueryTextSubmit(String query) {

                adapter = new TimeRecyclerAdapter(getApplicationContext(), getDataset(query));
                adapter.setOnItemClickListener(SearchActivity.this);
                mTimeRecyclerView2.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
            searchview.setQuery("",false);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };
}



