package com.fcadev.kotlinmaps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.fcadev.kotlinmaps.databinding.ActivityMapsBinding
import com.google.android.material.snackbar.Snackbar

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher : ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registerLauncher()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //casting
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener{
            override fun onLocationChanged(p0: Location) {
                println("Location: " + p0.toString())
            }
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.root, "Permission needed for location", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    //request permission
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            }else{
                //request permission
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }else{
            //permission granted
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        }

        //Hazır kordinat ile konum belirtmek.
        // latitude 48.8583736, longitude 2.2922926

        //val eiffel = LatLng(48.8583736, 2.2922926)
        //mMap.addMarker(MarkerOptions().position(eiffel).title("Eiffel Tower"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eiffel, 15f))
    }

    private fun registerLauncher (){
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    //permission granted
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                }
            }else{
                //permission denied
                Toast.makeText(this, "Permission Needed!", Toast.LENGTH_LONG)
            }
        }
    }

}