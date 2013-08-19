package com.gao.myalarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent i = new Intent(context,AwakenActivity.class);
		i.putExtra("PhotoUri", intent.getStringExtra("PhotoUri"));
		i.putExtra("myPIN", intent.getStringExtra("myPIN"));
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);

	}
	
}
