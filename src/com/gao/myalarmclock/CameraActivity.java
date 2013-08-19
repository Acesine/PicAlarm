package com.gao.myalarmclock;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class CameraActivity extends Activity{

	private ImageView imageView;
	private static final int REQUEST_CODE = 1;
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		imageView = (ImageView)findViewById(R.id.ImgView_result);
	}
	
	public void onClick(View view){
		Intent intent = new Intent();
	    intent.setType("image/*");
	    intent.setAction(Intent.ACTION_GET_CONTENT);
	    intent.addCategory(Intent.CATEGORY_OPENABLE);
	    startActivityForResult(intent, REQUEST_CODE);
	}
	
	 @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    InputStream stream = null;
	    if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
	      try {
	        // We need to recycle unused bitmaps
	        if (bitmap != null) {
	          bitmap.recycle();
	        }
	        stream = getContentResolver().openInputStream(data.getData());
	        bitmap = BitmapFactory.decodeStream(stream);

	        imageView.setImageBitmap(bitmap);
	      } catch (FileNotFoundException e) {
	        e.printStackTrace();
	      }
	 }

}
