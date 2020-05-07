package com.example.foodfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class TrailInfoActivity extends AppCompatActivity {


    private String TAG = "FoodFinder";
    private TextView textView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail_info);

        Intent mIntent = getIntent();
        String trailID = mIntent.getStringExtra("trail_id");
        readTrail("trails", trailID);
    }

    public void readTrail(String collection, String docID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection)
                .document(docID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                                if(document.exists()){
                                    //A trail to store 3 restaurants
                                    String trailName = (String) document.get("name");
                                    textView = findViewById(R.id.trail_name);
                                    textView.setText("Trail: " + trailName);

                                    ArrayList<HashMap<String,Object>> restaurantList= (ArrayList<HashMap<String, Object>>) document.get("restaurantList");
                                    int restCount = restaurantList.size();
                                    if(restCount > 0){
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        HashMap<String,Object> restaurant = (HashMap<String, Object>) restaurantList.get(0);
                                        String imageUri = (String) restaurant.get("imageUri");
                                        if(imageUri !=null){
                                            Log.d(TAG, "imageURI" + imageUri);
                                            imageView = findViewById(R.id.rest_image_1);
                                            Glide.with(getApplicationContext()).load(imageUri).into(imageView);
                                            imageView.setVisibility(View.VISIBLE);
                                        }

                                        textView = findViewById(R.id.rest_name_1);
                                        textView.setText(restaurant.get("name").toString());
                                        textView.setVisibility(View.VISIBLE);

                                        textView = findViewById(R.id.rating_num_1);
                                        textView.setText(restaurant.get("rating").toString());
                                        textView.setVisibility(View.VISIBLE);

                                        textView = findViewById(R.id.address_content_1);
                                        textView.setText(restaurant.get("address").toString());
                                        textView.setVisibility(View.VISIBLE);

                                        textView = findViewById(R.id.rating_1);
                                        textView.setVisibility(View.VISIBLE);
                                        textView = findViewById(R.id.address_1);
                                        textView.setVisibility(View.VISIBLE);
                                    }
                                    if (restCount > 1){
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        HashMap<String,Object> restaurant = (HashMap<String, Object>) restaurantList.get(1);
                                        String imageUri = (String) restaurant.get("imageUri");
                                        if(imageUri !=null){
                                            Uri uri = Uri.parse(imageUri);
                                            imageView = findViewById(R.id.rest_image_2);
                                            Glide.with(getApplicationContext()).load(imageUri).into(imageView);
                                            imageView.setVisibility(View.VISIBLE);
                                        }

                                        textView = findViewById(R.id.rest_name_2);
                                        textView.setText(restaurant.get("name").toString());
                                        textView.setVisibility(View.VISIBLE);

                                        textView = findViewById(R.id.rating_num_2);
                                        textView.setText(restaurant.get("rating").toString());
                                        textView.setVisibility(View.VISIBLE);

                                        textView = findViewById(R.id.address_content_2);
                                        textView.setText(restaurant.get("address").toString());
                                        textView.setVisibility(View.VISIBLE);

                                        ImageView imageView = findViewById(R.id.rest_image_2);
                                        imageView.setVisibility(View.VISIBLE);
                                        textView = findViewById(R.id.rating_2);
                                        textView.setVisibility(View.VISIBLE);
                                        textView = findViewById(R.id.address_2);
                                        textView.setVisibility(View.VISIBLE);
                                    }
                                    if (restCount > 2){
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        HashMap<String,Object> restaurant = (HashMap<String, Object>) restaurantList.get(2);
                                        String imageUri = (String) restaurant.get("imageUri");
                                        if(imageUri !=null){
                                            Uri uri = Uri.parse(imageUri);
                                            imageView = findViewById(R.id.rest_image_3);
                                            Glide.with(getApplicationContext()).load(imageUri).into(imageView);
                                            imageView.setVisibility(View.VISIBLE);
                                        }

                                        textView = findViewById(R.id.rest_name_3);
                                        textView.setText(restaurant.get("name").toString());
                                        textView.setVisibility(View.VISIBLE);

                                        textView = findViewById(R.id.rating_num_3);
                                        textView.setText(restaurant.get("rating").toString());
                                        textView.setVisibility(View.VISIBLE);

                                        textView = findViewById(R.id.address_content_3);
                                        textView.setText(restaurant.get("address").toString());
                                        textView.setVisibility(View.VISIBLE);

                                        ImageView imageView = findViewById(R.id.rest_image_3);
                                        imageView.setVisibility(View.VISIBLE);
                                        textView = findViewById(R.id.rating_3);
                                        textView.setVisibility(View.VISIBLE);
                                        textView = findViewById(R.id.address_3);
                                        textView.setVisibility(View.VISIBLE);
                                    }
                                }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}
