package yapp.dev_diary;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import yapp.dev_diary.DB.MyDBHelper;
import yapp.dev_diary.DB.MyItem;
import yapp.dev_diary.Detail.DetailActivity;

/**
 * Created by YoungJung on 2017-08-26.
 */

public class SaveActivity extends AppCompatActivity {
    Button save_btn, btn_weather, btn_feel;
    Switch pic_switch;
    static int feel, weather;
    int chk_num;
    ImageButton img1, img2, img3, img4,img5,img6,img7,img8;
    TextView data_view;
    EditText edit_title;
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
    MyDBHelper DBHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBHelper = new MyDBHelper(SaveActivity.this);
        db = DBHelper.getWritableDatabase();

        // 상태바, 엑션바 둘다 없애기 setContentView 보다 먼저 써야됨.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main_btn);
        img1 = (ImageButton) findViewById(R.id.img1);
        img2 = (ImageButton) findViewById(R.id.img2);
        img3 = (ImageButton) findViewById(R.id.img3);
        img4 = (ImageButton) findViewById(R.id.img4);
        img5 = (ImageButton) findViewById(R.id.img5);
        img6 = (ImageButton) findViewById(R.id.img6);
        img7 = (ImageButton) findViewById(R.id.img7);
        img8 = (ImageButton) findViewById(R.id.img8);
        img1.setVisibility(View.VISIBLE);
        img2.setVisibility(View.VISIBLE);
        img3.setVisibility(View.VISIBLE);
        img4.setVisibility(View.VISIBLE);
        img5.setVisibility(View.INVISIBLE);
        img6.setVisibility(View.INVISIBLE);
        img7.setVisibility(View.INVISIBLE);
        img8.setVisibility(View.INVISIBLE);
        btn_weather = (Button) findViewById(R.id.btn_weather);
        btn_feel = (Button) findViewById(R.id.btn_feel);
        data_view= (TextView) findViewById(R.id.edit_btn);
        edit_title = (EditText) findViewById(R.id.edit_title);
        chk_num =1;
        edit_title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });
        data_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SaveActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        btn_weather.setFocusableInTouchMode(true);
        btn_weather.requestFocus();
        Log.e("TEST","현재 포커스=>"+getCurrentFocus());
        btn_weather.setTextColor(getResources().getColor(R.color.colorAccent));
        // 날씨 이모티콘
        btn_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_weather.setTextColor(getResources().getColor(R.color.colorAccent));
                btn_feel.setTextColor(getResources().getColor(R.color.gray));
                img1.setVisibility(View.VISIBLE);
                img2.setVisibility(View.VISIBLE);
                img3.setVisibility(View.VISIBLE);
                img4.setVisibility(View.VISIBLE);
                img5.setVisibility(View.INVISIBLE);
                img6.setVisibility(View.INVISIBLE);
                img7.setVisibility(View.INVISIBLE);
                img8.setVisibility(View.INVISIBLE);
                img1.setEnabled(true);
                img2.setEnabled(true);
                img3.setEnabled(true);
                img4.setEnabled(true);
                img5.setEnabled(false);
                img6.setEnabled(false);
                img7.setEnabled(false);
                img8.setEnabled(false);
                img1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img1.setImageAlpha(2000);
                        img2.setImageAlpha(50);
                        img3.setImageAlpha(50);
                        img4.setImageAlpha(50);
                    }
                });
                img2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img2.setImageAlpha(2000);
                        img1.setImageAlpha(50);
                        img3.setImageAlpha(50);
                        img4.setImageAlpha(50);
                    }
                });
                img3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img3.setImageAlpha(2000);
                        img2.setImageAlpha(50);
                        img1.setImageAlpha(50);
                        img4.setImageAlpha(50);
                    }
                });
                img4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img4.setImageAlpha(2000);
                        img2.setImageAlpha(50);
                        img3.setImageAlpha(50);
                        img1.setImageAlpha(50);
                    }
                });
            }
        });
        //기분 이모티콘
        btn_feel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img5.setVisibility(View.VISIBLE);
                img6.setVisibility(View.VISIBLE);
                img7.setVisibility(View.VISIBLE);
                img8.setVisibility(View.VISIBLE);
                img1.setVisibility(View.INVISIBLE);
                img2.setVisibility(View.INVISIBLE);
                img3.setVisibility(View.INVISIBLE);
                img4.setVisibility(View.INVISIBLE);
                img8.setEnabled(true);
                img7.setEnabled(true);
                img6.setEnabled(true);
                img5.setEnabled(true);
                img4.setEnabled(false);
                img3.setEnabled(false);
                img2.setEnabled(false);
                img1.setEnabled(false);
                btn_feel.setTextColor(getResources().getColor(R.color.colorAccent));
                btn_weather.setTextColor(getResources().getColor(R.color.gray));
                img5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img5.setImageAlpha(2000);
                        img6.setImageAlpha(50);
                        img7.setImageAlpha(50);
                        img8.setImageAlpha(50);
                    }
                });
                img6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img6.setImageAlpha(2000);
                        img5.setImageAlpha(50);
                        img7.setImageAlpha(50);
                        img8.setImageAlpha(50);
                    }
                });
                img7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img7.setImageAlpha(2000);
                        img6.setImageAlpha(50);
                        img5.setImageAlpha(50);
                        img8.setImageAlpha(50);
                    }
                });
                img8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img8.setImageAlpha(2000);
                        img5.setImageAlpha(50);
                        img6.setImageAlpha(50);
                        img7.setImageAlpha(50);
                    }
                });
            }
        });
        btn_weather.performClick();
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
                // DB에 추가
                // 임시 데이터들
                Intent mainIntent = getIntent();
                String strP_Path = "";
                for (int i = 0; i < MainActivity.ok_path.size(); i++)
                {
                    if (i == MainActivity.ok_path.size()-1)
                        strP_Path += MainActivity.ok_path.get(i);
                    else
                        strP_Path += MainActivity.ok_path.get(i)+",";
                }
                String p_path = strP_Path;
                String r_path = mainIntent.getStringExtra("r_path");
                String content = mainIntent.getStringExtra("content");
                String title = edit_title.getText().toString();
                int dateInt = 0;

                dateInt = myCalendar.get(Calendar.YEAR);
                dateInt *= 100;
                dateInt += myCalendar.get(Calendar.MONTH) + 1;
                dateInt *= 100;
                dateInt += myCalendar.get(Calendar.DAY_OF_MONTH);
                Log.i("db", "p_path : " + p_path + ", r_path : " + r_path + ", content : " + content + "weather : " + weather + ", feel : " + feel + ", title : " + title + ", date : " + dateInt);
                MyItem newItem = new MyItem(p_path, r_path, content, weather, feel, title, dateInt, 0);
                int rowID = DBHelper.insert(newItem);

                Intent i = new Intent(SaveActivity.this,DetailActivity.class);
                i.putExtra("chk_num", chk_num);
                i.putExtra("rowID", rowID);
                startActivity(i);
                finish();
            }
        });
    }
    private void updateLabel() {
        String myFormat = "yyyy"+"년 "+"MM"+"월 " +"dd"+"일"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        data_view.setText(sdf.format(myCalendar.getTime()));
    }
}
