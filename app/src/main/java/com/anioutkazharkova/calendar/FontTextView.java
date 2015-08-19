package com.anioutkazharkova.calendar;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class FontTextView extends TextView {
	
	private Typeface font;

	public FontTextView(Context context) {
		super(context,null,0);
		// TODO Auto-generated constructor stub
	}
	public FontTextView(Context context,AttributeSet attrs) {
		super(context,attrs,0);
		// TODO Auto-generated constructor stub
	}
	public FontTextView(Context context,AttributeSet attrs,int style) {
		super(context,attrs,style);
		// TODO Auto-generated constructor stub
	}
	
	public void setCustomFont(int fontType,int fontStyle)
	{
		String fontName=Utility.fontSelector(fontType);
		font=Utility.createTypeface(getContext(),fontName);
		setTypeface(font,fontStyle);
	}
	
	

}
