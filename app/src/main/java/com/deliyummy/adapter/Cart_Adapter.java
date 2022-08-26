package com.deliyummy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deliyummy.FoodDetails;
import com.deliyummy.HomeActivity;
import com.deliyummy.R;
import com.deliyummy.model.Cart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rabbil.toastsiliconlibrary.ToastSilicon;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.deliyummy.CartActivity.go2;
import static com.deliyummy.CartActivity.rem;
import static com.deliyummy.VerifyPhoneActivity.getUniqueId;

public class Cart_Adapter extends RecyclerView.Adapter<Cart_Adapter.AllMenuViewHolder> {

    Context context;
    List<Cart> cartlist;

    public Cart_Adapter(Context context, List<Cart> cartlist) {
        this.context = context;
        this.cartlist = cartlist;
    }

    @NonNull
    @Override
    public Cart_Adapter.AllMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_items, parent, false);

        return new Cart_Adapter.AllMenuViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Cart_Adapter.AllMenuViewHolder holder, final int position) {
        holder.no=cartlist.get(position).getNumber();
        holder.namei=cartlist.get(position).getName();
        holder.key = ("Deilyummy"+cartlist.get(position).getName()+cartlist.get(position).getPrice().replace("+","").replace(" ",""));
        holder.cartName.setText(cartlist.get(position).getName());
        holder.cartPrice.setText("₹ "+cartlist.get(position).getPrice());
        holder.cartNote.setText(cartlist.get(position).getNote());
        holder.cartno.setText(cartlist.get(position).getNumber());
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.num= String.valueOf(Integer.parseInt(holder.no)+Integer.parseInt("-1"));
                if (Integer.parseInt(holder.num)<1){
                    holder.num="1";
                }
                holder.no=holder.num;
                holder.pricei=String.valueOf(Integer.parseInt(cartlist.get(position).getPrice())*Integer.parseInt(holder.num));
                holder.create("-1",context);
                holder.cartno.setText(holder.num);
                holder.cartPrice.setText("₹ "+holder.pricei);
                go2();
            }
        });
        holder.plu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.num= String.valueOf(Integer.parseInt(holder.no)+Integer.parseInt("1"));
                if (Integer.parseInt(holder.num)<1){
                    holder.num="1";
                }
                holder.no=holder.num;
                holder.pricei=String.valueOf(Integer.parseInt(cartlist.get(position).getPrice())*Integer.parseInt(holder.num));
                holder.create("1",context);
                holder.cartno.setText(holder.num);
                holder.cartPrice.setText("₹ "+holder.pricei);
                go2();
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rem(position,cartlist,context);
            }
        });

        Glide.with(context).load(cartlist.get(position).getImageUrl()).into(holder.cartImage);
    }

    @Override
    public int getItemCount() {
        return cartlist.size();
    }


    public static class AllMenuViewHolder extends RecyclerView.ViewHolder{
        Button min,plu;
        TextView cartName, cartNote, cartno, cartTime, cartCharges, cartPrice;
        ImageView cartImage,remove;
        String no,namei,key,pricei,num;
        public AllMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            min=itemView.findViewById(R.id.min);
            plu=itemView.findViewById(R.id.plu);
            remove=itemView.findViewById(R.id.remove);
            cartno=itemView.findViewById(R.id.cart_item_num);
            cartName = itemView.findViewById(R.id.cart_name);
            cartNote = itemView.findViewById(R.id.cart_note);
            cartPrice = itemView.findViewById(R.id.cart_price);
            cartImage = itemView.findViewById(R.id.cart_image);
        }
        public void create(String x, final Context context ){

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(getUniqueId()).child("Cart").child(foodid());
            if (Integer.parseInt(num)<1){
                num="1";
            }
            final HashMap map = new HashMap();
            map.put("number", num);
            ref.updateChildren(map, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        ToastSilicon.toastWarningOne(context, " Device Database error!!!", Toast.LENGTH_SHORT);
                    }
                }
            });
        }


        public String foodid() {
            UUID uniqueKey = UUID.nameUUIDFromBytes(key.getBytes());
            return uniqueKey.toString().replace("-","");
        }


    }

}
