package com.gao.myalarmclock;

import java.io.IOException;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AwakenActivity extends Activity{
	public static final int CAMERA_ACTIVITY_CODE = 100;
	public static final int SHAKE_ACTIVITY_CODE = 200;
	private Uri originalUri;
	private ImageView oriImgView;
	private ImageView imgView;
	private Bitmap mPhoto;
	private Bitmap oriPhoto;
	private TextView scoreText;
	private Button checkBtn;
	private Button takepicBtn;
	private EditText editTxt;
	private String Pin;
	private Button holdnshakeBtn;
	private NotificationManager notificationMgr;

	private void callCameraActivity()
	{

		//Use internal camera activity
		Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraintent,CAMERA_ACTIVITY_CODE);

		//Use customized camera activity
		//Intent cameraintent = new Intent(this,CameraActivity.class);
		//startActivityForResult(cameraintent,CAMERA_ACTIVITY_CODE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	  // set up a notification
	  NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
	                                            .setSmallIcon(R.drawable.smallclock)
	                                            .setContentTitle("Pic Alarm")
	                                            .setContentText("Alarmmmmming!");
	  notificationMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	  notificationMgr.notify(0,mBuilder.build());

	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_awaken);

	  Pin = getIntent().getStringExtra("myPIN");
		editTxt = (EditText)findViewById(R.id.pinedit);
		checkBtn = (Button)findViewById(R.id.checkpin);
		checkBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(Pin.equalsIgnoreCase(editTxt.getText().toString()))
				{
					mServ.stopMusic();
	        		doUnbindService();
	        		finish();
				}
			}

		});

		takepicBtn = (Button)findViewById(R.id.takepic_btn);
		takepicBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				callCameraActivity();
			}

		});

		doBindService();
		Intent music = new Intent(this, BackgroundMusicService.class);
		music.putExtra("MusicUri", getIntent().getStringExtra("MusicUri"));
		startService(music);

		if(mIsBound)
			Toast.makeText(this, "Alarmmmmmm", Toast.LENGTH_SHORT).show();

		scoreText = (TextView)findViewById(R.id.matchScore);
		originalUri = Uri.parse(getIntent().getStringExtra("PhotoUri"));
		oriImgView = (ImageView)findViewById(R.id.originalPhoto);
		imgView = (ImageView)findViewById(R.id.photoTaken);

		try {
			oriPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), originalUri);
			oriImgView.setImageBitmap(oriPhoto);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		holdnshakeBtn = (Button)findViewById(R.id.holdnshake);
		holdnshakeBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				Intent shakeIntent = new Intent(AwakenActivity.this,ShakeActivity.class);
				startActivityForResult(shakeIntent, SHAKE_ACTIVITY_CODE);
			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAMERA_ACTIVITY_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Image captured and saved to fileUri specified in the Intent
	        	mPhoto = (Bitmap)data.getExtras().get("data");
	        	//byte[] dd = data.getByteArrayExtra("data");
	        	//mPhoto = BitmapFactory.decodeByteArray(dd, 0, dd.length);
	        	imgView.setImageBitmap(mPhoto);

	        	int diff = ImageCompare.isSimilar(mPhoto, oriPhoto);
	        	String scoreStr = "Match Score: "+diff+" (The smaller, the better)";
	        	Toast.makeText(this,scoreStr, Toast.LENGTH_SHORT).show();
	        	scoreText.setText(scoreStr);
	        	if(diff>10)
	        	{
	        		Toast.makeText(this, "Not a similar pic!!Take another one or input pin", Toast.LENGTH_SHORT).show(); //if not similar
	        	}
	        	else
	        	{
	        		if(mIsBound)
	        		{
	        			mServ.stopMusic();
	        			doUnbindService();
	        			finish();
	        		}
	        	}

	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture

	        } else {
	            // Image capture failed, advise user
	        }
	    }
	    if(requestCode == SHAKE_ACTIVITY_CODE)
	    {
	    	if (resultCode == RESULT_OK) {
	    		if(mIsBound)
        		{
        			mServ.stopMusic();
        			doUnbindService();
        			finish();
        		}
	    	}
	    }
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(mIsBound)
			mServ.stopMusic();

		notificationMgr.cancel(0);
		super.onDestroy();
	}

	// For music service
	private boolean mIsBound = false;
	private BackgroundMusicService mServ;
	private final ServiceConnection Scon =new ServiceConnection(){
		@Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
			mServ = ((BackgroundMusicService.ServiceBinder) binder).getService();
		}

		@Override
    public void onServiceDisconnected(ComponentName name) {
			mServ = null;
		}
	};

	void doBindService(){
		bindService(new Intent(this,BackgroundMusicService.class),Scon,Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService()
	{
		if(mIsBound)
		{
			unbindService(Scon);
			mIsBound = false;
		}
	}


}
