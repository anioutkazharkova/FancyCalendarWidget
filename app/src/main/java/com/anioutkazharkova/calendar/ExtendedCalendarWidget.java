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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract.Calendars;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

public class ExtendedCalendarWidget extends AppWidgetProvider {

	private static boolean isFirstStart = true;
	private static final int initialDirect = 0;
	private static DayEntity currentDay;
	
	
	// private static int direct = 0;
	private PendingIntent timePendIntent;
	private AlarmManager timeManager;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Intent serviceIntent = new Intent(context, TimeServiceExtended.class);
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

	public static void updateTime(Context context, AppWidgetManager manager,
			int widgetId) {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.HOUR_OF_DAY) == 24
				&& calendar.get(Calendar.MINUTE) == 0
				&& calendar.get(Calendar.SECOND) == 0 || isFirstStart) {
			if (isFirstStart)
				isFirstStart = false;

			updateView(context, manager, widgetId);
			
		}
	}

	public static void updateView(Context context, AppWidgetManager manager,
			int widgetId) {

		int direct = 0;
		SharedPreferences preferences = context.getSharedPreferences(
				Utility.WIDGET_PREF_EXTENDED + widgetId, context.MODE_PRIVATE);
		int fontType = preferences.getInt(Utility.FONT_PREF, 0);
		int selectedTheme = preferences.getInt(Utility.THEME_PREF, 0);
		direct = preferences.getInt(Utility.COUNTER_PREF, direct);
		int currentDayId=-1;
		Theme theme = Utility.themeSelector(selectedTheme);
		boolean showHighlight = preferences.getBoolean(Utility.SHOW_SELECTION,
				true);
		RemoteViews remoteView = new RemoteViews(context.getPackageName(),
				R.layout.activity_calendar_extended);

		remoteView.setInt(R.id.commonLayout, "setBackgroundResource",
				theme.getBackground());
		
	//	remoteView.setInt(R.id.layoutDayManage, "setBackgroundColor",
//				context.getResources().getColor(theme.getHeaderColor()));
		

		View view = setNewView(direct, context, widgetId);
		Bitmap bitmap = Utility.createDrawableFromView(context, view);
		remoteView.setImageViewBitmap(R.id.layoutDays, bitmap);
		Utility.unbindDrawables(view);
		view = null;

		MonthEntity currentMonth = new MonthEntity(direct, context
				.getResources().getStringArray(R.array.monthes));

		DayEntity[][] 
				monthArray = null;
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
		
		try {
			monthArray=new CheckEventsTask(context, monthArray).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < Utility.WEEKS_NUMBER
				; i++) {
			for (int j = 0; j < Utility.DAYS_NUMBER; j++) {
				DayEntity dayEntity = monthArray[i][j];
				Bitmap dayBitmap =null;
				Typeface typeface=(Utility.createTypeface(context,
						Utility.fontSelector(fontType)));
int fontColor=0;
				if (dayEntity.getDAY_TYPE() == DayType.REGULAR) {
					fontColor=(context.getResources().getColor(
							theme.getRegularFont()));
				} else if (dayEntity.getDAY_TYPE() == DayType.NOT_CURRENT) {
					fontColor=(context.getResources().getColor(
							theme.getNonCurrentFont()));
				} else if (dayEntity.getDAY_TYPE() == DayType.WEEKEND) {
					fontColor=(context.getResources().getColor(
							theme.getWeekendFont()));
				}
				if ((currentMonth.isCurrentMonth() & dayEntity.getDay()== currentMonth.getToday()
						&& dayEntity.getDAY_TYPE() != DayType.NOT_CURRENT )) {
					View dayView = getDayNewView(dayEntity,(i)*Utility.DAYS_NUMBER+j,currentDayId, context, widgetId, currentMonth);
					dayBitmap=Utility.createDrawableFromView(context,dayView);
			Utility.unbindDrawables(dayView);
			dayView=null;
				}
				else
				{
				
					
				//Toast.makeText(context, dayEntity.isHasEvents()?"events":"no", Toast.LENGTH_LONG).show();
				 dayBitmap = getFontBitmap(context, dayEntity.getDay()+"",fontType,fontColor,15);
				}	
						
				if (dayBitmap != null) {
					int id = context.getResources().getIdentifier(
							"imDay" + ((i)*Utility.DAYS_NUMBER+j), "id",
							context.getPackageName());
					remoteView.setImageViewBitmap(id, dayBitmap);
					
					Intent dayIntent=new Intent();
					dayIntent.setAction("com.anioutkazharkova.calendar.dayExtended");
					dayIntent.putExtra("month",dayEntity.getMonth());
					dayIntent.putExtra("year", dayEntity.getYear());
					dayIntent.putExtra("day", dayEntity.getDay());
					dayIntent.putExtra("day_id", i*Utility.DAYS_NUMBER+j);
					dayIntent.putExtra("widgetId", widgetId);
					PendingIntent dayPend=PendingIntent.getBroadcast(context,i*Utility.DAYS_NUMBER+j, dayIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				remoteView.setOnClickPendingIntent(id, dayPend);
				dayBitmap=null;
				System.gc();
				int backId = context.getResources().getIdentifier(
						"imBack" + ((i)*Utility.DAYS_NUMBER+j), "id",
						context.getPackageName());
				if (dayEntity.isHasEvents())
				{
					
					remoteView.setViewVisibility(backId, View.VISIBLE);
					remoteView.setInt(backId, "setColorFilter", context.getResources().getColor(theme.getAccentColor()));
					
					
					//remoteView.setInt(id, "setBackgroundResource", R.drawable.evented_day_background);
				
				}
				else
				{
					remoteView.setViewVisibility(backId, View.INVISIBLE);
				}
				}
				
			}
			
			System.gc();
		}
		
		View headerView = getHeaderView(context, widgetId);
		Bitmap headerBitmap = Utility.createDrawableFromView(context,
				headerView);
		remoteView.setImageViewBitmap(R.id.tvMonthYear, headerBitmap);
		Utility.unbindDrawables(headerView);
		headerView = null;

		Intent confIntent = new Intent(context, ConfActivity.class);
		confIntent.putExtra("id", widgetId);
		confIntent.putExtra("prefix", Utility.WIDGET_PREF_EXTENDED);
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
				remoteView.setInt(
						R.id.layoutDays,
						"setBackgroundColor",
						context.getResources().getColor(
								android.R.color.transparent));
			}
		} else {
			remoteView.setInt(R.id.layoutDays, "setBackgroundColor", context
					.getResources().getColor(R.color.white));
		}

		remoteView.setInt(R.id.headerLayout, "setBackgroundColor", context
				.getResources().getColor(theme.getHeaderColor()));
		remoteView.setInt(R.id.divider, "setBackgroundColor", context
				.getResources().getColor(theme.getDividerColor()));
		remoteView.setInt(R.id.btnPrev, "setColorFilter", context.getResources().getColor(theme.getArrowFont()));
		remoteView.setInt(R.id.btnNext, "setColorFilter", context.getResources().getColor(theme.getArrowFont()));
		
		Intent prevIntent = new Intent(
				"com.anioutkazharkova.calendar.previousExtendedCalendar");
		prevIntent.putExtra("id", widgetId);
		PendingIntent prevPend = PendingIntent.getBroadcast(context, widgetId,
				prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteView.setOnClickPendingIntent(R.id.btnPrev, prevPend);
		Intent nextIntent = new Intent(
				"com.anioutkazharkova.calendar.nextExtendedCalendar");
		nextIntent.putExtra("id", widgetId);
		PendingIntent nextPend = PendingIntent.getBroadcast(context, widgetId,
				nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteView.setOnClickPendingIntent(R.id.btnNext, nextPend);
		manager.updateAppWidget(widgetId, remoteView);
		/*
		 * if (bitmap != null) { bitmap.recycle(); bitmap = null; }
		 */
		
		if (headerBitmap != null) {
			headerBitmap.recycle();
			headerBitmap = null;
		}
		System.gc();
	}
	public static Bitmap getFontBitmap(Context context, String text, int fontType, int color, float fontSizeSP) {
	    int fontSizePX = Utility.DpToPx(context, (int) fontSizeSP);
	    //color=Color.WHITE;
	    int pad = (fontSizePX / 9);
	    Paint paint = new Paint();
	    Typeface typeface = Utility.createTypeface(context,Utility.fontSelector(fontType));
	    paint.setAntiAlias(true);
	    paint.setTypeface(typeface);
	    paint.setColor(color);
	    paint.setTextSize(fontSizePX);

	    int textWidth = (int) (paint.measureText(text) + pad * 2);
	    int height = (int) (fontSizePX / 0.75);
	    Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_4444);
	    Canvas canvas = new Canvas(bitmap);
	    float xOriginal = pad;
	    canvas.drawText(text, xOriginal, fontSizePX, paint);
	    return bitmap;
	}

	private static View getHeaderView(Context context, int widgetId) {
		int direct = 0;
		SharedPreferences preferences = context.getSharedPreferences(
				Utility.WIDGET_PREF_EXTENDED + widgetId, context.MODE_PRIVATE);
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
				ExtendedCalendarWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		int[] widgetIds = manager.getAppWidgetIds(thisComponentName);
		if (intent.getAction() == "com.anioutkazharkova.calendar.previousExtendedCalendar") {

			int widgetId = intent.getIntExtra("id", -1);
			if (widgetId != -1) {
				resetCounters(widgetId, context, -1);
				updateView(context, manager, widgetId);
			}

		} else if (intent.getAction() == "com.anioutkazharkova.calendar.nextExtendedCalendar") {
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
		else if (intent.getAction().equals("com.anioutkazharkova.calendar.dayExtended"))
		{
			//Надо в intent класть дату
			int currentDayId=intent.getIntExtra("day_id", -1);
			int day=intent.getIntExtra("day", -1);
			int month=intent.getIntExtra("month", -1);
			int year=intent.getIntExtra("year", -1);
			
			
			
			//Toast.makeText(context,day+" "+month+" "+year, Toast.LENGTH_LONG).show();
			int widgetId=intent.getIntExtra("widgetId", -1);
			SharedPreferences preferences = context.getSharedPreferences(
					Utility.WIDGET_PREF_EXTENDED + widgetId, context.MODE_PRIVATE);
			Editor edit = preferences.edit();
			edit.putInt(Utility.CURRENT_DAY, currentDayId);
			edit.commit();				
			
			
			Calendar cal=Calendar.getInstance();
			cal.set(year, month-1, day);
			Calendar today=Calendar.getInstance();
			today.set(currentDay.getYear(),currentDay.getMonth()-1,currentDay.getDay());
			long time=0;
			
			//if (cal.after(today)|| cal.equals(today))
				time=cal.getTimeInMillis();
			//else
			//	time=today.getTimeInMillis();
			Intent calendIntent=new Intent (Intent.ACTION_VIEW,Uri.parse ("content://com.android.calendar"+
					//Calendars.CONTENT_URI+
					"/time/"+time));
			calendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity (calendIntent);
			//updateView(context, manager, widgetId);
			
			
		}
		else
			if (intent.getAction().equals("android.intent.action.PROVIDER_CHANGED"))
			{
				ComponentName extendedComponent=new ComponentName(context, ExtendedCalendarWidget.class);
				int[]extIds = manager.getAppWidgetIds(extendedComponent);
				//Toast.makeText(context, "calendar", Toast.LENGTH_LONG).show();
				for(int widgetId:extIds)
				updateView(context, manager, widgetId);
			}
	}

	
	
	private void resetCounters(int widgetId, Context context, int shift) {
		int direct = 0;
		SharedPreferences preferences = context.getSharedPreferences(
				Utility.WIDGET_PREF_EXTENDED + widgetId, context.MODE_PRIVATE);
		int fontType = preferences.getInt(Utility.FONT_PREF, 0);
		int selectedTheme = preferences.getInt(Utility.THEME_PREF, 0);
		direct = preferences.getInt(Utility.COUNTER_PREF, direct);
		int currentDayId=preferences.getInt(Utility.CURRENT_DAY, -1);
		if (shift == 0) {
			direct = initialDirect;
		} else {
			direct = direct + shift;
		}

		Editor edit = preferences.edit();
		edit.putInt(Utility.FONT_PREF, fontType);
		edit.putInt(Utility.THEME_PREF, selectedTheme);
		edit.putInt(Utility.COUNTER_PREF, direct);
		edit.putInt(Utility.CURRENT_DAY, currentDayId);
		edit.commit();
	}

	private static View setNewView(final int direct, Context context,
			int widgetId) {

		SharedPreferences preferences = context.getSharedPreferences(
				Utility.WIDGET_PREF_EXTENDED + widgetId, context.MODE_PRIVATE);
		int fontType = preferences.getInt(Utility.FONT_PREF, 0);
		int selectedTheme = preferences.getInt(Utility.THEME_PREF, 0);
		Theme theme = Utility.themeSelector(selectedTheme);

		View view = LayoutInflater.from(context).inflate(
				R.layout.month_layout_extended, null);

		LinearLayout table;
		table = (LinearLayout) view.findViewById(R.id.table);

		String[] dayNames = context.getResources().getStringArray(
				R.array.days_of_week);
		

		for (String day : dayNames) {
			View dayView = LayoutInflater.from(context).inflate(
					R.layout.day_item_extended, null);
			dayView.setLayoutParams(new ViewGroup.LayoutParams(Utility.DpToPx(
					context, 40), LinearLayout.LayoutParams.WRAP_CONTENT));
			ImageView mark=(ImageView)dayView.findViewById(R.id.imEventMark);
			mark.setVisibility(View.GONE);
			TextView text = (TextView) dayView.findViewById(R.id.tvDayNumber);

			text.setTextColor(context.getResources().getColor(
					theme.getWeekendFont()));
			text.setText(day);
			text.setAllCaps(true);
			text.setTextSize(13);

			text.setTypeface(Utility.createTypeface(context,
					Utility.fontSelector(fontType)));
			table.addView(dayView);

		}
		
		return view;
	}

	private static View getDayNewView(DayEntity dayEntity, int dayId,final int currentDayId, Context context,
			int widgetId, MonthEntity currentMonth) {
		SharedPreferences preferences = context.getSharedPreferences(
				Utility.WIDGET_PREF_EXTENDED + widgetId, context.MODE_PRIVATE);
		int fontType = preferences.getInt(Utility.FONT_PREF, 0);
		int selectedTheme = preferences.getInt(Utility.THEME_PREF, 0);

		Theme theme = Utility.themeSelector(selectedTheme);
		int day = dayEntity.getDay();
		View dayView = LayoutInflater.from(context).inflate(
				R.layout.day_item_extended, null);
		ImageView mark=(ImageView)dayView.findViewById(R.id.imEventMark);
		if (dayEntity.isHasEvents())
			{mark.setVisibility(View.VISIBLE);
			mark.getDrawable().setColorFilter(context.getResources().getColor(theme.getAccentColor()), PorterDuff.Mode.SRC_IN);
			}
		else
		mark.setVisibility(View.INVISIBLE);
		ImageView image = (ImageView) dayView.findViewById(R.id.imDay);
		TextView text = (TextView) dayView.findViewById(R.id.tvDayNumber);
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

		if ((currentMonth.isCurrentMonth() && day == currentMonth.getToday()
				&& dayEntity.getDAY_TYPE() != DayType.NOT_CURRENT )) {
			
			currentDay=dayEntity;
			image.setImageDrawable(context.getResources().getDrawable(
					theme.getDaySelected()));
			
			if (dayEntity.isHasEvents())
			{mark.setVisibility(View.VISIBLE);
			
			}
			//currentDayId=dayId;
			//currentDayEntity=dayEntity;
			if (!theme.getIsStroke()) {
				image.getDrawable()
						.setColorFilter(
								context.getResources().getColor(
										theme.getAccentColor()),
								PorterDuff.Mode.SRC_IN);
			} else {
				image.getDrawable()
						.setColorFilter(
								context.getResources().getColor(
										theme.getAccentColor()),
								PorterDuff.Mode.MULTIPLY);

			}
			if (theme.getThemeType() != ThemeType.TRANSPARENT) {
				if (!theme.getIsStroke()) {
					text.setTextColor(context.getResources().getColor(
							theme.getLabelFont()));
					mark.getDrawable().setColorFilter(context.getResources().getColor(theme.getLightColor()), PorterDuff.Mode.SRC_IN);
				} else {
					text.setTextColor(context.getResources().getColor(
							theme.getRegularFont()));
				}
			} else {
				text.setTextColor(context.getResources().getColor(R.color.dark));
				mark.getDrawable().setColorFilter(context.getResources().getColor(R.color.clouds), PorterDuff.Mode.SRC_IN);
			}

		} else {
			/*if ((currentMonth.isCurrentMonth() && day == currentMonth.getToday()
					&& dayEntity.getDAY_TYPE() != DayType.NOT_CURRENT))
					{
				text.setTextColor(context.getResources().getColor(
						theme.getAccentColor()));
					}
*/
			image.setBackgroundResource(R.drawable.day_background);

		}
		text.setText(day + "");
		text.setTextSize(15);

		return dayView;
	}
	
	

}
