package com.example.foodfinder;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Coding Cafe on 7/18/2018.
 */

public class GetNearbyPlaces extends AsyncTask<Object, String, String>
{
    private String googleplaceData, url_cafe,url_bar,url_res,url_meald,url_mealt;
    // private GoogleMap mMap;
    public AsyncResponsePlaces asyncResponsePlaces;

    public void setOnAsyncResponsePlaces(AsyncResponsePlaces asyncResponsePlaces)
    {
        this.asyncResponsePlaces = asyncResponsePlaces;

    }

    @Override
    protected String doInBackground(Object... objects)
    {
        // mMap = (GoogleMap) objects[0];
        url_cafe = (String) objects[0];
        url_bar = (String) objects[1];
        url_res = (String) objects[2];
        url_meald = (String) objects[3];
        url_mealt = (String) objects[4];


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
            asyncResponsePlaces.onDataReceivedSuccess(nearByPlacesSet);
        }else{
            asyncResponsePlaces.onDataReceivedFailed();
        }





    }


}
