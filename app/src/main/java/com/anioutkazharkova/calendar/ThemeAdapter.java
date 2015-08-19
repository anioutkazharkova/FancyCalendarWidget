package com.anioutkazharkova.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ThemeAdapter extends ArrayAdapter<String> {

	private String[]  themes;
	private Context context;

	public ThemeAdapter(Context context, int resourceId,
	     String[] objects) {
	     super(context, resourceId, objects);
	     this.themes = objects;
	     this.context = context;
	     
	}

	@Override
	public View getDropDownView(int position, View convertView,
	    ViewGroup parent) {
	    return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {

	LayoutInflater inflater=(LayoutInflater) context.getSystemService(  Context.LAYOUT_INFLATER_SERVICE );
	View row=inflater.inflate(R.layout.theme_element_layout, parent, false);
	Theme theme=Utility.themeSelector(position);
	ImageView header=(ImageView)row.findViewById(R.id.header);
	
	ImageView accent=(ImageView)row.findViewById(R.id.accent);
	TextView tvName=(TextView)row.findViewById(R.id.tvThemeName);
	RelativeLayout back=(RelativeLayout) row.findViewById(R.id.themeBack);
	if (position==0)
	{
		
		back.setBackgroundDrawable(context.getResources().getDrawable(theme.getBackground()));
		header.setBackgroundColor(context.getResources().getColor(theme.getHeaderColor()));
		accent.setBackgroundColor(context.getResources().getColor(theme.getAccentColor()));
	}
	else
	{
		back.setBackgroundDrawable(null);
		header.setBackgroundColor(context.getResources().getColor(theme.getHeaderColor()));
		accent.setBackgroundColor(context.getResources().getColor(theme.getAccentColor()));
	}
	tvName.setText(themes[position]);
	return row;
	}
}
