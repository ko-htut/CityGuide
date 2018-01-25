package com.albertotorresgi.cityguide.fragments;

import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.albertotorresgi.cityguide.R;
import com.albertotorresgi.cityguide.adapters.PlaceAdapter;
import com.albertotorresgi.cityguide.models.ApiResponse;
import com.albertotorresgi.cityguide.models.Place;
import com.albertotorresgi.cityguide.models.PlaceResultSet;
import com.albertotorresgi.cityguide.models.PlaceType;
import com.albertotorresgi.cityguide.utils.Constants;
import com.albertotorresgi.cityguide.utils.Utils;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * receive the answer of the places request and manage the Swipe refresh
 */
public class PlaceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "PlaceFragment";
    private static final String ARG_TYPE = "arg_type";

    PlaceAdapter mPlaceAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.place_list) RecyclerView place_list;
    private PlaceType mPlaceType;

    public static PlaceFragment newInstance(PlaceType placeType) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_TYPE, placeType);
        PlaceFragment fragment = new PlaceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlaceType = getArguments().getParcelable(ARG_TYPE);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        mSwipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(
                R.layout.fragment_place, container, false);
        ButterKnife.bind(this, mSwipeRefreshLayout);

        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mPlaceAdapter = new PlaceAdapter(getResources());
        place_list.setAdapter(mPlaceAdapter);
        place_list.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        place_list.setLayoutManager(layoutManager);
        place_list.setItemAnimator(new DefaultItemAnimator());

        return mSwipeRefreshLayout;
    }



    @Subscribe
    public void onEvent(final ApiResponse apiResponse){
        Log.i(TAG, "zz apiResponse="+apiResponse.getResponse());

        if(apiResponse.getCode()==200){
            Log.i(TAG, "zz 200");

            if (apiResponse.getType_request() == Constants.REQUEST_BAR_INDEX) {
                fetchData(Constants.REQUEST_BAR_INDEX, apiResponse.getLocation(), apiResponse.getResponse());
            }else if (apiResponse.getType_request() == Constants.REQUEST_BISTRO_INDEX) {
                fetchData(Constants.REQUEST_BISTRO_INDEX, apiResponse.getLocation(), apiResponse.getResponse());
            }else if (apiResponse.getType_request() == Constants.REQUEST_CAFE_INDEX) {
                fetchData(Constants.REQUEST_CAFE_INDEX, apiResponse.getLocation(), apiResponse.getResponse());
            }

        }else{
            //TODO: handler errors
        }
    }

    private void fetchData(final int typeRequest, final Location location, final String response){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                try {
                    ImmutableList.Builder<Place> placeItems = ImmutableList.builder();
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    final JSONArray jsonArray = new JSONArray(jsonObjectResponse.getString("results"));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Gson gson = new Gson();

                        Place place = gson.fromJson(jsonArray.get(i).toString(), Place.class);
                        place.setDistance(Utils.computeDistanceInMiles(location, jsonArray.get(i).toString()));
                        placeItems.add(place);
                    }

                    if(typeRequest == Constants.REQUEST_BAR_INDEX){
                        final PlaceResultSet placeItemResultSet =
                                new PlaceResultSet(placeItems.build(), PlaceType.BAR, true);
                        onNewPlaceResultSet(placeItemResultSet);
                    }else if(typeRequest == Constants.REQUEST_BISTRO_INDEX){
                        final PlaceResultSet placeItemResultSet =
                                new PlaceResultSet(placeItems.build(), PlaceType.BISTRO, true);
                        onNewPlaceResultSet(placeItemResultSet);
                    }else if(typeRequest == Constants.REQUEST_CAFE_INDEX){
                        final PlaceResultSet placeItemResultSet =
                                new PlaceResultSet(placeItems.build(), PlaceType.CAFE, true);
                        onNewPlaceResultSet(placeItemResultSet);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void onNewPlaceResultSet(PlaceResultSet placeResultSet) {
        if (!placeResultSet.didRequestSucceed) {
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        if (placeResultSet.placeType != mPlaceType) {
            return;
        }

        mPlaceAdapter.setPlaceResultSet(placeResultSet);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh() {
        //TODO: make refresh call
        //Dummy
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }.start();

    }
}
