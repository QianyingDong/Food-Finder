/*
* Copyright 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.foodfinder;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import static android.app.Activity.RESULT_OK;

/**
 * Fragment that demonstrates how to use CardView.
 */
public class CardViewFragment extends Fragment {

    static final int REQUEST_TAKE_PHOTO = 1;
    private Context mContext;
    private StorageReference mStorageRef;
    private FirebaseStorage storage;
    private String restNameString, restAddressString;
    private ImageView mImageView;
    private SeekBar mFoodBar, mServiceBar;
    private TextView mRestName, mRestAddress, mTrailName;
    private Button mSubmitButton, mTakePhotoButton;
    private ProgressBar mUploadProgressBar;
    private Bitmap photoBitmap;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificationFragment.
     */
    public static CardViewFragment newInstance() {
        CardViewFragment fragment = new CardViewFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public CardViewFragment() {
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
            Double latitude = bundle.getDouble("Latitude");
            Double longtitude = bundle.getDouble("Longitude");

        }else{
            System.out.println("No data from GoogleMapsActivity");
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_view, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRestName = view.findViewById(R.id.mRestName);
        mRestName.setText(restNameString);
        mRestAddress = view.findViewById(R.id.mRestAddress);
        mRestAddress.setText(restAddressString);
        mImageView = view.findViewById(R.id.imageView2);
        mTakePhotoButton = view.findViewById(R.id.button2);
        // perform setOnClickListener on take-photo Button
        mTakePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
                // Code here save photo to local storage of mobile phone
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//                    // Create the File where the photo should go
//                    File imageFile = null;
//                    try {
//                        imageFile = createImageFile();
//                    } catch (IOException ex) {
//                        // Error occurred while creating the File
//                    }
//                    // Continue only if the File was successfully created
//                    if (imageFile != null) {
//
//                        imageUri = FileProvider.getUriForFile(getActivity(),
//                                "com.example.android.fileprovider",
//                                imageFile);
//                        Log.i("GenerateImageUri", "Image Uri is: " + imageUri);
//                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//                    }
//                }
            }
        });
        mFoodBar = (SeekBar) view.findViewById(R.id.cardview_foodbar);
        mServiceBar = (SeekBar) view.findViewById(R.id.cardview_servicebar);
        mTrailName = view.findViewById(R.id.editText);
        mSubmitButton = view.findViewById(R.id.button);
        mSubmitButton.setOnClickListener(mUploadClickHandler);
        mUploadProgressBar = view.findViewById(R.id.upload_progress_bar);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            try {
//                /* if take photo successfully, transform Uri to Bitmap
//                *  using the decodeStream method of BitmapFactory*/
//                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
//                mImageView.setImageBitmap(bitmap); // Display the photo in ImageView
//                Log.i("OnActivityResult", "Photo Display successfully!!!!!!!");
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }

            Bundle extras = data.getExtras();
            photoBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(photoBitmap);
//            Toast.makeText(getActivity(), "Photo taken successfully.", Toast.LENGTH_LONG).show();
        }
    }


    public static String createImageName(String restNameStr) {
        // Create an image name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = restNameStr.trim() + "_" + timeStamp;

        Log.i("CreateImageName", "Generated image name is: " + imageFileName);
        return imageFileName;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "ResPhoto_" + timeStamp + "_";
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        Log.i("CreateImageFile", "Generated image file is: "+image.toString());
        // Save a file: path for use with ACTION_VIEW intents
        //String currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private View.OnClickListener mUploadClickHandler = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            final String trailName = mTrailName.getText().toString().trim();
            if(trailName.isEmpty()){
                builder = new AlertDialog.Builder(mContext);
                alert = builder.setTitle("Ops, empty input")
                    .setMessage("Please enter a name for the new trail.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(mContext, "OK clicked.", Toast.LENGTH_SHORT).show();
                        }
                    }).create();
                alert.show();
            }else if(photoBitmap==null){
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
                final StorageReference mStorageRef = CardViewFragment.this.mStorageRef.child(createImageName(restNameString));
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
                int totalRating = mFoodBar.getProgress() + mServiceBar.getProgress();
                Double rating = (double) totalRating/40;
                BigDecimal ratingResult = new BigDecimal(rating);
                rating = ratingResult.setScale(1, RoundingMode.HALF_UP).doubleValue();
                getDownLoadUriTask.addOnSuccessListener(getActivity(), new UriSuccessListener(trailName,restNameString, restAddressString, rating, mContext))
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
                                            getFragmentManager().beginTransaction().remove(CardViewFragment.this).commit();
                                        }
                                    }).setNegativeButton("NO, see my trail", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            getFragmentManager().beginTransaction().remove(CardViewFragment.this).commit();
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

