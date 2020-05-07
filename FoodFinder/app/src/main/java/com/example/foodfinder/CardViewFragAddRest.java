package com.example.foodfinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.app.Fragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class CardViewFragAddRest extends Fragment {

    static final int REQUEST_TAKE_PHOTO = 1;
    private Context mContext;
    private StorageReference mStorageRef;
    private FirebaseStorage storage;
    private String restNameString, restAddressString, trailName;
    private ImageView mImageView;
    private RatingBar mFoodBar, mServiceBar;
    private TextView mTrailName;
    private Button mSubmitButton, mTakePhotoButton;
    private ProgressBar mUploadProgressBar;
    private Bitmap photoBitmap;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    public static CardViewFragAddRest newInstance() {
        CardViewFragAddRest fragment = new CardViewFragAddRest();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public CardViewFragAddRest() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //get data from GoogleMapsActivity

        Bundle bundle = getArguments();

        if(bundle != null){
            restNameString = bundle.getString("Name");
            restAddressString = bundle.getString("Address");
            trailName = bundle.getString("trailName");
            System.out.println("-------------Initialize trail name--------------");
            System.out.println("trail name is:" + trailName);
            Double latitude = bundle.getDouble("Latitude");
            Double longtitude = bundle.getDouble("Longitude");

        }else{
            System.out.println("No data from GoogleMapsActivity");
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_rest, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        mImageView = view.findViewById(R.id.imageView);
        mTakePhotoButton = view.findViewById(R.id.button3);
        // perform setOnClickListener on take-photo Button
        mTakePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        });
        mFoodBar = view.findViewById(R.id.ratingBar_food);
        mServiceBar = view.findViewById(R.id.ratingBar_service);
        mTrailName = view.findViewById(R.id.trail_trailName);
        System.out.println("-------------Initialize trail name--------------");
        System.out.println("trail name is:" + trailName);
        mTrailName.setText(trailName);
        mSubmitButton = view.findViewById(R.id.button4);
        mSubmitButton.setOnClickListener(mUploadClickHandler);
        mUploadProgressBar = view.findViewById(R.id.uploadBar);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            photoBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(photoBitmap);
        }
    }

    private View.OnClickListener mUploadClickHandler = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(photoBitmap==null){
                builder = new AlertDialog.Builder(mContext);
                alert = builder.setTitle("Ops, empty photo")
                        .setMessage("Please take a photo for this restaurant.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(mContext, "OK clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }).create();
                alert.show();
            }else {
                mStorageRef = storage.getReference();
                final StorageReference mStorageRef = CardViewFragAddRest.this.mStorageRef.child(CardViewFragment.createImageName(restNameString));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mStorageRef.putBytes(data);
                mUploadProgressBar.setVisibility(View.VISIBLE);
                mSubmitButton.setEnabled(false);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(mContext, "Ops, unsuccessful image upload.", Toast.LENGTH_LONG).show();
                        System.out.println("Ops, unsuccessful image upload.");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        Toast.makeText(mContext, "Congratulation, image uploaded successfully!", Toast.LENGTH_LONG).show();
                        System.out.println("Congratulation, image uploaded successfully!");
                    }
                });

                Task<Uri> getDownLoadUriTask = uploadTask.continueWithTask(
                        new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return mStorageRef.getDownloadUrl();
                            }
                        }
                );
                double totalRating = mFoodBar.getRating() + mServiceBar.getRating();
                Double rating = (double) totalRating/2;
                BigDecimal ratingResult = new BigDecimal(rating);
                final Double finalRating = ratingResult.setScale(1, RoundingMode.HALF_UP).doubleValue();
//                final String finalRestAddr = restAddressString
                getDownLoadUriTask.addOnSuccessListener(getActivity(), new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final HashMap<String, Object> restaurant = new HashMap<>();
                        restaurant.put("imageUri", uri.toString());
                        restaurant.put("name", restNameString);
                        restaurant.put("address", restAddressString);
                        restaurant.put("rating", finalRating);
                // Add new restaurant into restaurant array list
                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("trails")
                            .whereEqualTo("name", trailName)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        String trailID = "";
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            trailID = document.getId();
                                            Log.d("FindTrailByName",  trailID + " => " + document.getData());

                                        }
                                        DocumentReference trailRef = db.collection("trails").document(trailID);
                                        trailRef.update("restaurantList", FieldValue.arrayUnion(restaurant));
                                    } else {
                                        Log.d("FindTrailByName", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                    }
                })
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                mUploadProgressBar.setVisibility(View.GONE);
                                builder = new AlertDialog.Builder(mContext);
                                alert = builder.setTitle("What's Next?")
                                        .setMessage("Would you like to find more restaurant for this trail?")
                                        .setPositiveButton("YES, continue", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                getFragmentManager().beginTransaction().remove(CardViewFragAddRest.this).commit();
                                            }
                                        }).setNegativeButton("NO, see my trail", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                getFragmentManager().beginTransaction().remove(CardViewFragAddRest.this).commit();
                                                Intent intent = new Intent(getActivity(), UserProfile.class);
                                                startActivity(intent);
                                            }
                                        }).create();
                                alert.show();
                            }
                        });

            }

        }
    };
}
