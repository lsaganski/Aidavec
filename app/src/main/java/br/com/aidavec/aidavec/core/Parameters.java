package br.com.aidavec.aidavec.core;

/**
 * Created by leonardo.saganski on 24/02/17.
 */

public class Parameters {
    public static int MAX_ACCURACY_ALLOWED = 35;  // meters
    public static int MOTION_DETECT_INTERVAL = 0;   // miliseconds
    public static int LOCATION_DETECT_INTERVAL = 2000;   // miliseconds
    public static int LOCATION_DETECT_FASTEST_INTERVAL = 1000;    // miliseconds
    public static int LIMIT_TO_REGISTER_DISTANCE = 5;
    public static int MIN_SPEED_ALLOWED = 10;    // km/h
    public static int MIN_GAP_ALLOWED_BETWEEN_TRIPS = 180;    // seconds
    public static int MIN_COUNT_STILL_ALLOWED_TO_LOCATE = 1;    // times
    public static int MIN_COUNT_STOP_ALLOWED_TO_LOCATE = 1;    // times
    public static int MIN_DISTANCE_TO_VALID_VEHICLE = 30000;    // meters
    public static int REFRESH_HOME_UI_INTERVAL = 3000;    // miliseconds
    public static int VIBRATION_DURATION = 2000;    // miliseconds
    public static int MIN_CONFIDENCE_ALLOWED_IN_MOTION = 75;    // percentual
    public static int COUNT_INCREMENT_FACTOR_NORMAL = 1;    // unit
    public static int COUNT_INCREMENT_FACTOR_FAST = 40;    // unit
    public static int COUNT_LIMIT_TO_START = 2;    // unit
    public static int COUNT_LIMIT_TO_STOP = 150;    // unit
    public static int SYNC_INTERVAL = 300000;    // miliseconds
    public static int SPLASH_WAIT = 2000;    // miliseconds
}
