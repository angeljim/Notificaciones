/* **************************************************
 Copyright (c) 2014, Idiap
 Hugues Salamin, hugues.salamin@idiap.ch

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

package com.ubhave.dataformatter.json.push;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.DetectedActivity;
import com.ubhave.dataformatter.json.PushSensorJSONFormatter;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.config.SensorConfig;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.push.ActivityRecognitionData;
import com.ubhave.sensormanager.data.push.PassiveLocationData;
import com.ubhave.sensormanager.process.AbstractProcessor;
import com.ubhave.sensormanager.process.push.ActivityRecognitionProcessor;
import com.ubhave.sensormanager.process.push.PassiveLocationProcessor;
import com.ubhave.sensormanager.sensors.SensorUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityRecognitionFormatter extends PushSensorJSONFormatter
{
	private final static String CONFIDENCE = "confidence";
	private final static String TYPE = "type";
	private final static String STRING_TYPE = "string_type";
	private final static String TIME = "time";

	private final static String UNKNOWN_STRING = "unknown";
	private final static double UNKNOWN_DOUBLE = 0.0;
	private final static long UNKNOWN_LONG = 0;

	public ActivityRecognitionFormatter(final Context context)
	{
		super(context, SensorUtils.SENSOR_TYPE_ACTIVITY_RECOGNITION);
	}

	@Override
	public SensorData toSensorData(String jsonString)
	{
		JSONObject jsonData = super.parseData(jsonString);
		long senseStartTimestamp = super.parseTimeStamp(jsonData);
		SensorConfig sensorConfig = super.getGenericConfig(jsonData);

		boolean setRawData = true;
		boolean setProcessedData = false;
		DetectedActivity detectedActivity;
		try
		{
			detectedActivity = new DetectedActivity(jsonData.getInt(TYPE),jsonData.getInt(CONFIDENCE));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}
		try
		{
			ActivityRecognitionProcessor processor = (ActivityRecognitionProcessor) AbstractProcessor.getProcessor(applicationContext, sensorType, setRawData, setProcessedData);
			return processor.process(senseStartTimestamp, sensorConfig, detectedActivity);
		}
		catch (ESException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	protected void addSensorSpecificData(JSONObject json, SensorData data) throws JSONException
	{
		ActivityRecognitionData activityRecognitionData = (ActivityRecognitionData) data;
		DetectedActivity activity = activityRecognitionData.getDetectedActivity();
		if (activity != null)
		{
			json.put(CONFIDENCE, activity.getConfidence());
			json.put(TYPE, activity.getType());
			json.put(TIME, System.currentTimeMillis());
		}
		else
		{
			json.put(CONFIDENCE, ((ActivityRecognitionData) data).getConfidence());
			json.put(TYPE, ((ActivityRecognitionData) data).getType());
			json.put(TIME, System.currentTimeMillis());
		}
	}

}
