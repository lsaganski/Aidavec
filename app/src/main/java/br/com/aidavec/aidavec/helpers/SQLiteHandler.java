package br.com.aidavec.aidavec.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.models.Logg;
import br.com.aidavec.aidavec.models.Waypoint;

/**
 * Created by Leonardo Saganski on 01-Nov-16.
 */
public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "AIDAVEC";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // AID_WAYPOINTS
        db.execSQL("DROP TABLE IF EXISTS AID_WAYPOINTS");

        String CREATE_WAYPOINTS_TABLE = "CREATE TABLE AID_WAYPOINTS ("
                + "WAY_ID INTEGER PRIMARY KEY,"
                + "USR_ID INTEGER,"
                + "WAY_DATE TEXT,"
                + "WAY_LATITUDE TEXT,"
                + "WAY_LONGITUDE TEXT,"
                + "WAY_PERCORRIDO TEXT" + ")";
        db.execSQL(CREATE_WAYPOINTS_TABLE);

        // AID_LOGS
        db.execSQL("DROP TABLE IF EXISTS AID_LOGS");

        String CREATE_LOGS_TABLE = "CREATE TABLE AID_LOGS ("
                + "LOG_ID INTEGER PRIMARY KEY,"
                + "LOG_DATE TEXT,"
                + "LOG_TRACE TEXT" + ")";
        db.execSQL(CREATE_LOGS_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing waypoint in database
     * */
    public void addWaypoint(Waypoint w) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String now = Utils.getInstance().getStringNow();
            ContentValues values = new ContentValues();
            values.put("USR_ID", w.getUsr_id());
            values.put("WAY_DATE", w.getWay_date());
            values.put("WAY_LATITUDE", w.getWay_latitude());
            values.put("WAY_LONGITUDE", w.getWay_longitude());
            values.put("WAY_PERCORRIDO", w.getWay_percorrido());

            if (db.isOpen()) {
                long id = db.insert("AID_WAYPOINTS", null, values);
            }
           // db.close();
        } catch (Exception e) {
            Utils.getInstance().saveLog("SQLLiteHandler - addWaypoint", e.getMessage());
        }
    }

    /**
     * Storing log in database
     * */
    public void addLog(Logg w) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            String now = Utils.getInstance().getStringNow();
            ContentValues values = new ContentValues();
            values.put("LOG_DATE", Utils.getInstance().getStringNow());
            values.put("LOG_TRACE", w.getLog_tipo());

            if (db.isOpen()) {
                Log.i("LOGGG", w.getLog_tipo());

                long id = db.insert("AID_LOGS", null, values);
            }
  //          db.close(); // Closing database connection
        } catch (Exception e) {
//            Utils.getInstance().saveLog("SQLLiteHandler - addLog", e.getMessage());
        }
    }

    /**
     * Getting waypoint data from database
     * */
    public List<Waypoint> getWaypoints() {
        List<Waypoint> waypoints = new ArrayList<Waypoint>();

        try {
            Waypoint waypoint;
            String selectQuery = "SELECT  * FROM AID_WAYPOINTS";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // Move to first row
            if (db.isOpen()) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    waypoint = new Waypoint();
                    waypoint.setUsr_id(Integer.valueOf(cursor.getString(1)));
                    waypoint.setWay_date(cursor.getString(2));
                    waypoint.setWay_latitude(cursor.getDouble(3));
                    waypoint.setWay_longitude(cursor.getDouble(4));
                    waypoint.setWay_percorrido(cursor.getDouble(5));
                    waypoints.add(waypoint);
                    cursor.moveToNext();
                }
                cursor.close();
  //              db.close();
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("SQLLiteHadnler - getWaypoints", e.getMessage());
        }

        return waypoints;
    }

    /**
     * Getting waypoint data from database
     * */
    public List<Waypoint> getWaypointsLimit() {
        List<Waypoint> waypoints = new ArrayList<Waypoint>();

        try {
            Waypoint waypoint;
            String selectQuery = "SELECT  * FROM AID_WAYPOINTS LIMIT 200";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // Move to first row
            if (db.isOpen()) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    waypoint = new Waypoint();
                    waypoint.setUsr_id(Integer.valueOf(cursor.getString(1)));
                    waypoint.setWay_date(cursor.getString(2));
                    waypoint.setWay_latitude(cursor.getDouble(3));
                    waypoint.setWay_longitude(cursor.getDouble(4));
                    waypoint.setWay_percorrido(cursor.getDouble(5));
                    waypoints.add(waypoint);
                    cursor.moveToNext();
                }
                cursor.close();
   //             db.close();
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("SQLLiteHadnler - getWaypointLimit", e.getMessage());
        }

        return waypoints;
    }

    /**
     * Getting waypoint data from database
     * */
    public double getSumDistance() {
        double val = 0;

        try {
            String selectQuery = "SELECT SUM(CAST(WAY_PERCORRIDO as decimal)) FROM AID_WAYPOINTS WHERE USR_ID = " + Globals.getInstance().loggedUser.getUsr_id();

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // Move to first row
            if (db.isOpen()) {
                if (cursor.moveToFirst()) {
                    val = cursor.getDouble(0);
                }

                cursor.close();
  //              db.close();
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("SQLLiteHandler - getSumDistance", e.getMessage());
        }

        return val;
    }

    public void deleteWaypoints(List<Waypoint> waypoints) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            // Delete Rows
            for (Waypoint w : waypoints) {
                db.delete("AID_WAYPOINTS", "WAY_DATE = '" + w.getWay_date() + "'", null);
            }
 //           db.close();

 //           Log.d(TAG, "Deleted waypoints info from sqlite");
        } catch (Exception e) {
            Utils.getInstance().saveLog("SQLLiteHandler - deleteWaypoints", e.getMessage());
        }
    }

}