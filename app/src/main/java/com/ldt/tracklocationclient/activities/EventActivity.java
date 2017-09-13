package com.ldt.tracklocationclient.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ldt.tracklocationclient.R;
import com.ldt.tracklocationclient.entities.EventEntity;
import com.ldt.tracklocationclient.utilities.DateHelper;
import com.ldt.tracklocationclient.utilities.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EventActivity extends AppCompatActivity {

    private final String TAG = EventActivity.class.getSimpleName();
    List<EventEntity> events = new ArrayList<>();

    @BindView(R.id.rvEvents)
    RecyclerView rvEvents;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ButterKnife.bind(this);

        mLayoutManager = new LinearLayoutManager(this);
        rvEvents.setLayoutManager(mLayoutManager);

        events.addAll(fakeData());
        // specify an adapter (see also next example)
        mAdapter = new EventActivity.MainAdapter(events);
        rvEvents.setAdapter(mAdapter);
    }

    private List<EventEntity> fakeData(){
        List<EventEntity> lstEvents = new ArrayList<>();
        EventEntity event1 = new EventEntity();
        event1.setSummary("Client Meeting");
        event1.setStartTime(1505271600000l);
        event1.setEndTime(1505275200000l);

        EventEntity event2 = new EventEntity();
        event2.setSummary("Team Meeting");
        event2.setStartTime(1505275200000l);
        event2.setEndTime(1505278800000l);

        EventEntity event3 = new EventEntity();
        event3.setSummary("Company Meeting");
        event3.setStartTime(1505293200000l);
        event3.setEndTime(1505296800000l);

        lstEvents.add(event1);
        lstEvents.add(event2);
        lstEvents.add(event3);
        return lstEvents;
    }

    class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<EventEntity> entryList;

        public MainAdapter(List<EventEntity> entryList) {
            this.entryList = entryList;
            Log.d(TAG, "MainAdapter: " + entryList.size());
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder: ");
            return new EventActivity.MainAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false));
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: ");
            EventActivity.MainAdapter.MyViewHolder myViewHolder = (EventActivity.MainAdapter.MyViewHolder) holder;
            EventEntity entry = entryList.get(position);

            myViewHolder.tvSummary.setText(entry.getSummary());
            myViewHolder.tvStart.setText("Start: " +DateHelper.dateToString(entry.getStartTime(), DateTimeFormat.Time));
            myViewHolder.tvEnd.setText("End: " + DateHelper.dateToString(entry.getEndTime(), DateTimeFormat.Time));

        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "getItemCount: " + entryList.size());
            return this.entryList == null ? 0 : this.entryList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.tvSummary)
            TextView tvSummary;

            @BindView(R.id.tvStart)
            TextView tvStart;

            @BindView(R.id.tvEnd)
            TextView tvEnd;

            public MyViewHolder(View itemView) {
                super(itemView);
                Log.d(TAG, "MyViewHolder: ");
                ButterKnife.bind(this, itemView);
            }


        }
    }
}
