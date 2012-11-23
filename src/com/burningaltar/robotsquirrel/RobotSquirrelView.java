package com.burningaltar.robotsquirrel;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

public class RobotSquirrelView extends SurfaceView implements SurfaceHolder.Callback {

	public RobotSquirrelGame thread;
	Context context;
	
	public RobotSquirrelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		//resume();
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub		
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
		thread.initGraphics(getWidth(),getHeight());
		thread.setRunning(true);
		thread.start();		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//thread.setRunning(false);
		
		// TODO Auto-generated method stub	
	}
	
	@Override
	public boolean onTouchEvent (MotionEvent event) {
		return thread.onTouch(event);
	}
	

    public boolean keyUp(int keyCode) {
		Log.d("RS VIEW","Key event detected");
		if(thread==null)
			return true;
		else
			return thread.doKeyDown(keyCode);
    }
	
	public void pause(){
		if(thread!=null){
			try {
				thread.setRunning(false);
				thread.interrupt();
				thread = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void resume(){
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		thread = new RobotSquirrelGame(context, holder);
	}
}
