package com.aghyksa.storyapp.map

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.aghyksa.storyapp.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.aghyksa.storyapp.databinding.ActivityMapsBinding
import com.aghyksa.storyapp.datastore.UserPreference
import com.aghyksa.storyapp.login.LoginActivity
import com.aghyksa.storyapp.utils.GenericViewModelFactory
import com.google.android.gms.maps.model.MarkerOptions

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    
    private val viewModel: MapViewModel by viewModels {
        GenericViewModelFactory.create(
            MapViewModel(UserPreference.getInstance(dataStore))
        )
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupViewModel()
    }
    
    private fun setupViewModel() {
        with(viewModel) {
            getUser().observe(this@MapsActivity) {
                if (!it.isLogin) {
                    startActivity(Intent(this@MapsActivity, LoginActivity::class.java))
                    finish()
                }else{
                    fetchStories(it.token)
                }
            }
            getMessage().observe(this@MapsActivity) {
                Toast.makeText(this@MapsActivity, it, Toast.LENGTH_LONG).show()
                showLoading(false)
            }
            getStories().observe(this@MapsActivity) {
                for(story in it){
                    mMap.addMarker(MarkerOptions().position(LatLng(story.lat,story.lon)).title(story.name))
                }
                showLoading(false)
            }
        }
    }
    
    private fun fetchStories(token:String) {
        viewModel.setStories(token)
        showLoading(true)
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.cvProgress.visibility = View.VISIBLE
        } else {
            binding.cvProgress.visibility = View.GONE
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
        val indonesia = LatLng(-1.8751424373403263, 119.52042779565886)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(indonesia))
    }
}