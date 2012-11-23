package com.burningaltar.robotsquirrel;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class Squirrel extends Visual{
	public static final String ID = "Squirrel";
	public static final int STATE_UNSTARTED=0;
	public static final int STATE_ACCEL = 1;
	public static final int STATE_OKSTOP = 3;
	public static final int STATE_GOODSTOP = 4;
	public static final int STATE_BATTERY = 5;
	//public static final int STATE_LIGHTNING = 6;
	public static final int STATE_DONE = 7;
	public static final int STATE_HALT = 8;
	public static final int STATE_THOUNDERCLOUD = 9;
	public static final int STATE_JUMP = 10;
	public static final int STATE_SHOOT = 11;

	
	public int state;
	boolean isTouching=false;
	boolean isLightning;
	public boolean isJustLanded = false;

	
	public final String[] STATE_NAMES = {
		"unstarted",
		"accel",
		"decel",
		"badstop",
		"goodstop",
		"onramp",
		"celebrating",
		"done",
		"halt",
		"animating",
		"jumping",
		"shooting"
	};

	
	//speed constants
	public static final double ACCEL = 1;
	public static final double ACCEL_AT_MAX_VELOCITY = .5;
	public static final double BURST_ACCEL = 4;
	public static final double MAX_ACCEL_VELOCITY = 120;
	public static final int MIN_VELOCITY = 30;
	public static final double BADSTOP_PCT_RETAINED = .95;
	public static final double GOODSTOP_PCT_RETAINED = 1.05;
	public static final double HIT_PCT_RETAINED = .90;
	public static final double SNAIL_HIT_PCT_RETAINED = .80;

	
	//squirrel
	
	//lightning
	public final static double LIGHTNING_WH_RATIO = 49.0/198.0;
	public static final int LIGHTNING_STRIKE_ANIMATION_DURATION = 800;
	public static final int LIGHTNING_BOOST_DURATION = 5000;
	public static final int LIGHNING_VELOCITY_BOOST = 100;
	public long lightningTimer;
	public Bitmap bmpLightning;
	public Rect lightningRect;
	int lightningBmpWidth = 49;
	int lightningBmpHeight = 198;
	
	//jump
	static double JUMP_HEIGHT_REVS = .56;
	int jumpHeightPx;
	static final int JUMP_TIME = 500;
	static double JUMP_GRAVITY;
	static double JUMP_INITIAL_VEL;
	
	//shoot
	static final int SHOOT_TIME = 200;
	final double LASER_X_PCT = 530.0/600.0;
	final double LASER_TOPY_PCT = 190.0/526.0;
	final double LASER_BOTTOMY_PCT = 214.75/526.0;
	Paint laserPaint=new Paint();
	int laserColor = Color.argb(150, 255, 0, 0);
	
	//hit
	boolean isHit = false;
	static final int HIT_TIME = 200;
	public Bitmap bmpHit;
	public Bitmap bmpHitStopped;
	public long hitTimer;
	
	
	public double totalTime;
	public long lastUpdateTime;
	
	//public double dist;
	public double revs;	//same as dist, but used even when stopped
	public double velocity;	//careful with this, it can apply to the "flywheel" even when stopped	
	public boolean isGoodStop;
	
	//GRAPHICS
	int squirrelY;
	
	public final static double BMP_WH_RATIO = 600.0/526.0;
	public final static double WHEEL_WH_RATIO = 201.0/125.0;
	public final static double STARS_WH_RATIO = 219.0/100.0;
	public final static double BMP_HEIGHT_PCT = .33;


	
	public final static double X_COORD_SCREEN_PCT = .1;
	public int leftX;
	public int pickupX;	//x coord where the squirrel picks up stuff
	
	public static final int BURSTING_DURATION = 1000;
	public static final double GOOD_ACCEL_VELOCITY_BOOST = 5;
	public static final double BATTERY_TAP_VELOCITY_BOOST = 2;
	public int lastStateChangeTimer;
	
	public static int bmpWidth;
	public static int bmpHeight;
	
	
	public int eyeColor;
	//public Bitmap bmpStopped;
	public Bitmap[] bmpStoppedFrames;
	public int stoppedFrameIdx;
	
	public Bitmap bmpMoving;
	
	public Bitmap bmpMovingGold;
	public Bitmap[] bmpStoppedFramesGold;
	public BitmapDrawable bmpEyeStopped;
	public BitmapDrawable bmpEyeMoving;
	public BitmapDrawable bmpEyeLaser;
	
	public Bitmap[] bmpWheel;
	public Bitmap[] bmpWheelBad;
	public Bitmap[] bmpWheelGood;
	
	public int revIdx;
	double partialRevs;
	
	public Rect squirrelRect;
	public Rect wheelHitbox;
	public Rect starsSrcRect;
	public Rect wheelRect;
	public Rect wheelColorRect;
	public Rect eyeRect;

	
	public Rect laserRect;
	
	int wheelYOffsetPx;	
	public Paint transBlackPaint;
	
	public Squirrel(){

		
		bmpHeight = (int) (BMP_HEIGHT_PCT * mScreenHeight);
		bmpWidth = (int) (BMP_HEIGHT_PCT * mScreenHeight * BMP_WH_RATIO);
		int wheelHeight = (int) (bmpHeight/2.0);
		int wheelWidth = (int) (wheelHeight*WHEEL_WH_RATIO);
		
		leftX = (int) (X_COORD_SCREEN_PCT * intScreenWidth);
		pickupX = (int) (leftX + .90 * bmpWidth);
		squirrelRect = new Rect(leftX,intScreenHeight - bmpHeight,leftX+bmpWidth,intScreenHeight);

		squirrelY = squirrelRect.top;
		jumpHeightPx = (int) (JUMP_HEIGHT_REVS*pxPerRev);
		double halfJumpTime = JUMP_TIME/2;
		JUMP_GRAVITY = -1*(jumpHeightPx * 2)/(halfJumpTime*halfJumpTime);
		JUMP_INITIAL_VEL = -1*halfJumpTime*JUMP_GRAVITY;
		
		wheelRect = new Rect(squirrelRect.left,squirrelRect.top + (int)(squirrelRect.height()/2.0),squirrelRect.left + wheelWidth,squirrelRect.bottom);
		wheelHitbox = new Rect(wheelRect);
		wheelHitbox.left = wheelHitbox.left + wheelHitbox.width()/2;
		wheelYOffsetPx = wheelRect.top - squirrelRect.top;
		wheelColorRect = new Rect(squirrelRect.left  + (int)(wheelRect.width()/2.0),wheelRect.top,wheelRect.right,wheelRect.bottom);
		eyeRect = new Rect(squirrelRect.left + (int)(squirrelRect.width()/2.0),squirrelRect.top,squirrelRect.right,squirrelRect.top + (int)(squirrelRect.height()/2.0));
		
		laserPaint.setColor(laserColor);
		
		transBlackPaint = new Paint();
		transBlackPaint.setColor(Color.argb(100, 0, 0, 0));

		bmpEyeStopped = new BitmapDrawable(null,initBitmap(R.drawable.sq_stopped_eye_tr));
		bmpEyeStopped.setBounds(eyeRect);		
		bmpEyeStopped.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
		
		bmpEyeMoving = new BitmapDrawable(null,initBitmap(R.drawable.sq_rolling_eye_tr));
		bmpEyeMoving.setBounds(eyeRect);		
		bmpEyeMoving.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);	
		
		bmpEyeLaser = new BitmapDrawable(null,initBitmap(R.drawable.sq_rolling_eye_tr));
		bmpEyeLaser.setBounds(eyeRect);		
		bmpEyeLaser.setColorFilter(laserColor, PorterDuff.Mode.SRC_IN);	
		
		//bmpStopped = initBitmap(R.drawable.sq_stopped_sm,squirrelRect.width(),squirrelRect.height());
		bmpMoving = initBitmap(R.drawable.sq_rolling_sm,squirrelRect.width(),squirrelRect.height());
		bmpHit = initBitmap(R.drawable.sq_red_flash,squirrelRect.width(),squirrelRect.height());
		bmpHitStopped = initBitmap(R.drawable.sq_red_flash_stopped,squirrelRect.width(),squirrelRect.height());
		
		bmpStoppedFrames = new Bitmap[2];
		bmpStoppedFrames[0] = initBitmap(R.drawable.sq_stopped_sm,squirrelRect.width(),squirrelRect.height());
		bmpStoppedFrames[1] = initBitmap(R.drawable.sq_stopped_sm2,squirrelRect.width(),squirrelRect.height());
		//bmpStoppedFrames[2] = initBitmap(R.drawable.sq_stopped_sm3,destRect.width(),destRect.height());
		
		bmpMovingGold = initBitmap(R.drawable.sq_rolling_gold,squirrelRect.width(),squirrelRect.height());
		
		bmpStoppedFramesGold = new Bitmap[2];
		bmpStoppedFramesGold[0] = initBitmap(R.drawable.sq_stopped_gold,squirrelRect.width(),squirrelRect.height());
		bmpStoppedFramesGold[1] = initBitmap(R.drawable.sq_stopped2_gold,squirrelRect.width(),squirrelRect.height());
		
		bmpWheel = new Bitmap[4];
		bmpWheel[0] = initBitmap(R.drawable.sq_wheel1,wheelWidth,wheelHeight);
		bmpWheel[1] = initBitmap(R.drawable.sq_wheel2,wheelWidth,wheelHeight);
		bmpWheel[2] = initBitmap(R.drawable.sq_wheel3,wheelWidth,wheelHeight);
		bmpWheel[3] = initBitmap(R.drawable.sq_wheel4,wheelWidth,wheelHeight);
		
		bmpWheelBad = new Bitmap[4];
		bmpWheelBad[0] = initBitmap(R.drawable.sq_yellow_wheel1,wheelWidth,wheelHeight);
		bmpWheelBad[1] = initBitmap(R.drawable.sq_yellow_wheel2,wheelWidth,wheelHeight);
		bmpWheelBad[2] = initBitmap(R.drawable.sq_yellow_wheel3,wheelWidth,wheelHeight);
		bmpWheelBad[3] = initBitmap(R.drawable.sq_yellow_wheel4,wheelWidth,wheelHeight);

		
		bmpWheelGood = new Bitmap[4];
		bmpWheelGood[0] = initBitmap(R.drawable.sq_green_wheel1,wheelWidth,wheelHeight);
		bmpWheelGood[1] = initBitmap(R.drawable.sq_green_wheel2,wheelWidth,wheelHeight);
		bmpWheelGood[2] = initBitmap(R.drawable.sq_green_wheel3,wheelWidth,wheelHeight);
		bmpWheelGood[3] = initBitmap(R.drawable.sq_green_wheel4,wheelWidth,wheelHeight);
		
		bmpLightning = initBitmap(R.drawable.lightning,lightningBmpWidth,lightningBmpHeight);
		laserRect = new Rect((int)(LASER_X_PCT * squirrelRect.width()) + squirrelRect.left, (int)(LASER_TOPY_PCT * squirrelRect.height()) + squirrelRect.top,
					intScreenWidth,(int)(LASER_BOTTOMY_PCT * squirrelRect.height()) + squirrelRect.top);
		
		reset();
		
	}
	
	public void reset(){
		state = STATE_UNSTARTED;
		velocity = MIN_VELOCITY;
		totalTime = 0;
		deltaTimeInSec = 0;
		dist = 0;
		deltaDist = 0;
		revIdx=0;
		partialRevs = 0;
		lastStateChangeTimer = 0;
		stoppedFrameIdx = 0;
		isLightning = false;
		squirrelRect.top = squirrelY;
		wheelHitbox.offsetTo(wheelHitbox.left, squirrelRect.top + wheelYOffsetPx);
		eyeRect.offsetTo(eyeRect.left, squirrelRect.top);
		bmpEyeStopped.setBounds(eyeRect);
	}

	
	public boolean update(){
		lastStateChangeTimer += deltaTime;
		if(state==STATE_UNSTARTED || state==STATE_DONE){
			stoppedFrameIdx = (int) ((lastStateChangeTimer/100)%2);
//			if(stoppedFrameIdx==3)
//				stoppedFrameIdx=1;
			return false;
		}
		
		long newTime = System.currentTimeMillis();
		deltaTimeInSec = deltaTime/1000.0;
		totalTime += deltaTimeInSec;
		lastUpdateTime = newTime;
		
		if(isLightning){
			lightningTimer += deltaTime;
			if(lightningTimer >= LIGHTNING_BOOST_DURATION)
				lightningDone();
		}
		
		if(isHit){
			hitTimer += deltaTime;
			if(hitTimer >= HIT_TIME)
				hitDone();
		}
		
		switch(state){			
		case STATE_ACCEL:
		case STATE_SHOOT:
			if(state == STATE_SHOOT && lastStateChangeTimer > SHOOT_TIME){
				if(isTouching)
					setState(STATE_ACCEL);
				else{
					setState(STATE_OKSTOP);
					break;
				}
			}
			double newVelocity = 0;
			if(velocity >= MAX_ACCEL_VELOCITY)
				newVelocity = velocity + ACCEL_AT_MAX_VELOCITY * deltaTimeInSec;
			else
				newVelocity = velocity + ACCEL * deltaTimeInSec;
			
			deltaDist = ((newVelocity + velocity)/2.0)* deltaTimeInSec;
			dist += deltaDist;
			revs +=deltaDist;
			
			velocity = newVelocity;
			break;
		case STATE_GOODSTOP:
		case STATE_OKSTOP:
			revs += 4*deltaTimeInSec;
		case STATE_HALT:
		case STATE_UNSTARTED:
			stoppedFrameIdx = (int) ((lastStateChangeTimer/100)%2);
			if(stoppedFrameIdx==3)
				stoppedFrameIdx=1;
			break;
		case STATE_BATTERY:
			break;
			//if(lastStateChangeTimer > BURSTING_DURATION)
				//setState(STATE_GOODSTOP);
			
		case STATE_THOUNDERCLOUD:
			if(lastStateChangeTimer > LIGHTNING_STRIKE_ANIMATION_DURATION)
				lightningStrike();
			break;
			
			
		case STATE_JUMP:
			deltaDist = ((velocity*2)/2.0)* deltaTimeInSec;
			dist += deltaDist;
			//revs +=deltaDist;
			
			squirrelRect.top = (int) (squirrelY - ((JUMP_INITIAL_VEL * lastStateChangeTimer)+(.5*JUMP_GRAVITY*lastStateChangeTimer*lastStateChangeTimer)));
			wheelHitbox.offsetTo(wheelHitbox.left, squirrelRect.top + wheelYOffsetPx);
			eyeRect.offsetTo(eyeRect.left, squirrelRect.top);
			bmpEyeStopped.setBounds(eyeRect);
			//squirrelRect.top = (int) (squirrelY - (.5*JUMP_GRAVITY*lastStateChangeTimer*lastStateChangeTimer));
			if(lastStateChangeTimer > JUMP_TIME){
				squirrelRect.top = squirrelY;
				wheelHitbox.offsetTo(wheelHitbox.left, squirrelRect.top + wheelYOffsetPx);
				eyeRect.offsetTo(eyeRect.left, squirrelRect.top);
				bmpEyeStopped.setBounds(eyeRect);
				if(isTouching)
					setState(STATE_ACCEL);
				else{
					isJustLanded = true;
					//setState(STATE_OKSTOP);
				}
				vib.vibrate(40);
			}
			break;
			
		}
				
		revIdx = ((int)(revs*4))%4;
		if(deltaDist<=0)
			return false;
		else
			return true;		
	}
	
	@Override
	public void draw(Canvas c){
		
		//moving
		if(state == STATE_ACCEL || state == STATE_SHOOT){
			
			if(isLightning)
				c.drawBitmap(bmpMovingGold,squirrelRect.left,squirrelRect.top, paint);
			else{
				c.drawBitmap(bmpMoving,squirrelRect.left,squirrelRect.top, paint);
				if(isHit)
					c.drawBitmap(bmpHit,squirrelRect.left,squirrelRect.top, paint);				
			}
			
			
//			if(state == STATE_BATTERY){
//				c.drawBitmap(bmpWheel[0], null, wheelRect, paint);
//				for(int i=0;i<chargeIdx;i++)
//					c.drawBitmap(bmpWheelGood[i], wheelColorRect.left, wheelColorRect.top, paint);
//			}else
				c.drawBitmap(bmpWheel[revIdx], wheelRect.left, squirrelRect.top +wheelYOffsetPx, paint);
						
			
			if(state == STATE_SHOOT){
				bmpEyeLaser.draw(c);
				c.drawRect(laserRect, laserPaint);
			}else
				bmpEyeMoving.draw(c);
			
			

		}else{	//stopped or jumping
			bmpEyeStopped.draw(c);
			
			if(isLightning)
				c.drawBitmap(bmpStoppedFramesGold[stoppedFrameIdx],squirrelRect.left,squirrelRect.top,  paint);
			else{
				c.drawBitmap(bmpStoppedFrames[stoppedFrameIdx],squirrelRect.left,squirrelRect.top,  paint);
				if(isHit)
					c.drawBitmap(bmpHitStopped,squirrelRect.left,squirrelRect.top, paint);	
			}
					
			//draw wheel
			c.drawBitmap(bmpWheel[0], wheelRect.left, squirrelRect.top +wheelYOffsetPx, paint);
			if(state== STATE_GOODSTOP){
				c.drawBitmap(bmpWheelGood[revIdx],null, wheelColorRect, paint);
			}else if(state== STATE_OKSTOP){
				c.drawBitmap(bmpWheelBad[revIdx], null, wheelColorRect, paint);							
				
			}
			
			if(state == STATE_THOUNDERCLOUD){
				if(lastStateChangeTimer < 600){
					transBlackPaint.setAlpha((int)(((double)lastStateChangeTimer / 600.0)*250));
					c.drawRect(0, 0, intScreenWidth, intScreenHeight, transBlackPaint);
				}else{
					isLightning = true;
					whitePaint.setAlpha(255 - (int)(((double)(lastStateChangeTimer-600) / 200.0)*250));
					c.drawRect(0, 0, intScreenWidth, intScreenHeight, whitePaint);
				}
				
				if((lastStateChangeTimer > 550 && lastStateChangeTimer < 650)){
					vib.vibrate(50);
					c.drawBitmap(bmpLightning, null,lightningRect, paint);
				}
			}
		}
		//c.drawRect(wheelHitbox, whitePaint);
	}
	
	public void setState(int newState){
		if(newState == state)
			return;
		
		if(isHit)
			isHit = false;
				
		switch(newState){			
		case STATE_ACCEL:
			//Sounds.stopIdle();
			if(state == STATE_GOODSTOP || state == STATE_OKSTOP || state == STATE_UNSTARTED)
				Sounds.accel();
			break;
		case STATE_GOODSTOP:
			velocity = velocity *GOODSTOP_PCT_RETAINED;
			Sounds.idle();
			deltaDist = 0;
			break;
		case STATE_OKSTOP:
			if(!isLightning && velocity > MIN_VELOCITY)
				velocity = Math.max(MIN_VELOCITY, (velocity *BADSTOP_PCT_RETAINED));
			Sounds.idle();

			deltaDist = 0;
			break;
		case STATE_BATTERY:
			deltaDist = 0;
			break;
		case STATE_HALT:			
			Sounds.hit();
			isHit = true;
			vib.vibrate(HIT_TIME);
			hitTimer = 0;
			velocity = MIN_VELOCITY;
			deltaDist = 0;
			isLightning=false;
			break;
		case STATE_UNSTARTED:
			break;
		case STATE_DONE:
			break;
		case STATE_JUMP:
			Sounds.jump();
			break;
		case STATE_SHOOT:
			Sounds.laser();
			vib.vibrate(50);
			break;
		}
		if(state == STATE_JUMP){	//just landed
			squirrelRect.top = squirrelY;
		}
		state = newState;
		lastStateChangeTimer = 0;
	}
	
	public boolean jump(){
		if(!isHit && state == STATE_ACCEL){
			setState(STATE_JUMP);
			return true;
		}
		return false;
	}
	
	public boolean shoot(){
		if(state != STATE_JUMP){
			setState(STATE_SHOOT);
			return true;
		}
		return false;
	}
	
	public void hit(boolean isSnail){
		if(isLightning){
			vib.vibrate(HIT_TIME/4);
			return;
		}
		Sounds.hit();
		isHit = true;
		
		hitTimer = 0;
		if(isSnail){
			velocity *= SNAIL_HIT_PCT_RETAINED;
			vib.vibrate(HIT_TIME+100);
		}else{
			velocity *= HIT_PCT_RETAINED;
			vib.vibrate(HIT_TIME);
		}
	}
	
	public void hit(){
		hit(false);
	}
	
	public void hitDone(){
		isHit = false;
	}
	
	public void burst(double amt){
		vib.vibrate(100);
		velocity += amt;
	}
	
	public void redlight(){
		setState(STATE_HALT);
	}
	
	public void setBattery(){
		setState(STATE_BATTERY);
	}
	
	
	public void setEyeColor(int color){
		bmpEyeStopped.setColorFilter(color, PorterDuff.Mode.SRC_IN);
		bmpEyeMoving.setColorFilter(color, PorterDuff.Mode.SRC_IN);
	}
	
	public void giveNut(int color){
		Sounds.nut();
		setEyeColor(color);
		burst(BURST_ACCEL);
	}
	
	public void underThundercloud(int cloudY) {
		if(isLightning)
			return;
		int lightningHeight = intScreenHeight - cloudY;
		int lightningWidth = (int) (LIGHTNING_WH_RATIO * lightningHeight);
		lightningRect = new Rect(squirrelRect.centerX(),cloudY,squirrelRect.centerX()+lightningWidth,cloudY+lightningHeight);
		setState(STATE_THOUNDERCLOUD);
		Sounds.thunder();
	}
	
	public void lightningStrike(){
		setState(STATE_GOODSTOP);
		isLightning = true;

		lightningTimer = 0;
		velocity += LIGHNING_VELOCITY_BOOST;
	}
	
	public void lightningDone(){
		Sounds.loselightning();
		isLightning = false;
		lightningTimer = 0;
		velocity -= LIGHNING_VELOCITY_BOOST;
	}
	
	public void endGame(){
		setState(STATE_DONE);
		vib.vibrate(400);
	}
	
	
	public void touchDown(int lightState){
		isTouching = true;
		if(state == STATE_THOUNDERCLOUD || state == STATE_JUMP || state == STATE_DONE)
			return;
		if(state == STATE_BATTERY && chargeIdx != MAX_CHARGE_IDX){
			chargeIdx++;
			burst(BATTERY_TAP_VELOCITY_BOOST);
			return;
		}

		switch(lightState){
		case Stoplight.STATE_RED:
			setState(STATE_HALT);
			break;
		case Stoplight.STATE_GREEN_FAST:
			burst(GOOD_ACCEL_VELOCITY_BOOST);
			setState(STATE_ACCEL);
			break;
		
		default:
			setState(STATE_ACCEL);
			break;			
		}
		if(state==STATE_UNSTARTED){
			lastUpdateTime = System.currentTimeMillis();
		}
	}
	
	public void touchUp(int lightState){
		isTouching=false;
//		isLightning = false;
		if(state == STATE_THOUNDERCLOUD || state == STATE_JUMP || state == STATE_DONE)
			return;
		if(state ==  STATE_BATTERY){
			if(chargeIdx == MAX_CHARGE_IDX)
				setState(STATE_GOODSTOP);
			return;
		}

		
		switch(lightState){
		case Stoplight.STATE_YELLOW1:
			setState(STATE_GOODSTOP);
			break;
		case Stoplight.STATE_RED:
			break;
		default:
			setState(STATE_OKSTOP);
		}
	}
}
