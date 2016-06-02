package cl.uai.modlab.activitymonitor.injection;

import android.content.Context;

import javax.inject.Singleton;

import cl.uai.modlab.activitymonitor.notifications.NotificationGenerator;
import cl.uai.modlab.activitymonitor.notifications.NotificationManager;
import dagger.Module;
import dagger.Provides;

/**
 * Based on https://raw.githubusercontent.com/square/dagger/master/examples/android-simple/src/main/java/com/example/dagger/simple/AndroidModule.java
 * Created by gohucan on 17-04-16.
 */
@Module
public class AndroidModule {
    private final Context context;
    private final NotificationGenerator notificationGenerator;

    public AndroidModule(Context context) {
        this.context = context;
        this.notificationGenerator = new NotificationGenerator();
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link ForApplication @Annotation} to explicitly differentiate it from an activity context.
     */
    @Provides
    @Singleton
    public Context providesApplicationContext() {
        return this.context;
    }

    @Provides
    @Singleton
    public NotificationGenerator providesNotificationGenerator() {
        return this.notificationGenerator;
    }

}
