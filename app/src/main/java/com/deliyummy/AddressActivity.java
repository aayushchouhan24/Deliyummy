package com.deliyummy;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deliyummy.adapter.AddressAdapter;
import com.deliyummy.adapter.Cart_Adapter;
import com.deliyummy.model.Address;
import com.deliyummy.model.Cart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rabbil.toastsiliconlibrary.ToastSilicon;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.MapType;
import com.sucho.placepicker.PlacePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.deliyummy.HomeActivity.f;
import static com.deliyummy.HomeActivity.n;
import static com.deliyummy.VerifyPhoneActivity.getUniqueId;

public class AddressActivity extends AppCompatActivity {
    Double currentLatitude, currentLongitude ;
    public static Boolean create;
    static AddressAdapter address_adapter;
    FrameLayout sl ,ca;
    static RecyclerView address_rv;
    TextView textView,et_add_name,et_name,et_landmark,et_phone;
    ImageView mark;
    static public Boolean b;
    LinearLayout cm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        Intent intent = getIntent();
        address_rv = findViewById(R.id.address_rv);
        et_add_name = findViewById(R.id.et_add_name);
        et_name = findViewById(R.id.et_name);
        et_landmark = findViewById(R.id.et_landmark);
        et_phone = findViewById(R.id.et_phone);
        ca = findViewById(R.id.ca);
        sl = findViewById(R.id.sl);
        textView = findViewById(R.id.textView10);
        mark=findViewById(R.id.mark);
        currentLatitude = intent.getDoubleExtra("currentLatitude",0.0);
        currentLongitude= intent.getDoubleExtra("currentLongitude",0.0);
        b= intent.getBooleanExtra("home",true);
        if(create==null){
            create=false;
        }
        cm=findViewById(R.id.cm);
        if(create){
            Drawable minus=getResources().getDrawable(R.drawable.minus);
            mark.setImageDrawable(minus);
            cm.setVisibility(View.VISIBLE);
        } else{
            Drawable plus=getResources().getDrawable(R.drawable.plus);
            mark.setImageDrawable(plus);
            cm.setVisibility(View.GONE);
        }
        findViewById(R.id.sl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new PlacePicker.IntentBuilder()
                        .setMapRawResourceStyle(R.raw.map_style)
                        .setMapType(MapType.NORMAL)
                        .sethome(b)
                        .setLatLong(currentLatitude,currentLongitude)
                        .build(AddressActivity.this);
                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
            }
        });
        go();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        findViewById(R.id.create_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(create){
                    Drawable plus=getResources().getDrawable(R.drawable.plus);
                    mark.setImageDrawable(plus);
                    cm.setVisibility(View.GONE);
                    create=false;
                } else{
                    Drawable minus=getResources().getDrawable(R.drawable.minus);
                    mark.setImageDrawable(minus);
                    cm.setVisibility(View.VISIBLE);
                    create=true;
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                b=data.getBooleanExtra("home",true);
                sl.setVisibility(View.GONE);
                ca.setVisibility(View.VISIBLE);
                final AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
                TextView loc=findViewById(R.id.full_location);
                loc.setText(addressData.component3().get(0).getAddressLine(0));
                loc.setVisibility(View.VISIBLE);
                ca.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        create =false;
                        if(et_phone.length()!=0  &&  et_landmark.length()!=0  &&  et_name.length()!=0  &&  et_add_name.length()!=0) {
                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                            final HashMap map = new HashMap();
                            map.put("address_name", et_add_name.getText().toString());
                            map.put("address", addressData.component3().get(0).getAddressLine(0));
                            map.put("city", addressData.component3().get(0).getLocality());
                            map.put("pincode", addressData.component3().get(0).getPostalCode());
                            map.put("user_name", et_name.getText().toString());
                            map.put("landmark", et_landmark.getText().toString());
                            map.put("currentLatitude", String.valueOf(addressData.component3().get(0).getLatitude()));
                            map.put("currentLongitude", String.valueOf(addressData.component3().get(0).getLongitude()));
                            map.put("phone", et_phone.getText().toString());
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(getUniqueId())) {
                                        if (!snapshot.child(getUniqueId()).child("Address").hasChild(et_add_name.getText().toString())) {
                                            ref.child(getUniqueId()).child("Address").child(et_add_name.getText().toString()).updateChildren(map, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    if (databaseError != null) {
                                                        ToastSilicon.toastWarningOne(AddressActivity.this, " Device Database error!!!", Toast.LENGTH_SHORT);
                                                    } else {
                                                        ToastSilicon.toastSuccessOne(AddressActivity.this, "Address Created", Toast.LENGTH_SHORT);
                                                    }
                                                }
                                            });
                                            ref.child(getUniqueId()).child("Current").updateChildren(map, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    if(b){
                                                        Intent intent = new Intent(AddressActivity.this, HomeActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                    } else {
                                                        Intent intent = new Intent(AddressActivity.this, CartActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });

                                        } else {
                                            ToastSilicon.toastSuccessOne(AddressActivity.this, "Address With This Name Already Exist's", Toast.LENGTH_SHORT);
                                        }
                                    } else {
                                        ToastSilicon.toastSuccessOne(AddressActivity.this, "Please Login First", Toast.LENGTH_SHORT);

                                        Intent intent = new Intent(AddressActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else  {
                            if(et_add_name.length()==0){
                                et_add_name.setError("Address Name Required");
                                et_add_name.requestFocus();
                            }
                            else if(et_name.length()==0){
                                et_name.setError("Person Name Required");
                                et_name.requestFocus();
                            }  else if(et_landmark.length()==0){
                                et_landmark.setError("Landmark Required");
                                et_landmark.requestFocus();
                            }  else if(et_phone.length()==0){
                                et_phone.setError("Phone Number Required");
                                et_phone.requestFocus();
                            }
                        }

                    }
                });

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void rem(final int i, final List<Address> addresses, final Context context){
        new AlertDialog.Builder(context)
                .setMessage("Are you sure you want to remove?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String address_name=addresses.get(i).getAddress_name();
                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(getUniqueId());
                        final DatabaseReference nm= FirebaseDatabase.getInstance().getReference("Users").child(getUniqueId()).child("Current");
                        ref.child("Address").child(address_name).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull final DatabaseReference ref) {
                                addresses.remove(i);
                                address_adapter.notifyItemRemoved(i);
                                address_adapter.notifyDataSetChanged();
                                nm.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            if(snapshot.getValue(Address.class).getAddress_name().equals(address_name)){
                                                nm.removeValue();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void go(){
        final List<Address> listData=new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(getUniqueId()).child("Address");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    address_rv.removeAllViewsInLayout();
                    address_rv.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        Address l=npsnapshot.getValue(Address.class);
                        listData.add(l);
                    }
                    getAddress(listData);
                } else {
                    address_rv.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void  getAddress(List<Address> addresses){
        address_rv = findViewById(R.id.address_rv);
        address_adapter = new AddressAdapter(this, addresses);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        address_rv.setLayoutManager(layoutManager);
        address_rv.setAdapter(address_adapter);
        address_adapter.notifyDataSetChanged();
    }

}