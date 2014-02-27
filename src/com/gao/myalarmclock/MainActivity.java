package com.gao.myalarmclock;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private ListView alarmListView = null;
	private ArrayList<ClockInfo> list = null;
	private ClockInfoAdapter aa = null;
	private Button addAlarmButton = null;
	private Button deleteAlarmButton = null;
	private int currentPos = 0;
	private AlarmManager am = null;
	private String musicUriString = null;
	private final Context mainactivity = this;
	private static final int SHOW_SETTINGACTIVITY = 1;
	public static final String ACTION_ALARM = "com.gao.action.alarm";
	private static final String PREFS_NAME = "MyPrefsFile";
	private static final int SELECT_TONE_ACTIVITY = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toast.makeText(this, R.string.welcome_msg, Toast.LENGTH_SHORT).show();

		// Initialize alarm manager
		am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		// Initialize clock list
		list = new ArrayList<ClockInfo>();
		// Load settings from preference file
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		musicUriString = settings.getString("MusicUriString", null);
		int len = settings.getInt("ListLen", 0);
		for(int i=0;i<len;i++)
		{
			String time_tmp = settings.getString("Time"+i, "");
			String pin_tmp = settings.getString("Pin"+i, "");
			String uri_tmp = settings.getString("Uri"+i, "");
			boolean check_tmp = settings.getBoolean("Check"+i, false);
			list.add(new ClockInfo(time_tmp,uri_tmp,pin_tmp,check_tmp));
		}

		currentPos = list.size()-1;
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
				intent.putExtra("Pos", position);
				intent.putExtra("imageUri", list.get(currentPos).getImageUriString());
				intent.putExtra("Time", list.get(currentPos).getTime());
				intent.putExtra("PIN", list.get(currentPos).getPin());
				startActivityForResult(intent,SHOW_SETTINGACTIVITY);
//				startActivity(intent);
			}
		});

		alarmListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				currentPos = position;
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which){
						case DialogInterface.BUTTON_POSITIVE:
							if(list.size()>0)
							{
								//cancel pending intent:
								if(list.get(currentPos).getImage() != null)
								{
									Intent intenttoFire = new Intent(ACTION_ALARM);
									String str = list.get(currentPos).getTime();
									String[] timestr = str.split(" : ");
									int uniqueCode = Integer.parseInt(timestr[0])*60+Integer.parseInt(timestr[1]);
									PendingIntent pi = PendingIntent.getBroadcast(mainactivity, uniqueCode, intenttoFire, PendingIntent.FLAG_UPDATE_CURRENT);
									am.cancel(pi);
									pi.cancel();
									new File(list.get(currentPos).getImage().getPath()).delete(); // delete image file
								}
								//delete item in list
								list.remove(currentPos);
								aa.notifyDataSetChanged();
								Toast.makeText(mainactivity, "Alarm "+currentPos+" has been deleted!", Toast.LENGTH_LONG).show();
							}
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							//No button clicked
							break;
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
				builder.setMessage("Delete this alarm?").setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();
				return true;
			}

		});

		addAlarmButton = (Button)findViewById(R.id.button_add);
		addAlarmButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View context) {
				if(musicUriString == null)
				{
					Toast.makeText(context.getContext(), "Please select a alarm tone first!!(Setting)", Toast.LENGTH_SHORT).show();
					return;
				}
				ClockInfo newclock = new ClockInfo("");
				list.add(newclock);
				aa.notifyDataSetChanged();
				int position = list.size()-1;
				currentPos = position;
				Intent intent = new Intent(MainActivity.this,ClockSettingActivity.class);
				intent.putExtra("Pos", position);
				startActivityForResult(intent,SHOW_SETTINGACTIVITY);
			}

		});
		deleteAlarmButton = (Button)findViewById(R.id.button_delete);
		deleteAlarmButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				if(list.size()>0)
				{
					//cancel pending intent:
					currentPos = list.size()-1;
					if(list.get(currentPos).getImage() != null)
					{
						Intent intenttoFire = new Intent(ACTION_ALARM);
						String str = list.get(currentPos).getTime();
						String[] timestr = str.split(" : ");
						int uniqueCode = Integer.parseInt(timestr[0])*60+Integer.parseInt(timestr[1]);
						PendingIntent pi = PendingIntent.getBroadcast(mainactivity, uniqueCode, intenttoFire, PendingIntent.FLAG_UPDATE_CURRENT);
						am.cancel(pi);
						pi.cancel();
						new File(list.get(currentPos).getImage().getPath()).delete(); // delete image file
					}
					//delete item in list
					list.remove(currentPos);
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
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
		case (SHOW_SETTINGACTIVITY):
			if(resultCode==RESULT_OK)
			{
				String myPIN = data.getStringExtra("myPIN");
				int hour = data.getIntExtra("hour", 0);
				int min = data.getIntExtra("minute", 0);
				boolean isChecked = data.getBooleanExtra("isChecked",true);

				String str = hour + " : " + min;
				Uri imageUri;
				String imageUriString = data.getStringExtra("imageUri");
				imageUri = Uri.parse(imageUriString);

				list.get(currentPos).setTime(str);
				list.get(currentPos).setImage(imageUri);
				list.get(currentPos).setPin(myPIN);
				list.get(currentPos).setIsChecked(isChecked);
				aa.notifyDataSetChanged();

				if(isChecked)
				{
					Intent intenttoFire = new Intent(ACTION_ALARM);
					intenttoFire.putExtra("PhotoUri", list.get(currentPos).getImage().toString()); // put original photo in broadcast msg
					intenttoFire.putExtra("myPIN", myPIN);
					intenttoFire.putExtra("MusicUri", musicUriString);
					PendingIntent pi = PendingIntent.getBroadcast(this, hour*60+min, intenttoFire, PendingIntent.FLAG_UPDATE_CURRENT);
					Calendar c = Calendar.getInstance();
					int currentH = c.get(Calendar.HOUR_OF_DAY);
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
				}
				else
				{
					Intent intenttoFire = new Intent(ACTION_ALARM);
					PendingIntent pi = PendingIntent.getBroadcast(this, hour*60+min, intenttoFire, PendingIntent.FLAG_UPDATE_CURRENT);
					am.cancel(pi);
					pi.cancel();
				}
			}
			else
			{
				if(currentPos>=0 && list.get(currentPos).getImageUriString()=="")
				{
					//delete item in list
					list.remove(currentPos);
					aa.notifyDataSetChanged();
				}
			}
			break;
		case (SELECT_TONE_ACTIVITY):
			if(resultCode==RESULT_OK)
			{
				Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				if (uri != null)
				{
					this.musicUriString = uri.toString();
				}
				else
				{
					this.musicUriString = null;
				}
			}
			break;
		}
	}

	// Save App states
	@Override
	protected void onStop() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("MusicUriString", musicUriString);
		editor.putInt("ListLen", list.size());
		for(int i=0;i<list.size();i++)
		{
			editor.putString("Time"+i, list.get(i).getTime());
			editor.putString("Pin"+i, list.get(i).getPin());
			editor.putString("Uri"+i, list.get(i).getImageUriString());
			editor.putBoolean("Check"+i, list.get(i).getIsChecked());
		}
		// Commit the edits!
		editor.commit();
		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
        case R.id.setalarmtone:
        	Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        	intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
        	intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        	intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        	this.startActivityForResult(intent, SELECT_TONE_ACTIVITY);
            return true;
        default:
            return super.onOptionsItemSelected(item);
		}
	}
}
