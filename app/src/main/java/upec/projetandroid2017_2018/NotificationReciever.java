package upec.projetandroid2017_2018;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import upec.projetandroid2017_2018.Lists.TestScreen;

/**
 * Created by Sasig on 16/03/2018.
 */

public class NotificationReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(context, TestScreen.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //if we want ring on notifcation then uncomment below line//
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"my_channel_id_01").
                setSmallIcon(R.drawable.dialog_bg).
                setContentIntent(pendingIntent).
                setContentText("this is my notification").
                setContentTitle("my notificaton").
//                setSound(alarmSound).
        setAutoCancel(true);
        notificationManager.notify(100, builder.build());

    }
}
