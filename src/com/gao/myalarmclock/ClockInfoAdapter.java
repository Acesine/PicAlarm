package com.gao.myalarmclock;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
		//TextView dateView = (TextView)clockinfoView.findViewById(R.id.rowdate);
		
		timeView.setText(time);
		timeView.setTextColor(0xFF003366);
		timeView.setTextSize(20);
		timeView.setTypeface(Typeface.DEFAULT_BOLD);
		timeView.setGravity(Gravity.CENTER);
		
		if(item.getImageUriString() != null)
		{
			ImageView imview = (ImageView)clockinfoView.findViewById(R.id.item_imageview);
			Bitmap bm;
			try {
				bm = MediaStore.Images.Media.getBitmap(clockinfoView.getContext().getContentResolver(), item.getImage());
				imview.setImageBitmap(bm);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		CheckBox cb = (CheckBox) clockinfoView.findViewById(R.id.item_checkbox);
		cb.setChecked(item.getIsChecked());
		

		return clockinfoView;
	}
	
}
