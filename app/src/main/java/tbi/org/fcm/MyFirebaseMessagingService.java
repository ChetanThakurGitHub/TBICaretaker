package tbi.org.fcm;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import tbi.org.R;
import tbi.org.activity.SplashActivity;
import tbi.org.activity.main_activity.CaretakerHomeActivity;
import tbi.org.activity.main_activity.SuffererHomeActivity;
import tbi.org.session.Session;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";
    private String body;

    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "getFrom : " + remoteMessage.getFrom().toString());
        Log.d(TAG, "getData : " + remoteMessage.getData().toString());
        Log.d(TAG, "getNotification : " + remoteMessage.getNotification().toString());

        if (remoteMessage.getData() != null) {
            notificationHandle(remoteMessage);
        }
    }

    private void notificationHandle(RemoteMessage remoteMessage) {
        // {reminder_date=2018-3-28, reminder_time=11:37 AM, reminder_description=ghhhhhh, reference_id=228, body=success, type=Reminder_add, title=Chetan added new reminder, click_action=ChatActivity, reminder_title=ghdhhdhsh}
        // {reminder_date=0000-00-00, reminder_time=00:00 PM, reminder_description=kckcjfigig, reference_id=257, body=success, type=Reminder_delete, title=Chetan delete the reminder, click_action=ChatActivity, reminder_title=hxhdhdh}
        // {reminder_date=, reminder_time=, reminder_description=you are removed by Chetan, reference_id=, body=success, type=Sufferer_removed, title=Chetan removed you, click_action=ChatActivity, reminder_title=sufferer removed}
        // {reminder_date=, reminder_time=, reminder_description=you are added by Chetan, reference_id=83, body=success, type=Sufferer_add, title=Chetan added you as a sufferer, click_action=ChatActivity, reminder_title=sufferer add}
        // {reminder_date=, reminder_time=, reminder_description=you are added by Chetan, reference_id=84, body=success, type=Sufferer_add, title=Chetan added you as a sufferer, click_action=ChatActivity, reminder_title=sufferer add}// my caretaker remove
        // {reminder_date=2018-03-28, reminder_time=02:52 PM, reminder_description=Bxbnznxnxn, reference_id=261, body=success, type=Reminder_done, title=mindiiisuf done the reminder, click_action=ChatActivity, reminder_title=bxnxbbxxbnx}

        //getData : {other_key=true, uid=116, body=hiii, type=chat, title=chetan thakur, click_action=ChatActivity}

        String type = remoteMessage.getData().get("type");


        if (type != null && !type.equals("")) {
            if (type.equals("Reminder_add") | type.equals("Reminder_delete") | type.equals("Reminder_update")) {
                String reminder_date = remoteMessage.getData().get("reminder_date");
                String notification_id = remoteMessage.getData().get("notification_id");
                String reminder_time = remoteMessage.getData().get("reminder_time");
                String reminder_description = remoteMessage.getData().get("reminder_description");
                String reference_id = remoteMessage.getData().get("reference_id");
                String bodys = remoteMessage.getData().get("body");
                String title = remoteMessage.getData().get("title");
                String click_action = remoteMessage.getData().get("click_action");
                String reminder_title = remoteMessage.getData().get("reminder_title");
                String intentType = "1";
                sendNotificationAddReminder(title, intentType, notification_id);
            } else if (type.equals("Sufferer_removed")) {
                String reminder_description = remoteMessage.getData().get("reminder_description");
                String notification_id = remoteMessage.getData().get("notification_id");
                String intentType = "2";
                sendNotificationAddReminder(reminder_description, intentType, notification_id);
            } else if (type.equals("Sufferer_add")) {
                String title = remoteMessage.getData().get("title");
                String notification_id = remoteMessage.getData().get("notification_id");
                String intentType = "3";
                sendNotificationAddReminder(title, intentType, notification_id);
            } else if (type.equals("Reminder_done")) {
                String title = remoteMessage.getData().get("title");
                String notification_id = remoteMessage.getData().get("notification_id");
                String intentType = "4";
                sendNotificationAddReminder(title, intentType, notification_id);
            } else if (type.equals("Caretaker_removed")) {
                String title = remoteMessage.getData().get("title");
                String notification_id = remoteMessage.getData().get("notification_id");
                String intentType = "5";
                sendNotificationAddReminder(title, intentType, notification_id);
            } else if (type.equals("FAQS_Add") | type.equals("FAQS_Update") | type.equals("FAQS_Delete")) {
                String title = remoteMessage.getData().get("title");
                String notification_id = remoteMessage.getData().get("notification_id");
                String intentType = "6";
                sendNotificationAddReminder(title, intentType, notification_id);
            } else if (type.equals("chat")) {
                String title = remoteMessage.getData().get("title");
                body = remoteMessage.getData().get("body");
                String notification_id = remoteMessage.getData().get("notification_id");
                String intentType = "7";
                sendNotificationAddReminder(title, intentType, notification_id);
            }
        }
    }

    private void sendNotificationAddReminder(String title, String intentType, String notification_id) {
        Intent intent = null;
        Session session = new Session(this);
        if (session.getIsLogedIn()) {
            if (intentType.equals("1")) {
                intent = new Intent(this, SuffererHomeActivity.class);
                intent.putExtra("notification_id", notification_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (intentType.equals("2")) {
                intent = new Intent(this, SuffererHomeActivity.class);
                intent.putExtra("notification_id", notification_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (intentType.equals("3")) {
                intent = new Intent(this, SuffererHomeActivity.class);
                intent.putExtra("NOTIFICATION", "data");
                intent.putExtra("notification_id", notification_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (intentType.equals("4")) {
                intent = new Intent(this, CaretakerHomeActivity.class);
                intent.putExtra("notification_id", notification_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (intentType.equals("5")) {
                intent = new Intent(this, CaretakerHomeActivity.class);
                intent.putExtra("notification_id", notification_id);
                intent.putExtra("NOTIFICATION", "addSufferer");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (intentType.equals("6")) {
                intent = new Intent(this, SuffererHomeActivity.class);
                intent.putExtra("notification_id", notification_id);
                intent.putExtra("NOTIFICATION", "faqSufferer");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (intentType.equals("7")) {
                if (session.getUserType().equals("1")) {
                    intent = new Intent(this, SuffererHomeActivity.class);
                } else {
                    intent = new Intent(this, CaretakerHomeActivity.class);
                }
                intent.putExtra("NOTIFICATION", "chat");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        } else {
            intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);
        Uri notificaitonSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo))
                .setSmallIcon(R.drawable.ic_sufferer_white)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle("TBI Caretaker")
                .setContentText(title)
                .setAutoCancel(true)
                .setSound(notificaitonSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        if (intentType.equals("7")) {
            NotificationCompat.Builder notificationBuilder1 = new NotificationCompat.Builder(this)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo))
                    .setSmallIcon(R.drawable.ic_sufferer_white)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(notificaitonSound)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager1.notify(0, notificationBuilder1.build());
        }


    }

}
