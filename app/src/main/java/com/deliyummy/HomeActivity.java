package com.deliyummy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deliyummy.adapter.AllMenuAdapter;
import com.deliyummy.adapter.CategoryAdapter;
import com.deliyummy.adapter.RecommendedAdapter;
import com.deliyummy.model.Address;
import com.deliyummy.model.Allmenu;
import com.deliyummy.model.Category;
import com.deliyummy.model.Recommended;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.MapType;
import com.sucho.placepicker.PlacePicker;

import java.util.ArrayList;
import java.util.List;

import static com.deliyummy.VerifyPhoneActivity.getUniqueId;

public class HomeActivity extends BaseActivity   {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    RecyclerView categoryRecyclerView, recommendedRecyclerView, allMenuRecyclerView;
    CategoryAdapter categoryAdapter;
    RecommendedAdapter recommendedAdapter;
    static TextView n,f;
    AllMenuAdapter allMenuAdapter;
    static double currentLatitude, currentLongitude;

    @Override
    int getContentViewId() {
        return R.layout.activity_home;
    }
    @Override
    int getNavigationMenuItemId() {
        return R.id.home_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this);
        n =findViewById(R.id.n_add);
        f =findViewById(R.id.full_add);
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               add(AddressActivity.class,true,HomeActivity.this);
            }
        });
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(getUniqueId()).child("Current");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final TextView n =findViewById(R.id.n_add);
                final TextView f =findViewById(R.id.full_add);
                if(snapshot.exists()){ ;
                    n.setText(snapshot.child("address_name").getValue().toString());
                    f.setText(snapshot.child("address").getValue().toString());
                }else {
                    n.setText("Select Address");
                    f.setText("Click to select or create new address");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        go();
    }

    @Override
    public void onBackPressed() {
    }

    static public void add(Class c, Boolean b, Context context) {
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(context,R.anim.slide_up_in,R.anim.slide_up_out).toBundle();
        Intent i = new Intent(context, c);
        i.putExtra("currentLatitude",currentLatitude );
        i.putExtra("currentLongitude",currentLongitude );
        i.putExtra("home",b );
        context.startActivity(i); }

    private void go1(){
        final List<Allmenu> listData=new ArrayList<>();
        final DatabaseReference nm= FirebaseDatabase.getInstance().getReference("FoodData").child("allmenu");
        nm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        Allmenu l=npsnapshot.getValue(Allmenu.class);
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

    private void go2(){
        final List<Category> listData=new ArrayList<>();
        final DatabaseReference nm= FirebaseDatabase.getInstance().getReference("FoodData").child("category");
        nm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        Category l=npsnapshot.getValue(Category.class);
                        listData.add(l);
                    }
                    getCategoryData(listData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void go3(){
        final List<Recommended> listData=new ArrayList<>();
        final Query nm= FirebaseDatabase.getInstance().getReference("FoodData").child("allmenu").orderByChild("recommended").equalTo("true");
        nm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        Recommended l=npsnapshot.getValue(Recommended.class);
                        listData.add(l);
                    }
                    getRecommendedData(listData);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

void  go(){
go1();go2();go3();
}

    private void  getCategoryData(List<Category> categoryList){

        categoryRecyclerView = findViewById(R.id.category_recycler);
        categoryAdapter = new CategoryAdapter(this, categoryList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(layoutManager);
        categoryRecyclerView.setAdapter(categoryAdapter);

    }

    private void  getRecommendedData(List<Recommended> recommendedList){

        recommendedRecyclerView = findViewById(R.id.recommended_recycler);
        recommendedAdapter = new RecommendedAdapter(this, recommendedList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recommendedRecyclerView.setLayoutManager(layoutManager);
        recommendedRecyclerView.setAdapter(recommendedAdapter);

    }

    private void  getAllMenu(List<Allmenu> allmenuList){

        allMenuRecyclerView = findViewById(R.id.all_menu_recycler);
        allMenuAdapter = new AllMenuAdapter(this, allmenuList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        allMenuRecyclerView.setLayoutManager(layoutManager);
        allMenuRecyclerView.setAdapter(allMenuAdapter);
        allMenuAdapter.notifyDataSetChanged();

    }

    static public void doo(){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(getUniqueId()).child("Current");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){ ;
                    n.setText(snapshot.child("address_name").getValue().toString());
                    f.setText(snapshot.child("address").getValue().toString());
                }else {
                    n.setText("Select Address");
                    f.setText("Click to select or create new address");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onStart() {
        super.onStart();
        doo();
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            currentLatitude=mLastLocation.getLatitude();
                            currentLongitude=mLastLocation.getLongitude();
                        }
                    }
                });
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(HomeActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }
    private void requestPermissions() {

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        startLocationPermissionRequest();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    public static void wToast(Activity a,String s){
        LayoutInflater inflater = a.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) a.findViewById(R.id.custom_toast_container));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(s);
        Toast toast = new Toast(a.getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

}
