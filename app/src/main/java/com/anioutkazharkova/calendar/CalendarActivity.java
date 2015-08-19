package com.anioutkazharkova.calendar;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;


public class CalendarActivity extends Activity {

	
	private TextView tvMonth,tvYear;	
	private ImageView btnPrev,btnNext;
	
	int direct=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_calendar);
      final ImageView calendarLayout=(ImageView)findViewById(R.id.calendarLayout);
      //
      tvMonth=(TextView)findViewById(R.id.tvMonth);
      tvYear=(TextView)findViewById(R.id.tvYear);
      btnPrev=(ImageView)findViewById(R.id.btnPrev);
      btnNext=(ImageView)findViewById(R.id.btnNext);
     btnNext.getDrawable().setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.MULTIPLY);
     btnPrev.getDrawable().setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.MULTIPLY);
     
     btnPrev.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				direct=direct-1;
				View prevView=setNewView(direct);
				calendarLayout.setImageBitmap(Utility.createDrawableFromView(getApplicationContext(), prevView));				
			}
		});
      btnNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				direct=direct+1;
				View prevView=setNewView(direct);
				calendarLayout.setImageBitmap(Utility.createDrawableFromView(getApplicationContext(), prevView));						
			}
		});
      MonthEntity currentMonth=new MonthEntity(direct);
   	DayEntity[][] monthArray=currentMonth.createMonthArray();    
   	tvYear.setText(currentMonth.getYear()+"");
   	tvMonth.setText(currentMonth.getMonthName());
   	calendarLayout.setImageBitmap(Utility.createDrawableFromView(getApplicationContext(), setNewView(direct)));
    }
    
    private View setNewView(final int direct)
    {
    	View view=LayoutInflater.from(this).inflate(R.layout.month_layout, null);
    	LinearLayout table; table=(LinearLayout)view.findViewById(R.id.table);         
         
         String[] dayNames=new String[]{"Mon","Tue","Wen","Thr","Fri","Sat","Sun"};
         LinearLayout row=new LinearLayout(this);
         LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
       layoutParams.setMargins(0, 0, 0, 5);
        
         row.setLayoutParams(layoutParams);
         for(String day:dayNames)
         {
         View dayView=LayoutInflater.from(this).inflate(R.layout.day_item,null);   
         TextView text=(TextView)dayView.findViewById(R.id.tvDayNumber);
         
         text.setTextColor(getResources().getColor(R.color.dark_yellow_transparent));
         text.setText(day);    
         text.setTextSize(14);
         row.addView(dayView);
         
         }
         table.addView(row);
         
         MonthEntity currentMonth=new MonthEntity(direct);
     	DayEntity[][] monthArray=currentMonth.createMonthArray();    
     	tvYear.setText(currentMonth.getYear()+"");
     	tvMonth.setText(currentMonth.getMonthName());
     	for(int i=0;i<Utility.WEEKS_NUMBER;i++)
     	{
     	row=new LinearLayout(this);
     	layoutParams = new LinearLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
         
         row.setLayoutParams(layoutParams);
         for(int j=0;j<Utility.DAYS_NUMBER;j++)
         {
         	DayEntity dayEntity=monthArray[i][j];
         	int day=dayEntity.getDay();
         	 View dayView=LayoutInflater.from(this).inflate(R.layout.day_item,null);  
         	 ImageView image=(ImageView)dayView.findViewById(R.id.imDay);
         	 TextView text=(TextView)dayView.findViewById(R.id.tvDayNumber);
         	if (currentMonth.isCurrentMonth() && day==currentMonth.getToday()&&dayEntity.getDAY_TYPE()!=DayType.NOT_CURRENT)
         	{
         		image.setBackgroundDrawable(getResources().getDrawable(R.drawable.day_selected));
         		// Toast.makeText(getApplicationContext(), currentMonth.isCurrentMonth()+" "+day+" "+currentMonth.getToday(),Toast.LENGTH_LONG).show();
         	
         	}
         	else 
         		{
         		image.setBackgroundResource(R.drawable.day_background);
         		//image.setBackgroundDrawable(null);
         		}
         	if (dayEntity.getDAY_TYPE()==DayType.REGULAR)
         	{
         		text.setTextColor(getResources().getColor(R.color.dark_yellow));
         	}
         	else if (dayEntity.getDAY_TYPE()==DayType.NOT_CURRENT)
         	{
         		text.setTextColor(getResources().getColor(R.color.dark_yellow_transparent));
         	}
         	else if (dayEntity.getDAY_TYPE()==DayType.WEEKEND)
         	{
         		text.setTextColor(getResources().getColor(R.color.orange_red));
         	}
         	
         	 text.setText(day+"");
  	        text.setTextSize(14);
        
         row.addView(dayView);
         
         }
         table.addView(row);//(row,new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
         }
    	return view;
    }
  
    	
    }

    
  


