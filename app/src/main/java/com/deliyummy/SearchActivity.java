package com.deliyummy;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deliyummy.adapter.C_Adapter;
import com.deliyummy.model.C;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {
    RecyclerView cRecyclerView;
    C_Adapter cAdapter;
    EditText searchbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchbar = findViewById(R.id.searchbar);
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                go1(s.toString().toLowerCase());
            }
        });
    }
    @Override
    public void onBackPressed() {
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_search;
    }
    @Override
    int getNavigationMenuItemId() {
        return R.id.search_screen;
    }

    private void go1(String name){
        final List<C> listData=new ArrayList<>();
        final Query nm= FirebaseDatabase.getInstance().getReference("FoodData").child("allmenu").orderByChild("keyword").startAt(name).endAt(name+"\uf8ff");
        nm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        C l=npsnapshot.getValue(C.class);
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


    private void  getAllMenu(List<C> c){
        cRecyclerView = findViewById(R.id.s_recycler);
        cAdapter = new C_Adapter(this, c);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cRecyclerView.setLayoutManager(layoutManager);
        cRecyclerView.setAdapter(cAdapter);
        cAdapter.notifyDataSetChanged();

    }

}