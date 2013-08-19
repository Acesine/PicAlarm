package com.gao.myalarmclock;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ClockSettingActivity extends Activity{
	private Button Okbtn;
	private Button Cancelbtn;
	private Button SelectPic;
	public TimePicker timepicker;
	private static final int SELECTPIC_CODE = 1;
	private Intent bitmapIntent;
	private ImageView imview;
	private Bitmap oriPhoto = null;
	private EditText editText;
	private String myPIN = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		editText = (EditText)findViewById(R.id.setting_pinedit);
		
		imview = (ImageView)findViewById(R.id.selectaPicView);
		timepicker = (TimePicker)findViewById(R.id.timepicker);
		timepicker.setIs24HourView(false);
		Okbtn = (Button)findViewById(R.id.Ok);
		OnClickListener onOK = new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(bitmapIntent==null)
				{
					Toast.makeText(v.getContext(), "Select an image first!", Toast.LENGTH_SHORT).show();
					return;
				}
				else
				{
					myPIN = editText.getText().toString();

					bitmapIntent.putExtra("myPIN", myPIN);
					bitmapIntent.putExtra("hour",timepicker.getCurrentHour());
					bitmapIntent.putExtra("minute", timepicker.getCurrentMinute());
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
				Intent intent = new Intent();
			    intent.setType("image/*");
			    intent.setAction(Intent.ACTION_GET_CONTENT);
			    intent.addCategory(Intent.CATEGORY_OPENABLE);
			    startActivityForResult(intent, SELECTPIC_CODE);
			}
			
		});
	}
	
	@Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == SELECTPIC_CODE && resultCode == Activity.RESULT_OK)
	    {
	    	bitmapIntent = data;
	    	
	    	try {
				oriPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	imview.setImageBitmap(oriPhoto);
	    }
	}
	
}
