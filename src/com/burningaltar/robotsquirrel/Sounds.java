package com.burningaltar.robotsquirrel;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;



public class Sounds {
	 static int LASER = 0;
	 static int NUT = 1;
	 static int JUMP = 2;
	 static int THUNDER = 3;
	 static int IDLE = 4;
	 static int ACCEL;
	 static int HIT;
	 static int END;
	 static int LOSELIGHTNING;
	 static int BEE;


	 
	static boolean isMusic = true;
	static boolean isFX = true;
	
	static SoundPool fx;
	static MediaPlayer mp;
	
	static int idleStreamId=-1;
	static int beeStreamId=-1;
	
	public static void init(Context context,boolean uIsSound,boolean uIsFX){
		isMusic = uIsSound;
		isFX = uIsFX;
		
		fx = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		LASER = fx.load(context, R.raw.laser, 1);
		THUNDER = fx.load(context, R.raw.thunder, 1);
		NUT = fx.load(context, R.raw.nutsound, 1);
		HIT = fx.load(context, R.raw.hit, 1);
		ACCEL= fx.load(context, R.raw.accel, 1);
		IDLE= fx.load(context, R.raw.stop, 1);
		JUMP= fx.load(context, R.raw.jump, 1);
		END= fx.load(context, R.raw.end, 1);
		LOSELIGHTNING= fx.load(context, R.raw.loselightning, 1);
		BEE= fx.load(context, R.raw.bee, 1);

		
		mp = MediaPlayer.create(context, R.raw.rsmusic);
		
		if(mp==null)
			isMusic = false;
		else
			mp.setVolume(0.8f, 0.8f);

	}
	
	public static void destroy(){
		fx.release();
		fx = null;
		mp.stop();
		mp.release();
		mp = null;
	}
	

	public static void laser(){
		if(isFX)fx.play(LASER, 1, 1, 1, 0, 1);
	}
	
	public static void thunder(){
		if(isFX)fx.play(THUNDER, 1, 1, 1, 0, 1);

	}
	
	public static void nut(){
		if(isFX)fx.play(NUT, 1, 1, 1, 0, 1);
	}
	
	public static void end(){
		if(isFX)fx.play(END, 1, 1, 1, 0, 1);
	}
	
	public static void hit(){
		if(isFX)fx.play(HIT, 1, 1, 1, 0, 1);
	}
	
	
	public static void accel(){
		if(isFX)fx.play(ACCEL, 1, 1, 1, 0, 1);
	}
	
	public static void loselightning(){
		if(isFX)fx.play(LOSELIGHTNING, 1, 1, 1, 0, 1);
	}
	
	public static void idle(){
		if(isFX)idleStreamId=fx.play(IDLE, 1, 1, 1, 0, 1);
	}
	
	public static void jump(){
		if(isFX)fx.play(JUMP, 1, 1, 1, 0, 1);
	}
	public static void stopIdle(){
		if(isFX&& idleStreamId!=-1)fx.pause(idleStreamId);
		idleStreamId=-1;
	}
	
	public static void startBee(){
		if(isFX&& beeStreamId==-1)beeStreamId = fx.play(BEE, 1, 1, 1, 10, 1);
	}
	
	public static void stopBee(){
		if(isFX&& beeStreamId!=-1)fx.pause(beeStreamId);
		beeStreamId=-1;
	}
	
	public static void playMusic(){
		if(isMusic){
			mp.setLooping(true);
			mp.start();
		}
	}
	
	public static void stopMusic(){
		mp.pause();
		mp.seekTo(0);
	}
	
	public static void toggleMusic(boolean uIsMusic){
		if(isMusic == uIsMusic)
			return;
		isMusic = uIsMusic;
		if(isMusic){
			playMusic();
		}else{
			stopMusic();
		}
	}
	
	public static void toggleFX(boolean uIsFX){
		if(isFX == uIsFX)
			return;
		isFX = uIsFX;
		if(isFX)
			accel();
	}

}
