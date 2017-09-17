package yapp.dev_diary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by 이경원 on 2017-09-09.
 */

public class InputActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        Intent intent=new Intent(this.getIntent());

    }

}
