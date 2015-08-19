package com.anioutkazharkova.calendar;

import android.os.AsyncTask;

public class MonthTask extends AsyncTask<Void, Void, DayEntity[][]> {

	MonthEntity currentMonth;
	
	public MonthTask() {
		// TODO Auto-generated constructor stub
	}
	public MonthTask(MonthEntity currentMonth)
	{
		this.currentMonth=currentMonth;
	}
	@Override
	protected DayEntity[][] doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return currentMonth.createMonthArray();
		//Для каждого дня поискать наличие событий
		//return days;
	}

}
