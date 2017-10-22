package yapp.dev_diary.Detail;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;
import yapp.dev_diary.MainActivity;
import yapp.dev_diary.R;

/**
 * Created by HANSUNG on 2017-09-17.
 */

public class AdjustActivity extends BaseActivity implements ObservableScrollViewCallbacks {
    ArrayList<String> select_pic = new ArrayList<>();
    ArrayList<Uri> selectedUriList;
    private ViewGroup mSelectedImagesContainer;
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    public RequestManager mGlideRequestManager;
    private View mImageView;
    //    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private View mFab;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private boolean mFabIsShown;
    private TextView mTitleDate;
    private TextView mTitleDiary, mTitlePic;
    Uri selectedUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust);

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();

        mImageView = findViewById(R.id.image);
//        mOverlayView = findViewById(R.id.overlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText("제목을 입력하세요!");
        mTitleDate = (TextView) findViewById(R.id.title_date);
        mTitleDiary = (TextView) findViewById(R.id.title_diary);
        mTitlePic = (TextView) findViewById(R.id.title_pic);
        mTitleDiary.setText("오늘의\n일기_");
        mTitlePic.setText("오늘의\n사진_");
        mGlideRequestManager = Glide.with(this);
        mSelectedImagesContainer = (ViewGroup) findViewById(R.id.selected_photos_container);
        setMultiShowButton();
        Intent intent = getIntent();
        final int chk_num = intent.getExtras().getInt("chk_num");
        if(chk_num == 1){
            select_pic = MainActivity.ok_path;
        }else{
            select_pic.clear();
        }
        if(chk_num == 1){
            select_pic = MainActivity.ok_path;
        }else{
            select_pic.clear();
        }
        showStringgList(select_pic);
        setTitle(null);
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdjustActivity.this, "FAB is clicked", Toast.LENGTH_SHORT).show();
            }
        });
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
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

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

        ViewHelper.setTranslationY(mTitleDate, titleTranslationY);

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
        } else {
//            ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
            ViewHelper.setTranslationY(mFab, fabTranslationY-150);
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
    private void setMultiShowButton() {
        ImageButton btn_multi_show = (ImageButton) findViewById(R.id.btn_gall);
        btn_multi_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(AdjustActivity.this)
                                .setOnMultiImageSelectedListener(new TedBottomPicker.OnMultiImageSelectedListener() {
                                    @Override
                                    public void onImagesSelected(ArrayList<Uri> uriList) {
                                        selectedUriList = uriList;
                                        showUriList(uriList);
                                    }
                                })
                                //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                                .setPeekHeight(1600)
                                .showTitle(false)
                                .setCompleteButtonText("Done")
                                .setEmptySelectionText("No Select")
                                .setSelectedUriList(selectedUriList)
                                .setSelectMaxCount(5)
                                .create();
                        bottomSheetDialogFragment.show(getSupportFragmentManager());
                    }
                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(AdjustActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }
                };
                new TedPermission(AdjustActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });
    }
    private void showUriList(ArrayList<Uri> uriList) {
        // Remove all views before
        // adding the new ones.
        mSelectedImagesContainer.removeAllViews();
        mSelectedImagesContainer.setVisibility(View.VISIBLE);

        int wdpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
        int htpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
        for (Uri uri : uriList) {
            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            ImageView thumbnail = (ImageView) imageHolder.findViewById(R.id.media_image);
            Glide.with(this)
                    .load(uri.toString())
                    .fitCenter()
                    .into(thumbnail);
            mSelectedImagesContainer.addView(imageHolder);
            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(wdpx, htpx));
        }
    }

    private void showStringgList(ArrayList<String> StringList) {
        mSelectedImagesContainer.removeAllViews();
        mSelectedImagesContainer.setVisibility(View.VISIBLE);

        int wdpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
        int htpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
        for (int i = 0; i < StringList.size(); i++) {
            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            ImageView thumbnail = (ImageView) imageHolder.findViewById(R.id.media_image);
            Glide.with(this)
                    .load(StringList.get(i).toString())
                    .fitCenter()
                    .into(thumbnail);
            mSelectedImagesContainer.addView(imageHolder);
            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(wdpx, htpx));
        }
    }
}

