package com.deliyummy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deliyummy.adapter.Cart_Adapter;
import com.deliyummy.model.Cart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.deliyummy.HomeActivity.add;
import static com.deliyummy.VerifyPhoneActivity.getUniqueId;

public class CartActivity extends BaseActivity {

    static RecyclerView cart_rv;
    static LinearLayout l1,v;
    static FrameLayout l3;
    static ScrollView sv;
    static  Cart_Adapter cartadapter;
    static  String items,totalm,dcharge="0",charge,grtotal,tipm="0";
    EditText tip,message;
    static  TextView total_items,total_money,charges,dcharges,gtotal,btotal;
    static Button go,tipb;

    @Override
    int getContentViewId() {
        return R.layout.activity_cart;
    }
    @Override
    int getNavigationMenuItemId() {
        return R.id.cart_screen;
    }
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cart_rv = findViewById(R.id.cart_rv);
        l1 = findViewById(R.id.l1);
        v = findViewById(R.id.v);
        l3 = findViewById(R.id.l3);
        sv = findViewById(R.id.sv);
        go = findViewById(R.id.go);
        total_items = findViewById(R.id.item_no);
        total_money = findViewById(R.id.total_money);
        charges = findViewById(R.id.charges);
        dcharges = findViewById(R.id.dcharges);
        gtotal = findViewById(R.id.gtotal);
        btotal = findViewById(R.id.b_total);
        tip = findViewById(R.id.tip);
        tipb = findViewById(R.id.tipb);
        message = findViewById(R.id.message);
        findViewById(R.id.ch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add(AddressActivity.class,false,CartActivity.this);
            }
        });
        tipb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tip.isEnabled()){
                    int l= Integer.parseInt(tipm);
                    dcharge= String.valueOf(Integer.parseInt(dcharge)-l);
                    grtotal= String.valueOf(Integer.parseInt(grtotal)-l);
                    dcharges.setText("₹ "+dcharge);
                    gtotal.setText("₹ "+grtotal);
                    btotal.setText("₹ "+grtotal);
                    tipm= "0";
                    tipb.setVisibility(View.INVISIBLE);
                    tip.setText("");
                    tip.setEnabled(true);
                }
            }
        });
        tip.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView textView, int i, KeyEvent keyEvent) {
                if(textView.length()>0){
                    if(Integer.parseInt(textView.getText().toString())<10001){
                        if(Integer.parseInt(textView.getText().toString())>0){
                            int l= Integer.parseInt(textView.getText().toString());
                            dcharge= String.valueOf(Integer.parseInt(charge)+l);
                            grtotal= String.valueOf(Integer.parseInt(grtotal)+l);
                            dcharges.setText("₹ "+dcharge);
                            gtotal.setText("₹ "+grtotal);
                            btotal.setText("₹ "+grtotal);
                            tipm= String.valueOf(l);
                            tipb.setVisibility(View.VISIBLE);
                            tipb.setBackgroundResource(R.drawable.danger_circle);
                            tipb.setForeground(getDrawable(R.drawable.ic_baseline_close_24));
                            textView.setEnabled(false);
                        } else {
                            tip.setError("Invalid Amount");
                        }
                    }else {
                        tip.setError("Maximum Limit Is 10000");
                    }
                    } else {
                        tip.setError("Please Enter Amount");
                    }

                return false;
            }
        });
        go1();
        go2();
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               conferm();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        doo();

    }

    public void conferm() {
        final String date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss aaa", Locale.getDefault()).format(new Date());
        String key = ("Deilyummy" +date+grtotal+items+getUniqueId().replace("+", "").replace(":", "").replace(" ", "").replace("/", "").replace(",", ""));
        UUID uniqueKey = UUID.nameUUIDFromBytes(key.getBytes());
        final String oid = uniqueKey.toString().replace("-", "");

        Intent i = new Intent(CartActivity.this, CheckoutActivity.class);
        i.putExtra("order_id", oid);
        i.putExtra("grand_total", grtotal);
        i.putExtra("items", items);
        i.putExtra("tip", tipm);
        i.putExtra("date", date);
        i.putExtra("items", items);
        i.putExtra("message", message.getText().toString());
        i.putExtra("status", "pending");
        startActivity(i);
    }

    public void doo(){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(getUniqueId()).child("Current");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TextView n=findViewById(R.id.address_cart);
                TextView f=findViewById(R.id.full_a_c);
                if(snapshot.exists()){ ;
                    n.setText("Deliver to "+snapshot.child("address_name").getValue().toString());
                    f.setText(snapshot.child("address").getValue().toString());
                }else {
                    n.setText("Select Address");
                    f.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public static void go2(){

        final DatabaseReference nm= FirebaseDatabase.getInstance().getReference("Users").child(getUniqueId()).child("Cart");
        nm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    items="0";
                    totalm="0";
                    charge="0";
                    grtotal="0";
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        Cart l=npsnapshot.getValue(Cart.class);
                        items = String.valueOf(Integer.parseInt(items)+Integer.parseInt(l.getNumber()));
                        totalm= String.valueOf((Integer.parseInt(totalm)+((Integer.parseInt(l.getNumber()))*Integer.parseInt(l.getPrice()))));
                    }
                    charge=String.valueOf(((Integer.parseInt(totalm) / 100)*5)+Integer.parseInt(items)*5);
                    if((Integer.parseInt(charge)>100)){

                    }
                    grtotal=String.valueOf((Integer.parseInt(totalm)+Integer.parseInt(charge)));
                    charges.setText("₹ "+charge);
                    total_items.setText(items);
                    total_money.setText("₹ "+totalm);
                    gtotal.setText("₹ "+grtotal);
                    btotal.setText("₹ "+grtotal);
                } else {
                    l3.setVisibility(View.GONE);
                    v.setVisibility(View.GONE);
                    sv.setVisibility(View.GONE);
                    l1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void go1(){
        final List<Cart> listData=new ArrayList<>();
        final DatabaseReference nm= FirebaseDatabase.getInstance().getReference("Users").child(getUniqueId()).child("Cart");
        nm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        Cart l=npsnapshot.getValue(Cart.class);
                        listData.add(l);
                    }
                    l1.setVisibility(View.GONE);
                    l3.setVisibility(View.GONE);
                    v.setVisibility(View.VISIBLE);
                    sv.setVisibility(View.VISIBLE);
                    getAllMenu(listData);
                } else {
                    l3.setVisibility(View.GONE);
                    v.setVisibility(View.GONE);
                    sv.setVisibility(View.GONE);
                    l1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void rem(final int i, final List<Cart> cartlist, final Context context){
        new AlertDialog.Builder(context)
                .setMessage("Are you sure you want to remove?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String key = ("Deilyummy" + cartlist.get(i).getName() + cartlist.get(i).getPrice().replace("+", "").replace(" ", ""));
                        UUID uniqueKey = UUID.nameUUIDFromBytes(key.getBytes());
                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(getUniqueId()).child("Cart").child(uniqueKey.toString().replace("-",""));
                        ref.removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                cartlist.remove(i);
                                cartadapter.notifyItemRemoved(i);
                                cartadapter.notifyDataSetChanged();
                                go2();
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void  getAllMenu(List<Cart> cartList){
        cart_rv = findViewById(R.id.cart_rv);
        cartadapter = new Cart_Adapter(this, cartList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cart_rv.setLayoutManager(layoutManager);
        cart_rv.setAdapter(cartadapter);
        cartadapter.notifyDataSetChanged();
    }
}
