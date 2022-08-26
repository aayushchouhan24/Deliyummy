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
import com.deliyummy.FoodDetails;
import com.deliyummy.R;
import com.deliyummy.model.C;

import java.util.List;

public class C_Adapter extends RecyclerView.Adapter<C_Adapter.AllMenuViewHolder> {

    Context context;
    List<C> categoryList;

    public C_Adapter(Context context, List<C> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public C_Adapter.AllMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_items, parent, false);

        return new C_Adapter.AllMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull C_Adapter.AllMenuViewHolder holder, final int position) {

        holder.categoryName.setText(categoryList.get(position).getName());
        holder.categoryPrice.setText("₹ "+categoryList.get(position).getPrice());
        holder.categoryTime.setText(categoryList.get(position).getDeliveryTime());
        holder.categoryRating.setText(categoryList.get(position).getRating());
        holder.categoryCharges.setText(categoryList.get(position).getDeliveryCharges());
        holder.categoryNote.setText(categoryList.get(position).getNote());

        Glide.with(context).load(categoryList.get(position).getImageUrl()).into(holder.categoryImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, FoodDetails.class);
                i.putExtra("info", categoryList.get(position).getInfo());
                i.putExtra("note", categoryList.get(position).getNote());
                i.putExtra("name", categoryList.get(position).getName());
                i.putExtra("price", categoryList.get(position).getPrice());
                i.putExtra("rating", categoryList.get(position).getRating());
                i.putExtra("image", categoryList.get(position).getImageUrl());

                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class AllMenuViewHolder extends RecyclerView.ViewHolder{

        TextView categoryName, categoryNote, categoryRating, categoryTime, categoryCharges, categoryPrice;
        ImageView categoryImage;

        public AllMenuViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.category_name);
            categoryNote = itemView.findViewById(R.id.category_note);
            categoryCharges = itemView.findViewById(R.id.category_delivery_charge);
            categoryRating = itemView.findViewById(R.id.category_rating);
            categoryTime = itemView.findViewById(R.id.category_deliverytime);
            categoryPrice = itemView.findViewById(R.id.category_price);
            categoryImage = itemView.findViewById(R.id.category_image);
        }
    }

}
