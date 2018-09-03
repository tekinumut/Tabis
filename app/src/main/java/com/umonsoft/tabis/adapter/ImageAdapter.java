package com.umonsoft.tabis.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.umonsoft.tabis.HelperClasses.ImageController;
import com.umonsoft.tabis.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Uri> imagePaths;
    private ImageController imageController;

    public ImageAdapter(Context context, ImageController imageController, ArrayList<Uri> imagePaths) {
        this.context = context;
        this.imageController = imageController;
        this.imagePaths = imagePaths;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tab3image_recyclerview_inside, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Uri imagePath = imagePaths.get(position);
        // holder.imageView.getLayoutParams().width
        if(holder.imageView!=null)
        {
            Glide
                    .with(context)
                    .load(imagePath)
                    .into(holder.imageView);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageController.setImgMain(imagePath);
            }
        });
    }

    public void changePath(ArrayList<Uri> imagePaths) {
        this.imagePaths = imagePaths;
        imageController.setImgMain(imagePaths.get(0));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView =itemView.findViewById(R.id.img_item);
        }
    }
}