package com.burningaltar.robotsquirrel;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class JustLandmarks extends Visual{
	public static final String ID = "JUST LANDMARKS";
	
	public Bitmap[] landmarkBmps;
	public Bitmap landmarkBmp;
	public double landmarkHeightScreenPct=.75;
	public static final double LANDMARK_WH_RATIO=750.0/442.0;
	
	public int landmarkWidth;
	public int landmarkHeight;
	
	int currentLandmark;
	public boolean isOnscreen = false;
	
	public static final int NUM_LANDMARKS=9;
	
	public static final int LANDMARK_BMP_IDS[] = {
			R.drawable.landmark_space,
			R.drawable.landmark_arch,
			R.drawable.landmark_liberty,
			R.drawable.landmark_bigben,
			R.drawable.landmark_eiffel,
			R.drawable.landmark_pisa,
			R.drawable.landmark_pyramid,
			R.drawable.landmark_taj,
			R.drawable.landmark_godzilla
	};
	
	public static final String LANDMARK_NAMES[] = {
			"THE SPACE NEEDLE",
			"THE ST LOUIS ARCH",
			"THE STATUE OF LIBERTY",
			"BIG BEN",
			"THE EIFFEL TOWER",
			"THE LEANING TOWER",
			"THE PYRAMIDS",
			"THE TAJ MAHAL",
			"GOZDILLA"
	};
	
	public static final int distances[] = {
		 500,
		 1500,
		 2500,
		 3500,
		 4500,
		 55000,
		 65000,
		 75000,
		 85000
	};
	public int landmarkIdx;
	public Rect landmarkRect;
	
	public Rect [] hillRects = new Rect[MAX_HILLS];
	public double [] hillX = new double[MAX_HILLS];
	public int hillIdx;
	public static final int MAX_HILLS = 10;
	
	public final static double BMP_WH_RATIO = 2.0;
	public final static double BMP_HEIGHT_PCT = .75;
	
	public static final double distSlowdownFactor = .04;
	double relativeDeltaDist;
	
	public JustLandmarks(){
		
		landmarkHeight = (int) (intScreenHeight * landmarkHeightScreenPct);
		landmarkWidth = (int) (landmarkHeight * LANDMARK_WH_RATIO);
		Log.v(ID,"Landmark width is "+landmarkWidth +" and height is "+ landmarkHeight);
		
		landmarkRect = new Rect(intScreenWidth,intScreenHeight - landmarkHeight,intScreenWidth + landmarkWidth,intScreenHeight);

		landmarkBmps = new Bitmap[NUM_LANDMARKS];
//		if(!saveMem){
			for(int i = 0; i < NUM_LANDMARKS; i++)
				landmarkBmps[i] = initBitmap(LANDMARK_BMP_IDS[i], landmarkWidth, landmarkHeight);
//		}
		
		reset();
	}
	
	public void reset(){
		landmarkRect = new Rect(intScreenWidth,intScreenHeight - landmarkHeight,intScreenWidth + landmarkWidth,intScreenHeight);
		landmarkIdx = 0;
		currentLandmark=0;
//		if(saveMem)
//			landmarkBmp = initBitmap(LANDMARK_BMP_IDS[0],landmarkWidth,landmarkHeight);
		
	}

	
	public void updateGapless(){
		relativeDeltaDist = deltaDist*distSlowdownFactor*pxPerRev;
		if(!isOnscreen && dist >= distances[landmarkIdx]){
			isOnscreen = true;
			landmarkRect.left = intScreenWidth;
			landmarkRect.right = intScreenWidth + landmarkWidth;
		}
		if(isOnscreen){
			landmarkRect.left -= relativeDeltaDist;
			landmarkRect.right -= relativeDeltaDist;		
		
			if(landmarkRect.right < 0 && landmarkIdx < (LANDMARK_BMP_IDS.length - 1)){
				isOnscreen = false;
				landmarkIdx++;
//				if(saveMem)
//					landmarkBmp = initBitmap(LANDMARK_BMP_IDS[landmarkIdx],landmarkWidth,landmarkHeight);
			}	
		}
	}
	
	public void addLandmark(){
		isOnscreen = true;
		landmarkRect.left = intScreenWidth;
		landmarkRect.right = intScreenWidth + landmarkWidth;
	}
	
	public void draw(Canvas c){
		if(isOnscreen){
//			if(saveMem)
//				c.drawBitmap(landmarkBmp, null, landmarkRect,paint);
//			else
				c.drawBitmap(landmarkBmps[landmarkIdx], null, landmarkRect, paint);
		}
	}
}
