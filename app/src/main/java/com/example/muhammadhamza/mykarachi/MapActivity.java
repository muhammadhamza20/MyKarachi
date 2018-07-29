package com.example.muhammadhamza.mykarachi;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168) ,  new LatLng(71, 136));
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int ERROR_DIALOG_REQUEST = 9001;


    private AutoCompleteTextView searchText;
    private ImageView gps , info , placePicker;
    private Fragment fragment;

    private boolean LocationPermissionGranted = false;
    private FusedLocationProviderClient LocationProvider;
    private GoogleMap gMap;
    private GeoDataClient geoDataClient;
    private GoogleApiClient googleApiClient;
    private PlaceAutoCompleteAdapter adapter;
    private PlaceInfo placeObj;
    private Marker marker;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "MAP is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG , "onMapReady: Map is ready..!");
        gMap = googleMap;
        Log.d(TAG, "onMapReady: LocationPermissionGranted: " + LocationPermissionGranted);
        if(LocationPermissionGranted){
            Log.d(TAG, "onMapReady: asking for Device location.");
//            getDeviceLocation();

            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if(isServicesOkay()) {
            searchText = (AutoCompleteTextView) findViewById(R.id.input_search);
            gps = (ImageView) findViewById(R.id.GPS);
            info = (ImageView) findViewById(R.id.info);
            placePicker = (ImageView) findViewById(R.id.placePicker);
            fragment = getSupportFragmentManager().findFragmentById(R.id.map);

            Log.d(TAG, "Map onCreate: Chcecking Permissions..!");
            getPermissions();
        }
    }

    private void init(){
        Log.d(TAG , "init: Initializing!");

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        geoDataClient = Places.getGeoDataClient(this, null);

        adapter = new PlaceAutoCompleteAdapter(this, geoDataClient, LAT_LNG_BOUNDS, null);
        searchText.setAdapter(adapter);
        searchText.setOnItemClickListener(autoComplete);

        searchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText.setCursorVisible(true);
            }
        });
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        ||event.getAction() == KeyEvent.KEYCODE_ENTER){
                    //LOGIC FOR SEARCHING
                    geoLocate();
                }
                return false;
            }
        });
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "GPS onClik: Clicked GPS");
                getDeviceLocation();
                searchText.setText("");
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "info onClik: Clicked info");
                try {
                    if(marker.isInfoWindowShown()) {
                        Log.d(TAG, "info onClik: marker window hiding");
                        marker.hideInfoWindow();
                    }
                    else {
                        Log.d(TAG, "info onClik: marker window showing");
                        marker.showInfoWindow();
                    }
                }catch(NullPointerException e){
                    Log.d(TAG, "info Onclick: NullPointerException: " + e.getMessage());
                }
            }
        });

        placePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(MapActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.d(TAG, "placePicker onClick: GooglePlayServicesRepairableException: " + e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.d(TAG, "placePicker onClick: GooglePlayServicesNotAvailableException: " + e.getMessage());
                }
            }
        });
        hideKeyboard();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(googleApiClient, place.getId());
                placeResult.setResultCallback(updatedPlaceDetailsCallBack);
            }
        }
    }

    private void geoLocate(){
        Log.d(TAG , "geoLocate: Locating the entered Location!");
        String searchString = searchText.getText().toString();
        Geocoder geoCoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();

        try{
            list = geoCoder.getFromLocationName(searchString, 1);
        }catch(IOException e){
            Log.d(TAG, "geoLocate: IOEXCEPTION: " + e.getMessage());
        }

        android.location.Address address = list.get(0);
        Log.d(TAG, "Found a location: address: " + address.toString());
        //Toast.makeText(MapActivity.this, "Found a location: address: " + address.toString() , Toast.LENGTH_SHORT).show();

        moveCamera(new LatLng(address.getLatitude(), address.getLongitude()) , DEFAULT_ZOOM, address.getAddressLine(0));
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting Device location.");
        LocationProvider = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        try{
            if(LocationPermissionGranted){
                Task Location = LocationProvider.getLastLocation();
                Location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Location found.");
                            android.location.Location currentLocation = (Location) task.getResult();
                            if(currentLocation!=null)
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()) , DEFAULT_ZOOM , "My Location");
                        }
                        else {
                            Log.d(TAG, "onComplete: CurrentLocation is null");
                            Toast.makeText(MapActivity.this, "Couldn't find your Location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch(SecurityException e){
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latlng, float zoom, PlaceInfo placeInfo){
        Log.d(TAG, "moveCamera: moving camera to lat=" + latlng.latitude + " and lng=" + latlng.longitude);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoom));
        gMap.clear();
        gMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity.this));

        if(placeInfo!=null){
            try{
                String snippet = "Address: "      + placeInfo.getAddress()     + "\n"
                        +        "PhoneNumber: "  + placeInfo.getPhoneNumber() + "\n"
                        +        "Price Rating: " + placeInfo.getRating()      + "\n"
                        +        "Website: "      + placeInfo.getWebsiteUri()  + "\n"
                        +        "Name: "         + placeInfo.getName()        + "\n";
                Log.d(TAG, "moveCamera: snippet Prepared:\n" + snippet);

                MarkerOptions options = new MarkerOptions().position(latlng).title(placeInfo.getName()).snippet(snippet);
                marker = gMap.addMarker(options);
                Log.d(TAG, "moveCamera: marker set");
            }catch(NullPointerException e) {
                Log.d(TAG, "moveCamera: NullPointerException: " + e.getMessage());
            }
        }
        else {
            gMap.addMarker(new MarkerOptions().position(latlng));
            Log.d(TAG, "moveCamera: marker set without snippet");
        }
        hideKeyboard();
    }

    private void moveCamera(LatLng latlng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving camera to lat=" + latlng.latitude + " and lng=" + latlng.longitude);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoom));

        if(!(title == "My Location")){
            Log.d(TAG, "moveCamera: setting marker with title");
            MarkerOptions options = new MarkerOptions().position(latlng).title(title);
            gMap.addMarker(options);
        }
        hideKeyboard();
    }

    private void initMap(){
        Log.d(TAG , "InitMap: Inititalizing Map..!");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    public void getPermissions(){
        Log.d(TAG , "getPermissions: Called!");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION , android.Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permissions Granted
                Log.d(TAG , "getPermissions: Permission Granted!");
                LocationPermissionGranted = true;
                initMap();
            }
            else {
                Log.d(TAG , "getPermissions: inside ELSE Requesting Persmissions!");
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else {
            Log.d(TAG , "getPermissions: outside ELSE Requesting Persmissions!");
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LocationPermissionGranted=false;
        Log.d(TAG , "onRequestPermissionsResult: Getting results..!");
        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if(grantResults.length>0) {
                    for(int i=0; i<grantResults.length; i++) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            Log.d(TAG , "onRequestPermissionsResult: Permission Failed..!");
                            LocationPermissionGranted=false;
                            return;
                        }
                    }
                    Log.d(TAG , "onRequestPermissionsResult: Permission Granted!");
                    LocationPermissionGranted=true;
                    //Initialize Map
                    initMap();
                }
            }
        }
    }

    /*
    ************************************ GOOGLE API PLACES ******************************************8
     */

    private AdapterView.OnItemClickListener autoComplete = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideKeyboard();
            final AutocompletePrediction item = adapter.getItem(position);
            final String placeID = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(googleApiClient, placeID);
            placeResult.setResultCallback(updatedPlaceDetailsCallBack);
        }
    };

    private ResultCallback<PlaceBuffer> updatedPlaceDetailsCallBack = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: place query did not complete. " + places.getStatus().toString());
                places.release();
                return;
            }

            final Place place = places.get(0);

            try {
                placeObj = new PlaceInfo();
                placeObj.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: Place Address: " + placeObj.getAddress().toString());
//                placeObj.setAttributions(place.getAttributions().toString());
//                Log.d(TAG, "onResult: Place Attributions: " + placeObj.getAttributions().toString());
                placeObj.setLatlng(place.getLatLng());
                Log.d(TAG, "onResult: Place LatLng: " + placeObj.getLatlng());
                placeObj.setId(place.getId().toString());
                Log.d(TAG, "onResult: Place Id: " + placeObj.getId().toString());
                placeObj.setName(place.getName().toString());
                Log.d(TAG, "onResult: Place Name: " + placeObj.getName().toString());
                placeObj.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: Place Phone Number: " + placeObj.getPhoneNumber().toString());
                placeObj.setRating(place.getRating());
                Log.d(TAG, "onResult: Place Rating: " + placeObj.getRating());
                placeObj.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: Place Website URI: " + placeObj.getWebsiteUri());

                Log.d(TAG, "onResult: Place Detail: " + placeObj.toString());
            }catch(NullPointerException e){
                Log.d(TAG, "onResult: NullPointerException: " + e.getMessage());
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude ),DEFAULT_ZOOM, placeObj);
            places.release();
        }
    };

    private void hideKeyboard() {
        Log.d(TAG, "HIDEKEYBOARD: ENTERED ");
        //Check if no view has focus
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            searchText.setCursorVisible(false);
        }
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        hideKeyboard();
        return super.dispatchTouchEvent(ev);
    }


    public boolean isServicesOkay(){
        Log.d(TAG, "isServicesOkay: Checking Google Services Version.");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapActivity.this);

        if(available == ConnectionResult.SUCCESS) {
            // Correct version is installed and MAPS request can be made.
            Log.d(TAG, "isServicesOkay: GooglePlay Service is Working.");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //am error occured for different version but it is resolvable.
            Log.d(TAG, "isServicesOkay: An ERROR occured but it can be resolved.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this, "You can't make MAPS request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
