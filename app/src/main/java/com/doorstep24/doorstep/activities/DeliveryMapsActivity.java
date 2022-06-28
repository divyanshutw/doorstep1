package com.doorstep24.doorstep.activities;

import androidx.fragment.app.FragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.doorstep24.doorstep.R;
import com.doorstep24.doorstep.databinding.ActivityDeliveryMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
//import com.doorstep24.doorstep.activities.databinding.ActivityDeliveryMapsBinding;

public class DeliveryMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityDeliveryMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDeliveryMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&key="+"R.string.YOUR_API_KEY")
                .method("GET", null)
                .build();


        try {
            Response response = client.newCall(request).execute();
            Polyline poly;

            JSONObject jsonObject=new JSONObject(response.body().toString());
            Log.e("DeliveryMapsActivity",jsonObject.getJSONObject("routes").toString());
            PolylineOptions polyoptions = new PolylineOptions();
            polyoptions.color(getResources().getColor(R.color.black));
            polyoptions.width(5);
           // polyoptions.addAll(jsonObject.getJSONObject("routes").getJSONObject());
            poly = mMap.addPolyline(polyoptions);
            poly.setClickable(true);
        } catch (IOException | JSONException e) {
            Log.e("DeliveryMapsActivity",e.getMessage());
        }
        HashMap<String, Objects> map=new HashMap<String,Objects>();
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}