package com.example.bhanu.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.model.RestaurantDatabase;
import com.model.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Bhanugoban Nadar on 1/28/2018.
 */

public class restaurantActivity extends AppCompatActivity implements MenuAdapter.ItemClickListener {

    String placeId;
    String req = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    String apikey = "&key=AIzaSyA_PUIMp_dv0hwSIDAUIvooEVahgJktUIU";
    String real;
    ImageView restaurant_profile;
    List<FoodMenu> foodList;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);
        Bundle extras = getIntent().getExtras();

        ImageView call = (ImageView) findViewById(R.id.call_op);
        ImageView been_here = (ImageView) findViewById(R.id.been_here);
        ImageView bookmark = (ImageView) findViewById(R.id.bookmark_image);
        ImageView review = (ImageView) findViewById(R.id.review_image);
        ImageView pics = (ImageView) findViewById(R.id.call_op);
        restaurant_profile = (ImageView) findViewById(R.id.restaurant_image);
        been_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestaurantDatabase restaurantDatabase = new RestaurantDatabase();

                restaurantDatabase.incrementBeen_here(placeId);
            }
        });
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestaurantDatabase restaurantDatabase = new RestaurantDatabase();

                restaurantDatabase.incrementBookMark(placeId);
            }
        });

        final TextView restaurant_rating = (TextView) findViewById(R.id.restaurant_rating);
        final TextView restaurant_name = (TextView) findViewById(R.id.restaurant_name);
        final TextView restaurant_address_small = (TextView) findViewById(R.id.restaurant_address);
        final TextView restaurant_timing = (TextView) findViewById(R.id.restaurant_timing);
        final TextView restaurant_basic = (TextView) findViewById(R.id.restaurant_basic);
        final TextView restaurant_phonenumber = (TextView) findViewById(R.id.restaurant_phonenumber);
        final TextView restaurant_full_address = (TextView) findViewById(R.id.full_address);
        TextView restaurant_average_cost = (TextView) findViewById(R.id.restaurant_average_cost);
        final TextView restaurant_opening_hours = (TextView) findViewById(R.id.opening_hours);
        final TextView restaurant_map = (TextView) findViewById(R.id.restaurant_map);
        final TextView restaurant_rating2 = (TextView) findViewById(R.id.restaurant_rating2);
        final TextView reve = (TextView) findViewById(R.id.numbers_reviews);
        final TextView forX = (TextView) findViewById(R.id.reviews);
        reve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent numbersIntent = new Intent(restaurantActivity.this, Review_List.class);
                numbersIntent.putExtra("name", restaurant_name.getText().toString());
                numbersIntent.putExtra("placeId", placeId);
                startActivity(numbersIntent);
            }
        });
        final TextView number_of_reviews = (TextView) findViewById(R.id.numbers_reviews);
        ImageView restaurant_profile = (ImageView) findViewById(R.id.restaurant_image);
        Button button = (Button) findViewById(R.id.add_review);
        if (extras != null) {
            placeId = extras.getString("placeId");
            Log.e("id", placeId);
            //The key argument here must match that used in the other activity
        }
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Addreview.class);
                i.putExtra("name", restaurant_name.getText().toString());
                i.putExtra("placeId", placeId);
                startActivity(i);
            }
        });

        number_of_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Review_List.class);
                i.putExtra("name", restaurant_name.getText().toString());
                startActivity(i);
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + restaurant_phonenumber.getText().toString()));
                startActivity(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Addreview.class);

                i.putExtra("name", restaurant_name.getText().toString());
                i.putExtra("placeId", placeId);
                startActivity(i);
            }
        });

        String req = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
        real = req + placeId + apikey;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, real, null, new Response.Listener<JSONObject>() {
                    int i;

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject result = response.getJSONObject("result");
                            restaurant_name.setText(result.getString("name"));
                            restaurant_address_small.setText("");
                            JSONArray addr = result.getJSONArray("address_components");
                            for (i = 0; i < addr.length(); i++) {
                                JSONObject local = addr.getJSONObject(i);
                                JSONArray type = local.getJSONArray("types");
                                for (int j = 0; j < type.length(); j++) {
                                    String s = type.getString(j);
                                    if (s.equals("sublocality_level_2") || s.equals("sublocality_level_1")) {
                                        restaurant_address_small.append(local.getString("short_name") + " ");
                                    }
                                }
                            }
                            restaurant_opening_hours.setText("");
                            JSONObject open = result.getJSONObject("opening_hours");
                            JSONArray working_hours = open.getJSONArray("weekday_text");

                            for (i = 0; i < working_hours.length(); i++) {
                                restaurant_opening_hours.append(working_hours.getString(i) + "\n");
                            }
                            boolean a = open.getBoolean("open_now");
                            if (a) {
                                restaurant_timing.setText("Open Now");
                                restaurant_timing.setTextColor(Color.parseColor("#008000"));
                            } else {
                                restaurant_timing.setText("Closed Now");
                                restaurant_timing.setTextColor(Color.parseColor("#ff0000"));
                            }
                            final String url2 = result.getString("url");
                            restaurant_map.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    String url = url2;

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
// Access the RequestQueue through your singleton class.
        Singleton.getInstance(this).addToRequestQueue(jsObjRequest);
        //////////////////////////firebase time
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Restaurant").document(placeId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.w(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                        Log.w(TAG, "DocumentSnapshot data: " + document.get("rating"));
                        restaurant_rating.setText(document.get("rating").toString());

                        restaurant_rating2.setText(document.get("rating").toString());
                        String rev = document.get("reviews").toString();
                        String bookm = document.get("bookmark").toString();
                        String been = document.get("been_here").toString();
                        restaurant_basic.setText(rev + " Reviews||" + bookm + " Bookmark||" + been + " Been Here");
                        restaurant_full_address.setText(document.get("address").toString());
                        forX.setText("Based on " + rev + " Reviews");
                        number_of_reviews.setText("Read All Reviews(" + rev + ")");
                        restaurant_phonenumber.setText(document.get("phonenumber").toString());
                    } else {
                        Log.w(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        getPhotos();

        foodList = new ArrayList<FoodMenu>();
        foodList.add(new FoodMenu("Jollof Rice", "https://res.cloudinary.com/hngfun/image/upload/v1541400404/jollof_rice_vdoty8.jpg", 2000));
        foodList.add(new FoodMenu("Burger", "https://res.cloudinary.com/hngfun/image/upload/v1541400403/burger_y6pf80.jpg", 4000));
        foodList.add(new FoodMenu("Afang Soup", "https://res.cloudinary.com/hngfun/image/upload/v1541400409/afang_qepgyi.jpg", 1500));
        foodList.add(new FoodMenu("Chips & Chicken", "https://res.cloudinary.com/hngfun/image/upload/v1541400403/chips_and_chicken_mm05wn.jpg", 2500));
        // set up the RecyclerView
        recyclerView = findViewById(R.id.menuRecyclerViewHome);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        MenuAdapter adapter = new MenuAdapter(this, foodList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void getPhotos() {
        final String placeId2 = placeId;
        final GeoDataClient mGeoDataClient = Places.getGeoDataClient(this, null);
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId2);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                if (photos != null) {
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                PlacePhotoMetadata photoMetadata = null;

                    // Get the first photo in the list.
                    try{
                        if(photoMetadataBuffer != null){
                            photoMetadata = photoMetadataBuffer.get(0);
                        }else {
                            Log.e("", "no image");
                        }

                    }catch (IllegalStateException e){

                        recyclerView.setVisibility(View.GONE);
                    }

                        if(photoMetadata != null){


                        // Get a full-size bitmap for the photo.
                        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                PlacePhotoResponse photo = task.getResult();
                                Bitmap bitmap = photo.getBitmap();

                                restaurant_profile.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "No Images", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void setdata() {

    }


    @Override
    public void onItemClick(View view, int position) {
        FoodMenu food = foodList.get(position);
        new RavePayManager(this).setAmount(food.amount)
                .setCountry("Nigeria")
                .setCurrency("NGN")
                .setEmail("ann@gmal.com")
                .setfName("Hannah")
                .setlName("Hyoko")
                .setNarration(food.name)
                .setPublicKey("FLWPUBK-788826b45061a577522abd4b8137604f-X")
                .setSecretKey("FLWSECK-03ad1f2d8226f614153d15c4bda70feb-X")
                .setTxRef(food.name + "yikndm")
                .acceptAccountPayments(true)
                .acceptCardPayments(true)
                .onStagingEnv(false)
                .allowSaveCardFeature(true)
                .withTheme(R.style.DefaultTheme)
                .initialize();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Toast.makeText(this, "SUCCESS " + message, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}