package br.com.aidavec.aidavec.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import org.joda.time.Period;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.aidavec.aidavec.core.AidavecDB;
import br.com.aidavec.aidavec.core.AidavecLocation;
import br.com.aidavec.aidavec.core.AidavecMotion;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.core.Parameters;
import br.com.aidavec.aidavec.helpers.Utils;

/**
 * Created by leonardo.saganski on 30/11/16.
 */

public class AidavecLocationService extends IntentService {

    private static AidavecLocationService instance;

    public static final String TAG = "AidavecLocationService";
    Context _context;

    public static AidavecLocationService getInstance() {
        if (instance == null)
            instance = new AidavecLocationService();

        return instance;
    }

    public AidavecLocationService() {
        super("AidavecLocationService");

        _context = Globals.getInstance().context;

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Globals.getInstance().LOCALIZANDO = true;


            if (Globals.getInstance() != null && Globals.getInstance().loggedUser != null && Globals.getInstance().LOCALIZANDO) {

                if (LocationResult.hasResult(intent)) {
                    LocationResult result = LocationResult.extractResult(intent);

                    if (result != null) {
                        Location location = result.getLastLocation();

                        Globals.getInstance().lastLocationAccuracy = location.getAccuracy();

                        Globals.getInstance().lastValidMotion = "Accu: " + String.format("%.2f", Globals.getInstance().lastLocationAccuracy);
                        Utils.getInstance().saveLog("AidavecLocationService", "Pegou location. " + Globals.getInstance().lastValidMotion);

                        Globals.getInstance().handlerUIHome.sendEmptyMessage(103);

                        if (location.hasAccuracy() && location.getAccuracy() <= Parameters.MAX_ACCURACY_ALLOWED) {  //  Tem accuracy menor que o permitido ( 30 ) ? Ou seja, a margem de erro é menor que 30 ?
                            if (Globals.getInstance().lastWaypointWithRegisteredDistance != null) {
                                // Pega o intervalo entre a data/hora do ultimo waypoint que tem distancia registrada e do atual
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                                Date a = sdf.parse(Globals.getInstance().lastWaypointWithRegisteredDistance.getWay_date().replace('T', ' ').replace("Z", ""));
                                Date b = sdf.parse(Utils.getInstance().getStringNow().replace('T', ' ').replace("Z", ""));
                                Period period = new Period(a.getTime(), b.getTime());
                                Globals.getInstance().secondsToLast = period.getSeconds();

                                // calculando a velocidade
                                double distance = Utils.getInstance().calculaDistancia(location.getLatitude(), location.getLongitude(), Globals.getInstance().lastWaypointWithRegisteredDistance.getWay_latitude(), Globals.getInstance().lastWaypointWithRegisteredDistance.getWay_longitude());
                                double speed = distance / Globals.getInstance().secondsToLast;
                                speed *= 3.6; // converting m/s to km/h

                                // Se a velocidade está menor que o limite, incrementa o contador de parado, logo nao deve localizar
                                if (speed > Parameters.MIN_SPEED_ALLOWED)
                                    Globals.getInstance().countStill = 0;
                                else
                                    Globals.getInstance().countStill++;

                                Globals.getInstance().lastSpeed = speed;

                            }

                            Globals.getInstance().lastValidMotion = "Accu: " + String.format("%.2f", Globals.getInstance().lastLocationAccuracy) + "Speed: " + String.format("%.2f", Globals.getInstance().lastSpeed) + " Still: " + Globals.getInstance().countStill;
                            Utils.getInstance().saveLog("AidavecLocationService", "Tem Accu válido e speed. " + Globals.getInstance().lastValidMotion);
                            Globals.getInstance().handlerUIHome.sendEmptyMessage(103);

                            // So registrar localizacao se estiver em movimento.. abaixo é verificado se o veiculo esta parado.
                            if (Globals.getInstance().countStill <= Parameters.MIN_COUNT_STILL_ALLOWED_TO_LOCATE &&     // Não foi atingido o maximo permitido de registros com velocidade menor que 10 ?
                                Globals.getInstance().countStop <= Parameters.MIN_COUNT_STOP_ALLOWED_TO_LOCATE)     // Nao foi atingido o maximo de deteccoes como PARADO ?
                                Utils.getInstance().saveLog("AidavecLocationService", "Vai validar o location.");
                                handleNewLocation(location);
                        }
                    }

                }
            }
        } catch (ParseException p) {
            Utils.getInstance().saveLog("AidavecLocationService - onHandleIntent Parse", p.getMessage());
            if (Globals.getInstance().devMode)
                Utils.Show(p.getMessage(), false);
        } catch (Exception e) {
            Utils.getInstance().saveLog("AidavecLocationService - onHandleIntent", e.getMessage());
            AidavecLocation.getInstance().stopService();
            AidavecLocation.getInstance().stopLocation();
            AidavecMotion.getInstance().stopService();
            AidavecMotion.getInstance().stopRecognition();
        }
    }

    public void handleNewLocation(Location location) {
        try {
            if (location != null) {
                AidavecDB.getInstance().saveWaypoint(location.getLatitude(), location.getLongitude());
            }
            // Extract the receiver passed into the service
        } catch (Exception e) {
            Utils.getInstance().saveLog("AidavecLocationService - handleNewLocation", e.getMessage());
            AidavecLocation.getInstance().stopService();
            AidavecLocation.getInstance().stopLocation();
            AidavecMotion.getInstance().stopService();
            AidavecMotion.getInstance().stopRecognition();
        }
    }
}
