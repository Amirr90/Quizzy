package com.e.quizzy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.quizzy.Models.Banner;
import com.e.quizzy.R;
import com.e.quizzy.SliderActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.MyViewHolder> {
    List<Banner>banners;
    Context context;
    String TAG="BannerAdapter";

    public BannerAdapter(List<Banner> banners, Context context) {
        this.banners = banners;
        this.context = context;
    }

    @NonNull
    @Override
    public BannerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.banner_view, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BannerAdapter.MyViewHolder myViewHolder, final int position) {
        try {
            String imageUrl=banners.get( position ).getImage() ;
            String titile=banners.get( position ).getTitle() ;
            myViewHolder.title.setText( titile );
            Picasso.with(context).load( imageUrl ).into( myViewHolder.bannerImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    myViewHolder.bannerImage.setImageResource( R.drawable.profile );
                }
            } );
        }
        catch (Exception e){
            Log.d(TAG,e.getLocalizedMessage());
        }

        try {
            myViewHolder.bannerImage.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String KEY = banners.get( position ).getId();
                    String TITLE = banners.get( position ).getTitle();
                    String IMAGE_URL = banners.get( position ).getImage();
                    String DESCRIPTION = banners.get( position ).getDescription();
                    String SET_ID = banners.get( position ).getSetId();
                    context.startActivity( new Intent( context, SliderActivity.class )
                            .putExtra( "SliderBannerKey", KEY )
                            .putExtra( "title", TITLE )
                            .putExtra( "imageUrl", IMAGE_URL )
                            .putExtra( "description", DESCRIPTION )
                            .putExtra( "setId", SET_ID ));
                }
            } );
        }
        catch (Exception e){
            Log.d( TAG, "onBindViewHolder: "+e.getLocalizedMessage() );
        }
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView bannerImage;
        private TextView title;
        public MyViewHolder(@NonNull View itemView) {
            super( itemView );
            bannerImage=(ImageView)itemView.findViewById( R.id.banner_image );
            title=(TextView)itemView.findViewById( R.id.banner_title );

        }
    }
}
