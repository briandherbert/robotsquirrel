package com.burningaltar.robotsquirrel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

/**
 * Draws a layer of clouds of varying shape
 * @author brianherbert
 *
 */
public class Clouds extends Visual{
	public static final String ID = "Clouds";
	public Bitmap bmpCloud;
	public Rect [] cloudRects = new Rect[MAX_CLOUDS];
	public double [] cloudX = new double[MAX_CLOUDS];

	public static final int MAX_CLOUDS = 6;
	
	public final static double BMP_WH_RATIO = 8.0/5.0;
	public final static double BMP_HEIGHT_PCT = .75;
	
	//in revs
	public static final double minCloudWidth = .8;
	public static final  double maxCloudWidth = 1.5;
	public static final  double minCloudHeight = .3;
	public static final  double maxCloudHeight = .5;	
	public static final  double maxCloudY = 1.4;
	
	public int minCloudWidthPx;
	public int maxCloudWidthPx;
	public int minCloudHeightPx;
	public int maxCloudHeightPx;
	public int maxCloudYPx;
	
	public double distSinceLastAdd;
	public static final double addFreq = 1;
	
	public static final double avgCloudWidth = (maxCloudWidth+minCloudWidth)/2.0;

	public static final double distSlowdownFactor = .12;
	
	public Clouds(){						
		minCloudWidthPx = (int) (pxPerRev * minCloudWidth);
		maxCloudWidthPx = (int) (pxPerRev * maxCloudWidth);
		minCloudHeightPx = (int) (pxPerRev * minCloudHeight);
		maxCloudHeightPx = (int) (pxPerRev * maxCloudHeight);
		maxCloudYPx = (int) (pxPerRev * maxCloudY);
		
		//bmpCloud = initBitmap(R.drawable.cloud2,maxCloudWidthPx,maxCloudHeightPx);
		
		bmpCloud = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.cloud2);
		
		for(int i = 0; i<MAX_CLOUDS; i++){
			cloudRects[i] = new Rect(-1,-1,-1,-1);
			cloudX[i] = 0;
		}

		reset();		
	}
	
	public void reset(){
		distSinceLastAdd = 0;
		addCloud();
	}
	
	public void update(){

		double adjustedDeltaDist = deltaDist * distSlowdownFactor;
		distSinceLastAdd += adjustedDeltaDist;
		for(int i = 0; i<MAX_CLOUDS; i++){
			if(cloudRects[i].right>=0){
				cloudX[i] -= adjustedDeltaDist * pxPerRev * (1.0-((double)cloudRects[i].top/(maxCloudYPx*3)));				
				cloudRects[i].right = (int) cloudX[i] + cloudRects[i].width();
				cloudRects[i].left = (int) cloudX[i];
			}
		}
		
		if(distSinceLastAdd >= avgCloudWidth*addFreq){
			distSinceLastAdd = 0;
			if(rand.nextBoolean())
				addCloud();
		}
	}
	
	public void draw(Canvas c){		
		for(int i = 0; i<MAX_CLOUDS; i++){
			if(cloudRects[i].right>=0 && cloudRects[i].left < intScreenWidth)
				c.drawBitmap(bmpCloud, null, cloudRects[i], paint);
		}
	}
	
	public void addCloud(){
		int idx=0;
		for(idx=0; idx < cloudRects.length && cloudRects[idx].right>=0;idx++);
		if(idx == cloudRects.length)
			return;
		
		cloudRects[idx].left= intScreenWidth+rand.nextInt(intScreenWidth);
		cloudX[idx] = cloudRects[idx].left;
		cloudRects[idx].right = cloudRects[idx].left + Math.max(minCloudWidthPx,rand.nextInt(maxCloudWidthPx));		
		cloudRects[idx].top = (int) (rand.nextDouble()*maxCloudYPx);
		cloudRects[idx].bottom = cloudRects[idx].top + Math.max(minCloudHeightPx,rand.nextInt(maxCloudHeightPx));
		distSinceLastAdd = 0;
		if(logs)
			Log.v(ID,"Adding cloud at " + cloudRects[idx].left + " " +cloudRects[idx].top
					+ " " +cloudRects[idx].right+ " " +cloudRects[idx].bottom);
	}
}
