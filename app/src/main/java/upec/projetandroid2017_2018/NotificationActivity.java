package upec.projetandroid2017_2018;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import upec.projetandroid2017_2018.Accueil.AccueilActivity;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
    }

    public class AlertReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent received = getIntent();
            String title = received.getStringExtra("Title");

            Intent intent1 =  new Intent(context, AccueilActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"ChannelID")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_menu_camera)
                    .setTicker("Hearty365")
                    .setContentTitle(title)
                    .setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
                    .setContentInfo("HELLO EVERY BODY")
                    .setAutoCancel(true);
            notificationManager.notify(100,builder.build());
        }
    }
}
