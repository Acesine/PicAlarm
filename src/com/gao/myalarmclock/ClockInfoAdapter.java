package com.gao.myalarmclock;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ClockInfoAdapter extends ArrayAdapter<ClockInfo>{

	int resource;
	public ClockInfoAdapter(Context context, int resource,
			List<ClockInfo> items) {
		super(context, resource, items);
		this.resource = resource;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LinearLayout clockinfoView;
		ClockInfo item = getItem(position);
		String time = item.getTime();
		
		if(convertView == null)
		{
			clockinfoView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li;
			li = (LayoutInflater)getContext().getSystemService(inflater);
			li.inflate(resource, clockinfoView,true);
		}
		else
		{
			clockinfoView = (LinearLayout)convertView;
		}
		
		TextView timeView = (TextView)clockinfoView.findViewById(R.id.clockinfo);
		TextView dateView = (TextView)clockinfoView.findViewById(R.id.rowdate);
		
		timeView.setText(time);
		
		if(item.bitmapUri != null)
		{
			try {
				InputStream stream = null;
				stream = clockinfoView.getContext().getContentResolver().openInputStream(Uri.parse(item.bitmapUri));
				Bitmap bitmap = BitmapFactory.decodeStream(stream);
				ImageView imview = (ImageView)clockinfoView.findViewById(R.id.item_imageview);
				imview.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return clockinfoView;
	}
	
}
