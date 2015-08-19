package com.anioutkazharkova.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.widget.RemoteViewsService.RemoteViewsFactory;
import android.widget.TextView;



	public class ListProvider implements RemoteViewsFactory {
		
		private ArrayList listItemList = new ArrayList();
		private Context context = null;
		private int appWidgetId;
		Theme theme=Utility.themeSelector(6);
		 int fontType=0;
		 int period=1;
		private SharedPreferences preferences;
		 
		public ListProvider(Context context, Intent intent) {
		this.context = context;
		appWidgetId = intent.getIntExtra("widgetId",
		-1);
		SharedPreferences preferences = context.getSharedPreferences(
				Utility.LIST_PREF + appWidgetId, context.MODE_PRIVATE);
		fontType = preferences.getInt(Utility.FONT_PREF, 0);
		int selectedTheme = preferences.getInt(Utility.THEME_PREF, 0);		
		period=preferences.getInt(Utility.CALEND_PERIOD, period);
		
		theme = Utility.themeSelector(selectedTheme);
		
		}
		public ListProvider(Context context, Intent intent,List<EventItem> events) {
			this.context = context;
			appWidgetId = intent.getIntExtra("widgetId",
			-1);
			preferences = context.getSharedPreferences(
					Utility.LIST_PREF + appWidgetId, context.MODE_PRIVATE);
			fontType = preferences.getInt(Utility.FONT_PREF, 0);
			int selectedTheme = preferences.getInt(Utility.THEME_PREF, 0);		
			period=preferences.getInt(Utility.CALEND_PERIOD, period);
			theme = Utility.themeSelector(selectedTheme);
			listItemList=new ArrayList<EventItem>(events);
			}
		 
		
		@Override
		public int getCount() {
		return listItemList.size();
		}
		 
		@Override
		public long getItemId(int position) {
		return position;
		}
		 
	
		@Override
		public RemoteViews getViewAt(int position) {
		final RemoteViews remoteView = new RemoteViews(
		context.getPackageName(), R.layout.list_row);
		EventItem listItem = (EventItem) listItemList.get(position);
		Bitmap headerBitmap=null,bitmap=null;
		SharedPreferences preferences = context.getSharedPreferences(
				Utility.LIST_PREF + appWidgetId, context.MODE_PRIVATE);
		fontType = preferences.getInt(Utility.FONT_PREF, 0);
		int selectedTheme = preferences.getInt(Utility.THEME_PREF, 0);		
		
		theme = Utility.themeSelector(selectedTheme);
		if(listItem.StartDate!="")
		{
			View header=getHeaderView(context,listItem);
			headerBitmap=Utility.createDrawableFromView(context, header);
			
			remoteView.setImageViewBitmap(R.id.imRow, headerBitmap);
			remoteView.setInt(R.id.divider, "setBackgroundColor", context.getResources().getColor(android.R.color.transparent));
		}
		else
		{
			View view=getItemView(context,listItem);
			bitmap=Utility.createDrawableFromView(context, view);
			
			remoteView.setImageViewBitmap(R.id.imRow, bitmap);
			remoteView.setInt(R.id.divider, "setBackgroundColor", context.getResources().getColor(theme.getDividerColor()));
		}
		
		if (headerBitmap!=null)
		{
			//headerBitmap.recycle();
			headerBitmap=null;
		}
		if (bitmap!=null)
		{
			//bitmap.recycle();
			bitmap=null;
		}
		System.gc();
		Intent editIntent=new Intent("com.anioutkazharkova.calendar.editcalendar");		
	    long eventId =listItem.ID; 
	    editIntent.putExtra("event",eventId);
	 
	   if (editIntent!=null)
		remoteView.setOnClickFillInIntent(R.id.imRow, editIntent);
		
		
		return remoteView;
		}
		
		private View getItemView(Context context,EventItem item)
		{
			View view=LayoutInflater.from(context).inflate(R.layout.calend_row_item, null);
			TextView tvTime=(TextView)view.findViewById(R.id.startTime);
			TextView tvTitle=(TextView)view.findViewById(R.id.heading);
			ImageView divider=(ImageView)view.findViewById(R.id.divider);
			//TextView tvDesc=(TextView)view.findViewById(R.id.content);
			
			SimpleDateFormat tt=new SimpleDateFormat("HH:mm");
			String startTime=tt.format(new Date(item.StartTime));
			tvTime.setText(startTime);
			tvTitle.setText(item.Title);
			//tvDesc.setText(item.Text);
			
			tvTime.setTypeface(Utility.createTypeface(context,Utility.fontSelector(fontType)));
			tvTime.setTextColor(context.getResources().getColor(theme.getRegularFont()));
			tvTitle.setTypeface(Utility.createTypeface(context,Utility.fontSelector(fontType)));
			tvTitle.setTextColor(context.getResources().getColor(theme.getRegularFont()));
			//tvDesc.setTypeface(Utility.createTypeface(context,Utility.fontSelector(0)));
			//tvDesc.setTextColor(context.getResources().getColor(theme.getNonCurrentFont()));
			divider.setBackgroundColor(context.getResources().getColor(theme.getAccentColor()));
			return view;
		}
		
		private View getHeaderView(Context context,EventItem item)
		{
			View header=LayoutInflater.from(context).inflate(R.layout.calend_header_item, null);
			TextView tvDate=(TextView)header.findViewById(R.id.startDate);
			tvDate.setText(item.StartDate);
			ImageView divider=(ImageView)header.findViewById(R.id.divider);
			LinearLayout layout=(LinearLayout)header.findViewById(R.id.layout);
			if (theme.getThemeType()!=ThemeType.TRANSPARENT)
			{
				if (theme.getThemeType()==ThemeType.CONTRAST)
				{
					layout.setBackgroundColor(context.getResources().getColor(theme.getAccentColor()));
				}
				else
			layout.setBackgroundColor(context.getResources().getColor(theme.getLightColor()));
			}
			else
				layout.setBackgroundColor(context.getResources().getColor(R.color.white_transparent));
			
			
			tvDate.setTypeface(Utility.createTypeface(context,Utility.fontSelector(fontType)));
			if (theme.getThemeType()!=ThemeType.TRANSPARENT){
				if (theme.getThemeType()==ThemeType.JOSEPHINE)
				{
					tvDate.setTextColor(context.getResources().getColor(theme.getAccentColor()));
				}
				else
			tvDate.setTextColor(context.getResources().getColor(theme.getHeaderColor()));
			}
			else
			{
				tvDate.setTextColor(context.getResources().getColor(R.color.white));
			}
			//divider.setBackgroundColor(context.getResources().getColor(theme.getDividerColor()));
			
			return header;
		}

		
		@Override
		public RemoteViews getLoadingView() {
			return null;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public void onCreate() {
		}

		@Override
		public void onDataSetChanged() {
			period=preferences.getInt(Utility.CALEND_PERIOD, period);
			try {
				listItemList=(ArrayList) new GetEventsTask(context, period).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		@Override
		public void onDestroy() {
		}
		}


