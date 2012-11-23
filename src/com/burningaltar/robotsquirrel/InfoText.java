package com.burningaltar.robotsquirrel;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;

public class InfoText extends Visual{

	public static final int DEFAULT_FADE_TEXT_DURATION = 1500;
	public static final int DEFAULT_TEXT_DURATION = 1000;
	Paint paintFill;
	Paint paintStroke;
	static final double textHeightAsScreenPct = .15;
	
	long drawStartTime;
	long drawEndTime;
	
	double elapsedMs;
	double durationMs;
	
	float idealTextWidth;
	
	boolean fade = false;
	
	String text = "";
	Rect timerRect;
	Paint timerPaint = new Paint();
	int timeRemaining;
	int timerX;
	int timerY;
	
	Paint timerWarnPaint = new Paint();
	Bitmap bmpStopwatch;
	double STOPWATCH_WH_RATIO = 150.0 / 189.0;
	Rect stopwatchRect;

	public InfoText(Rect sunRect){
		paintFill = new Paint();
		paintStroke = new Paint();
		idealTextWidth = intScreenWidth * .8f;
		float textWidth = paintFill.measureText("SLIDE SIDEWAYS TO SHOOT!");		
		float textScaleFactor = idealTextWidth / textWidth;
		paintFill.setTextSize(paintFill.getTextSize()*textScaleFactor);
		paintStroke.setTextSize(paintFill.getTextSize());
		
		paintFill.setColor(Color.WHITE);
		paintFill.setStyle(Paint.Style.FILL);
		paintFill.setTextAlign(Align.CENTER);
		paintFill.setTypeface(Typeface.DEFAULT_BOLD);
			
		paintStroke.setColor(Color.BLACK);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setStrokeWidth(3);
		paintStroke.setTextAlign(Align.CENTER);
		paintStroke.setTypeface(Typeface.DEFAULT_BOLD);
		
		//timer
		int stopwatchHeight = (int) (pxPerRev/3.0);
		int stopwatchWidth = (int) (stopwatchHeight * STOPWATCH_WH_RATIO);
//		bmpStopwatch = initBitmap(R.drawable.stopwatch,stopwatchWidth,stopwatchHeight);
		stopwatchRect = new Rect(10,60,10 + stopwatchWidth,stopwatchHeight + 60);
		timerRect = new Rect(sunRect);

		int timerTextSize = getTextSizeInWidth((int)pxPerRev/2, "60");
		timerPaint.setTextSize(timerTextSize);
		Rect textBounds = new Rect();
		timerPaint.getTextBounds("60", 0, 2, textBounds);
		
//		timerX  = stopwatchRect.left + stopwatchRect.width()/2;
//		timerY = (int) (stopwatchRect.top + stopwatchHeight * .6 + (.1 * stopwatchHeight));
		
		timerY = (int) (70+textBounds.height());
		timerX = 20;
		
//		timerX = sunRect.centerX();
//		timerY = sunRect.centerY() + textBounds.height()/2;
		timerPaint.setColor(Color.WHITE);
		timerPaint.setAlpha(195);

//		timerPaint.setStyle(Paint.Style.STROKE);
//		paintStroke.setStrokeWidth(3);
		timerPaint.setTextAlign(Align.LEFT);
		timerPaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		timerWarnPaint = new Paint(timerPaint);
		timerWarnPaint.setColor(Color.RED);
		timerWarnPaint.setTextSize(timerTextSize + 10);
//		timerWarnPaint.set

		
		
	}
	
	public void draw(Canvas c){
		if(System.currentTimeMillis() < drawEndTime){
			elapsedMs = (int) (System.currentTimeMillis() - drawStartTime);
			
			if(fade){
				paintFill.setAlpha(255 - (int)(255.0*(elapsedMs / durationMs)));
				paintStroke.setAlpha(paintFill.getAlpha());
			}
			
			c.drawText(text, screenCenterX, screenCenterY, paintFill);
			c.drawText(text, screenCenterX, screenCenterY, paintStroke);
		}
		if((countdown*1000) < RobotSquirrelGame.GAME_DURATION){
			if(countdown<=3)
				c.drawText(""+countdown, timerX, timerY, timerWarnPaint);
			else
				c.drawText(""+countdown, timerX, timerY, timerPaint);
		}
	}
	
	public void setText(String text,boolean fade){
		this.text = text;
		this.fade = fade;
		drawStartTime = System.currentTimeMillis();
		if(fade)
			durationMs = DEFAULT_FADE_TEXT_DURATION;
		else
			durationMs = DEFAULT_TEXT_DURATION;
		drawEndTime = (long) (drawStartTime + durationMs);
				
		paintFill.setAlpha(255);
		paintStroke.setAlpha(255);
	}
	
	public void setText(String text){
		setText(text,true);
	}
}
