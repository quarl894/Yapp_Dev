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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import yapp.dev_diary.Calendar.Activity.MultiCalendarActivity;
import yapp.dev_diary.DB.MyDBHelper;
import yapp.dev_diary.DB.MyItem;
import yapp.dev_diary.R;
import yapp.dev_diary.SaveActivity;

public class ListDActivity extends AppCompatActivity implements TimeRecyclerAdapter.OnItemClickListener {
    private TimeRecyclerAdapter adapter;
    MyDBHelper     DBHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initToolbar();

        RecyclerView mTimeRecyclerView = (RecyclerView) findViewById(R.id.mTimeRecyclerView);
        mTimeRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mTimeRecyclerView.setLayoutManager(layoutManager);

        adapter = new TimeRecyclerAdapter(getDataset());
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
            case R.id.menu_list :
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListDActivity.this, MultiCalendarActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, adapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();
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
            dataset.add(new MyData(tmpItem.getTitle(), tmpItem.getDate()/10000, (tmpItem.getDate()/100)%100, tmpItem.getDate()%100));
            Log.i("p_path:", ""+tmpItem.getDate()/10000 +"," + (tmpItem.getDate()/100)%100 + ", "+tmpItem.getDate()%100);
        }
        return dataset;
    }
}
