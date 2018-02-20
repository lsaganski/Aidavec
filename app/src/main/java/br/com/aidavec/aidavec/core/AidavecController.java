package br.com.aidavec.aidavec.core;

import android.location.Location;
import android.os.Handler;
import android.os.Message;

import org.joda.time.Period;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.models.Waypoint;

/**
 * Created by leonardo.saganski on 09/01/17.
 */

public class AidavecController {

    private static AidavecController instance;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            callbackLastWaypoint();
        }
    };

    public static AidavecController getInstance() {
        if (instance == null)
            instance = new AidavecController();

        return instance;
    }

    public void getLastWaypoint() {
        try {
            if (Globals.getInstance().lastWaypointCreated == null) {
                List<Waypoint> localWays = Globals.getInstance().db.getWaypoints();

                if (localWays.size() > 0) {
                    Globals.getInstance().lastWaypointCreated = localWays.get(localWays.size() - 1);
                   // if (localWays.get(localWays.size() - 1).getWay_percorrido() > 0)
                        Globals.getInstance().lastWaypointWithRegisteredDistance = localWays.get(localWays.size() - 1);
                    callbackLastWaypoint();
                } else {
                    Api.getInstance().GetLastWaypoint(handler);
                }
            } else {
                callbackLastWaypoint();
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("AidavecController - getLastWaypoint", e.getMessage());
        }
    }

    public void callbackLastWaypoint() {
        AidavecMotion.getInstance().startRecognition();
       // AidavecLocation.getInstance().startLocation();
    }

    public double getDistanciaEntrePontos(Waypoint w) {
        double distancia = 0;

        try {
            // Verifica se no Splash foi capturado o ultimo waypoint, caso nao, provavlmente este é o primeiro, entao retorna 0.
            if (Globals.getInstance().lastWaypointWithRegisteredDistance != null) {

                // Se o intervalo for menor ou igual a 60 segundos, então pertence a mesma viagem e retorna a distancia entre os pontos,
                // Senão, não pertence à mesma viagem e retorna 0.
                if (Globals.getInstance().secondsToLast <= Parameters.MIN_GAP_ALLOWED_BETWEEN_TRIPS ) {
                    distancia = Utils.getInstance().calculaDistancia(w.getWay_latitude(), w.getWay_longitude(),
                            Globals.getInstance().lastWaypointWithRegisteredDistance.getWay_latitude(),
                            Globals.getInstance().lastWaypointWithRegisteredDistance.getWay_longitude());
                }
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("AidavecController - getDistanciaEntrePontos", e.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show(e.getMessage(), false);

        }

        return distancia;
    }
}
