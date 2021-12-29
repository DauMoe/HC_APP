package com.example.hc_app;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import android.location.LocationListener;
import android.location.LocationManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.hc_app.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    MyLocationListener mylistener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFrag = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(googleMap -> {
            mMap = googleMap;
            Log.e("MAP_STATE", "READY");
            GetCurrentLocation();
        });
    }
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Log.e("GPS", location.getLatitude() + ", " + location.getLongitude());
            LatLng here = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(here).title("Current Location")).setIcon(BitmapFromVector(getApplicationContext(), R.drawable.ic_my_location));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 18.2f));
        }
    }
    private void GetCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("NO_GPS: ", "GPS IS NOT ALLOWED!");

            //Request permissions
            ActivityCompat.requestPermissions(MapsActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
        } else {
            //Doc: https://stackoverflow.com/questions/2227292/how-to-get-latitude-and-longitude-of-the-mobile-device-in-android (11 Upvotes)
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            Location location;
            while(true) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    break;
                }
            }
            mylistener = new MyLocationListener();
            mylistener.onLocationChanged(location);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mylistener);
//            locationManager.removeUpdates(mylistener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check permissions
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0) {
                    boolean fine_location = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean coarse_location = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (fine_location && coarse_location) {
                        GetCurrentLocation();
                    }
                }
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
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(20.9808164, 105.7940398);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Học viện kỹ thuật mật mã"));
//        mMap.setMinZoomPreference(15.0f);
//        mMap.setMaxZoomPreference(30.0f);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//
//    }
}