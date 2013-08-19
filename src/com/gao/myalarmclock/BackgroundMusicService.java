package com.gao.myalarmclock;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class BackgroundMusicService extends Service implements OnErrorListener{

	private final IBinder mBinder = new ServiceBinder();
    MediaPlayer mPlayer;
    
	public class ServiceBinder extends Binder {
		BackgroundMusicService getService()
	   	 {
	   		return BackgroundMusicService.this;
	   	 }
   }
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		mPlayer = MediaPlayer.create(this, R.raw.ring);
		mPlayer.setOnErrorListener(this);

		if(mPlayer!= null)
		{
			mPlayer.setLooping(true);
			mPlayer.setVolume(0.6f,0.6f);
		}


		mPlayer.setOnErrorListener(new OnErrorListener() {

			public boolean onError(MediaPlayer mp, int what, int
					extra){

				onError(mPlayer, what, extra);
				return true;
			}
		});
	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
		if(mPlayer != null)
		{
			try{
				mPlayer.stop();
				mPlayer.release();
			}finally {
				mPlayer = null;
			}
		}
		return false;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mPlayer!=null)
		{
			try{
				mPlayer.stop();
				mPlayer.release();
			}finally{
				mPlayer = null;
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		mPlayer.start();
		return START_STICKY;
	}

	public void stopMusic()
	{
		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
	}
}
