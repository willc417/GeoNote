package hu.ait.android.geonote;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.github.florent37.awesomebar.ActionItem;
import com.github.florent37.awesomebar.AwesomeBar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import hu.ait.android.geonote.data.Post;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private Location LastLocation;
    private LocationManager locationManager;
    private GoogleMap mMap;
    private Context context;
    private DrawerLayout drawerLayout;
    private AwesomeBar bar;
    private TextView userName;
    private NavigationView navView;
    private View headerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        context = getApplicationContext();
        drawerLayout = findViewById(R.id.drawerLayout);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        setUsername();
        requestNeededPermission();
        mapFragment.getMapAsync(this);


        bar = findViewById(R.id.bar);
        bar.addAction(R.drawable.awsb_ic_edit_animated, "Compose");
        bar.setActionItemClickListener(new AwesomeBar.ActionItemClickListener() {
            @Override
            public void onActionItemClicked(int position, ActionItem actionItem) {
                addMessage();
            }
        });
        bar.setOnMenuClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //menuItem.setChecked(true);
                        switch (menuItem.getItemId()) {
                            case R.id.action_add:
                                addMessage();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                            case R.id.action_view:
                                viewMessages();
                                break;
                            case R.id.action_about:
                                Toast.makeText(context, "Created by Will Clinton", Toast.LENGTH_LONG).show();
                        }
                        return false;
                    }
                });

    }

    private void viewMessages() {
        Intent viewIntent = new Intent(MainActivity.this, ViewActivity.class);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Double LastLat = LastLocation.getLatitude();
        Double LastLon = LastLocation.getLongitude();
        viewIntent.putExtra("Lat", LastLat);
        viewIntent.putExtra("Lon", LastLon);
        startActivityForResult(viewIntent, 1);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void setUsername() {
        navView =  findViewById(R.id.navigationView);
        headerView = navView.getHeaderView(0);

        userName = headerView.findViewById(R.id.userName);
        userName.setText(getString(R.string.username, FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
    }

    private void addMessage() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Double LastLat = LastLocation.getLatitude();
        Double LastLon = LastLocation.getLongitude();
        Intent addIntent = new Intent(MainActivity.this, AddActivity.class);

        addIntent.putExtra("Lat", LastLat);
        addIntent.putExtra("Lon", LastLon);
        startActivityForResult(addIntent, 1);
    }


    private void requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Toast...
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    101);
        } else {
            startLocationMonitoring();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

                // start our job
                startLocationMonitoring();
            } else {
                Toast.makeText(this, "Permission not granted :(", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLocationMonitoring() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void stopLocationMonitoring() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        //LastLocation = new LatLng(location.getLatitude(), location.getLongitude());
        //mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    protected void onStop() {
        super.onStop();
        stopLocationMonitoring();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        AddMapMarkers(mMap);
        mMap.setOnMarkerClickListener(this);


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng latLng = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.getTitle();
                return true;
            }
        });
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void mapReset(){
        mMap.clear();
        AddMapMarkers(mMap);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        String name = marker.getTitle();

        return false;
    }

    public void AddMapMarkers(final GoogleMap googleMap) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post newPost = dataSnapshot.getValue(Post.class);
                LatLng loc = new LatLng(newPost.getLat(),newPost.getLon());


                Marker newMarker = googleMap.addMarker(new MarkerOptions().position(loc).title(newPost.getAuthor()).snippet(newPost.getTitle()));
                if (newPost.getAuthor().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                    newMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mapReset();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
