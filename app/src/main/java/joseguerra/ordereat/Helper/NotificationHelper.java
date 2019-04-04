package joseguerra.ordereat.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import joseguerra.ordereat.R;

public class NotificationHelper extends ContextWrapper {

    private static final String JRGP_channel_ID="joseguerra.ordereat.Helper.JoseDev";
    private static final String JRGP_channel_NAME="Eat it";
    private NotificationManager manager;


    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)// ESCRIBIR LA FUNCION SI LA API ES MAYOR DE 26
            crearChannel();
    }


    @TargetApi(Build.VERSION_CODES.O)
    private void crearChannel() {
        NotificationChannel jrgpChannel= new NotificationChannel(JRGP_channel_ID,JRGP_channel_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        jrgpChannel.enableLights(false);
        jrgpChannel.enableVibration(true);
        jrgpChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(jrgpChannel);
    }

    public NotificationManager getManager() {
        if (manager==null)
        {
            manager= (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getEatChannelNotification(String titulo, String body, PendingIntent intent, Uri uri)
    {
        return new Notification.Builder(getApplicationContext(), JRGP_channel_ID)
                .setContentIntent(intent)
                .setContentTitle(titulo)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(uri)
                .setAutoCancel(true);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getEatChannelNotification(String titulo, String body, Uri uri)
    {
        return new Notification.Builder(getApplicationContext(), JRGP_channel_ID)
                .setContentTitle(titulo)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(uri)
                .setAutoCancel(true);
    }



}
