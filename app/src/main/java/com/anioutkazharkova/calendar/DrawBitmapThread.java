package com.anioutkazharkova.calendar;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

public class DrawBitmapThread extends Thread {
	
	private Context mContext;
	private WeakReference<View> viewRef;
	private WeakReference<RemoteViews> remoteRef;
	
	public DrawBitmapThread(){}
	public DrawBitmapThread(Context context,WeakReference<View> viewRef,WeakReference<RemoteViews> remoteRef){
		this.mContext=context;
		this.viewRef=viewRef;
		this.remoteRef=remoteRef;
	}
@Override
public void run() {
	// TODO Auto-generated method stub
	Bitmap bitmap=createBitmapFromView();
	
	if (remoteRef!=null && bitmap!=null)
	{		
		remoteRef.get().setImageViewBitmap(R.id.calendarLayout, bitmap);
	}
	if (bitmap!=null)
	{
		bitmap.recycle();
		bitmap=null;
	}
}
	
	private Bitmap createBitmapFromView() {
		if (viewRef != null) {
			View view = viewRef.get();
			DisplayMetrics displayMetrics = mContext.getResources()
					.getDisplayMetrics();
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);

			view.setLayoutParams(param);
			view.measure(displayMetrics.widthPixels,
					displayMetrics.heightPixels);
			view.layout(0, 0, displayMetrics.widthPixels,
					displayMetrics.heightPixels);
			view.buildDrawingCache();

			Bitmap bitmap = Bitmap.createBitmap(viewRef.get().getMeasuredWidth(),
					view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			view.draw(canvas);
			view.destroyDrawingCache();
			
			return bitmap;
		} else {
			return null;
		}
	}
	
	

}
