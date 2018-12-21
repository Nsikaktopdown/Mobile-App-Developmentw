package com.client;

import com.model.PlaceResponse;

import retrofit2.Call;
import retrofit2.http.GET;


public interface APIInterface {

    @GET("place/nearbysearch/json?location=4.64231,7.92438&radius=1500&type=restaurant&key=AIzaSyA_PUIMp_dv0hwSIDAUIvooEVahgJktUIU")
    Call<PlaceResponse> getNearbyRestaurant();
}
