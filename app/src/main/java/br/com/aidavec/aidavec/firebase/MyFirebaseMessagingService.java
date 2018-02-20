package br.com.aidavec.aidavec.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.models.Note;
import br.com.aidavec.aidavec.views.MainActivity;

/**
 * Created by leonardo.saganski on 04/01/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        Map<String, String> data = remoteMessage.getData();
        // if (data.size() > 0)
        if (data.size() > 0) {
            //Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            //Log.d(TAG, "Message data payload: " + data);
            //Log.d(TAG, "Nome: " + data.get("nome"));
            //Log.d(TAG, "Sobrenome: " + data.get("sobrenome"));
            Globals.getInstance().increaseCountUnreadNotes();

            Note note = new Note();
            note.setNot_titulo(data.get("NOT_TITULO"));
            note.setNot_mensagem(data.get("NOT_MENSAGEM"));
            note.setNot_tipo(data.get("NOT_TIPO"));
            note.setNot_opcaoa(data.get("NOT_OPCAOA"));
            note.setNot_opcaob(data.get("NOT_OPCAOB"));
            note.setNot_opcaoc(data.get("NOT_OPCAOC"));
            note.setNot_opcaod(data.get("NOT_OPCAOD"));
            note.setNot_opcaoe(data.get("NOT_OPCAOE"));

            Globals.getInstance().lastPush = note;
            //    if (Utils.getInstance().getYesNoWithExecutionStop("Nova notificação", "Você acaba de receber uma nova notificação. Deseja visualizá-la agora ?", Globals.getInstance().context)) {
            //        startActivity(new Intent(Globals.getInstance().context, NoteFrag.class));
            //    }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }



        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //Uri defaultSoundUri= getResources(). RingtoneManager.ge tDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_drawer_carro)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(Uri.parse("android.resource://" + getPackageName() + "bells.mp3"))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}