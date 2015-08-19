package com.anioutkazharkova.calendar;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

public class DrawBitmapTask extends AsyncTask<Void, Void, Bitmap> {
	private Context mContext;
	private WeakReference<View> viewRef;

	public DrawBitmapTask() {
		// TODO Auto-generated constructor stub
	}

	public DrawBitmapTask(Context context, WeakReference<View> viewRef) {
		mContext = context;
		this.viewRef = viewRef;
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		
		// TODO Auto-generated method stub
		return createBitmapFromView();
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