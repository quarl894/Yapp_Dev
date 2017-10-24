package yapp.dev_diary.List;

public class MyData extends AdapterItem {
    private String name;
    private int DBIndex;

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
}
