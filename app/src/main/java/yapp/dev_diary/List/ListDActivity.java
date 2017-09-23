package yapp.dev_diary.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import yapp.dev_diary.MainActivity;
import yapp.dev_diary.R;
import yapp.dev_diary.ResultActivity;
import yapp.dev_diary.Setting.SetActivity;
import yapp.dev_diary.Voice.VoiceActivity;

public class ListDActivity extends AppCompatActivity implements TimeRecyclerAdapter.OnItemClickListener {

    private TimeRecyclerAdapter adapter;

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
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.menu_start :
                Intent i = new Intent(this, VoiceActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_list :
                Intent i2 = new Intent(this, ResultActivity.class);
                startActivity(i2);
                return true;
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
                Intent i = new Intent(ListDActivity.this, SetActivity.class);
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

//        for(int i=1; i<13; i++){
//            dataset.add(new MyData("Hello", 2017, i, 1));
//        }

        dataset.add(new MyData("Cristiano Ronaldo", 1985, 1, 1));
        dataset.add(new MyData("Lionel Messi", 1985, 1, 1));
        dataset.add(new MyData("Wayne Rooney", 1985, 1, 1));
        dataset.add(new MyData("Karim Benzema", 1989, 12, 16));
        dataset.add(new MyData("Luka Modric", 1991, 8, 19));
        dataset.add(new MyData("Fernando Torres", 1992, 3, 24));
        dataset.add(new MyData("David Silva", 1986, 5, 6));
        dataset.add(new MyData("Raheem Sterling", 1986, 5, 6));
        dataset.add(new MyData("Philippe Coutinho", 1986, 5, 6));

        return dataset;
    }
}
