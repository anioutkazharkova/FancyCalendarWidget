package com.anioutkazharkova.calendar;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class FontTypesAdapter extends ArrayAdapter<String>{

private String[] fonts=Utility.fontNames;
private Context context;

public FontTypesAdapter(Context context, int resourceId,
     String[] objects) {
     super(context, resourceId, objects);
     this.fonts = objects;
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
View row=inflater.inflate(R.layout.font_type_element, parent, false);
FontTextView textView=(FontTextView) row.findViewById(R.id.tvType);
textView.setCustomFont(position,Typeface.NORMAL);
textView.setText(context.getResources().getString(R.string.font_example));
return row;
}

}
