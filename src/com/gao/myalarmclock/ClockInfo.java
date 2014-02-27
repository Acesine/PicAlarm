package com.gao.myalarmclock;

import android.net.Uri;

public class ClockInfo{
	private String time = "";
	private String bitmapUri = "";
	private String pin = "";
	private boolean isChecked=false; 
	
	public void setPin(String s)
	{
		pin = s;
	}
	
	public String getPin()
	{
		return pin;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public void setImage(Uri imgUri)
	{
		bitmapUri = imgUri.toString();
	}
	
	public String getImageUriString()
	{
		return bitmapUri;
	}
	
	public Uri getImage()
	{
		if(bitmapUri!=null)
			return Uri.parse(bitmapUri);
		else
			return null;
	}
	public void setIsChecked(boolean b)
	{
		isChecked = b;
	}
	
	public boolean getIsChecked()
	{
		return isChecked;
	}
	
	public ClockInfo(String time) {
		this.time = time;
	}

	public ClockInfo(String time, String bitmapUri, String pin, boolean isChecked) {
		this.time = time;
		this.bitmapUri = bitmapUri;
		this.pin = pin;
		this.isChecked = isChecked;
	}

	public String getString() {
		// TODO Auto-generated method stub
		return time;
	}
	
}
