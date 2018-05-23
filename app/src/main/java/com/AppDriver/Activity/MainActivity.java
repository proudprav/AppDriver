package com.AppDriver.Activity;


import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.AppDriver.LocationService;
import com.AppDriver.R;
import com.AppDriver.roomdb.Word;
import com.AppDriver.roomdb.WordListAdapter;
import com.AppDriver.roomdb.WordViewModel;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.AppDriver.floatingService.FloatingService;
import com.AppDriver.Network.Mysingleton_Volleyclass;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;

    private WordViewModel mWordViewModel;
    private SupportMapFragment mapFragment;
    private Word mWord;
    private GoogleMap mMap;
    private LatLng mCurrentLocation;
    private JsonObjectRequest jsonObjectRequest;
    private Polyline polyline1;
    private Button navigateBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        navigateBtn = findViewById(R.id.navigate);

        navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
//
//
//                    this.enterPictureInPictureMode();
                    if (Settings.canDrawOverlays(MainActivity.this)) {

                        startService(new Intent(MainActivity.this,
                            FloatingService.class));
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                            String.valueOf(mWord.getLat()) + "," +
                            String.valueOf(mWord.getLng()));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);}else{
                        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        startActivityForResult(myIntent, 9);
                    }
                } else
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(MainActivity.this)) {
                        startService(new Intent(MainActivity.this,
                                FloatingService.class));
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                                String.valueOf(mWord.getLat()) + "," +
                                String.valueOf(mWord.getLng()));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);}else {
                        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        startActivityForResult(myIntent, 9);
                    }
                    } else {
                        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        startService(new Intent(getApplicationContext(),
                                FloatingService.class));
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                                String.valueOf(mWord.getLat()) + "," +
                                String.valueOf(mWord.getLng()));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);                    }
                }

        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LocationService.getInstance(this).getLocation();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final WordListAdapter adapter = new WordListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get a new or existing ViewModel from the ViewModelProvider.
        mWordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mWordViewModel.getAllWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable final List<Word> words) {
                // Update the cached copy of the words in the adapter.
                adapter.setWords(words);
                if(words != null){
                mWord = words.get(0);
                mMap.addMarker(new MarkerOptions().position(new LatLng((mWord.getLat()),
                        (mWord.getLng()
                        ))));}

            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewWordActivity.class);
                startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.
                ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.
                PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    01);
        }

        subscribeToLocationData();

    }

    private void subscribeToLocationData() {


        LocationService locationService = LocationService.getInstance(this);
        if (locationService != null) {
            LocationService.getInstance(this).getLocationLiveData().observe(this, new Observer<LatLng>() {
                @Override
                public void onChanged(@Nullable LatLng latLng) {


                    mCurrentLocation = latLng;

                    if (mCurrentLocation != null) {
                        CameraPosition nnn = CameraPosition.builder().target(new LatLng(mCurrentLocation.latitude,
                                mCurrentLocation.longitude)).zoom(12).build();

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(nnn));

                                   makeNewRoute(mCurrentLocation);        }
                }
            });


        }
    }

    private void makeNewRoute(LatLng mCurrentLocation) {

       String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                mCurrentLocation.latitude + "," + mCurrentLocation.longitude + "&destination=" +
                String.valueOf(mWord.getLat()
                ) + "," +
                String.valueOf(mWord.getLng()
                ) +
                "&key=" + "AIzaSyDVIPYsrNICg5c301aD3pO2jLvw042DttY";


        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        drawPath(response.toString(), mMap);
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("volley", "Error: " + error.getMessage());
            }
        });
        Mysingleton_Volleyclass.getInstance(this).addrequest(jsonObjectRequest);
    }

    private void drawPath(String s, GoogleMap mMap) {

        if (polyline1 != null) {
            polyline1.remove();
        }
        try {
            final JSONObject json = new JSONObject(s);
            JSONArray routeArray = json.getJSONArray("routes");
            if(routeArray.length() != 0 ) {
                JSONObject routes = routeArray.getJSONObject(0);
                JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");
                List<LatLng> list = decodePoly(encodedString);
                PolylineOptions polyline;

                if (this != null) {
                    polyline = new PolylineOptions()
                            .addAll(list)
                            .width(10).zIndex(10000000)
                            .color(this.getResources().getColor(android.R.color.holo_blue_light)).visible(true);
                    polyline1 = mMap.addPolyline(polyline);
                    polyline1.setEndCap(new RoundCap());
                    polyline1.setStartCap(new RoundCap());
                    polyline1.setJointType(JointType.ROUND);

                }

            }
        } catch (JSONException e) {
            Log.e("at error", e.getMessage(), e);


        }
    }

    public void resetMap() {

        mMap.clear();
    }




    private List<LatLng> decodePoly(String encoded) {


        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Word word = new Word(data.getStringExtra(NewWordActivity.EXTRA_REPLY), 9.467323, 76.356669);
            mWordViewModel.insert(word);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }

        if (requestCode == 9) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    startService(new Intent(this, FloatingService.class));
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + String.valueOf(mWord.getLat())
                            + "," + String.valueOf(mWord.getLng()));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            } else {
                startService(new Intent(this, FloatingService.class));
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + String.valueOf(mWord.getLat())
                        + "," + String.valueOf(mWord.getLng()));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        mMap.getUiSettings().setRotateGesturesEnabled(true);
//        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.setBuildingsEnabled(false);
//        mMap.getUiSettings().setMapToolbarEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService(new Intent(getApplicationContext(),
                FloatingService.class));
    }


}
