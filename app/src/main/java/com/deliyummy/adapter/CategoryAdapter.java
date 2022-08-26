package com.deliyummy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.deliyummy.FoodDetails;
import com.deliyummy.R;
import com.deliyummy.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.category_recycler_items, parent, false);
         // here we need to create a layout for recyclerview cell items.


        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, final int position) {

        holder.categoryName.setText(categoryList.get(position).getName());

        // for image we add Glide library dependency for image fetching from server
        RequestOptions requestOptions = new RequestOptions();
        requestOptions=requestOptions.transform(new CenterCrop(), new RoundedCorners(22));
        Glide.with(context).load(categoryList.get(position).getImageUrl()).apply(requestOptions).into(holder.categoryImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, com.deliyummy.Category.class);
                i.putExtra("name", categoryList.get(position).getName());
                i.putExtra("image", categoryList.get(position).getImageUrl());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public  static class CategoryViewHolder extends RecyclerView.ViewHolder{

        ImageView categoryImage;
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.all_menu_name);
            categoryImage = itemView.findViewById(R.id.all_menu_image);

        }
    }
}
