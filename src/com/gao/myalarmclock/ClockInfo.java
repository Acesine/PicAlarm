package com.gao.myalarmclock;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class ClockInfo implements Serializable{

	private static final long serialVersionUID = 123456;
	String time;
	String bitmapUri=null;
	String pin;
	
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
	
	public Uri getImage()
	{
		return Uri.parse(bitmapUri);
	}
	
	public ClockInfo(String time) {
		this.time = time;
	}

	public ClockInfo(String time, String bitmapUri, String pin) {
		this.time = time;
		this.bitmapUri = bitmapUri;
		this.pin = pin;
	}

	public String getString() {
		// TODO Auto-generated method stub
		return time;
	}
	
}
