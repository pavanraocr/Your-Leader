package com.example.pavan.yourgovernment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by pavan on 4/22/17.
 */

public class Locator {

    private static final String TAG = "Locator";
    private MainActivity owner;
    private LocationManager locationManager;

    public Locator(MainActivity activity) {
        owner = activity;

        if (checkPermission()) {
            setUpLocationManager();
            determineLocation();
        }
    }

    private String doAddress(double latitude, double longitude) {

        Log.d(TAG, "doAddress: Lat: " + latitude + ", Lon: " + longitude);

        List<Address> addresses = null;
        Address address;

        for (int times = 0; times < 3; times++) {
            Geocoder geocoder = new Geocoder(owner, Locale.getDefault());
            try {
                Log.d(TAG, "doAddress: Getting address now");

                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                StringBuilder sb = new StringBuilder();

                if(addresses.size() > 0){

                    address = addresses.get(0);

                    Log.d(TAG, "doLocation: " + address);

                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++){
                        if(sb.length() > 0) {
                            sb.append(", ");
                        }
                        sb.append(address.getAddressLine(i));
                    }

                    if(!address.getCountryName().isEmpty()){
                        if(sb.length() > 0) {
                            sb.append(", ");
                        }
                        sb.append(address.getCountryName());
                    }
                }

                return sb.toString();
            } catch (IOException e) {
                Log.d(TAG, "doAddress: " + e.getMessage());
            }
            Toast.makeText(owner, "GeoCoder service is slow - please wait", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(owner, "GeoCoder service timed out - please try again", Toast.LENGTH_LONG).show();
        return null;
    }

    public void setUpLocationManager() {

        if (locationManager != null)
            return;

        if (!checkPermission())
            return;

        // Get the system's Location Manager
        locationManager = (LocationManager) owner.getSystemService(LOCATION_SERVICE);
    }

    public void shutdown() {
        locationManager = null;
    }

    // This method chooses the best location provider Network ==> Passive ==> GPS
    public void determineLocation() {

        if (!checkPermission())
            return;

        if (locationManager == null)
            setUpLocationManager();

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null) {
                owner.setSearchedLocation(doAddress(loc.getLatitude(), loc.getLongitude()));
                Log.d(TAG, "Using " + LocationManager.NETWORK_PROVIDER + " Location provider");
                return;
            }
        }

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (loc != null) {
                owner.setSearchedLocation(doAddress(loc.getLatitude(), loc.getLongitude()));
                Log.d(TAG, "Using " + LocationManager.PASSIVE_PROVIDER + " Location provider");
                return;
            }
        }

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                owner.setSearchedLocation(doAddress(loc.getLatitude(), loc.getLongitude()));
                Log.d(TAG, "Using " + LocationManager.GPS_PROVIDER + " Location provider");
                return;
            }
        }

        // If you get here, you got no location at all
        owner.noLocationAvailable();
        return;
    }


    // This method asks the user for Locations permissions (if it does not already have them)
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(owner, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(owner,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
            return false;
        }
        return true;
    }
}
