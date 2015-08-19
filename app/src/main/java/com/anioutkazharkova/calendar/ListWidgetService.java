package com.anioutkazharkova.calendar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.RemoteViewsService;

public class ListWidgetService extends RemoteViewsService {

	int period=1;

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		int appWidgetId = intent.getIntExtra(
				"widgetId",
				-1);
		SharedPreferences preferences = getApplicationContext().getSharedPreferences(
				Utility.LIST_PREF + appWidgetId, getApplicationContext().MODE_PRIVATE);
		
		List<EventItem> events=new ArrayList<EventItem>();
		period=preferences.getInt(Utility.CALEND_PERIOD, period);
try {
	events=new GetEventsTask(getApplicationContext(),period).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
} catch (InterruptedException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} catch (ExecutionException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
		return (new ListProvider(this.getApplicationContext(), intent,events));
	}

}
