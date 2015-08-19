package com.anioutkazharkova.calendar;

import java.util.Calendar;
import java.util.Locale;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class TimeServiceExtended extends Service {

private Context context;
	
	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
@Override
public void onCreate() {
	// TODO Auto-generated method stub
	super.onCreate();
}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		context = getApplicationContext();		
		updateTime();
		return super.onStartCommand(intent, flags, startId);
	}

	private void updateTime() {	
		AppWidgetManager manager = AppWidgetManager
				.getInstance(getApplicationContext());
		ComponentName calendarComponent = new ComponentName(
				getApplicationContext(), CalendarWidget.class);
		
		ComponentName smallComponent=new ComponentName(getApplicationContext(), SmallCalendarWidget.class);
		ComponentName listComponent=new ComponentName(getApplicationContext(), ListWidgetProvider.class);
		
		ComponentName extendedComponent=new ComponentName(getApplicationContext(), ExtendedCalendarWidget.class);
		int[] widgetIds = manager.getAppWidgetIds(calendarComponent);
		int[] smallIds=manager.getAppWidgetIds(smallComponent);
		int[] extIds=manager.getAppWidgetIds(extendedComponent);
		//int[] listIds=manager.getAppWidgetIds(listComponent);
		
		Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
		DataHolder.CURRENT_DAY=currentCalendar.get(Calendar.DAY_OF_MONTH);
		if (DataHolder.CURRENT_DAY!=DataHolder.PREVIOUS_DAY)
		{
			DataHolder.PREVIOUS_DAY =DataHolder.CURRENT_DAY;
		
		for (int widgetId : extIds) {
			try{
			ExtendedCalendarWidget.updateView(getApplicationContext(), manager, widgetId);
			}
			catch(Exception e)
			{
				
			}
			
		//CalendarWidget.updateTime(context, manager, widgetId);
		}
		/*for (int widgetId : listIds) {
			try{
			ListWidgetProvider.updateWidgetListView(getApplicationContext(), manager, widgetId);
			}
			catch(Exception e)
			{
				
			}
			
		//CalendarWidget.updateTime(context, manager, widgetId);
		}*/
		
		}
	}

}

