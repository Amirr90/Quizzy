package com.e.quizzy;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class SplashScreen extends AppCompatActivity {

    CountDownTimer myCountdownTimer;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


    }

    @Override
    protected void onRestart() {
        myCountdownTimer.start();
        super.onRestart();
    }

    private void sendToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myCountdownTimer != null) {
            myCountdownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        final ImageView img = (ImageView) findViewById(R.id.imageView4);
        final TextView textView = (TextView) findViewById(R.id.textView4);
        final Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        img.startAnimation(aniFade);
        textView.startAnimation(aniFade);

        db = FirebaseFirestore.getInstance();
        db.collection("SplashScreen")
                .document("data")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            final String image_url = (String) snapshot.get("image");
                            String title = (String) snapshot.get("title");
                            if (!image_url.equals("") && image_url != null)
                                Picasso.with(SplashScreen.this).load(image_url).into(img, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        img.setImageResource(R.drawable.phoo);
                                    }
                                });
                            else {
                                img.setImageResource(R.drawable.phoo);
                            }

                            textView.setText(title);

                        } else {
                            img.setImageResource(R.drawable.phoo);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                textView.setText("Be ready. Be smart. Be noticed!");
                img.setImageResource(R.drawable.phoo);

            }
        });

        myCountdownTimer = new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                sendToMainActivity();
            }
        }.start();
        super.onStart();
    }
}

