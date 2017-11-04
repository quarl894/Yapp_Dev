package yapp.dev_diary.Voice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import yapp.dev_diary.List.ListDActivity;
import yapp.dev_diary.MainActivity;
import yapp.dev_diary.R;
import yapp.dev_diary.Setting.SetActivity;

/**
 * Created by HANSUNG on 2017-08-18.
 */

public class VoiceActivity extends AppCompatActivity {
    private Menu menu;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        Toast toast = Toast.makeText(getApplicationContext(),
                "토스트창에 출력될 문자", Toast.LENGTH_LONG);

        toast.show();
        initToolbar();
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
                Intent i = new Intent(VoiceActivity.this, SetActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        this.menu = menu;
        menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.record_icon));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        switch(id){
            case R.id.menu_start :

                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_list :
                Intent i2 = new Intent(this, ListDActivity.class);
                startActivity(i2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
