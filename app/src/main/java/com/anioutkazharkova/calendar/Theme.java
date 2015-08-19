package com.anioutkazharkova.calendar;




public class Theme {
	
	private int daySelected;
	private int regularFont;
	private int weekendFont;
	private int nonCurrentFont;
	private int dividerColor;
	private int headerColor;
	private int background;
	private int labelFont;
	private int arrowFont;
	private int accentColor;
	private int accentShape;
	private int lightColor;
	private boolean isStroke=false;
	
	private ThemeType TypeTheme;
	
	
	public Theme()
	{
		this.TypeTheme=ThemeType.CHOCO;
		initTheme();
	}
	public Theme(ThemeType type) {
		// TODO Auto-generated constructor stub
		this.TypeTheme=type;
		initTheme();
	}	
	
	private void initTheme() {
		// TODO Auto-generated method stub
		 if (TypeTheme==ThemeType.TRANSPARENT)
		{
			daySelected=R.drawable.oval_highlighter;
			regularFont=R.color.white;
			weekendFont=R.color.white;
			headerColor=android.R.color.transparent;
			accentColor=R.color.white;
			dividerColor=R.color.white_transparent;
			nonCurrentFont=R.color.white_transparent;
			setBackground(R.drawable.calendar_shape);
			labelFont=R.color.white;
			arrowFont=R.color.white;
			lightColor=0;
		}
		
		else
			if (TypeTheme==ThemeType.PINK)
			{
				daySelected=R.drawable.oval_highlighter;
				regularFont=R.color.dark;
				weekendFont=R.color.pink_header;
				nonCurrentFont=R.color.secondary;
				labelFont=R.color.white;
				arrowFont=R.color.white;
				setBackground(R.drawable.calendar_shape_white);
				headerColor=R.color.pink_header;
				dividerColor=R.color.pink_header_divider;
				accentColor=R.color.pink_accent;
				lightColor=R.color.light_pink_label;
			}
			else 
				if (TypeTheme==ThemeType.ORANGE)
				{
					daySelected=R.drawable.oval_highlighter;
					regularFont=R.color.dark;
					weekendFont=R.color.orange_header;
					nonCurrentFont=R.color.secondary;
					labelFont=R.color.white;
					arrowFont=R.color.white;
					setBackground(R.drawable.calendar_shape_white);
					headerColor=R.color.orange_header;
					dividerColor=R.color.orange_header_divider;
					accentColor=R.color.orange_accent;
					lightColor=R.color.light_orange_label;
				}
				else
					if (TypeTheme==ThemeType.RED)
					{
						daySelected=R.drawable.oval_highlighter;
						regularFont=R.color.dark;
						weekendFont=R.color.red_header;
						nonCurrentFont=R.color.secondary;
						labelFont=R.color.white;
						arrowFont=R.color.white;
						setBackground(R.drawable.calendar_shape_white);
						headerColor=R.color.red_header;
						dividerColor=R.color.red_header_divider;
						accentColor=R.color.red_accent;
						lightColor=R.color.light_red_label;
					}
					else
						if (TypeTheme==ThemeType.PURPLE)
						{
							daySelected=R.drawable.oval_highlighter;
							regularFont=R.color.dark;
							weekendFont=R.color.purple_header;
							nonCurrentFont=R.color.secondary;
							labelFont=R.color.white;
							arrowFont=R.color.white;
							setBackground(R.drawable.calendar_shape_white);
							headerColor=R.color.purple_header;
							dividerColor=R.color.purple_header_divider;
							accentColor=R.color.purple_accent;
							lightColor=R.color.light_purple_label;
						}
						else
							if (TypeTheme==ThemeType.BLUE)
							{
								daySelected=R.drawable.oval_highlighter;
								regularFont=R.color.dark;
								weekendFont=R.color.blue_header;
								nonCurrentFont=R.color.secondary;
								labelFont=R.color.white;
								arrowFont=R.color.white;
								setBackground(R.drawable.calendar_shape_white);
								headerColor=R.color.blue_header;
								dividerColor=R.color.blue_header_divider;
								accentColor=R.color.blue_accent;
								lightColor=R.color.blue_purple_label;
							}
							else
								if (TypeTheme==ThemeType.CYAN)
								{
									daySelected=R.drawable.oval_highlighter;
									regularFont=R.color.dark;
									weekendFont=R.color.cyan_header;
									nonCurrentFont=R.color.secondary;
									labelFont=R.color.white;
									arrowFont=R.color.white;
									setBackground(R.drawable.calendar_shape_white);
									headerColor=R.color.cyan_header;
									dividerColor=R.color.cyan_header_divider;
									accentColor=R.color.cyan_accent;
									lightColor=R.color.light_cyan_label;
								}
								else
									if (TypeTheme==ThemeType.GREEN)
									{
										daySelected=R.drawable.oval_highlighter;
										regularFont=R.color.dark;
										weekendFont=R.color.green_header;
										nonCurrentFont=R.color.secondary;
										labelFont=R.color.white;
										arrowFont=R.color.white;
										setBackground(R.drawable.calendar_shape_white);
										headerColor=R.color.green_header;
										dividerColor=R.color.green_header_divider;
										accentColor=R.color.green_accent;
										lightColor=R.color.light_green_label;
									}
									else
										if (TypeTheme==ThemeType.YELLOW)
										{
											daySelected=R.drawable.oval_highlighter;
											regularFont=R.color.dark;
											weekendFont=R.color.yellow_header;
											nonCurrentFont=R.color.secondary;
											labelFont=R.color.white;
											arrowFont=R.color.white;
											setBackground(R.drawable.calendar_shape_white);
											headerColor=R.color.yellow_header;
											dividerColor=R.color.yellow_header_divider;
											accentColor=R.color.yellow_accent;
											lightColor=R.color.light_yellow_label;
										}
										else
											if (TypeTheme==ThemeType.GRAY)
											{
												daySelected=R.drawable.oval_highlighter;
												regularFont=R.color.dark;
												weekendFont=R.color.gray_header;
												nonCurrentFont=R.color.secondary;
												labelFont=R.color.white;
												arrowFont=R.color.white;
												setBackground(R.drawable.calendar_shape_white);
												headerColor=R.color.gray_header;
												dividerColor=R.color.gray_header_divider;
												accentColor=R.color.gray_accent;
												lightColor=R.color.light_gray_label;
											}
											else
												if (TypeTheme==ThemeType.CHOCO)
												{
													daySelected=R.drawable.oval_highlighter;
													regularFont=R.color.dark;
													weekendFont=R.color.choco_header;
													nonCurrentFont=R.color.secondary;
													labelFont=R.color.white;
													arrowFont=R.color.white;
													setBackground(R.drawable.calendar_shape_white);
													headerColor=R.color.choco_header;
													dividerColor=R.color.choco_header_divider;
													accentColor=R.color.choco_accent;
													lightColor=R.color.light_choco_label;
												}
												else
													if (TypeTheme==ThemeType.JOSEPHINE)
													{
														daySelected=R.drawable.oval_highlighter;
														regularFont=R.color.dark;
														weekendFont=R.color.yellow_header_divider;
														nonCurrentFont=R.color.secondary;
														labelFont=R.color.white;
														arrowFont=R.color.white;
														setBackground(R.drawable.calendar_shape_white);
														headerColor=R.color.josy_header;
														dividerColor=R.color.josy_header_divider;
														accentColor=R.color.josy_accent;
														lightColor=R.color.light_gray_label;
													}
													else
														if (TypeTheme==ThemeType.CONTRAST)
														{
															daySelected=R.drawable.oval_highlighter;
															regularFont=R.color.dark;
															weekendFont=R.color.red_header;
															nonCurrentFont=R.color.secondary;
															labelFont=R.color.white;
															arrowFont=R.color.white;
															setBackground(R.drawable.calendar_shape_white);
															headerColor=R.color.dark;
															dividerColor=R.color.black;
															accentColor=R.color.red_accent;
															lightColor=R.color.white;
														}
		// isStroke=true;
		//daySelected=R.drawable.rect_stroke_highlighter;
		
	}

	public ThemeType getThemeType()
	{
		return this.TypeTheme;
	}
	
	public int getDaySelected() {
		return daySelected;
	}
	public void setDaySelected(int daySelected) {
		this.daySelected = daySelected;
	}
	public int getRegularFont() {
		return regularFont;
	}
	public void setRegularFont(int regularFont) {
		this.regularFont = regularFont;
	}
	public int getWeekendFont() {
		return weekendFont;
	}
	public void setWeekendFont(int weekendFont) {
		this.weekendFont = weekendFont;
	}
	public int getNonCurrentFont() {
		return nonCurrentFont;
	}
	public void setNonCurrentFont(int nonCurrentFont) {
		this.nonCurrentFont = nonCurrentFont;
	}
	public int getLabelFont() {
		return labelFont;
	}
	public void setLabelFont(int labelFont) {
		this.labelFont = labelFont;
	}
	public int getArrowFont() {
		return arrowFont;
	}
	public void setArrowFont(int arrowFont) {
		this.arrowFont = arrowFont;
	}

	public int getBackground() {
		return background;
	}

	public void setBackground(int background) {
		this.background = background;
	}
	public int getDividerColor() {
		return dividerColor;
	}
	public void setDividerColor(int dividerColor) {
		this.dividerColor = dividerColor;
	}
	public int getHeaderColor() {
		return headerColor;
	}
	public void setHeaderColor(int headerColor) {
		this.headerColor = headerColor;
	}
	public int getAccentColor() {
		return accentColor;
	}
	public void setAccentColor(int accentColor) {
		this.accentColor = accentColor;
	}
	public int getAccentShape() {
		return accentShape;
	}
	public void setAccentShape(int accentShape) {
		this.accentShape = accentShape;
	}
	public int getLightColor() {
		return lightColor;
	}
	public void setLightColor(int lightColor) {
		this.lightColor = lightColor;
	}
	public boolean getIsStroke() {
		return isStroke;
	}
	public void setIsStroke(boolean isStroke) {
		this.isStroke = isStroke;
	}
	
	
	
	

}

