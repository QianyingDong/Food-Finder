package com.example.foodfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TrailListActivity extends AppCompatActivity {
    private Intent intent;
    private Bundle bundle;
    private String TAG = "FoodFinder";
    private RadioGroup radioGroup;
    private LinearLayout linearLayout;
    private TextView restNameText, restAddressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail_list);
        radioGroup = findViewById(R.id.radio_container);
        linearLayout = findViewById(R.id.trail_container);
        restNameText = findViewById(R.id.trail_restName);
        restAddressText = findViewById(R.id.trail_restAddress);
        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        readTrail("users", userUID);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radbtn = (RadioButton) findViewById(checkedId);
                Toast.makeText(TrailListActivity.this, "You chooose:" + radbtn.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        intent = this.getIntent();
        bundle = intent.getExtras();
        restNameText.setText(bundle.getString("Name"));
        restAddressText.setText(bundle.getString("Address"));
        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedID = radioGroup.getCheckedRadioButtonId();
                RadioButton trail = (RadioButton) findViewById(checkedID);
                if(trail != null){
                    String trailName = (String) trail.getText();
                    bundle.putString("trailName", trailName);
                    Toast.makeText(TrailListActivity.this, "Add to " + trailName, Toast.LENGTH_SHORT).show();
                    // return result to previous activity
                    intent.putExtras(bundle);
                    TrailListActivity.this.setResult(RESULT_OK, intent);
                    // end this activity
                    TrailListActivity.this.finish();
                }
            }
        });
    }
    // Find personal trails by user ID
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
                                    //Total number of trails
                                    for(DocumentReference trail:trailList){
                                        //Get each trail name
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("trails")
                                                .document(trail.getId())
                                                .get()
                                                .addOnCompleteListener(new FindTrailCompleteListener(TrailListActivity.this, radioGroup, linearLayout));
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
}
