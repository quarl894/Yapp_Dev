package yapp.dev_diary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by YoungJung on 2017-08-21.
 */

public class ResultActivity extends AppCompatActivity {
    ArrayList<String> select_pic = new ArrayList<>();
    ImageView image, image2, image3, image4, image5;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        image = (ImageView) findViewById(R.id.test_img);
        image2 = (ImageView) findViewById(R.id.test_img2);
        image3 = (ImageView) findViewById(R.id.test_img3);
        image4 = (ImageView) findViewById(R.id.test_img4);
        image5 = (ImageView) findViewById(R.id.test_img5);
        Intent intent = getIntent();
        int chk_num = intent.getExtras().getInt("chk_num");
        if(chk_num == 1){
            select_pic = MainActivity.ok_path;
        }else{
            select_pic.clear();
        }
        for(int i=0; i<select_pic.size(); i++) {
            Bitmap img_bitmap = BitmapFactory.decodeFile(select_pic.get(i));
            switch (i) {
                case 0:
                    image.setImageBitmap(img_bitmap);
                    break;
                case 1:
                    image2.setImageBitmap(img_bitmap);
                    break;
                case 2:
                    image3.setImageBitmap(img_bitmap);
                    break;
                case 3:
                    image4.setImageBitmap(img_bitmap);
                    break;
                case 4:
                    image5.setImageBitmap(img_bitmap);
                    break;
                default:
                    break;
            }
        }
    }
}
