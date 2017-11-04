package yapp.dev_diary.Detail;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import yapp.dev_diary.DB.MyDBHelper;
import yapp.dev_diary.DB.MyItem;
import yapp.dev_diary.R;

/**
 * Created by YoungJung on 2017-08-27.
 * circle progressbar_gradient Reference : https://github.com/CardinalNow/Android-CircleProgressIndicator
 */

public class DetailActivity extends BaseActivity implements ObservableScrollViewCallbacks {
    ArrayList<String> select_pic = new ArrayList<>();
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private ViewGroup mSelectedImagesContainer;
    public RequestManager mGlideRequestManager;
    private View mImageView;
    //    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;    // 제목
    private TextView mContentView;  // 내용
    private View mFab;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin, mProgressBarMargin;
    private boolean mFabIsShown;
    private TextView mTitleDate;
    private TextView mTitleDiary, mTitlePic;
    private ImageButton btn_edit, btn_backup;
    private LinearLayout edit_view;

    private MyDBHelper     DBHelper;
    private SQLiteDatabase db;

    ProgressBar mProgressBar, backProgressBar;
    Handler handler = new Handler();
    Context context;
    private TextView record_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        DBHelper = new MyDBHelper(DetailActivity.this);
        db = DBHelper.getWritableDatabase();

        Intent intent = getIntent();
        final int chk_num = intent.getExtras().getInt("chk_num");
        final int rowID = intent.getExtras().getInt("rowID");
        final MyItem thisItem = DBHelper.oneSelect(rowID);

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();

        mImageView = findViewById(R.id.image);
//        mOverlayView = findViewById(R.id.overlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(thisItem.getTitle());
        mContentView = (TextView) findViewById(R.id.detail_context);
        mContentView.setText(thisItem.getContent());
        mTitleDate = (TextView) findViewById(R.id.detail_title_date);

        //***날짜 형식 변경***
        Date nDate = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            nDate = simpleDateFormat.parse(thisItem.getDate()+"");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd E");
        String strToDay = simpleDateFormat.format(nDate);

        mTitleDate.setText(strToDay);
        mTitleDiary = (TextView) findViewById(R.id.title_diary);

        mTitlePic = (TextView) findViewById(R.id.title_pic);
        mTitleDiary.setText("오늘의\n일기_");
        mTitlePic.setText("오늘의\n사진_");
        edit_view = (LinearLayout) findViewById(R.id.edit_view);
        edit_view.bringToFront();
        record_time = (TextView) findViewById(R.id.record_time);
        context = this.getApplicationContext();
        mProgressBar = (ProgressBar) findViewById(R.id.circle_progress_bar);
        backProgressBar = (ProgressBar) findViewById(R.id.circle_back_progress_bar);

        Log.d("체크", ""+chk_num+","+thisItem.getP_path().toString());
        if(chk_num == 1){
            ArrayList<String> tmpList = new ArrayList<>();
            if (!thisItem.getP_path().isEmpty())
            {
                String[] strList = thisItem.getP_path().split(",");

                for (int i = 0; i < strList.length; i ++)
                {
                    tmpList.add(strList[i]);
                }
                select_pic = tmpList;
            }
            else
                select_pic.clear();

        }else{
            select_pic.clear();
        }
        mGlideRequestManager = Glide.with(this);
        mSelectedImagesContainer = (ViewGroup) findViewById(R.id.selected_photos_container);
        showStringgList(select_pic);
        btn_edit = (ImageButton) findViewById(R.id.btn_edit);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailActivity.this, "hi", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(DetailActivity.this, AdjustActivity.class);
                i.putExtra("chk_num", chk_num);
                i.putExtra("rowID", rowID);
                startActivity(i);
                finish();
            }
        });

        setTitle(null);
        mFab = findViewById(R.id.fab);
        Log.d("체크", "음성 패스"+thisItem.getR_path());
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailActivity.this, "녹음파일을 재생합니다", Toast.LENGTH_SHORT).show();
                MediaPlayer mPlayer = new MediaPlayer();
                if (thisItem.getR_path() != null)
                {
                    try {
                        mPlayer.setDataSource(thisItem.getR_path());
                        mPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    final int record_sec = mPlayer.getDuration()/1000;
                    mPlayer.start();
                    // 현재 시간을 받아옴
                    mProgressBar.setProgress(record_sec);
                    new Thread(new Runnable() {
                        int progressStatus = record_sec;
                        public void run() {
                            while (progressStatus > 0) {
                                progressStatus -= 1;
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                // Update the progress bar
                                handler.post(new Runnable() {
                                    public void run() {
                                        mProgressBar.setProgress(progressStatus);
                                        record_time.setText("00:" + String.format("%02d", progressStatus));
                                        Log.e("test",Integer.toString(mProgressBar.getProgress()));
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });
        Log.e("view 확인", record_time.getParent().toString() +"//" + mFab.getParent().toString());
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        record_time.getParent().bringChildToFront(mFab);
        record_time.bringToFront();
        record_time.invalidate();
        Log.e("view 확인2", record_time.getParent().toString() +"//" + mFab.getParent().toString());
        ViewHelper.setScaleX(mFab, 0);
        ViewHelper.setScaleY(mFab, 0);

        ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, 1);
            }
        });
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
//        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
//        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
//        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
//        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 2000);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        ViewHelper.setPivotX(mTitleDate,0);
        ViewHelper.setPivotY(mTitleDate, 2100);
        ViewHelper.setScaleX(mTitleDate, scale);
        ViewHelper.setScaleY(mTitleDate, scale);

        ViewHelper.setPivotX(record_time,-350);
        ViewHelper.setPivotY(record_time, 100);
        ViewHelper.setScaleX(record_time, scale);
        ViewHelper.setScaleY(record_time, scale);

        ViewHelper.setPivotX(mProgressBar,-350);
        ViewHelper.setPivotY(mProgressBar, 400);
        ViewHelper.setScaleX(mProgressBar, scale);
        ViewHelper.setScaleY(mProgressBar, scale);

        ViewHelper.setPivotX(backProgressBar,-350);
        ViewHelper.setPivotY(backProgressBar, 400);
        ViewHelper.setScaleX(backProgressBar, scale);
        ViewHelper.setScaleY(backProgressBar, scale);
        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

        ViewHelper.setTranslationY(mTitleDate, titleTranslationY);

        ViewHelper.setTranslationY(record_time, titleTranslationY);

        ViewHelper.setTranslationY(backProgressBar,titleTranslationY);
        ViewHelper.setTranslationY(mProgressBar,titleTranslationY);

        // Translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
                mActionBarSize - mFab.getHeight() / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
            // which causes FAB's OnClickListener not working.
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
//            lp.leftMargin = mOverlayView.getWidth() - mFabMargin - mFab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            mFab.requestLayout();
            record_time.getParent().bringChildToFront(mFab);

        } else {
//            ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
            ViewHelper.setTranslationY(mFab, fabTranslationY-150);
            ViewHelper.setTranslationY(record_time, fabTranslationY-150);
//            ViewHelper.setTranslationY(mProgressBar, fabTranslationY-150);
//            mScrollView.getParent().bringChildToFront(mProgressBar);
//            record_time.bringToFront();
//            record_time.getParent().bringChildToFront(mFab);

//            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) record_time.getLayoutParams();
//            ((FrameLayout) mFab.getParent()).addView(record_time);
            mFab.getParent().bringChildToFront(record_time);
            record_time.getParent().clearChildFocus(mFab);
        }

        // Show/hide FAB
        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
            mFabIsShown = false;
        }
    }
    private void showStringgList(ArrayList<String> StringList) {
        mSelectedImagesContainer.removeAllViews();
        mSelectedImagesContainer.setVisibility(View.VISIBLE);

        int wdpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
        int htpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
        for (int i = 0; i < StringList.size(); i++) {
            final View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            final ImageView thumbnail = (ImageView) imageHolder.findViewById(R.id.media_image);
            Glide.with(this)
                    .load(StringList.get(i).toString())
                    .fitCenter()
                    .into(thumbnail);
            mSelectedImagesContainer.addView(imageHolder);
            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(wdpx, htpx));
        }
    }
}
