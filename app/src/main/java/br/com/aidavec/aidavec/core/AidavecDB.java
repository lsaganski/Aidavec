package br.com.aidavec.aidavec.core;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.models.Waypoint;
import br.com.aidavec.aidavec.services.AidavecLocationService;
import br.com.aidavec.aidavec.services.AidavecMotionService;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class AidavecDB {

    private static AidavecDB instance;

    public static AidavecDB getInstance() {
        if (instance == null)
            instance = new AidavecDB();

        return instance;
    }

    public void saveWaypoint(double latitude, double longitude) {
        try {
            Waypoint w = new Waypoint();
            w.setUsr_id(Globals.getInstance().loggedUser.getUsr_id());
            w.setWay_date(Utils.getInstance().getStringNow());
            w.setWay_latitude(latitude);
            w.setWay_longitude(longitude);

            double val = 0;

            Globals.getInstance().countToRegisterDistance++;

            if (Globals.getInstance().countToRegisterDistance >= Parameters.LIMIT_TO_REGISTER_DISTANCE) {
                val = AidavecController.getInstance().getDistanciaEntrePontos(w);
            }

            w.setWay_percorrido(val);

            if (Globals.getInstance().firstWaypoint) {
                w.setWay_percorrido(1);
                Globals.getInstance().firstWaypoint = false;
            }

            Globals.getInstance().db.addWaypoint(w);

            if (Globals.getInstance().countToRegisterDistance >= Parameters.LIMIT_TO_REGISTER_DISTANCE) {
                Globals.getInstance().lastWaypointWithRegisteredDistance = w;
                Globals.getInstance().countToRegisterDistance = 0;
            }

            Globals.getInstance().lastWaypointCreated = w;
        } catch (Exception e) {
            Utils.getInstance().saveLog("AidavecDB - saveWaypoint", e.getMessage());
            AidavecLocationService.getInstance().stopService(new Intent( Globals.getInstance().context, AidavecLocationService.class ));
            AidavecLocationService.getInstance().onDestroy();

            AidavecMotionService.getInstance().stopService(new Intent( Globals.getInstance().context, AidavecMotionService.class ));
            AidavecMotionService.getInstance().onDestroy();
        }
    }
}
