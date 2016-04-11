package com.usertaxi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.usertaxi.gcmclasses.Config;
import com.usertaxi.gcmclasses.Controller;
import com.usertaxi.utils.AppPreferences;
import org.json.JSONException;
import org.json.JSONObject;


@SuppressWarnings("deprecation")
public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";
    public static int NOTIFICATION_ID = 1;
    private Controller aController = null;
    public static NotificationManager mNotificationManager;

    static int notify_no = 0;
    String message, tripSendmessage, tripAccept, tripCancel, tripArrived, finishTrip,notifyAccept,emailverify;
    public static String productId = "", reciverId = "", productName,
            productPrice, productOwnerName;

    public GCMIntentService() {
        // Call extended class Constructor GCMBaseIntentService
        super(Config.GOOGLE_SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {

        // Get Global Controller Class object (see application tag in
        // AndroidManifest.xml)
        if (aController == null)
            aController = (Controller) getApplicationContext();

        Log.i(TAG, "---------- onRegistered -------------");
        Log.i(TAG, "Device registered: regId = " + registrationId);

        aController.displayRegistrationMessageOnScreen(context,
                "Your device registred with GCM");
        // Log.d("NAME", ChatActivity.name);

        aController.register(context, RegisterActivity.name, RegisterActivity.email,
                registrationId, RegisterActivity.imei);


    }

    /**
     * Method called on device unregistred
     */
    @Override
    protected void onUnregistered(Context context, String registrationId) {

        if (aController == null)
            aController = (Controller) getApplicationContext();

        Log.i(TAG, "---------- onUnregistered -------------");
        Log.i(TAG, "Device unregistered");

        aController.displayRegistrationMessageOnScreen(context,
                getString(R.string.gcm_unregistered));

        aController.unregister(context, registrationId, LoginActivity.imei);
    }

    /**
     * Method called on Receiving a new message from GCM server
     */
    @Override
    protected void onMessage(Context context, Intent intent) {

        if (aController == null)
            aController = (Controller) getApplicationContext();

        Log.i(TAG, "---------- onMessage -------------");
        message = "" + intent.getExtras().getString("message");
        tripAccept = "" + intent.getExtras().getString("tripaccept");
        tripCancel = "" + intent.getExtras().getString("tripcancelled");
        tripArrived = "" + intent.getExtras().getString("triparrived");
        tripSendmessage = "" + intent.getExtras().getString("sendmessage");
        finishTrip = "" + intent.getExtras().getString("borded");
        notifyAccept = "" + intent.getExtras().getString("admin");
        emailverify = "" + intent.getExtras().getString("activation");
        Log.d("myyyyyymessage", tripCancel + " accept :" + tripAccept + "finish" +finishTrip +"adminnoti"+notifyAccept);

        if (!message.equalsIgnoreCase("null")) {
        } else if (!tripAccept.equalsIgnoreCase("null")) {
            sendNotificationDetail("Booking Request Accept", tripAccept);
        } else if (!tripCancel.equalsIgnoreCase("null")) {
            Log.d("tripcancelmessage:---", tripCancel);
            try {
                JSONObject jsonObject = new JSONObject(tripCancel);
                sendNotificationCancel(jsonObject.getString("message"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (!tripArrived.equalsIgnoreCase("null")) {
            sendNotificationArrived("Taxi Arrived", tripArrived);
        } else if (!tripSendmessage.equalsIgnoreCase("null")) {

            Log.d("tripmessage:---", tripSendmessage);
            sendNotificationSendmessage("Driver sent message", tripSendmessage);
            Intent intentt = new Intent(this, RecivedMessage.class);
            intentt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(tripSendmessage);
                intentt.putExtra("message", jsonObject.getString("message"));
                //if (TaxiOntheWay_Activity.taxiOnTheWay) {
                    startActivity(intentt);
               // }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (!finishTrip.equalsIgnoreCase("null")) {
            Log.d("tripmessage:---", finishTrip +":::"+RateExperience_Activity.instance);
            Log.d("RateTest", "0111");
            if(RateExperience_Activity.instance == null){
                Log.d("RateTest", "111100000");
                sendNotificationFinishtrip("Trip Succesfully completed", finishTrip);

            }

        } else if (!notifyAccept.equalsIgnoreCase("null")) {
            Log.d("tripmessage:---", notifyAccept);
            notificationDetailbyadmin("New Message", notifyAccept);
        }else if (!emailverify.equalsIgnoreCase("null")) {
            Log.d("tripmessage:---", emailverify);
            reciveemailnotification("Email verify successfully", emailverify);
        }
        Log.i("GCM", "message : " + message);
    }


    /**
     * Method called on receiving a deleted message
     */
    @Override
    protected void onDeletedMessages(Context context, int total) {

        if (aController == null)
            aController = (Controller) getApplicationContext();

        Log.i(TAG, "---------- onDeletedMessages -------------");
        String message = getString(R.string.gcm_deleted, total);

        String title = "DELETED";


    }

    /**
     * Method called on Error
     */
    @Override
    public void onError(Context context, String errorId) {

        if (aController == null)
            aController = (Controller) getApplicationContext();

        Log.i(TAG, "---------- onError -------------");
        Log.i(TAG, "Received error: " + errorId);
        aController.displayRegistrationMessageOnScreen(context,
                getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {

        if (aController == null)
            aController = (Controller) getApplicationContext();

        Log.i(TAG, "---------- onRecoverableError -------------");

        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        aController.displayRegistrationMessageOnScreen(context,
                getString(R.string.gcm_recoverable_error, errorId));

        return super.onRecoverableError(context, errorId);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, NotificationActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("User Center")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendNotificationDetail(String msg, String tripAccept) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, TaxiOntheWay_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("data", tripAccept);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        System.out.println("ShowTaxi_InMap.showTaxiInMap " + ShowTaxi_InMap.showTaxiInMap + " DrawerMainActivity.drawerActivity :  " + DrawerMainActivity.drawerActivity);
        if (ShowTaxi_InMap.showTaxiInMap || DrawerMainActivity.drawerActivity) {
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(intent);
            if(ShowTaxi_InMap.showTaxi_InMapInstance != null)
            {
                ActivityCompat.finishAffinity(ShowTaxi_InMap.showTaxi_InMapInstance);
            }
        } else {
            AppPreferences.setNotificationMessage(getApplicationContext(),tripAccept);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    this).setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("User Center")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
                    .setContentText(msg);
            mBuilder.setContentIntent(contentIntent).setAutoCancel(true);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            mBuilder.setAutoCancel(true);
        }

    }

    private void sendNotificationArrived(String msg, String tripArrived) {

        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, TaxiArrived_Acitivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("data", tripArrived);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        if (TaxiOntheWay_Activity.taxiOnTheWay)
        {
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(intent);
            if(TaxiOntheWay_Activity.taxiOntheWay_activity_instance != null)
            {
                ActivityCompat.finishAffinity(TaxiOntheWay_Activity.taxiOntheWay_activity_instance);
            }
        }else{
            AppPreferences.setNotificationMessage(getApplicationContext(),tripArrived);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    this).setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("User Central")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
                    .setContentText(msg);
            mBuilder.setContentIntent(contentIntent).setAutoCancel(true);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            mBuilder.setAutoCancel(true);
        }
    }

    private void sendNotificationSendmessage(String msg, String tripSendmessage) {


        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, NotificationActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("User Center")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
                .setContentText(msg);
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        mNotificationManager.cancel(NOTIFICATION_ID);
        mBuilder.setAutoCancel(true);
    }

    private void sendNotificationCancel(String msg) {
        try {
            AppPreferences.setApprequestTaxiScreen(getApplicationContext(), false);
            AppPreferences.setTripId(getApplicationContext(), "");

            Intent intent = new Intent(this, DrawerMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("notificationData", msg);

            if (TaxiOntheWay_Activity.taxiOnTheWay || TaxiArrived_Acitivity.taxiArrived) {
                if(TaxiOntheWay_Activity.taxiOntheWay_activity_instance != null)
                {
                    ActivityCompat.finishAffinity(TaxiOntheWay_Activity.taxiOntheWay_activity_instance);
                }
                if(TaxiArrived_Acitivity.taxiArrived_AcitivityInstance != null)
                {
                    ActivityCompat.finishAffinity(TaxiArrived_Acitivity.taxiArrived_AcitivityInstance);
                }

                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("activityTest", TaxiOntheWay_Activity.taxiOnTheWay + " : " + TaxiArrived_Acitivity.taxiArrived);
                startActivity(intent);
            }else
            {
                if(TaxiOntheWay_Activity.taxiOntheWay_activity_instance != null)
                {
                    ActivityCompat.finishAffinity(TaxiOntheWay_Activity.taxiOntheWay_activity_instance);
                }

                if(TaxiArrived_Acitivity.taxiArrived_AcitivityInstance != null)
                {
                    ActivityCompat.finishAffinity(TaxiArrived_Acitivity.taxiArrived_AcitivityInstance);
                }

                mNotificationManager = (NotificationManager) this
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        this).setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("User Central")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
                        .setContentText(msg);
                mBuilder.setContentIntent(contentIntent).setAutoCancel(true);
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                mBuilder.setAutoCancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendNotificationFinishtrip(String msg, String finishtrip) {

        Log.d("RateTest", "aaa");
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d("RateTest", "vvvvv");
        Intent intent = new Intent(this, RateExperience_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("data", finishtrip);
        Log.d("RateTest", "1bbbb11");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        Log.d("RateTest", "ccc");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("User Center")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
                .setContentText(msg);
        Log.d("RateTest", "dddd");
       // if (ShowTaxi_InMap.showTaxi_InMapInstance != null || DrawerMainActivity.mainActivityInstance != null) {
            Log.d("RateTest", "111");
            mBuilder.setContentIntent(contentIntent);
            if (TaxiOntheWay_Activity.taxiOnTheWay || TaxiArrived_Acitivity.taxiArrived) {
                Log.d("RateTest", TaxiArrived_Acitivity.taxiArrived+"111"+TaxiOntheWay_Activity.taxiOnTheWay);
                Log.d("activityTest", TaxiOntheWay_Activity.taxiOnTheWay + " : " + TaxiArrived_Acitivity.taxiArrived);
                startActivity(intent);
            }
//            try {
//                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                r.play();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

      //  } else {
       //     mBuilder.setContentIntent(contentIntent);
       //     mBuilder.setAutoCancel(true);
       // }
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        mBuilder.setAutoCancel(true);
//        if (ShowTaxi_InMap.instance != null || DrawerMainActivity.mainActivityInstance != null) {
//            mNotificationManager.cancel(NOTIFICATION_ID);
//        } else {
//        }
    }

    private void notificationDetailbyadmin(String msg, String notifyAccept) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //  Intent notificationIntent = new Intent().setClassName("com.corporatetaxi", "com.corporatetaxi.TaxiOntheWay_Activity");
        intent.putExtra("data", notifyAccept);
        //   notificationIntent.putExtra("data", tripAccept);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("User Central")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
                .setContentText(msg);

        if ((ShowTaxi_InMap.showTaxiInMap || DrawerMainActivity.drawerActivity) && (ShowTaxi_InMap.showTaxi_InMapInstance != null || DrawerMainActivity.mainActivityInstance != null)) {
            mBuilder.setContentIntent(contentIntent);
            startActivity(intent);

        } else {
            mBuilder.setContentIntent(contentIntent).setAutoCancel(true);

        }

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        mBuilder.setAutoCancel(true);
    }
    private void reciveemailnotification(String msg, String emailverify) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, UserProfileAcivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("data", emailverify);
        //   notificationIntent.putExtra("data", tripAccept);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("User Central")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
                .setContentText(msg);
                mBuilder.setContentIntent(contentIntent);
                startActivity(intent);
//        if ((ShowTaxi_InMap.showTaxiInMap || DrawerMainActivity.drawerActivity) && (ShowTaxi_InMap.showTaxi_InMapInstance != null || DrawerMainActivity.mainActivityInstance != null)) {
//            mBuilder.setContentIntent(contentIntent);
//            startActivity(intent);
//
//        } else {
//            mBuilder.setContentIntent(contentIntent).setAutoCancel(true);
//
//        }

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        mBuilder.setAutoCancel(true);
    }
}