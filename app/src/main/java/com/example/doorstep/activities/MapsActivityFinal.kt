package com.example.doorstep.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.doorstep.R
import com.example.doorstep.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import java.io.IOException
import java.util.*

class MapsActivityFinal : FragmentActivity(), OnMapReadyCallback, LocationListener {
    private var mMap: GoogleMap? = null
    private var binding: ActivityMapsBinding? = null
    var flag = false
    var addressLine: TextInputEditText? = null
    lateinit var address: String
    lateinit var city:String
    lateinit var state:String
    lateinit var country:String
    lateinit var postalCode:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        addressLine = binding!!.edittextAddressLine

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
                flag = false
                updateUI()
            } catch (e: Exception) {
                Log.e("Maps Activity", e.message!!)
            }
        }
    }

    private fun updateUI() {
        addressLine?.setText(address)
    }




    @Throws(IOException::class)
    private fun getAddress(location: Location): String {

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
        Log.e("Maps Activity", "$address $city $state $country $postalCode")
        mMap!!.addMarker(
            MarkerOptions().position(LatLng(location.latitude, location.longitude))
                .title("Your Location")
        )
        mMap!!.moveCamera(
            CameraUpdateFactory.newLatLng(
                LatLng(
                    location.latitude,
                    location.longitude
                )
            )
        )
        return address
    }


    }