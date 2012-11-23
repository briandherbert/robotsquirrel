package com.burningaltar.robotsquirrel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.util.Log;

public class GameMenu extends Visual{
	public static final int STATE_PREGAME = 0;
	public static final int STATE_MIDGAME = 1;
	public static final int STATE_ENDGAME = 2;
	
	int state=-1;
	
	public static final int ACTION_NEW_GAME = 1;
	public static final int ACTION_RESUME = 2;
	public static final int ACTION_SOUND = 3;
	public static final int ACTION_FX = 4;
	
	public static final int NUM_BUTTONS = 2;
	
	Paint buttonPaint = new Paint();
	Paint buttonTextPaint = new Paint();
	Paint paintStroke = new Paint();
	Paint menuBgPaint = new Paint();
	Paint paintFill;
	
	int buttonWidth;
	int buttonHeight;
	int buttonVertSpacing;
	
	float textScaleFactor;
	int textSize;
	int textHeight;
	
	Bitmap buttonBmp;
	
	//logo
	Bitmap bmpLogo;
	Rect logoRect;
	static final double LOGO_HW_RATIO= 208.0/700.0;
	
	//new game button
	Rect newGameRect;
	String newGameText = "New Game";
	int newGameTextY;	
	Rect newGameHorizRect;
	Rect newGameVertRect;
	
	//resume button
	Rect resumeRect;
	String resumeText = "Resume";
	int resumeTextY;
	
	//tips
	Rect tipsRect;
	Paint tipsPaint = new Paint();
	static final int NUM_TIPS = 9;
	String[] tips = new String[NUM_TIPS];
	int currentTip = 0;
	long tipTimer;
	long lastTime;
	int tipsPaintAlpha;
	public static final int TIP_CHANGE_DURATION = 7000;
	int tipsY;
	
	//bottom runner
	static final double RUNNER_HW_RATIO  =59.0/800.0;
	int runnerHeight;
	Bitmap tipsBgBmp;
	
	Paint fadeInPaint = new Paint();
	int fadeInAlpha = 255;
	public static final int FADE_IN_DURATION = 1200;
	int fadeInTimer=0;
	
	//score
	int scoreTextSize;
	int scoreMaxTextSize;
	int currentHighScore=0;
	boolean isNewHighScore=false;
	Rect highscoreRect;
	String midScreenText="";
	String highscoreTipText="";
	Paint scorePaint = new Paint();
	int scoreColor = Color.BLACK;
	
	String newHighScoreText="";
	String highScoreText = "";
	String scoreText = "";
	String distUnit = "";
	
	int r=0;
	int g=0;
	int b=0;

	//sound button
	Rect soundRect;
	Bitmap bmpSound;
	Bitmap bmpSlashIcon;
	boolean isSound;
	
	//FX button
	Rect fxRect;
	Bitmap bmpFX;
	boolean isFX;

	public GameMenu(Context mContext, int currentHighScore, boolean isSound, boolean isFX){
		this.currentHighScore = currentHighScore;
		this.isSound = isSound;
		this.isFX = isFX;
		
		buttonHeight = intScreenHeight / 5;
		buttonWidth = intScreenWidth/3;
		int logoWidth = intScreenWidth/2;
		buttonVertSpacing = buttonHeight / 3;
		buttonBmp = initBitmap(R.drawable.graybutton,buttonWidth,buttonHeight);						

		//logo	
		int logoHeight = (int)(logoWidth*LOGO_HW_RATIO);
		bmpLogo = initBitmap(R.drawable.rslogo,logoWidth,logoHeight);
		logoRect = new Rect(screenCenterX-(logoWidth/2),0,screenCenterX+(logoWidth/2),logoHeight);

		Rect boundingRect = new Rect();		
		float textWidth = buttonTextPaint.measureText(newGameText);		
		textScaleFactor = buttonWidth / textWidth;
		Log.d("GameMenu","text bounding width:"+ textWidth);
	
		menuBgPaint.setColor(Color.argb(100, 0, 0, 0));
		buttonPaint.setColor(Color.rgb(0, 0, 0));
		
		buttonTextPaint.setTextSize((int)(buttonTextPaint.getTextSize()*textScaleFactor*.8));	
		buttonTextPaint.getTextBounds(newGameText, 0, newGameText.length(), boundingRect);
		textHeight = boundingRect.height();
		buttonTextPaint.setColor(Color.WHITE);
		buttonTextPaint.setAlpha(230);
		buttonTextPaint.setTextAlign(Align.CENTER);
		buttonTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		paintStroke.setColor(Color.BLACK);
		paintStroke.setTextSize(buttonTextPaint.getTextSize());
		paintStroke.setAlpha(200);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setStrokeWidth(2);
		paintStroke.setTextAlign(Align.CENTER);
		paintStroke.setTypeface(Typeface.DEFAULT_BOLD);
		
		int buttonX = screenCenterX-(buttonWidth/2);
			
		newGameRect = new Rect(buttonX,0,buttonX+buttonWidth,buttonHeight);
		newGameRect.top = logoRect.bottom +buttonVertSpacing;
		newGameRect.bottom = newGameRect.top + buttonHeight;
		newGameTextY = newGameRect.top + (buttonHeight/2) + (textHeight/2);
		
		newGameVertRect = new Rect(newGameRect);
		newGameHorizRect = new Rect(newGameRect);
		int horizSpacing = (intScreenWidth - (2*buttonWidth)) / 3;
		newGameHorizRect.offsetTo(horizSpacing, newGameRect.top);
		
		resumeRect = new Rect(newGameRect);
		resumeRect.offsetTo(newGameHorizRect.right+horizSpacing, newGameHorizRect.top);
		resumeTextY = resumeRect.top + (buttonHeight/2) + (textHeight/2);
		
		//score	
		highscoreTipText = mContext.getString(R.string.tiphighscore);

		newHighScoreText = mContext.getString(R.string.newhighscore);
		highScoreText = mContext.getString(R.string.highscore);
		scoreText = mContext.getString(R.string.score);
		distUnit = " " +mContext.getString(R.string.distunit);
		midScreenText = newHighScoreText +" "+ Integer.toString(currentHighScore);
		
		scorePaint.setColor(scoreColor);
		scoreTextSize = getTextSizeInWidth(logoRect.width(), newHighScoreText + "X");
		scoreMaxTextSize = getTextSizeInWidth(intScreenWidth, midScreenText);
		scorePaint.setTextSize(scoreTextSize);
		scorePaint.setTextAlign(Align.CENTER);
		scorePaint.setTypeface(Typeface.DEFAULT_BOLD);

		
		scorePaint.getTextBounds(newGameText, 0, newGameText.length(), boundingRect);
		highscoreRect = new Rect(0,0,intScreenWidth,buttonHeight);
		highscoreRect.top = newGameRect.bottom +buttonVertSpacing;
		highscoreRect.bottom = highscoreRect.top + boundingRect.height();

		//tips
		runnerHeight = (int) (RUNNER_HW_RATIO * intScreenWidth);
		tipsBgBmp = initBitmap(R.drawable.bottombg,intScreenWidth,runnerHeight);
		
		tipsRect = new Rect(newGameVertRect);
		tipsRect.offsetTo(newGameVertRect.left, newGameVertRect.bottom + buttonVertSpacing);
		
		tips[0] = mContext.getString(R.string.tip0);
		tips[1] = mContext.getString(R.string.tip1);
		tips[2] = mContext.getString(R.string.tip2);
		tips[3] = mContext.getString(R.string.tip3);
		tips[4] = mContext.getString(R.string.tip4);
		tips[5] = mContext.getString(R.string.tip5);
		tips[6] = mContext.getString(R.string.tip6);

		tips[7] = mContext.getString(R.string.tiphighscore) + " "+Integer.toString(currentHighScore);
		tips[8] = mContext.getString(R.string.tiplandmark) + " "+JustLandmarks.LANDMARK_NAMES[rand.nextInt(JustLandmarks.LANDMARK_NAMES.length)];
		
		int longTipId=0;
		for(int i=0;i<tips.length;i++)
			if(tips[i].length()>tips[longTipId].length())
				longTipId = i;
				//tipTextLength = tips[i].length();
		int tipTextSize = getTextSizeInWidth(intScreenWidth, tips[longTipId]);
		tipsPaint.setColor(Color.WHITE);
		tipsPaint.setTextSize(tipTextSize);
		tipsPaint.setTextAlign(Align.CENTER);
		tipsPaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		
		Rect tipsBoundRect = new Rect();
		tipsPaint.getTextBounds(tips[0], 0, tips[0].length(), tipsBoundRect);

		tipsRect = new Rect(0,(int)(mScreenHeight - runnerHeight),intScreenWidth,(int)mScreenHeight);
		tipsY = tipsRect.centerY() + tipsBoundRect.height()/2;
		
		//sound
		int soundIconWidth = (int) (highscoreRect.height()*2.1);
		
//		Log.v(ID,"screen width, height, sound icon width: "+intScreenWidth+" "+ intScreenHeight+" "+soundIconWidth);
		int soundRectY = highscoreRect.bottom + (tipsRect.top - highscoreRect.bottom)/2 - soundIconWidth/2;
		soundRect = new Rect(screenCenterX - soundIconWidth/2,soundRectY,screenCenterX + soundIconWidth/2,soundRectY+soundIconWidth);
		bmpSound = initBitmap(R.drawable.soundicon,soundIconWidth,soundIconWidth);
		bmpSlashIcon = initBitmap(R.drawable.slashicon,soundIconWidth,soundIconWidth);
		
		//FX
		fxRect = new Rect(soundRect);
		bmpFX =initBitmap(R.drawable.fxicon,soundIconWidth,soundIconWidth);
		
		fxRect.offsetTo(fxRect.left - soundIconWidth, fxRect.top);
		soundRect.offsetTo(fxRect.right + soundIconWidth, soundRect.top);
		
		fadeInPaint.setColor(Color.WHITE);
		
		//prefs = mContext.getSharedPreferences(PREF_NAME, 0);		
		reset();
	}
	
	public void reset(){
		setState(STATE_PREGAME);
		tipsPaintAlpha = 255;
		lastTime = System.currentTimeMillis();
	}

	
	public void draw(Canvas c) {
		
		//bad place for this, but fades in and out tip text
		tipTimer += System.currentTimeMillis() - lastTime;
		if(tipTimer < TIP_CHANGE_DURATION/2 && tipsPaintAlpha < 255 - 20){
			tipsPaintAlpha += 20;
			tipsPaint.setAlpha(tipsPaintAlpha);
		}else if(tipTimer > TIP_CHANGE_DURATION - 1000 && tipsPaintAlpha > 20){
			tipsPaintAlpha-=20;
			tipsPaint.setAlpha(tipsPaintAlpha);
		}
			
		
		if(tipTimer > TIP_CHANGE_DURATION){
			tipTimer=0;
			currentTip = (currentTip +1) %NUM_TIPS;
		}
		//c.drawRect(screenRect, menuBgPaint);
		//c.drawCircle(newGameRect.left, newGameRect.top + buttonHeight/2, buttonHeight/2, buttonPaint);
		c.drawBitmap(bmpLogo, null, logoRect, buttonPaint);
		
		c.drawBitmap(bmpSound, null,soundRect, buttonPaint);
		if(!isSound)
			c.drawBitmap(bmpSlashIcon, null,soundRect, buttonPaint);
		
		c.drawBitmap(bmpFX, null,fxRect, buttonPaint);
		if(!isFX)
			c.drawBitmap(bmpSlashIcon, null,fxRect, buttonPaint);

		
		//high score
		//c.drawText(highscoreText, highscoreRect.centerX()+1,highscoreRect.centerY()+1, paintStroke);
		if(isNewHighScore && fadeInTimer%5 ==0)
			scorePaint.setColor(Color.rgb(rand.nextInt(256),rand.nextInt(256), rand.nextInt(256)));
		
		
		
		if(state == STATE_PREGAME || state == STATE_ENDGAME){		
			c.drawBitmap(buttonBmp, null, newGameRect, buttonPaint);
			c.drawText(newGameText, screenCenterX+1,newGameTextY+1, paintStroke);
			c.drawText(newGameText, screenCenterX,newGameTextY, buttonTextPaint);
			
			if(state == STATE_ENDGAME){
				fadeInTimer += System.currentTimeMillis() - lastTime;
				if(fadeInTimer < FADE_IN_DURATION){
					c.drawRect(screenRect, fadeInPaint);
				}else if(fadeInAlpha > 0 ){
					fadeInAlpha = Math.max(0, fadeInAlpha - 12);
					fadeInPaint.setAlpha(fadeInAlpha);
					c.drawRect(screenRect, fadeInPaint);
				}				
				
			}
			
		}else if(state == STATE_MIDGAME){
			c.drawBitmap(buttonBmp, null, newGameHorizRect, buttonPaint);
			c.drawText(newGameText, newGameHorizRect.centerX()+1,newGameTextY+1, paintStroke);
			c.drawText(newGameText, newGameHorizRect.centerX(),newGameTextY, buttonTextPaint);
			
			c.drawBitmap(buttonBmp, null, resumeRect, buttonPaint);		
			c.drawText(resumeText, resumeRect.centerX()+1,resumeTextY+1, paintStroke);
			c.drawText(resumeText, resumeRect.centerX(), resumeTextY,buttonTextPaint);					
		}
		
		c.drawText(midScreenText, highscoreRect.centerX(),highscoreRect.centerY(), scorePaint);
		


		c.drawBitmap(tipsBgBmp, null, tipsRect, buttonPaint);		
		c.drawText(tips[currentTip], screenCenterX, tipsY, tipsPaint);
		
		lastTime = System.currentTimeMillis();

	}
	
	public int onTouch(int x, int y){
		if(newGameRect.contains(x, y)){
			return ACTION_NEW_GAME;
		}else if(state == STATE_MIDGAME && resumeRect.contains(x,y))
			return ACTION_RESUME;
		else if(tipsRect.contains(x,y)){
			currentTip = (currentTip +1) %NUM_TIPS;
			tipTimer = 0;
			tipsPaintAlpha = 255;
			tipsPaint.setAlpha(tipsPaintAlpha);
		}else if(soundRect.contains(x,y)){
			return ACTION_SOUND;
		}else if(fxRect.contains(x,y)){
			return ACTION_FX;
		}
		return 0;
	}
	
	public void setState(int newState){
		if(state == newState)
			return;
		currentTip = 0;
		scorePaint.setColor(scoreColor);
		//new state
		if(newState == STATE_PREGAME){
			midScreenText = highScoreText +" "+ Integer.toString(currentHighScore) +distUnit;
			
		}else if(newState == STATE_MIDGAME){
			newGameRect.set(newGameHorizRect);
			midScreenText = highScoreText +" "+ Integer.toString(currentHighScore)+distUnit;
		}else if(newState == STATE_ENDGAME){
			tips[8] = mContext.getString(R.string.tiplandmark) + " "+JustLandmarks.LANDMARK_NAMES[rand.nextInt(JustLandmarks.LANDMARK_NAMES.length)];
			if(dist > currentHighScore){
				isNewHighScore = true;
				currentHighScore = (int)dist;
				midScreenText = newHighScoreText +" "+ Integer.toString(currentHighScore)+distUnit;
				
			}else
				midScreenText = scoreText +" "+ Integer.toString((int)dist)+distUnit;
			fadeInAlpha = 255;
			fadeInPaint.setAlpha(fadeInAlpha);
			fadeInTimer = 0;			
		}
		
		//state we're leaving
		if(state == STATE_MIDGAME)
			newGameRect.set(newGameVertRect);
		else if(state == STATE_ENDGAME){
			isNewHighScore = false;
			midScreenText = highscoreTipText +" "+ Integer.toString(currentHighScore);
			scorePaint.setColor(Color.DKGRAY);
		}
		lastTime = System.currentTimeMillis();	
		state = newState;
	}
	
	public void pauseGame(){
		setState(STATE_MIDGAME);
	}
	
	public void endGame(){
		setState(STATE_ENDGAME);
	}

}
