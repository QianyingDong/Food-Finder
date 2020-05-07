package com.example.foodfinder;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Database {

    private String TAG = "FoodFinder";
    private boolean existResult = false;
    private boolean updateResult = false;
    private boolean removeResult = false;

    public void store(String collection, HashMap<String,Object> document,
                      String name){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add a new document with an ID, merge if already exists
        db.collection(collection)
                .document(name)
                .set(document)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void generateAutoTrails(String collection, String orderField){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection)
                .orderBy(orderField, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            int size = task.getResult().size();
                            int trailNum = size/3;//quotient
                            if (trailNum > 3){
                                trailNum = 3;
                            }
                            int trailCount = 0;
                            int restCount = 0;
                            int restOrder = 0;
                            List<DocumentReference> restIDList = new ArrayList<DocumentReference>();
                            HashMap<String, Object> trail = new HashMap<>();
                            double rating = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.exists()){
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                    trailCount = restCount/3;
                                    restOrder = restCount%3;
//                                    Log.d(TAG, "trailCount " + trailCount
//                                            +" trailNum "+ trailNum
//                                            + " restaurant order " + restOrder
//                                            + " restaurant Count " + restCount);
                                    if(trailCount < trailNum){
                                        DocumentReference documentReference =
                                                db.collection("restaurants")
                                                        .document(document.getId());
                                        restIDList.add(documentReference);

                                        rating += Double.parseDouble(document.get("rating").toString());

                                        if(restOrder == 2){
                                            trail.put("trailRating", rating);
                                            trail.put("restaurantList",restIDList);

                                            // Add a new document with an ID, merge if already exists
                                            db.collection("autoTrails")
                                                    .document(Integer.toString(trailCount))
                                                    .set(trail)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error writing document", e);
                                                        }
                                                    });
                                            //Clear
                                            trail.clear();
                                            restIDList.clear();
                                            rating = 0;
                                        }
                                    }
                                    restCount++;
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void readAutoTrails(String collection, String orderField){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection)
                .orderBy(orderField, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //To store 3 trails
                            List<List<HashMap<String,Object>>> trails = new ArrayList<>();
                            List<HashMap<String,Object>> trail = new ArrayList<>();
                            int restCount = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.exists()){
                                    //A trail to store 3 restaurants
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    HashMap<String,Object> restaurant = (HashMap<String, Object>) document.getData();
                                    trail.add(restaurant);
                                    if(restCount == 2 || restCount == 5 || restCount == 8){
                                        trails.add(trail);
                                        trail.clear();
                                    }
                                }
                                restCount++;
                            }

                            //view <-- data


                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public boolean isExist(String collection, String key, Object value){
        existResult = false;
//        String key = "googlePlaceID";
//        Object value = document.get(key);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(collection)
                .whereEqualTo(key,value)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.exists()){
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    existResult = true;
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            existResult = false;
                        }
                    }
                });
        return existResult;
    }

    public void getDocID(String collection, String key, Object value){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection)
                .whereEqualTo(key,value)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.exists()){
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    String docID = document.getId();
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void readAll(String collection){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.exists()){
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public boolean update(String collection, String docID, String key, Object value){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection)
                .document(docID)
                .update(key, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        updateResult = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        updateResult = false;
                    }
                });
        return updateResult;
    }

    public void updateInArray(String collection, String docID, String key, Object value){
        // value : map (imageURL, restaurant ref ID)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection)
                .document(docID)
                .update(key, FieldValue.arrayUnion(value))
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
    }

    public boolean removeInArray(String collection, String docID, String key, Object value){
        // value : map (imageURL, restaurant ref ID)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection)
                .document(docID)
                .update(key, FieldValue.arrayRemove(value))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        removeResult = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        removeResult = false;
                    }
                });
        return removeResult;
    }

    public void remove(String collection, String docId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection)
                .document(docId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener < Void > () {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Delete successfully");
                    }
                });
    }

    //read trail + image + ref restaurants

}
