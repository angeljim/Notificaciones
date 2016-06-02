package cl.uai.modlab.activitymonitor.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.ubhave.dataformatter.DataFormatter;
import com.ubhave.dataformatter.json.JSONFormatter;
import com.ubhave.datahandler.except.DataHandlerException;
import com.ubhave.datahandler.loggertypes.AbstractDataLogger;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.SensorDataListener;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.sensors.SensorUtils;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import cl.uai.modlab.activitymonitor.ActivityMonitorApplication;
import cl.uai.modlab.activitymonitor.injection.AndroidComponent;
import cl.uai.modlab.activitymonitor.loggers.AsyncWiFiOnlyEncryptedDatabase;

public class ContextCaptureService extends Service implements SensorDataListener {
    private static final String TAG = "ContextCaptureService";

    @Inject
    Context context;
    private AbstractDataLogger logger;
    private ESSensorManager sm;

    public ContextCaptureService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = ActivityMonitorApplication.getAndroidComponent().getApplicationContext();
        try {
            this.logger = AsyncWiFiOnlyEncryptedDatabase.getInstance(this.context);
            this.sm = ESSensorManager.getSensorManager(this.context);
        } catch (ESException | DataHandlerException e) {
            e.printStackTrace();
        }
    }

    /**
     ESSensorManager sm = ESSensorManager.getSensorManager(getApplicationContext());
     sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_ACTIVITY_RECOGNITION, this);
     sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_BATTERY, this);
     sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_CALL_CONTENT_READER, this);
     sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_LIGHT, this);
     sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_MICROPHONE, this);
     sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_PASSIVE_LOCATION, this);
     sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_PHONE_RADIO, this);
     sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_PHONE_STATE, this);

     */

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * {@link Context#startService}, providing the arguments it supplied and a
     * unique integer token representing the start request.  Do not call this method directly.
     * <p/>
     * <p>For backwards compatibility, the default implementation calls
     * {@link #onStart} and returns either {@link #START_STICKY}
     * or {@link #START_STICKY_COMPATIBILITY}.
     * <p/>
     * <p>If you need your application to run on platform versions prior to API
     * level 5, you can use the following model to handle the older {@link #onStart}
     * callback in that case.  The <code>handleCommand</code> method is implemented by
     * you as appropriate:
     * <p/>
     * {@sample development/samples/ApiDemos/src/com/example/android/apis/app/ForegroundService.java
     * start_compatibility}
     * <p/>
     * <p class="caution">Note that the system calls this on your
     * service's main thread.  A service's main thread is the same
     * thread where UI operations take place for Activities running in the
     * same process.  You should always avoid stalling the main
     * thread's event loop.  When doing long-running operations,
     * network calls, or heavy disk I/O, you should kick off a new
     * thread, or use {@link AsyncTask}.</p>
     *
     * @param intent  The Intent supplied to {@link Context#startService},
     *                as given.  This may be null if the service is being restarted after
     *                its process has gone away, and it had previously returned anything
     *                except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags   Additional data about this start request.  Currently either
     *                0, {@link #START_FLAG_REDELIVERY}, or {@link #START_FLAG_RETRY}.
     * @param startId A unique integer representing this specific request to
     *                start.  Use with {@link #stopSelfResult(int)}.
     * @return The return value indicates what semantics the system should
     * use for the service's current started state.  It may be one of the
     * constants associated with the {@link #START_CONTINUATION_MASK} bits.
     * @see #stopSelfResult(int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        try {
            sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_ACTIVITY_RECOGNITION, this);
            sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_CALL_CONTENT_READER, this);
            sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_PASSIVE_LOCATION, this);
            sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_PHONE_RADIO, this);
            sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_PHONE_STATE, this);
            sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_SCREEN, this);
            sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_INTERACTION, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDataSensed(SensorData data) {
        JSONFormatter f = DataFormatter.getJSONFormatter(this.context, data.getSensorType());
        this.logger.logSensorData(data, f);
    }

    @Override
    public void onCrossingLowBatteryThreshold(boolean isBelowThreshold) {

    }
}
