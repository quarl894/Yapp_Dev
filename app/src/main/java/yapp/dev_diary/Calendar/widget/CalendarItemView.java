package yapp.dev_diary.Calendar.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import yapp.dev_diary.Calendar.utils.Common;
import yapp.dev_diary.DB.MyDBHelper;
import yapp.dev_diary.DB.MyItem;
import yapp.dev_diary.R;

import static yapp.dev_diary.Calendar.Activity.MultiCalendarActivity.adapterHourLine;
import static yapp.dev_diary.Calendar.Activity.MultiCalendarActivity.calendar_Month_List;
import static yapp.dev_diary.Calendar.Activity.MultiCalendarActivity.itemList;


/**
 * Created by hnhariat on 2016-01-06.
 */
public class CalendarItemView extends View {

    Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint mPaintTextWhite = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint mPaintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint mPaintBackgroundToday = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint mPaintBackgroundEvent = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int dayOfWeek = -1;
    private boolean isStaticText = false;
    private long millis;
    private Rect rect;
    private boolean isTouchMode;
    private int dp11;
    private int dp16;
    private boolean hasEvent = false;
    private int[] mColorEvents;
    private final float RADIUS = 100f;
    public static ArrayList<String> tmp = new ArrayList<String>();

    public CalendarItemView(Context context) {
        super(context);
        initialize();
    }

    public CalendarItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        dp11 = (int) Common.dp2px(getContext(),11);
        dp16 = (int) Common.dp2px(getContext(),16);

        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(dp11);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaintTextWhite.setColor(Color.WHITE);
        mPaintTextWhite.setTextSize(dp11);
        mPaintTextWhite.setTextAlign(Paint.Align.CENTER);
        mPaintBackground.setColor(ContextCompat.getColor(getContext(), R.color.p_color1));
        mPaintBackgroundToday.setColor(ContextCompat.getColor(getContext(), R.color.p_color1));
        mPaintBackgroundEvent.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        setClickable(true);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                Log.d("hatti.onTouchEvent", event.getAction() + "");
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        //setBackgroundResource(R.drawable.selector_btn_rounded);
                        rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        isTouchMode = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isTouchMode) {
                            ((CalendarView) getParent()).setCurrentSelectedView(v);
                            isTouchMode = false;
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        isTouchMode = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
                            isTouchMode = false;
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(millis);

                MyDBHelper DBHelper = new MyDBHelper(getContext());
                SQLiteDatabase db = DBHelper.getWritableDatabase();
                String tmpStr = ""+calendar.get(Calendar.YEAR);

                if ((calendar.get(Calendar.MONTH))+1 < 10)
                    tmpStr += ("0"+calendar.get(Calendar.MONTH)+1);
                else
                    tmpStr += calendar.get(Calendar.MONTH)+1;

                if ((calendar.get(Calendar.DATE)) < 10)
                    tmpStr += ("0"+calendar.get(Calendar.DATE));
                else
                    tmpStr += calendar.get(Calendar.DATE);

                Log.d("체크", "확인용2 : "+calendar.get(Calendar.YEAR)+"."+calendar.get(Calendar.MONTH)+"."+calendar.get(Calendar.DATE)+','+tmpStr);
                int tmpInt = Integer.valueOf(tmpStr);
                ArrayList<MyItem> today_ItemList = DBHelper.calendarSelect(tmpInt);
                if (today_ItemList != null)
                {
                    itemList = today_ItemList;

                    for (int i = 0; i < today_ItemList.size(); i++)
                    {
                        tmp.add(today_ItemList.get(i).getTitle());
                    }
                    adapterHourLine.setList(tmp);
                }
                else
                {
                    itemList = null;
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add("일기를 추가해주세요");
                    adapterHourLine.setList(tmp);
                }

            }
        });
        setPadding(30, 0, 30, 0);
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((mPaint.descent() + mPaint.ascent()) / 2));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        MyDBHelper DBHelper = new MyDBHelper(getContext());

//        String strTmp = calendar.get(Calendar.YEAR) + "";
//
//        if (calendar.get(Calendar.MONTH)+1 < 10)
//            strTmp += "0"+(calendar.get(Calendar.MONTH)+1);
//        else
//            strTmp += (calendar.get(Calendar.MONTH)+1);
//
//        if (calendar.get(Calendar.DATE) < 10)
//            strTmp += "0"+calendar.get(Calendar.DATE);
//        else
//            strTmp += calendar.get(Calendar.DATE);
//        calendar_Month_List = DBHelper.monthSelect(Integer.valueOf(strTmp), false);
//        Log.d ("체크", "확이~ : "+strTmp+", ");
        CalendarView calendarView = (CalendarView) getParent();
        if (calendarView.getParent() instanceof ViewPager) {
            ViewGroup parent = (ViewPager) calendarView.getParent();
            CalendarItemView tagView = (CalendarItemView) parent.getTag();

            if (!isStaticText && tagView != null && tagView.getTag() != null && tagView.getTag() instanceof Long) {
                long millis = (long) tagView.getTag();
                if (isSameDay(millis, this.millis)) {
                    RectF rectF = new RectF(xPos - dp16, getHeight() / 2 - dp16, xPos + dp16, getHeight() / 2 + dp16);
                    canvas.drawRoundRect(rectF, RADIUS, RADIUS, mPaintBackground);
                }
            }
        }

        if (!isStaticText && isToday(millis)) {
            RectF rectF = new RectF(xPos - dp16, getHeight() / 2 - dp16, xPos + dp16, getHeight() / 2 + dp16);
            canvas.drawRoundRect(rectF, RADIUS, RADIUS, mPaintBackgroundToday);
        }
        if (isStaticText) {
            // 요일 표시
            canvas.drawText(CalendarView.DAY_OF_WEEK[dayOfWeek], xPos, yPos, mPaint);
        } else {
            // 날짜 표시
            if (isToday(millis)) {
                canvas.drawText(calendar.get(Calendar.DATE) + "", xPos, yPos, mPaintTextWhite);
            } else {
                canvas.drawText(calendar.get(Calendar.DATE) + "", xPos, yPos, mPaint);

                if (calendar_Month_List.containsKey(calendar.get(Calendar.DATE)))
                {
                    Paint paint = new Paint();
                    paint.setColor(Color.MAGENTA);
                    canvas.drawCircle(xPos, yPos+10, 5, paint);
                }
                else
                {
                    String strTmp = calendar.get(Calendar.YEAR) + "";

                    if (calendar.get(Calendar.MONTH)+1 < 10)
                        strTmp += "0"+(calendar.get(Calendar.MONTH)+1);
                    else
                        strTmp += (calendar.get(Calendar.MONTH)+1);

                    if (calendar.get(Calendar.DATE) < 10)
                        strTmp += "0"+calendar.get(Calendar.DATE);
                    else
                        strTmp += calendar.get(Calendar.DATE);
                    List<MyItem> tmpList = DBHelper.calendarSelect(Integer.valueOf(strTmp));

                    if (tmpList != null)
                    {
                        Paint paint = new Paint();
                        paint.setColor(Color.MAGENTA);
                        canvas.drawCircle(xPos, yPos+10, 5, paint);
                    }
                }

                //날짜 아래 원 표시
//                Paint paint = new Paint();
//                paint.setColor(Color.MAGENTA);
//                canvas.drawCircle(xPos, yPos+10, 5, paint);
            }
        }

        if (hasEvent) {
            mPaintBackgroundEvent.setColor(getResources().getColor(mColorEvents[0]));
            RectF rectF = new RectF(xPos - 5, getHeight() / 2 + 20, xPos + 5, getHeight() / 2 + 30);
            canvas.drawRoundRect(rectF, RADIUS, RADIUS, mPaintBackgroundToday);
        }

    }

    private boolean isToday(long millis) {
        Calendar cal1 = Calendar.getInstance();
        return isSameDay(cal1.getTimeInMillis(), millis);

    }

    public void setDate(long millis) {
        this.millis = millis;
        setTag(millis);
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        isStaticText = true;
    }

    public void setEvent(int... resid) {
        hasEvent = true;
        mColorEvents = resid;
    }
    public boolean isSameDay(long millis1, long millis2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(millis1);
        cal2.setTimeInMillis(millis2);
        Log.d("hatti.calendar", "" + cal1.get(Calendar.YEAR) + "" + cal1.get(Calendar.MONTH) + "" + cal1.get(Calendar.DATE) + " VS " +
                cal2.get(Calendar.YEAR) + "" + cal2.get(Calendar.MONTH) + "" + cal2.get(Calendar.DATE));
        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE));
    }

    public boolean isStaticText() {
        return isStaticText;
    }


}
