package yapp.dev_diary.List;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import yapp.dev_diary.DB.DBHelper;
import yapp.dev_diary.DB.MyDBHelper;
import yapp.dev_diary.DB.MyItem;
import yapp.dev_diary.R;

/**
 * Created by seoheepark on 2017-10-29.
 */

public class SearchActivity  extends AppCompatActivity {
    public static SQLiteDatabase db;
    public static int count;
    public  static MyItem[] myItems;
    public SearchView searchview;
    MyDBHelper helper;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        helper = new MyDBHelper(this);
        //임시 디비
        helper.insert(new MyItem("s","hee","seohee",1,2,"바보",3,4));
        helper.insert(new MyItem("s","hee","멍청잉",1,2,"seohee",3,4));
        db= helper.getWritableDatabase();
        Toast toast = Toast.makeText(getApplicationContext(),
                "토스트창에 출력될 문자", Toast.LENGTH_LONG);

        toast.show();
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchview = (SearchView) menu.findItem(R.id.search).getActionView();
        searchview.setIconified(false);
        searchview.clearFocus();
        searchview.setQueryHint("검색어를 입력해주세요.");
        searchview.setOnQueryTextListener(queryTextListener);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if(null!=searchManager ) {
            searchview.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        return true;

    }
    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener(){
        @Override
        public boolean onQueryTextSubmit(String query) {
            ArrayList<MyItem> myItems = helper.searchSelect(query);
            for(MyItem mi : myItems){
                String log =  "내용:" +mi.getContent() + "제목" + mi.getTitle();
                Log.d("결과 ", log);
            }
            //검색한거 보여줌 ListDActivity에서 db로 리스트 가져오는 부분!!!! 가져다쓰기

            searchview.setQuery("",false);

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

}



