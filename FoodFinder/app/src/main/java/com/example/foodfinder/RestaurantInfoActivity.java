package com.example.foodfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestaurantInfoActivity extends AppCompatActivity {

    private String TAG = "FoodFinder";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);

        Intent mIntent = getIntent();
        int intValue = mIntent.getIntExtra("trail_num", 0);

        switch (intValue){
            case 1:
                //read first trail
                readFirstTrail("restaurants", "rating");
                mIntent.removeExtra("trail_num");
                System.out.println(intValue);
                break;
            case 2:
                //read first trail
                readSecondTrail("restaurants", "rating");
                mIntent.removeExtra("trail_num");
                System.out.println(intValue);
                break;
            case 3:
                //read first trail
                readThirdTrail("restaurants", "rating");
                mIntent.removeExtra("trail_num");
                System.out.println(intValue);
                break;

        }
    }

    public void readFirstTrail(String collection, String orderField){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection)
                .orderBy(orderField, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int restCount = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.exists()){
                                    //A trail to store 3 restaurants
                                    if(restCount == 0 ){
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        HashMap<String,Object> restaurant = (HashMap<String, Object>) document.getData();
                                        textView = findViewById(R.id.rest_name_1);
                                        textView.setText(restaurant.get("place_name").toString());

                                        textView = findViewById(R.id.rating_num_1);
                                        textView.setText(restaurant.get("rating").toString());

                                        textView = findViewById(R.id.address_content_1);
                                        textView.setText(restaurant.get("vicinity").toString());
                                    }else if (restCount == 1){
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        HashMap<String,Object> restaurant = (HashMap<String, Object>) document.getData();
                                        textView = findViewById(R.id.rest_name_2);
                                        textView.setText(restaurant.get("place_name").toString());

                                        textView = findViewById(R.id.rating_num_2);
                                        textView.setText(restaurant.get("rating").toString());

                                        textView = findViewById(R.id.address_content_2);
                                        textView.setText(restaurant.get("vicinity").toString());
                                    }else if (restCount == 2){
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        HashMap<String,Object> restaurant = (HashMap<String, Object>) document.getData();
                                        textView = findViewById(R.id.rest_name_3);
                                        textView.setText(restaurant.get("place_name").toString());

                                        textView = findViewById(R.id.rating_num_3);
                                        textView.setText(restaurant.get("rating").toString());

                                        textView = findViewById(R.id.address_content_3);
                                        textView.setText(restaurant.get("vicinity").toString());
                                    }
                                }
                                restCount++;
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void readSecondTrail(String collection, String orderField){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection)
                .orderBy(orderField, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int restCount = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.exists()){
                                    //A trail to store 3 restaurants
                                    if(restCount == 3 ){
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        HashMap<String,Object> restaurant = (HashMap<String, Object>) document.getData();
                                        textView = findViewById(R.id.rest_name_1);
                                        textView.setText(restaurant.get("place_name").toString());

                                        textView = findViewById(R.id.rating_num_1);
                                        textView.setText(restaurant.get("rating").toString());

                                        textView = findViewById(R.id.address_content_1);
                                        textView.setText(restaurant.get("vicinity").toString());
                                    }else if (restCount == 4){
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        HashMap<String,Object> restaurant = (HashMap<String, Object>) document.getData();
                                        textView = findViewById(R.id.rest_name_2);
                                        textView.setText(restaurant.get("place_name").toString());

                                        textView = findViewById(R.id.rating_num_2);
                                        textView.setText(restaurant.get("rating").toString());

                                        textView = findViewById(R.id.address_content_2);
                                        textView.setText(restaurant.get("vicinity").toString());
                                    }else if (restCount == 5){
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        HashMap<String,Object> restaurant = (HashMap<String, Object>) document.getData();
                                        textView = findViewById(R.id.rest_name_3);
                                        textView.setText(restaurant.get("place_name").toString());

                                        textView = findViewById(R.id.rating_num_3);
                                        textView.setText(restaurant.get("rating").toString());

                                        textView = findViewById(R.id.address_content_3);
                                        textView.setText(restaurant.get("vicinity").toString());
                                    }
                                }
                                restCount++;
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void readThirdTrail(String collection, String orderField){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection)
                .orderBy(orderField, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int restCount = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.exists()){
                                    //A trail to store 3 restaurants
                                    if(restCount == 6 ){
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        HashMap<String,Object> restaurant = (HashMap<String, Object>) document.getData();
                                        textView = findViewById(R.id.rest_name_1);
                                        textView.setText(restaurant.get("place_name").toString());

                                        textView = findViewById(R.id.rating_num_1);
                                        textView.setText(restaurant.get("rating").toString());

                                        textView = findViewById(R.id.address_content_1);
                                        textView.setText(restaurant.get("vicinity").toString());
                                    }else if (restCount == 7){
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        HashMap<String,Object> restaurant = (HashMap<String, Object>) document.getData();
                                        textView = findViewById(R.id.rest_name_2);
                                        textView.setText(restaurant.get("place_name").toString());

                                        textView = findViewById(R.id.rating_num_2);
                                        textView.setText(restaurant.get("rating").toString());

                                        textView = findViewById(R.id.address_content_2);
                                        textView.setText(restaurant.get("vicinity").toString());
                                    }else if (restCount == 8){
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        HashMap<String,Object> restaurant = (HashMap<String, Object>) document.getData();
                                        textView = findViewById(R.id.rest_name_3);
                                        textView.setText(restaurant.get("place_name").toString());

                                        textView = findViewById(R.id.rating_num_3);
                                        textView.setText(restaurant.get("rating").toString());

                                        textView = findViewById(R.id.address_content_3);
                                        textView.setText(restaurant.get("vicinity").toString());
                                    }
                                }
                                restCount++;
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
