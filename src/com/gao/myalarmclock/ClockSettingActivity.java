package com.gao.myalarmclock;

import java.io.File;
import java.io.FileOutputStream;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ClockSettingActivity extends Activity{
	private Button Okbtn = null;
	private Button Cancelbtn = null;
	private Button SelectPic = null;
	public TimePicker timepicker = null;
	private Intent bitmapIntent = null;
	private ImageView imview = null;
	private Bitmap oriPhoto = null;
	private EditText editText = null;
	private String myPIN = null;
	private CheckBox cb = null;
	private String imageUri = null;
	private static final int CAMERA_ACTIVITY_CODE = 1;
	
	private void callCameraActivity()
	{
		//Use internal camera activity
		Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraintent,CAMERA_ACTIVITY_CODE);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		myPIN = getIntent().getStringExtra("PIN");
		editText = (EditText)findViewById(R.id.setting_pinedit);
		if(myPIN!=null)
			editText.setText(myPIN);
		String time = getIntent().getStringExtra("Time");
		if(time!=null)
		{
			String[] timestr = time.split(" : ");
			Intent intenttoFire = new Intent(MainActivity.ACTION_ALARM);
			int uniqueCode = Integer.parseInt(timestr[0])*60+Integer.parseInt(timestr[1]);
			PendingIntent pi = PendingIntent.getBroadcast(this, uniqueCode, intenttoFire, PendingIntent.FLAG_UPDATE_CURRENT);
			((AlarmManager)getSystemService(Context.ALARM_SERVICE)).cancel(pi);
			pi.cancel();
		}
		
		imageUri = getIntent().getStringExtra("imageUri");
		imview = (ImageView)findViewById(R.id.selectaPicView);
		if(imageUri!=null)
		{
			imview.setImageURI(Uri.parse(imageUri));
		}
		timepicker = (TimePicker)findViewById(R.id.timepicker);
		timepicker.setIs24HourView(true);
		Okbtn = (Button)findViewById(R.id.Ok);
		OnClickListener onOK = new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(imageUri==null)
				{
					Toast.makeText(v.getContext(), "Take an picture first!", Toast.LENGTH_SHORT).show();
					return;
				}
				else
				{
					myPIN = editText.getText().toString();
					
					bitmapIntent = new Intent();
					bitmapIntent.putExtra("imageUri", imageUri);
					bitmapIntent.putExtra("myPIN", myPIN);
					bitmapIntent.putExtra("hour",timepicker.getCurrentHour());
					bitmapIntent.putExtra("minute", timepicker.getCurrentMinute());
					bitmapIntent.putExtra("isChecked", cb.isChecked());
					setResult(RESULT_OK,bitmapIntent);
					finish();
				}
			}
			
		};
		Okbtn.setOnClickListener(onOK);
		Cancelbtn = (Button)findViewById(R.id.Cancel);
		Cancelbtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setResult(RESULT_CANCELED);
				finish();
			}
			
		});
		SelectPic = (Button)findViewById(R.id.selectaPic);
		SelectPic.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				callCameraActivity();
			}
			
		});
		
		cb = (CheckBox)findViewById(R.id.OnOFF);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked)
				{
					cb.setText("ON");
				}
				else
				{
					cb.setText("OFF");
				}
				
			}
			
		});
		
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_ACTIVITY_CODE && resultCode == Activity.RESULT_OK)
		{
			oriPhoto = (Bitmap)data.getExtras().get("data");
			try {
				Integer pos = getIntent().getIntExtra("Pos", 0);
				String filename = this.getFilesDir().getPath()+"/";
				filename = filename + pos.toString()+".png";
				FileOutputStream out = new FileOutputStream(filename);
				oriPhoto.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.close();
				
				File file = new File(filename);
				imageUri = Uri.fromFile(file).toString();
				imview.setImageBitmap(oriPhoto);
			} catch (Exception e) {
				Toast.makeText(this, "Can't save image!", Toast.LENGTH_LONG).show();
			}
		}
	}

}
