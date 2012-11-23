package com.burningaltar.robotsquirrel;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

public class RobotSquirrel extends Activity {
	public static final String ID = "RS Activity";
	
	public static final String MY_AD_UNIT_ID = "1234";
	RobotSquirrelView mRSView;
	View menuOverlay;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(ID,"oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        LayoutInflater inflater = getLayoutInflater();
        menuOverlay = inflater.inflate(R.layout.main, null);
        
        //Runtime rt = Runtime.getRuntime();
        //long maxMemory = rt.maxMemory();
        //Log.v("onCreate", "maxMemory:" + Long.toString(maxMemory));

        mRSView = (RobotSquirrelView) findViewById(R.id.robotsquirrel);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	Log.d(ID,"onresume");
    	mRSView.resume();
    	getWindow().setBackgroundDrawable(null);
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	Log.d(ID,"onpause");
    	mRSView.pause();
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	Log.d(ID,"ondestroy");
    	finish();
    }
    
	@Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
		//super.onKeyUp(keyCode, msg);
        return mRSView.keyUp(keyCode);
    }
    
}