package com.e.quizzy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class SliderActivity extends AppCompatActivity {

    String SLIDER_TITLE, SLIDER_KEY, DESCRIPTION, SET_ID;
    private String IMAGE_URL;
    private TextView mTitie, mDescripion;
    private ImageView mSliderImage;
    private RelativeLayout mLoadingLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_slider );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );


        mLoadingLayout = (RelativeLayout) findViewById( R.id.loading_layout );

        if (getIntent().hasExtra( "SliderBannerKey" )) {
            SLIDER_TITLE = getIntent().getStringExtra( "title" );
            SLIDER_KEY = getIntent().getStringExtra( "SliderBannerKey" );
            DESCRIPTION = getIntent().getStringExtra( "description" );
            IMAGE_URL = getIntent().getStringExtra( "imageUrl" );
            SET_ID = getIntent().getStringExtra( "setId" );
            Log.d( "SLIDER_ID: ", SLIDER_KEY );
            setToolbar( toolbar, SLIDER_TITLE );
            showBannerSliderDetail( SLIDER_KEY );
        } else {
            onBackPressed();
            finish();
        }

    }

    private void setVisibility(boolean visibility) {
        mLoadingLayout.setVisibility( visibility ? View.GONE : View.VISIBLE );
        mSliderImage.setVisibility( visibility ? View.VISIBLE : View.GONE );
    }

    private void showBannerSliderDetail(String slider_key) {
        mTitie = (TextView) findViewById( R.id.slider_title );
        mDescripion = (TextView) findViewById( R.id.slider_des );
        mSliderImage = (ImageView) findViewById( R.id.slider_image );

        //setting Values

        mTitie.setText( SLIDER_TITLE );
        mDescripion.setText( DESCRIPTION );

        if (IMAGE_URL != null)
            Picasso.with( this ).load( IMAGE_URL ).into( mSliderImage, new Callback() {
                @Override
                public void onSuccess() {
                    setVisibility( true );

                }

                @Override
                public void onError() {
                    Toast.makeText( SliderActivity.this, "can't load image", Toast.LENGTH_SHORT ).show();
                    setVisibility( false );

                }
            } );


        //Toast.makeText( this, slider_key, Toast.LENGTH_SHORT ).show();
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

    public void playNowButton(View view) {
        if (!SET_ID.equalsIgnoreCase( "" )) {
            startActivity( new Intent( SliderActivity.this, QuizActivity.class )
                    .putExtra( "set_id", SET_ID )
                    .putExtra( "cat_id", "GENERAL" )
                    .putExtra( "from", "SliderActivity" ) );
        } else {
            String msg = "No any sets available at this moment";
            Toast.makeText( this, msg, Toast.LENGTH_SHORT ).show();
        }


    }

    public void alertDialog(String msg) {

        new AlertDialog.Builder( SliderActivity.this )
                .setMessage( msg )
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                } ).show();
    }

}
