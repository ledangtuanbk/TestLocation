package com.ldt.tracklocationclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldt.tracklocationclient.R;
import com.ldt.tracklocationclient.controllers.UserController;
import com.ldt.tracklocationclient.entities.ResponseEntity;
import com.ldt.tracklocationclient.entities.TestUserEntity;
import com.ldt.tracklocationclient.interfaces.IResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListUserActivity extends AppCompatActivity {

    private static final String TAG = ListUserActivity.class.getSimpleName();
    @BindView(R.id.rvUser)
    RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<TestUserEntity> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);


        UserController<List<TestUserEntity>> controller = new UserController<>();
        controller.getAllUsers(new IResponse<List<TestUserEntity>>() {
            @Override
            public void onResponse(ResponseEntity<List<TestUserEntity>> response) {
                Log.d(TAG, "onResponse: ");
                if (response != null) {
                    Log.d(TAG, "onResponse: " + response.getData().size());
                    datas.clear();
                    datas.addAll(response.getData());
                    Log.d(TAG, "onResponse: " + datas.size());
                    mAdapter.notifyDataSetChanged();
                } else Log.d(TAG, "onResponse: null");
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

        // specify an adapter (see also next example)
        mAdapter = new MainAdapter(datas);
        recyclerView.setAdapter(mAdapter);
    }

    class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<TestUserEntity> entryList;

        public MainAdapter(List<TestUserEntity> entryList) {
            this.entryList = entryList;
            Log.d(TAG, "MainAdapter: " + entryList.size());
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder: ");
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false));
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: ");
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            TestUserEntity entry = entryList.get(position);
            myViewHolder.tvName.setText(entry.getUserId());

        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "getItemCount: " + entryList.size());
            return this.entryList == null ? 0 : this.entryList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final String TAG = MyViewHolder.class.getSimpleName();

            @BindView(R.id.ivAvatar)
            ImageView ivAvatar;

            @BindView(R.id.tvName)
            TextView tvName;

            public MyViewHolder(View itemView) {
                super(itemView);
                Log.d(TAG, "MyViewHolder: ");
                ButterKnife.bind(this, itemView);
            }

            @OnClick(R.id.llItem)
            public void onClick(View view) {
                Intent intent = new Intent(ListUserActivity.this, UserConfigViewActivity.class);
                intent.putExtra(getResources().getString(R.string.userId), datas.get(getAdapterPosition()).getUserId());
                startActivity(intent);
            }
        }
    }
}
