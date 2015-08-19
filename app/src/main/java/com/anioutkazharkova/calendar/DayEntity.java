package com.anioutkazharkova.calendar;

public class DayEntity {

	private int year;
	private int month;
	private int day;
	private DayType dayType;
	private boolean hasEvents;
	
	public DayEntity()
	{
		dayType=DayType.REGULAR;
		
	}
	public DayEntity(int day,DayType type)
	{
		this.day=day;
		this.dayType=type;
		
	}
	
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public DayType getDAY_TYPE() {
		return dayType;
	}
	public void setDAY_TYPE(DayType dAY_TYPE) {
		dayType = dAY_TYPE;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public boolean isHasEvents() {
		return hasEvents;
	}
	public void setHasEvents(boolean hasEvents) {
		this.hasEvents = hasEvents;
	}
}
