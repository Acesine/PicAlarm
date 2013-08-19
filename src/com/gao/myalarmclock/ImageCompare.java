package com.gao.myalarmclock;

import android.graphics.Bitmap;

public class ImageCompare {
	
	static public int isSimilar(Bitmap img1, Bitmap img2)
	{
		// just kidding...^^
		int []hashcode1 = new int[64];
		int []hashcode2 = new int[64];
		// processing image 1
		double avg1 = 0;
		int h1 = img1.getHeight();
		int h_step1 = h1/8;
		int w1 = img1.getWidth();
		int w_step1 = w1/8;
		for(int i=0;i<w1;i++)
		{
			for(int j=0;j<h1;j++)
			{
				avg1 += img1.getPixel(i,j);
			}
		}
		avg1 /= h1*w1;
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				if(img1.getPixel(i*w_step1, j*h_step1)<avg1)
					hashcode1[i*8+j] = 0;
				else
					hashcode1[i*8+j] = 1;
			}
		}
		
		// processing image 2
		double avg2 = 0;
		int h2 = img2.getHeight();
		int h_step2 = h2/8;
		int w2 = img2.getWidth();
		int w_step2 = w2/8;
		for(int i=0;i<w2;i++)
		{
			for(int j=0;j<h2;j++)
			{
				avg2 += img2.getPixel(i,j);
			}
		}
		avg2 /= h2*w2;
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				if(img2.getPixel(i*w_step2, j*h_step2)<avg2)
					hashcode2[i*8+j] = 0;
				else
					hashcode2[i*8+j] = 1;
			}
		}
		// compare difference
		int diff = 0;
		for(int i=0;i<64;i++)
		{
			if(hashcode1[i] != hashcode2[i])
				diff ++;
		}
		
		return diff;
	}
	
}
