package com.gao.myalarmclock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AwakenActivity extends Activity{
	private static final int CAMERA_ACTIVITY_CODE = 100;
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

	private void callCameraActivity()
	{
		//Use internal camera activity
		Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraintent,CAMERA_ACTIVITY_CODE);
//		startActivity(cameraintent);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
		startService(music);
		
		if(mIsBound)
			Toast.makeText(this, "Alarmmmmmm", Toast.LENGTH_SHORT).show();
		
		scoreText = (TextView)findViewById(R.id.matchScore);		
		originalUri = Uri.parse(getIntent().getStringExtra("PhotoUri"));
		imgView = (ImageView)findViewById(R.id.photoTaken);
		oriImgView = (ImageView)findViewById(R.id.originalPhoto);
		try {
			oriPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), originalUri);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		oriImgView.setImageBitmap(oriPhoto);
		
		callCameraActivity();		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAMERA_ACTIVITY_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Image captured and saved to fileUri specified in the Intent
	        	Uri fileUri = data.getData();
	  
	        	try {
					mPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	imgView.setImageBitmap(mPhoto);
	        	
	        	int diff = ImageCompare.isSimilar(mPhoto, oriPhoto);
	        	String scoreStr = "Match Score: "+diff+" (The smaller, the better)";
	        	Toast.makeText(this,scoreStr, Toast.LENGTH_SHORT).show();
	        	scoreText.setText(scoreStr);
	        	if(diff>15)
	        	{
	        		Toast.makeText(this, "Not a similar pic!!Take another one or input pin", Toast.LENGTH_SHORT).show(); //if not similar
	        	}
	        	else
	        	{
	        		mServ.stopMusic();
	        		doUnbindService();
	        	}
	        	
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        	
	        } else {
	            // Image capture failed, advise user
	        }
	    }
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(mIsBound)
			mServ.stopMusic();
		super.onDestroy();
	}

	// For music service
	private boolean mIsBound = false;
	private BackgroundMusicService mServ;
	private ServiceConnection Scon =new ServiceConnection(){
		public void onServiceConnected(ComponentName name, IBinder binder) {
			mServ = ((BackgroundMusicService.ServiceBinder) binder).getService();
		}

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
