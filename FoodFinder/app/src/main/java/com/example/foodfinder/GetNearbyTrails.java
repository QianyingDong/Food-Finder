package com.example.foodfinder;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Coding Cafe on 7/18/2018.
 */

public class GetNearbyTrails extends AsyncTask<Object, String, String>
{
    private String googleplaceData, url_cafe,url_bar,url_res,url_meald,url_mealt;
    private GoogleMap mMap;
    private String TAG = "FoodFinder";
  //  public AsyncResponseTrail asyncResponseTrail;
    public AsyncResponseTrail asyncResponseTrails;

    public void setOnAsyncResponseTrail(AsyncResponseTrail asyncResponseTrails)
    {
        this.asyncResponseTrails = asyncResponseTrails;

    }


    @Override
    protected String doInBackground(Object... objects)
    {
        mMap = (GoogleMap) objects[0];
        url_cafe = (String) objects[1];
        url_bar = (String) objects[2];
        url_res = (String) objects[3];
        url_meald = (String) objects[4];
        url_mealt = (String) objects[5];


        DownloadUrl downloadUrl = new DownloadUrl();
        try
        {
            String googleplaceData1 = downloadUrl.ReadTheURL(url_cafe);
            String googleplaceData2 = downloadUrl.ReadTheURL(url_bar);
            String googleplaceData3 = downloadUrl.ReadTheURL(url_res);

            String googleplaceData4 = downloadUrl.ReadTheURL(url_meald);

            String googleplaceData5 = downloadUrl.ReadTheURL(url_mealt);

            boolean FLAG = true;
            if(googleplaceData1.indexOf("ZERO_RESULTS")!= -1){
                if(googleplaceData2.indexOf("ZERO_RESULTS")!= -1){
                    if(googleplaceData3.indexOf("ZERO_RESULTS")!= -1){
                        if(googleplaceData4.indexOf("ZERO_RESULTS")!= -1){
                            if(googleplaceData5.indexOf("ZERO_RESULTS")!= -1){
                                FLAG  = false;
                            }
                        }
                    }
                }

            }

            if(FLAG){
                googleplaceData = googleplaceData1+googleplaceData2+googleplaceData3+googleplaceData4
                        +googleplaceData5;
            }else{
                googleplaceData = null;
            }



        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return googleplaceData;
    }


    @Override

    protected void onPostExecute(String s)
    {
        if(s != null){
            Set<HashMap<String, Object>> nearByPlacesSet = null;
            DataParser dataParser = new DataParser();
            nearByPlacesSet = dataParser.parse(s);

            System.out.print("This is a test");
            System.out.print(nearByPlacesSet);



            //Store restaurants to database
            Database db = new Database();
            int count = 0;
            for(HashMap<String, Object> nearbyFood : nearByPlacesSet){
                //send to database
                db.store("restaurants",nearbyFood, Integer.toString(count));
                count++;
            }

            //create 3 recommended trails
            db.generateAutoTrails("restaurants","rating");

            readAutoTrails("restaurants", "rating");
            asyncResponseTrails.onDataReceivedSuccess();
        }else{
            asyncResponseTrails.onDataReceivedFailed();
        }


    }


    private void DisplayNearbyPlaces(Set<HashMap<String, Object>> nearByPlacesSet)
    {
        for(HashMap<String, Object> googleNearbyPlace:nearByPlacesSet){
            MarkerOptions markerOptions = new MarkerOptions();

            // HashMap<String, String> googleNearbyPlace = nearByPlacesList.get(i);
            String nameOfPlace = googleNearbyPlace.get("place_name").toString();
            String vicinity = googleNearbyPlace.get("vicinity").toString();
            String rating = googleNearbyPlace.get("rating").toString();
            Object geoObject = googleNearbyPlace.get("location");
            String resId = googleNearbyPlace.get("place_id").toString();

            GeoPoint geoLocation = (GeoPoint) geoObject;

            double lat = geoLocation.getLatitude();
            double lng = geoLocation.getLongitude();


            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            //markerOptions.title(nameOfPlace + " : " + vicinity);
            markerOptions.title(nameOfPlace + ":" + vicinity+":"+rating);
           // markerOptions.title(nameOfPlace + " : " + rating);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
    }

    private void DisplayNearbyTrails(List<HashMap<String,Object>> trail,int color_R,int color_G,int color_B){




        List<LatLng> latLngs = new ArrayList<>();
        for(HashMap<String, Object> googleNearbyPlace:trail){

            Object geoObject = googleNearbyPlace.get("location");

            System.out.println("print geoLocation");
            System.out.println(geoObject);

            GeoPoint geoLocation = (GeoPoint) geoObject;

            System.out.println("print geoPoint");
            System.out.println(geoLocation);

            double lat = geoLocation.getLatitude();
            double lng = geoLocation.getLongitude();
            LatLng latLng = new LatLng(lat, lng);
            latLngs.add(latLng);


        }
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(latLngs.get(0), latLngs.get(1), latLngs.get(2))
                .width(10)
                .color(Color.rgb(color_R,color_G,color_B)));
        // System.out.println("123456789");
        //line.isVisible(TRUE);
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
                            List<HashMap<String,Object>> trail_1 = new ArrayList<>();
                            List<HashMap<String,Object>> trail_2 = new ArrayList<>();
                            List<HashMap<String,Object>> trail_3 = new ArrayList<>();
                            int restCount = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.exists()){
                                    //A trail to store 3 restaurants
                                    if(restCount < 3) {
                                        Log.d("Restaurants", document.getId() + " => " + document.getData());
                                        HashMap<String, Object> restaurant = (HashMap<String, Object>) document.getData();
                                        trail_1.add(restaurant);
                                        if (restCount == 2 ) {
                                            trails.add(trail_1);
                                            Log.d(TAG, "Trail List" + trail_1);
                                        }
                                    }else if (restCount > 2 && restCount <6){
                                        Log.d("Restaurants", document.getId() + " => " + document.getData());
                                        HashMap<String, Object> restaurant = (HashMap<String, Object>) document.getData();
                                        trail_2.add(restaurant);
                                        if (restCount == 5 ) {
                                            trails.add(trail_2);
                                            Log.d(TAG, "Trail List" + trail_2);
                                        }
                                    }else if (restCount > 5 && restCount <9){
                                        Log.d("Restaurants", document.getId() + " => " + document.getData());
                                        HashMap<String, Object> restaurant = (HashMap<String, Object>) document.getData();
                                        trail_3.add(restaurant);
                                        if (restCount == 8) {
                                            trails.add(trail_3);
                                            Log.d(TAG, "Trail List" + trail_3);
                                        }
                                    }
                                }
                                restCount++;
                            }
                            //view <-- data
                            System.out.println("This is trailsShow111111");
                            System.out.println(trails);
                            DisplayNearbyTrails(trails.get(0),255,0,0);//red line
                            DisplayNearbyTrails(trails.get(1),0,255,0);//green line
                            DisplayNearbyTrails(trails.get(2),0,0,255);//blue line

                            Set<HashMap<String, Object>> trailFoodSet = new HashSet<>();
                            for(List<HashMap<String, Object>> foodtrail:trails){
                                for (int i=0; i<foodtrail.size(); i++)
                                {
                                    trailFoodSet.add(foodtrail.get(i));
                                }
                            }
                            DisplayNearbyPlaces(trailFoodSet);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }



}
