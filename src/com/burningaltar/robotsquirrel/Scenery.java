package com.burningaltar.robotsquirrel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class Scenery extends Visual{
	public static final String ID = "SCENERY";
	public Bitmap bmpHill;
	public Rect [] hillRects = new Rect[MAX_HILLS];
	public double [] hillX = new double[MAX_HILLS];
	public int hillIdx;
	public static final int MAX_HILLS = 10;
	
	public final static double BMP_WH_RATIO = 2.0;
	public final static double BMP_HEIGHT_PCT = .75;
	
	public Bitmap bmpDirt;
	public int dirtX;
	public final static double BMP_DIRT_WH_RATIO = 989/80;
	int dirtWidth;
		
	public Bitmap[] hills;
	public int currentHillIdx;
	
	//in revs
	public double minHillWidthRevs = 3;
	public double maxHillWidthRevs = 5;
	public double minHillHeightScreenPct = .2;
	public double maxHillHeightScreenPct = .6;
	
	public int minHillWidthPx;
	public int maxHillWidthPx;
	public int minHillHeightPx;
	public int maxHillHeightPx;
	
	public int hillHeightDiffPx;
	
	public double distSinceLastAdd;
	
	public double avgHillWidth = (maxHillWidthRevs+minHillWidthRevs)/2.0;
	public double hillWidthsPerAdd = .8;
	public double distSlowdownFactor = .2;
	
	
	public double bgDistSlowdownFactor = distSlowdownFactor/2;
	Rect bgHillRect;
	
	public int screenBufferWidth;
	public int rightmostHillIdx;
	public int overlap;
	
	
	public Scenery(){		
		minHillWidthPx = (int) (pxPerRev * minHillWidthRevs);
		maxHillWidthPx = (int) (pxPerRev * maxHillWidthRevs);
		minHillHeightPx = (int) (intScreenHeight * minHillHeightScreenPct);
		maxHillHeightPx = (int) (intScreenHeight * maxHillHeightScreenPct);
		
		hillHeightDiffPx = maxHillHeightPx - minHillHeightPx;
		
		dirtWidth = (int) (SCREEN_BOTTOM_PX * BMP_DIRT_WH_RATIO);
		bmpDirt = initBitmapOpaque(R.drawable.dirt,dirtWidth,SCREEN_BOTTOM_PX);
		dirtX = 0;
		
		if(!saveMem){
			hills = new Bitmap[Items.NUT_COLORS.length+1];
			//hills[0] = initBitmap(R.drawable.hill_green,maxHillWidthPx/2,maxHillHeightPx/2);
			
			hills[0] = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.hill_green);
			for(int i=0;i<hills.length-1;i++)
				hills[i+1] = initBitmap(Items.NUT_COLORS[i].hillBmpId,maxHillWidthPx/2,maxHillHeightPx/2);
		}
		
		
//		mtsHeight = (int)(.5 * intScreenHeight);
//		mtsWidth = (int) ( mtsHeight* BMP_MTS_WH_RATIO);
//		mtsWidth = intScreenWidth;
//		mtsHeight = (int)(mtsWidth/BMP_MTS_WH_RATIO);
//		bmpMts = initBitmapOpaque(R.drawable.skyfade,mtsWidth,mtsHeight);
//		mtsX = 0;
		
		for(int i = 0; i<MAX_HILLS; i++){
			hillRects[i] = new Rect(-1,-1,-1,-1);
			hillX[i] = 0;
		}
		bgHillRect = new Rect(-1,-1,-1,-1);
		
		screenBufferWidth = (int)(1.3*intScreenWidth);
		rightmostHillIdx = 0;
		overlap = (int) (minHillWidthPx/3);
		hillRects[rightmostHillIdx] = new Rect(0,(int)(intScreenHeight - maxHillHeightPx),(int)(0+maxHillWidthPx),intScreenHeight);
		hillX[rightmostHillIdx] = hillRects[rightmostHillIdx].left;
		
		reset();
	}
	
	public void reset(){
		hillIdx=0;
		distSinceLastAdd = 0;
		currentHillIdx=0;
		if(saveMem)
			bmpHill = initBitmap(R.drawable.hill_green,maxHillWidthPx,maxHillHeightPx);
		updateGapless();
	}
	
	public void update(double deltaDist){
		if(deltaDist==0)
			return;
				
		deltaDist *= distSlowdownFactor;
		distSinceLastAdd += deltaDist;
		for(int i = 0; i<MAX_HILLS; i++){
			if(hillRects[i].right>=0){
				hillX[i] -= deltaDist*pxPerRev;
				
				hillRects[i].right = (int) hillX[i] + hillRects[i].width();
				hillRects[i].left = (int) hillX[i];
				//if(logs)Log.v(ID, "hills left is "+ hillRects[i].left);
			}
		}
		
		if(distSinceLastAdd >= minHillWidthRevs * hillWidthsPerAdd){
			distSinceLastAdd -=minHillWidthRevs * hillWidthsPerAdd;
			addHill();
		}
	}
	
	public void updateGapless(){	

		int deltaDistPx = (int) (deltaDist*pxPerRev);
		dirtX -= deltaDistPx;
		dirtX %= dirtWidth;
		
		deltaDistPx*=distSlowdownFactor;
		
		for(int i = 0; i<MAX_HILLS; i++){
			if(hillRects[i].right>=0){								
				hillRects[i].right -= deltaDistPx;
				hillRects[i].left -= deltaDistPx;

			}
		}
		
		while(hillRects[rightmostHillIdx].right < screenBufferWidth){
			int oldIdx = rightmostHillIdx;
			rightmostHillIdx++;
			if(rightmostHillIdx == hillRects.length)
				rightmostHillIdx = 0;
			
			//int newOverlap = (int) ((.2 + rand.nextDouble()) * overlap);
			hillRects[rightmostHillIdx].left= hillRects[oldIdx].right-overlap;
			hillRects[rightmostHillIdx].right = hillRects[rightmostHillIdx].left + Math.max(minHillWidthPx,rand.nextInt(maxHillWidthPx));
			hillRects[rightmostHillIdx].bottom = intScreenHeight;
			hillRects[rightmostHillIdx].top = intScreenHeight - (minHillHeightPx + rand.nextInt(hillHeightDiffPx));
		}
		
		
		//bg hill
		bgHillRect.left-= deltaDistPx/2;
		bgHillRect.right-=deltaDistPx/2;
		if(bgHillRect.right < 0){
			bgHillRect.set(intScreenWidth, intScreenHeight - maxHillHeightPx/2, intScreenWidth+maxHillWidthPx, intScreenHeight);
		}
		
	}
	
	public void draw(Canvas c){
		
		//bg hill
		if(saveMem)
			c.drawBitmap(bmpHill, null, bgHillRect, paint);
		else
			c.drawBitmap(hills[currentHillIdx], null, bgHillRect, paint);
//		c.drawBitmap(bmpMts, 0, (float)(intScreenHeight-mtsHeight), paint);
		for(int i = 0; i<MAX_HILLS; i++){
			if(hillRects[i].right>=0 && hillRects[i].left <intScreenWidth){
				if(saveMem)
					c.drawBitmap(bmpHill, null, hillRects[i], paint);
				else
					c.drawBitmap(hills[currentHillIdx], null, hillRects[i], paint);
			}
		}
		

		
		for(int i=dirtX; i< intScreenWidth; i+=dirtWidth)
			c.drawBitmap(bmpDirt, i, intScreenHeight, paint);
	}
	
	public void setHillColor(int idx){
		//bmpHill = initBitmap(gameColor.hillBmpId,maxHillWidthPx,maxHillHeightPx);
		
		if(saveMem)
			bmpHill = initBitmap(Items.NUT_COLORS[idx].hillBmpId,maxHillWidthPx,maxHillHeightPx);
		currentHillIdx = idx + 1;

	}
	
	public void addHill(){
//		if(rand.nextBoolean())
//			return;
		int idx=0;
		for(idx=0; idx < hillRects.length && hillRects[idx].right>=0;idx++);
		if(idx == hillRects.length)
			return;
		
		//hillRects[idx].left= intScreenWidth+rand.nextInt(intScreenWidth/4);
		hillRects[idx].left= intScreenWidth;
		hillX[idx] = hillRects[idx].left;
		hillRects[idx].right = hillRects[idx].left + Math.max(minHillWidthPx,rand.nextInt(maxHillWidthPx));
		hillRects[idx].bottom = intScreenHeight;
		hillRects[idx].top = intScreenHeight - (minHillHeightPx + rand.nextInt(hillHeightDiffPx));

	}
}
