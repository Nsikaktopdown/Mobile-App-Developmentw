package com.example.bhanu.controller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.model.Review;
import com.model.UserDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bhanugoban Nadar on 2/16/2018.
 */

public class Review_List extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String name=UserDatabase.personName;
    String res_name;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_view);
        String placeId;
        Bundle extras = getIntent().getExtras();
        placeId = extras.getString("placeId");
        res_name = extras.getString("name");
        DocumentReference PdocRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("Reviews").document(res_name);
        PdocRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ArrayList<Review> word=new ArrayList<>();
                        if (task.isSuccessful()) {
                               Map<String, Object> reviewMap = new HashMap<>();
                               if(task.getResult().exists()) {


                                   reviewMap = task.getResult().getData();
                                   String review = reviewMap.get("Review").toString();
                                   String email = reviewMap.get("email").toString();
                                   String name = reviewMap.get("name").toString();
                                   String rating = reviewMap.get("rating").toString();
                                   word.add(new Review(name, email, rating, review));
                                   Toast.makeText(Review_List.this, review, Toast.LENGTH_SHORT).show();
                               }
                        } else {
                            Log.w("Multiple", "Error getting documents: ", task.getException());
                        }
                        Review_Adapter adapter = new Review_Adapter(Review_List.this, word);

                        ListView listView = (ListView) findViewById(R.id.List);

                        listView.setAdapter(adapter);
                        Log.w("Repeat","check");
                    }

                });


    }
}
