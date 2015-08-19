package com.anioutkazharkova.calendar;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class Utility {

	public static final String SELECT_THEME = "select_theme";

	public static final String WIDGET_PREF = "widget_pref";
	public static final String WIDGET_PREF_SMALL = "small_widget_pref";
	public static final String WIDGET_PREF_EXTENDED = "extended_widget_pref";
	public static final String LIST_PREF="list_widget_pref";
	
	public static final String FONT_PREF="font_pref";
	public static final String THEME_PREF="theme_pref";
	public static final String COUNTER_PREF="counter_pref";
	public static final String SHOW_SELECTION="highlighter_pref";

	public static final String CALEND_PERIOD="calend_period_pref";
	public static final String CURRENT_DAY = "cur_day_pref"; 

	public static Theme CurrentTheme = themeSelector(0);

	public static int WEEKS_NUMBER = 6;
	public static int DAYS_NUMBER = 7;
	public static int TOTAL_DAYS = WEEKS_NUMBER * DAYS_NUMBER;

	private static Typeface font;

	public static void setFont(Typeface newFont)
	{
		font=newFont;
	}
	
	public static String[] fontNames={"sheep sans.ttf","Roboto-Light.ttf","Roboto-Regular.ttf","Hattori Hanzo.otf","roboto_condensed_regular.ttf","taurus-normal.ttf","ubuntu.ttf","Yagora.ttf","uk_caslon.ttf","Cuprum-Regular.ttf","Comfortaa-Regular.ttf","helios-cond-light.ttf"};
	public static Typeface getFont(Context context)
	{
		if (font==null)
		{
			font=createTypeface(context, fontSelector(0));
		}
		return font;
	}
	public static Theme themeSelector(int themeType)
	{
		ThemeType type=ThemeType.TRANSPARENT;
		switch(themeType)
		{
		case 0:
			type=ThemeType.TRANSPARENT;
			break;
		case 1:
			type=ThemeType.PINK;
			break;
		case 2:
			type=ThemeType.PURPLE;
			break;
		case 3:
			type=ThemeType.RED;
			break;
		case 4:
			type=ThemeType.BLUE;
			break;
		case 5:
			type=ThemeType.CYAN;
			break;
		case 6:
			type=ThemeType.ORANGE;
			break;
		case 7:
			type=ThemeType.YELLOW;
			break;
		case 8:
			type=ThemeType.GRAY;
			break;
		case 9:
			type=ThemeType.GREEN;
			break;
		case 10:
			type=ThemeType.JOSEPHINE;
			break;
		case 11:
			type=ThemeType.CONTRAST;
			break;
		case 12:
			type=ThemeType.CHOCO;
			break;
		}
		
		return new Theme(type);
	}
	
	public static Bitmap createDrawableFromView(Context context, View view)  {
		// return (Bitmap)new CreateBitmapTask(context, view).Execute().Get();
		Bitmap bitmap = null;
		WeakReference<View> viewRef=new WeakReference<View>(view);
		DrawBitmapTask task= new DrawBitmapTask(context, viewRef);
		try {
			bitmap=task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}

	public static void  unbindDrawables(View view){
		if (view!=null)
		{
			if (view.getBackground()!=null)
			{
				view.getBackground().setCallback(null);
			}
			if ((view instanceof ViewGroup) && !(view instanceof AdapterView))
			{
				try{
				for(int i=0;i<((ViewGroup)view).getChildCount();i++)
				{
					unbindDrawables(((ViewGroup)view).getChildAt(i));
				}
				}
				catch(Exception e)
				{
					
				}
				try{
					((ViewGroup)view).removeAllViews();
				}
				catch(Exception e)
				{
					
				}
			}
			else
			{
				
				view=null;				
			}
		}
		
	}
	
	public static String fontSelector(int fontType) {

		String fontName = "";
		if (fontType < fontNames.length) {
			fontName = fontNames[fontType];
		}
		return fontName;
	}

	public static Typeface createTypeface(Context context, String fontName) {
		return Typeface.createFromAsset(context.getAssets(), fontName);
	}
	public static int DpToPx(Context context, int value) {
		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		float scale = displayMetrics.density;
		int pixels = (int) (value * scale + 0.5f);
		return pixels;

	}
}
