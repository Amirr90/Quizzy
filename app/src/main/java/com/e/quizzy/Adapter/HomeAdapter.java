package com.e.quizzy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.e.quizzy.Models.Categories;
import com.e.quizzy.R;
import com.e.quizzy.ShowCategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
    List<Categories> data;

    Context context;

    String TAG = "error in homeRec :";

    public HomeAdapter(List<Categories> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.catview, viewGroup, false );
        return new MyViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeAdapter.MyViewHolder myViewHolder, final int position) {
        try {
            String title = data.get( position ).getTitle();
            if (!title.equals( "" ) && title != null) {
                myViewHolder.name.setText( data.get( position ).getTitle() );
            } else {
                myViewHolder.name.setText( data.get( position ).getId() );
            }

            myViewHolder.questions.setText( "" + data.get( position ).getQuestion() + " QUESTIONS" );
            myViewHolder.sets.setText( "" + data.get( position ).getSets() + " Sets" );
        } catch (Exception e) {
            Log.d( TAG, e.getLocalizedMessage() );
        }

        try {

            myViewHolder.mCategoryImage.setVisibility( View.VISIBLE );
            if (!data.get( position ).getImage().equalsIgnoreCase( "" ) && data.get( position ).getImage() != null) {
                Picasso.with( context ).load( data.get( position ).getImage() ).into( myViewHolder.mCategoryImage, new Callback() {
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
                    String title = data.get( position ).getTitle();
                    //setData2( data.get( position ).getTitle() );
                    context.startActivity( new Intent( context, ShowCategory.class ).putExtra( "cat_title", title ) );

                }
            } );
        } catch (Exception e) {
            Log.d( TAG, e.getLocalizedMessage() );
        }


    }

    private void setQuestionCount(final int position, final MyViewHolder myViewHolder) {
        final StringBuilder builder = new StringBuilder();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection( "Sets" )
                .whereEqualTo( "category", data.get( position ).getTitle() )
                .whereEqualTo( "isActive", true )
                .get()
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {


                            //getting question count
                            for (int a = 0; a < queryDocumentSnapshots.size(); a++) {
                                db.collection( "Question" )
                                        .whereEqualTo( "category", data.get( position ).getTitle() )
                                        .whereEqualTo( "isActive", true )
                                        .get()
                                        .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if (!queryDocumentSnapshots.isEmpty()) {
                                                    myViewHolder.questions.setText( queryDocumentSnapshots.size() + " Questions" );
                                                }


                                            }
                                        } ).addOnFailureListener( new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                } );
                            }

                            myViewHolder.sets.setText( queryDocumentSnapshots.size() + " Sets" );

                        }
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( context, "fails in listning sets", Toast.LENGTH_SHORT ).show();
            }
        } );


        /*db.collection( "Question" )
                .whereEqualTo( "setId",data.get( position ).getId()  )
                //.whereEqualTo( "isActive",true )
                .get()
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            builder.append( queryDocumentSnapshots.size()+" Questions" );
                        }
                        else {
                            builder.append( "" );
                        }
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( context, "fails in listning question", Toast.LENGTH_SHORT ).show();
            }
        } );*/

        //myViewHolder.questions.setText( "123456" );
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, questions, sets;
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

    private void setData(final String id) {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();


        for (int a = 0; a < 10; a++) {

            final Map<String, Object> map = new HashMap<>();
            map.put( "isActive", true );
            map.put( "category", id );
            map.put( "title", "" );
            map.put( "image", "" );
            map.put( "questions", 10 );
            map.put( "questions", 10 );
            map.put( "description", "" );
            map.put( "difficulty", new Random().nextInt( 2 ) );

            db.collection( "CATEGORIES" )
                    .document( id )
                    .collection( "Sets" )
                    .add( map )
                    .addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(final DocumentReference documentReference2) {
                            Log.d( TAG, "DocumentSnapshot written with ID: " + documentReference2.getId() );

                            db.collection( "Question" )
                                    .document( documentReference2.getId() )
                                    .set( map );

                            for (int a = 0; a < 10; a++) {
                                final Map<String, Object> map = new HashMap<>();
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
                                map.put( "category", id );
                                map.put( "setId", documentReference2.getId() );

                                db.collection( "CATEGORIES" )
                                        .document( id )
                                        .collection( "Sets" )
                                        .document( documentReference2.getId() )
                                        .collection( "Questions" )
                                        .add( map )
                                        .addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                db.collection( "Question" )
                                                        .document( documentReference2.getId() )
                                                        .collection( "Questions" )
                                                        .document( documentReference.getId() )
                                                        .set( map );


                                            }
                                        } );


                            }


                        }
                    } )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w( TAG, "Error adding document", e );
                        }
                    } );

        }

    }

    private void setData2(final String cat_title) {

       /* List<String> title_name = new ArrayList<>();
        final List<String> chap_name = new ArrayList<>();
        List<String> sub_name = new ArrayList<>();

        title_name.add( "Science" );
        title_name.add( "Mathematics" );
        title_name.add( "English" );


        sub_name.add( "CBSE Class 10 Biology" );
        sub_name.add( "CBSE Class 10 Chemistry" );
        sub_name.add( "CBSE Class 10 Physics" );*/


        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (int a = 0; a < 10; a++) {

            final Map<String, Object> map = new HashMap<>();
            map.put( "isActive", true );
            map.put( "category", cat_title );
            map.put( "sub_name", "" );
            map.put( "title", "" );
            map.put( "image", "" );
            map.put( "isNew", false );
            map.put( "questions", 10 );
            map.put( "description", "" );
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
                            for (int a = 0; a < 10; a++) {
                                final Map<String, Object> map = new HashMap<>();
                                map.put( "isActive", true );
                                map.put( "title", "" );
                                map.put( "image", "" );
                                map.put( "question", "" );
                                map.put( "answer", "" );
                                map.put( "optionA", "" );
                                map.put( "optionB", "" );
                                map.put( "optionC", "" );
                                map.put( "optionD", "" );
                                map.put( "isNew", false );
                                map.put( "view", 0 );
                                map.put( "category", cat_title );
                                map.put( "setId", set_id );
                                map.put( "chap_name", "" );

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

                    Toast.makeText( context, e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
                }
            } );

        }


    }


}
