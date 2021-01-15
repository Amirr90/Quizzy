package com.e.quizzy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e.quizzy.Models.Banner;
import com.e.quizzy.Models.Categories;
import com.e.quizzy.Models.item;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.kcode.bottomlib.BottomDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    HomeAdapter0 adapter;
    List<Categories> category;
    List<Categories> random;
    List<Categories> titleList;
    List<Categories> getEducationList;
    List<Banner> bannerList;
    private String TAG = "status :";
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Toolbar toolbar;
    private TextView more, btnLogin;
    private CircleImageView mProfileImage;
    List<item> items;

    RelativeLayout mLoadingLayout;
    private LinearLayout mRecyclerLayout;
    List<Banner> mSliderBannerList;
    private long backPressedTime;
    int a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        toolbar = (Toolbar) findViewById( R.id.home_toolbar );

        //setData();

        findViews();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //setting All Category
        setCategory();


        //Checking logged in User
        checkLogin( currentUser );


        getSliderBannerData();

        subscribeToTopic();


    }

    private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("newSetAdded")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                    }
                });


        FirebaseMessaging.getInstance().subscribeToTopic("notification")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                    }
                });
    }

    private void getSliderBannerData() {


        //adding Banner Data
        final CollectionReference docRef = db.collection( "SliderBanner" );
        docRef.whereEqualTo( "isActive", true )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w( TAG, "Listen failed.", e );
                            return;
                        }
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            mSliderBannerList.clear();
                            for (int a = 0; a < queryDocumentSnapshots.size(); a++) {
                                try {
                                    String sliderBannerId = (String) queryDocumentSnapshots.getDocuments().get( a ).getId();
                                    String image_url = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "image" );
                                    String title = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "title" );
                                    String description = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "description" );
                                    String setId = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "setId" );

                                    //Log.d( TAG, " data: " + (long) queryDocumentSnapshots.getDocuments().get( a ).get( "question" ) + "  " + id );
                                    mSliderBannerList.add( new Banner( title, image_url, sliderBannerId, description, setId ) );
                                } catch (Exception e1) {
                                    Log.d( TAG, e.getLocalizedMessage() );
                                }
                            }

                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d( TAG, "data: null" );
                        }
                    }
                } );
    }

    private void setVisibility(boolean visibility) {
        mLoadingLayout.setVisibility( visibility ? View.GONE : View.VISIBLE );
        mRecyclerLayout.setVisibility( visibility ? View.VISIBLE : View.GONE );
    }

    private void setData() {
        List<String> sub_name = new ArrayList<>();
        sub_name.add( "CBSE Class 10 Biology" );
        sub_name.add( "CBSE Class 10 Chemistry" );
        sub_name.add( "CBSE Class 10 Physics" );
        sub_name.add( "CBSE Class 10 Maths " );


        for (int a = 0; a < sub_name.size(); a++) {
            Map<String, Object> map = new HashMap<>();
            map.put( "image", "" );
            map.put( "isActive", true );
            map.put( "question", 123 );
            map.put( "title", sub_name.get( a ) );

            db.collection( "Education Quiz" )
                    .document()
                    .set( map );
        }


    }

    private void findViews() {


        mRecyclerLayout = (LinearLayout) findViewById( R.id.recLinLay );
        mLoadingLayout = (RelativeLayout) findViewById( R.id.loading_layout );
        more = (TextView) toolbar.findViewById( R.id.more );
        btnLogin = (TextView) toolbar.findViewById( R.id.login_text );
        mProfileImage = (CircleImageView) toolbar.findViewById( R.id.tool_profile_image );

        btnLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginScreen();
            }
        } );

        mProfileImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUser = mAuth.getCurrentUser();
                if (checkUserLoginStatus( currentUser ))
                    startActivity( new Intent( MainActivity.this, ShowProfile.class ) );

            }
        } );


        more.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        } );

    }

    private void showBottomDialog() {
        final String shareText = "Share Quizzy App to get Rs10 PAYTM cash\nhttps://play.google.com/store/apps/details?id=com.e.quizzy";
        BottomDialog dialog = BottomDialog.newInstance( "more" +
                "", "cancel", new String[]{"Share App", "Rate us"} );
        dialog.show( getSupportFragmentManager(), "QUIZZY" );
        //add item click listener
        dialog.setListener( new BottomDialog.OnClickListener() {
            @Override
            public void click(int position) {
                switch (position) {
                    case 0:
                        Intent shareIntent = ShareCompat.IntentBuilder.from( MainActivity.this )
                                .setType( "text/plain" )
                                .setText( shareText )
                                .getIntent();
                        if (shareIntent.resolveActivity( getPackageManager() ) != null) {
                            startActivity( shareIntent );
                        }
                }
            }
        } );
    }

    public void loggedOut() {

        AuthUI.getInstance()
                .signOut( this )
                .addOnCompleteListener( new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText( MainActivity.this, "Signed out successfully", Toast.LENGTH_SHORT ).show();
                        btnLogin.setVisibility( View.VISIBLE );
                        mProfileImage.setVisibility( View.GONE );
                        showLoginScreen();
                    }
                } );
    }

    private void checkLogin(FirebaseUser currentUser) {
        if (checkUserLoginStatus( currentUser )) {
            showUserInformation();

        } else {

            btnLogin.setVisibility( View.VISIBLE );
            mProfileImage.setVisibility( View.GONE );

        }
    }

    private void showLoginScreen() {
        ProgressDialog dialog = new ProgressDialog( this );
        dialog.setTitle( "Loading" );
        dialog.setMessage( "Please wait" );
        dialog.setCancelable( false );
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build() );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders( providers )
                        /*.setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html" )*/
                        .setLogo(R.drawable.logo)      // Set logo drawable
                        .setTheme( R.style.AppTheme_NoActionBar )
                        .build(),
                10 );
    }

    private boolean checkUserLoginStatus(FirebaseUser currentUser) {
        if (currentUser == null) {
            return false;
        } else {
            return true;
        }
    }

    private void setCategory() {
        recyclerView = (RecyclerView) findViewById( R.id.rv_parent );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        category = new ArrayList<>();
        random = new ArrayList<>();
        titleList = new ArrayList<>();
        getEducationList = new ArrayList<>();
        bannerList = new ArrayList<>();
        mSliderBannerList = new ArrayList<>();
        adapter = new HomeAdapter0( titleList, category, random, bannerList, mSliderBannerList, getEducationList, this );
        recyclerView.setAdapter( adapter );

        loadData( adapter );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == 10) {
            IdpResponse response = IdpResponse.fromResultIntent( data );

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Toast.makeText( this, "sign in successfully", Toast.LENGTH_SHORT ).show();

                    checkAlreadyUserStatus( user );

                }

            } else {
                Toast.makeText( this, "Sign in failed", Toast.LENGTH_SHORT ).show();
                btnLogin.setVisibility( View.VISIBLE );
                mProfileImage.setVisibility( View.GONE );
            }
        }
    }

    private void checkAlreadyUserStatus(final FirebaseUser user) {
        final boolean status;
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection( "Users" ).document( user.getUid() );
        docIdRef.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d( TAG, "Document exists!" );

                        showUserInformation();
                    } else {
                        Log.d( TAG, "Document does not exist!" );
                        updateUser( user );
                    }
                } else {
                    Log.d( TAG, "Failed with: ", task.getException() );
                }
            }
        } );


    }

    private void updateUser(FirebaseUser User) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();

        user.put( "uid", User.getUid() );
        user.put( "isBlocked", false );
        user.put( "name", User.getDisplayName() );
        user.put( "profile", User.getPhotoUrl() != null ? User.getPhotoUrl().toString() : null );
        user.put( "number", User.getPhoneNumber() );
        user.put( "email", User.getEmail() );
        user.put( "city", "" );
        user.put( "about", "" );
        user.put( "win_percent", 0 );
        user.put( "win", 0 );
        user.put( "loss", 0 );
        user.put( "loss_percent", 0 );
        user.put( "played", 0 );
        // Add a new document with a generated ID
        db.collection( "Users" )
                .document( User.getUid() )
                .set( user )
                .addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText( MainActivity.this, "", Toast.LENGTH_SHORT ).show();
                            Log.d( TAG, "true" );
                            showUserInformation();
                        }

                    }

                    private void showUserInformation() {
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d( TAG, "false" );
                Toast.makeText( MainActivity.this, "information not updated due to " + e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    private void showUserInformation() {

        currentUser = mAuth.getCurrentUser();
        btnLogin.setVisibility( View.GONE );
        mProfileImage.setVisibility( View.VISIBLE );
        final DocumentReference docRef = db.collection( "Users" )
                .document( currentUser.getUid() );
        docRef.addSnapshotListener( new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w( TAG, "Listen failed.", e );
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d( TAG, "Current data: " + snapshot.getData() );
                    if (snapshot.getString( "profile" ) != null && !snapshot.getString( "profile" ).equals( "" )) {
                        String profileUrl = snapshot.getString( "profile" );
                        Picasso.with( MainActivity.this ).load( profileUrl ).placeholder( R.drawable.profile ).into( mProfileImage );
                    }
                } else {
                    Log.d( TAG, "Current data: null" );
                }
            }
        } );

    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser = mAuth.getCurrentUser();

        checkLogin( currentUser );
    }

    private void loadData(final HomeAdapter0 adapter) {

        items = new ArrayList<>();
        //adding Title

        final CollectionReference docRef = db.collection( "CATEGORY HEADER" );
        docRef.whereEqualTo( "isActive", true )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w( TAG, "Listen failed.", e );
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            titleList.clear();
                            for (int a = 0; a < queryDocumentSnapshots.size(); a++) {
                                try {
                                    long viewType = (Long) queryDocumentSnapshots.getDocuments().get( a ).get( "viewType" );
                                    String title = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "title" );

                                    titleList.add( new Categories( title, viewType ) );
                                } catch (Exception e1) {
                                    Log.d( TAG, e1.getLocalizedMessage() );
                                }
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d( TAG, "data: null" );
                        }
                    }
                } );


        //adding CATEGORIES
        final CollectionReference titleRef = db.collection( "CATEGORIES" );
        titleRef.whereEqualTo( "isActive", true )
                .whereEqualTo( "showOnHomePage", true )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w( TAG, "Listen failed.", e );
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            category.clear();
                            for (int a = 0; a < queryDocumentSnapshots.size(); a++) {
                                try {
                                    long questions = 0;
                                    String id = (String) queryDocumentSnapshots.getDocuments().get( a ).getId();
                                    String image_url = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "image" );
                                    String title = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "title" );
                                    long sets = (Long) queryDocumentSnapshots.getDocuments().get( a ).get( "sets" );
                                    Log.d( TAG, " data: " + (long) queryDocumentSnapshots.getDocuments().get( a ).get( "question" ) + "  " + id );


                                    if ((Long) queryDocumentSnapshots.getDocuments().get( a ).get( "question" ) != null) {
                                        questions = Long.parseLong( queryDocumentSnapshots.getDocuments().get( a ).get( "question" ).toString() );
                                    }
                                    category.add( new Categories( id, questions, image_url, title, sets ) );
                                } catch (Exception e1) {
                                    Log.d( TAG, e1.getLocalizedMessage() );
                                }
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d( TAG, "data: null" );
                        }
                    }
                } );


        //adding RANDOM QUIZ
        final CollectionReference random_Ref = db.collection( "RANDOM QUIZ" );
        random_Ref.whereEqualTo( "isActive", true )
                .whereEqualTo( "showOnHomePage", true )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w( TAG, "Listen failed.", e );
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            random.clear();
                            for (int a = 0; a < queryDocumentSnapshots.size(); a++) {
                                try {
                                    String id = (String) queryDocumentSnapshots.getDocuments().get( a ).getId();
                                    String image_url = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "image" );
                                    String title = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "title" );
                                    Log.d( TAG, " data: " + (long) queryDocumentSnapshots.getDocuments().get( a ).get( "question" ) + "  " + id );
                                    long questions = 0;
                                    long sets = (Long) queryDocumentSnapshots.getDocuments().get( a ).get( "sets" );
                                    if ((Long) queryDocumentSnapshots.getDocuments().get( a ).get( "question" ) != null) {
                                        questions = Long.parseLong( queryDocumentSnapshots.getDocuments().get( a ).get( "question" ).toString() );
                                    }

                                    random.add( new Categories( id, questions, image_url, title, sets ) );
                                } catch (Exception e1) {
                                    Log.d( TAG, e1.getLocalizedMessage() );
                                }
                            }
                            setVisibility( true );
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d( TAG, "data: null" );
                        }
                    }
                } );


        //adding banner data
        final CollectionReference banner_Ref = db.collection( "Banner" );
        banner_Ref.whereEqualTo( "isActive", true )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w( TAG, "Listen failed.", e );
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            bannerList.clear();
                            for (int a = 0; a < queryDocumentSnapshots.size(); a++) {
                                try {

                                    String id = (String) queryDocumentSnapshots.getDocuments().get( a ).getId();
                                    String image_url = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "image" );
                                    String title = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "title" );
                                    String desc = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "description" );
                                    String setId = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "setId" );

                                    bannerList.add( new Banner( title, image_url, id, desc, setId ) );
                                } catch (Exception e1) {
                                    Log.d( TAG, e.getLocalizedMessage() );
                                }
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d( TAG, "data: null" );
                        }
                    }
                } );


        //adding education data
        final CollectionReference edu_Ref = db.collection( "Education Quiz" );
        edu_Ref.whereEqualTo( "isActive", true )
                .whereEqualTo( "showOnHomePage", true )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w( TAG, "Edu Listen failed.", e );
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            getEducationList.clear();
                            for (int a = 0; a < queryDocumentSnapshots.size(); a++) {
                                try {
                                    String id = (String) queryDocumentSnapshots.getDocuments().get( a ).getId();
                                    String image_url = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "image" );
                                    String title = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "title" );
                                    long sets = (Long) queryDocumentSnapshots.getDocuments().get( a ).get( "sets" );
                                    Log.d( TAG, " data: " + queryDocumentSnapshots.getDocuments() );
                                    getEducationList.add( new Categories( id, 10, image_url, title, sets ) );
                                } catch (Exception e1) {
                                    Log.d( TAG, e1.getLocalizedMessage() );
                                }
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d( TAG, "data: null" );
                        }
                    }
                } );

    }

    private void setData2(final List<String> sub_name, final List<String> chap_name, String category, final String cat_id) {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (a = 0; a < sub_name.size(); a++) {

            final Map<String, Object> map = new HashMap<>();
            map.put( "isActive", true );
            map.put( "category", "EDUCATION_QUESTION" );
            map.put( "title", sub_name.get( a ) );
            map.put( "image", "" );
            map.put( "isNew", false );
            map.put( "questions", 10 );
            map.put( "description", "" );
            map.put( "chap_name", chap_name.get( a ) );
            map.put( "difficulty", new Random().nextInt( 3 ) );
            db.collection( "Sets" )
                    .add( map )
                    .addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String set_id = documentReference.getId();
                            updateSetId( set_id );
                            setNewQuestion( set_id );

                        }

                        private void setNewQuestion(String set_id) {
                            for (int b = 0; b < chap_name.size(); b++) {
                                final Map<String, Object> map = new HashMap<>();
                                map.put( "isActive", true );
                                map.put( "title", chap_name.get( b ) );
                                map.put( "image", "" );
                                map.put( "question", "" );
                                map.put( "answer", "" );
                                map.put( "optionA", "" );
                                map.put( "optionB", "" );
                                map.put( "optionC", "" );
                                map.put( "optionD", "" );
                                map.put( "isNew", false );
                                map.put( "view", 0 );
                                map.put( "category", "EDUCATION_QUESTION" );
                                map.put( "setId", set_id );
                                map.put( "setId", chap_name.get( b ) );

                                db.collection( "Question" )
                                        .add( map )
                                        .addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                String que_id = documentReference.getId();
                                                updateQuestionId( que_id );
                                            }

                                            private void updateQuestionId(String que_id) {
                                                final Map<String, Object> map = new HashMap<>();
                                                map.put( "queId", que_id );
                                                db.collection( "Question" )
                                                        .document( que_id )
                                                        .update( map );
                                            }
                                        } );
                            }
                        }

                        private void updateSetId(String set_id) {
                            final Map<String, Object> map = new HashMap<>();
                            map.put( "setId", set_id );
                            db.collection( "Sets" )
                                    .document( set_id )
                                    .update( map );

                        }
                    } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            } );

        }


    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {

            finish();

        } else {
            Toast.makeText( this, "Press back again to close", Toast.LENGTH_LONG ).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}



