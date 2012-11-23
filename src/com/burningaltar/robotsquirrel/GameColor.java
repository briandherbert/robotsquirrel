package com.burningaltar.robotsquirrel;

import android.graphics.Color;

public enum GameColor {

	RED (Color.RED,"RED",R.drawable.hill_red),
	YELLOW (Color.YELLOW,"YELLOW",R.drawable.hill_yellow),
	ORANGE (Color.rgb(255, 102, 0),"ORANGE",R.drawable.hill_orange),
	PURPLE (Color.rgb(68, 0, 170),"PURPLE",R.drawable.hill_purple),
	BLUE (Color.BLUE,"BLUE",R.drawable.hill_blue),
	BLACK (Color.BLACK,"BLACK",R.drawable.hill_black),
	WHITE (Color.WHITE,"WHITE",R.drawable.hill_white),
	GRAY (Color.GRAY,"GRAY",R.drawable.hill_gray),
	DARKGREEN(Color.rgb(100, 255, 100),"DARKGREEN",R.drawable.hill_darkgreen),
	PINK (Color.rgb(255, 85, 153),"PINK",R.drawable.hill_pink),
	GREEN (Color.GREEN,"GREEN",R.drawable.hill_green);
	
	public final int color;
	public final String name;
	public final int hillBmpId;
	GameColor(int color,String name, int hillBmpId){
		this.color = color;
		this.name = name;
		this.hillBmpId = hillBmpId;
	}
}