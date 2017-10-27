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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import yapp.dev_diary.DB.MyDBHelper;
import yapp.dev_diary.R;

public class TimeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<AdapterItem> itemList;
    private OnItemClickListener listener;
    private ArrayList<Integer> checkedList;

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
            if(listener != null)
                listener.onViewHolderClick(getPosition());
        }

        private interface OnViewHolderClickListener {
            void onViewHolderClick(int position);
        }
    }

    public TimeRecyclerAdapter(ArrayList<MyData> dataset) {
        itemList = initItemList(orderByTimeDesc(dataset));
        checkedList = new ArrayList<Integer>();
    }

    private ArrayList<AdapterItem> initItemList(ArrayList<MyData> dataset) {
        ArrayList<AdapterItem> result = new ArrayList<>();

        int year = 0, month = 0, dayOfMonth = 0;
        for(MyData data:dataset) {
            if(year != data.getYear() || month != data.getMonth() || dayOfMonth != data.getDayOfMonth()) {
                result.add(new TimeItem(data.getYear(), data.getMonth(), data.getDayOfMonth()));
                year = data.getYear();
                month = data.getMonth();
                dayOfMonth = data.getDayOfMonth();
            }
            result.add(data);
        }
        return result;
    }

    private ArrayList<MyData> orderByTimeDesc(ArrayList<MyData> dataset) {
        ArrayList<MyData> result = dataset;
        for(int i=0; i<result.size()-1; i++) {
            for(int j=0; j<result.size()-i-1; j++) {
                if(result.get(j).getTime() < result.get(j+1).getTime()) {
                    MyData temp2 = result.remove(j+1);
                    MyData temp1 = result.remove(j);
                    result.add(j, temp2);
                    result.add(j+1, temp1);
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
                            if(listener != null)
                                listener.onItemClick(position);
                        }
                    }
            );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int pos = position;
        if(holder instanceof TimeViewHolder) {
            TimeViewHolder tHolder = (TimeViewHolder) holder;
            tHolder.timeItemView.setText(itemList.get(position).getTimeToString());
            tHolder.tv_year.setText(itemList.get(position).getYearToString());
            tHolder.tv_size.setText("("+itemList.size()+"개의 저장된 일기)");
            tHolder.tv_year.setBackgroundResource(R.drawable.rectangle_5);

        } else {
            final DataViewHolder dHolder = (DataViewHolder) holder;
            dHolder.timeView.setText(itemList.get(position).getDateToString());
            dHolder.nameView.setText(
                    ((MyData)itemList.get(position))
                            .getName());

            dHolder.cb.setTag(itemList.get(position));
//            Log.i("position", Integer.toString(position) + " / " + dHolder.cb.getTag());
            Log.i("position", Integer.toString(position)  + " / " + ((MyData) itemList.get(position)).getName()+ " / " + itemList.get(position));

            dHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked == true)
                    {
//                        Log.i("checkbox", buttonView.getId() + " / " + buttonView.getParent());
                        Log.i("position", Integer.toString(pos) + " / " + buttonView.getTag());
                        checkedList.add(pos);
                    }
                    else if(isChecked == false)
                    {   checkedList.remove(checkedList.indexOf(pos));  }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public MyData getItem(int position) {
        return (MyData)itemList.get(position);
    }

    public interface OnItemClickListener {
        public void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ArrayList<Integer> getCheckedList(){ return checkedList; }

    public void deleteSelected(Context context){
        MyDBHelper DBHelper;
        SQLiteDatabase db;
        DBHelper = new MyDBHelper(context);
        db = DBHelper.getWritableDatabase();
        int pos;
        AdapterItem tmpItem;
        for(int i = checkedList.size()-1 ; 0 <= i ; i--)
        {
            pos = checkedList.get(i);
            checkedList.remove(i);
            tmpItem = itemList.get(pos);
            Log.i("DBIndex", Integer.toString(((MyData)tmpItem).getDBIndex()));
            DBHelper.delete( ((MyData)tmpItem).getDBIndex() );
            itemList.remove(pos);
        }
    }
}