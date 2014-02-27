package com.gao.myalarmclock;

import com.gao.myalarmclock.R;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class ShakeActivity extends Activity implements SensorEventListener{

	private SensorManager sm = null;
	private Sensor accel = null;
	private TextView []canvas;
	private final int RedColor = 0xFFBD2031;
	private final int WhiteColor = 0xFFFFFFFF;
	private final int ShakeTime = 500;
	
	private int shakeCount;
	private long lastUpdate;
	private long lastShake;
	private long lastPulse;
	
	private int countDown;
	
	private float last_x;
	private float last_y;
	private float last_z;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shake);
		sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		canvas = new TextView[5];
		canvas[0] = (TextView)findViewById(R.id.text1);
		canvas[1] = (TextView)findViewById(R.id.text2);
		canvas[2] = (TextView)findViewById(R.id.text3);
		canvas[3] = (TextView)findViewById(R.id.text4);
		canvas[4] = (TextView)findViewById(R.id.text5);
		
		for(int i=0;i<5;i++)
        {
      	  canvas[i].setBackgroundColor(RedColor);
        }
		
		countDown = -1;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		sm.unregisterListener(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		sm.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		long now = System.currentTimeMillis();
	    float x = event.values[0];
	    float y = event.values[1];
	    float z = event.values[2];
	    
	    if ((now - lastPulse) > 1000) {
	        shakeCount = 0;
	    }
	    
	    if ((now - lastUpdate) > 100) {
	      long diff = now - lastUpdate;
	      float speed = Math.abs(x+y+z-last_x-last_y-last_z) / diff * 10000;
	      if (speed > 800) {
	        if ((++shakeCount >= 3) && (now - lastShake > ShakeTime)) {
	          lastShake = now;
	          shakeCount = 0;
	          countDown++;
	        }
	        for(int i=0;i<countDown && i<5;i++)
	        {
	        	canvas[i].setBackgroundColor(WhiteColor);
	        }
	        if(countDown==5)
	        {
	        	Intent intent = new Intent();
	        	setResult(RESULT_OK, intent);
	        	finish();
	        }
	        lastPulse = now;
	      }
	      lastUpdate = now;
	      last_x = x;
	      last_y = y;
	      last_z = z;
	    }
	    
	}
	
}
