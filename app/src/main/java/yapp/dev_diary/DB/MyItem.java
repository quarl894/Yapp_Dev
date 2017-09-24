package yapp.dev_diary.DB;

/**
 * Created by seoheepark on 2017-09-23.
 */

public class MyItem {
    public int _index;
    public String p_path;
    public String r_path;
    public String content;
    public int weather;
    public int mood;
    public String title;
    public int date;
    public int backup;

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

    public MyItem() {
    }

    public MyItem(int _index) {
        this._index = _index;
    }

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
