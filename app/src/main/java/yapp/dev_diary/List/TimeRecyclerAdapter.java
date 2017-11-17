package yapp.dev_diary.List;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import yapp.dev_diary.DB.MyDBHelper;
import yapp.dev_diary.R;

public class TimeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<AdapterItem> itemList;
    private OnItemClickListener listener;
    private ArrayList<Integer> checkedList;

//    ArrayList<CheckBox> checkBoxes;

    private Context context;

    public static class TimeViewHolder extends RecyclerView.ViewHolder {
        public TextView timeItemView, tv_year, tv_size;

        public TimeViewHolder(View v) {
            super(v);
            timeItemView = (TextView) v.findViewById(R.id.timeItemView);
            tv_year = (TextView) v.findViewById(R.id.tv_year);
            tv_size = (TextView) v.findViewById(R.id.tv_size);
        }
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView timeView, nameView;
        public CheckBox cb;

        private OnViewHolderClickListener listener;

        public DataViewHolder(View v, OnViewHolderClickListener listener) {
            super(v);
            timeView = (TextView) v.findViewById(R.id.timeView);
            nameView = (TextView) v.findViewById(R.id.nameView);
            cb = (CheckBox) v.findViewById(R.id.cb_data);

            v.setOnClickListener(this);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if (listener != null)
                listener.onViewHolderClick(getPosition());
        }

        private interface OnViewHolderClickListener {
            void onViewHolderClick(int position);
        }
    }

    public TimeRecyclerAdapter(Context c, ArrayList<MyData> dataset) {
        context = c;
        itemList = initItemList(orderByTimeDesc(dataset));
        checkedList = new ArrayList<Integer>();
    }

    private ArrayList<AdapterItem> initItemList(ArrayList<MyData> dataset) {
//   ****  1. 리스트생성 : [initItemList]에서 new 함
//      *  2. 아이템 추가 : [initItemList]에서 CheckBoxes.add(null)로 아이템 수 만큼 추가함
//        checkBoxes = new ArrayList<CheckBox>();

        ArrayList<AdapterItem> result = new ArrayList<>();
        int year = 0, month = 0;
        for (MyData data : dataset) {
            if (year != data.getYear() || month != data.getMonth()) {
                result.add(new TimeItem(data.getYear(), data.getMonth(), data.getDayOfMonth()));
//                checkBoxes.add(null);
                year = data.getYear();
                month = data.getMonth();
            }
            result.add(data);
//            checkBoxes.add(null);
        }
        return result;
    }

    private ArrayList<MyData> orderByTimeDesc(ArrayList<MyData> dataset) {
        ArrayList<MyData> result = dataset;
        for (int i = 0; i < result.size() - 1; i++) {
            for (int j = 0; j < result.size() - i - 1; j++) {
                if (result.get(j).getTime() < result.get(j + 1).getTime()) {
                    MyData temp2 = result.remove(j + 1);
                    MyData temp1 = result.remove(j);
                    result.add(j, temp2);
                    result.add(j + 1, temp1);
                }
            }
        }
        return result;
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == AdapterItem.TYPE_TIME)
            return new TimeViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.recycler_item_time, parent, false));
        else
            return new DataViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.recycler_item_data, parent, false),
                    new DataViewHolder.OnViewHolderClickListener() {
                        @Override
                        public void onViewHolderClick(int position) {
                            if (listener != null)
                                listener.onItemClick(position);
                        }
                    }
            );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int pos = position;
        if (holder instanceof TimeViewHolder) {  // TimeViewHolder

            TimeViewHolder tHolder = (TimeViewHolder) holder;
            AdapterItem item = itemList.get(position);

            tHolder.timeItemView.setText(item.getTimeToString());
            tHolder.tv_year.setText(item.getYearToString());

            int count = 0;
            int year = item.getYear();
            int month = item.getMonth();
            int real_month = item.getMonth();
            month = year * 100 + month;
            String monthStr = Integer.toString(month);

            MyDBHelper DBHelper = new MyDBHelper(context);
            ArrayList<Integer> days = DBHelper.monthSelect(month, true);
            if (days != null) {
                count = days.size();
                tHolder.tv_size.setText("(" + count + "개의 저장된 일기)");
                tHolder.tv_year.setBackgroundResource(R.drawable.rectangle_5);
            } else {
                try {
                    checkedList.clear();
                    Log.e("여기 들어가나? finally", Integer.toString(checkedList.size()));
                } catch (Exception e) {
                    Log.e("여기서 에러나나?", itemList.get(position).getDateToString());
                }
            }
            //DataViewHolder
        } else {
            final DataViewHolder dHolder = (DataViewHolder) holder;
            dHolder.timeView.setText(itemList.get(position).getDateToString());
            dHolder.nameView.setText(
                    ((MyData) itemList.get(position))
                            .getName());
            if (ListDActivity.cb_check) {
                dHolder.cb.setVisibility(View.VISIBLE);
            } else {
                dHolder.cb.setVisibility(View.GONE);
                dHolder.cb.setChecked(false);
            }
            // 전체삭제 하는 곳
//            checkBoxes.set(pos, dHolder.cb);
            // 추가 3. 체크박스 설정
            ((MyData) itemList.get(position)).setCheckBox(dHolder.cb);
            Log.i("추가", ((MyData) itemList.get(position)).getCheckBox().toString() );

            if(ListDActivity.allChecked==true){
                checkAll(true);
            }else{
                checkAll(false);
            }


            dHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Log.e("is checked", " " + ((MyData) itemList.get(pos)).getCheckBox().toString() + " (" + pos + ") [" + buttonView.toString() + "] ");     //추가. 둘이 같아야 함
//                        Log.e("is checked", " " + checkBoxes.get(pos).toString() + " (" + pos + ") [" + buttonView.toString() + "] ");
//                        buttonView.getVerticalScrollbarPosition() // 0
                        checkedList.add(pos);   // 달라짐,,
                    } else {
                        Log.e("is unchecked", " " + pos); //itemList.get(pos).toString());
                        if(checkedList.size() !=0){
                            Log.e("position_111_off", " " + Integer.toString(checkedList.indexOf(pos)));
                            checkedList.remove(checkedList.indexOf(pos));
                            Log.e("position_remove_size", " " + Integer.toString(checkedList.size()));
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public MyData getItem(int position) {
        return (MyData) itemList.get(position);
    }

    public interface OnItemClickListener {
        public void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ArrayList<Integer> getCheckedList() {
        return checkedList;
    }

    public void deleteSelected(Context context) {
        MyDBHelper DBHelper;
        SQLiteDatabase db;
        DBHelper = new MyDBHelper(context);
        db = DBHelper.getWritableDatabase();
        AdapterItem tmpItem;

        try{
            int pos;
            for(int i = checkedList.size() - 1; 0 <= i ; i--){
                pos = checkedList.get(i);
                Log.i("# ", Integer.toString(pos));
                tmpItem = itemList.get(pos);

                DBHelper.delete(((MyData) tmpItem).getDBIndex());
                itemList.remove(pos);
//                checkBoxes.remove(pos);         // ****  4. 삭제 : [deleteSelected]에서 itemList.remove 할 때 체크박스도 같이 remove

                int m = tmpItem.getMonth();
                m += ( tmpItem.getYear() * 100 );
                Log.e("m ", Integer.toString(m) + " / pos : "  + pos);
                ArrayList<Integer> days = DBHelper.monthSelect(m, true);
                if(days==null){
                    Log.e("onCreateViewHolder", "해당 달에 일기 없음 [" + Integer.toString(m) + "]");
                    for (int k = 0; k < itemList.size(); k++) {
                        Log.e("여기서 고친다", Integer.toString(itemList.get(k).getMonth()));
                        int n = itemList.get(k).getMonth() + ( itemList.get(k).getYear() * 100 );
                        if (m == n) {
                            Log.e("여기서 지워져야된다", "ok" + Integer.toString(itemList.size()));
                            itemList.remove(k);
//                            checkBoxes.remove(k);       // **** 해당 달에 일기 없을 때 -> 년,월 아이템 지우면서 체크박스(null) 같이 삭제
                        }
                    }
                }
            }
            checkedList.clear();
            ListDActivity.allChecked = false;
        }catch (Exception e){
            Log.e("에러안나겠지","ㅎㅎ");
        }
    }

    public void checkAll(boolean to){
        Log.i("*** checkAll to", Boolean.toString(to));

        //추가
        for(AdapterItem item : itemList){
            if( item.getType() == 2 ){
                ((MyData)item).setChecked(to);
            }
        }

        // checkList 확인용
        String str = "";
        for(int ii : checkedList){
            str += ii;
            str += " ";
        }
        Log.i("#", str);
        Log.e("allcheckAll 확인 : ", " " +Boolean.toString(to));
        Log.e("itemList 확인", " " + Integer.toString(itemList.size()));
        for(int num =0; num<itemList.size(); num++){
            Log.e("확인 확인 : ", itemList.get(num).getDateToString());
        Log.e("checkedList 사이즈: "," "+Integer.toString(checkedList.size()) + " checkBoex 사이즈 : "+Integer.toString(checkBoxes.size()));
    }
}
