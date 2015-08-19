package com.anioutkazharkova.calendar;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

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
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TableLayout;
import android.widget.TextView;

public class CalendarWidget extends AppWidgetProvider {

	private static boolean isFirstStart = true;
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

		/*
		 * if (DataHolder.WIDGET_STYLES == null) { DataHolder.WIDGET_STYLES =
		 * new HashMap<Integer, WidgetStyle>(); for (int id : appWidgetIds) {
		 * DataHolder.WIDGET_STYLES.put(id, new WidgetStyle()); } } else for
		 * (int widgetId : appWidgetIds) { if
		 * (DataHolder.WIDGET_STYLES.get(widgetId) == null) {
		 * DataHolder.WIDGET_STYLES.put(widgetId, new WidgetStyle()); } }
		 */
		for (int widgetId : appWidgetIds) {
			updateView(context, appWidgetManager, widgetId);
			// updateTime(context, appWidgetManager, widgetId);
		}
		// super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	public static void updateTime(Context context, AppWidgetManager manager,
			int widgetId) {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.HOUR_OF_DAY) == 24
				&& calendar.get(Calendar.MINUTE) == 0
				&& calendar.get(Calendar.SECOND) == 0 || isFirstStart) {
			if (isFirstStart)
				isFirstStart = false;

			// if (initialDirect==direct)
			// {
			updateView(context, manager, widgetId);
			// }
		}
	}

	public static void updateView(Context context, AppWidgetManager manager,
			int widgetId) {

		int direct = 0;
		SharedPreferences preferences = context.getSharedPreferences(
				Utility.WIDGET_PREF + widgetId, context.MODE_PRIVATE);
		int fontType = preferences.getInt(Utility.FONT_PREF, 0);
		int selectedTheme = preferences.getInt(Utility.THEME_PREF, 0);
		direct = preferences.getInt(Utility.COUNTER_PREF, direct);
		Theme theme = Utility.themeSelector(selectedTheme);
		boolean showHighlight = preferences.getBoolean(Utility.SHOW_SELECTION,
				true);
		RemoteViews remoteView = new RemoteViews(context.getPackageName(),
				R.layout.activity_calendar);

		remoteView.setInt(R.id.commonLayout, "setBackgroundResource",
				theme.getBackground());

		View view = setNewView(direct, context, widgetId);
		Bitmap bitmap = Utility.createDrawableFromView(context, view);
		remoteView.setImageViewBitmap(R.id.calendarLayout, bitmap);
		Utility.unbindDrawables(view);
		view = null;

		MonthEntity currentMonth = new MonthEntity(direct, context
				.getResources().getStringArray(R.array.monthes));

		View headerView = getHeaderView(context, widgetId);
		Bitmap headerBitmap = Utility.createDrawableFromView(context,
				headerView);
		remoteView.setImageViewBitmap(R.id.tvMonthYear, headerBitmap);
		Utility.unbindDrawables(headerView);
		headerView = null;

		Intent confIntent = new Intent(context, ConfActivity.class);
		confIntent.putExtra("id", widgetId);
		confIntent.putExtra("prefix", Utility.WIDGET_PREF);
		confIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		remoteView.setOnClickPendingIntent(R.id.tvMonthYear, PendingIntent
				.getActivity(context, widgetId, confIntent,
						PendingIntent.FLAG_UPDATE_CURRENT));
		if (showHighlight) {
		if (theme.getLightColor() != 0) {
			
				remoteView.setInt(R.id.layoutDays, "setBackgroundColor",
						context.getResources().getColor(theme.getLightColor()));
			}

			else {
				remoteView.setInt(R.id.layoutDays, "setBackgroundColor",
						context.getResources().getColor(android.R.color.transparent));
			}
		}
		else
		{
			remoteView.setInt(R.id.layoutDays, "setBackgroundColor",
					context.getResources().getColor(R.color.white));
		}

		remoteView.setInt(R.id.headerLayout, "setBackgroundColor", context
				.getResources().getColor(theme.getHeaderColor()));
		remoteView.setInt(R.id.divider, "setBackgroundColor", context
				.getResources().getColor(theme.getDividerColor()));
		remoteView.setInt(R.id.btnPrev, "setColorFilter", context.getResources().getColor(theme.getArrowFont()));
		remoteView.setInt(R.id.btnNext, "setColorFilter", context.getResources().getColor(theme.getArrowFont()));

		Intent prevIntent = new Intent(
				"com.anioutkazharkova.calendar.previousCalendar");
		prevIntent.putExtra("id", widgetId);
		PendingIntent prevPend = PendingIntent.getBroadcast(context, widgetId,
				prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteView.setOnClickPendingIntent(R.id.btnPrev, prevPend);
		Intent nextIntent = new Intent(
				"com.anioutkazharkova.calendar.nextCalendar");
		nextIntent.putExtra("id", widgetId);
		PendingIntent nextPend = PendingIntent.getBroadcast(context, widgetId,
				nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteView.setOnClickPendingIntent(R.id.btnNext, nextPend);
		manager.updateAppWidget(widgetId, remoteView);
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		
		if (headerBitmap != null) {
			headerBitmap.recycle();
			headerBitmap = null;
		}
		System.gc();
	}

	private static View getHeaderView(Context context, int widgetId) {
		int direct = 0;
		SharedPreferences preferences = context.getSharedPreferences(
				Utility.WIDGET_PREF + widgetId, context.MODE_PRIVATE);
		int fontType = preferences.getInt(Utility.FONT_PREF, 0);
		int selectedTheme = preferences.getInt(Utility.THEME_PREF, 0);
		direct = preferences.getInt(Utility.COUNTER_PREF, direct);
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
		tvMonth.setText(currentMonth.getMonthName() + "");
		tvYear.setText(currentMonth.getYear() + "");
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
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);

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

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		if (intent == null)
			return;

		ComponentName thisComponentName = new ComponentName(context,
				CalendarWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		int[] widgetIds = manager.getAppWidgetIds(thisComponentName);
		if (intent.getAction() == "com.anioutkazharkova.calendar.previousCalendar") {

			int widgetId = intent.getIntExtra("id", -1);
			if (widgetId != -1) {
				resetCounters(widgetId, context, -1);
				updateView(context, manager, widgetId);
			}

		} else if (intent.getAction() == "com.anioutkazharkova.calendar.nextCalendar") {
			// direct = direct + 1;

			int widgetId = intent.getIntExtra("id", -1);

			if (widgetId != -1) {
				resetCounters(widgetId, context, 1);
				updateView(context, manager, widgetId);
			}

		}
		// добавить реакцию на data_changed, time_changed time_zone_changed
		else if (intent.getAction()
				.equals("android.intent.action.DATE_CHANGED")
				|| intent.getAction().equals("android.intent.action.TIME_SET")
				|| intent.getAction().equals(
						"android.intent.action.TIMEZONE_CHANGED")) {

			for (int widgetId : widgetIds) {
				resetCounters(widgetId, context, initialDirect);
				updateView(context, manager, widgetId);
			}
		}
	}

	private void resetCounters(int widgetId, Context context, int shift) {
		int direct = 0;
		SharedPreferences preferences = context.getSharedPreferences(
				Utility.WIDGET_PREF + widgetId, context.MODE_PRIVATE);
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

	private static View setNewView(final int direct, Context context,
			int widgetId) {

		SharedPreferences preferences = context.getSharedPreferences(
				Utility.WIDGET_PREF + widgetId, context.MODE_PRIVATE);
		int fontType = preferences.getInt(Utility.FONT_PREF, 0);
		int selectedTheme = preferences.getInt(Utility.THEME_PREF, 0);
		Theme theme = Utility.themeSelector(selectedTheme);

		View view = LayoutInflater.from(context).inflate(R.layout.month_layout,
				null);

		LinearLayout table;
		table = (LinearLayout) view.findViewById(R.id.table);

		String[] dayNames = context.getResources().getStringArray(
				R.array.days_of_week);
		LinearLayout row = new LinearLayout(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT,
				TableLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 0, 0, 0);

		row.setLayoutParams(layoutParams);

		for (String day : dayNames) {
			View dayView = LayoutInflater.from(context).inflate(
					R.layout.day_item, null);
			dayView.setLayoutParams(new ViewGroup.LayoutParams(Utility.DpToPx(
					context, 30), LinearLayout.LayoutParams.WRAP_CONTENT));

			TextView text = (TextView) dayView.findViewById(R.id.tvDayNumber);

			text.setTextColor(context.getResources().getColor(
					theme.getWeekendFont()));
			text.setText(day);
			text.setAllCaps(true);
			text.setTextSize(12);

			text.setTypeface(Utility.createTypeface(context,
					Utility.fontSelector(fontType)));
			row.addView(dayView);

		}
		table.addView(row);

		MonthEntity currentMonth = new MonthEntity(direct, context
				.getResources().getStringArray(R.array.monthes));
		DayEntity[][] monthArray = null;
		try {
			monthArray = new MonthTask(currentMonth).executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < Utility.WEEKS_NUMBER; i++) {
			row = new LinearLayout(context);
			layoutParams = new LinearLayout.LayoutParams(Utility.DpToPx(
					context, 210), TableLayout.LayoutParams.WRAP_CONTENT);
			row.setLayoutParams(layoutParams);
			for (int j = 0; j < Utility.DAYS_NUMBER; j++) {
				DayEntity dayEntity = monthArray[i][j];
				int day = dayEntity.getDay();
				View dayView = LayoutInflater.from(context).inflate(
						R.layout.day_item, null);
				ImageView image = (ImageView) dayView.findViewById(R.id.imDay);
				TextView text = (TextView) dayView
						.findViewById(R.id.tvDayNumber);
				text.setTypeface(Utility.createTypeface(context,
						Utility.fontSelector(fontType)));

				if (dayEntity.getDAY_TYPE() == DayType.REGULAR) {
					text.setTextColor(context.getResources().getColor(
							theme.getRegularFont()));
				} else if (dayEntity.getDAY_TYPE() == DayType.NOT_CURRENT) {
					text.setTextColor(context.getResources().getColor(
							theme.getNonCurrentFont()));
				} else if (dayEntity.getDAY_TYPE() == DayType.WEEKEND) {
					text.setTextColor(context.getResources().getColor(
							theme.getWeekendFont()));
				}

				if (currentMonth.isCurrentMonth()
						&& day == currentMonth.getToday()
						&& dayEntity.getDAY_TYPE() != DayType.NOT_CURRENT) {
					image.setImageDrawable(context.getResources().getDrawable(
							theme.getDaySelected()));
					if (!theme.getIsStroke()) {
						image.getDrawable().setColorFilter(
								context.getResources().getColor(
										theme.getAccentColor()),
								PorterDuff.Mode.SRC_IN);
					} else {
						image.getDrawable().setColorFilter(
								context.getResources().getColor(
										theme.getAccentColor()),
								PorterDuff.Mode.MULTIPLY);

					}
					if (theme.getThemeType() != ThemeType.TRANSPARENT) {
						if (!theme.getIsStroke()) {
							text.setTextColor(context.getResources().getColor(
									theme.getLabelFont()));
						} else {
							text.setTextColor(context.getResources().getColor(
									theme.getRegularFont()));
						}
					} else {
						text.setTextColor(context.getResources().getColor(
								R.color.dark));
					}

				} else {

					image.setBackgroundResource(R.drawable.day_background);

				}
				text.setText(day + "");
				text.setTextSize(14);

				row.addView(dayView);

			}
			table.addView(row);
		}

		return view;
	}

}
