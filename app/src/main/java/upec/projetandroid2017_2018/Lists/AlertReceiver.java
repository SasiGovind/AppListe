package upec.projetandroid2017_2018.Lists;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

import upec.projetandroid2017_2018.Accueil.AccueilActivity;
import upec.projetandroid2017_2018.R;

/**
 * Created by Sasig on 17/03/2018.
 */

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent1 =  new Intent(context, AccueilActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String title = intent.getStringExtra("Title");
        String mode = intent.getStringExtra("Mode");
        String category = intent.getStringExtra("Category");
        int icon = intent.getIntExtra("Icon", R.mipmap.ic_home);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"ChannelID")
                .setContentIntent(pendingIntent)
                .setSmallIcon(icon)
                .setTicker("ToDo List")
                .setContentTitle(title)
                .setContentText("Category : "+category)
                .setContentInfo(mode)
                .setAutoCancel(true);
        notificationManager.notify(100,builder.build());

    }
}
