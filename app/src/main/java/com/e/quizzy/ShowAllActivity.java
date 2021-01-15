package com.e.quizzy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.e.quizzy.Adapter.HomeAdapter;
import com.e.quizzy.Models.Categories;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowAllActivity extends AppCompatActivity {
    String COLLECTION_REF, VIEW_TYPE;
    RecyclerView recyclerView;
    ShowAllAdapter adapter;
    List<Categories> allCategoriesList;
    private String TAG = "ShowAllActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_show_all );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        if (getIntent().hasExtra( "title" )) {
            COLLECTION_REF = getIntent().getStringExtra( "title" );
            VIEW_TYPE = getIntent().getStringExtra( "view_type" );
            setToolbar( toolbar, COLLECTION_REF );
            showAllData( VIEW_TYPE, COLLECTION_REF );
        } else {
            onBackPressed();
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

    private void showAllData(String VIEW_TYPE, String COLLECTION_REF) {
        allCategoriesList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById( R.id.showAllViewRec );
        if (VIEW_TYPE.equalsIgnoreCase( "0" )) {
            LinearLayoutManager layoutManager = new LinearLayoutManager( this );
            recyclerView.setLayoutManager( layoutManager );
            recyclerView.setItemAnimator( new DefaultItemAnimator() );
        } else {
            GridLayoutManager layoutManager = new GridLayoutManager( this, 2 );
            recyclerView.setLayoutManager( layoutManager );
            recyclerView.setItemAnimator( new DefaultItemAnimator() );
        }

        adapter = new ShowAllAdapter( allCategoriesList, this );
        recyclerView.setAdapter( adapter );

        loadData( adapter, COLLECTION_REF );


    }

    private void loadData(final ShowAllAdapter adapter, String collection_ref) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference titleRef = db.collection( collection_ref );
        titleRef.whereEqualTo( "isActive", true )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w( TAG, "Listen failed.", e );
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            allCategoriesList.clear();
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
                                    allCategoriesList.add( new Categories( id, questions, image_url, title, sets ) );
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

    private class ShowAllAdapter extends RecyclerView.Adapter<ShowAllActivity.MyViewHolder> {
        List<Categories> allCategoriesList;
        Context context;

        public ShowAllAdapter(List<Categories> allCategoriesList, Context context) {
            this.allCategoriesList = allCategoriesList;
            this.context = context;
        }

        @NonNull
        @Override
        public ShowAllActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.catview, viewGroup, false );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull final ShowAllActivity.MyViewHolder myViewHolder, final int position) {

            try {
                String title = allCategoriesList.get( position ).getTitle();
                if (!title.equals( "" ) && title != null) {
                    myViewHolder.name.setText( allCategoriesList.get( position ).getTitle() );
                } else {
                    myViewHolder.name.setText( allCategoriesList.get( position ).getId() );
                }

                // setQuestionCount( position, myViewHolder );
                myViewHolder.questions.setText( "" + allCategoriesList.get( position ).getQuestion() + " QUESTIONS" );
                myViewHolder.sets.setText( "" + allCategoriesList.get( position ).getSets() + " Sets" );
            } catch (Exception e) {
                Log.d( TAG, e.getLocalizedMessage() );
            }


            try {

                myViewHolder.mCategoryImage.setVisibility( View.VISIBLE );
                if (!allCategoriesList.get( position ).getImage().equalsIgnoreCase( "" ) && allCategoriesList.get( position ).getImage() != null) {
                    Picasso.with( context ).load( allCategoriesList.get( position ).getImage() ).into( myViewHolder.mCategoryImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            myViewHolder.mCategoryImage.setImageResource( R.drawable.profile );
                        }
                    } );


                }
            } catch (Exception e) {
                Log.d( TAG, e.getLocalizedMessage() );
            }

            try {
                myViewHolder.layout.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = allCategoriesList.get( position ).getTitle();
                        context.startActivity( new Intent( context, ShowCategory.class ).putExtra( "cat_title", title ) );

                    }
                } );
            } catch (Exception e) {
                Log.d( TAG, e.getLocalizedMessage() );
            }
        }

        @Override
        public int getItemCount() {
            return allCategoriesList.size();
        }
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, questions, sets;
        private CardView layout;
        private CircleImageView mCategoryImage;

        public MyViewHolder(@NonNull View itemView) {
            super( itemView );
            name = (TextView) itemView.findViewById( R.id.textView2 );
            questions = (TextView) itemView.findViewById( R.id.desc );
            sets = (TextView) itemView.findViewById( R.id.textView3 );
            layout = (CardView) itemView.findViewById( R.id.view_lay );
            mCategoryImage = (CircleImageView) itemView.findViewById( R.id.profile_image );
        }
    }
}
