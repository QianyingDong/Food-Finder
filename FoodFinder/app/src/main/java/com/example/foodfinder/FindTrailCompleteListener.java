package com.example.foodfinder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;

public class FindTrailCompleteListener implements OnCompleteListener<DocumentSnapshot> {
    private Context mContext;
    private RadioGroup radioGroup;
    private LinearLayout linearLayout;

    public FindTrailCompleteListener(Context mContext, RadioGroup radioGroup, LinearLayout linearLayout) {
        this.mContext = mContext;
        this.radioGroup = radioGroup;
        this.linearLayout = linearLayout;
    }

    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
            DocumentSnapshot documentSnapshot = task.getResult();
            if(documentSnapshot.exists()){
                Log.d("FindPersonalTrails", documentSnapshot.getId() + " => " + documentSnapshot.getData());
                String trailname = documentSnapshot.get("name").toString();
                ArrayList restaurantList = (ArrayList) documentSnapshot.get("restaurantList");
                int size = restaurantList.size();
                if(size != 3){
                    //trail not full
                    RadioButton trail = new RadioButton(mContext);
                    trail.setText(trailname);
                    trail.setId(View.generateViewId());
                    radioGroup.addView(trail);
                }else{
                    //trail is full
                    TextView fullTrail = new TextView(mContext);
                    fullTrail.setText(trailname);
                    linearLayout.addView(fullTrail);
                }
            }

        }else {
            Log.w("FindPersonalTrails", "Error getting documents.", task.getException());
        }
    }
}
