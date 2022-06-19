package com.example.doorstep.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.doorstep.R
import com.example.doorstep.databinding.ActivityMapsBinding
import com.example.doorstep.utilities.AppNetworkStatus
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MapsActivityFinal : FragmentActivity(), OnMapReadyCallback, LocationListener {
    private var mMap: GoogleMap? = null
    private var binding: ActivityMapsBinding? = null
    var flag = false
    var addressLine: TextInputEditText? = null
    var address: String? = null
    var city: String? = null
    var state: String? = null
    var country: String? = null
    var postalCode: String? = null
    var lattitude: String?=null
    var longitude:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        addressLine = binding!!.edittextAddressLine
        binding!!.buttonSubmit.setOnClickListener {
            if(AppNetworkStatus.getInstance(this).isOnline) {
                onClickSubmit()
            }
            else{
                Snackbar.make(binding!!.layout,getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.retry)){
                        onClickSubmit()
                    }.show()
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    private fun onClickSubmit() {
        if (!binding!!.edittextBuildingName.editableText.equals("")) {
            val firestore = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val data = HashMap<String, Any>()
            val address = HashMap<String, Any?>()
            address["AddressLine"] = this.address
            address["BuildingName"] = binding!!.edittextBuildingName.text.toString()
            address["pinCode"] = postalCode
            address["tag"] = "Home"
            address["isActive"] = true
            address["Landmark"] = binding!!.edittextLandMark.text.toString()
            address["lat"]=lattitude
            address["long"]=longitude
            val list = ArrayList<HashMap<String, Any?>>()
            list.add(address)

            Log.e("MAPS",data.toString());
            firestore.collection("Customers").document(auth.uid!!).get().addOnSuccessListener(
                OnSuccessListener {
                    var list1: ArrayList<HashMap<String, Any?>>?=null
                    if (it["address"] != null) {
                        list1 = it["address"] as ArrayList<HashMap<String,Any?>>
                        for(item in list1){
                            item["isActive"]=false
                        }
                    }
                    list1?.add(address)
                    data["address"] = list
                    Log.e("MAPS ACTIITY FINAL",list1.toString())
                    firestore.collection("Customers").document(auth.uid!!)
                        .set(data, SetOptions.merge()).addOnSuccessListener {
                            startActivity(Intent(baseContext, CustomerHomeActivity::class.java))
                            Log.e("MAPSACTIVITYFINAL","DATA SAVED")
                        }.addOnFailureListener { }

                })

        }
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
        mMap = googleMap


        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
        } else {
            locationUpdate
        }

    }

    @get:SuppressLint("MissingPermission")
    private val locationUpdate: Unit
        private get() {

            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0f
            ) { location: Location ->
                this.onLocationChanged(
                    location
                )
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {

            // Checking whether user granted the permission or not.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                locationUpdate
            } else {
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        if (!flag) {
            try {
                getAddress(location)
                flag = true
                updateUI()
            } catch (e: Exception) {
                Log.e("Maps Activity", e.message!!)
            }
        }
    }

    private fun updateUI() {
        addressLine!!.setText(address)
    }


    override fun onPointerCaptureChanged(hasCapture: Boolean) {
        super.onPointerCaptureChanged(hasCapture)
    }

    @Throws(IOException::class)
    private fun getAddress(location: Location): String? {
        mMap!!.addMarker(
            MarkerOptions().position(LatLng(location.latitude, location.longitude))
                .title("Your Location")
        )
        mMap!!.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ),16.0f
            )
        )
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(this, Locale.getDefault())
        addresses = geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        address =
            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        city = addresses[0].locality
        state = addresses[0].adminArea
        country = addresses[0].countryName
        postalCode = addresses[0].postalCode
        lattitude=location.latitude.toString()
        longitude=location.longitude.toString()

        Log.e("Maps Activity", "$address $city $state $country $postalCode")
        return address
    }

    override fun onBackPressed() {
        startActivity(Intent(this, CustomerHomeActivity::class.java))
    }
}