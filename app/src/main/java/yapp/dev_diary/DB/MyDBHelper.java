package yapp.dev_diary.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by seoheepark on 2017-09-23.
 */

public class MyDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "my_db";
    public static final String TABLE_NAME = "my_table";
    public MyDBHelper(Context context){
        super(context, DB_NAME, null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TALBE " + TABLE_NAME +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, index TEXT, photo_path TEXT, record_path TEXT, content TEXT, weather TEXT, mood TEXT, title TEXT, date TEXT, backup TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
