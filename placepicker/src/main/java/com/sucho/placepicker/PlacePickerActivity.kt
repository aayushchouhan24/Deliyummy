package com.sucho.placepicker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.*
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class PlacePickerActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

  companion object {
    private const val TAG = "PlacePickerActivity"
  }

  private lateinit var map: GoogleMap
  private var googleApiKey: String? = null
  private lateinit var markerImage: ImageView
  private lateinit var markerShadowImage: ImageView
  private lateinit var placeSelectedFab: Button
  private lateinit var myLocationFab: ImageView
  private lateinit var l1: RelativeLayout
  private lateinit var l2: RelativeLayout
  private lateinit var placeNameTextView: TextView
  private lateinit var placeAddressTextView: TextView
  private lateinit var infoLayout: FrameLayout
  private lateinit var placeCoordinatesTextView: TextView
  internal lateinit var mLastLocation: Location
  internal lateinit var mLocationResult: LocationRequest
  internal lateinit var mLocationCallback: LocationCallback
  internal var mCurrLocationMarker: Marker? = null
  internal var mGoogleApiClient: GoogleApiClient? = null
  internal lateinit var mLocationRequest: LocationRequest

  private var latitude = Constants.DEFAULT_LATITUDE
  private var longitude = Constants.DEFAULT_LONGITUDE
  private var initLatitude = Constants.DEFAULT_LATITUDE
  private var initLongitude = Constants.DEFAULT_LONGITUDE
  private var showLatLong = true
  private var zoom = Constants.DEFAULT_ZOOM
  private var addressRequired: Boolean = true
  private var shortAddress = ""
  private var fullAddress = ""
  private var hideMarkerShadow = false
  private var mapRawResourceStyleRes: Int = -1
  private var addresses: List<Address>? = null
  private var mapType: MapType = MapType.NORMAL
  private var onlyCoordinates: Boolean = false
  private var hideLocationButton: Boolean = false
  private var disableMarkerAnimation: Boolean = false
  var fusedLocationClient: FusedLocationProviderClient?=null
  val PERMISSION_ID = 42
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_place_picker)

    getIntentData()

    fusedLocationClient= LocationServices.getFusedLocationProviderClient(this)

    mLocationCallback=LocationCallback();

    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

    mapFragment.getMapAsync(this)

    bindViews()


    placeCoordinatesTextView.visibility = if (showLatLong) View.VISIBLE else View.GONE

    placeSelectedFab.setOnClickListener {
      if (onlyCoordinates) {
        sendOnlyCoordinates()
      } else {
        if (addresses != null) {
          val addressData = AddressData(latitude, longitude, addresses)
          val returnIntent = Intent()
          returnIntent.putExtra(Constants.ADDRESS_INTENT, addressData)
          setResult(RESULT_OK, returnIntent)
          finish()
        } else {
          if (!addressRequired) {
            sendOnlyCoordinates()
          } else {
            Toast.makeText(this@PlacePickerActivity, R.string.no_address, Toast.LENGTH_LONG)
              .show()
          }
        }
      }
    }

    myLocationFab.setOnClickListener {
      if (checkPermission(
                      Manifest.permission.ACCESS_COARSE_LOCATION,
                      Manifest.permission.ACCESS_FINE_LOCATION)) {
        fusedLocationClient?.lastLocation?.
        addOnSuccessListener(this
        ) { location : Location? ->
          if(location == null) {
            // TODO, handle it
          } else location.apply {
            // Handle location object
            initLatitude=location.latitude
            initLongitude=location.longitude
          }
        }
      }

      if(this::map.isInitialized) {
        map.animateCamera(
          CameraUpdateFactory.newLatLngZoom(
            LatLng(initLatitude, initLongitude),
            zoom
          )
        )
      }
    }
  }

  private fun checkPermission(vararg perm:String) : Boolean {
    val havePermissions = perm.toList().all {
      ContextCompat.checkSelfPermission(this,it) ==
              PackageManager.PERMISSION_GRANTED
    }
    if (!havePermissions) {
      if(perm.toList().any {
                ActivityCompat.
                shouldShowRequestPermissionRationale(this, it)}
      ) {                  ActivityCompat.requestPermissions(this, perm, PERMISSION_ID)
      } else {
        ActivityCompat.requestPermissions(this, perm, PERMISSION_ID)
      }
      return false
    }
    return true
  }
  override fun onRequestPermissionsResult(requestCode: Int,
                                          permissions: Array<String>, grantResults: IntArray) {
    when (requestCode) {
      PERMISSION_ID -> {

      }
    }
  }
  private fun bindViews() {
    markerImage = findViewById(R.id.marker_image_view)
    markerShadowImage = findViewById(R.id.marker_shadow_image_view)
    placeSelectedFab = findViewById(R.id.con_loc)
    myLocationFab = findViewById(R.id.my_location_button)
    placeNameTextView = findViewById(R.id.text_view_place_name)
    l1 = findViewById(R.id.l1)
    l2 = findViewById(R.id.l2)
    placeAddressTextView = findViewById(R.id.text_view_place_address)
    placeCoordinatesTextView = findViewById(R.id.text_view_place_coordinates)
    infoLayout = findViewById(R.id.info_layout)
  }

  private fun sendOnlyCoordinates() {
    val addressData = AddressData(latitude, longitude, null)
    val returnIntent = Intent()
    returnIntent.putExtra(Constants.ADDRESS_INTENT, addressData)
    setResult(RESULT_OK, returnIntent)
    finish()
  }

  private fun getIntentData() {
    latitude = intent.getDoubleExtra(Constants.INITIAL_LATITUDE_INTENT, Constants.DEFAULT_LATITUDE)
    longitude = intent.getDoubleExtra(Constants.INITIAL_LONGITUDE_INTENT, Constants.DEFAULT_LONGITUDE)
    initLatitude = latitude
    initLongitude = longitude
    showLatLong = true
    addressRequired = intent.getBooleanExtra(Constants.ADDRESS_REQUIRED_INTENT, true)
    hideMarkerShadow = intent.getBooleanExtra(Constants.HIDE_MARKER_SHADOW_INTENT, false)
    zoom = intent.getFloatExtra(Constants.INITIAL_ZOOM_INTENT, Constants.DEFAULT_ZOOM)
    mapRawResourceStyleRes = intent.getIntExtra(Constants.MAP_RAW_STYLE_RES_INTENT, -1)
    mapType = intent.getSerializableExtra(Constants.MAP_TYPE_INTENT) as MapType
    onlyCoordinates = intent.getBooleanExtra(Constants.ONLY_COORDINATES_INTENT, false)
    googleApiKey = intent.getStringExtra(Constants.GOOGLE_API_KEY)
    hideLocationButton = intent.getBooleanExtra(Constants.HIDE_LOCATION_BUTTON, false)
    disableMarkerAnimation = intent.getBooleanExtra(Constants.DISABLE_MARKER_ANIMATION, false)
  }
  @Synchronized
  protected fun buildGoogleApiClient() {
    mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
    mGoogleApiClient!!.connect()
  }

  override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap
    if(this::map.isInitialized) {
      map.animateCamera(
              CameraUpdateFactory.newLatLngZoom(
                      LatLng(initLatitude, initLongitude),
                      zoom
              )
      )
    }
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (ContextCompat.checkSelfPermission(this,
                      Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        buildGoogleApiClient()
        map.uiSettings.isMyLocationButtonEnabled=false
        map.isMyLocationEnabled = true
      }
    } else {
      buildGoogleApiClient()
      map.uiSettings.isMyLocationButtonEnabled=false
      map.isMyLocationEnabled = true
    }

    map.setOnCameraMoveStartedListener {
      l2.visibility = View.VISIBLE
      l1.visibility=View.GONE
      if (markerImage.translationY == 0f && !disableMarkerAnimation) {
        markerImage.animate()
            .translationY(-75f)
            .setInterpolator(OvershootInterpolator())
            .setDuration(250)
            .start()
      }
    }

    map.setOnCameraIdleListener {
      if(!disableMarkerAnimation) {
        markerImage.animate()
          .translationY(0f)
          .setInterpolator(OvershootInterpolator())
          .setDuration(250)
          .start()
      }
      showLoadingBottomDetails()
      val latLng = map.cameraPosition.target
      latitude = latLng.latitude
      longitude = latLng.longitude
      AsyncTask.execute {
        getAddressForLocation()
        runOnUiThread { setPlaceDetails(latitude, longitude, shortAddress, fullAddress) }
      }
    }
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))
    if (mapRawResourceStyleRes != -1) {
      map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, mapRawResourceStyleRes))
    }
    map.mapType = when(mapType) {
      MapType.NORMAL -> GoogleMap.MAP_TYPE_NORMAL
      MapType.SATELLITE -> GoogleMap.MAP_TYPE_SATELLITE
      MapType.HYBRID -> GoogleMap.MAP_TYPE_HYBRID
      MapType.TERRAIN -> GoogleMap.MAP_TYPE_TERRAIN
      MapType.NONE -> GoogleMap.MAP_TYPE_NONE
      else -> GoogleMap.MAP_TYPE_NORMAL
    }
  }
  override fun onLocationChanged(location: Location) {

    mLastLocation = location
    if (mCurrLocationMarker != null) {
      mCurrLocationMarker!!.remove()
    }
    //Place current location marker
    val latLng = LatLng(location.latitude, location.longitude)
    val markerOptions = MarkerOptions()
    markerOptions.position(latLng)
    markerOptions.title("Current Position")
    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
    mCurrLocationMarker = map!!.addMarker(markerOptions)

//move map camera
    map!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    map!!.animateCamera(CameraUpdateFactory.zoomTo(11f))

//stop location updates
    if (mGoogleApiClient != null) {
      fusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }
  }

  private fun showLoadingBottomDetails() {
    l2.visibility = View.VISIBLE
    l1.visibility=View.GONE
  }

  private fun setPlaceDetails(
    latitude: Double,
    longitude: Double,
    shortAddress: String,
    fullAddress: String
  ) {
    if (latitude == -1.0 || longitude == -1.0) {
      l2.visibility = View.VISIBLE
      l1.visibility=View.GONE
      return
    }
    l2.visibility = View.GONE
    l1.visibility=View.VISIBLE
    placeNameTextView.text = if (shortAddress.isEmpty()) "Unknown Place" else shortAddress
    placeAddressTextView.text = fullAddress
    placeCoordinatesTextView.text = Location.convert(latitude, Location.FORMAT_DEGREES) + ", " + Location.convert(longitude, Location.FORMAT_DEGREES)
  }

  private fun getAddressForLocation() {
    setAddress(latitude, longitude)
  }

  private fun setAddress(
    latitude: Double,
    longitude: Double
  ) {
    val geoCoder = Geocoder(this, Locale.getDefault())
    try {
      val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
      this.addresses = addresses
      return if (addresses != null && addresses.size != 0) {
        fullAddress = addresses[0].getAddressLine(
            0
        ) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        shortAddress = addresses[0].locality
      } else {
        shortAddress = ""
        fullAddress = ""
      }
    } catch (e: Exception) {
      //Time Out in getting address
      e.message?.let { Log.e(TAG, it) }
      shortAddress = ""
      fullAddress = ""
      addresses = null
    }
  }
  override fun onConnected(bundle: Bundle?) {
    mLocationRequest = LocationRequest()
    mLocationRequest.interval = 1000
    mLocationRequest.fastestInterval = 1000
    mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
      fusedLocationClient?.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper())
    }
  }

  override fun onConnectionFailed(connectionResult: ConnectionResult) {
    Toast.makeText(applicationContext,"connection failed", Toast.LENGTH_SHORT).show()
  }

  override fun onConnectionSuspended(p0: Int) {
    Toast.makeText(applicationContext,"connection suspended", Toast.LENGTH_SHORT).show()
  }
}
