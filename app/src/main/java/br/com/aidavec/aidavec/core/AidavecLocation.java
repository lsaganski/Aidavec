package br.com.aidavec.aidavec.core;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import br.com.aidavec.aidavec.base.BaseActivity;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.services.AidavecLocationService;
import br.com.aidavec.aidavec.services.LocationReceiver;
import br.com.aidavec.aidavec.views.MainActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class AidavecLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static AidavecLocation instance;

    public static final String TAG = "AidavecLocation";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Context _context;

    public LocationReceiver resultReceiver;

    public static AidavecLocation getInstance() {
        if (instance == null)
            instance = new AidavecLocation();

        return instance;
    }

    public AidavecLocation() {
        try {
            _context = Globals.getInstance().context;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                checkForPermission();

            mGoogleApiClient = new GoogleApiClient.Builder(_context)
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        } catch (Exception e) {
            Utils.getInstance().saveLog("AidavecLocation", e.getMessage());
        }
    }

    public void startLocation() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void stopLocation() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void stopService() {
        try {
            if (mGoogleApiClient.isConnected()) {
                Intent intent = new Intent(_context, AidavecLocationService.class);
                PendingIntent pendingIntent = PendingIntent.getService(_context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, pendingIntent);
                pendingIntent.cancel();
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("AidavecLoaction - stopService", e.getMessage());
        }
    }

    // LISTENER
    @Override
    public void onConnected(Bundle bundle) {
        try {
            if (ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                mLocationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(Parameters.LOCATION_DETECT_INTERVAL)
                        .setFastestInterval(Parameters.LOCATION_DETECT_FASTEST_INTERVAL);

                Intent intent = new Intent(_context, AidavecLocationService.class);
////            intent.putExtra("receiver", resultReceiver);
                PendingIntent pendingIntent = PendingIntent.getService(_context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, pendingIntent);

                Utils.getInstance().saveLog("AidavecLocation", "Vai começar a localizar");
            } else {
                checkForPermission();
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("AidavecLocation - onConnected", e.getMessage());

        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

  /*  public void setupResultReceiver() {
        resultReceiver = new LocationReceiver(new Handler());
        // This is where we specify what happens when data is received from the service
        resultReceiver.setReceiver(new LocationReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == RESULT_OK) {
                    double latitude = resultData.getDouble("resultLatitude");
                    double longitude = resultData.getDouble("resultLongitude");
                    AidavecDB.getInstance().saveWaypoint(latitude, longitude);
                }
            }
        });
    }*/

    @TargetApi(Build.VERSION_CODES.M)
    private void checkForPermission() {
        int okCoarse = ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION);
        int okFine = ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (okCoarse == PackageManager.PERMISSION_GRANTED && okFine == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Granted");
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale((BaseActivity)Globals.getInstance().context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Log.d(TAG, "Contacts Permission Required!!");
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale((BaseActivity)Globals.getInstance().context, Manifest.permission.CAMERA)) {
                Log.d(TAG, "Contacts Permission Required!!");
            }
            ActivityCompat.
                    requestPermissions((BaseActivity)Globals.getInstance().context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
}
