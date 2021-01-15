package com.e.quizzy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e.quizzy.Models.StatsModel;
import com.e.quizzy.utility.TimeAgo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatsActivity extends AppCompatActivity {
    String uid;
    RecyclerView recyclerView;
    List<StatsModel> statsModelList;
    private TextView mResultCount;
    private TimeAgo timeAgo;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mResultCount = (TextView) findViewById(R.id.result_count);


        timeAgo = new TimeAgo();
        if (getIntent().hasExtra("uid")) {
            uid = getIntent().getStringExtra("uid");
            setToolbar(toolbar, "Result");
            showStatsData(uid);
        } else {
            Toast.makeText(this, "No stats found", Toast.LENGTH_SHORT).show();
        }

    }


    private void setToolbar(Toolbar toolbar, String id) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(id);
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showStatsData(String uid) {
        recyclerView = (RecyclerView) findViewById(R.id.stats_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        statsModelList = new ArrayList<>();
        StatsAdapter adapter = new StatsAdapter(statsModelList, this);
        recyclerView.setAdapter(adapter);
        loadData(statsModelList, adapter);

    }

    private void loadData(final List<StatsModel> statsModelList, final StatsAdapter adapter) {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PLAYED_QUIZ")
                .whereEqualTo("uid", uid)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            return;
                        }
                        mResultCount.setText(task.getResult().size() + " Results");
                        for (int a = 0; a < task.getResult().size(); a++) {
                            final String id = task.getResult().getDocuments().get(a).getId();
                            final long timestamp = (Long) task.getResult().getDocuments().get(a).get("timeStamp");
                            final String status = (String) task.getResult().getDocuments().get(a).get("result");
                            final String title = (String) task.getResult().getDocuments().get(a).get("title");
                            statsModelList.add(new StatsModel(title, status, id, timestamp));
                            adapter.notifyDataSetChanged();

                        }
                    }
                });

    }

    private class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.MyViewHolder> {

        List<StatsModel> statsModelList;
        Context context;

        public StatsAdapter(List<StatsModel> statsModelList, Context context) {
            this.statsModelList = statsModelList;
            this.context = context;
        }

        @NonNull
        @Override
        public StatsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stats_view, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StatsAdapter.MyViewHolder myViewHolder, final int i) {
            myViewHolder.title.setText(statsModelList.get(i).getTitle());
            String status = statsModelList.get(i).getStatus();
            long time = statsModelList.get(i).getTimestamp();
            String MyFinalValue = timeAgo.getlongtoago(time);
            myViewHolder.mTimeStamp_tv.setText(MyFinalValue);
            if (status.equals("Pass")) {
                myViewHolder.status.setText("Pass");
                myViewHolder.status.setTextColor(getResources().getColor(R.color.green));

            } else {
                myViewHolder.status.setText("Fail");
                myViewHolder.status.setTextColor(getResources().getColor(R.color.red));
            }

            myViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String QUIZ_ID = statsModelList.get(i).getId();
                    context.startActivity(new Intent(context, ShowPlayedQuizActivity.class)
                            .putExtra("QUIZ_ID", QUIZ_ID));

                }
            });

        }

        @Override
        public int getItemCount() {
            return statsModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private CircleImageView imageView;
            private TextView title, status, mTimeStamp_tv;
            private RelativeLayout layout;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                imageView = (CircleImageView) itemView.findViewById(R.id.imageView2);
                title = (TextView) itemView.findViewById(R.id.stats_title);
                status = (TextView) itemView.findViewById(R.id.stats_status);
                mTimeStamp_tv = (TextView) itemView.findViewById(R.id.just_now);
                layout = (RelativeLayout) itemView.findViewById(R.id.stats_lay4);
            }
        }
    }
}
