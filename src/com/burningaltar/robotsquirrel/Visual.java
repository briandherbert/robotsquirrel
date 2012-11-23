package com.burningaltar.robotsquirrel;

import java.util.Random;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.util.Log;

/*
 * Everything is expressed in feet. The squirrel's wheel is 1 foot in diameter, so we can find px per foot
 * by finding the desired height of the sq bmp in px, dividing that by 2.7304 (wheels per height),
 * and multiplying that by pi.
 */

public abstract class Visual{
	
	public static final String ID = "VISUAL";
	public static boolean logs = false;
	public static final boolean saveMem = false;

	
	public static double mScreenWidth;
	public static double mScreenHeight;
	
	public static final double REVS_PER_SQUIRREL_BMP_HEIGHT = .86911353;
	public static final double SQUIRREL_BMP_HEIGHT = 526;
	
	public static final int SCREEN_BOTTOM_PX = 15;
	
	
	//tracking movement
	public static double deltaTimeInSec;
	public static long deltaTime=0;
	public static long lastTic;
	public static double dist=0;
	public static double deltaDist=0;
	public static double distInPx=0;
	
	
	public static double pxPerRev;
	public static double pxPerQuarterRev;
	
	public static int intScreenWidth;
	public static int intScreenHeight;
	
	public static int screenCenterX;
	public static int screenCenterY;
	public static Context mContext;
	public static Rect screenRect;
	
	public static Paint paint = new Paint();
	public static Paint skyPaint;
	public static Paint transPaint = new Paint();
	public static Paint whitePaint = new Paint();
	public static Paint fadePaint = new Paint();
	
	public static Random rand;
	static Vibrator vib;
	
	public static double scaleFactor;
	
	public static BitmapFactory.Options options;
	
	public static final int MAX_CHARGE_IDX = 4;
	public static int chargeIdx;
	
	boolean isVisible=true;
	
	public static int countdown;
	
	public static void init(int screenWidth, int screenHeight, Context context){
		mContext = context;
		mScreenWidth = intScreenWidth= screenWidth;
		mScreenHeight = intScreenHeight = screenHeight;
		screenCenterX = intScreenWidth/2;
		screenCenterY = intScreenHeight/2;
		screenRect = new Rect(0,0,screenWidth,screenHeight);
		
		pxPerRev = (Squirrel.BMP_HEIGHT_PCT * mScreenHeight)/REVS_PER_SQUIRREL_BMP_HEIGHT;
		pxPerQuarterRev = pxPerRev/4.0;
		vib = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
		paint.setColor(Color.BLACK);
		
		skyPaint = new Paint();
		skyPaint.setColor(Color.rgb(68, 194, 255));
		transPaint.setColor(Color.argb(100,0, 0, 0));
		whitePaint.setColor(Color.WHITE);
		rand = new Random();
		
		chargeIdx = 0;
		
		scaleFactor = ((double)screenHeight*Squirrel.BMP_HEIGHT_PCT)/SQUIRREL_BMP_HEIGHT;
		
		options = new BitmapFactory.Options();
		options.inTempStorage = new byte[8*1024];
		options.inSampleSize = 2;
		intScreenHeight -= SCREEN_BOTTOM_PX;
		
		countdown = 0;
		
		Log.v("blarg", "screen width " + intScreenWidth + " height " + intScreenHeight);
	}
	
	public static void startTimers(){
		deltaTime=0;
		deltaTimeInSec = 0;
		dist=0;
		deltaDist=0;
		distInPx=0;
	}
	
	
	public Bitmap initBitmap(int id){
		Bitmap bmp=BitmapFactory.decodeResource(mContext.getResources(),id);
		//bmp = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth()*.25), (int)(bmp.getHeight()*.25), false);
		return bmp;
	}
	
	public Bitmap initBitmap(int id, int width, int height){
		if(width <=0)
			width = 1;
		if(height <= 0)
			height = 1;
		
		Bitmap bmp=BitmapFactory.decodeResource(mContext.getResources(),id);
		//bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
		bmp = Bitmap.createScaledBitmap(bmp, width, height, false);
		return bmp;
	}
	
	public Bitmap initBitmapOpaque(int id, int width, int height){
		Bitmap bmp=BitmapFactory.decodeResource(mContext.getResources(),id);
		bmp = bmp.copy(Bitmap.Config.RGB_565, false);
		bmp = Bitmap.createScaledBitmap(bmp, width, height, false);

		return bmp;
	}
	
	public abstract void draw(Canvas c);

	public void initBmps() {

		
	}
	
	public static double scaleThisDouble(double d){
		return d * scaleFactor;
	}
	
	public static int getTextSizeInWidth(int widthPx, String text){
		Rect boundingRect = new Rect();
		Paint tempPaint = new Paint();
		tempPaint.setTextSize(100);
		tempPaint.setTypeface(Typeface.DEFAULT_BOLD);
		if(text.length()>18)
			text += "WW";
		tempPaint.getTextBounds(text, 0, text.length(), boundingRect);
		double textScaleFactor = (double)widthPx / (double)boundingRect.width();
		int newSize = (int)(100*textScaleFactor);
		
		tempPaint.setTextSize(newSize);
		tempPaint.getTextBounds(text, 0, text.length(), boundingRect);
//		do{
//			tempPaint.setTextSize(--newSize);
//			tempPaint.getTextBounds(text, 0, text.length(), boundingRect);
//		}while(boundingRect.width()>widthPx);
		if(logs)Log.w(ID,"Width of box " + widthPx + " calc width " + boundingRect.width() + " for " + text);
		return newSize;
	}
	
	public static int getTextSizeInWidth(int widthPx, int textLength){
		String text = "";
		while(text.length()<textLength)
			text+="W";
		return getTextSizeInWidth(widthPx,text);
	}
	
	public void setVisibility(boolean uIsVisible){
		isVisible = uIsVisible;
	}
}
