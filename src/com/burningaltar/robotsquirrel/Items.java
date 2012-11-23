package com.burningaltar.robotsquirrel;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class Items extends Visual{
	public static final String ID = "ITEMS";
	
	public static final int NUT_ID = 0;
	public static final int BATTERY_ID = 1;
	public static final int BEE_ID = 2;
	public static final int STORMCLOUD_ID = 3;
	public static final int SNAIL_ID = 4;
	

	
	public static final GameColor NUT_COLORS[] = {
		GameColor.ORANGE,
		GameColor.YELLOW,
		GameColor.DARKGREEN,
		GameColor.BLUE,
		GameColor.PURPLE,
		GameColor.GRAY,
		GameColor.WHITE,
		GameColor.PINK,
		GameColor.BLACK
	};
	
	//this is the items in order and is parallel with itemLocations
	public static final int[] itemIds = {
		NUT_ID,
		NUT_ID,
		NUT_ID,
		NUT_ID,
		STORMCLOUD_ID,
		NUT_ID,
		NUT_ID,
		NUT_ID,
		NUT_ID,
		STORMCLOUD_ID,
		NUT_ID

	};
	
	public static final int[] nutDistances = {
		1000,// snail
		2000,// nut
		3000,// nut
		4000,// nut
		5000,// stormcloud
		6000,// nut
		7000,// nut
		8200,//nut
		9400
	};
	
	public static final int NUM_STORMCLOUDS = 3;
	int stormCloudDistances[] = new int[NUM_STORMCLOUDS];
	int currentStormcloud = 0;

	
	public static final int NUM_NUTS = NUT_COLORS.length;
	
	public static final Bitmap bmpBattery[] = new Bitmap[MAX_CHARGE_IDX+1];
	
	public int nutIdx;
	
	public static final double NUT_HEIGHT_REV = .26;
	public static final double NUT_WIDTH_REV = .26;
	public static final double NUT_REVS_ABOVE_GROUND = .50;
	int nutY;
	int nutHeightPx;
	int nutWidthPx;
	public Bitmap bmpNut;
	public BitmapDrawable bmpNutColor;
	public Bitmap bmpNutOutline;
	
	public static final double BATTERY_HEIGHT_REV = .34;
	public static final double BATTERY_WIDTH_REV = .16;
	public static final double BATTERY_REVS_ABOVE_GROUND = .50;
	int battY;
	public Bitmap bmpRamp;
	int batteryHeightPx;
	int batteryWidthPx;

	public static final double STORMCLOUD_HEIGHT_REV = .5;
	public static final double STORMCLOUD_WIDTH_REV = 1;
	public static final double STORMCLOUD_PCT_ABOVE_GROUND = .70;
	
	public int stormcloudBottomY;
	int stormcloudY;
	public Bitmap bmpStormcloud;
	int stormcloudHeightPx;
	int stormcloudWidthPx;
	
	public static final double SNAIL_HEIGHT_REV = .20;
	public static final double SNAIL_WH_RATIO = 200.0/124.0;
	public static final double SNAIL_WIDTH_REV = SNAIL_WH_RATIO * SNAIL_HEIGHT_REV;
	public static final double SNAIL_SPEED_REV_MS = .0003;
	double creatureTravelPx;
	long creatureOnscreenMsTimer;
	int snailMsPerPx;
	
	int snailY;
	public Bitmap bmpSnail;
	int snailHeightPx;
	int snailWidthPx;
	
	public static final double BEE_HEIGHT_REV = .15;
	public static final double BEE_Y_REV = 370.0/608.2;
	public static final double BEE_WH_RATIO = 200.0/125.0;
	public static final double BEE_WIDTH_REV = BEE_WH_RATIO * BEE_HEIGHT_REV;
	public static final double BEE_SPEED_REV_MS = .0005;
	public static final int BEE_FADE_DURATION = 600;
	public long beeKillTime;
	public boolean isBeeDead;
	int beeMsPerPx;
	
	public static final int ENEMY_INTERVAL = 1700;
	public static final int ENEMY_ODDS = 2;
	public long enemyTimer;
	
	
	int beeY;
	public Bitmap bmpBee;
	public Bitmap bmpBeeWhite;
	int beeHeightPx;
	int beeWidthPx;
		
//	int nextItemIdx;
	double distPastScreenRight;
	
	public Rect itemRect = new Rect(10000,0,0,0);
	
	public static final int MAX_ITEMS_ONSCREEN = 5;	//is equal to the number of item types, so only 1 of each item can be on screen at a time
	Rect[] onscreenRects = new Rect[MAX_ITEMS_ONSCREEN];
	boolean[] isItemTypeOnscreen = new boolean[MAX_ITEMS_ONSCREEN]; //look up if an item type is onscreen by it's id

	public final static double BMP_WH_RATIO = 2.0;
	public final static double BMP_HEIGHT_PCT = .75;
	
	public final static double LIGHTNING_WH_RATIO = 49.0/198.0;
	public Bitmap bmpLightning;
	Rect lightningRect;
	int lightningWidth;

	public double distSlowdownFactor = .06;
	int relativeDeltaDist;
	
	public Items(){
		nutY = intScreenHeight-(int)(NUT_REVS_ABOVE_GROUND*pxPerRev);
		nutHeightPx = (int)(NUT_HEIGHT_REV*pxPerRev);
		nutWidthPx = (int)(NUT_WIDTH_REV*pxPerRev);
		
		battY = intScreenHeight-(int)(BATTERY_HEIGHT_REV*pxPerRev);
		batteryHeightPx = (int)(BATTERY_HEIGHT_REV*pxPerRev);
		batteryWidthPx = (int)(BATTERY_WIDTH_REV*pxPerRev);
		
		stormcloudY = (int) (intScreenHeight - intScreenHeight*STORMCLOUD_PCT_ABOVE_GROUND);
		stormcloudHeightPx = (int)(STORMCLOUD_HEIGHT_REV*pxPerRev);
		stormcloudWidthPx = (int)(STORMCLOUD_WIDTH_REV*pxPerRev);
		bmpStormcloud = initBitmap(R.drawable.storm_cloud,stormcloudWidthPx,stormcloudHeightPx);
		stormcloudBottomY = stormcloudY + stormcloudHeightPx;
		
		int lightningHeight = intScreenHeight - stormcloudBottomY;
		lightningWidth = (int) ((double)lightningHeight * LIGHTNING_WH_RATIO);
		lightningRect = new Rect(0,stormcloudBottomY - (stormcloudHeightPx / 2),0,intScreenHeight);

		snailHeightPx = (int)(SNAIL_HEIGHT_REV*pxPerRev);
		snailWidthPx = (int)(SNAIL_WIDTH_REV *pxPerRev);
		snailY = intScreenHeight - snailHeightPx;
		Log.d(ID,"snail width and height and revs:"+ snailWidthPx + "," + snailHeightPx + " " + SNAIL_WIDTH_REV + "," + SNAIL_HEIGHT_REV);
		snailMsPerPx = (int) (1 / (SNAIL_SPEED_REV_MS * pxPerRev));
		//snailMsPerPx = 500;
		
		beeHeightPx = (int)(BEE_HEIGHT_REV*pxPerRev);
		beeWidthPx = (int)(BEE_WIDTH_REV *pxPerRev);
		beeY = intScreenHeight - (int)(BEE_Y_REV *pxPerRev);
		beeMsPerPx = (int) (1 / (BEE_SPEED_REV_MS * pxPerRev));

		bmpLightning = initBitmap(R.drawable.lightning,lightningWidth,lightningHeight);				
		bmpNut = initBitmap(R.drawable.nut_outline,nutWidthPx,nutHeightPx);
		bmpSnail = initBitmap(R.drawable.snail,snailWidthPx,snailHeightPx);
		bmpBee = initBitmap(R.drawable.beesingle,beeWidthPx,beeHeightPx);
		bmpBeeWhite = initBitmap(R.drawable.beewhite,beeWidthPx,beeHeightPx);
		
//		bmpBattery[0] = initBitmap(R.drawable.batt1,batteryWidthPx,batteryHeightPx);
//		bmpBattery[1] = initBitmap(R.drawable.batt2,batteryWidthPx,batteryHeightPx); 
//		bmpBattery[2] = initBitmap(R.drawable.batt3,batteryWidthPx,batteryHeightPx); 
//		bmpBattery[3] = initBitmap(R.drawable.batt4,batteryWidthPx,batteryHeightPx); 
//		bmpBattery[4] = initBitmap(R.drawable.batt5,batteryWidthPx,batteryHeightPx); 


		//itemRect = new Rect(1000,nutY,1000,nutY + nutHeightPx);
		bmpNutColor = new BitmapDrawable(null,initBitmap(R.drawable.nut_fill,nutWidthPx,nutHeightPx));
//		bmpNutColor.setBounds(itemRect);		
//		bmpNutColor.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
		
		reset();
	}
	
	public void reset(){
//		nextItemIdx = 0;
		nutIdx = 0;
		stormCloudDistances[0] = rand.nextInt(2000);
		stormCloudDistances[1] = rand.nextInt(2000)+3000;
		stormCloudDistances[2] = rand.nextInt(1000)+6000;
		currentStormcloud = 0;
		
		creatureTravelPx = 0;
		creatureOnscreenMsTimer = 0;
		enemyTimer = 0;
		for(int i = 0;i<MAX_ITEMS_ONSCREEN;i++){
			onscreenRects[i] = new Rect(0,0,0,0);
			isItemTypeOnscreen[i] = false;
		}
		onscreenRects[NUT_ID] = new Rect(intScreenWidth+1,nutY,intScreenWidth+1 + nutWidthPx,nutY +nutHeightPx);
		onscreenRects[BATTERY_ID]=new Rect(intScreenWidth+1,battY,intScreenWidth+1+ batteryWidthPx,battY + batteryHeightPx);
		onscreenRects[STORMCLOUD_ID] = new Rect(intScreenWidth+1,stormcloudY,intScreenWidth+1+stormcloudWidthPx,stormcloudY + stormcloudHeightPx);
		onscreenRects[SNAIL_ID] = new Rect(intScreenWidth+1,snailY,intScreenWidth+1 + snailWidthPx,snailY + snailHeightPx);
		onscreenRects[BEE_ID] = new Rect(intScreenWidth+1,beeY,intScreenWidth+1 + beeWidthPx,beeY + beeHeightPx);
	}
	
	public void update(){
		//move creatures
		if(isItemTypeOnscreen[SNAIL_ID]){
			creatureOnscreenMsTimer += deltaTime;
			if(creatureOnscreenMsTimer > snailMsPerPx){	
				onscreenRects[SNAIL_ID].offset(-1 * (int)(creatureOnscreenMsTimer / snailMsPerPx),0);
				creatureOnscreenMsTimer = creatureOnscreenMsTimer % snailMsPerPx;
			}
			if(onscreenRects[SNAIL_ID].right < 0){
				isItemTypeOnscreen[SNAIL_ID] = false;
				creatureOnscreenMsTimer = 0;
			}
		}else if(isItemTypeOnscreen[BEE_ID]){
			creatureOnscreenMsTimer += deltaTime;
			if(creatureOnscreenMsTimer > beeMsPerPx){	
				onscreenRects[BEE_ID].offset(-1*(int)(creatureOnscreenMsTimer / beeMsPerPx),0);
				creatureOnscreenMsTimer = creatureOnscreenMsTimer % beeMsPerPx;
			}
			if(onscreenRects[BEE_ID].right < 0){
				Sounds.stopBee();
				isItemTypeOnscreen[BEE_ID] = false;
				creatureOnscreenMsTimer = 0;
			}
		}else{	//opportunity to add an enemy
			enemyTimer += deltaTime;
			if(enemyTimer>ENEMY_INTERVAL){
				if(rand.nextInt(ENEMY_ODDS) == 0){
					if(rand.nextBoolean()){
						Sounds.startBee();
						isItemTypeOnscreen[BEE_ID]=true;
						onscreenRects[BEE_ID].offsetTo(intScreenWidth+1,onscreenRects[BEE_ID].top);
					}else{
						isItemTypeOnscreen[SNAIL_ID]=true;
						onscreenRects[SNAIL_ID].offsetTo(intScreenWidth+1,onscreenRects[SNAIL_ID].top);
					}
					creatureOnscreenMsTimer=0;
				}
				enemyTimer=0;
			}
		}
		
		if(isBeeDead){	//draw fading white overlay on lasered bee
			beeKillTime += deltaTime;
			fadePaint.setAlpha((int)((255 - ((float)beeKillTime / BEE_FADE_DURATION)*255)));
			if(beeKillTime > BEE_FADE_DURATION){
				isBeeDead = false;
				beeKillTime = 0;
				fadePaint.setAlpha(255);
			}
		}
		
		if(deltaDist == 0)
			return;
		
		//move everything on screen
		relativeDeltaDist = (int)(deltaDist*distSlowdownFactor*pxPerRev);
		
		for(int i=0;i<MAX_ITEMS_ONSCREEN;i++){
			if(isItemTypeOnscreen[i]){
				onscreenRects[i].offset(-1 * relativeDeltaDist, 0);				
				if(i==NUT_ID)
					bmpNutColor.setBounds(onscreenRects[i]);
				if(onscreenRects[i].right < 0)
					isItemTypeOnscreen[i] = false;	
			}
		}

		//add item we've just passed (really just nuts)
		if(nutIdx < NUM_NUTS && dist >= nutDistances[nutIdx]){
			Log.d(ID,"nut is on screen now");
			isItemTypeOnscreen[NUT_ID] = true;
			onscreenRects[NUT_ID].offsetTo(intScreenWidth+1,onscreenRects[NUT_ID].top);
			bmpNutColor.setColorFilter(NUT_COLORS[nutIdx].color, PorterDuff.Mode.SRC_IN);
			bmpNutColor.setBounds(onscreenRects[NUT_ID]);
			nutIdx++;
		}
		
		//stormclouds
		if(currentStormcloud < NUM_STORMCLOUDS && dist >= stormCloudDistances[currentStormcloud]){
			currentStormcloud++;
			isItemTypeOnscreen[STORMCLOUD_ID] = true;
			onscreenRects[STORMCLOUD_ID].offsetTo(intScreenWidth+1,onscreenRects[STORMCLOUD_ID].top);
		}
	}
	
	
//	public void draw(Canvas c){
//		if(itemRect.left>intScreenWidth)
//			return;
//		
//		switch(itemIds[currentItemIdx]){
//		case NUT_ID:
//			bmpNutColor.draw(c);
//			c.drawBitmap(bmpNut, null, itemRect, paint);
//			return;
//		case BATTERY_ID:
//			c.drawBitmap(bmpBattery[chargeIdx], null, itemRect, paint);
//			return;
//		case STORMCLOUD_ID:
//			c.drawBitmap(bmpStormcloud, null, itemRect, paint);
//			return;
//		}
//	}
	
	public void draw(Canvas c){
		
		if(isItemTypeOnscreen[NUT_ID]){
			bmpNutColor.draw(c);
			c.drawBitmap(bmpNut, null,onscreenRects[NUT_ID], paint);
			//Log.d(ID,"drawing nut at " + onscreenRects[NUT_ID].left + " , " + onscreenRects[NUT_ID].top + " , " + onscreenRects[NUT_ID].right + " , " + onscreenRects[NUT_ID].bottom );
		}
		
		if(isItemTypeOnscreen[BATTERY_ID]){
			c.drawBitmap(bmpBattery[chargeIdx], null,onscreenRects[BATTERY_ID], paint);
		}
		if(isItemTypeOnscreen[STORMCLOUD_ID]){
			c.drawBitmap(bmpStormcloud, null,onscreenRects[STORMCLOUD_ID], paint);
		}
		if(isItemTypeOnscreen[SNAIL_ID]){
			c.drawBitmap(bmpSnail, null,onscreenRects[SNAIL_ID], paint);
		}else if(isItemTypeOnscreen[BEE_ID]){
			c.drawBitmap(bmpBee, null,onscreenRects[BEE_ID], paint);
		}
		
		if(isBeeDead)
			c.drawBitmap(bmpBeeWhite, null, onscreenRects[BEE_ID],fadePaint);
	}
	
	public void drawLightning(Canvas c){
		lightningRect.left = itemRect.left + 20;
		lightningRect.right = lightningRect.left + lightningWidth;
		c.drawBitmap(bmpLightning, null, lightningRect, paint);
	}
	
	public int whatItemIsHere(int x){

		for(int i=0;i<MAX_ITEMS_ONSCREEN;i++){
			if(!isItemTypeOnscreen[i])
				continue;
			if(onscreenRects[i].contains(x, onscreenRects[i].centerY()))
					return i;
		}
		return -1;
	}
	
	public void killBee(){
		Sounds.stopBee();
		isBeeDead = true;
		beeKillTime = 0;
	}
	
	public int checkForCollision(Rect sqRect, Rect wheelRect){
		for(int i=0;i<MAX_ITEMS_ONSCREEN;i++){
			if(isItemTypeOnscreen[i]){
				if(i == NUT_ID && Rect.intersects(sqRect,onscreenRects[i])){
					isItemTypeOnscreen[i]=false;
					onscreenRects[NUT_ID].offsetTo(intScreenWidth+1,onscreenRects[NUT_ID].top);
					return i;
				}else if(i == SNAIL_ID && Rect.intersects(wheelRect,onscreenRects[i])){
					isItemTypeOnscreen[i]=false;
					return i;
				}else if(i == BEE_ID && Rect.intersects(sqRect,onscreenRects[i])){
					Sounds.stopBee();
					isItemTypeOnscreen[i]=false;
					return i;
				}
				
			}
		}
		return -1;
	}
}
