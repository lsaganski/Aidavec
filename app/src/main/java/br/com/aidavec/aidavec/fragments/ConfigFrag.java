package br.com.aidavec.aidavec.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.core.AidavecLocation;
import br.com.aidavec.aidavec.core.AidavecMotion;
import br.com.aidavec.aidavec.core.Api;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.models.Logg;
import br.com.aidavec.aidavec.models.Waypoint;
import br.com.aidavec.aidavec.services.AidavecSyncBroadcast;
import br.com.aidavec.aidavec.views.LoginActivity;
import br.com.aidavec.aidavec.views.SplashActivity;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class ConfigFrag extends Fragment {

    Button btnPerfil;
    Button btnVeiculo;
    Button btnSair;
    Button btnSenha;
    Button btnSync;

    Handler handlerSync = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            SendWaypoints();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_config, container, false);

        btnPerfil = (Button) v.findViewById(R.id.btnPerfil);
        btnVeiculo = (Button) v.findViewById(R.id.btnVeiculo);
        btnSair = (Button) v.findViewById(R.id.btnSair);
        btnSenha = (Button) v.findViewById(R.id.btnSenha);
        btnSync = (Button) v.findViewById(R.id.btnSync);

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_contentframe, new ProfileFrag(), "TAG").addToBackStack(null).commit();
            }
        });

        btnSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_contentframe, new PasswordFrag(), "TAG").addToBackStack(null).commit();
            }
        });

        btnVeiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_contentframe, new VehicleFrag(), "TAG").addToBackStack(null).commit();
            }
        });

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendWaypoints();
            }
        });

        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm();

                Globals.getInstance().prefsEditor.remove("loggedUser");
                Globals.getInstance().prefsEditor.remove("loggedVehicle");
                Globals.getInstance().prefsEditor.commit();

                Api.getInstance().SaveLog(new Logg("LOGOUT"));

                AidavecMotion.getInstance().stopRecognition();
                AidavecMotion.getInstance().stopService();
                AidavecLocation.getInstance().stopLocation();
                AidavecLocation.getInstance().stopService();

                Intent intent = new Intent(Globals.getInstance().context, SplashActivity.class);
                Globals.getInstance(true);
                startActivity(intent);
                getActivity().finish();



            }
        });

        return v;
    }

    private void cancelAlarm() {
        Intent intent = new Intent(getActivity().getApplicationContext(), AidavecSyncBroadcast.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(getActivity(), AidavecSyncBroadcast.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    private void SendWaypoints() {
        if (Globals.getInstance().db != null) {
            List<Waypoint> waypoints = Globals.getInstance().db.getWaypointsLimit();

            if (waypoints.size() > 0) {
                Api.getInstance().SaveWaypoints(waypoints, handlerSync);
            } else {
                if (Globals.getInstance().devMode)
                    Utils.Show("Todos os dados foram sincronizados.", true);
            }
        }
    }
}
