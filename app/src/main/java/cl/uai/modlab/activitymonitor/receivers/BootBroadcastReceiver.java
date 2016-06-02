package cl.uai.modlab.activitymonitor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import cl.uai.modlab.activitymonitor.services.ContextCaptureService;
import cl.uai.modlab.activitymonitor.services.NotificationService;

/**
 * Created by gohucan on 31-05-16.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Launch the specified service when this message is received
        Intent startNotificationServiceIntent = new Intent(context, NotificationService.class);
        context.startService(startNotificationServiceIntent);
        Intent startContextCaptureServiceIntent = new Intent(context, ContextCaptureService.class);
        context.startService(startContextCaptureServiceIntent);
    }
}
