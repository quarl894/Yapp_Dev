package yapp.dev_diary.Calendar.utils;

import android.content.Context;

/**
 * Created by sunje on 2016-04-15.
 */
public class Common {
    public static float dp2px(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
