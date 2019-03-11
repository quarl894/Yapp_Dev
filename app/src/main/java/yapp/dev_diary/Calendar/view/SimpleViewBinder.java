package yapp.dev_diary.Calendar.view;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Window;

import java.util.ArrayList;
import java.util.HashMap;

import yapp.dev_diary.Calendar.adapters.AdapterRCVBase;
import yapp.dev_diary.Calendar.adapters.AdapterRcvSimple;
import yapp.dev_diary.R;

/**
 * Created by sunje on 2016-04-21.
 */
public class SimpleViewBinder {
    public SimpleViewBinder() {

    }

    public static class RecyclerViewBuilder {
        private static final int LAYOUTMANAGER_LINEAR = 0;
        private static final int LAYOUTMANAGER_GRID = 1;
        private final Window window;
        private int layoutManager = LAYOUTMANAGER_LINEAR;
        private int idLayout = R.id.rcv;
        private int rawCount;
        private int orientation = LinearLayoutManager.VERTICAL;
        private ArrayList list;
        private HashMap map;
        private AdapterRCVBase adapter;
        private AdapterRCVBase.OnRCVItemListener onClickListener;

        public RecyclerViewBuilder(Window window) {
            this.window = window;
        }

        public RecyclerViewBuilder setLayout(int idLayout) {
            this.idLayout = idLayout;
            return this;
        }

        public RecyclerViewBuilder setLayoutManager(int layoutManager) {
            this.layoutManager = layoutManager;
            return this;
        }

        public RecyclerViewBuilder setRawCount(int rawCount) {
            this.rawCount = rawCount;
            return this;
        }

        public RecyclerViewBuilder setOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public RecyclerViewBuilder setList(ArrayList list) {
            this.list = new ArrayList();
            this.list.addAll(list);
            return this;
        }

        public RecyclerViewBuilder setMap(HashMap map) {
            this.map = new HashMap();
            this.map.putAll(map);
            return this;
        }

        public RecyclerViewBuilder setSimpleAdapter(FragmentManager fm) {
            adapter = new AdapterRcvSimple(R.layout.item_rcv_simple);
            return this;
        }

        public RecyclerViewBuilder setAdapter(AdapterRCVBase adapter, FragmentManager fm) {
            this.adapter = adapter;
            return this;
        }

        public RecyclerView build() {
            RecyclerView rcv = (RecyclerView) window.findViewById(idLayout);

            RecyclerView.LayoutManager lm = null;
            if (layoutManager == LAYOUTMANAGER_LINEAR) {
                lm = new LinearLayoutManager(window.getContext());
                ((LinearLayoutManager) lm).setOrientation(orientation);
            } else if (layoutManager == LAYOUTMANAGER_GRID) {
                lm = new GridLayoutManager(window.getContext(), rawCount);
            }
            rcv.setLayoutManager(lm);
            adapter.setList(list);
            adapter.setMap(map);
            rcv.setAdapter(adapter);
            return rcv;
        }

        public void setOnItemClickListener(AdapterRCVBase.OnRCVItemListener l) {
            onClickListener = l;
            adapter.setOnItemClickListener(l);
        }

        public AdapterRCVBase getAdapter() {
            return adapter;
        }
    }
}
