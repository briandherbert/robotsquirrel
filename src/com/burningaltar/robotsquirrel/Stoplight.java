package com.burningaltar.robotsquirrel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Stoplight extends Visual{
	public static final String ID = "Stoplight";
	
	public static final int STATE_GREEN_FAST = 0;
	public static final int STATE_GREEN = 1;
	public static final int STATE_YELLOW3 = 2;
	public static final int STATE_YELLOW2 = 3;
	public static final int STATE_YELLOW1 = 4;
	public static final int STATE_RED = 5;
	
	public static final int NUM_LIGHTS = 6;
	public Bitmap[] bmps = new Bitmap[NUM_LIGHTS];
	public int bmpIds[] = {
			R.drawable.greengoodlight,
			R.drawable.greenlight,
			R.drawable.yellowlight1,
			R.drawable.yellowlight2,
			R.drawable.yellowlight3,
			R.drawable.redlight,
	};
	
	Bitmap whitelight;
	
	public Rect destRect;
	
	public int state;
	
	boolean isStarted=false;
	
	public static final int GREEN_FAST_MS = 400;
	public static final int YELLOW_MAX_MS=750;
	public static final int YELLOW_DECREMENT_MS = 15;
	public int yellowMs;
	
	public static final int LIGHTS_PER_GAME=10;
	public static final int MS_PER_LIGHT_OPPORTUNITY=3100;
	public static final int MS_PER_YELLOW_SPEEDUP = 10000;
	
	public long yellowSpeedupTimer;
	public long lastLightOpportunityTimer;
	public long lastLightTimer;
	public long lastStateChangeTimer;
	public static final int LIGHT_ODDS= 3;
	
	public static final int MAX_RED_MS = 3000;
	public static final int MIN_RED_MS = 700;
	public int redMs;
	public double lightSideInRevs =.5;
	
	Paint greenFastPaint=new Paint();
	Paint greenPaint=new Paint();
	Paint yellowPaint=new Paint();
	Paint redPaint=new Paint();
	
	Paint lightPaint = new Paint();
	int lightX;
	int lightY;
	public int lightSidePx;
	Bitmap bgBmp;

	
	public Stoplight(){
		//msSinceLastLight = 0;
		lightSidePx = (int) (lightSideInRevs*pxPerRev*2);
		//lightX = (int) (intScreenWidth/2+lightSidePx/2);
		lightX = (int) (intScreenWidth/2-lightSidePx/2);
		lightY = (int) Math.min(60,(intScreenHeight*.20));
		redPaint.setColor(Color.RED);
		yellowPaint.setColor(Color.YELLOW);
		greenPaint.setColor(Color.GREEN);
		greenFastPaint.setColor(Color.BLUE);
		lightPaint.setColor(Color.WHITE);
		
		for(int i=0;i<NUM_LIGHTS;i++){
			//bmps[i] = initBitmap(bmpIds[i], lightSidePx,lightSidePx);
			bmps[i] = BitmapFactory.decodeResource(mContext.getResources(),bmpIds[i]);

		}
//		whitelight = initBitmap(R.drawable.whitelight);
// bgBmp = initBitmap(R.drawable.opaquegradbgbluedarker,intScreenWidth,intScreenHeight);
		
		bgBmp = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.opaquegradbgbluedarker);
		whitelight = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.whitelight);
		
		destRect = new Rect(lightX, lightY, lightX+ lightSidePx,lightY+ lightSidePx);
		reset();		
	}
	
	public void reset(){
		isStarted = false;
		redMs = MAX_RED_MS;
		yellowMs=YELLOW_MAX_MS;
		yellowSpeedupTimer = lastLightOpportunityTimer = lastLightTimer = lastStateChangeTimer = 0;
		setState(STATE_RED);
	}
	
	
	public void setState(int newState){
		lastStateChangeTimer=0;
		state=newState;
	}
	
	public void update(){
//		if(!isStarted){
//			yellowSpeedupTimer = lastLightOpportunityTimer = lastLightTimer = lastStateChangeTimer = 0;
//			isStarted=true;
//		}
		lastStateChangeTimer += deltaTime;
		yellowSpeedupTimer += deltaTime;
		
		switch(state){
		case STATE_RED:
			if(lastStateChangeTimer>=redMs)
				setState(STATE_GREEN_FAST);
			break;
		
		case STATE_GREEN_FAST:
			if(lastStateChangeTimer>=GREEN_FAST_MS)
				setState(STATE_GREEN);
			break;
		
		case STATE_GREEN:
			lastLightOpportunityTimer += deltaTime;
			if(lastLightOpportunityTimer>=MS_PER_LIGHT_OPPORTUNITY){
				
				//create a light
				if(rand.nextInt(LIGHT_ODDS)==0){
					redMs = Math.max(MIN_RED_MS, rand.nextInt(MAX_RED_MS));
					setState(STATE_YELLOW3);
				}
				lastLightOpportunityTimer = 0;
			}
			
			if(yellowSpeedupTimer >= MS_PER_YELLOW_SPEEDUP){
				yellowMs -= YELLOW_DECREMENT_MS;
				yellowSpeedupTimer=0;
			}
			break;
		case STATE_YELLOW3:
		case STATE_YELLOW2:
		case STATE_YELLOW1:
			if(lastStateChangeTimer>=yellowMs)
				setState(state + 1);
			break;
		}
	}
	
	public void draw(Canvas c){
		
		c.drawBitmap(bgBmp, null, screenRect, paint);
//		c.drawRect(screenRect, skyPaint);

		c.drawBitmap(whitelight, null,destRect, paint);
		c.drawBitmap(bmps[state], null,destRect, paint);
	}
	
}
