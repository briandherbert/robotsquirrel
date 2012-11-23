package com.burningaltar.robotsquirrel;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


public class RobotSquirrelGame extends Thread{
	public final String ID = "RSGAME";
	boolean log = true;
	private Context mContext;
	private SurfaceHolder mSurfaceHolder;
	private Canvas canvas = null;
	boolean mRun = false;
	
	boolean alreadyWarnedAboutYellow = false;
	boolean alreadyWarnedAboutJump = false;
	boolean alreadyWarnedAboutShoot = false;

	public boolean isTouching = false;
	
	public long startTime;
	public long lastTime;
	public long elapsedTime;

	public static final int GAME_DURATION = 60000;
	public int frames;
	public int msCounter;
	public double fpsGranularity = 1000;
	double fps;
	
	public static final int IDEAL_FPS = 70;
	public int msPerFrame = 1000 / IDEAL_FPS;
	long lastTick;
	long tickTime=0;
	
	public boolean isPhysicsStarted;
	public boolean isMenu;
	public boolean isIngamePaused=false;
	
	public Squirrel s;
	public Scenery hills;
	public JustLandmarks landmarks;
	public Clouds clouds;
	public Stoplight stoplight;
	public Items items;
	public InfoText infoText;
	
	public GameMenu menu;
	
	public Paint paint;
	public Paint paintStroke;
	
	public String text="";
	int textSize;
	
	float userY;
	float userX;
	
	int currentHighScore = 0;
	public static final String PREF_NAME = "savedPrefs";
	public static final String PREF_HIGH_SCORE = "highScore";
	public static final String PREF_SOUND = "sound";
	public static final String PREF_FX = "fx";


	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	
	String messageGoodStart = "";
	String messageGoodStop = "";
	String messageBadStop = "";
	String messageLightning = "";
	String messageWarnYellow = "";
	String messageStart = "";
	String wordNut = "";
	
	//sound


	public RobotSquirrelGame(Context context, SurfaceHolder holder) {
		mContext = context;
		mSurfaceHolder = holder;
		


		paint = new Paint();
		paint.setTextSize(25);
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		
		paintStroke = new Paint();
		paintStroke.setColor(Color.BLACK);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setTextSize(25);
		paintStroke.setStrokeWidth(1);
		
		messageGoodStart =  mContext.getString(R.string.greatstart);
		messageGoodStop =  mContext.getString(R.string.greatstop);
		messageBadStop = mContext.getString(R.string.badstop);
		messageLightning = mContext.getString(R.string.lightning);
		messageWarnYellow = mContext.getString(R.string.stopyellow);
		messageStart = mContext.getString(R.string.start);
		wordNut = mContext.getString(R.string.nut);
		
		isPhysicsStarted=false;
		frames = 0;
		elapsedTime = 0;
		fps=10;
		
		prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		editor = prefs.edit();
		int tempHs = prefs.getInt(PREF_HIGH_SCORE, -1);

		if(tempHs != -1)
			currentHighScore = tempHs;
		
		Sounds.init(mContext,prefs.getBoolean(PREF_SOUND, true),prefs.getBoolean(PREF_FX, true));
		isMenu = true;
	}
	
	public void initGraphics(int uScreenWidth, int uScreenHeight){
		if(log)Log.d(ID,"initGraphics");
		Visual.init(uScreenWidth, uScreenHeight, mContext);
		s = new Squirrel();
		if(log)Log.d(ID,"Initialized squirrel");
		hills = new Scenery();
		if(log)Log.d(ID,"Initialized scenery");
		landmarks = new JustLandmarks();
		if(log)Log.d(ID,"Initialized landmarks");
		clouds = new Clouds();
		if(log)Log.d(ID,"Initialized clouds");
		stoplight = new Stoplight();

		
		items = new Items();
		if(log)Log.d(ID,"Initialized items");
		infoText = new InfoText(stoplight.destRect);
				
		menu = new GameMenu(mContext,currentHighScore,Sounds.isMusic,Sounds.isFX);
		Sounds.playMusic();
		if(log)Log.d(ID,"Initialized graphics");
		lastTick = System.currentTimeMillis();
		
	}
	
	
	public void setRunning(boolean b) {
		mRun = b;
		if(b == false){
			Sounds.destroy();
		}
	}
			
			
	public void run() {
		while(mRun) {
			canvas = null;
			
	        try {

	            canvas = mSurfaceHolder.lockCanvas();
	            synchronized (mSurfaceHolder) {	 
	            	if(!isIngamePaused)
	            		doPhysics();
	            	doDraw();
	            }
	        } catch(Exception e){
	        System.out.println("Error "+e);

	        e.printStackTrace();
	        mSurfaceHolder.unlockCanvasAndPost(canvas);
	        return;
	        
			}finally {
		            // do this in a finally so that if an exception is thrown
		            // during the above, we don't leave the Surface in an
		            // inconsistent state
		            if (canvas != null) {
		                mSurfaceHolder.unlockCanvasAndPost(canvas);
		            }
		        }
			tickTime = System.currentTimeMillis() - lastTick;

			try{
	    	if(tickTime < msPerFrame) {
	    		sleep(msPerFrame - tickTime);
	    	}

			}catch(Exception e){
				String err = (e.getMessage()==null)?"Couldn't sleep":e.getMessage();

				Log.v(ID,err);
				}
			lastTick = System.currentTimeMillis();
		}

	}
	
	public void startGame(){
		isMenu = false;
		isIngamePaused = false;
		alreadyWarnedAboutYellow = false;
		userY = 0;
		userX = 0;
		infoText.setText(messageStart);
		hills.reset();
		s.reset();
		landmarks.reset();
		items.reset();
		stoplight.reset();
		elapsedTime = -1 * Stoplight.MAX_RED_MS;
		menu.pauseGame();
		Sounds.stopBee();
		Sounds.nut();
	}
	
	public void endGame(){
		Sounds.end();
		Sounds.stopBee();
//		SharedPreferences.Editor editor = prefs.edit();
		if(Visual.dist>currentHighScore){
			editor.putInt(PREF_HIGH_SCORE, (int)Visual.dist); // value to store
			editor.commit();
		}
		s.endGame();
		isMenu = true;
		menu.endGame();

	}
	
	public boolean onTouch(MotionEvent event) {
		//if(log)Log.v(ID,"Sending motion event before synch");
		synchronized (mSurfaceHolder) {
			//if(log)Log.v(ID,"Sending motion event");
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				isTouching = true;
				if(!isMenu){
					s.touchDown(stoplight.state);
					userY = event.getRawY();
					userX = event.getRawX();
					if(stoplight.state == Stoplight.STATE_GREEN_FAST)
						infoText.setText(messageGoodStart,false);
					else if(stoplight.state == Stoplight.STATE_RED)
						infoText.setText(messageBadStop,false);
				}
			}else if(event.getAction()==MotionEvent.ACTION_UP){
				isTouching = false;
				if(isMenu){
					switch(menu.onTouch((int)event.getX(), (int)event.getY())){
					case 0:
						return true;
					case GameMenu.ACTION_NEW_GAME:
						startGame();
						break;
					case GameMenu.ACTION_RESUME:
						resumeFromMenu();
						break;
					case GameMenu.ACTION_SOUND:
						Sounds.toggleMusic(!Sounds.isMusic);
						menu.isSound = Sounds.isMusic;
						//Sounds.toggleMusic(menu.isSound);
						editor.putBoolean(PREF_SOUND, Sounds.isMusic); // value to store
						editor.commit();
						break;
					case GameMenu.ACTION_FX:
						Sounds.toggleFX(!Sounds.isFX);
						menu.isFX = Sounds.isFX;
						editor.putBoolean(PREF_FX, Sounds.isFX); // value to store
						editor.commit();
						break;
					}
				}else{
					s.touchUp(stoplight.state);
					if(stoplight.state == Stoplight.STATE_YELLOW1 && s.state != Squirrel.STATE_JUMP)
						infoText.setText(messageGoodStop,false);
					if(s.state == Squirrel.STATE_OKSTOP || s.state == Squirrel.STATE_GOODSTOP){
						if(items.isItemTypeOnscreen[Items.STORMCLOUD_ID] && 
								s.squirrelRect.left < items.onscreenRects[Items.STORMCLOUD_ID].centerX() && 
								s.squirrelRect.right > items.onscreenRects[Items.STORMCLOUD_ID].centerX() && !s.isLightning){
							s.underThundercloud(items.onscreenRects[Items.STORMCLOUD_ID].centerY());
							infoText.setText(messageLightning);
						}
					}
				}
			}else if(event.getAction()==MotionEvent.ACTION_MOVE && !isMenu && stoplight.state != Stoplight.STATE_RED){
				if(Math.abs(userY - event.getRawY())> 40 || Math.abs(userX - event.getRawX())> 40){
					if(Math.abs(userY - event.getRawY())> Math.abs(userX - event.getRawX()))
						s.jump();
					else{
						
						if(s.shoot() && items.isItemTypeOnscreen[Items.BEE_ID]){
							elapsedTime -= 400;

							items.isItemTypeOnscreen[Items.BEE_ID]=false;
							items.killBee();
						}
					}
					userX = event.getRawX();
					userY = event.getRawY();
				}
			}
		}
		return true;
	}
	
	public boolean doKeyDown(int keyCode){
		if(keyCode == KeyEvent.KEYCODE_MENU && !isMenu){
			pauseMenu();
			Log.d(ID,"Got key event menu");
			return true;
		}
		return false;
	}
	
	public void doPhysics(){
		if(!isPhysicsStarted){
			Visual.deltaTime=0;
			lastTime = System.currentTimeMillis();
			startTime = lastTime;
			isPhysicsStarted = true;
			return;
		}
		
		if(!isMenu && elapsedTime > GAME_DURATION){
			endGame();
		}
		
		Visual.deltaTime = System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		
		
		msCounter += Visual.deltaTime;
		
		frames++;
		if(msCounter >= fpsGranularity){
			msCounter=(int) (msCounter - fpsGranularity);
			fps = frames;
			frames=0;
		}		
		
		if(isMenu){
			Visual.deltaDist = (.005)* Visual.deltaTime;
			//hills.updateGapless();
			//clouds.update();
		}else{
			elapsedTime += Visual.deltaTime;
			Visual.countdown = (int)((GAME_DURATION - elapsedTime) / 1000);
			items.update();
		}
		if(!isMenu && s.update()){	//we moved
			
			hills.updateGapless();
			landmarks.updateGapless();
			clouds.update();			
			
			if(stoplight.state == Stoplight.STATE_RED && s.state == Squirrel.STATE_ACCEL){
				s.redlight();
				infoText.setText(messageBadStop,false);
				
			}else if(alreadyWarnedAboutYellow == false && stoplight.state == Stoplight.STATE_YELLOW3){
				infoText.setText(messageWarnYellow,false);
				alreadyWarnedAboutYellow = true;
			}
			
			
			//check for collisions with items
			//if(items.itemRect.left < s.pickupX && s.state != Squirrel.STATE_BATTERY && Visual.chargeIdx==0){
				switch(items.checkForCollision(s.squirrelRect, s.wheelHitbox)){
				case Items.NUT_ID:
					text = Items.NUT_COLORS[items.nutIdx-1].name + " "+wordNut;
					infoText.setText(text);
					s.giveNut(Items.NUT_COLORS[items.nutIdx-1].color);
					elapsedTime -= 4000;
					hills.setHillColor(items.nutIdx-1);					
					break;
					
				case Items.SNAIL_ID:
					s.hit(true);
					break;
				case Items.BEE_ID:
					s.hit();
					break;
				}			
			//}
		}
		
		//squirrel just landed, this should be handled in squirrel but would be slower
		if(s.isJustLanded){
			if(stoplight.state == Stoplight.STATE_YELLOW1){
				s.setState(Squirrel.STATE_GOODSTOP);
				infoText.setText(messageGoodStop,false);
			}else
				s.setState(Squirrel.STATE_OKSTOP);
			if(items.isItemTypeOnscreen[Items.STORMCLOUD_ID] && 
					s.squirrelRect.left < items.onscreenRects[Items.STORMCLOUD_ID].centerX() && 
					s.squirrelRect.right > items.onscreenRects[Items.STORMCLOUD_ID].centerX() && !s.isLightning){
				s.underThundercloud(items.onscreenRects[Items.STORMCLOUD_ID].centerY());
				infoText.setText(messageLightning);
			}
			s.isJustLanded = false;
		}
		
		stoplight.update();
	} 
	
	public void pauseMenu(){
		isMenu = true;
		isIngamePaused = true;
	}
	
	
	public void resumeFromMenu(){
		lastTime = System.currentTimeMillis();
		isMenu = false;
		isIngamePaused = false;
	}
	
	public void doDraw(){

			stoplight.draw(canvas);	
		if(!isMenu){
			landmarks.draw(canvas);
		}
		
		//clouds.draw(canvas);
		//hills.draw(canvas);
		
		if(!isMenu){
			s.draw(canvas);		
			items.draw(canvas);		
			infoText.draw(canvas);
		}
		
		if(isMenu) {
			menu.draw(canvas);
		}

			
		if(log){
			
		if(tickTime >0)
			canvas.drawText("fps " +fps, 10, 200, paint);
//		canvas.drawText("Dist, delta" + (int)Visual.dist + " " + Visual.deltaDist, 10, 30, paint);
//		//canvas.drawText("Dist " + Visual.dist, 10, 30, paintStroke);
//		canvas.drawText("Speed " + s.velocity, 10, 60, paint);
//		canvas.drawText("Time " + Visual.countdown, 10, 90, paint);
//		canvas.drawText("Time " + msCounter, 200, 90, paint);
//
//		canvas.drawText("Light state " + stoplight.state, 10, 150, paint);
//		canvas.drawText("Squirrel state " + s.STATE_NAMES[s.state], 10, 180, paint);
//		//canvas.drawText("Frames " + frames, 10, 210, paint);
//		canvas.drawText("Is touching " + isTouching, 10, 240, paint);
//		canvas.drawText("nut idx " + items.nutIdx, 10, 270, paint);
		}
	}
	

}
