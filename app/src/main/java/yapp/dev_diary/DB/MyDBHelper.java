package yapp.dev_diary.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by seoheepark on 2017-09-23.
 */

public class MyDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "my_db";
    public static final String TABLE_NAME = "RECORD_TABLE";
    public static final String CREATE_TABLE = "create table "
            + TABLE_NAME + "(_index INTEGER PRIMARY KEY AUTOINCREMENT, p_path TEXT, r_path TEXT, content TEXT, weather INTEGER, mood INTEGER, title TEXT, date INTEGER, backup INTEGER);";

    public MyDBHelper(Context context){
        super(context, DB_NAME, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {//ok
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
    public int insert(MyItem myItem) {//ok
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("p_path", myItem.getP_path());
        values.put("r_path", myItem.getR_path());
        values.put("content", myItem.getContent());
        values.put("weather", myItem.getWeather());
        values.put("mood", myItem.getMood());
        values.put("title", myItem.getTitle());
        values.put("date", myItem.getDate());
        values.put("backup", myItem.getBackup());
        long rowID = db.insert(TABLE_NAME, null, values);
        Log.i("db.insert()", "rowID : " + rowID);
        // DB에 입력한 값으로 행 추가
        db.close();
        return (int)rowID;
    }

    public void update(MyItem myItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("p_path", myItem.getP_path());
        values.put("r_path", myItem.getR_path());
        values.put("content", myItem.getContent());
        values.put("weather", myItem.getWeather());
        values.put("mood", myItem.getMood());
        values.put("title", myItem.getTitle());
        values.put("date", myItem.getDate());
        values.put("backup", myItem.getBackup());
        // 입력한 항목과 일치하는 행의 가격 정보 수정

        db.update(TABLE_NAME, values, "_index = ?", new String[] { String.valueOf(myItem.get_Index()) });
        db.close();
    }

    public void delete(int _index) {//ok
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM RECORD_TABLE WHERE _index=" + _index+";");
        db.close();
    }
    public MyItem oneSelect(int _index){ //ok
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] { "_index",

                        "p_path", "r_path","content","weather","mood","title","date" ,"backup"}, "_index = ?",

                new String[] { String.valueOf(_index) }, null, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();
        MyItem myItem = new MyItem(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)), cursor.getString(6),Integer.parseInt(cursor.getString(7)), Integer.parseInt(cursor.getString(8)) );
        return  myItem;
   }

    public List<MyItem> allSelect() {//모든 필드 반환(수정할 때& 상세보기 사용) ok
        List<MyItem> myItems = new ArrayList<MyItem>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM RECORD_TABLE", null);
        while (cursor.moveToNext()) {
            MyItem myItem = new MyItem();
            myItem.set_Index(cursor.getInt(0));
            myItem.setP_path(cursor.getString(1));
            myItem.setR_path(cursor.getString(2));
            myItem.setContent(cursor.getString(3));
            myItem.setWeather(cursor.getInt(4));
            myItem.setMood(cursor.getInt(5));
            myItem.setTitle(cursor.getString(6));
            myItem.setDate(cursor.getInt(7));
            myItem.setBackup(cursor.getInt(8));
            myItems.add(myItem);
        }
        cursor.close();
        db.close();
        return myItems;
    }

    /* 해당 날짜로 select된 행들을 리스트로 반환. 없으면 null 반환 */
    public ArrayList<MyItem> calendarSelect(int date){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<MyItem> list = null;
        Cursor cursor = db.rawQuery("SELECT * FROM RECORD_TABLE WHERE date = "+date+";", null);

        if(cursor != null && cursor.getCount() > 0) {
            Log.i("calendarSelect", "cursor.getCount() : " + cursor.getCount());
            list = new ArrayList<MyItem>();
            while (cursor.moveToNext()) {
                int _index = cursor.getInt(0);
                String p_path = cursor.getString(1);
                String r_path = cursor.getString(2);
                String content = cursor.getString(3);
                int weather = cursor.getInt(4);
                int mood = cursor.getInt(5);
                String title = cursor.getString(6);
                date = cursor.getInt(7);
                int backup = cursor.getInt(8);

                MyItem item = new MyItem(_index, p_path, r_path, content, weather, mood, title, date, backup);
                list.add(item);
            }
            cursor.close();
        }
        db.close();
        return list;
    }

    /* 해당 월에 일기가 있는 날짜 리스트로 반환. 없으면 null 반환.
     * 파라미터
      * 1) input : YYYYMM 형식으로 주세요. e.g. 201710
      * 2) dup   : 결과 중복 허용? true 허용. false 비허용 */
    public ArrayList<Integer> monthSelect(int input, boolean dup){
        ArrayList<Integer> days = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT date FROM RECORD_TABLE WHERE date/100 == " + input + ";", null);
        if( cursor != null && cursor.getCount() > 0 )
        {
            days = new ArrayList<Integer>();
            while (cursor.moveToNext()){
                int itemDay = cursor.getInt(0) % 100;
                if( dup ) days.add(itemDay);         // 중복 허용
                else if( !days.contains(itemDay) )  //중복 비허용
                    days.add(itemDay);
            }
        }
        db.close();
        return days;
    }

    public ArrayList<MyItem> searchSelect(String word){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<MyItem> list = new ArrayList<MyItem>();

        Cursor cursor = db.rawQuery("SELECT * from RECORD_TABLE WHERE title LIKE '%" + word+"%' or content LIKE '%"+ word+"%'", null);
        while (cursor.moveToNext()) {
            int _index = cursor.getInt(0);
            String p_path = cursor.getString(1);
            String r_path = cursor.getString(2);
            String content = cursor.getString(3);
            int weather = cursor.getInt(4);
            int mood = cursor.getInt(5);
            String title = cursor.getString(6);
            int date = cursor.getInt(7);
            int backup = cursor.getInt(8);

            MyItem item = new MyItem(_index, p_path,r_path, content, weather, mood, title, date, backup);
            list.add(item);
        }
        cursor.close();
        db.close();
        return list;
    }

}
