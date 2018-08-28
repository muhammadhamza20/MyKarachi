package com.example.muhammadhamza.mykarachi;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.*;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
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

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;

public class MapFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MapFragment";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private static final int PLACE_PICKER_REQUEST = 1;
    //    private Fragment fragment;
    View view;
    private AutoCompleteTextView searchText;
    private ImageView gps, info, placePicker;
    private boolean LocationPermissionGranted = false;
    private FusedLocationProviderClient LocationProvider;
    private GoogleMap gMap;
    //    private PlaceDetectionClient placeDetectionClient;
    private GeoDataClient geoDataClient;
    private GoogleApiClient googleApiClient;
    private PlaceAutoCompleteAdapter adapter;
    private PlaceInfo placeObj;
    private Marker marker;
    private ResultCallback<PlaceBuffer> updatedPlaceDetailsCallBack = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
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
            } catch (NullPointerException e) {
                Log.d(TAG, "onResult: NullPointerException: " + e.getMessage());
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude), DEFAULT_ZOOM, placeObj);
            places.release();
        }
    };
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        googleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false);
        searchText = (AutoCompleteTextView) view.findViewById(R.id.input_search);
        gps = (ImageView) view.findViewById(R.id.GPS);
        info = (ImageView) view.findViewById(R.id.info);

        Log.d(TAG, "Map onCreate: Chcecking Permissions..!");
        getPermissions();
        return view;
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting Device location.");
        LocationProvider = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            if (LocationPermissionGranted) {
                Task Location = LocationProvider.getLastLocation();
                Location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Location found.");
                            android.location.Location currentLocation = (android.location.Location) task.getResult();
                            if (currentLocation != null)
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                        } else {
                            Log.d(TAG, "onComplete: CurrentLocation is null");
                            Toast.makeText(getActivity(), "Couldn't find your Location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latlng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving camera to lat=" + latlng.latitude + " and lng=" + latlng.longitude);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));

        if (!(title == "My Location")) {
            Log.d(TAG, "moveCamera: setting marker with title");
            MarkerOptions options = new MarkerOptions().position(latlng).title(title);
            gMap.addMarker(options);
        }
        hideKeyboard();
    }

    private void moveCamera(LatLng latlng, float zoom, PlaceInfo placeInfo) {
        Log.d(TAG, "moveCamera: moving camera to lat=" + latlng.latitude + " and lng=" + latlng.longitude);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
        gMap.clear();
        gMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity()));

        if (placeInfo != null) {
            try {
                String snippet = "Name: " + placeInfo.getAddress() + "\n"
                        + "PhoneNumber: " + placeInfo.getPhoneNumber() + "\n"
                        + "Address: " + placeInfo.getName() + "\n"
                        + "Price Rating: " + placeInfo.getRating() + "\n";
                Log.d(TAG, "moveCamera: snippet Prepared:\n" + snippet);

                MarkerOptions options = new MarkerOptions().position(latlng).title(placeInfo.getName()).snippet(snippet);
                marker = gMap.addMarker(options);
                Log.d(TAG, "moveCamera: marker set");
            } catch (NullPointerException e) {
                Log.d(TAG, "moveCamera: NullPointerException: " + e.getMessage());
            }
        } else {
            gMap.addMarker(new MarkerOptions().position(latlng));
            Log.d(TAG, "moveCamera: marker set without snippet");
        }
        hideKeyboard();
    }

    private void initMap() {
        Log.d(TAG, "InitMap: Inititalizing Map..!");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
//                Toast.makeText(getActivity(), "Map is Ready!", Toast.LENGTH_SHORT).show();
                gMap = googleMap;
                if (LocationPermissionGranted) {
                    Log.d(TAG, "onMapReady: asking for Device location.");
                    getDeviceLocation();

                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        return;
                    gMap.setMyLocationEnabled(true);
                    gMap.getUiSettings().setMyLocationButtonEnabled(false);
                    init();
                }
            }
        });
    }

    public void init() {
        Log.d(TAG, "init: Initializing!");

        geoDataClient = Places.getGeoDataClient(getActivity(), null);
        adapter = new PlaceAutoCompleteAdapter(getActivity(), geoDataClient, LAT_LNG_BOUNDS, null);
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
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //LOGIC FOR SEARCHING
                    geoLocate();
                    searchText.dismissDropDown();
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
                    if (marker.isInfoWindowShown()) {
                        Log.d(TAG, "info onClik: marker window hiding");
                        marker.hideInfoWindow();
                    } else {
                        Log.d(TAG, "info onClik: marker window showing");
                        marker.showInfoWindow();
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "info Onclick: NullPointerException: " + e.getMessage());
                }
            }
        });
        hideKeyboard();
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: Locating the entered Location!");
        String searchString = searchText.getText().toString();
        Geocoder geoCoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();

        try {
            list = geoCoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.d(TAG, "geoLocate: IOEXCEPTION: " + e.getMessage());
        }

        if (list.size() > 0) {
            android.location.Address address = list.get(0);
            Log.d(TAG, "Found a location: address: " + address.toString());
            //Toast.makeText(MapActivity.this, "Found a location: address: " + address.toString() , Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }

    }

    public void getPermissions() {
        Log.d(TAG, "getPermissions: Called!");
//        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION , android.Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getActivity(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permissions Granted
                Log.d(TAG, "getPermissions: Permission Granted!");
                LocationPermissionGranted = true;
                initMap();
            } else {
                Log.d(TAG, "getPermissions: inside ELSE Requesting Persmissions!");
                requesttPermissions();
            }
        } else {
            Log.d(TAG, "getPermissions: outside ELSE Requesting Persmissions!");
            requesttPermissions();
        }
    }

    /*
    ************************************ GOOGLE API PLACES ******************************************8
     */

    private void requesttPermissions() {
        final String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
        boolean shouldProvideRationaleFine =
                shouldShowRequestPermissionRationale(FINE_LOCATION);

        if (shouldProvideRationaleFine) {
            Log.d(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    view.findViewById(R.id.map),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LocationPermissionGranted = false;
        Log.d(TAG, "onRequestPermissionsResult: Getting results..!");
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: Permission Failed..!");
                            LocationPermissionGranted = false;
                            Log.d(TAG, "Ended up here in else!!!");
                            Snackbar.make(
                                    view.findViewById(R.id.mapLayout),
                                    R.string.permission_denied_explanation,
                                    Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.settings, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // Build intent that displays the App settings screen.
                                            Intent intent = new Intent();
                                            intent.setAction(
                                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package",
                                                    BuildConfig.APPLICATION_ID, null);
                                            intent.setData(uri);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    })
                                    .show();
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: Permission Granted!");
                    LocationPermissionGranted = true;
                    //Initialize Map
                    initMap();
                }
            }
        }
    }

    private void hideKeyboard() {
        Log.d(TAG, "HIDEKEYBOARD: ENTERED ");
        //Check if no view has focus
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            searchText.setCursorVisible(false);
        }
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}