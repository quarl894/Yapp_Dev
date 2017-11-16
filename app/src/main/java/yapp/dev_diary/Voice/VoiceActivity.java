package yapp.dev_diary.Voice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import yapp.dev_diary.List.ListDActivity;
import yapp.dev_diary.MainActivity;
import yapp.dev_diary.R;
import yapp.dev_diary.SaveActivity;
import yapp.dev_diary.Setting.SetActivity;

/**
 * Created by HANSUNG on 2017-08-18.
 */

public class VoiceActivity extends AppCompatActivity implements View.OnClickListener{
    private Menu menu;
    EditText txtResult;
    Button mBtnSave2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        mBtnSave2 = (Button)findViewById(R.id.btnSave2);
        txtResult = (EditText) findViewById(R.id.txt_result);
        txtResult.setText("");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        this.menu = menu;
        menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.record_icon));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_start:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_list:
                Intent i2 = new Intent(this, ListDActivity.class);
                startActivity(i2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave2:
                try {
                    String param = null;
                    Intent i = new Intent(VoiceActivity.this, SaveActivity.class);
                    i.putExtra("content", txtResult.getText().toString());
                    i.putExtra("r_path", param);
                    startActivity(i);
                }
                catch (Exception e){

                }
        }
    }
}
