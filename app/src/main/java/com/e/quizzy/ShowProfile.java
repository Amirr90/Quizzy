package com.e.quizzy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


public class ShowProfile extends AppCompatActivity {

    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private String TAG = "ShowProfile";
    private TextView mUserEmail, mUserCity, mUserMobile, mUserAbout;
    private TextView played, mPass, mFail, mPassCount, mFailCount;
    private LinearLayout mStatsLayout;
    public int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        findViews();

        if (currentUser != null) {
            setToolbar(toolbar, currentUser.getDisplayName());
            setUserData();
            setPlayedQuiz();
            setPassAndFailPercent();
        }

        mStatsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null) {
                    startActivity(new Intent(ShowProfile.this, StatsActivity.class)
                            .putExtra("uid", currentUser.getUid()));
                }
            }
        });
    }

    private void setPassAndFailPercent() {
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("PLAYED_QUIZ")
                    .whereEqualTo("uid", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            QuerySnapshot snapshot = task.getResult();
                            int totalQuiz = snapshot.size();
                            int pass_count = 0;
                            int fail_count = 0;
                            for (int a = 0; a < snapshot.size(); a++) {
                                String status = (String) snapshot.getDocuments().get(a).get("result");
                                if (status.equalsIgnoreCase("Pass")) {
                                    pass_count++;
                                } else {
                                    fail_count++;
                                }

                            }

                            if (pass_count != 0) {
                                int percent;
                                percent = (pass_count * 100) / totalQuiz;
                                mPass.setText(percent + "%");
                                mPassCount.setText(pass_count + "");
                            } else {
                                mPass.setText("0%");
                                mPassCount.setText("0");
                            }

                            if (fail_count != 0) {
                                int percent;
                                percent = (fail_count * 100) / totalQuiz;
                                mFail.setText(percent + "%");
                                mFailCount.setText(fail_count + "");
                            } else {
                                mFail.setText("0%");
                                mFailCount.setText("0");
                            }


                        }
                    });
        }
    }

    private void setPlayedQuiz() {
        db.collection("PLAYED_QUIZ")
                .whereEqualTo("uid", currentUser.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            played.setText("0");
                            return;
                        }
                        int played_count = queryDocumentSnapshots.size();
                        played.setText("" + played_count);
                    }
                });
    }

    private void setUserData() {
        currentUser = mAuth.getCurrentUser();
        db.collection("Users")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(ShowProfile.this, "can't update user detail", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        DocumentSnapshot snapshot = task.getResult();
                        String email = (String) snapshot.get("email");
                        String about = (String) snapshot.get("about");
                        String city = (String) snapshot.get("city");
                        if (email != null && !email.equals("")) {
                            mUserEmail.setText("e-mail: " + email);
                        } else {
                            mUserEmail.setText("email: -----");
                        }

                        if (!about.equals("") && about != null) {
                            mUserAbout.setText(about);
                        } else {
                            mUserAbout.setText("-----");
                        }

                        if (city != null && !city.equals("")) {
                            mUserCity.setText("city: " + city);
                        } else {
                            mUserCity.setText("city: -----");
                        }


                    }
                });

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

    private void findViews() {

        mStatsLayout = (LinearLayout) findViewById(R.id.stats_lay);
        db = FirebaseFirestore.getInstance();
        mUserCity = (TextView) findViewById(R.id.user_city);
        mUserEmail = (TextView) findViewById(R.id.user_email);
        mUserAbout = (TextView) findViewById(R.id.user_about);
        mUserMobile = (TextView) findViewById(R.id.user_number);
        played = (TextView) findViewById(R.id.played_count);
        mPass = (TextView) findViewById(R.id.pass);
        mFail = (TextView) findViewById(R.id.fail);
        mPassCount = (TextView) findViewById(R.id.pass_count);
        mFailCount = (TextView) findViewById(R.id.fail_count);


    }


    public void loggedOut(final View view) {

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Snackbar.make(view, "Signed out successfully", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        startActivity(new Intent(ShowProfile.this, MainActivity.class).putExtra("loggedOut", "loggedOut"));
                        finish();

                    }
                });
    }


}
