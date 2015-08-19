package com.anioutkazharkova.calendar;

import java.util.Calendar;
import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

public class CheckEventsTask extends AsyncTask<Void, Void, DayEntity[][]> {

	boolean checkEvents=true;
	Context mContext;
	DayEntity[][] days;
	public CheckEventsTask() {
		// TODO Auto-generated constructor stub
	}
	public CheckEventsTask(Context context,DayEntity[][] days)
	{
		this.mContext=context;
		this.days=days;
	}
	
	@Override
	protected DayEntity[][] doInBackground(Void... params) {
		// TODO Auto-generated method stub
		for (int i = 0; i < Utility.WEEKS_NUMBER; i++) {
			for (int j = 0; j < Utility.DAYS_NUMBER; j++) {
				DayEntity dayEntity = days[i][j];
				
				boolean hasEvents=checkDay(dayEntity);
				//Проверяем каждый день
				dayEntity.setHasEvents(hasEvents);
			}
		}
		return days;
	}
	
	private boolean checkDay(DayEntity day)
	{
		
		String[] projection = new String[] { CalendarContract.Events.CALENDAR_ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_LOCATION };

		// 0 = January, 1 = February, ...

		Calendar startTime = Calendar.getInstance();
		startTime.set(day.getYear(), day.getMonth()-1, day.getDay(),0,0);
		Calendar endTime = Calendar.getInstance();
		endTime.set(day.getYear(), day.getMonth()-1, day.getDay(),23,59);

		//Calendar endTime= Calendar.getInstance();
		//endTime.set(2015,00,01,00,00);

		// the range is all data from 2014

		String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ) AND ( deleted != 1 ) )";

		Cursor cursor = mContext.getContentResolver().query( CalendarContract.Events.CONTENT_URI, projection, selection, null, null );
int count=0;
		// output the events 
		if (cursor!=null)
		{
			if (cursor.moveToFirst()) {
		    do {
		    	Date sd=new Date(cursor.getLong(3));
		    	Log.d("sd", sd.toString());
		    	count+=1;
		    	
		      //  Toast.makeText( this.getApplicationContext(), "Title: " + cursor.getString(1) + " Start-Time: " + (new Date(cursor.getLong(3))).toString(), Toast.LENGTH_LONG ).show();
		    } while ( cursor.moveToNext());
		}
			
		}
		cursor.close();
		return (count==0?false:true);
	}

}
