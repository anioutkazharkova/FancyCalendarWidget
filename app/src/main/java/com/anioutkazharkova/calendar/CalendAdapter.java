package com.anioutkazharkova.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CalendAdapter extends  ArrayAdapter<String>{

private String[] calends;
private int[] days={1,7,30};
private Context context;

public CalendAdapter(Context context, int resourceId,
     String[] objects) {
     super(context, resourceId, objects);
     this.calends = objects;
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
TextView textView=(TextView) row.findViewById(R.id.tvType);

textView.setText(calends[position]);
return row;
}

}