package yapp.dev_diary.Calendar.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import yapp.dev_diary.Calendar.interfaces.IInitializer;

/**
 * Created by 1100160 on 2017. 3. 17..
 */

public class BaseActivity extends AppCompatActivity implements IInitializer {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initialize() {
        initData();
        initView();
        initControl();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initControl() {

    }
}
