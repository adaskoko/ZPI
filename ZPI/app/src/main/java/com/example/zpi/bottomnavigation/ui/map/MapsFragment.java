package com.example.zpi.bottomnavigation.ui.map;

import static com.example.zpi.bottomnavigation.BottomNavigationActivity.TRIP_KEY;

import android.annotation.SuppressLint;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener, GoogleMap.OnPolylineClickListener {

    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "MapsFragment";

    //vars
    private GoogleMap mMap;
    private Trip trip;
    private GeoApiContext mGeoApiContext = null;
    private ArrayList<PolylineData> mPolylinesData = new ArrayList<>();
    private List<TripPoint> tripPointList = new ArrayList<>();
    private List<TripPointLocation> tripPointLocationList = new ArrayList<>();


    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mMap = googleMap;
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            //mMap.setMyLocationEnabled(true);
            //getLocation();
            //addPoints();
            mMap.setOnInfoWindowClickListener(MapsFragment.this);
            mMap.setOnPolylineClickListener(MapsFragment.this);
            addDirections();
        }
    };

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
        Intent intent = getActivity().getIntent();
        trip = (Trip) intent.getSerializableExtra(TRIP_KEY);
        Log.i(getClass().getSimpleName(), "trip = null: "+String.valueOf(trip == null));
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

//    private void getLocation() {
//        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
//
//        @SuppressLint("MissingPermission")
//        final Task location = mFusedLocationProviderClient.getLastLocation();
//        location.addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                Location currentLocation = (Location) task.getResult();
//                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//                Date currentDate = new Date();
//                Marker m = mMap.addMarker(new MarkerOptions()
//                        .position(latLng)
//                        .title("Yours position"));
//                m.setTag(new ArrayList<>());
//                moveCamera(latLng, DEFAULT_ZOOM);
//                Log.d(TAG, "onComplete: found location!, lat: " + currentLocation.getLatitude() + "long: " + currentLocation.getLongitude());
//
//            }else{
//                Log.d(TAG, "onComplete: current location is null");
//                Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void calculateDirections(Marker marker){
//        Log.d(TAG, "calculateDirections: calculating directions.");
//
//        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
//                marker.getPosition().latitude,
//                marker.getPosition().longitude
//        );
//        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
//
//        directions.alternatives(true);
//        directions.origin(
//                new com.google.maps.model.LatLng(KRAKOW.latitude, KRAKOW.longitude)
//        );
////        directions.origin(
////                new com.google.maps.model.LatLng(
////                        mUserPosition.getGeo_point().getLatitude(),
////                        mUserPosition.getGeo_point().getLongitude()
////                )
////        );
//        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
//        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
//            @Override
//            public void onResult(DirectionsResult result) {
//                Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
//                Log.d(TAG, "onResult: duration: " + result.routes[0].legs[0].duration);
//                Log.d(TAG, "onResult: distance: " + result.routes[0].legs[0].distance);
//                Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
//                addPolylineToMap(result);
//            }
//
//            @Override
//            public void onFailure(Throwable e) {
//                Log.e(TAG, "onFailure: " + e.getMessage() );
//
//            }
//        });
//    }

    private void calculateDirections(List<TripPointLocation> tripPointLocationList){
        Log.d(TAG, "calculateDirections: " + tripPointLocationList.size());
        TripPointLocation end = tripPointLocationList.get(1);
        TripPointLocation start = tripPointLocationList.get(0);

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.destination(new com.google.maps.model.LatLng(
                end.getLatitude(),
                end.getLongitude()
        ));
        directions.origin(new com.google.maps.model.LatLng(
                start.getLatitude(),
                start.getLongitude()
        ));

        directions.alternatives(true);
        directions.optimizeWaypoints(true);
        for (int i = 2; i < tripPointLocationList.size(); i++) {
            directions.waypoints(new com.google.maps.model.LatLng(
                    tripPointLocationList.get(i).getLatitude(),
                    tripPointLocationList.get(i).getLongitude()
            ));
        }

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                end.getLatitude(),
                end.getLongitude()
        );
        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(
                start.getLatitude(),
                start.getLongitude()
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        Log.d(TAG, "calculateDirections: origin: " + origin.toString());
        directions.setCallback(new PendingResult.Callback<DirectionsResult>() {
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

    private void addDirections() {
        new Thread(() -> {
            try {
                tripPointList = new TripPointDao(BaseConnection.getConnectionSource()).getTripPointsForToday(trip, null);
                TripPointLocationDao tripPointLocationDao = new TripPointLocationDao(BaseConnection.getConnectionSource());
                for (TripPoint point : tripPointList) {
                    tripPointLocationList.add(tripPointLocationDao.getLocationForTripPoint(point));
                }
                getActivity().runOnUiThread(() -> {
                    addMarkers();
                    calculateDirections(tripPointLocationList);
                });
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }

    private void addMarkers() {
        TripPointLocation pointLocation;
        TripPoint tripPoint;
        for (int i = 0; i < tripPointList.size(); i++) {
        //for (TripPointLocation tripPoint : tripPointLocationList) {
            pointLocation = tripPointLocationList.get(i);
            tripPoint = tripPointList.get(i);
            mMap.addMarker(new MarkerOptions()
                    .title(tripPoint.getName())
                    .snippet(pointLocation.getAddress())
                    .position(new LatLng(pointLocation.getLatitude(), pointLocation.getLongitude()))
            );
        }
    }

    private void addPolylineToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(() -> {
            Log.d(TAG, "run: result routes: " + result.routes.length);
            if (mPolylinesData.size() > 0) {
                for (PolylineData polylineData : mPolylinesData) {
                    polylineData.getPolyline().remove();
                }
                mPolylinesData.clear();
                mPolylinesData = new ArrayList<>();
            }

            double duration = 999999999;
            for(DirectionsRoute route: result.routes){
                Log.d(TAG, "run: leg: " + route.legs[0].toString());
                //List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                List<LatLng> decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());


                Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
                //Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                polyline.setColor(ContextCompat.getColor(requireActivity(), R.color.quantum_grey));
                polyline.setClickable(true);
                mPolylinesData.add(new PolylineData(polyline, route.legs[0]));

                double tempDuration = route.legs[0].duration.inSeconds;
                if(tempDuration < duration){
                    duration = tempDuration;
                    onPolylineClick(polyline);
                    zoomRoute(polyline.getPoints());
                }
            }
        });
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    @Override
    public void onInfoWindowClick(@NonNull final Marker marker) {
        Toast.makeText(requireContext(), "Info window click", Toast.LENGTH_SHORT).show();
//        if(marker.getSnippet().equals("This is you")){
//            marker.hideInfoWindow();
//        }
//        else{
//            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setMessage(marker.getSnippet())
//                    .setCancelable(true)
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                            calculateDirections(marker);
//                            dialog.dismiss();
//                        }
//                    })
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                            dialog.cancel();
//                        }
//                    });
//            final AlertDialog alert = builder.create();
//            alert.show();
//        }
    }

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        for(PolylineData polylineData: mPolylinesData){
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(requireActivity(), R.color.quantum_googblue));
                polylineData.getPolyline().setZIndex(1);
            }
            else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(requireActivity(), R.color.quantum_grey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }
}