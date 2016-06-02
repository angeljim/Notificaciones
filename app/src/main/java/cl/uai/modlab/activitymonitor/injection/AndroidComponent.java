package cl.uai.modlab.activitymonitor.injection;

import android.content.Context;

import javax.inject.Singleton;

import cl.uai.modlab.activitymonitor.notifications.NotificationGenerator;
import cl.uai.modlab.activitymonitor.notifications.NotificationManager;
import dagger.Component;

/**
 * Created by gohucan on 17-04-16.
 */
@Singleton
@Component(modules = AndroidModule.class)
public interface AndroidComponent {
    //Exposes Application to any component which depends on this
    Context getApplicationContext();
    NotificationGenerator getNotificationGenerator();
}
