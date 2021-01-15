package com.e.quizzy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e.quizzy.Adapter.HomeAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowPlayedQuizActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<DocumentSnapshot> snapshots;
    FirebaseFirestore db;
    public static String PLAYED_QUIZ = "PLAYED_QUIZ";
    public static String QUIZ_DATA = "QUIZ_DATA";
    public static String QUIZ_ID;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_played_quiz);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbar(toolbar, "Result");

        recyclerView = (RecyclerView) findViewById(R.id.resultrecview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        db = FirebaseFirestore.getInstance();

        snapshots=new ArrayList<>();

        if (getIntent().hasExtra("QUIZ_ID")) {
            QUIZ_ID = getIntent().getStringExtra("QUIZ_ID");
            ResultAdapter adapter = new ResultAdapter(snapshots, this);
            recyclerView.setAdapter(adapter);
            loadData(adapter);
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

    private void loadData(final ResultAdapter adapter) {
        db.collection(PLAYED_QUIZ).document(QUIZ_ID).collection(QUIZ_DATA)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null) {
                            progressBar.setVisibility(View.GONE);
                            snapshots.addAll(task.getResult().getDocuments());
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(ShowPlayedQuizActivity.this, "No result found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHolder> {
        List<DocumentSnapshot> snapshotList;
        Context context;

        public ResultAdapter(List<DocumentSnapshot> snapshotList, Context context) {
            this.snapshotList = snapshotList;
            this.context = context;
        }

        @NonNull
        @Override
        public ResultAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.answer_view, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ResultAdapter.MyViewHolder myViewHolder, int i) {

            int position;
            position = i + 1;
            String ans = snapshotList.get(i).getString("answer");
            String selected_ans = snapshotList.get(i).getString("selected_ans");
            String que = snapshotList.get(i).getString("question");

            myViewHolder.question.setText("Que " + position + ": " + que);
            myViewHolder.selected_ans.setText("Selected Ans: " + selected_ans);
            myViewHolder.answer.setText("Correct Ans: " + ans);


            if (selected_ans.equalsIgnoreCase(ans)) {
                myViewHolder.colorLay.setBackgroundColor(getResources().getColor(R.color.transparent_green));
                myViewHolder.answer.setVisibility(View.GONE);
            } else {
                myViewHolder.colorLay.setBackgroundColor(getResources().getColor(R.color.transparent_red));
                myViewHolder.answer.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return snapshotList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView answer, selected_ans, question;
            private RelativeLayout colorLay;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                question = (TextView) itemView.findViewById(R.id.textView11);
                selected_ans = (TextView) itemView.findViewById(R.id.textView10);
                answer = (TextView) itemView.findViewById(R.id.textView9);

                colorLay = (RelativeLayout) itemView.findViewById(R.id.colorLay);
            }
        }
    }
}
