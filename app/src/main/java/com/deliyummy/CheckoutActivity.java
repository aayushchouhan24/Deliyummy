package com.deliyummy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rabbil.toastsiliconlibrary.ToastSilicon;

import java.util.HashMap;

import static com.deliyummy.CartActivity.go2;
import static com.deliyummy.VerifyPhoneActivity.getUniqueId;

public class CheckoutActivity extends AppCompatActivity {
String order_id,grand_total,items,date,status;
TextView total;
Button place;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Intent intent = getIntent();
        place=findViewById(R.id.place);
        order_id = intent.getStringExtra("order_id");
        grand_total = intent.getStringExtra("grand_total");
        items = intent.getStringExtra("items");
        date = intent.getStringExtra("date");
        status = intent.getStringExtra("status");
        total=findViewById(R.id.total);
        if(Integer.parseInt(items)>1){
        total.setText("Payment of ₹ "+grand_total+" for "+items+" item's");
        } else {
        total.setText("Payment of ₹ "+grand_total+" for 1 item");
        }
        place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(CheckoutActivity.this)
                        .setMessage("Are you sure you want to place order?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                                final HashMap map = new HashMap();
                                map.put("order_id", order_id);
                                map.put("grand_total", grand_total);
                                map.put("items", items);
                                map.put("date", date);
                                map.put("status","pending");
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.hasChild(getUniqueId())){
                                            ref.child(getUniqueId()).child("Orders").child(order_id).updateChildren(map, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    if (databaseError != null) {
                                                        ToastSilicon.toastWarningOne(CheckoutActivity.this, " Device Database error!!!", Toast.LENGTH_SHORT);
                                                    } else {
                                                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(getUniqueId()).child("Cart");
                                                        ref.removeValue(new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                                            }
                                                        });
                                                    }
                                                }
                                            });

                                        } else {
                                            ToastSilicon.toastSuccessOne(CheckoutActivity.this, "Please Login First", Toast.LENGTH_SHORT);
                                            Intent intent = new Intent(CheckoutActivity.this, HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        go2();
    }
}