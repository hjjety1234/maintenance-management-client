package com.wondertek.video.sensor;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;

public class SensorObserver implements SensorEventListener {
	private static String TAG = "Sensor";
	private static SensorObserver instance = null;
	private VenusActivity venusHandle = null;

	private final static int ESENSORTYPE_Gravity = 0;
	private final static int ESENSORTYPE_Orientation = 1;
	private final static int ESENSORTYPE_Temperature = 2;
	
//	private static float xValue = 0;
//	private static float yValue = 0;
//	private static float zValue = 0;
	
	private static SensorManager mSensorManager;
	
	private SensorHandle acceSensor;
	private SensorHandle orieSensor;
	private SensorHandle tempSensor;

	private class SensorHandle {
		private Sensor mSensor;
		
		private int type;
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		private float oldXValue = 0;
		private int accuracyXValue;
		
		private float oldYValue = 0;
		private int accuracyYValue;
		
		private float oldZValue = 0;
		private int accuracyZValue;
	}
	
	private SensorObserver(VenusActivity va)
	{
		venusHandle = va;
	}
	
	public static SensorObserver getInstance(VenusActivity va)
	{
		if(instance == null)
		{
			instance = new SensorObserver(va);
		}
		
		return instance;
	}
	
	public boolean isSensorReady(int nType) {
		Log.d(TAG, "on isSensorReady");
		
		if (mSensorManager == null) {
			mSensorManager = (SensorManager) VenusApplication.getInstance().getSystemService(Context.SENSOR_SERVICE);
		}
		Log.d(TAG, "mSensorManager: " + mSensorManager);

		if (nType == ESENSORTYPE_Gravity)
			nType = Sensor.TYPE_ACCELEROMETER;
		else if (nType == ESENSORTYPE_Orientation)
			nType = Sensor.TYPE_ORIENTATION;
		else if (nType == ESENSORTYPE_Temperature)
			nType = Sensor.TYPE_TEMPERATURE;
		else
			return false;

		List<Sensor> sensors = mSensorManager.getSensorList(nType);
		
		if (sensors.size() > 0)
			return true;
		else
			return false;
	}

	public Object registerSensorEvent(int nType, int during) {
		Log.d(TAG, "onRegisterSensorEvent");
		
		if (mSensorManager == null) {
			mSensorManager = (SensorManager) VenusApplication.getInstance().getSystemService(Context.SENSOR_SERVICE);
			if(mSensorManager == null)
				return null;
		}
		Log.d(TAG, "during: " + during);
		during =3;
		if (during < 0 || during > 3)
			return null;
		
		if (nType == ESENSORTYPE_Gravity) {
			acceSensor = new SensorHandle();
			acceSensor.setType(ESENSORTYPE_Gravity);
			acceSensor.mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mSensorManager.registerListener(this, acceSensor.mSensor, during);
			return acceSensor;
		}
		if (nType == ESENSORTYPE_Orientation) {
			orieSensor = new SensorHandle();
			orieSensor.setType(ESENSORTYPE_Orientation);
			orieSensor.mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION); 
			mSensorManager.registerListener(this, orieSensor.mSensor, during);
			Log.d(TAG, "mOrientationSensor: " + orieSensor.mSensor);
			return orieSensor;
		}
		if (nType == ESENSORTYPE_Temperature) {
			tempSensor = new SensorHandle();
			tempSensor.setType(ESENSORTYPE_Temperature);
			tempSensor.mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
			mSensorManager.registerListener(this, tempSensor.mSensor, during);
			Log.d(TAG, "mTemperatureSensor: " + tempSensor.mSensor);
			return tempSensor;
		}

		return null;
	}

	public void unregisterSensorEvent(int nType) {
		Log.d(TAG, "on UnRegisterSensorEvent type: " + nType);

		if (ESENSORTYPE_Gravity == nType)
			mSensorManager.unregisterListener(this, mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		else if (ESENSORTYPE_Orientation == nType)
			mSensorManager.unregisterListener(this, mSensorManager
					.getDefaultSensor(Sensor.TYPE_ORIENTATION));
		else if (ESENSORTYPE_Temperature == nType)
			mSensorManager.unregisterListener(this, mSensorManager
					.getDefaultSensor(Sensor.TYPE_TEMPERATURE));
	}

	public void setSensorAccuracy(int nType, int xValue, int yValue, int zValue) {
		Log.d(TAG, "on setSensorParm nType=" +nType +";xValue=" +xValue+";yValue="+yValue+";zValue="+zValue);
		SensorHandle tempSensorHandle;
		if (nType == ESENSORTYPE_Gravity)
			tempSensorHandle=acceSensor;
		else if (nType == ESENSORTYPE_Orientation)
			tempSensorHandle=orieSensor;
		else if (nType == ESENSORTYPE_Temperature)
			tempSensorHandle=tempSensor;
		else
			return;
		tempSensorHandle.accuracyXValue=xValue;
		tempSensorHandle.accuracyYValue=yValue;
		tempSensorHandle.accuracyZValue=zValue;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		
		switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				if(acceSensor.oldXValue == 0.0 && acceSensor.oldYValue == 0.0 && acceSensor.oldZValue ==0.0)
				{
					acceSensor.oldXValue = event.values[0];
					acceSensor.oldYValue = event.values[1];
					acceSensor.oldZValue = event.values[2];
					Log.d(TAG, " TYPE_ACCELEROMETER acceSensor.oldXValue =" +acceSensor.oldXValue+
							";acceSensor.oldYValue=" +acceSensor.oldYValue +";acceSensor.oldZValue=" +acceSensor.oldZValue );
					nativesensorresult(acceSensor.getType(), acceSensor.oldXValue, acceSensor.oldYValue, acceSensor.oldZValue);
				}
				else if(event.values[0]-acceSensor.oldXValue > acceSensor.accuracyXValue ||
							event.values[1]-acceSensor.oldYValue > acceSensor.accuracyYValue ||
							event.values[2]-acceSensor.oldZValue > acceSensor.accuracyZValue ||
							acceSensor.oldXValue - event.values[0] > acceSensor.accuracyXValue ||
							acceSensor.oldYValue - event.values[1] > acceSensor.accuracyYValue ||
							acceSensor.oldZValue - event.values[2] > acceSensor.accuracyZValue)
				{
					acceSensor.oldXValue = event.values[0];
					acceSensor.oldYValue = event.values[1];
					acceSensor.oldZValue = event.values[2];
					Log.d(TAG, " TYPE_ACCELEROMETER acceSensor.oldXValue =" +acceSensor.oldXValue+
							";acceSensor.oldYValue=" +acceSensor.oldYValue +";acceSensor.oldZValue=" +acceSensor.oldZValue );
					nativesensorresult(acceSensor.getType(), acceSensor.oldXValue, acceSensor.oldYValue, acceSensor.oldZValue);
				}
				break;
			case Sensor.TYPE_ORIENTATION:
				if(orieSensor.oldXValue == 0.0 && orieSensor.oldYValue == 0.0 && orieSensor.oldZValue ==0.0)
				{
					orieSensor.oldXValue = event.values[0];
					orieSensor.oldYValue = event.values[1];
					orieSensor.oldZValue = event.values[2];
					Log.d(TAG, " TYPE_ORIENTATION acceSensor.oldXValue =" +orieSensor.oldXValue+
							";acceSensor.oldYValue=" +orieSensor.oldYValue +";orieSensor.oldZValue=" +orieSensor.oldZValue );
					nativesensorresult(orieSensor.getType(), orieSensor.oldXValue, orieSensor.oldYValue, orieSensor.oldZValue);
				}
				else if(event.values[0]-orieSensor.oldXValue > orieSensor.accuracyXValue ||
							event.values[1]-orieSensor.oldYValue > orieSensor.accuracyYValue ||
							event.values[2]-orieSensor.oldZValue > orieSensor.accuracyZValue ||
							orieSensor.oldXValue - event.values[0] > orieSensor.accuracyXValue ||
							orieSensor.oldYValue - event.values[1] > orieSensor.accuracyYValue ||
							orieSensor.oldZValue - event.values[2] > orieSensor.accuracyZValue)
				{
					orieSensor.oldXValue = event.values[0];
					orieSensor.oldYValue = event.values[1];
					orieSensor.oldZValue = event.values[2];
					Log.d(TAG, " TYPE_ORIENTATION acceSensor.oldXValue =" +orieSensor.oldXValue+
							";acceSensor.oldYValue=" +orieSensor.oldYValue +";acceSensor.oldZValue=" +orieSensor.oldZValue );
					nativesensorresult(orieSensor.getType(), orieSensor.oldXValue, orieSensor.oldYValue, orieSensor.oldZValue);
				}
				break;
			case Sensor.TYPE_TEMPERATURE:
				if(tempSensor.oldXValue == 0.0 && tempSensor.oldYValue == 0.0 && tempSensor.oldZValue ==0.0)
				{
					tempSensor.oldXValue = event.values[0];
					tempSensor.oldYValue = event.values[1];
					tempSensor.oldZValue = event.values[2];
					Log.d(TAG, " TYPE_TEMPERATURE tempSensor.oldXValue =" +tempSensor.oldXValue+
							";tempSensor.oldYValue=" +tempSensor.oldYValue +";tempSensor.oldZValue=" +tempSensor.oldZValue );
					nativesensorresult(tempSensor.getType(), tempSensor.oldXValue, tempSensor.oldYValue, tempSensor.oldZValue);
				}
				else if(event.values[0]-tempSensor.oldXValue > tempSensor.accuracyXValue ||
							event.values[1]-tempSensor.oldYValue > tempSensor.accuracyYValue ||
							event.values[2]-tempSensor.oldZValue > tempSensor.accuracyZValue ||
							tempSensor.oldXValue - event.values[0] > tempSensor.accuracyXValue ||
							tempSensor.oldYValue - event.values[1] > tempSensor.accuracyYValue ||
							tempSensor.oldZValue - event.values[2] > tempSensor.accuracyZValue)
				{
					tempSensor.oldXValue = event.values[0];
					tempSensor.oldYValue = event.values[1];
					Log.d(TAG, " TYPE_TEMPERATURE tempSensor.oldXValue =" +tempSensor.oldXValue+
							";tempSensor.oldYValue=" +tempSensor.oldYValue +";tempSensor.oldZValue=" +tempSensor.oldZValue );
					nativesensorresult(tempSensor.getType(), tempSensor.oldXValue, tempSensor.oldYValue, tempSensor.oldZValue);
				}
				break;
			default:
				break;
		}
	}
	
	public native void nativesensorresult(int nType, double d, double e, double f);

}
