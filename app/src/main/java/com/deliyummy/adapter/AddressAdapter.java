package com.deliyummy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deliyummy.AddressActivity;
import com.deliyummy.CartActivity;
import com.deliyummy.HomeActivity;
import com.deliyummy.R;
import com.deliyummy.model.Address;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rabbil.toastsiliconlibrary.ToastSilicon;

import java.util.HashMap;
import java.util.List;

import static com.deliyummy.AddressActivity.b;
import static com.deliyummy.AddressActivity.rem;
import static com.deliyummy.VerifyPhoneActivity.getUniqueId;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    Context context;
    List<Address> addresses;

    public AddressAdapter(Context context, List<Address> addresses) {
        this.context = context;
        this.addresses = addresses;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.address_item, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, final int position) {
        holder.address.setText(addresses.get(position).getAddress());
        holder.address_name.setText("₹ "+addresses.get(position).getAddress_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final HashMap map = new HashMap();
                map.put("address_name", addresses.get(position).getAddress_name());
                map.put("address", addresses.get(position).getAddress());
                map.put("city", addresses.get(position).getCity());
                map.put("pincode", addresses.get(position).getPincode());
                map.put("user_name", addresses.get(position).getUser_name());
                map.put("landmark", addresses.get(position).getLandmark());
                map.put("currentLatitude", addresses.get(position).getCurrentLatitude());
                map.put("currentLongitude", addresses.get(position).getCurrentLongitude());
                map.put("phone", addresses.get(position).getPhone());
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                ref.child(getUniqueId()).child("Current").updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            ToastSilicon.toastWarningOne(context, " Device Database error!!!", Toast.LENGTH_SHORT);
                        }
                    }
                });

                if(b){
                    Intent i = new Intent(context, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                } else {
                    Intent i = new Intent(context, CartActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                }
            }
        });
        holder.del_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rem(position,addresses,context);
            }
        });

    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder{

        TextView address_name, address;
        ImageView del_add;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            address_name = itemView.findViewById(R.id.a_name);
            address = itemView.findViewById(R.id.a_add);
            del_add = itemView.findViewById(R.id.del_add);
        }
    }

}
