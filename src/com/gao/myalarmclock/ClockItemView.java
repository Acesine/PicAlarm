package com.gao.myalarmclock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class ClockItemView extends TextView{
	
	private int paperColor;
	
	public ClockItemView(Context context) {
		super(context);
		init();
	}
	
	public ClockItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ClockItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	private void init()
	{
		Resources myResources = getResources();
		paperColor = myResources.getColor(R.color.notepad_paper);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawColor(paperColor);

		canvas.save();
		
		super.onDraw(canvas);
		canvas.restore();
	}
	
}
