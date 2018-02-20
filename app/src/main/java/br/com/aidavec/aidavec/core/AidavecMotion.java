package br.com.aidavec.aidavec.core;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.models.Logg;
import br.com.aidavec.aidavec.services.AidavecMotionService;

/**
 * Created by leonardo.saganski on 30/11/16.
 */

public class AidavecMotion  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static AidavecMotion instance;

    public static final String TAG = "AidavecMotionService";
    private GoogleApiClient mGoogleApiClient;
    Context _context;

    public static AidavecMotion getInstance() {
        if (instance == null)
            instance = new AidavecMotion();

        return instance;
    }

    public AidavecMotion() {

        _context = Globals.getInstance().context;

        mGoogleApiClient = new GoogleApiClient.Builder(_context)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    public void startRecognition() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void stopRecognition() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void stopService() {
        try {
            if (mGoogleApiClient.isConnected()) {
                Intent intent = new Intent(_context, AidavecMotionService.class);
                PendingIntent pendingIntent = PendingIntent.getService(_context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, pendingIntent);
                pendingIntent.cancel();
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("AidavecMotion - stopService", e.getMessage());
        }
    }

    // LISTENER
    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent( _context, AidavecMotionService.class );
        PendingIntent pendingIntent = PendingIntent.getService( _context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mGoogleApiClient, Parameters.MOTION_DETECT_INTERVAL, pendingIntent );

        Utils.getInstance().saveLog("AidavecMotion", "Se conectou no AidavecMotion core");
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}
}
