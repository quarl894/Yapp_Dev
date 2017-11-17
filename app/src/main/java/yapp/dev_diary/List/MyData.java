package yapp.dev_diary.List;

import android.widget.CheckBox;

public class MyData extends AdapterItem {
    private String name;
    private int DBIndex;
    private CheckBox cb = null; //추가

    public MyData(String name, long time, int DBIndex) {
        super(time);
        this.name = name;
        this.DBIndex = DBIndex;
    }

    public MyData(String name, int year, int month, int dayOfMonth, int DBIndex) {
        super(year, month, dayOfMonth);
        this.name = name;
        this.DBIndex = DBIndex;
    }

    @Override
    public int getType() {
        return TYPE_DATA;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDBIndex() { return DBIndex; }

    // 추가
    public void setCheckBox(CheckBox cb) { this.cb = cb; }
    public CheckBox getCheckBox() { return this.cb; }
    public void setChecked(boolean to) { this.cb.setChecked(to); }
}
