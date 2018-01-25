package com.albertotorresgi.cityguide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.albertotorresgi.cityguide.adapters.PagerAdapter;
import com.albertotorresgi.cityguide.models.ApiResponse;
import com.albertotorresgi.cityguide.models.PlaceType;
import com.albertotorresgi.cityguide.services.MyApi;
import com.albertotorresgi.cityguide.utils.Constants;
import com.albertotorresgi.cityguide.views.SlidingTabView;
import com.google.common.base.Joiner;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        SlidingTabView.TabClickListener  {

    private static String TAG="MainActivity";
    private Context mContext;

    @BindView(R.id.pager) ViewPager mPager;
    @BindView(R.id.sliding_tab) SlidingTabView mSlidingTabView;

    PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPager.setOnPageChangeListener(mSlidingTabView);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(2); // ensures all the tabs are kept attached.
        mSlidingTabView.setOnTabClickListener(this);

        askPermissions();
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @SuppressLint("MissingPermission")
        @Override
        public void onPermissionGranted() {
            Log.d(TAG, "zz onPermissionGranted");
            getNearPlaces();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(mContext, "Missing Permission \n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }

    };

    private void askPermissions() {
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Permisssion required")
                .setPermissions(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .check();
    }

    private void getNearPlaces(){

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        try{
            @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            Log.d(TAG, "zz scanning location");
            if (location != null) {
                Log.d(TAG, "zz with location lat="+location.getLatitude());
                Log.d(TAG, "zz with location lon="+location.getLongitude());

                makeCallRequest(location, Constants.REQUEST_BAR_INDEX);
                makeCallRequest(location, Constants.REQUEST_BISTRO_INDEX);
                makeCallRequest(location, Constants.REQUEST_CAFE_INDEX);
            }else{
                Log.d(TAG, "zz NO location");
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void makeCallRequest(Location location, int typeRequest){
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", Constants.GOOGLE_API_KEY);
        //params.put("location", "19.4153106,-99.1848389");
        params.put("location", location.getLatitude() + "," + location.getLongitude());
        params.put("radius", "5000" /* meters */);

        if(typeRequest==Constants.REQUEST_BAR_INDEX){
            params.put("types", Joiner.on("|").join(PlaceType.BAR.matchingPlaceTypes));
            MyApi.customGet(mContext, Constants.BASE_URL, params, Constants.REQUEST_BAR_INDEX, location);
        }else if(typeRequest==Constants.REQUEST_BISTRO_INDEX){
            params.put("types", Joiner.on("|").join(PlaceType.BISTRO.matchingPlaceTypes));
            MyApi.customGet(mContext, Constants.BASE_URL, params, Constants.REQUEST_BISTRO_INDEX, location);
        }else if(typeRequest==Constants.REQUEST_CAFE_INDEX){
            params.put("types", Joiner.on("|").join(PlaceType.CAFE.matchingPlaceTypes));
            MyApi.customGet(mContext, Constants.BASE_URL, params, Constants.REQUEST_CAFE_INDEX, location);
        }
    }


    @Subscribe
    public void onEvent(final ApiResponse apiResponse){
        Log.i(TAG, "zz apiResponse="+apiResponse.getResponse());

        if(apiResponse.getCode()==200){
            Log.i(TAG, "zz 200");

            //TODO: handler errors
        }else if(apiResponse.getCode()==422){
            Log.i(TAG, "zz 422");
        }else{
            try {
                JSONObject jsonObject=new JSONObject(apiResponse.getResponse());
                String message = jsonObject.getString("message");
                Log.i(TAG, "zz code="+apiResponse.getResponse()+" message="+message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSlidingTabView.onPageSelected(mPager.getCurrentItem());
    }


    @Override
    public void onTabClick(int index) {
        mPager.setCurrentItem(index);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
