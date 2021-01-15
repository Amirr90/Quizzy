package com.e.quizzy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e.quizzy.Models.AnsList;
import com.e.quizzy.Models.Question;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class ResultActivity extends AppCompatActivity {
    ArrayList<String> arrayList;
    private String set_id, cat_id;
    CircularProgressIndicator circularProgress;
    Button btn_showAns, btn_share;
    CardView cardView;
    RelativeLayout mLoadingLayout;
    AnsAdapter ansAdapter;
    List<AnsList> ansLists;

    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private String TAG = "ResultActivity";
    List<Question> mList;
    private TextView mCorrect, mIncorrect, mTotal, mResultStatus;
    RecyclerView recyclerView;
    boolean isRecShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbar(toolbar, "Result");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        cardView = (CardView) findViewById(R.id.res_cardView);
        mLoadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
        circularProgress = (CircularProgressIndicator) findViewById(R.id.circular_progress);
        btn_showAns = (Button) findViewById(R.id.btn_share);
        btn_share = (Button) findViewById(R.id.btn_shareResult);
        mCorrect = (TextView) findViewById(R.id.correct_num);
        mIncorrect = (TextView) findViewById(R.id.in_correct_number);
        mTotal = (TextView) findViewById(R.id.total_number);
        mResultStatus = (TextView) findViewById(R.id.result_status);


        ansLists = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.ans_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        if (getIntent().hasExtra("ansList")) {
            arrayList = getIntent().getStringArrayListExtra("ansList");
            set_id = getIntent().getStringExtra("set_id");
            cat_id = getIntent().getStringExtra("cat_id");
            getDataFromServer(arrayList, set_id);

            showLoadingLayout(true);
            currentUser = mAuth.getCurrentUser();
            checkUserAndUpload(currentUser);

        }

        btn_showAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isRecShowing) {
                    btn_showAns.setText("SHOW ANSWER");
                    isRecShowing = false;
                    recyclerView.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    btn_showAns.setText("HIDE ANSWER");
                    isRecShowing = true;
                    if (mList.size() == arrayList.size()) {
                        ansLists.clear();
                        showAns();
                    }
                }
            }
        });


    }

    private void showAns() {
        ansAdapter = new AnsAdapter(ansLists, this);
        recyclerView.setAdapter(ansAdapter);
        loadData(ansAdapter);


    }

    public void shareResult(View view) {

    }

    private void checkUserAndUpload(final FirebaseUser currentUser) {
        final int points;
        if (mCorrect.getText() != null && !mCorrect.getText().equals("")) {
            points = Integer.parseInt(mCorrect.getText().toString());
        } else {
            points = 0;
        }

        if (currentUser != null) {

            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            DocumentReference docIdRef = rootRef.collection("SCORE")
                    .document(currentUser.getUid());
            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "User exists!");
                            updateScore(points, true, currentUser);
                            // showUserInformation();

                        } else {
                            Log.d(TAG, "User does not exist!");
                            // updateUser( user );
                            updateScore(points, false, currentUser);
                        }
                    } else {
                        Log.d(TAG, "Failed with: ", task.getException());
                    }
                }
            });
        } else {
            showLoginScreen();
        }
    }

    private void showLoginScreen() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html")
                        //.setLogo(R.drawable.logoo)      // Set logo drawable
                        .setTheme(R.style.AppTheme_NoActionBar)
                        .build(),
                10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Toast.makeText(this, "sign in successfully", Toast.LENGTH_SHORT).show();

                    checkAlreadyUserStatus(user);


                }

            } else {
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void checkAlreadyUserStatus(final FirebaseUser user) {
        final boolean status;
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("Users").document(user.getUid());
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Document exists!");

                        //showLoadingLayout( false );
                        Toast.makeText(ResultActivity.this, "Sharing Result", Toast.LENGTH_SHORT).show();
                        checkUserAndUpload(user);
                    } else {
                        Log.d(TAG, "Document does not exist!");
                        updateUser(user);
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });


    }

    private void updateUser(final FirebaseUser User) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();

        user.put("uid", User.getUid());
        user.put("isBlocked", false);
        user.put("name", User.getDisplayName());
        user.put("profile", User.getPhotoUrl() != null ? User.getPhotoUrl().toString() : null);
        user.put("number", User.getPhoneNumber());
        user.put("email", User.getEmail());
        user.put("city", "");
        user.put("about", "");
        user.put("win_percent", 0);
        user.put("win", 0);
        user.put("loss", 0);
        user.put("loss_percent", 0);
        user.put("played", 0);

        // Add a new document with a generated ID
        db.collection("Users")
                .document(User.getUid())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //showLoadingLayout( false );
                            Log.d(TAG, "true");
                            Toast.makeText(ResultActivity.this, "Sharing Result", Toast.LENGTH_SHORT).show();
                            checkUserAndUpload(User);
                        }

                    }


                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showLoadingLayout(false);
                Log.d(TAG, "false");
                Toast.makeText(ResultActivity.this, "information not updated due to " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateScore(final long points, boolean isAlreadyUser, final FirebaseUser currentUser) {
        final Map<String, Object> map = new HashMap<>();
        map.put("isActive", true);


        if (!isAlreadyUser) {
            map.put("score", points);
            map.put("payed_quiz", 1);
            db.collection("SCORE")
                    .document(currentUser.getUid())
                    .set(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ResultActivity.this, "Result share successfully", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ResultActivity.this, "not shared", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            db.collection("SCORE")
                    .document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                long score = (Long) snapshot.get("score");
                                score = score + points;

                                long played = (Long) snapshot.get("payed_quiz");
                                played = played + 1;
                                map.put("score", score);
                                map.put("played_quiz", played);
                                db.collection("SCORE")
                                        .document(currentUser.getUid())
                                        .update(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                showLoadingLayout(false);
                                                Toast.makeText(ResultActivity.this, "Result share successfully", Toast.LENGTH_SHORT).show();
                                                btn_share.setText("SHARED");
                                                btn_share.setEnabled(false);
                                                setResultToDataBase(currentUser);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showLoadingLayout(false);
                                        Toast.makeText(ResultActivity.this, "not shared", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showLoadingLayout(false);
                    Toast.makeText(ResultActivity.this, "cant get score", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void loadData(AnsAdapter ansAdapter) {

        for (int a = 0; a < mList.size(); a++) {
            ansLists.add(new AnsList(
                    mList.get(a).getAnswer(),
                    mList.get(a).getQuestion(),
                    arrayList.get(a)));
        }
        ansAdapter.notifyDataSetChanged();
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

    private void getDataFromServer(final ArrayList<String> arrayList, final String set_id) {
        mList = new ArrayList<>();
        final CollectionReference docRef = db.collection("Question");
        docRef.whereEqualTo("isActive", true)
                .whereEqualTo("setId", set_id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            showLoadingLayout(false);
                            mList.clear();
                            for (int a = 0; a < queryDocumentSnapshots.size(); a++) {
                                try {
                                    String ques_id = (String) queryDocumentSnapshots.getDocuments().get(a).getId();
                                    Log.d(TAG, " id: " + ques_id);
                                    Log.d(TAG, " id: " + queryDocumentSnapshots.getDocuments());
                                    String question = (String) queryDocumentSnapshots.getDocuments().get(a).get("question");
                                    String answer = (String) queryDocumentSnapshots.getDocuments().get(a).get("answer");
                                    String category = (String) queryDocumentSnapshots.getDocuments().get(a).get("queId");
                                    String opA = (String) queryDocumentSnapshots.getDocuments().get(a).get("optionA");
                                    String opB = (String) queryDocumentSnapshots.getDocuments().get(a).get("optionB");
                                    String opC = (String) queryDocumentSnapshots.getDocuments().get(a).get("optionC");
                                    String opD = (String) queryDocumentSnapshots.getDocuments().get(a).get("optionD");
                                    mList.add(new Question(set_id, ques_id, question, opA, opB, opC, opD, category, answer));
                                    setResult(mList, arrayList);
                                } catch (Exception e1) {
                                    Log.d(TAG, e1.getLocalizedMessage());


                                }
                            }


                        } else {
                            Log.d(TAG, "data: null");
                        }
                    }
                });


    }

    private void showLoadingLayout(boolean visibility) {
        mLoadingLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
        cardView.setVisibility(visibility ? View.GONE : View.VISIBLE);
    }

    public void setResultToDataBase(FirebaseUser currentUser) {
        String passResult;
        if (mCorrect.getText() != null) {
            int points = Integer.parseInt(mCorrect.getText().toString());
            int percent = ((points * 100) / mList.size());
            if (percent >= 60) {
                passResult = "Pass";
                //mResultStatus.setTextColor( getResources().getColor( R.color.green ) );
            } else {
                passResult = "Fail";
                //mResultStatus.setTextColor( getResources().getColor( R.color.red ) );
            }

            // mResultStatus.setText( passResult );
            Map<String, Object> map = new HashMap<>();
            map.put("uid", currentUser.getUid());
            map.put("setId", set_id);
            map.put("timeStamp", System.currentTimeMillis());
            map.put("title", cat_id);
            map.put("points", mCorrect.getText().toString());
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                map.put("email", currentUser.getEmail());
            }
            map.put("result", passResult);

            this.currentUser = mAuth.getCurrentUser();
            db.collection("PLAYED_QUIZ")
                    .add(map)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String ref_id = documentReference.getId();
                            for (int a = 0; a < mList.size(); a++) {
                                ansLists.add(new AnsList(
                                        mList.get(a).getAnswer(),
                                        mList.get(a).getQuestion(),
                                        arrayList.get(a)));
                            }
                            for (int a = 0; a < ansLists.size(); a++) {
                                db.collection("PLAYED_QUIZ")
                                        .document(ref_id)
                                        .collection("QUIZ_DATA")
                                        .add(ansLists.get(a));
                            }
                            // Toast.makeText( ResultActivity.this, "Ok", Toast.LENGTH_SHORT ).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ResultActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void setResult(List<Question> mlist, ArrayList<String> arrayList) {
        int correct_ques = 0;
        int incorrect_ques;
        String passResult;
        int percent;
        if (mlist.size() == arrayList.size()) {
            for (int a = 0; a < mlist.size(); a++) {
                if (arrayList.get(a).equalsIgnoreCase(mlist.get(a).getAnswer())) {
                    correct_ques++;
                }
            }

            incorrect_ques = mlist.size() - correct_ques;
            percent = ((correct_ques * 100) / mlist.size());

            circularProgress.setProgress(percent, 100);
            circularProgress.setTextSizeSp(18);
            mCorrect.setText("" + correct_ques);
            mIncorrect.setText("" + incorrect_ques);
            mTotal.setText("" + mlist.size());

            if (correct_ques > incorrect_ques) {
                //circularProgress.setProgressBackgroundColor( getResources().getColor( R.color.green ) );
                circularProgress.setTextColor(getResources().getColor(R.color.green));
                mResultStatus.setTextColor(getResources().getColor(R.color.green));
                mResultStatus.setText("Pass");
            } else {
                // circularProgress.setProgressBackgroundColor( getResources().getColor( R.color.red ) );
                circularProgress.setTextColor(getResources().getColor(R.color.red));
                mResultStatus.setTextColor(getResources().getColor(R.color.red));
                mResultStatus.setText("Fail");
            }

        }
    }

    private class AnsAdapter extends RecyclerView.Adapter<AnsAdapter.MyViewHolder> {

        List<AnsList> ansLists;
        Context context;

        public AnsAdapter(List<AnsList> ansLists, Context context) {
            this.ansLists = ansLists;
            this.context = context;
        }

        @NonNull
        @Override
        public AnsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.answer_view, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AnsAdapter.MyViewHolder myViewHolder, int i) {
            int position;
            position = i + 1;
            String ans = ansLists.get(i).getAnswer();
            String selected_ans = ansLists.get(i).getSelected_ans();
            String que = ansLists.get(i).getQuestion();

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
            return ansLists.size();
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
