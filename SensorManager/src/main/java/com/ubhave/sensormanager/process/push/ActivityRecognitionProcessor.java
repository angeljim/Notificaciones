package com.ubhave.sensormanager.process.push;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.ubhave.sensormanager.config.SensorConfig;
import com.ubhave.sensormanager.data.push.ActivityRecognitionData;
import com.ubhave.sensormanager.data.push.BatteryData;
import com.ubhave.sensormanager.process.AbstractProcessor;

public class ActivityRecognitionProcessor extends AbstractProcessor
{
	public ActivityRecognitionProcessor(Context c, boolean rw, boolean sp)
	{
		super(c, rw, sp);
	}

    public ActivityRecognitionData process(long recvTime, SensorConfig config, Intent dataIntent)
    {
        ActivityRecognitionData data = new ActivityRecognitionData(recvTime, config);
        if(ActivityRecognitionResult.hasResult(dataIntent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(dataIntent);
            for(DetectedActivity activity: result.getProbableActivities() ){
                if (activity.getConfidence() >= 75) {
                    data.setConfidence(activity.getConfidence());
                    data.setType(activity.getType());
                    if (setRawData)
                    {
                        data.setDetectedActivity(activity);
                    }
                }
            }
        }
        return data;
    }

    public ActivityRecognitionData process(long recvTime, SensorConfig config, DetectedActivity activity)
    {
        ActivityRecognitionData data = new ActivityRecognitionData(recvTime, config);
        if (activity.getConfidence() >= 75) {
            data.setConfidence(activity.getConfidence());
            data.setType(activity.getType());
            if (setRawData)
            {
                data.setDetectedActivity(activity);
            }
        }
        return data;
    }

}
