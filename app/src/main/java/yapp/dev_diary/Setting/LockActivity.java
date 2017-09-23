package yapp.dev_diary.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import yapp.dev_diary.Lock.core.AppLock;
import yapp.dev_diary.Lock.core.AppLockActivity;
import yapp.dev_diary.Lock.core.LockManager;
import yapp.dev_diary.R;

/**
 * Created by AnGwangHo on 2017-09-17.
 */

public class LockActivity extends AppCompatActivity {
    private Switch switch_lock;
    private LinearLayout setting_lock_change_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_lock);

        switch_lock = (Switch) findViewById(R.id.setting_lock_onoff_switch);
        setting_lock_change_layout = (LinearLayout) findViewById(R.id.setting_lock_change_layout);

        LockManager.getInstance().enableAppLock(getApplication());
        updateUI();

        switch_lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked)
                { //체크상태
                    int type = AppLock.ENABLE_PASSLOCK;
                    Intent intent = new Intent(LockActivity.this, AppLockActivity.class);
                    intent.putExtra(AppLock.TYPE, type);
                    startActivityForResult(intent, type);
                }
                else
                {
                    int type = AppLock.DISABLE_PASSLOCK;
                    Intent intent = new Intent(LockActivity.this, AppLockActivity.class);
                    intent.putExtra(AppLock.TYPE, type);
                    startActivityForResult(intent, type);
                }
            }
        });

        setting_lock_change_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LockActivity.this, AppLockActivity.class);
                intent.putExtra(AppLock.TYPE, AppLock.CHANGE_PASSWORD);
                intent.putExtra(AppLock.MESSAGE,
                        getString(R.string.enter_old_passcode));
                startActivityForResult(intent, AppLock.CHANGE_PASSWORD);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AppLock.DISABLE_PASSLOCK:
                break;
            case AppLock.ENABLE_PASSLOCK:
            case AppLock.CHANGE_PASSWORD:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, getString(R.string.setup_passcode),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        updateUI();
    }

    private void updateUI() {
        if (LockManager.getInstance().getAppLock().isPasscodeSet()) {
            switch_lock.setChecked(true);
            setting_lock_change_layout.setEnabled(true);
        } else {
            switch_lock.setChecked(false);
            setting_lock_change_layout.setEnabled(false);
        }
    }
}