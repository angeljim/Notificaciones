package cl.uai.modlab.activitymonitor;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.tumblr.remember.Remember;

import cl.uai.modlab.activitymonitor.injection.AndroidComponent;
import cl.uai.modlab.activitymonitor.injection.AndroidModule;
import cl.uai.modlab.activitymonitor.injection.DaggerAndroidComponent;
import cl.uai.modlab.activitymonitor.notifications.NotificationManager;

/**
 * Created by gohucan on 17-04-16.
 */
public class ActivityMonitorApplication extends MultiDexApplication {

    private static AndroidComponent androidComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        // build the android component module for dagger 2 injection
        androidComponent = DaggerAndroidComponent.builder().androidModule(new AndroidModule(this)).build();
        // initialize the shared preferences library
        Remember.init(getApplicationContext(), "cl.modlab.uai.activitymonitor");
        // initializes the notification manager
        NotificationManager nm = new NotificationManager(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static AndroidComponent getAndroidComponent() {
        return androidComponent;
    }
}
