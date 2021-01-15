package com.e.quizzy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e.quizzy.Models.SubCategories;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowCategory extends AppCompatActivity {

    private String cat_title;
    private RecyclerView recyclerView;
    CategoryAdapter adapter;
    List<SubCategories> data;
    FirebaseFirestore db;
    private String TAG = "ShowCategory";
    RelativeLayout mNoContetLayout;
    TextView resulCountCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_show_category );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        mNoContetLayout = (RelativeLayout) findViewById( R.id.no_data_layout );
        resulCountCat = (TextView) findViewById( R.id.result_count_cat );


        db = FirebaseFirestore.getInstance();


        if (getIntent().hasExtra( "cat_title" )) {
            cat_title = getIntent().getStringExtra( "cat_title" );
            Log.d( "selected cat id : ", cat_title );
            setToolbar( toolbar, cat_title );
            showCategory( cat_title );
        } else {
            onBackPressed();
            finish();
        }
    }




    private void setToolbar(Toolbar toolbar, String id) {
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setDisplayShowHomeEnabled( true );
        getSupportActionBar().setTitle( id );
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showCategory(String cat_id) {
        recyclerView = (RecyclerView) findViewById( R.id.show_cateory_rec );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        data = new ArrayList<>();
        adapter = new CategoryAdapter( data, this );
        recyclerView.setAdapter( adapter );

        loadData( adapter, cat_id );

    }

    private void loadData(final CategoryAdapter adapter, String cat_id) {

        final CollectionReference docRef = db.collection( "Sets" );
        docRef.whereEqualTo( "isActive", true )
                .whereEqualTo( "category", cat_id )
                .orderBy( "timestamp", Query.Direction.ASCENDING )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            resulCountCat.setText( "0 Results" );
                            Log.w( TAG, "Listen failed.", e );
                            showNoDataLayout( true );
                            Toast.makeText( ShowCategory.this, "Listen Failed: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            data.clear();
                            showNoDataLayout( false );
                            resulCountCat.setText( queryDocumentSnapshots.size() + " Results" );
                            for (int a = 0; a < queryDocumentSnapshots.size(); a++) {
                                try {
                                    String id = (String) queryDocumentSnapshots.getDocuments().get( a ).getId();
                                    Log.d( TAG, " id: " + id );
                                    Log.d( TAG, " id: " + queryDocumentSnapshots.getDocuments() );
                                    String title = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "title" );
                                    String desc = (String) queryDocumentSnapshots.getDocuments().get( a ).get( "description" );
                                    long diff = (Long) queryDocumentSnapshots.getDocuments().get( a ).get( "difficulty" );
                                    long ques = (Long) queryDocumentSnapshots.getDocuments().get( a ).get( "questions" );
                                    long played = (Long) queryDocumentSnapshots.getDocuments().get( a ).get( "played" );
                                    long timestamp = (Long) queryDocumentSnapshots.getDocuments().get( a ).get( "timestamp" );
                                    boolean isNew = (Boolean) queryDocumentSnapshots.getDocuments().get( a ).get( "isNew" );
                                    data.add( new SubCategories( title, id, desc, diff, ques, timestamp, isNew, played ) );
                                } catch (Exception e1) {
                                    Log.d( TAG, e1.getLocalizedMessage() );


                                }
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d( TAG, "data: null" );
                            showNoDataLayout( true );
                        }

                    }
                } );
    }

    private void showNoDataLayout(boolean value) {
        mNoContetLayout.setVisibility( value ? View.VISIBLE : View.GONE );
        recyclerView.setVisibility( value ? View.GONE : View.VISIBLE );

    }


    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {


        List<SubCategories> data;
        Context context;

        public CategoryAdapter(List<SubCategories> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @NonNull
        @Override
        public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.category_view, viewGroup, false );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.MyViewHolder myViewHolder, final int i) {
            // try {
            if (!data.get( i ).getTitle().equals( "" ))
                myViewHolder.title.setText( data.get( i ).getTitle() );
            else {
                myViewHolder.title.setText( data.get( i ).getId() );
            }
            myViewHolder.isNew.setVisibility( data.get( i ).isNew() ? View.VISIBLE : View.GONE );
            myViewHolder.desc.setText( data.get( i ).getDescription() );
            myViewHolder.playedTimes.setText( data.get( i ).getPlayed()+"\nTimes" );

            int difficulty = (int) data.get( i ).getDifficulty();
            switch (difficulty) {
                case 0:
                    myViewHolder.diff.setText( "Easy" );
                    myViewHolder.diff_image.setImageResource( R.drawable.easy );
                    break;
                case 1:
                    myViewHolder.diff.setText( "Medium" );
                    myViewHolder.diff_image.setImageResource( R.drawable.medium );
                    break;
                case 2:
                    myViewHolder.diff.setText( "Hard" );
                    myViewHolder.diff_image.setImageResource( R.drawable.hard );
                    break;


            }

            myViewHolder.mStartBtn.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String set_id = data.get( i ).getId();
                    context.startActivity( new Intent( context, QuizActivity.class )
                            .putExtra( "cat_id", data.get( i ).getTitle() )
                            .putExtra( "set_id", set_id ) );
                }
            } );

            setQuestionCount( myViewHolder, i );

        }


        private String millisecondsToTime(long milliseconds) {
            long minutes = (milliseconds / 1000) / 60;
            long seconds = (milliseconds / 1000) % 60;
            String secondsStr = Long.toString( seconds );
            String secs;
            if (secondsStr.length() >= 2) {
                secs = secondsStr.substring( 0, 2 );
            } else {
                secs = "0" + secondsStr;
            }

            return minutes + ":" + secs;
        }

        private void setQuestionCount(final MyViewHolder myViewHolder, int i) {
            final CollectionReference docRef = db.collection( "Question" );
            docRef.whereEqualTo( "isActive", true )
                    .whereEqualTo( "setId", data.get( i ).getId() )
                    .addSnapshotListener( new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w( TAG, "Listen failed.", e );
                                return;
                            }

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                for (int a = 0; a < queryDocumentSnapshots.size(); a++) {
                                    try {
                                        int count = queryDocumentSnapshots.size();
                                        myViewHolder.ques.setText( "" + count );

                                    } catch (Exception e1) {
                                        Log.d( TAG, e1.getLocalizedMessage() );


                                    }
                                }


                            } else {
                                Log.d( TAG, "data: null" );
                            }
                        }
                    } );
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView title, desc, diff, ques, isNew, playedTimes;
            private ImageView diff_image;
            private Button mStartBtn;

            public MyViewHolder(@NonNull View itemView) {
                super( itemView );
                title = (TextView) itemView.findViewById( R.id.Title );
                desc = (TextView) itemView.findViewById( R.id.description );
                diff = (TextView) itemView.findViewById( R.id.difficulty );
                isNew = (TextView) itemView.findViewById( R.id.is_new );
                ques = (TextView) itemView.findViewById( R.id.question );
                diff_image = (ImageView) itemView.findViewById( R.id.image_diff );
                mStartBtn = (Button) itemView.findViewById( R.id.btn_start );
                playedTimes = (TextView) itemView.findViewById( R.id.played_times );

            }
        }
    }

    private void setData() {

        Map<String, Object> map = new HashMap<>();
        map.put( "isActive", true );
        map.put( "title", "" );
        map.put( "image", "" );
        map.put( "question", "" );
        map.put( "answer", "" );
        map.put( "optionA", "" );
        map.put( "optionB", "" );
        map.put( "optionC", "" );
        map.put( "optionD", "" );
        map.put( "view", 0 );
        map.put( "category", "general" );
        map.put( "setId", "3echu5HQKxG8ZUI35lra" );

        for (int a = 0; a < 10; a++) {
            db.collection( "CATEGORIES" )
                    .document( "GENERAL" )
                    .collection( "Sets" )
                    .document( "3echu5HQKxG8ZUI35lra" )
                    .collection( "Questions" )
                    .document()
                    .set( map );
        }


    }

    public void goBack(View view) {
        onSupportNavigateUp();
    }
}
