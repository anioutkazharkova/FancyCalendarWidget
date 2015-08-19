package com.anioutkazharkova.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.util.Log;

public class GetEventsTask extends AsyncTask<Void, Void, List<EventItem>> {

	

		boolean checkEvents=true;
		Context mContext;
		List<EventItem> events=new ArrayList<EventItem>();
		int days=7;
		public GetEventsTask() {
			// TODO Auto-generated constructor stub
		}
		public GetEventsTask(Context context,int days)
		{
			this.mContext=context;
			this.days=days;
		}
		
		@Override
		protected List<EventItem> doInBackground(Void... params) {
			
			return getEvents();
		}
		
		private List<EventItem> getEvents()
		{
			List<EventItem> events=new ArrayList<EventItem>();
			String[] projection = new String[] { CalendarContract.Events.CALENDAR_ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_LOCATION,CalendarContract.Events._ID };

			// 0 = January, 1 = February, ...

			long start;
			Calendar startTime = Calendar.getInstance();
			
			startTime.set(startTime.get(Calendar.YEAR),startTime.get(Calendar.MONTH),startTime.get(Calendar.DAY_OF_MONTH),0,0);
			Calendar endTime = Calendar.getInstance();
			endTime.add(Calendar.DAY_OF_YEAR,days);

			Calendar teTime= Calendar.getInstance();
			teTime.set(startTime.get(Calendar.YEAR),startTime.get(Calendar.MONTH),startTime.get(Calendar.DAY_OF_MONTH),23,59);

			// the range is all data from 2014

			String selection = "((( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " )) OR (( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " +teTime.getTimeInMillis() + " )) AND ( deleted != 1 ) )";

			SimpleDateFormat dt=new SimpleDateFormat("dd MMMM");
			SimpleDateFormat tt=new SimpleDateFormat("HH:mm");
			
			String currentDate="";
			Cursor cursor = mContext.getContentResolver().query( CalendarContract.Events.CONTENT_URI, projection, selection, null, CalendarContract.Events.DTSTART+ " ASC" );
	int count=0;
			// output the events 
			if (cursor!=null)
			{
				if (cursor.moveToFirst()) {
			    do {
			    	String startDate=dt.format(new Date(cursor.getLong(3)));
					if (!currentDate.equals(startDate))
					{
						currentDate=startDate;
						EventItem dateItem=new EventItem();
						dateItem.StartDate=currentDate;
						events.add(dateItem);
					}
					
			    	EventItem item=new EventItem();
			    	item.ID=cursor.getInt(cursor.getColumnIndex(CalendarContract.Events._ID));
			    	item.Title=cursor.getString(1);
			    	item.Text=cursor.getString(2);
			    	item.StartTime=cursor.getLong(3);
			    	//item.EndTime=cursor.getLong(4);
			    	
					
					item.StartDate="";
			    	events.add(item);
			    	
			    	Log.d("sd", "Title: " + cursor.getString(1) + " Start-Time: " + (new Date(cursor.getLong(3))).toString());
			    } while ( cursor.moveToNext());
			}
				
			}
			cursor.close();
			return events;
		}

	}