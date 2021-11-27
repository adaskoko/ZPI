package com.example.zpi.bottomnavigation.ui.map;

import static com.example.zpi.bottomnavigation.BottomNavigationActivity.TRIP_KEY;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripParticipant;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointLocation;
import com.example.zpi.models.User;
import com.example.zpi.models.UserLocation;
import com.example.zpi.repositories.TripParticipantDao;
import com.example.zpi.repositories.TripPointDao;
import com.example.zpi.repositories.TripPointLocationDao;
import com.example.zpi.repositories.UserDao;
import com.example.zpi.repositories.UserLocationDao;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener, GoogleMap.OnPolylineClickListener, DatePickerDialog.OnDateSetListener {

    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "MapsFragment";

    //vars
    private GoogleMap mMap;
    private Trip trip;
    private GeoApiContext mGeoApiContext = null;
    private ArrayList<PolylineData> mPolylinesData = new ArrayList<>();
    private List<TripPoint> tripPointList = new ArrayList<>();
    private List<TripPointLocation> tripPointLocationList = new ArrayList<>();
    private DirectionsRoute currentRoute = null;
    private ArrayList<Marker> POIList = new ArrayList<>();
    private ArrayList<Marker> tripMarkerList = new ArrayList<>();
    private ArrayList<Marker> userMarkerList = new ArrayList<>();
    private Date chosenDate = null;
    private ArrayList<User> tripUsers = new ArrayList<>();
    private ArrayList<UserLocation> tripUsersLocation = new ArrayList<>();
    private boolean [] selectedUsers;
    private String [] sUsers;

    //tutorial
    private CameraPosition cameraPosition;

    // The entry point to the Places API.
    private PlacesClient placesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 50;
    private String[] likelyPlaceNames;
    private String[] likelyPlaceAddresses;
    private List[] likelyPlaceAttributions;
    private LatLng[] likelyPlaceLatLngs;


    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @SuppressLint({"MissingPermission", "PotentialBehaviorOverride"})
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mMap = googleMap;
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            //mMap.setMyLocationEnabled(true);
            //getLocation();
            //addPoints();
            mMap.setOnInfoWindowClickListener(MapsFragment.this);
            mMap.setOnPolylineClickListener(MapsFragment.this);

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                // Return null here, so that getInfoContents() is called next.
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    // Inflate the layouts for the info window, title and snippet.
                    View infoWindow = LayoutInflater.from(getContext()).inflate(R.layout.custom_info_window, null);

                    TextView title = infoWindow.findViewById(R.id.title);
                    title.setText(marker.getTitle());

                    TextView snippet = infoWindow.findViewById(R.id.snippet);
                    snippet.setText(marker.getSnippet());

                    return infoWindow;
                }
            });
            getLocationPermission();
            addDirections();

            //POI
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
            getUsersLocation();
        }
    };

    private void getUsersLocation() {
        new Thread(() -> {
            if (tripUsers.size() > 0) {
                tripUsers.clear();
                tripUsers = new ArrayList<>();
            }
            if (tripUsersLocation.size() > 0) {
                tripUsersLocation.clear();
                tripUsersLocation = new ArrayList<>();
            }
            try {
                TripParticipantDao tripParticipantDao = new TripParticipantDao(BaseConnection.getConnectionSource());
                UserLocationDao userLocationDao = new UserLocationDao(BaseConnection.getConnectionSource());
                List<TripParticipant> tripParticipants = tripParticipantDao.getByTrip(trip);
                User user;
                UserLocation userLocation;
                for (TripParticipant participant : tripParticipants) {
                    user = participant.getUser();
                    userLocation = userLocationDao.getUserLocationByUser(user);
                    if (userLocation != null) {
                        tripUsers.add(user);
                        tripUsersLocation.add(userLocation);
                    }
                }
                Log.d(TAG, "trip participants " + tripParticipants.size());
                Log.d(TAG, "trip participants with location " + tripUsersLocation.size());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            selectedUsers = new boolean[tripUsers.size()];
            sUsers = new String[tripUsers.size()];
            for (int i=0; i<tripUsers.size(); i++) {
                selectedUsers[i] = false;
                sUsers[i] = tripUsers.get(i).getName();
            }
        }).start();
    }

    public MapsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = requireActivity().getIntent();
        trip = (Trip) intent.getSerializableExtra(TRIP_KEY);
        Log.i(getClass().getSimpleName(), "trip = null: "+String.valueOf(trip == null));

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        // Construct a PlacesClient
        Places.initialize(requireContext(), getString(R.string.google_maps_api_key));
        placesClient = Places.createClient(requireContext());

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
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
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
        view.findViewById(R.id.info_button).setOnClickListener(v -> showCurrentPlace());
        view.findViewById(R.id.date_picker_button).setOnClickListener(v -> showDatePickerDialog());
        view.findViewById(R.id.clear_button).setOnClickListener(v -> removePOIMarkers());
        view.findViewById(R.id.person_picker).setOnClickListener(v -> showUserPickerDialog());
    }

    private void showUserPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        builder.setTitle("Wybierz uczestników");
        builder.setCancelable(false);

        builder.setMultiChoiceItems(sUsers, selectedUsers, (dialog, which, isChecked) -> {
            selectedUsers[which] = isChecked;
        }).setPositiveButton("OK", ((dialog, which) -> {
            showUsersLocation();
        })
        ).setNegativeButton("Zamknij", ((dialog, which) -> {
            dialog.dismiss();
        })
        ).setNeutralButton("Wyczyść", ((dialog, which) -> {
            Arrays.fill(selectedUsers, false);
        }));
        builder.show();
    }

    private void showUsersLocation() {
        removeUserMarkers();
        Marker marker;
        for (int i = 0; i < selectedUsers.length; i++) {
            if (selectedUsers[i]) {
                marker =  mMap.addMarker(new MarkerOptions()
                        .title(sUsers[i])
                        .snippet(tripUsers.get(i).getEmail())
                        .position(new LatLng(tripUsersLocation.get(i).getLatitude(), tripUsersLocation.get(i).getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                );
                userMarkerList.add(marker);
            }
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                final Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        Request requestHttp = new Request.Builder()
//                .url("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Museum%20of%20Contemporary%20Art%20Australia&inputtype=textquery&fields=formatted_address%2Cname%2Crating%2Copening_hours%2Cgeometry&key="+getString(R.string.google_maps_key))
//                .method("GET", null)
//                .build();
//        new Thread(() -> {
//            try {
//                Response response = client.newCall(requestHttp).execute();
//                //JSONObject myObject = new JSONObject(response);
//                Log.d(TAG, "http " + response.toString());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();


        if (locationPermissionGranted) {
            // Use fields to define the data types to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                    Place.Field.LAT_LNG, Place.Field.TYPES);

            // Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.newInstance(placeFields);

            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final
            Task<FindCurrentPlaceResponse> placeResult =
                    placesClient.findCurrentPlace(request);
            placeResult.addOnCompleteListener (task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    FindCurrentPlaceResponse likelyPlaces = task.getResult();

                    // Set the count, handling cases where less than 5 entries are returned.
                    Log.i(TAG, "Places size " + likelyPlaces.getPlaceLikelihoods().size());

                    int count;
                    if (likelyPlaces.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                        count = likelyPlaces.getPlaceLikelihoods().size();
                    } else {
                        count = M_MAX_ENTRIES;
                    }

                    int i = 0;
                    likelyPlaceNames = new String[count];
                    likelyPlaceAddresses = new String[count];
                    likelyPlaceAttributions = new List[count];
                    likelyPlaceLatLngs = new LatLng[count];

                    for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                        // Build a list of likely places to show the user.
                        likelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                        likelyPlaceAddresses[i] = placeLikelihood.getPlace().getAddress();
                        likelyPlaceAttributions[i] = placeLikelihood.getPlace().getAttributions();
                        likelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();
                        //Log.i(TAG, "Places attributions " + placeLikelihood.getPlace().getAttributions());
                        Log.i(TAG, "Places types " + placeLikelihood.getPlace().getTypes());


                        i++;
                        if (i > (count - 1)) {
                            break;
                        }
                    }

                    // Show a dialog offering the user the list of likely places, and add a
                    // marker at the selected place.
                    MapsFragment.this.openPlacesDialog();
                }
                else {
                    Log.e(TAG, "Exception: %s", task.getException());
                }
            });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title("Default info title")
                    .position(defaultLocation)
                    .snippet("Default_info_snippet"));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            // The "which" argument contains the position of the selected item.
            LatLng markerLatLng = likelyPlaceLatLngs[which];
            String markerSnippet = likelyPlaceAddresses[which];
            if (likelyPlaceAttributions[which] != null) {
                markerSnippet = markerSnippet + "\n" + likelyPlaceAttributions[which];
            }

            // Add a marker for the selected place, with an info window
            // showing information about that place.
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .title(likelyPlaceNames[which])
                    .position(markerLatLng)
                    .snippet(markerSnippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            );

            // Position the map's camera at the location of the marker.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                    DEFAULT_ZOOM));

            POIList.add(marker);
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Wybierz miejsce")
                .setItems(likelyPlaceNames, listener)
                .show();
    }

    private void removeUserMarkers() {
        if (userMarkerList.size() > 0) {
            for (Marker marker : userMarkerList)
                marker.remove();
        }
        userMarkerList.clear();
        userMarkerList = new ArrayList<>();
    }

    private void removePOIMarkers() {
        if (POIList.size() > 0) {
            for (Marker marker : POIList) {
                marker.remove();
            }
            POIList.clear();
            POIList = new ArrayList<>();
        }
    }

    private void removeTripMarkers() {
        if (tripMarkerList.size() > 0) {
            for (Marker marker : tripMarkerList) {
                marker.remove();
            }
            tripMarkerList.clear();
            tripMarkerList = new ArrayList<>();
        }
    }

    private void removePolylines() {
        if (mPolylinesData.size() > 0) {
            for (PolylineData polylineData : mPolylinesData) {
                polylineData.getPolyline().remove();
            }
            mPolylinesData.clear();
            mPolylinesData = new ArrayList<>();
        }
    }

    private void clearMap() {
        removePOIMarkers();
        removeTripMarkers();
        removePolylines();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(trip.getEndDate().getTime());
        Log.d(TAG, "date " + trip.getEndDate().getTime());
        datePickerDialog.getDatePicker().setMinDate(trip.getStartDate().getTime());
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month + 1;
        String date = year + "-" + month +"-"+ dayOfMonth;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            chosenDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "chosen date " + chosenDate.toString());
       addDirections();
    }

    private void calculateDirections(List<TripPointLocation> tripPointLocationList){
        Log.d(TAG, "tripPointLocation List: " + tripPointLocationList.size());
        Log.d(TAG, "tripPointList: " + tripPointList.size());
        if (tripPointLocationList.size() < 2) {
            return;
        }
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

        //directions.alternatives(true);
        ArrayList<com.google.maps.model.LatLng> waypoints = new ArrayList<>();
        directions.optimizeWaypoints(true);
        for (int i = 2; i < tripPointLocationList.size(); i++) {
            waypoints.add(new com.google.maps.model.LatLng(
                    tripPointLocationList.get(i).getLatitude(),
                    tripPointLocationList.get(i).getLongitude()
            ));
//            directions.waypoints(new com.google.maps.model.LatLng(
//                    tripPointLocationList.get(i).getLatitude(),
//                    tripPointLocationList.get(i).getLongitude()
//            ));

        }
        directions.waypoints(waypoints.toArray(new com.google.maps.model.LatLng[0]));
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
            if (tripPointList.size() > 0) {
                tripPointList.clear();
                tripPointList = new ArrayList<>();
            }
            if (tripPointLocationList.size() > 0) {
                tripPointLocationList.clear();
                tripPointLocationList = new ArrayList<>();
            }
            try {
                Date date = new Date();
                if (date.after(trip.getEndDate()) || date.before(trip.getStartDate())) {
                    chosenDate = trip.getStartDate();
                }
                tripPointList = new TripPointDao(BaseConnection.getConnectionSource()).getTripPointsForToday(trip, chosenDate);
                TripPointLocationDao tripPointLocationDao = new TripPointLocationDao(BaseConnection.getConnectionSource());
                for (TripPoint point : tripPointList) {
                    tripPointLocationList.add(tripPointLocationDao.getLocationForTripPoint(point));
                }
                requireActivity().runOnUiThread(() -> {
                    addMarkers();
                    calculateDirections(tripPointLocationList);
                });
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }

    private void addMarkers() {
        removeTripMarkers();
        TripPointLocation pointLocation;
        TripPoint tripPoint;
        for (int i = 0; i < tripPointList.size(); i++) {
            //for (TripPointLocation tripPoint : tripPointLocationList) {
            pointLocation = tripPointLocationList.get(i);
            tripPoint = tripPointList.get(i);
            Marker m = null;
            if (tripPoint.getTripPointType().getName().equals("Atrakcja")) {
                m = mMap.addMarker(new MarkerOptions()
                        .title(tripPoint.getName())
                        .snippet(pointLocation.getAddress())
                        .position(new LatLng(pointLocation.getLatitude(), pointLocation.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                );
            } else if (tripPoint.getTripPointType().getName().equals("Nocleg")) {
                m = mMap.addMarker(new MarkerOptions()
                        .title(tripPoint.getName())
                        .snippet(pointLocation.getAddress())
                        .position(new LatLng(pointLocation.getLatitude(), pointLocation.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                );
            }
            tripMarkerList.add(m);
        }
    }

    private void addPolylineToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(() -> {
            Log.d(TAG, "alternatives: " + result.routes.length);
            removePolylines();

            //double duration = 999999999;

            Log.d(TAG, "run: leg: " + result.routes[0].legs[0].toString());
            currentRoute = result.routes[0];
            //List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
            List<LatLng> decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());
            Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
            polyline.setColor(ContextCompat.getColor(requireActivity(), R.color.quantum_googblue));
            zoomRoute(polyline.getPoints());
            for (int i = 0; i < result.routes[0].legs.length; i++) {
                mPolylinesData.add(new PolylineData(polyline, result.routes[0].legs[i]));
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
//            if(polyline.getId().equals(polylineData.getPolyline().getId())){
//                polylineData.getPolyline().setColor(ContextCompat.getColor(requireActivity(), R.color.quantum_googblue));
//                polylineData.getPolyline().setZIndex(1);
//            }
//            else{
//                polylineData.getPolyline().setColor(ContextCompat.getColor(requireActivity(), R.color.quantum_grey));
//                polylineData.getPolyline().setZIndex(0);
//            }
        }
    }
}