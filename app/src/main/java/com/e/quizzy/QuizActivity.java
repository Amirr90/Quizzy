package com.e.quizzy;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.e.quizzy.Models.Question;
import com.e.quizzy.Models.SelectedAns;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.omidh.liquidradiobutton.LiquidRadioButton;

public class QuizActivity extends AppCompatActivity {
    private static final String OPTION_A = "Option A: ";
    private static final String OPTION_B = "Option B: ";
    private static final String OPTION_C = "Option C: ";
    private static final String OPTION_D = "Option D: ";
    private static final String QUESTION = "Question: ";
    private static final String DONT_KNOW_ANSWER = "don't know answer??\nAsk to friend..";
    private static final String SHARE_TEXT = "Hey dear I stuck in a question, it will be Good if you help me out\n";
    private static final String SET_ID = "set_id";
    private static final String CAT_ID = "cat_id";
    private static final int TIMER = 30000;
    private static final int INTERVAL = 1000;
    private String set_id, cat_id;
    private Button btnNext;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private String TAG = "QuizActivity";
    List<Question> mList;
    List<SelectedAns> ansList;
    ArrayList<String> ansListString;
    private TextView mQuestion, mQuestionCount, mShareQuestion;
    int POSITION = 0;
    RadioGroup group;
    TextView showSelectedAns, timerText;
    LiquidRadioButton opA, opB, opC, opD;
    CountDownTimer myCountdownTimer;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViews();

        if (getIntent().hasExtra(SET_ID)) {
            set_id = getIntent().getStringExtra(SET_ID);
            cat_id = getIntent().getStringExtra(CAT_ID);
            Log.d("selected id : ", set_id);
            setToolbar(toolbar, cat_id);
            mList = new ArrayList<>();
            getQuestion();

            showQuiz(set_id);
        } else {
            onBackPressed();
            finish();
        }
        if (getIntent().hasExtra("from")) {
            setTimer(true);
        } else {
            setTimer(false);
        }


    }

    private void setTimer(boolean showBtn) {

        timerText.setVisibility(showBtn ? View.VISIBLE : View.GONE);
        if (showBtn) {
            myCountdownTimer = new CountDownTimer(TIMER, INTERVAL) {

                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / INTERVAL;
                    if (seconds < 10) {
                        timerText.setTextColor(getResources().getColor(R.color.red));
                        timerText.setText("00:0" + seconds);
                    } else {
                        timerText.setTextColor(getResources().getColor(R.color.colorbackground));
                        timerText.setText("00:" + seconds);
                    }

                    // Kick off your AsyncTask here.
                }

                public void onFinish() {

                    myCountdownTimer.cancel();
                    // the 30 seconds is up now so do make any checks you need here.
                    POSITION = POSITION + 1;
                    saveAnsToList("Time Out", mQuestion.getText().toString(), POSITION - 1);
                    ansListString.add("Time Out");
                    showNextQuestion(POSITION);
                    group.clearCheck();
                    myCountdownTimer.start();
                }
            }.start();
        }


    }

    private void setQuestion(int position, List<Question> mList) {
        int pos = POSITION + 1;
        if (mList.size() == POSITION) {
            btnNext.setEnabled(false);
            if (myCountdownTimer != null)
                myCountdownTimer.cancel();
            btnNext.setBackgroundResource(R.drawable.btn_corner);
            showResult();
            return;
        }

        if (!mList.get(position).getQuestion().equals("") && mList.get(position).getQuestion() != null) {
            String question = pos + ". " + mList.get(position).getQuestion();
            mQuestion.setText(question.replace("/n", "\n"));
        } else {
            mQuestion.setText(mList.get(position).getQuestionId());
        }

        //setting Options value
        try {
            opA.setText(mList.get(position).getOptionA());
            opB.setText(mList.get(position).getOptionB());
            opC.setText(mList.get(position).getOptionC());
            opD.setText(mList.get(position).getOptionD());
        } catch (Exception e) {
            Log.e(TAG, "error in setting option data: " + e.getLocalizedMessage());
        }


        String status = "" + pos + "/" + mList.size();
        mQuestionCount.setText(status);


    }

    private void showResult() {


        /*int points = 0;
        int percent;
        POSITION = 0;
        Toast.makeText( this, "Showing result", Toast.LENGTH_SHORT ).show();
        StringBuilder builder = new StringBuilder();

        for (int a = 0; a < ansList.size(); a++) {
            if (ansList.get( a ).getSelectedAns().equalsIgnoreCase( ansList.get( a ).getAnswer() )) {
                points++;
            }
            builder.append( "question: " + ansList.get( a ).getQuestion() + "\nselected ans: " + ansList.get( a ).getSelectedAns() );
            builder.append( "\ncorrect ans: " + ansList.get( a ).getAnswer() );
            builder.append( "\n-------\n" );
        }
        percent = ((points * 100) / ansList.size());
        builder.append( "\n\ncorrect: " + points + "/" + ansList.size() );
        builder.append( "\npercent: " + percent + "%" );

        showSelectedAns.setText( builder.toString() );


        AlertDialog.Builder dialog = new AlertDialog.Builder( QuizActivity.this );


        // get the layout inflater
        LayoutInflater inflater = QuizActivity.this.getLayoutInflater();

        // inflate and set the layout for the dialog
        // pass null as the parent view because its going in the dialog layout
        View view = View.inflate( this, R.layout.result_view, null );

        CircularProgressIndicator circularProgress = (CircularProgressIndicator) view.findViewById( R.id.circular_progress );
        Button share_btn=(Button)view.findViewById( R.id.btn_share ) ;

        circularProgress.setProgress( 5000, 10000 );// returns 5000/ returns 10000

        dialog.setView( inflater.inflate( R.layout.result_view, null ) )
                .setCancelable( false )
                .setPositiveButton( "Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        onBackPressed();
                        finish();
                    }
                } )
                .show();


        share_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( QuizActivity.this, "Sharing post", Toast.LENGTH_SHORT ).show();
            }
        } );*/

        startActivity(new Intent(QuizActivity.this, ResultActivity.class)
                .putStringArrayListExtra("ansList", ansListString)
                .putExtra("set_id", set_id)
                .putExtra("cat_id", cat_id));
        finish();
    }

    private void findViews() {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        ansList = new ArrayList<>();
        ansListString = new ArrayList<>();
        timerText = (TextView) findViewById(R.id.timer);
        btnNext = (Button) findViewById(R.id.btn_nxt);

        opA = (LiquidRadioButton) findViewById(R.id.optionA);
        opB = (LiquidRadioButton) findViewById(R.id.optionB);
        opC = (LiquidRadioButton) findViewById(R.id.optionC);
        opD = (LiquidRadioButton) findViewById(R.id.optionD);

        group = (RadioGroup) findViewById(R.id.radio_group);
        mQuestion = (TextView) findViewById(R.id.qst_txt);
        mQuestionCount = (TextView) findViewById(R.id.count_quest);
        mShareQuestion = (TextView) findViewById(R.id.share_question);

        mShareQuestion.setText(DONT_KNOW_ANSWER);
        mShareQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder builder = new StringBuilder();
                builder.append(SHARE_TEXT);
                builder.append(QUESTION + mQuestion.getText().toString() + "\n");
                builder.append(OPTION_A + opA.getText().toString() + "\n");
                builder.append(OPTION_B + opB.getText().toString() + "\n");
                builder.append(OPTION_C + opC.getText().toString() + "\n");
                builder.append(OPTION_D + opD.getText().toString() + "\n");

                Intent shareIntent = ShareCompat.IntentBuilder.from(QuizActivity.this)
                        .setType("text/plain")
                        .setText(builder.toString())
                        .getIntent();
                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(shareIntent);
                }
            }
        });

        // showSelectedAns = (TextView) findViewById( R.id.textView4 );


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selected_ans;
                int selectedRadioButtonID = group.getCheckedRadioButtonId();

                // If nothing is selected from Radio Group, then it return -1
                if (selectedRadioButtonID != -1) {
                    if (myCountdownTimer != null) {
                        myCountdownTimer.cancel();
                        setTimer(true);
                    }

                    switch (selectedRadioButtonID) {
                        case R.id.optionA:
                            POSITION = POSITION + 1;
                            selected_ans = opA.getText().toString();
                            saveAnsToList(selected_ans, mQuestion.getText().toString(), POSITION - 1);
                            ansListString.add(selected_ans);
                            showNextQuestion(POSITION);
                            group.clearCheck();
                            break;
                        case R.id.optionB:
                            POSITION = POSITION + 1;
                            selected_ans = opB.getText().toString();
                            saveAnsToList(selected_ans, mQuestion.getText().toString(), POSITION - 1);
                            ansListString.add(selected_ans);
                            showNextQuestion(POSITION);
                            group.clearCheck();
                            break;
                        case R.id.optionC:
                            POSITION = POSITION + 1;
                            selected_ans = opC.getText().toString();
                            saveAnsToList(selected_ans, mQuestion.getText().toString(), POSITION - 1);
                            ansListString.add(selected_ans);
                            showNextQuestion(POSITION);
                            group.clearCheck();
                            break;
                        case R.id.optionD:
                            POSITION = POSITION + 1;
                            selected_ans = opD.getText().toString();
                            saveAnsToList(selected_ans, mQuestion.getText().toString(), POSITION - 1);
                            ansListString.add(selected_ans);
                            showNextQuestion(POSITION);
                            group.clearCheck();
                            break;

                    }

                } else {
                    Toast.makeText(QuizActivity.this, "nothing selected", Toast.LENGTH_SHORT).show();
                }


            }


        });


    }


    private void showNextQuestion(int POSITION) {
        // alertCustomizedLayout();
        //selectedRadioButton.setChecked( false );
        setQuestion(POSITION, mList);
    }

    private void saveAnsToList(String selected_ans, String ques, int pos) {

        ansList.add(new SelectedAns(
                selected_ans,
                mList.get(pos).getAnswer(),
                ques,
                mList.get(pos).getQuestionId()
        ));
    }


    private void showQuiz(String mQuizId) {
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

    private void getQuestion() {

        final CollectionReference docRef = db.collection("Question");
        docRef.whereEqualTo("isActive", true)
                .whereEqualTo("setId", set_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Listen failed.", task.getException());
                            return;
                        }

                        QuerySnapshot snapshots = task.getResult();
                        if (snapshots != null && !snapshots.isEmpty()) {
                            mList.clear();
                            for (int a = 0; a < snapshots.size(); a++) {
                                try {
                                    String ques_id = (String) snapshots.getDocuments().get(a).getId();
                                    Log.d(TAG, " id: " + ques_id);
                                    Log.d(TAG, " id: " + snapshots.getDocuments());
                                    String question = (String) snapshots.getDocuments().get(a).get("question");
                                    String answer = (String) snapshots.getDocuments().get(a).get("answer");
                                    String category = (String) snapshots.getDocuments().get(a).get("queId");
                                    String opA = (String) snapshots.getDocuments().get(a).get("optionA");
                                    String opB = (String) snapshots.getDocuments().get(a).get("optionB");
                                    String opC = (String) snapshots.getDocuments().get(a).get("optionC");
                                    String opD = (String) snapshots.getDocuments().get(a).get("optionD");
                                    mList.add(new Question(set_id, ques_id, question, opA, opB, opC, opD, category, answer));
                                    setQuestion(POSITION, mList);
                                } catch (Exception e1) {
                                    Log.d(TAG, e1.getLocalizedMessage());
                                }
                            }
                        } else {
                            Log.d(TAG, "data: null");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void setData() {

        Map<String, Object> map = new HashMap<>();
        map.put("isActive", true);
        map.put("title", "");
        map.put("image", "");
        map.put("question", "");
        map.put("answer", "");
        map.put("optionA", "");
        map.put("optionB", "");
        map.put("optionC", "");
        map.put("optionD", "");
        map.put("view", 0);
        map.put("category", "general");
        map.put("setId", "3echu5HQKxG8ZUI35lra");

        for (int a = 0; a < 10; a++) {
            db.collection("CATEGORIES")
                    .document("GENERAL")
                    .collection("Sets")
                    .document("3echu5HQKxG8ZUI35lra")
                    .collection("Questions")
                    .document()
                    .set(map);
        }


    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {

            finish();

        } else {
            Toast.makeText(this, "Press back again to finish Quiz", Toast.LENGTH_LONG).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myCountdownTimer != null) {
            myCountdownTimer.cancel();
        }
    }

}
