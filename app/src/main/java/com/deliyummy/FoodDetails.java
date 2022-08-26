package com.deliyummy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rabbil.toastsiliconlibrary.ToastSilicon;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import static com.deliyummy.HomeActivity.wToast;
import static com.deliyummy.VerifyPhoneActivity.getUniqueId;

public class FoodDetails extends AppCompatActivity {

    // now we will get data from intent and set to UI

    ImageView imageView;
    TextView itemName, itemPrice, itemRating, information;
    RatingBar ratingBar;
    Button add;
    String name, price, rating, imageUrl,date,note,info;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);
        findViewById(R.id.cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodDetails.this, CartActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        add=findViewById(R.id.add_to_cart);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create();
            }
        });
        name = intent.getStringExtra("name");
        info= intent.getStringExtra("info");
        note = intent.getStringExtra("note");
        price = intent.getStringExtra("price");
        rating = intent.getStringExtra("rating");
        imageUrl = intent.getStringExtra("image");
        date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss aaa", Locale.getDefault()).format(new Date());
        information=findViewById(R.id.info);
        imageView = findViewById(R.id.imageView5);
        itemName = findViewById(R.id.name);
        itemPrice = findViewById(R.id.price);
        itemRating = findViewById(R.id.rating);
        ratingBar = findViewById(R.id.ratingBar);
        findViewById(R.id.google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String escapedQuery = null;
                try {
                    escapedQuery = URLEncoder.encode(name, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Uri uri = Uri.parse("http://www.google.com/search?q=" + escapedQuery);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        Glide.with(getApplicationContext()).load(imageUrl).into(imageView);
        itemName.setText(name);
        itemPrice.setText("₹ "+price);
        itemRating.setText(rating);
        information.setText(info);
        ratingBar.setRating(Float.parseFloat(rating));
        ratingBar.setFocusable(false);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

    }
    public String foodid() {
        String key = ("Deilyummy"+name+price.replace("+","").replace(" ",""));
        UUID uniqueKey = UUID.nameUUIDFromBytes(key.getBytes());
        return uniqueKey.toString().replace("-","");
    }

    public void create() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        final HashMap map = new HashMap();
        map.put("name", name);
        map.put("price", price);
        map.put("note", note);
        map.put("imageUrl", imageUrl);
        map.put("number","1");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(getUniqueId())){
                    ref.child(getUniqueId()).child("Cart").child(foodid()).updateChildren(map, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                ToastSilicon.toastWarningOne(FoodDetails.this, " Device Database error!!!", Toast.LENGTH_SHORT);
                            } else {
                               wToast(FoodDetails.this, "Added To Cart");
                            }
                        }
                    });

                } else {
                    ToastSilicon.toastSuccessOne(FoodDetails.this, "Please Login First", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(FoodDetails.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
