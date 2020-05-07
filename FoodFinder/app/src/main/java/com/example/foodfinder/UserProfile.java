package com.example.foodfinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {
    private String TAG = "User profile";
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        linearLayout = findViewById(R.id.user_trail_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        readTrail("users", userUID);

//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//        StorageReference imagesRef = storageRef.child("images");
//        StorageReference userPicRef = imagesRef.child("images/profile.jpg");
//        ImageView userPicView = findViewById(R.id.image_user);
//        final long ONE_MEGABYTE = 1024 * 1024;
//        userPicRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // Data for "images/user.png" is returns, use this as needed
//                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                    userPicView.setImageBitmap(bmp);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                // Handle any errors
//                Log.w(TAG, "Error retrieving user picture", e);
//                Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
//            }
//        });

//        loadWithGlide();
    }

    public void readTrail(String collection, String documentID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection)
                .document(documentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                try {
                                    ArrayList<DocumentReference> trailList = (ArrayList<DocumentReference>) documentSnapshot.get("trailList");

                                    if(trailList == null){
                                        //No trail existing
                                        TextView tv = new TextView(getApplicationContext());
                                        tv.setText("No food trail has been added yet.\n\n" +
                                                "Try to find nearby restaurants ! ");
                                        tv.setTextSize(20);
                                        RelativeLayout.LayoutParams textView_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        textView_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                        textView_params.addRule(RelativeLayout.ALIGN_PARENT_START);
                                        linearLayout.addView(tv,textView_params);
                                    }else {

                                        //Total number of trails
                                        int size = trailList.size();
                                        for (final DocumentReference trail : trailList) {
                                            //Get each trail name
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            db.collection("trails")
                                                    .document(trail.getId())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                if (documentSnapshot.exists()) {
                                                                    Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                                                    String trailname = documentSnapshot.get("name").toString();
                                                                    ArrayList restaurantList = (ArrayList) documentSnapshot.get("restaurantList");
                                                                    int size = restaurantList.size();
                                                                    //create trail view
                                                                    RelativeLayout relativeLayout = new RelativeLayout(new ContextThemeWrapper(getApplicationContext(), R.style.TrailList_Relativelayout));
                                                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                                                                    relativeLayout.setLayoutParams(params);

                                                                    TextView textView = new TextView(getApplicationContext());
                                                                    textView.setText(trailname);
                                                                    textView.setPadding(20, 20, 20, 20);
                                                                    textView.setTextAppearance(getApplicationContext(), R.style.TrailList_TextView);
                                                                    RelativeLayout.LayoutParams textView_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                                    textView_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                                                    textView_params.addRule(RelativeLayout.ALIGN_PARENT_START);

                                                                    TextView tv2 = new TextView(getApplicationContext());
                                                                    tv2.setText(documentSnapshot.getId());
                                                                    tv2.setVisibility(View.INVISIBLE);

                                                                    ImageView imageView = new ImageView(getApplicationContext());
                                                                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                                                    imageView.setImageResource(R.drawable.ic_arrow);

                                                                    RelativeLayout.LayoutParams imageView_params = new RelativeLayout.LayoutParams(70, 70);
                                                                    imageView_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                                                    imageView_params.addRule(RelativeLayout.ALIGN_PARENT_END);
                                                                    imageView_params.setMargins(10, 20, 10, 10);

                                                                    int relID = View.generateViewId();
                                                                    tv2.setId(relID + 1);
                                                                    relativeLayout.setId(relID);
                                                                    relativeLayout.addView(textView, textView_params);
                                                                    relativeLayout.addView(imageView, imageView_params);
                                                                    linearLayout.addView(tv2, textView_params);

                                                                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            Intent intent = new Intent(UserProfile.this, TrailInfoActivity.class);
                                                                            int relID = v.getId();
                                                                            int tvID = relID + 1;
                                                                            TextView tv = findViewById(tvID);
                                                                            String trailID = tv.getText().toString();
                                                                            intent.putExtra("trail_id", trailID);
                                                                            startActivity(intent);
                                                                        }
                                                                    });

                                                                    linearLayout.addView(relativeLayout);
                                                                }
                                                            } else {
                                                                Log.w(TAG, "Error getting documents.", task.getException());
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                } catch (NullPointerException e){
                                    Log.w(TAG, "Error getting trail list.", e);
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

//    public void loadWithGlide() {
//        // [START storage_load_with_glide]
//        // Reference to an image file in Cloud Storage
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/profile.jpg");
//
//        // ImageView in your Activity
//        ImageView imageView = findViewById(R.id.image_user);
//
//        // Download directly from StorageReference using Glide
//        // (See MyAppGlideModule for Loader registration)
//        Glide.with(this /* context */)
//                .load(storageReference)
//                .into(imageView);
//        // [END storage_load_with_glide]
//    }

}
