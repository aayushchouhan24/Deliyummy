package com.deliyummy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.deliyummy.adapter.C_Adapter;
import com.deliyummy.model.C;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rabbil.toastsiliconlibrary.ToastSilicon;

import java.util.ArrayList;
import java.util.List;

public class Category extends AppCompatActivity {

    RecyclerView cRecyclerView;
    C_Adapter cAdapter;
    String name;
    TextView c_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        c_name = findViewById(R.id.c_name);
        c_name.setText(name);
        go1();
    }

    private void go1() {
        final List<C> listData = new ArrayList<>();
        final Query nm = FirebaseDatabase.getInstance().getReference("FoodData").child("allmenu").orderByChild("category").equalTo(name);
        nm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                        C l = npsnapshot.getValue(C.class);
                        listData.add(l);
                    }
                    getAllMenu(listData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAllMenu(List<C> c) {
        cRecyclerView = findViewById(R.id.c_recycler);
        cAdapter = new C_Adapter(this, c);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cRecyclerView.setLayoutManager(layoutManager);
        cRecyclerView.setAdapter(cAdapter);
        cAdapter.notifyDataSetChanged();

    }
}