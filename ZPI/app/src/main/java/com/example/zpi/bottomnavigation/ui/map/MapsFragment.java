package com.example.zpi.bottomnavigation.ui.map;

import static com.example.zpi.bottomnavigation.BottomNavigationActivity.TRIP_KEY;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointLocation;
import com.example.zpi.repositories.TripPointDao;
import com.example.zpi.repositories.TripPointLocationDao;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MapsFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener, GoogleMap.OnPolylineClickListener {

    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "MapsFragment";

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Trip trip;
    private GeoApiContext mGeoApiContext = null;
    private ArrayList<PolylineData> mPolylinesData = new ArrayList<>();

    private static final LatLng WARSZAWA = new LatLng(52.13, 21.00);
    private static final LatLng KOSZALIN = new LatLng(54.11, 16.10);
    private static final LatLng WROCLAW = new LatLng(51.06, 17.01);
    private static final LatLng BERLIN = new LatLng(52.31, 13.24);
    private static final LatLng KRAKOW = new LatLng(50.03, 19.56);


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            //mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setMyLocationEnabled(true);
            //getLocation();
            //addPoints();
            tempAddMarkers();
            mMap.setOnInfoWindowClickListener(MapsFragment.this);
            mMap.setOnPolylineClickListener(MapsFragment.this);
        }
    };

    private void tempAddMarkers() {
        mMap.addMarker(new MarkerOptions()
                .position(WARSZAWA)
                .title("Warszawa")
                .snippet("Atrakcja")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );
        mMap.addMarker(new MarkerOptions()
                .position(KOSZALIN)
                .title("Koszalin")
                .snippet("Atrakcja")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );
        mMap.addMarker(new MarkerOptions()
                .position(WROCLAW)
                .title("Wroclaw")
                .snippet("Nocleg")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        );
        mMap.addMarker(new MarkerOptions()
                .position(BERLIN)
                .title("Berlin")
                .snippet("Nocleg")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        );
        moveCamera(WARSZAWA, 0);
    }

    private void addPoints() {
        try {
            TripPointDao tripPointDao = new TripPointDao(BaseConnection.getConnectionSource());
            List<TripPoint> tripPointList = tripPointDao.getTripPointsByTrip(trip);
            TripPointLocationDao tripPointLocationDao = new TripPointLocationDao(BaseConnection.getConnectionSource());
            TripPointLocation tripPointLocation = null;
            LatLng latLng = null;
            for ( TripPoint point : tripPointList) {
                //tripPointLocation = tripPointLocationDao.getLocation(point);
                if (point != null) {
                    latLng = new LatLng(tripPointLocation.getLatitude(), tripPointLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("nocleg lub atrakcja"));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Intent intent = requireActivity().getIntent();
            trip = (Trip) intent.getSerializableExtra(TRIP_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        @SuppressLint("MissingPermission")
        final Task location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Location currentLocation = (Location) task.getResult();
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                Date currentDate = new Date();
                Marker m = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Yours position"));
                m.setTag(new ArrayList<>());
                moveCamera(latLng, DEFAULT_ZOOM);
                Log.d(TAG, "onComplete: found location!, lat: " + currentLocation.getLatitude() + "long: " + currentLocation.getLongitude());

            }else{
                Log.d(TAG, "onComplete: current location is null");
                Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateDirections(Marker marker){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(KRAKOW.latitude, KRAKOW.longitude)
        );
//        directions.origin(
//                new com.google.maps.model.LatLng(
//                        mUserPosition.getGeo_point().getLatitude(),
//                        mUserPosition.getGeo_point().getLongitude()
//                )
//        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
                Log.d(TAG, "onResult: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "onResult: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylineToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );

            }
        });
    }

    private void addPolylineToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                if (mPolylinesData.size() > 0) {
                    for (PolylineData polylineData : mPolylinesData) {
                        polylineData.getPolyline().remove();
                    }
                    mPolylinesData.clear();
                    mPolylinesData = new ArrayList<>();
                }

                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.quantum_grey));
                    polyline.setClickable(true);
                    mPolylinesData.add(new PolylineData(polyline, route.legs[0]));
                }
            }
        });
    }

    @Override
    public void onInfoWindowClick(@NonNull final Marker marker) {
        if(marker.getSnippet().equals("This is you")){
            marker.hideInfoWindow();
        }
        else{
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(marker.getSnippet())
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            calculateDirections(marker);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        for(PolylineData polylineData: mPolylinesData){
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.quantum_googblue));
                polylineData.getPolyline().setZIndex(1);
            }
            else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.quantum_grey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }
}