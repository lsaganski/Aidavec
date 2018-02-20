package br.com.aidavec.aidavec.helpers;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.core.Parameters;
import br.com.aidavec.aidavec.models.Logg;
import br.com.aidavec.aidavec.views.MainActivity;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class Utils {

    boolean mResult;
    private Vibrator mVibrator;


    private static Utils instance;

    public static Utils getInstance() {
        if (instance == null)
            instance = new Utils();

        return instance;
    }

    public String getStringNow() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String formattedDate = df.format(Calendar.getInstance().getTime());

        return formattedDate;
    }

    public static void Show(String msg, boolean longToast){
        Toast t = Toast.makeText(Globals.getInstance().context, msg, longToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        t.setGravity(Gravity.TOP| Gravity.RIGHT, 0, 0);
        t.show();
    }

    public static boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) Globals.getInstance().context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

    public boolean getYesNoWithExecutionStop(String title, String message,
                                             Context context) {

/*        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };*/

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mResult = true;
               /* handler.sendMessage(handler.obtainMessage());*/
            }
        });

        alert.show();

        try {
            Looper.loop();
        } catch (RuntimeException e2) {
        }

        return mResult;
    }

    public boolean getYesNoConfirmWithExecutionStop(String title, String message, String Sim, String Nao,
                                                    Context context) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        AlertDialog.Builder alert = new AlertDialog.Builder(Globals.getInstance().context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton(Nao, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mResult = false;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.setNegativeButton(Sim, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mResult = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });

        alert.show();

        try {
            Looper.loop();
        } catch (RuntimeException e2) {
        }

        return mResult;
    }

    public static String DefStrVal(String valor, String defaultValue){
        if (valor == null)
            return defaultValue;
        else
            return valor;
    }

    public static String HashMD5(String s) {
        String res = "";

        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            res = new BigInteger(1, m.digest()).toString(16);
        } catch (Exception e) {

        }

        return res;
    }

    public static String CleanStr(String s){
        return s.toUpperCase()
                .replace('Ã','A')
                .replace('Á','A')
                .replace('Â','A')
                .replace('À','A')
                .replace('É','E')
                .replace('Õ','O')
                .replace('Ó','O')
                .replace('Ô','O')
                .replace('Í','I')
                .replace('Ú','U')
                .replace('Ç','C');
    }

    public static String MaskPhone(String v)
    {
        if (v.length() > 0 && v.charAt(0) != '(')
            v = '(' + v;

        if (v.length() > 3 && v.charAt(3) != ')')
            v = v.substring(0, 2) + ')' + v.substring(3, v.length()-1);

        return v;
    }

    public static boolean isValidEmail(String target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String getPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(Globals.getInstance().context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        if (Globals.getInstance() != null && Globals.getInstance().context != null) {
            ActivityManager manager = (ActivityManager) Globals.getInstance().context.getSystemService(Context.ACTIVITY_SERVICE);

            if (manager != null) {
                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                    if (serviceClass.getName().equals(service.service.getClassName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public double calculaDistancia(double lat1, double lng1, double lat2, double lng2) {
        //double earthRadius = 3958.75;//miles
        double earthRadius = 6371;//kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist * 1000; //em metros
    }

    public void sendNotification(String message) {
        if (Globals.getInstance().context != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(Globals.getInstance().context);
            builder.setContentText(message);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(Globals.getInstance().context.getString(R.string.app_name));
            NotificationManagerCompat.from(Globals.getInstance().context).notify(0, builder.build());
        }
    }

    public void saveLog(String where, String trace) {
//        String who = Globals.getInstance().loggedUser != null ? String.valueOf(Globals.getInstance().loggedUser.getUsr_id()) : "-";
//        String info = ">>> Who : " + who +
//                " >>> Where : " + where  +
//                " >>> StackTrace : " + trace;
//        Globals.getInstance().db.addLog(new Logg(info));
    }

    public String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    public void vibrate() {
        mVibrator = (Vibrator) ((MainActivity) Globals.getInstance().context).getSystemService(Context.VIBRATOR_SERVICE);
        // Check whether device hardware has a Vibrator
        if(mVibrator.hasVibrator()){
            mVibrator.vibrate(Parameters.VIBRATION_DURATION); // 2 seconds
        }
    }

    public void playRingtone() {
        ((MainActivity)Globals.getInstance().context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mPlayer = MediaPlayer.create(Globals.getInstance().context, R.raw.bells);

                try {
                    mPlayer.prepare();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                mPlayer.start();

            }
        });
    }

    public void verifyWIFI() {

        WifiManager man = (WifiManager) Globals.getInstance().context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!man.isWifiEnabled()) {
            Utils.Show("Ligue seu WI-FI/GPS para aumentar a precisão da sua localização.", true);
        }

//        ConnectivityManager man2 = ((ConnectivityManager) Globals.getInstance().context.getSystemService(Context.CONNECTIVITY_SERVICE));
//        if (!man2.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable()) {
//        }

    }
}
