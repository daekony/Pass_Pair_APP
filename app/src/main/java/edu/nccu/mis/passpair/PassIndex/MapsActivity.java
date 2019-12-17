package edu.nccu.mis.passpair.PassIndex;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.nccu.mis.passpair.Homepage.HomePage;
import edu.nccu.mis.passpair.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap; //宣告 google map 物件
    float zoom;
    private LocationManager locMgr;
    String bestProv;
//    private ListView lstPrefer;

    public String nam;
    private int users = 0;
    String UID;
    RecyclerView recyclerView;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ArrayList<String> namelist = new ArrayList<String>();
    ArrayList<String> imagelist = new ArrayList<String>();
    ArrayList<String> IDlist = new ArrayList<String>();
    ArrayList<Double> dis_list = new ArrayList<Double>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        UID = bundle.getString("UID");

        // 利用 getSupportFragmentManager() 方法取得管理器
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // 以非同步方式取得 GoogleMap 物件
        mapFragment.getMapAsync(this);

        retrieveData();

        recyclerView = (RecyclerView) findViewById(R.id.map_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        RecAdapterMap adapterMap = new RecAdapterMap(MapsActivity.this, imagelist, namelist, IDlist, dis_list, UID);
        recyclerView.setAdapter(adapterMap);
        adapterMap.notifyDataSetChanged();

    }

    private void retrieveData() {
        DatabaseReference reference_contacts = FirebaseDatabase.getInstance().getReference("User");
        reference_contacts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double Lat1r = dataSnapshot.child(UID).child("基本資料").child("Location").child("latitude").getValue(Double.class);
                double Long1r = dataSnapshot.child(UID).child("基本資料").child("Location").child("longitude").getValue(Double.class);
                String user_sexual = dataSnapshot.child(UID).child("基本資料").child("性別").getValue(String.class);
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String user_id = ds.getKey();
                    String sexual = ds.child("基本資料").child("性別").getValue(String.class);
                    boolean is_friend = false;
                    for (DataSnapshot ds_my_friend : dataSnapshot.child(UID).child("朋友").getChildren()) {
                        String my_friend_uid = ds_my_friend.getKey();
                        if (TextUtils.equals(my_friend_uid, user_id)) {
                            is_friend = true;
                        }
                    }
                    if (!is_friend && !TextUtils.equals(sexual,user_sexual)) {
                        if (dataSnapshot.child(ds.getKey()).child("基本資料").hasChild("Location")) {
                            if (!TextUtils.equals(ds.getKey(), UID)) {
                                users = users + 1;
                                double Lat2r = dataSnapshot.child(ds.getKey()).child("基本資料").child("Location").child("latitude").getValue(Double.class);
                                double Long2r = dataSnapshot.child(ds.getKey()).child("基本資料").child("Location").child("longitude").getValue(Double.class);
                                float result[] = new float[1];
                                Location.distanceBetween(Lat1r, Long1r, Lat2r, Long2r, result);
                                double distance = result[0];
                                if (distance != 0.0 ) {
                                    if (IDlist.size() > 0) {
                                        boolean is_added = false;
                                        for (int i = 0; i < IDlist.size(); i++) {
                                            if (TextUtils.equals(ds.getKey(), IDlist.get(i))) {
                                                is_added = true;
                                            }
                                        }
                                        if (!is_added) {
                                            namelist.add(dataSnapshot.child(ds.getKey()).child("基本資料").child("暱稱").getValue(String.class));
                                            IDlist.add(ds.getKey());
                                            imagelist.add(dataSnapshot.child(ds.getKey()).child("基本資料").child("大頭照").getValue(String.class));
                                            dis_list.add(distance);
                                            Log.e("namelist", namelist.get(users - 1));
                                            Log.e("IDlist", IDlist.get(users - 1));
                                            Log.e("imagelist", imagelist.get(users - 1));
                                            Log.e("dis_list", dis_list.get(users - 1) + "");
                                            RecAdapterMap adapterMap = new RecAdapterMap(MapsActivity.this, imagelist, namelist, IDlist, dis_list, UID);
                                            recyclerView.setAdapter(adapterMap);
                                            adapterMap.notifyDataSetChanged();
                                        }
                                    } else {
                                        namelist.add(dataSnapshot.child(ds.getKey()).child("基本資料").child("暱稱").getValue(String.class));
                                        IDlist.add(ds.getKey());
                                        imagelist.add(dataSnapshot.child(ds.getKey()).child("基本資料").child("大頭照").getValue(String.class));
                                        dis_list.add(distance);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // 取得 GoogleMap 物件
        mMap = googleMap;
        LatLng Taipei101 = new LatLng(24.987516, 121.576074); // 台北 101
        zoom = 17;
        mMap.addMarker(new MarkerOptions().position(Taipei101).title("台北 101"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Taipei101, zoom));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);       // 一般地圖
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        // 檢查授權
        requestPermission();
    }

    // 檢查授權
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {  // Androis 6.0 以上
            // 判斷是否已取得授權
            int hasPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {  // 未取得授權
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
        }
        // 如果裝置版本是 Androis 6.0 以下，
        // 或是裝置版本是6.0（包含）以上，使用者已經授權
        setMyLocation(); //  顯示定位圖層
    }

    // 使用者完成授權的選擇以後，會呼叫 onRequestPermissionsResult 方法
    //     第一個參數：請求授權代碼
    //     第二個參數：請求的授權名稱
    //     第三個參數：使用者選擇授權的結果
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //按 允許 鈕
                setMyLocation(); //  顯示定位圖層
            } else {  //按 拒絕 鈕
                Toast.makeText(this, "未取得授權！", Toast.LENGTH_SHORT).show();
                finish();  // 結束應用程式
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //  顯示定位圖層
    private void setMyLocation() throws SecurityException {
        mMap.setMyLocationEnabled(true); // 顯示定位圖層
    }

    @Override
    public void onLocationChanged(Location location) {
        // 取得地圖座標值:緯度,經度
        String x = "緯=" + Double.toString(location.getLatitude());
        String y = "經=" + Double.toString(location.getLongitude());
        LatLng Point = new LatLng(location.getLatitude(), location.getLongitude());
        zoom = 17; //設定放大倍率1(地球)-21(街景)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Point, zoom));
        Toast.makeText(this, x + "\n" + y, Toast.LENGTH_LONG).show();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String UID = bundle.getString("UID");
        String Ref = "User/" + UID + "/基本資料/Location";
        DatabaseReference myRef = database.getReference(Ref);

        //寫入資料庫
        myRef.setValue(Point);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 取得定位服務
        locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 取得最佳定位
        Criteria criteria = new Criteria();
        bestProv = locMgr.getBestProvider(criteria, true);

        // 如果GPS或網路定位開啟，更新位置
        if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER) || locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //  確認 ACCESS_FINE_LOCATION 權限是否授權
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locMgr.requestLocationUpdates(bestProv, 1000, 1, this);
            }
        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //  確認 ACCESS_FINE_LOCATION 權限是否授權
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locMgr.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Criteria criteria = new Criteria();
        bestProv = locMgr.getBestProvider(criteria, true);
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("UID", UID);
        intent.putExtras(bundle);
        intent.setClass(MapsActivity.this, HomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}