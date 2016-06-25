package cl.uai.modlab.activitymonitor.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.sensors.SensorUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import br.com.goncalves.pugnotification.notification.PugNotification;
import cl.uai.modlab.activitymonitor.ActivityMonitorApplication;
import cl.uai.modlab.activitymonitor.ActivityMonitorConstants;
import cl.uai.modlab.activitymonitor.SurveyActivity;
import cl.uai.modlab.activitymonitor.injection.AndroidModule;
import cl.uai.modlab.activitymonitor.R;
import cl.uai.modlab.activitymonitor.services.ContextCaptureIntentService;

/**
 * Created by gohucan on 16-04-16.
 */
@Singleton
public final class NotificationManager {

    private final static AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private int previousId;
    private EventBus eventBus;
    private Context context;

    @Inject
    public NotificationManager(Context context) {
        this.eventBus = EventBus.getDefault();
        this.eventBus.register(this);
        this.context = context;
    }

    @Subscribe
    public void onEvent(final NotificationEvent event) {
        // get a new notification id
        final int id = ID_GENERATOR.incrementAndGet();
        // wait 5 seconds and send a notification
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                // shows the notification
                showNotification(id, event);
            }
        };
        timer.schedule(timerTask, 5*1000);
        // starts an intent service for listening context
        ContextCaptureIntentService.startActionCapture(this.context, id);
    }

    private void showNotification(int id, NotificationEvent event) {
        if (previousId != 0) {
            try {
                android.app.NotificationManager notificationManager = (android.app.NotificationManager) this.context.getSystemService(this.context.NOTIFICATION_SERVICE);
                notificationManager.cancel(previousId);
            } catch (Exception ignore) { }
        }
        this.previousId = id;
        Intent intent = new Intent(context, SurveyActivity.class);
        intent.putExtra(ActivityMonitorConstants.EXTRAS_NOTIFICATION_ID, id);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Intent dismissIntent = new Intent(SensorUtils.INTERACTION_ACTION);
        dismissIntent.putExtra(ActivityMonitorConstants.EXTRAS_NOTIFICATION_ID, id);
        dismissIntent.putExtra(ActivityMonitorConstants.EXTRAS_ACTION_TYPE, ActivityMonitorConstants.EXTRAS_ACTION_TYPE_DISMISS);
        PendingIntent dismissPendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        0,
                        dismissIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        String title = event.getTitle();
        String message = event.getMessage();
        PugNotification.with(context)
                .load()
                .identifier(id)
                .title(title == null?this.context.getString(R.string.app_name):title)
                .message(message == null?this.context.getString(R.string.new_notification):message)
                //.bigTextStyle(bigtext)
                .smallIcon(R.drawable.ic_notification)
                //.largeIcon(largeIcon)
                .flags(Notification.DEFAULT_ALL)
                .click(pendingIntent)
                .dismiss(dismissPendingIntent)
                //.button(icon, title, pendingIntent)
                //.color(color)
                //.ticker(ticker) // text that is displayed when msg arrives
                //.when(when) // timestamp
                //.vibrate(vibrate)
                //.lights(color, ledOnMs, ledOfMs)
                //.sound(sound)
                .autoCancel(true)
                .simple()
                .build();
    }

}
