package com.fiuba.tdp.petadopt.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.activities.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;


public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String type = data.getString("type");
        String petId = data.getString("pet_id");
        String userId = data.getString("user_id");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Type: " + type);

        if (type == null) {
            return;
        }

        String message = "";
        switch (type) {
            case "create_adoption":
                message = getString(R.string.adoption_request_notification);
                break;
            case "create_lost":
                message = getString(R.string.find_notification);
                break;
            case "accept_adoption":
                message = getString(R.string.accepted_own_adoption_request);
                break;
            case "accept_lost":
                message = getString(R.string.accepted_own_lost_notification);
                break;
            case "lost_pet_match":
                message = getString(R.string.matched_lost_pet_notification);
                break;
            case "found_pet_match":
                message = getString(R.string.matched_found_pet_notification);
                break;
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */

        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString("pet_id", petId);
        bundle.putString("user_id", userId);
        sendNotification(message, bundle);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, Bundle bundle) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notif_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}