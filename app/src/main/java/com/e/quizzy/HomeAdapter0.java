package com.e.quizzy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.e.quizzy.Adapter.BannerAdapter;
import com.e.quizzy.Adapter.HomeAdapter;
import com.e.quizzy.Adapter.SliderAdapterExample;
import com.e.quizzy.Models.Banner;
import com.e.quizzy.Models.Categories;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.List;

import ss.com.bannerslider.Slider;

class HomeAdapter0 extends RecyclerView.Adapter<HomeAdapter0.MyViewHolder> {

    List<Categories> titleList;
    List<Categories> category;
    List<Categories> random;
    List<Banner> bannerList;
    List<Banner> mSliderbannerList;
    List<Categories> getEducationList;
    Context context;


    public HomeAdapter0(List<Categories> titleList, List<Categories> category, List<Categories> random, List<Banner> bannerList, List<Banner> mSliderbannerList, List<Categories> getEducationList, Context context) {
        this.titleList = titleList;
        this.category = category;
        this.random = random;
        this.bannerList = bannerList;
        this.mSliderbannerList = mSliderbannerList;
        this.getEducationList = getEducationList;
        this.context = context;
    }

    HomeAdapter adapter;
    BannerAdapter bannerAdapter;
    private String TAG = "HomeAdapter0";
    RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.parent_recycler, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        try {

            long viewType = titleList.get(i).getViewType();
            myViewHolder.title.setText(titleList.get(i).getTitle());
            switch (i) {
                case 0: {
                    try {
                        myViewHolder.sliderView.setVisibility(View.VISIBLE);

                        myViewHolder.sliderView.setSliderAdapter(new SliderAdapterExample(mSliderbannerList, context));
                    } catch (Exception e) {
                        Log.d(TAG, "onBindViewHolder: " + e.getLocalizedMessage());
                    }

                    if (viewType == 0) {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                        myViewHolder.recyclerView.setLayoutManager(layoutManager);
                        myViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
                    } else {
                        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
                        layoutManager.setInitialPrefetchItemCount(category.size());
                        myViewHolder.recyclerView.setLayoutManager(layoutManager);
                        myViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
                    }


                    adapter = new HomeAdapter(category, context);
                    myViewHolder.recyclerView.setAdapter(adapter);
                    myViewHolder.recyclerView.setRecycledViewPool(viewPool);
                    adapter.notifyDataSetChanged();

                    //setting Banner Adapter
                    myViewHolder.bannerRec.setLayoutManager(new LinearLayoutManager(context));
                    myViewHolder.bannerRec.setItemAnimator(new DefaultItemAnimator());
                    bannerAdapter = new BannerAdapter(bannerList, context);
                    myViewHolder.bannerRec.setAdapter(bannerAdapter);
                    bannerAdapter.notifyDataSetChanged();
                    break;


                }
                case 1: {

                    if (viewType == 0) {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                        myViewHolder.recyclerView.setLayoutManager(layoutManager);
                        myViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
                    } else {
                        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
                        layoutManager.setInitialPrefetchItemCount(category.size());
                        myViewHolder.recyclerView.setLayoutManager(layoutManager);
                        myViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
                    }
                    adapter = new HomeAdapter(random, context);
                    myViewHolder.recyclerView.setAdapter(adapter);
                    myViewHolder.recyclerView.setRecycledViewPool(viewPool);
                    adapter.notifyDataSetChanged();
                    break;
                }
                case 2: {
                    if (viewType == 0) {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                        myViewHolder.recyclerView.setLayoutManager(layoutManager);
                        myViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
                    } else {
                        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
                        layoutManager.setInitialPrefetchItemCount(category.size());
                        myViewHolder.recyclerView.setLayoutManager(layoutManager);
                        myViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
                    }
                    adapter = new HomeAdapter(getEducationList, context);
                    myViewHolder.recyclerView.setAdapter(adapter);
                    myViewHolder.recyclerView.setRecycledViewPool(viewPool);
                    adapter.notifyDataSetChanged();
                    break;
                }
            }


        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        try {


            myViewHolder.sliderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "onBindViewHolder: " + e.getLocalizedMessage());
        }

        myViewHolder.showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                context.startActivity(new Intent(context, ShowAllActivity.class)
                        .putExtra("title", titleList.get(i).getTitle())
                        .putExtra("view_type", "" + titleList.get(i).getViewType()));
            }
        });


    }


    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, showAll;
        RecyclerView recyclerView, bannerRec;
        SliderView sliderView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            title = (TextView) itemView.findViewById(R.id.textView);
            showAll = (TextView) itemView.findViewById(R.id.show_all);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.home_rec);
            bannerRec = (RecyclerView) itemView.findViewById(R.id.banner_rec);
            sliderView = (SliderView) itemView.findViewById(R.id.imageSlider);
            Slider.init(new PicassoImageLoadingService(context));


            sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            // sliderView.setSliderTransformAnimation( SliderAnimations.CUBEINDEPTHTRANSFORMATION );
            sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
            sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
            sliderView.setIndicatorSelectedColor(context.getResources().getColor(R.color.colorbackground));
            sliderView.setIndicatorUnselectedColor(Color.GRAY);
            sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
            sliderView.startAutoCycle();


        }
    }
}
