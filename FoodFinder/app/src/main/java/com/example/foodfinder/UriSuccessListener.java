package com.example.foodfinder;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UriSuccessListener implements OnSuccessListener<Uri> {

    private String trailName;
    private String restNameString;
    private String restAddress;
    private Double rating;
    private Context mContext;
    private String TAG = "FoodFinder";

    public UriSuccessListener(String trailName, String restNameString, String restAddress, Double rating, Context mContext) {
        this.trailName = trailName;
        this.restNameString = restNameString;
        this.restAddress = restAddress;
        this.rating = rating;
        this.mContext = mContext;
    }

    @Override
    public void onSuccess(Uri uri) {
        System.out.println("The image Uri is: " + uri);

        HashMap<String, Object> createdTrail = new HashMap<>();
        createdTrail.put("name", trailName);

        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userIDReference =
                db.collection("users")
                        .document(userUID);
        createdTrail.put("userID", userIDReference);
        List<HashMap<String, Object>> restList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> restaurant = new HashMap<>();
        restaurant.put("imageUri", uri.toString());
        restaurant.put("name", restNameString);
        restaurant.put("address", restAddress);
        restaurant.put("rating", rating);
        System.out.println("The restaurant is: " + restaurant.toString());
        restList.add(restaurant);
        createdTrail.put("restaurantList", restList);
        db.collection("trails")
                .add(createdTrail)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Store reference trail ID in trail list of user collection
                        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users")
                                .document(userUID)
                                .update("trailList", FieldValue.arrayUnion(documentReference))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });
                        Toast.makeText(mContext, "Succeed to upload trail.", Toast.LENGTH_SHORT).show();
                        Log.d("UploadCreatedTrail", "Created Trail written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Fail to upload trail.", Toast.LENGTH_SHORT).show();
                        Log.w("UploadCreatedTrail", "Error adding trail", e);
                    }
                });
    }
}
