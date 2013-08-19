package com.gao.myalarmclock;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private ListView alarmListView;
	private ArrayList<ClockInfo> list;
	private ClockInfoAdapter aa;
	private Button addAlarmButton;
	private Button deleteAlarmButton;
	private static final int SHOW_SETTINGACTIVITY = 1;
	private int currentPos = 0;
	AlarmManager am;
	MyBroadcastReceiver br;
	private static final String ACTION_ALARM = "com.gao.action.alarm";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toast.makeText(this, R.string.welcome_msg, Toast.LENGTH_SHORT).show();

		br = new MyBroadcastReceiver();
        am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		list = new ArrayList<ClockInfo>();
		try {
			loadFromInternalStorage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		alarmListView = (ListView)findViewById(R.id.alarm_list);
		aa = new ClockInfoAdapter(this,R.layout.clock_itemview,list);
		alarmListView.setAdapter(aa);
		alarmListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				currentPos = position;
				Intent intent = new Intent(MainActivity.this,ClockSettingActivity.class);
				startActivityForResult(intent,SHOW_SETTINGACTIVITY);
//				startActivity(intent);
				
			}		
		});
		
		addAlarmButton = (Button)findViewById(R.id.button_add);
		addAlarmButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				ClockInfo newclock = new ClockInfo("");
				list.add(newclock);
				aa.notifyDataSetChanged();
			}
			
		});
		deleteAlarmButton = (Button)findViewById(R.id.button_delete);
		deleteAlarmButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				if(list.size()>0)
				{
					//cancel pendingintent:
					Intent intenttoFire = new Intent(ACTION_ALARM);
					intenttoFire.putExtra("PhotoUri", list.get(currentPos).getImage().toString()); // put original photo in broadcast msg
					intenttoFire.putExtra("myPIN", list.get(list.size()-1).pin);
					PendingIntent pi = PendingIntent.getBroadcast(view.getContext(), 0, intenttoFire, 0);
					am.cancel(pi);
					pi.cancel();
					//delete item in list
					list.remove(list.size()-1);
					aa.notifyDataSetChanged();
					Toast.makeText(view.getContext(), "Last Alarm has been deleted!", Toast.LENGTH_LONG).show();
				}
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
		case (SHOW_SETTINGACTIVITY):
			if(resultCode==RESULT_OK)
			{
				String myPIN = data.getStringExtra("myPIN");
				int hour = data.getIntExtra("hour", 0);
				int min = data.getIntExtra("minute", 0);
				String str = hour + " : " + min;
				list.get(currentPos).setTime(str);
				list.get(currentPos).setImage(data.getData());
				list.get(currentPos).setPin(myPIN);
				aa.notifyDataSetChanged();
				/* Pending to mend...
				 */
				
				Intent intenttoFire = new Intent(ACTION_ALARM);
				intenttoFire.putExtra("PhotoUri", list.get(currentPos).getImage().toString()); // put original photo in broadcast msg
				intenttoFire.putExtra("myPIN", myPIN);
				PendingIntent pi = PendingIntent.getBroadcast(this, 0, intenttoFire, 0);
				Calendar c = Calendar.getInstance();
				int currentH = c.get(Calendar.HOUR);
				int currentM = c.get(Calendar.MINUTE);
				if(currentH>hour)
				{
					hour += 24;
				}
				if(currentH==hour)
				{
					if(currentM>=min)
						hour += 24;
				}
				int hourToGo = (hour-currentH>=0)? hour-currentH : hour-currentH+24;
				int minToGo = min-currentM;
				if(minToGo<0)
				{
					hourToGo --;
					minToGo += 60;
				}
				int milliToGo = 1000*3600*hourToGo + 1000*60*minToGo;
				long currentT = System.currentTimeMillis();
				
				currentT = currentT/60000 * 60000;
				am.setRepeating(AlarmManager.RTC_WAKEUP,currentT+milliToGo,AlarmManager.INTERVAL_DAY, pi);
				Toast.makeText(this, "Alarm set in "+hourToGo+" h "+minToGo+" m...", Toast.LENGTH_LONG).show();
				/**/
			}
			break;
		}
	}	
/*
	// Save App states
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putParcelableArrayList("ClockInfoList",list);
		outState.putParcelableArrayList("PendingIntentList", piList);
		super.onSaveInstanceState(outState);
	}
*/	
	private void saveToInternalStorage() throws IOException
	{
		FileOutputStream fos1 = openFileOutput("clock.info", Context.MODE_PRIVATE);
		ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
		oos1.writeInt(list.size());
		for(ClockInfo i:list)
			oos1.writeObject(i);
		oos1.close();
		fos1.close();

	}

	private void loadFromInternalStorage() throws IOException, ClassNotFoundException
	{
		FileInputStream fis1 = openFileInput("clock.info");
		ObjectInputStream ois1 = new ObjectInputStream(fis1);
		int num = ois1.readInt();
		for(int i=0;i<num;i++)
		{
			list.add((ClockInfo)ois1.readObject());
		}
		ois1.close();

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try {
			saveToInternalStorage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onStop();
	}
	
}
