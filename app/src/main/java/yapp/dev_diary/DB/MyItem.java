package yapp.dev_diary.DB;

/**
 * Created by seoheepark on 2017-09-23.
 */

public class MyItem {
    private int _index;
    private String p_path;
    private String r_path;
    private String content;
    private int weather;
    private int mood;
    private String title;
    private int date;
    private int backup;


    public MyItem(){}

    // _index 있는 생성자
    public MyItem(int _index, String p_path, String r_path, String content, int weather, int mood, String title, int date, int backup) {
        this._index = _index;
        this.p_path = p_path;
        this.r_path = r_path;
        this.content = content;
        this.weather = weather;
        this.mood = mood;
        this.title = title;
        this.date = date;
        this.backup = backup;
    }


    public MyItem(int _index) {
        this._index = _index;
    }

    // _index 없는 생성자
    public MyItem(String p_path, String r_path, String content, int weather, int mood, String title, int date, int backup) {
        this.p_path = p_path;
        this.r_path = r_path;
        this.content = content;
        this.weather = weather;
        this.mood = mood;
        this.title = title;
        this.date = date;
        this.backup = backup;
    }


    public String getString(){
        String allToString = "p_path : " + p_path + ", r_path : " + r_path;
        allToString = allToString + ", content : " + content;
        allToString = allToString + ", weather : " + weather + ", mood : " + mood + ", title : " + title;
        allToString = allToString + ", date : " + date + ", backup : " + backup;
        return allToString;
    }

    public int get_Index() {
        return _index;
    }

    public void set_Index(int index) {
        this._index = index;
    }


    public String getP_path() {
        return p_path;
    }

    public void setP_path(String p_path) {
        this.p_path = p_path;
    }

    public String getR_path() {
        return r_path;
    }

    public void setR_path(String r_path) {
        this.r_path = r_path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getBackup() {
        return backup;
    }

    public void setBackup(int backup) {
        this.backup = backup;
    }
}
