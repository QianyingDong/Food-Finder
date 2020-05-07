package com.example.foodfinder;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface AsyncResponsePlaces {
    void onDataReceivedSuccess(Set<HashMap<String, Object>> nearbyPlaces);
    void onDataReceivedFailed();

}