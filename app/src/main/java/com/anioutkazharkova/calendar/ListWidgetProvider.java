package com.anioutkazharkova.calendar;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

public class ListWidgetProvider extends AppWidgetProvider {
	private PendingIntent timePendIntent;
	private AlarmManager timeManager;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		Intent serviceIntent = new Intent(context, ListTimeUpdateService.class);
		timePendIntent = PendingIntent.getService(context, 0, serviceIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Calendar calendar = Calendar.getInstance();
		timeManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		timeManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
				1000, timePendIntent);
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; ++i) {
			// RemoteViews remoteViews =
			updateWidgetListView(context, appWidgetManager, appWidgetIds[i]);

		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	public static void updateWidgetListView(Context context,
			AppWidgetManager manager, int appWidgetId) {

		SharedPreferences preferences = context.getSharedPreferences(
				Utility.LIST_PREF + appWidgetId, context.MODE_PRIVATE);
		int fontType = preferences.getInt(Utility.FONT_PREF, 0);
		int selectedTheme = preferences.getInt(Utility.THEME_PREF, 0);

		Theme theme = Utility.themeSelector(selectedTheme);

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.list_calendar);

		remoteViews.setInt(R.id.mainLayout, "setBackgroundResource",
				theme.getBackground());
		remoteViews.setInt(R.id.headerLayout, "setBackgroundColor", context
				.getResources().getColor(theme.getHeaderColor()));
		remoteViews.setInt(R.id.divider, "setBackgroundColor", context
				.getResources().getColor(theme.getDividerColor()));

		View headerView = getHeaderView(context, appWidgetId);
		Bitmap header = Utility.createDrawableFromView(context, headerView);
		remoteViews.setImageViewBitmap(R.id.tvMonthYear, header);

		Intent calendIntent = new Intent(
				"com.anioutkazharkova.calendar.showcalendar");
		PendingIntent calPend = PendingIntent.getBroadcast(context,
				appWidgetId, calendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.btnShowCalendar, calPend);

		Intent addIntent = new Intent(
				"com.anioutkazharkova.calendar.addcalendar");
		PendingIntent addPend = PendingIntent.getBroadcast(context,
				appWidgetId, addIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.btnAdd, addPend);

		Intent confIntent = new Intent(context, ConfActivity.class);
		confIntent.putExtra("id", appWidgetId);
		confIntent.putExtra("prefix", Utility.LIST_PREF);
		confIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		remoteViews.setOnClickPendingIntent(R.id.tvMonthYear, PendingIntent
				.getActivity(context, appWidgetId, confIntent,
						PendingIntent.FLAG_UPDATE_CURRENT));

		Intent edit = new Intent();// ("com.anioutkazharkova.calendar.editcalendar");
		PendingIntent pendEdit = PendingIntent.getBroadcast(context,
				appWidgetId, edit, PendingIntent.FLAG_UPDATE_CURRENT);

		remoteViews.setPendingIntentTemplate(R.id.listViewWidget, pendEdit);

		// RemoteViews Service needed to provide adapter for ListView
		Intent svcIntent = new Intent(context, ListWidgetService.class);

		svcIntent.putExtra("widgetId", appWidgetId);

		svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
		// setting adapter to listview of the widget
		remoteViews.setRemoteAdapter(appWidgetId, R.id.listViewWidget,
				svcIntent);

		remoteViews.setEmptyView(R.id.listViewWidget, R.id.tvEmpty);
		// return remoteViews;

		manager.updateAppWidget(appWidgetId, remoteViews);
		manager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listViewWidget);
	}

	private static View getHeaderView(Context context, int widgetId) {
		SharedPreferences preferences = context.getSharedPreferences(
				Utility.LIST_PREF + widgetId, context.MODE_PRIVATE);
		int fontType = preferences.getInt(Utility.FONT_PREF, 0);
		int selectedTheme = preferences.getInt(Utility.THEME_PREF, 0);
		int direct = 0;
		Theme theme = Utility.themeSelector(selectedTheme);

		MonthEntity currentMonth = new MonthEntity(direct, context
				.getResources().getStringArray(R.array.monthes));
		try {
			DayEntity[][] monthArray = new MonthTask(currentMonth)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		View headerView = LayoutInflater.from(context).inflate(
				R.layout.header_layout, null);
		TextView tvMonth = (TextView) headerView.findViewById(R.id.tvMonth), tvYear = (TextView) headerView
				.findViewById(R.id.tvYear);
		Calendar today = Calendar.getInstance();
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG,
				Locale.getDefault());

		tvMonth.setText(df.format(today.getTime()));
		// tvYear.setText(currentMonth.getYear() + "");
		tvMonth.setTextColor(context.getResources().getColor(
				theme.getLabelFont()));
		tvYear.setTextColor(context.getResources().getColor(
				theme.getLabelFont()));

		tvMonth.setTypeface(
				Utility.createTypeface(context, Utility.fontSelector(fontType)),
				Typeface.BOLD);
		tvYear.setTypeface(
				Utility.createTypeface(context, Utility.fontSelector(fontType)),
				Typeface.BOLD);

		return headerView;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		if (intent == null)
			return;
		if (intent.getAction().equals(
				"com.anioutkazharkova.calendar.showcalendar")) {
			Calendar today = Calendar.getInstance();
			long time = today.getTimeInMillis();
			Intent calendIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(Calendars.CONTENT_URI+"/time/" + time));
			calendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(calendIntent);
		} else if (intent.getAction().equals(
				"com.anioutkazharkova.calendar.addcalendar")) {
			long time = Calendar.getInstance().getTimeInMillis();
			Intent addIntent = new Intent(Intent.ACTION_EDIT);
			addIntent.setType("vnd.android.cursor.item/event");
			addIntent.putExtra("title", "");
			addIntent.putExtra("description", "");
			addIntent.putExtra("beginTime", time);
			addIntent.putExtra("endTime", time);
			addIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(addIntent);
		} else if (intent.getAction().equals(
				"android.intent.action.PROVIDER_CHANGED")) {
			// Toast.makeText(context,"CHANGED", Toast.LENGTH_LONG).show();
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			ComponentName component = new ComponentName(context,
					ListWidgetProvider.class);
			int[] extIds = manager.getAppWidgetIds(component);
			// Toast.makeText(context, "calendar", Toast.LENGTH_LONG).show();
			for (int widgetId : extIds) {
				updateWidgetListView(context, manager, widgetId);
			}
			manager.notifyAppWidgetViewDataChanged(extIds, R.id.listViewWidget);
		} else if (intent.getAction().equals(
				"com.anioutkazharkova.calendar.editcalendar")) {

			long eventId = intent.getLongExtra("event", -1);

			// Toast.makeText(context,eventId+"", Toast.LENGTH_LONG).show();
			if (eventId != -1) {
				Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI,
						eventId);
				Intent editIntent = new Intent(Intent.ACTION_VIEW).setData(uri);
				editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				context.startActivity(editIntent);

			}

		} else if (intent.getAction().equals(
				"com.anioutkazharkova.calendar.updatecalendar")) {
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			ComponentName component = new ComponentName(context,
					ListWidgetProvider.class);
			int[] extIds = manager.getAppWidgetIds(component);
			// Toast.makeText(context, "calendar", Toast.LENGTH_LONG).show();
			for (int widgetId : extIds) {
				updateWidgetListView(context, manager, widgetId);
			}
		}
		 else if (intent.getAction()
				.equals("android.intent.action.DATE_CHANGED")
				|| intent.getAction().equals("android.intent.action.TIME_SET")
				|| intent.getAction().equals(
						"android.intent.action.TIMEZONE_CHANGED")) {
			 AppWidgetManager manager = AppWidgetManager.getInstance(context);
				ComponentName component = new ComponentName(context,
						ListWidgetProvider.class);
				int[] extIds = manager.getAppWidgetIds(component);
				// Toast.makeText(context, "calendar", Toast.LENGTH_LONG).show();
				for (int widgetId : extIds) {
					updateWidgetListView(context, manager, widgetId);
				}
				manager.notifyAppWidgetViewDataChanged(extIds, R.id.listViewWidget);
		 }
	}
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		if (timeManager != null && timePendIntent != null) {
			timeManager.cancel(timePendIntent);
			// DataHolder.WIDGET_STYLES=null;
		}
		super.onDisabled(context);
		System.gc();
	}
}