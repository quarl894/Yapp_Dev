package yapp.dev_diary.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by YoungJung on 2017-08-28.
 */

public class DBHelper extends SQLiteOpenHelper {
    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL("CREATE TABLE MYDIARY (_id INTEGER PRIMARY KEY, p_path TEXT, v_path TEXT, d_conn TEXT, weather INTEGER, feel INTEGER, title TEXT, date DATE, backup BOOLEAN);");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(int _id, String p_path, String v_path, String d_conn, int weather, int feel, String title, Date date) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO MYDIARY VALUES("+_id+",'" + p_path + "', '" + v_path+ "','"+d_conn +"','"+weather+",'" + feel+",'" + title+",'" + date+"');");
        db.close();
    }

    public void update(String english, String hanguel) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE MYDIARY SET english=" + english + " WHERE hanguel='" + hanguel + "';");
        db.close();
    }

    public void delete(String english) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM MYDIARY WHERE english='" + english + "';");
        db.close();
    }

    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM MYWORD", null);
        while (cursor.moveToNext()) {
            result += cursor.getInt(0)
                    + " : "
                    + cursor.getString(1)
                    +" /"
                    + cursor.getString(2)
                    +" /"
                    + cursor.getString(3)
                    +" /"
                    + cursor.getString(4)
                    +" /"
                    + cursor.getString(5)
                    + "\n";
        }
        return result;
    }
}
