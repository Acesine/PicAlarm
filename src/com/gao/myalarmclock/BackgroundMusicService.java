package com.gao.myalarmclock;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

public class BackgroundMusicService extends Service implements OnErrorListener{

	private final IBinder mBinder = new ServiceBinder();
    private MediaPlayer mPlayer;
    //private Vibrator vibrator;
    private CountDownTimer cdt;

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
	  //vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	  //long[] pattern = {500,1000};
	  //vibrator.vibrate(pattern, 1);

		Uri musicUri = Uri.parse(intent.getStringExtra("MusicUri"));
		mPlayer = MediaPlayer.create(this, musicUri);
		mPlayer.setOnErrorListener(this);

		if(mPlayer!= null)
		{
			mPlayer.setLooping(true);

			mPlayer.setVolume(1.0f,1.0f);
		}


		mPlayer.setOnErrorListener(new OnErrorListener() {

			@Override
      public boolean onError(MediaPlayer mp, int what, int
					extra){

				onError(mPlayer, what, extra);
				return true;
			}
		});

		cdt = new CountDownTimer(60000*2, 1000) {

		     @Override
        public void onTick(long millisUntilFinished) {

		     }

		     @Override
        public void onFinish() {
		    	 mPlayer.stop();
		    	 mPlayer.release();
		    	 //vibrator.cancel();
		     }
		  };
		cdt.start();
		mPlayer.start();
		return START_STICKY;
	}

	public void stopMusic()
	{
		cdt.cancel();
		mPlayer.stop();
		mPlayer.release();
		//vibrator.cancel();
	}
}
