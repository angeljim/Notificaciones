package com.ubhave.sensormanager.process.log;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.ubhave.sensormanager.config.SensorConfig;
import com.ubhave.sensormanager.data.log.InteractionData;
import com.ubhave.sensormanager.data.push.ActivityRecognitionData;
import com.ubhave.sensormanager.process.AbstractProcessor;

import java.util.HashMap;

public class InteractionProcessor extends AbstractProcessor
{
	public InteractionProcessor(Context c, boolean rw, boolean sp)
	{
		super(c, rw, sp);
	}

    public InteractionData process(long recvTime, SensorConfig config, Intent dataIntent)
    {
        HashMap<String, String> map = new HashMap<>();
        Bundle bundle = dataIntent.getExtras();
        for(String key: bundle.keySet()) {
            Object object = bundle.get(key);
            String value = (object != null) ? object.toString() : null;
            map.put(key, value);
        }
        return new InteractionData(map);
    }

    public InteractionData process(long recvTime, HashMap<String, String> map)
    {
        return new InteractionData(recvTime, map);
    }

}
