package br.com.aidavec.aidavec.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.core.AidavecLocation;
import br.com.aidavec.aidavec.core.AidavecMotion;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.core.Parameters;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.models.Logg;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class AidavecMotionService extends IntentService {

    private static AidavecMotionService instance;

    public static final String TAG = "AidavecMotionService";
    Context _context;

    public static AidavecMotionService getInstance() {
        if (instance == null)
            instance = new AidavecMotionService();

        return instance;
    }

    public AidavecMotionService() {
        super("AidavecMotionService");

        _context = Globals.getInstance().context;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (Globals.getInstance() != null && Globals.getInstance().loggedUser != null) {
                if (ActivityRecognitionResult.hasResult(intent)) {
                    ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                    handleDetectedActivities(result.getProbableActivities());
                }
            }
        } catch (Exception e) {
            AidavecLocation.getInstance().stopService();
            AidavecLocation.getInstance().stopLocation();
            AidavecMotion.getInstance().stopService();
            AidavecMotion.getInstance().stopRecognition();
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        try {

            for (DetectedActivity activity : probableActivities) {
                if (activity.getConfidence() >= Parameters.MIN_CONFIDENCE_ALLOWED_IN_MOTION) {
                    switch (activity.getType()) {
                        case DetectedActivity.IN_VEHICLE: {
                            if (!Globals.getInstance().LOCALIZANDO) {
                                Globals.getInstance().countStart += Parameters.COUNT_INCREMENT_FACTOR_NORMAL;
                            }

                            Globals.getInstance().countStop = 0;
                            Globals.getInstance().countStill = 0;

                            Globals.getInstance().CARRO++;
                            Globals.getInstance().lastMotion = Globals.getInstance().CARRO + " CARRO !!! Conf : " + activity.getConfidence() + " - Sta : " + Globals.getInstance().countStart + " - Sto : " + Globals.getInstance().countStop + (Globals.getInstance().LOCALIZANDO ? " - LOC" : " - ");
                            Utils.getInstance().saveLog("AidavecMotionService", Globals.getInstance().lastMotion);
                            Globals.getInstance().handlerUIHome.sendEmptyMessage(102);

                            break;
                        }
                        case DetectedActivity.ON_BICYCLE: {
                            if (Globals.getInstance().LOCALIZANDO) {
                                Globals.getInstance().countStart = 0;
                                Globals.getInstance().countStop += Parameters.COUNT_INCREMENT_FACTOR_NORMAL;
                            }

                            Globals.getInstance().BICICLETA++;
                            Globals.getInstance().lastMotion = Globals.getInstance().BICICLETA + " BICICLETA !!! Conf : " + activity.getConfidence() + " - Sta : " + Globals.getInstance().countStart + " - Sto : " + Globals.getInstance().countStop + (Globals.getInstance().LOCALIZANDO ? " - LOC" : " - ");
                            Utils.getInstance().saveLog("AidavecMotionService", Globals.getInstance().lastMotion);
                            Globals.getInstance().handlerUIHome.sendEmptyMessage(102);

                            break;
                        }
                        case DetectedActivity.ON_FOOT: {
                            if (Globals.getInstance().LOCALIZANDO) {
                                Globals.getInstance().countStart = 0;
                                Globals.getInstance().countStop += Parameters.COUNT_INCREMENT_FACTOR_FAST;
                            }

                            Globals.getInstance().countStill++;
                            Globals.getInstance().PE++;
                            Globals.getInstance().lastMotion = Globals.getInstance().PE + " A PÉ !!! Conf : " + activity.getConfidence() + " - Sta : " + Globals.getInstance().countStart + " - Sto : " + Globals.getInstance().countStop + (Globals.getInstance().LOCALIZANDO ? " - LOC" : " - ");
                            Utils.getInstance().saveLog("AidavecMotionService", Globals.getInstance().lastMotion);
                            Globals.getInstance().handlerUIHome.sendEmptyMessage(102);

                            break;
                        }
                        case DetectedActivity.RUNNING: {
                            if (Globals.getInstance().LOCALIZANDO) {
                            }

                            Globals.getInstance().CORRENDO++;
                            Globals.getInstance().lastMotion = Globals.getInstance().CORRENDO + " CORRENDO !!! Conf : " + activity.getConfidence() + " - Sta : " + Globals.getInstance().countStart + " - Sto : " + Globals.getInstance().countStop + (Globals.getInstance().LOCALIZANDO ? " - LOC" : " - ");
                            Utils.getInstance().saveLog("AidavecMotionService", Globals.getInstance().lastMotion);
                            Globals.getInstance().handlerUIHome.sendEmptyMessage(102);

                            break;
                        }
                        case DetectedActivity.STILL: {
                            if (Globals.getInstance().LOCALIZANDO) {
                                Globals.getInstance().countStart = 0;
                                Globals.getInstance().countStop += Parameters.COUNT_INCREMENT_FACTOR_NORMAL;
                            }

                            Globals.getInstance().countStill++;
                            Globals.getInstance().PARADO++;
                            Globals.getInstance().lastMotion = Globals.getInstance().PARADO + " PARADO !!! Conf : " + activity.getConfidence() + " - Sta : " + Globals.getInstance().countStart + " - Sto : " + Globals.getInstance().countStop + (Globals.getInstance().LOCALIZANDO ? " - LOC" : " - ");
                            Utils.getInstance().saveLog("AidavecMotionService", Globals.getInstance().lastMotion);
                            Globals.getInstance().handlerUIHome.sendEmptyMessage(102);

                            break;
                        }
                        case DetectedActivity.TILTING: {
                            if (Globals.getInstance().LOCALIZANDO) {
                                Globals.getInstance().countStart = 0;
                                Globals.getInstance().countStop += Parameters.COUNT_INCREMENT_FACTOR_NORMAL;
                            }

                            Globals.getInstance().countStill++;
                            Globals.getInstance().SHAKING++;
                            Globals.getInstance().lastMotion = Globals.getInstance().SHAKING + " SHAKING !!! Conf : " + activity.getConfidence() + " - Sta : " + Globals.getInstance().countStart + " - Sto : " + Globals.getInstance().countStop + (Globals.getInstance().LOCALIZANDO ? " - LOC" : " - ");
                            Utils.getInstance().saveLog("AidavecMotionService", Globals.getInstance().lastMotion);
                            Globals.getInstance().handlerUIHome.sendEmptyMessage(102);
                            break;
                        }
                        case DetectedActivity.WALKING: {
                            if (Globals.getInstance().LOCALIZANDO) {
                            }

                            Globals.getInstance().CAMINHANDO++;
                            Globals.getInstance().lastMotion = Globals.getInstance().CAMINHANDO + " CAMINHANDO !!! Conf : " + activity.getConfidence() + " - Sta : " + Globals.getInstance().countStart + " - Sto : " + Globals.getInstance().countStop + (Globals.getInstance().LOCALIZANDO ? " - LOC" : " - ");
                            Utils.getInstance().saveLog("AidavecMotionService", Globals.getInstance().lastMotion);
                            Globals.getInstance().handlerUIHome.sendEmptyMessage(102);

                            break;
                        }
                        case DetectedActivity.UNKNOWN: {
                            Globals.getInstance().UNKNOWN++;
                            Globals.getInstance().lastMotion = Globals.getInstance().UNKNOWN + " NÃO SEI !!! Conf : " + activity.getConfidence() + " - Sta : " + Globals.getInstance().countStart + " - Sto : " + Globals.getInstance().countStop + (Globals.getInstance().LOCALIZANDO ? " - LOC" : " - ");
                            Utils.getInstance().saveLog("AidavecMotionService", Globals.getInstance().lastMotion);
                            Globals.getInstance().handlerUIHome.sendEmptyMessage(102);
                            break;
                        }
                    }
                }
            }

            if (Globals.getInstance().countStart >= Parameters.COUNT_LIMIT_TO_START && !Globals.getInstance().LOCALIZANDO) {
                Globals.getInstance().lastValidMotion = "Andando de carro !!!";
                Globals.getInstance().handlerUIHome.sendEmptyMessage(103);
                Globals.getInstance().LOCALIZANDO = true;
                Globals.getInstance().firstWaypoint = true;
                AidavecLocation.getInstance().startLocation();
                Globals.getInstance().countStart = 0;
                Globals.getInstance().countStop = 0;
                Utils.getInstance().saveLog("AidavecMotionService", Globals.getInstance().lastValidMotion);

            }

            if (Globals.getInstance().countStop >= Parameters.COUNT_LIMIT_TO_STOP && Globals.getInstance().LOCALIZANDO) {
                Globals.getInstance().lastValidMotion = "Indo a pé !!!";
                Globals.getInstance().handlerUIHome.sendEmptyMessage(103);
                AidavecLocation.getInstance().stopService();
                AidavecLocation.getInstance().stopLocation();
                Globals.getInstance().countToRegisterDistance = 0;
                Globals.getInstance().LOCALIZANDO = false;
                Globals.getInstance().countStart = 0;
                Globals.getInstance().countStop = 0;
                Utils.getInstance().saveLog("AidavecMotionService", Globals.getInstance().lastValidMotion);

            }
        } catch (Exception e) {
            AidavecLocation.getInstance().stopService();
            AidavecLocation.getInstance().stopLocation();
            AidavecMotion.getInstance().stopService();
            AidavecMotion.getInstance().stopRecognition();
        }
    }
}
