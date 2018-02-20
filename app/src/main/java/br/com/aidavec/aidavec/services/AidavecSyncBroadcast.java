package br.com.aidavec.aidavec.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.util.List;

import br.com.aidavec.aidavec.core.Api;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.models.Waypoint;

/**
 * Created by leonardo.saganski on 10/01/17.
 */

public class AidavecSyncBroadcast extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "br.com.aidavec.aidavec.BACKUP_WAYPOINTS";

    Handler handlerSync = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            SendWaypoints();
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Globals.getInstance() != null && Globals.getInstance().loggedUser != null) {

            if (Globals.getInstance().devMode)
                Utils.getInstance().sendNotification("Alarme mandou salvar às : " + Utils.getInstance().getStringNow());

            SendWaypoints();
        }
    }

    private void SendWaypoints() {
        if (Globals.getInstance().db != null) {
            Globals.getInstance().handlerUI.sendEmptyMessage(103);

            List<Waypoint> waypoints = Globals.getInstance().db.getWaypointsLimit();

            if (waypoints.size() > 0) {
                Api.getInstance().SaveWaypoints(waypoints, handlerSync);
            } else {
                if (Globals.getInstance().devMode)
                    Utils.Show("Todos os dados foram sincronizados.", true);
                Globals.getInstance().handlerUI.sendEmptyMessage(104);

            }
        }
    }
}
