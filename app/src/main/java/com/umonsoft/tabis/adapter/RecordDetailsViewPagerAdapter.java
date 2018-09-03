package com.umonsoft.tabis.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.umonsoft.tabis.R;

import java.util.List;

public class RecordDetailsViewPagerAdapter  extends PagerAdapter{

    private final Context mContext;
    private final List<String> imageList;


    public RecordDetailsViewPagerAdapter(Context mContext, List<String> imageList) {
        this.mContext = mContext;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view==object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.recorddetails_viewpager_inside,container,false);
        ImageView imageView=v.findViewById(R.id.viewpager_imageView);

        Glide.with(mContext)
                .load(imageList.get(position))
                .into(imageView);

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.invalidate();
    }

}