/* **************************************************
 Copyright (c) 2016, Universidad Adolfo Ibáñez
 Gonzalo Huerta Canepa, gonzalo.huerta@uai.cl

This library was developed as part of the EPSRC Ubhave (Ubiquitous and
Social Computing for Positive Behaviour Change) Project. For more
information, please visit http://www.emotionsense.org

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 ************************************************** */

package com.ubhave.sensormanager.sensors.push;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.config.push.ActivityRecognitionConfig;
import com.ubhave.sensormanager.data.push.ActivityRecognitionData;
import com.ubhave.sensormanager.process.push.ActivityRecognitionProcessor;
import com.ubhave.sensormanager.sensors.SensorUtils;

public class ActivityRecognitionSensor extends AbstractPushSensor implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "ActivRecognitionSensor";
    private static final String ACTIVITY_RECOGNITION_PERMISSION = "com.google.android.gms.permission.ACTIVITY_RECOGNITION";

    private static ActivityRecognitionSensor activityRecognitionSensor;
    private static final Object lock = new Object();
    private final GoogleApiClient apiClient;

    private int currentType = -1;

    public static ActivityRecognitionSensor getSensor(final Context context) throws ESException
    {
        if (activityRecognitionSensor == null)
        {
            synchronized (lock)
            {
                if (activityRecognitionSensor == null)
                {
                    if (permissionGranted(context, ACTIVITY_RECOGNITION_PERMISSION))
                    {
                        activityRecognitionSensor = new ActivityRecognitionSensor(context);
                    }
                    else
                    {
                        throw new ESException(ESException. PERMISSION_DENIED, SensorUtils.SENSOR_NAME_ACTIVITY_RECOGNITION);
                    }
                }
            }
        }
        return activityRecognitionSensor;
    }

    public ActivityRecognitionSensor(Context context) {
        super(context);
        apiClient = new GoogleApiClient.Builder(context)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onBroadcastReceived(Context context, Intent dataIntent) {
        ActivityRecognitionProcessor processor = (ActivityRecognitionProcessor) getProcessor();
        ActivityRecognitionData sensorData = processor.process(System.currentTimeMillis(), sensorConfig.clone(), dataIntent);
        if (sensorData.getType() != this.currentType)
            onDataSensed(sensorData);
    }

    @Override
    protected IntentFilter[] getIntentFilters() {
        IntentFilter[] filters = new IntentFilter[1];
        filters[0] = new IntentFilter(SensorUtils.ACTIVITY_RECOGNITION_ACTION);
        return filters;
    }

    @Override
    protected boolean startSensing() {
        this.apiClient.connect();
        return true;
    }

    @Override
    protected void stopSensing() {
        this.apiClient.disconnect();
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public int getSensorType() {
        return SensorUtils.SENSOR_TYPE_ACTIVITY_RECOGNITION;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected");
        long sensingInterval = ActivityRecognitionConfig.DEFAULT_SENSING_TIME;
        try {
            sensingInterval = (Long) this.getSensorConfig(ActivityRecognitionConfig.SENSING_TIME);
        } catch (ESException ignored) {}
        Intent intent = new Intent(SensorUtils.ACTIVITY_RECOGNITION_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( this.apiClient, sensingInterval, pendingIntent );
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed");
    }
}
