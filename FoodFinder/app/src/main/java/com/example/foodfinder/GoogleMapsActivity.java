package com.example.foodfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

//this version implement show trails on map

public class GoogleMapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnInfoWindowClickListener {

    static final int QUERY_PERSONAL_TRAIL = 0;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    private double latitide, longitude;
    private int ProximityRadiusTrail = 2000;
    private int ProximityRadiusNearby = 1000;
    private Button btn1, btn2, btn3;
    private boolean isButtonShow = false;
    private String TAG = "FoodFinder";
    private String API_KEY="YOUR_API_KEY"  // Add you google map API key
                

    public Set<HashMap<String, Object>> neabyRestaurant = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn1 = (Button) findViewById(R.id.btn_1st);
        btn2 = (Button) findViewById(R.id.btn_2nd);
        btn3 = (Button) findViewById(R.id.btn_3rd);
   /*     btn_addtrail = (Button) findViewById(R.id.btn_addtrail);
        btn_addtrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadFragment(new CardViewFragment());
                getFragmentManager().beginTransaction()
                        .add(R.id.container, CardViewFragment.newInstance())
                        .commit();
            }
        });*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(GoogleMapsActivity.this, UserProfile.class);
                startActivity(intent);
            }
        });

    }

    public void onClick(View v){

        String restaurant = "restaurant";
        Object transferData[] = new Object[6];
        Object transferDataNearby[] = new Object[5];
        GetNearbyTrails getNearbyTrails = new GetNearbyTrails();
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        String googleplaceData=null;

        switch (v.getId() ){

            case R.id.btn_find:
                mMap.clear();
                //set button visible


                System.out.println("neaby trails");
                System.out.println(latitide + " " + longitude);
                String url_cafe = getTrailUrl(latitide,longitude,"cafe");
                String url_bar = getTrailUrl(latitide,longitude,"bar");
                String url_res = getTrailUrl(latitide, longitude, restaurant);
                String url_meald = getTrailUrl(latitide,longitude,"meal_delivery");
                String url_mealt = getTrailUrl(latitide,longitude,"meal_takeaway");
                transferData[0] = mMap;
                transferData[1] = url_cafe;
                transferData[2] = url_bar;
                transferData[3] = url_res;
                transferData[4] = url_meald;
                transferData[5] = url_mealt;

                Toast.makeText(this, "Searching for Nearby Popular Trails...", Toast.LENGTH_LONG).show();

                //getNearbyPlaces.execute(transferData);

                getNearbyTrails.execute(transferData);

                getNearbyTrails.setOnAsyncResponseTrail(new AsyncResponseTrail() {
                    @Override
                    public void onDataReceivedSuccess() {
                        Toast.makeText(getApplicationContext(), "Showing Nearby Popular Trails...", Toast.LENGTH_SHORT).show();
                        btn1.setVisibility(View.VISIBLE);
                        btn2.setVisibility(View.VISIBLE);
                        btn3.setVisibility(View.VISIBLE);
                        isButtonShow = true;
                    }

                    @Override
                    public void onDataReceivedFailed() {
                        Toast.makeText(getApplicationContext(), "No Trails...", Toast.LENGTH_LONG).show();

                    }
                });



                break;




            case R.id.btn_nearby:
                mMap.clear();
                //set button invisible
                if(isButtonShow == true){
                    btn1 = (Button) findViewById(R.id.btn_1st);
                    btn1.setVisibility(View.INVISIBLE);
                    btn2 = (Button) findViewById(R.id.btn_2nd);
                    btn2.setVisibility(View.INVISIBLE);
                    btn3 = (Button) findViewById(R.id.btn_3rd);
                    btn3.setVisibility(View.INVISIBLE);
                }
                System.out.println("neaby location");
                System.out.println(latitide + " " + longitude);
                String url_cafe1 = getNearbyUrl(latitide,longitude,"cafe");
                String url_bar1 = getNearbyUrl(latitide,longitude,"bar");
                String url_res1 = getNearbyUrl(latitide, longitude, restaurant);
                String url_meald1 = getNearbyUrl(latitide,longitude,"meal_delivery");
                String url_mealt1 = getNearbyUrl(latitide,longitude,"meal_takeaway");
                //transferDataTrail[0] = mMap;
                transferDataNearby[0] = url_cafe1;
                transferDataNearby[1] = url_bar1;
                transferDataNearby[2] = url_res1;
                transferDataNearby[3] = url_meald1;
                transferDataNearby[4] = url_mealt1;


                Toast.makeText(this, "Searching for Nearby Restaurants...", Toast.LENGTH_LONG).show();

                getNearbyPlaces.execute(transferDataNearby);
               // getNearbyTrails.execute(transferData);
                getNearbyPlaces.setOnAsyncResponsePlaces(new AsyncResponsePlaces() {
                    @Override
                    public void onDataReceivedSuccess(Set<HashMap<String, Object>> nearbyPlaces) {

                        Toast.makeText(getApplicationContext(), "Showing Nearby Restaurants...", Toast.LENGTH_SHORT).show();
                        neabyRestaurant = nearbyPlaces;
                        DisplayNearbyPlaces(neabyRestaurant);


                    }

                    @Override
                    public void onDataReceivedFailed() {
                        Toast.makeText(getApplicationContext(), "No Restaurants", Toast.LENGTH_LONG).show();
                        System.out.println("NONONONONONON");
                    }
                });

                break;

            case R.id.btn_1st:

                if(isButtonShow == true) {
                    Intent intent = new Intent();
                    intent.putExtra("trail_num", 1);
                    intent.setClass(GoogleMapsActivity.this, RestaurantInfoActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.btn_2nd:

                if(isButtonShow == true){
                    Intent intent = new Intent();
                    intent.putExtra("trail_num", 2);
                    intent.setClass(GoogleMapsActivity.this, RestaurantInfoActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btn_3rd:

                if(isButtonShow == true){
                    System.out.println("3 rd button pressed");
                    Intent intent = new Intent();
                    intent.putExtra("trail_num", 3);
                    intent.setClass(GoogleMapsActivity.this, RestaurantInfoActivity.class);
                    startActivity(intent);
                }
                break;
        }

    }

    private String getTrailUrl(double latitide, double longitude, String nearbyPlace)
    {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitide + "," + longitude);
        googleURL.append("&radius=" + ProximityRadiusTrail);
        googleURL.append("&type=" + nearbyPlace);
        //googleURL.append("&type=" + "cafe");
        googleURL.append("&fields= rating");
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + API_KEY);


        Log.d("GoogleMapsActivity", "url = " + googleURL.toString());

        return googleURL.toString();
    }

    private String getNearbyUrl(double latitide, double longitude, String nearbyPlace)
    {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitide + "," + longitude);
        googleURL.append("&radius=" + ProximityRadiusNearby);
        googleURL.append("&type=" + nearbyPlace);
        //googleURL.append("&type=" + "cafe");
        googleURL.append("&fields= rating");
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + API_KEY);


        Log.d("GoogleMapsActivity", "url = " + googleURL.toString());

        return googleURL.toString();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {



            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);
            mMap.setOnInfoWindowClickListener(this);
        }

    }

    public boolean checkUserLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        }
        else
        {
            return true;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if (googleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected  synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        googleApiClient.connect();
    }


    @Override
    public void onLocationChanged(Location location) {

        latitide = location.getLatitude();
        longitude = location.getLongitude();
        lastLocation = location;
        if (currentUserLocationMarker != null)
        {
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("user Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        currentUserLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
       // mMap.animateCamera(CameraUpdateFactory.zoomBy(12));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

        if (googleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }



    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

            GeoPoint geoLocation = (GeoPoint) geoObject;

            double lat = geoLocation.getLatitude();
            double lng = geoLocation.getLongitude();


            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(nameOfPlace + ":" + vicinity+":"+rating);
            //markerOptions.title(nameOfPlace + " : " + );
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
    }

    String restaurantID;
    @Override
                
    public void onInfoWindowClick(final Marker marker) {

       if(! (marker.getTitle().equals("user Current Location"))){
           LatLng location = marker.getPosition();
           String title = marker.getTitle();

           String strings[]= title.split(":");
           String name = strings[0];
           String address = strings[1];

           final Bundle bundle = new Bundle();

           bundle.putString("Name", name);
           bundle.putString("Address",address);
           bundle.putDouble("Latitude",location.latitude);
           bundle.putDouble("Longitude",location.longitude);

           final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Your Trails");
            builder.setMessage("Do you want to add this restaurant to your trail?");
            builder.setPositiveButton("Create New", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    CardViewFragment myfragment = new CardViewFragment();

                    //send data to CardFragment
                    myfragment.setArguments(bundle);

                    getFragmentManager().beginTransaction()
                            .add(R.id.container, myfragment)
                            .commit();

                    System.out.println("yes");

                }
            });

            builder.setNegativeButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    System.out.println("Add to existing trail");

                    //Check if there is an existing trail
                    String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users")
                            .document(userUID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if(document.exists()){
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            ArrayList<DocumentReference> trailList = (ArrayList<DocumentReference>) document.get("trailList");
                                            //Total number of trails
                                            if(trailList!=null){
                                                Intent intent = new Intent(GoogleMapsActivity.this, TrailListActivity.class);
                                                intent.putExtras(bundle);
                                                startActivityForResult(intent, QUERY_PERSONAL_TRAIL);
                                            }else{
                                                // No existing trail to add
                                                Toast.makeText(GoogleMapsActivity.this, "No existing trail, please create one.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    } else {
                                        Log.w(TAG, "Error getting documents.", task.getException());
                                    }
                                }
                            });
                }
            });

            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    System.out.println("Cancel");
                }
            });


            //builder.show();
            final AlertDialog dialog = builder.create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
                }
            });

           // builder.show();
            dialog.show();

        }else {
            System.out.println("this is user location");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QUERY_PERSONAL_TRAIL && resultCode == RESULT_OK) {
            Bundle mBundle = data.getExtras();


            CardViewFragAddRest mCardViewFrag = new CardViewFragAddRest();

            //send data to CardFragment
            mCardViewFrag.setArguments(mBundle);

            getFragmentManager().beginTransaction()
                    .add(R.id.container, mCardViewFrag)
                    .commit();

        }
    }

    public boolean isExist(String collection, String docID){

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
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                ArrayList<DocumentReference> trailList = (ArrayList<DocumentReference>) document.get("trailList");
                                //Total number of trails
                                if(trailList!=null){

                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return false;
    }

}
