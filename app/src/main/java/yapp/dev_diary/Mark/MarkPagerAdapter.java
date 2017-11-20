package yapp.dev_diary.Mark;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

import yapp.dev_diary.MainActivity;
import yapp.dev_diary.R;

public class MarkPagerAdapter extends PagerAdapter {
    private int mSize;

    public MarkPagerAdapter() {
        mSize = 4;
    }

    public MarkPagerAdapter(int count) {
        mSize = count;
    }

    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        ImageView imageView = new ImageView(view.getContext());
        switch (position){
            case 0:
                imageView.setImageResource(R.drawable.mark_img1);
                break;
            case 1:
                imageView.setImageResource(R.drawable.mark_img2);
                break;
            case 2:
                imageView.setImageResource(R.drawable.mark_img3);
                break;
            case 3:
                imageView.setImageResource(R.drawable.mark_img3);
                break;
        }
        view.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return imageView;
    }
}