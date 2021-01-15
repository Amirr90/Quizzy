package com.e.quizzy.Adapter;

import android.util.Log;

import com.e.quizzy.Models.Banner;

import java.util.List;

import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class SliderAdapter extends ss.com.bannerslider.adapters.SliderAdapter {
    List<Banner>imageUrl;
    private String TAG="SliderAdapter";

    public SliderAdapter(List<Banner> imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int getItemCount() {
        return imageUrl.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        try {
            imageSlideViewHolder.bindImageSlide( imageUrl.get( position ).getImage() );
        }
        catch (Exception e){
            Log.d( TAG, "onBindImageSlide: "+e.getLocalizedMessage() );
        }

    }
}
