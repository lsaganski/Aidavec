package br.com.aidavec.aidavec.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.gson.Gson;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.base.BaseActivity;
import br.com.aidavec.aidavec.core.AidavecController;
import br.com.aidavec.aidavec.core.Api;
import br.com.aidavec.aidavec.core.Parameters;
import br.com.aidavec.aidavec.fragments.NoteFrag;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.models.Logg;
import br.com.aidavec.aidavec.models.User;
import br.com.aidavec.aidavec.models.Vehicle;

/**
 * Created by Leonardo Saganski on 20/11/16.
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_splash);

            Globals.getInstance().context = getApplicationContext();
            Globals.getInstance().setupDB();

            Globals.getInstance().mPrefs = getSharedPreferences("AIDAVEC", MODE_PRIVATE);
            Globals.getInstance().prefsEditor = Globals.getInstance().mPrefs.edit();
            Globals.getInstance().gson = new Gson();

            Globals.getInstance().savedSemana = 0;
            Globals.getInstance().savedDia = 0;
            Globals.getInstance().savedMes = 0;
            Globals.getInstance().savedDistancia = 0;
            Globals.getInstance().savedPontos = 0;
            Globals.getInstance().savedPontosCampanha = 0;

            Intent intent = getIntent();
            Bundle data = intent.getExtras();

            // Se veio de push...
            if (data != null && data.containsKey("NOT_OPCAOA")) {
                Globals.getInstance().showThisFrag = new NoteFrag();
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                finish();
            } else {
                // Senão, segue o fluxo normal
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String userJson = "";
                        if (Globals.getInstance().mPrefs != null) {
                            Globals.getInstance().apiPath = Globals.getInstance().mPrefs.getString("ApiServerAddress", Globals.getInstance().apiPaths.get(0));
                            userJson = Globals.getInstance().mPrefs.getString("loggedUser", "");
                        } else {
                            Globals.getInstance().apiPath = Globals.getInstance().apiPaths.get(0);
                        }

                        User loggedUser = null;
                        if (Globals.getInstance().gson != null)
                            loggedUser = Globals.getInstance().gson.fromJson(userJson, User.class);

                        if (loggedUser != null) {
                            Globals.getInstance().loggedUser = loggedUser;

                            String veiJson = Globals.getInstance().mPrefs.getString("loggedVehicle", "");
                            Vehicle loggedVei = Globals.getInstance().gson.fromJson(veiJson, Vehicle.class);

                            if (loggedVei != null)
                                Globals.getInstance().loggedVehicle = loggedVei;

                            startActivity(new Intent(getBaseContext(), MainActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(getBaseContext(), LoginActivity.class));
                            finish();
                        }
                    }
                }, Parameters.SPLASH_WAIT);

            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("SplashActivity", e.getMessage().toString());
        }
    }
}
