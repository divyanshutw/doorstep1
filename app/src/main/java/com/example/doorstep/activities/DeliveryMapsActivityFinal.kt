package com.example.doorstep.activities

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.doorstep.MainActivity
import com.example.doorstep.R
import com.example.doorstep.databinding.ActivityDeliveryMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.PolyUtil
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

//import com.example.doorstep.activities.databinding.ActivityDeliveryMapsBinding;
class DeliveryMapsActivityFinal : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var binding: ActivityDeliveryMapsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryMapsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
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
    override fun onMapReady(googleMap: GoogleMap) {

        getAddress()
        mMap = googleMap
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url("https://maps.googleapis.com/maps/api/directions/json?origin=5th Mail, Lane No. 4, Near Maruti Mandir, Dharm Nagar,, Wadgaonsheri,, near Upala Hotel, Somnath Nagar, Wadgaon Sheri, Pune, Maharashtra 411014, India&destination=183, GS Rd, Dispur, Christian Basti, Guwahati, Assam 781005&key=" + "AIzaSyDwnoXcQJ47WErq1rInr3cYS5Z1vEC6FEg")
//            .method("GET", null)
//            .build()
//        lateinit var json: JSONObject
//        AsyncTask.execute {
//            try {
//
//                val response = client.newCall(request).execute()
//                val resStr = response.body().string()
//                json = JSONObject(resStr)
//                val jsonTemp = JSONObject(json.getJSONArray("routes")[0].toString())
//                runOnUiThread(Runnable { changeUI(jsonTemp) })
//
//                Log.e("DeliveryMapsActivity1", json.toString())
//
//            } catch (e: IOException) {
//                Log.e("DeliveryMapsActivity", e.message!!)
//            }
//        }


        // Add a marker in Sydney and move the camera
        val sydney = LatLng(18.8203748, 74.35549589999999)
        mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun changeUI(jsonTemp: JSONObject) {
        lateinit var poly: Polyline
        val polyoptions = PolylineOptions()
        polyoptions.color(resources.getColor(R.color.black))
        polyoptions.width(5f)
        var json: JSONObject = JSONObject(jsonTemp.getJSONArray("legs")[0].toString())
        var json1: JSONArray = json.getJSONArray("steps")
        for (i in 0 until json1.length()) {
            var jsonObject: JSONObject = JSONObject(json1[i].toString())
            polyoptions.addAll(
                PolyUtil.decode(
                    jsonObject.getJSONObject("polyline").getString("points")
                )
            )
        }

        //polyoptions.addAll(PolyUtil.decode())
        poly = mMap!!.addPolyline(polyoptions)

        poly.isClickable = true
    }

//    private fun calculateShortestDistance(jsonTemp: Array<JSONObject>) {
//        var json: JSONObject = JSONObject(jsonTemp[0].getJSONArray("legs")[0].toString())
//
//    }

    private fun getAddress(){
         val firebaseFirestore =FirebaseFirestore.getInstance()
        val auth=FirebaseAuth.getInstance()
        auth.currentUser?.let { firebaseFirestore.collection("Customers").document(it.uid).get().addOnSuccessListener(
            OnSuccessListener {

                var map = java.util.HashMap<String, Objects>()
                map= it.getData() as HashMap<String, Objects>
                Log.e("Main Activity",map.toString())
                var list=java.util.ArrayList<HashMap<String,Objects>>()
                list=map["address"] as ArrayList<HashMap<String, Objects>>
                Log.e("MOFO",list.toString())
                requestJsonFromDirectionAPI(list[0]["AddressLine"].toString() )


            }) }

    }

    private fun requestJsonFromDirectionAPI(origin:String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://maps.googleapis.com/maps/api/directions/json?origin=$origin&destination=183, GS Rd, Dispur, Christian Basti, Guwahati, Assam 781005&key=AIzaSyDwnoXcQJ47WErq1rInr3cYS5Z1vEC6FEg")
            .method("GET", null)
            .build()
        lateinit var json: JSONObject
        AsyncTask.execute {
            try {

                val response = client.newCall(request).execute()
                val resStr = response.body().string()
                json = JSONObject(resStr)
                val jsonTemp = JSONObject(json.getJSONArray("routes")[0].toString())
                runOnUiThread(Runnable { changeUI(jsonTemp) })

                Log.e("DeliveryMapsActivity1", json.toString())

            } catch (e: IOException) {
                Log.e("DeliveryMapsActivity", e.message!!)
            }
        }

    }

}

