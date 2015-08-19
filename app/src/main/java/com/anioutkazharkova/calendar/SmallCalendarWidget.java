package com.anioutkazharkova.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

public class SmallCalendarWidget extends AppWidgetProvider {

	private static final int initialDirect = 0;
	// private static int direct = 0;
	private PendingIntent timePendIntent;
	private AlarmManager timeManager;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Intent serviceIntent = new Intent(context, TimeUpdateService.class);
		timePendIntent = PendingIntent.getService(context, 0, serviceIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Calendar calendar = Calendar.getInstance();
		timeManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		timeManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
				1000, timePendIntent);

		for (int widgetId : appWidgetIds) {
			updateView(context, appWidgetManager, widgetId);

		}
	}

	public static void updateView(Context context, AppWidgetManager manager,
			int widgetId) {
		int direct = 0;

		SharedPreferences preferences = context.getSharedPreferences(
				Utility.WIDGET_PREF_SMALL + widgetId, context.MODE_PRIVATE);
		int fontType = preferences.getInt(Utility.FONT_PREF, 0);
		int themeType = preferences.getInt(Utility.THEME_PREF, 0);
		direct = preferences.getInt(Utility.COUNTER_PREF, direct);

		Theme theme = Utility.themeSelector(themeType);

		RemoteViews remoteView = new RemoteViews(context.getPackageName(),
				R.layout.small_initial_layout);

		remoteView.setInt(R.id.divider, "setBackgroundColor", context
				.getResources().getColor(theme.getDividerColor()));
		remoteView.setInt(R.id.headerLayout, "setBackgroundColor", context
				.getResources().getColor(theme.getHeaderColor()));
		View headerView = createHeaderView(context, widgetId, direct);
		Bitmap headerBitmap = Utility.createDrawableFromView(context,
				headerView);
		remoteView.setImageViewBitmap(R.id.tvMonthYear, headerBitmap);
		Utility.unbindDrawables(headerView);

		headerView = null;

		if (headerBitmap != null) {
			headerBitmap = null;
		}

		Intent confIntent = new Intent(context, ConfActivity.class);
		confIntent.putExtra("id", widgetId);
		confIntent.putExtra("prefix", Utility.WIDGET_PREF_SMALL);
		confIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		remoteView.setOnClickPendingIntent(R.id.tvMonthYear, PendingIntent
				.getActivity(context, widgetId, confIntent,
						PendingIntent.FLAG_UPDATE_CURRENT));

		View view = createNewView(context, widgetId, direct);
		Bitmap bitmapDay = Utility.createDrawableFromView(context, view);
		remoteView.setImageViewBitmap(R.id.imDay, bitmapDay);

		Utility.unbindDrawables(view);
		view = null;
		if (bitmapDay != null) {
			// bitmapDay.recycle();
			bitmapDay = null;
		}

		remoteView.setInt(R.id.content, "setBackgroundResource",
				theme.getBackground());

		remoteView.setInt(R.id.btnPrev, "setColorFilter", context.getResources().getColor(theme.getArrowFont()));
		remoteView.setInt(R.id.btnNext, "setColorFilter", context.getResources().getColor(theme.getArrowFont()));

		Intent prevIntent = new Intent(
				"com.anioutkazharkova.calendar.previousSmallCalendar");
		prevIntent.putExtra("id", widgetId);
		PendingIntent prevPend = PendingIntent.getBroadcast(context, widgetId,
				prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteView.setOnClickPendingIntent(R.id.btnPrev, prevPend);
		Intent nextIntent = new Intent(
				"com.anioutkazharkova.calendar.nextSmallCalendar");
		nextIntent.putExtra("id", widgetId);
		PendingIntent nextPend = PendingIntent.getBroadcast(context, widgetId,
				nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteView.setOnClickPendingIntent(R.id.btnNext, nextPend);

		Intent currIntent = new Intent(
				"com.anioutkazharkova.calendar.small.current");
		currIntent.putExtra("id", widgetId);
		PendingIntent curPend = PendingIntent.getBroadcast(context, widgetId,
				currIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteView.setOnClickPendingIntent(R.id.imToCurrent, curPend);

		manager.updateAppWidget(widgetId, remoteView);
	}

	private static View createHeaderView(Context context, int widgetId,
			final int direct) {
		SharedPreferences preferences = context.getSharedPreferences(
				Utility.WIDGET_PREF_SMALL + widgetId, context.MODE_PRIVATE);
		int fontType = preferences.getInt(Utility.FONT_PREF, 0);
		int themeType = preferences.getInt(Utility.THEME_PREF, 0);

		Theme theme = Utility.themeSelector(themeType);

		Calendar calendar = Calendar.getInstance();
		if (direct != 0) {
			calendar.add(Calendar.DATE, direct);
		}

		String monthName = new SimpleDateFormat("MMM").format(calendar
				.getTime());
		int year = calendar.get(Calendar.YEAR);
		View view = LayoutInflater.from(context).inflate(
				R.layout.small_header_layout, null);
		TextView tvYear, tvMonth;

		tvYear = (TextView) view.findViewById(R.id.tvYear);
		tvMonth = (TextView) view.findViewById(R.id.tvMonth);

		tvYear.setText(year + "");
		tvMonth.setText(monthName.substring(0, 3));

		tvYear.setTextColor(context.getResources().getColor(
				theme.getLabelFont()));
		tvMonth.setTextColor(context.getResources().getColor(
				theme.getLabelFont()));

		tvYear.setTypeface(Utility.createTypeface(context,
				Utility.fontSelector(fontType)));
		tvMonth.setTypeface(Utility.createTypeface(context,
				Utility.fontSelector(fontType)));

		return view;
	}

	private static View createNewView(Context context, int widgetId,
			final int direct) {
		Calendar calendar = Calendar.getInstance();
		int today = calendar.get(Calendar.DAY_OF_MONTH);
		int thatMonth=calendar.get(Calendar.MONTH);
		int thatYear=calendar.get(Calendar.YEAR);
		
		
		if (direct != 0) {
			calendar.add(Calendar.DATE, direct);
		}
		int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		int curMonth=calendar.get(Calendar.MONTH);
		int curYear=calendar.get(Calendar.YEAR);
		
		String dayName = new SimpleDateFormat("EEEE")
				.format(calendar.getTime());

		SharedPreferences preferences = context.getSharedPreferences(
				Utility.WIDGET_PREF_SMALL + widgetId, context.MODE_PRIVATE);
		int fontType = preferences.getInt(Utility.FONT_PREF, 0);
		int themeType = preferences.getInt(Utility.THEME_PREF, 0);
		Theme theme = Utility.themeSelector(themeType);

		boolean showHighlight = preferences.getBoolean(Utility.SHOW_SELECTION,
				true);

		View view = LayoutInflater.from(context).inflate(
				R.layout.single_calendar_day, null);
		TextView tvDay, tvDayWeek;
		LinearLayout layoutDay;

		layoutDay = (LinearLayout) view.findViewById(R.id.layoutDay);
		ImageView imAccent = (ImageView) view.findViewById(R.id.imAccent);
		if (today == currentDay && thatMonth==curMonth && thatYear==curYear) {
			imAccent.setVisibility(View.VISIBLE);
			imAccent.getDrawable().setColorFilter(
					new PorterDuffColorFilter(context.getResources().getColor(
							theme.getAccentColor()), PorterDuff.Mode.MULTIPLY));
		} else {
			imAccent.setVisibility(View.INVISIBLE);
		}

		if (showHighlight) {
			if (theme.getLightColor() != 0) {
				layoutDay.setBackgroundColor(context.getResources().getColor(
						theme.getLightColor()));
			} else {
				layoutDay.setBackgroundColor(context.getResources().getColor(
						android.R.color.transparent));
			}
		}
		else {
			layoutDay.setBackgroundColor(context.getResources().getColor(
					R.color.white));
		}
		tvDay = (TextView) view.findViewById(R.id.tvDay);
		tvDayWeek = (TextView) view.findViewById(R.id.tvDayWeek);

		tvDay.setText(currentDay + "");
		tvDay.setTextColor(context.getResources().getColor(
				theme.getRegularFont()));
		tvDayWeek.setTextColor(context.getResources().getColor(
				theme.getWeekendFont()));

		tvDayWeek.setText(dayName);

		tvDay.setTypeface(Utility.createTypeface(context,
				Utility.fontSelector(fontType)));
		tvDayWeek.setTypeface(Utility.createTypeface(context,
				Utility.fontSelector(fontType)));

		return view;
	}

	private void resetCounters(int widgetId, Context context, int shift) {
		int direct = 0;
		SharedPreferences preferences = context.getSharedPreferences(
				Utility.WIDGET_PREF_SMALL + widgetId, context.MODE_PRIVATE);
		int fontType = preferences.getInt(Utility.FONT_PREF, 0);
		int selectedTheme = preferences.getInt(Utility.THEME_PREF, 0);
		direct = preferences.getInt(Utility.COUNTER_PREF, direct);
		if (shift == 0) {
			direct = initialDirect;
		} else {
			direct = direct + shift;
		}

		Editor edit = preferences.edit();
		edit.putInt(Utility.FONT_PREF, fontType);
		edit.putInt(Utility.THEME_PREF, selectedTheme);
		edit.putInt(Utility.COUNTER_PREF, direct);
		edit.commit();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		if (intent == null)
			return;
		ComponentName thisComponentName = new ComponentName(context,
				SmallCalendarWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		int[] widgetIds = manager.getAppWidgetIds(thisComponentName);
		if (intent.getAction().equals(
				"com.anioutkazharkova.calendar.nextSmallCalendar")) {
			int widgetId = intent.getIntExtra("id", -1);
			if (widgetId != -1) {
				resetCounters(widgetId, context, 1);
				updateView(context, manager, widgetId);
			}
		} else if (intent.getAction().equals(
				"com.anioutkazharkova.calendar.previousSmallCalendar")) {
			int widgetId = intent.getIntExtra("id", -1);

			if (widgetId != -1) {
				resetCounters(widgetId, context, -1);
				updateView(context, manager, widgetId);
			}
		} else if (intent.getAction().equals(
				"com.anioutkazharkova.calendar.small.current")) {
			int widgetId = intent.getIntExtra("id", -1);

			if (widgetId != -1) {
				resetCounters(widgetId, context, initialDirect);
				updateView(context, manager, widgetId);
			}
		} else if (intent.getAction().equals(
				"android.intent.action.DATE_CHANGED")
				|| intent.getAction().equals("android.intent.action.TIME_SET")
				|| intent.getAction().equals(
						"android.intent.action.TIMEZONE_CHANGED")) {

			for (int widgetId : widgetIds) {
				resetCounters(widgetId, context, initialDirect);
				updateView(context, manager, widgetId);
			}
		}
	}

}
