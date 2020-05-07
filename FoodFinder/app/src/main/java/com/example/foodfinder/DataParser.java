package com.example.foodfinder;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Coding Cafe on 7/18/2018.
 */

public class DataParser
{

    //private FirebaseFirestore mFirestore;

    private HashMap<String, Object> getSingleNearbyPlace(JSONObject googlePlaceJSON)
    {


        HashMap<String, Object> googlePlaceMap = new HashMap<>();
        String NameOfPlace = "-NA-";
        String vicinity = "-NA-";
        double latitude = 0;
        double rating = 0;
        double longitude = 0;
        String placeId = "";
       // String rate = "";
        GeoPoint geoPoint = null;

        try
        {
            if (!googlePlaceJSON.isNull("name"))
            {
                NameOfPlace = googlePlaceJSON.getString("name");
            }
            if (!googlePlaceJSON.isNull("vicinity"))
            {
                vicinity = googlePlaceJSON.getString("vicinity");
            }if(!googlePlaceJSON.isNull("rating"))
            {
               rating = googlePlaceJSON.getDouble("rating");
               BigDecimal bigDecimal = new BigDecimal(rating);
                rating   =   bigDecimal.setScale(1,   RoundingMode.HALF_UP).doubleValue();


               //rate = Double.toString(f1);

            }
            String lat = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            String longit = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            latitude = Double.parseDouble(lat);
            longitude = Double.parseDouble(longit);
            placeId = googlePlaceJSON.getString("place_id");


            googlePlaceMap.put("place_name", NameOfPlace);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("rating",rating);
            geoPoint = new GeoPoint(latitude,longitude);
            googlePlaceMap.put("location", geoPoint);
            //googlePlaceMap.put("lat", latitude);
           // googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("place_id", placeId);

            //System.out.println("single test");

            //System.out.println(googlePlaceMap);
            //send to firebase

           //CollectionReference restaurants = mFirestore.collection("trails");
            //restaurants.add(googlePlaceMap);
                    //.add(googlePlaceMap);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

       // System.out.println("232323232323232323");
        //System.out.println(googlePlaceMap);

        return googlePlaceMap;
    }



    private Set<HashMap<String, Object>> getAllNearbyPlaces(JSONArray jsonArray)
    {
        int counter = jsonArray.length();

        Set<HashMap<String, Object>> NearbyPlacesSet = new HashSet<>();

        HashMap<String, Object> NearbyPlaceMap = null;

        for (int i=0; i<counter; i++)
        {
            try
            {
                NearbyPlaceMap = getSingleNearbyPlace( (JSONObject) jsonArray.get(i) );
                NearbyPlacesSet.add(NearbyPlaceMap);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return NearbyPlacesSet;
    }



    public Set<HashMap<String, Object>> parse(String jSONdata)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try
        {
            jsonObject = new JSONObject(jSONdata);
            jsonArray = jsonObject.getJSONArray("results");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return getAllNearbyPlaces(jsonArray);
    }
}
