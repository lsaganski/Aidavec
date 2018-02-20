package br.com.aidavec.aidavec.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.helpers.SQLiteHandler;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.models.Note;
import br.com.aidavec.aidavec.models.User;
import br.com.aidavec.aidavec.models.Vehicle;
import br.com.aidavec.aidavec.models.Waypoint;
import br.com.aidavec.aidavec.views.MainActivity;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class Globals {

    private static Globals instance;

    public User loggedUser;
    public User loggedUserTemp;
    public Vehicle loggedVehicle;
    public Vehicle loggedVehicleTemp;
    public Context context;
    public SQLiteHandler db;
    public Handler handlerHome;
    public int countUnreadNotes;
    public Handler handlerUI;
    public Handler handlerUIHome;
    public String deviceToken;
    public Note lastPush;
    public Waypoint lastWaypointCreated;
    public Waypoint lastWaypointWithRegisteredDistance;
    public double lastSpeed;
    public int secondsToLast;
    public double lastLocationAccuracy;

    public double savedPontos;
    public double savedDistancia;
    public double savedDia;
    public double savedMes;
    public double savedSemana;
    public double savedPontosCampanha;

    public Fragment showThisFrag;

    public static SharedPreferences mPrefs;
    public static SharedPreferences.Editor prefsEditor;
    public static Gson gson;

    public List<String> apiPaths;
    public String apiPath = "";

    public String lastMotion;
    public String lastValidMotion;

    public int CARRO;
    public int BICICLETA;
    public int PE;
    public int CORRENDO;
    public int PARADO;
    public int SHAKING;
    public int CAMINHANDO;
    public  int UNKNOWN;

    public int countStart;
    public int countStop;
    public int countStill;
    public int countToRegisterDistance;
    public boolean LOCALIZANDO = false;
    public boolean firstWaypoint;

    public boolean devMode = false;

    public static Globals getInstance() {
        if (instance == null)
            instance = new Globals();

        return instance;
    }

    public static Globals getInstance(boolean force) {
        instance = new Globals();

        return instance;
    }

    public Globals() {
        apiPaths = new ArrayList<String>();
        apiPaths.add("http://52.67.200.134:3000/api/");   // Amazon
        apiPaths.add("http://www.mobila.kinghost.net/aidavecapi/api/");   // Kinghost
        apiPaths.add("http://localhost:3000/api/");   // Local

        CARRO = 0;
        BICICLETA = 0;
        PE = 0;
        CORRENDO = 0;
        PARADO = 0;
        SHAKING = 0;
        CAMINHANDO = 0;
        UNKNOWN = 0;

        LOCALIZANDO = false;
        countStart = 0;
        countStop = 0;
        countStill = 0;

        countToRegisterDistance = 0;
    }

    public void setupDB() {
        db = new SQLiteHandler(context);
    }

    public void startCountUnreadNotes()  {
        if (mPrefs.contains("countUnreadNotes")) {
            countUnreadNotes = Integer.valueOf(mPrefs.getString("countUnreadNotes", ""));
        } else {
            countUnreadNotes = 0;
        }

        handlerUI.sendEmptyMessage(101);
    }

    public void increaseCountUnreadNotes()  {
        try {
            countUnreadNotes = countUnreadNotes + 1;
            handlerUI.sendEmptyMessage(101);
            Utils.getInstance().playRingtone();
            Utils.getInstance().vibrate();
        } catch (Exception e) {
            Utils.getInstance().saveLog("Globals - increaseCountUnreadNotes", e.getMessage());
        }
    }

    public void clearCountUnreadNotes()  {
        countUnreadNotes = 0;
        handlerUI.sendEmptyMessage(101);
    }

    public void updateDeviceTokenIfNeeded() {
        try {
            if (deviceToken != null && !loggedUser.getUsr_device().equals(deviceToken)) {
                loggedUser.setUsr_device(deviceToken);
                Api.getInstance().SaveUser(null, loggedUser);
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("Globals - updateDeviceTokenIfNeeded", e.getMessage());
        }
    }

    public void saveVehicleInPrefs() {
        try {
            String jsonVei = gson.toJson(loggedVehicle);
            prefsEditor.putString("loggedVehicle", jsonVei);
            prefsEditor.commit();
        } catch (Exception e) {
            Utils.getInstance().saveLog("Globals - saveVehicleInPrefs", e.getMessage());
        }
    }

    public void saveUserInPrefs() {
        try {
            String jsonUser = gson.toJson(loggedUser);
            prefsEditor.putString("loggedUser", jsonUser);
            prefsEditor.commit();
        } catch (Exception e) {
            Utils.getInstance().saveLog("Globals - saveUserInPrefs", e.getMessage());
        }
    }
}