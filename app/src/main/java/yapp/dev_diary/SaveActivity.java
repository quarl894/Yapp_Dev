package yapp.dev_diary;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import yapp.dev_diary.Detail.DetailActivity;

/**
 * Created by YoungJung on 2017-08-26.
 */

public class SaveActivity extends AppCompatActivity {
    Button save_btn, btn_weather, btn_feel;
    Switch pic_switch;
    int chk_num;
    ImageButton img1, img2, img3, img4;
    EditText edit_btn;
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태바, 엑션바 둘다 없애기 setContentView 보다 먼저 써야됨.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main_btn);
        img1 = (ImageButton) findViewById(R.id.img1);
        img2 = (ImageButton) findViewById(R.id.img2);
        img3 = (ImageButton) findViewById(R.id.img3);
        img4 = (ImageButton) findViewById(R.id.img4);
        btn_weather = (Button) findViewById(R.id.btn_weather);
        btn_feel = (Button) findViewById(R.id.btn_feel);
        edit_btn = (EditText) findViewById(R.id.edit_btn);
        chk_num =1;
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SaveActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //기분 이모티콘
        btn_feel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_feel.setTextColor(getResources().getColor(R.color.colorAccent));
                btn_weather.setTextColor(getResources().getColor(R.color.gray));
                img1.setImageResource(R.drawable.smile);
                img2.setImageResource(R.drawable.notbad);
                img3.setImageResource(R.drawable.sad);
                img4.setImageResource(R.drawable.angry);
            }
        });
        // 날씨 이모티콘
        btn_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_weather.setTextColor(getResources().getColor(R.color.colorAccent));
                btn_feel.setTextColor(getResources().getColor(R.color.gray));
                img1.setImageResource(R.drawable.sun);
                img2.setImageResource(R.drawable.cloud);
                img3.setImageResource(R.drawable.rain);
                img4.setImageResource(R.drawable.snow);
            }
        });

        //사진 가져오기
        pic_switch = (Switch) findViewById(R.id.switch_btn);
        pic_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) chk_num =1;
                else chk_num = 0;
            }
        });

        //완료
        save_btn = (Button) findViewById(R.id.btn_save);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SaveActivity.this,DetailActivity.class);
                i.putExtra("chk_num", chk_num);
                startActivity(i);
            }
        });
    }
    private void updateLabel() {
        String myFormat = "yyyy"+"년 "+"MM"+"월 " +"dd"+"일"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        edit_btn.setText(sdf.format(myCalendar.getTime()));
    }
}
