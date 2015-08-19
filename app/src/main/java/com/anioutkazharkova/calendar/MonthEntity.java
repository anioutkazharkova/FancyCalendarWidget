package com.anioutkazharkova.calendar;

import java.util.Calendar;
import java.util.Locale;
import java.util.jar.JarException;



public class MonthEntity {

	private int daysOffset;
	private int month;
	private String monthName;
	private int monthShift;
	private boolean isCurrentMonth;
	private int today;
	private int year;
	int currentDayOfWeek;
	private String currentDayName;
	private String currentMonthName;
  	int currentMonth;
  	private int currentDay;
  	private DayEntity[][] MonthDays;
  	private String[] monthes;//=new String[]{"January","February","March","April","May","June","July","August","September","October",
            //"November","December"};
  	
  	

	public int getDaysOffset() {
		return daysOffset;
	}

	public void setDaysOffset(int daysOffset) {
		this.daysOffset = daysOffset;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}

	public int getMonthShift() {
		return monthShift;
	}

	public void setMonthShift(int monthShift) {
		this.monthShift = monthShift;
	}
	
	public MonthEntity(){
		daysOffset=0;
		monthShift=0;
		isCurrentMonth=true;
		MonthDays=new DayEntity[Utility.WEEKS_NUMBER][Utility.DAYS_NUMBER];
	}
	
	public MonthEntity(int monthShift)
	{
		this();
		this.monthShift=monthShift;
		if (monthShift==0)
		{
			isCurrentMonth=true;
		}
		else
			isCurrentMonth=false;
	}
	public MonthEntity(int monthShift,String[] monthes)
	{
		this(monthShift);
		this.monthes=monthes;
	}
	
	public DayEntity[][] createMonthArray() throws java.lang.NumberFormatException
	{
		Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
		if (monthShift!=0){
  	  currentCalendar.add(Calendar.MONTH,monthShift);
		}
  	currentDayOfWeek=(currentCalendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)?6:(currentCalendar.get(Calendar.DAY_OF_WEEK)-1);
  	
  	currentMonth=currentCalendar.get(Calendar.MONTH)+1;
  	setMonthName(monthes[currentMonth-1]);
  	currentDay=currentCalendar.get(Calendar.DAY_OF_MONTH);
  	int daysInMonth=currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
  	 setYear(currentCalendar.get(Calendar.YEAR));
  	
  	int[][] daysArray=createBaseMonthArray(daysInMonth);
  	daysArray=shiftDaysArray(currentDay, daysArray);
  	daysArray=fillOffsetDays(daysArray, daysInMonth);
		
	return MonthDays;
	}
	
	private int[][] createBaseMonthArray(int daysInMonth)
	{
		int[][] daysArray=new int[Utility.WEEKS_NUMBER][Utility.DAYS_NUMBER];
    	for(int day=0;day<daysInMonth;day++)
    	{
    		int row=day/Utility.DAYS_NUMBER;
    		int column=(day-Utility.DAYS_NUMBER*row);
    		daysArray[row][column]=day+1;
    		
    	}
    	return daysArray;
	}
	
	private int[][] shiftDaysArray(int currentDay,int[][] daysArray) throws java.lang.NumberFormatException
	{
		/*int column=(currentDay%Utility.DAYS_NUMBER)-1;
    	 daysOffset=(currentDayOfWeek>=column)?(currentDayOfWeek-column):(Utility.DAYS_NUMBER+column+1);
    	if ((currentMonth-1)==Calendar.JANUARY && (Calendar.getInstance().get(Calendar.YEAR)%4 >0))
    	{
    		daysOffset+=1;
    	}*/
		
		Calendar cal=Calendar.getInstance(Locale.getDefault());
		cal.set(year,currentMonth-1, 1);
		int dayWeek=((cal.get(Calendar.DAY_OF_WEEK)==0)?7:cal.get(Calendar.DAY_OF_WEEK))-1;	
		
		daysOffset=dayWeek-1;
		//daysOffset=Math.abs(1-(dayWeek-1));
		
    	if (daysOffset<=0) daysOffset+=Utility.DAYS_NUMBER;
    	
    	int[][] shiftedArray=new int[Utility.WEEKS_NUMBER][Utility.DAYS_NUMBER];
    	for(int i=0;i<Utility.WEEKS_NUMBER;i++)
    	{
    	for(int j=0;j<Utility.DAYS_NUMBER;j++)
    	{
    		if (j+daysOffset <Utility.DAYS_NUMBER)
    		{
    			shiftedArray[i][j+daysOffset]=daysArray[i][j];
    			MonthDays[i][j+daysOffset]=new DayEntity(daysArray[i][j],DayType.REGULAR);
    			switch (j+daysOffset)
    			{
    			case 5:
    			case 6:
    				MonthDays[i][j+daysOffset].setDAY_TYPE(DayType.WEEKEND);
    				break;
    			}
    			MonthDays[i][j+daysOffset].setMonth(currentMonth);
    			MonthDays[i][j+daysOffset].setYear(year);
    		}
    		else
    		{
    			if ((i+1)<Utility.WEEKS_NUMBER)
    			{
    			shiftedArray[i+1][(j+daysOffset)-Utility.DAYS_NUMBER]=daysArray[i][j];
    			MonthDays[i+1][(j+daysOffset)-Utility.DAYS_NUMBER]=new DayEntity(daysArray[i][j],DayType.REGULAR);
    			switch ((j+daysOffset)-Utility.DAYS_NUMBER)
    			{
    			case 5:
    			case 6:
    				MonthDays[i+1][j+daysOffset-Utility.DAYS_NUMBER].setDAY_TYPE(DayType.WEEKEND);
    				break;
    			}
    			MonthDays[i+1][(j+daysOffset)-Utility.DAYS_NUMBER].setMonth(currentMonth);
    			MonthDays[i+1][(j+daysOffset)-Utility.DAYS_NUMBER].setYear(year);
    			}
    		}
    	}
    	}
    	return shiftedArray;
	}
	
	private int[][] fillOffsetDays(int[][] daysArray, int daysInMonth) throws java.lang.NumberFormatException
	{
		Calendar currentCalendar=Calendar.getInstance(Locale.getDefault());
		currentCalendar.set(year,(currentMonth-1)-1,1);
    	int prevDays=currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    	//fill with previous month
    	
    	if (daysOffset>0)
    	{
    		for(int i=daysOffset-1;i>=0;i--)
    		{
    			daysArray[0][i]=prevDays;
    			MonthDays[0][i]=new DayEntity(prevDays,DayType.NOT_CURRENT);
    			MonthDays[0][i].setMonth(currentMonth==1?12:currentMonth-1);
    			
    				MonthDays[0][i].setYear(currentMonth==1? year-1:year);
    			
    			prevDays--;
    		}
    	}
    	
    	//Заполнение следующим месяцем
    	int dif=(Utility.WEEKS_NUMBER*Utility.DAYS_NUMBER)-(daysInMonth+daysOffset);
    	if (dif>0)
    	{
    		int nextDays=dif;
    		currentCalendar.add(Calendar.MONTH, 2);
    		for(int ind=0;ind<dif;ind++)
    		{
    			if (nextDays>0)
    			{
    			int i=Utility.DAYS_NUMBER-1-ind;
    			
    			if (i>=0)
    			{
    			daysArray[Utility.WEEKS_NUMBER-1][i]=nextDays;
    			MonthDays[Utility.WEEKS_NUMBER-1][i]=new DayEntity(nextDays,DayType.NOT_CURRENT);
    			MonthDays[Utility.WEEKS_NUMBER-1][i].setMonth(currentMonth==12?1:currentMonth+1);
    			MonthDays[Utility.WEEKS_NUMBER-1][i].setYear(currentMonth==12?year+1:year);
    			nextDays--;
    			}
    			else
    			{
    				i+=Utility.DAYS_NUMBER;
    				daysArray[Utility.WEEKS_NUMBER-2][i]=nextDays;
        			MonthDays[Utility.WEEKS_NUMBER-2][i]=new DayEntity(nextDays,DayType.NOT_CURRENT);
        			nextDays--;
        			
        			MonthDays[Utility.WEEKS_NUMBER-2][i].setMonth(currentMonth==12?1:currentMonth+1);
        			MonthDays[Utility.WEEKS_NUMBER-2][i].setYear(currentMonth==12?year+1:year);
    			}
    				
    			}
    			
    		}
    	}
    	return daysArray;
	}

	public DayEntity[][] getMonthDays() {
		return MonthDays;
	}

	public void setMonthDays(DayEntity[][] monthDays) {
		MonthDays = monthDays;
	}

	public boolean isCurrentMonth() {
		return isCurrentMonth;
	}

	public void setCurrentMonth(boolean isCurrentMonth) {
		this.isCurrentMonth = isCurrentMonth;
	}

	public int getToday() {
		return currentDay;
	}

	public void setToday(int today) {
		this.today = today;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getCurrentDayName() {
		return currentDayName;
	}

	public void setCurrentDayName(String currentDayName) {
		this.currentDayName = currentDayName;
	}

	public String getCurrentMonthName() {
		return currentMonthName;
	}

	public void setCurrentMonthName(String currentMonthName) {
		this.currentMonthName = currentMonthName;
	}
	
}
