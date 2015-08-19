package com.anioutkazharkova.calendar;

public class WidgetStyle {

	private int fontType;
	private int themeType;
	private int counter;
	private int currentDayId;
	
	public int getFontType() {
		return fontType;
	}
	public void setFontType(int fontType) {
		this.fontType = fontType;
	}
	public int getThemeType() {
		return themeType;
	}
	public void setThemeType(int themeType) {
		this.themeType = themeType;
	}
	public WidgetStyle()
	{
		this.fontType=0;
		this.themeType=0;
		this.counter=0;
		this.currentDayId=-1;
	}
	public WidgetStyle(int font,int theme)
	{
		this.fontType=font;
		this.themeType=theme;
		this.counter=0;
		this.currentDayId=-1;
	}
	
	public WidgetStyle(int font,int theme,int counter)
	{
		this(font,theme);
		this.counter=counter;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public int getCurrentDayId() {
		return currentDayId;
	}
	public void setCurrentDayId(int currentDayId) {
		this.currentDayId = currentDayId;
	}
}
