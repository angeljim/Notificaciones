package cl.uai.modlab.activitymonitor.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.ubhave.dataformatter.DataFormatter;
import com.ubhave.dataformatter.json.JSONFormatter;
import com.ubhave.datahandler.except.DataHandlerException;
import com.ubhave.datahandler.loggertypes.AbstractDataLogger;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.SensorDataListener;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.sensors.SensorUtils;

import java.util.Timer;
import java.util.TimerTask;

import cl.uai.modlab.activitymonitor.ActivityMonitorApplication;
import cl.uai.modlab.activitymonitor.ActivityMonitorConstants;
import cl.uai.modlab.activitymonitor.loggers.AsyncWiFiOnlyEncryptedDatabase;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ContextCaptureIntentService extends IntentService implements SensorDataListener {

    private AbstractDataLogger logger;
    private Context context;

    public ContextCaptureIntentService() {
        super("ContextCaptureIntentService");
        try {
            this.context = ActivityMonitorApplication.getAndroidComponent().getApplicationContext();
            this.logger = AsyncWiFiOnlyEncryptedDatabase.getInstance(this.context);
        } catch (ESException | DataHandlerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts this service to perform action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionCapture(Context context, int notificationId) {
        Intent intent = new Intent(context, ContextCaptureIntentService.class);
        intent.setAction(ActivityMonitorConstants.ACTION_CONTEXT_CAPTURE);
        intent.putExtra(ActivityMonitorConstants.EXTRAS_NOTIFICATION_ID, notificationId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ActivityMonitorConstants.ACTION_CONTEXT_CAPTURE.equals(action) && this.logger != null) {
                final int notificationId = intent.getIntExtra(ActivityMonitorConstants.EXTRAS_NOTIFICATION_ID, -1);
                handleActionCapture(notificationId);
            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCapture(int notificationId) {
        try {
            final ESSensorManager sm = ESSensorManager.getSensorManager(this.context);
            final int micSubscriptionId = sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_MICROPHONE, this);
            final int lightSubscriptionId = sm.subscribeToSensorData(SensorUtils.SENSOR_TYPE_LIGHT, this);

            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        sm.unsubscribeFromSensorData(micSubscriptionId);
                        sm.unsubscribeFromSensorData(lightSubscriptionId);
                    } catch (ESException e) {
                        e.printStackTrace();
                    }
                }
            };
            timer.schedule(timerTask, 30*1000); // 30 seconds from now
        } catch (ESException e) {
            e.printStackTrace();
        }
    }

    // This method will be called by the ES Sensor Manager when it has new data to publish
    // and lets you decide what actions to take with that data.
    public void onDataSensed(SensorData data)
    {
        JSONFormatter f = DataFormatter.getJSONFormatter(this.context, data.getSensorType());
        this.logger.logSensorData(data, f);
    }

    // This method will be called by the ES Sensor Manager when the phone's battery falls
    // below a pre-defined, configurable threshold, and again when the phone has been charged
    // above that threshold.
    public void onCrossingLowBatteryThreshold(boolean isBelowThreshold)
    {
    }
}
