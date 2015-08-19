package com.anioutkazharkova.calendar;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class ConfActivity extends Activity {

	CheckBox chbHighlight;
	Spinner spFonts, spThemes, spCalends;
	LinearLayout layoutCalendar;
	int id = -1;

	int fontType = 0;
	int selectedTheme = 0;
	int selectedFont = 0;
	boolean showHighlight = true;
	int[] days = { 1, 7, 30 };

	int period = 0;
	String prefix = Utility.WIDGET_PREF;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_layout);

		prefix = getIntent().getStringExtra("prefix");
		chbHighlight = (CheckBox) findViewById(R.id.chbHighlight);
		layoutCalendar = (LinearLayout) findViewById(R.id.layoutCalendar);
		if (prefix.equals(Utility.LIST_PREF))
			layoutCalendar.setVisibility(View.VISIBLE);
		else
			layoutCalendar.setVisibility(View.GONE);

		id = getIntent().getIntExtra("id", id);
		if (id != -1) {
			loadPreferences();
		}

		chbHighlight.setChecked(showHighlight);

		chbHighlight.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				showHighlight = isChecked;
			}
		});

		spFonts = (Spinner) findViewById(R.id.spFonts);
		spThemes = (Spinner) findViewById(R.id.spThemes);

		spCalends = (Spinner) findViewById(R.id.spCalends);
		spCalends.setAdapter(new CalendAdapter(getApplicationContext(),
				R.layout.font_type_element, getResources().getStringArray(
						R.array.calend_period)));
		int dayIndex = 0;
		if (period != 0) {
			for (int i = 0; i < days.length; i++) {
				if (days[i] == period)
					dayIndex = i;
			}
			spCalends.setSelection(dayIndex);
		}

		spCalends.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				period = days[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		spThemes.setAdapter(new ThemeAdapter(getApplicationContext(),
				R.layout.theme_element_layout, getResources().getStringArray(
						R.array.theme_values)));
		spFonts.setAdapter(new FontTypesAdapter(getApplicationContext(),
				R.layout.font_type_element, Utility.fontNames));
		spFonts.setSelection(fontType);

		spFonts.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				selectedFont = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		spThemes.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				selectedTheme = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		spThemes.setSelection(selectedTheme);

		getActionBar().setDisplayShowHomeEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.save_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.saveItem:
			apply();
			finish();
			break;
		case android.R.id.home:
			finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}

	private void loadPreferences() {
		// TODO Auto-generated method stub

		preferences = getApplicationContext().getSharedPreferences(prefix + id,
				getApplicationContext().MODE_PRIVATE);
		fontType = preferences.getInt(Utility.FONT_PREF, fontType);
		selectedFont = fontType;
		selectedTheme = preferences.getInt(Utility.THEME_PREF, selectedTheme);
		showHighlight = preferences.getBoolean(Utility.SHOW_SELECTION,
				showHighlight);
		if (prefix.equals(Utility.LIST_PREF)) {
			period = preferences.getInt(Utility.CALEND_PERIOD, period);
		}

	}

	private void apply() {

		if (id != -1) {
			Editor edit = preferences.edit();
			edit.putInt(Utility.FONT_PREF, selectedFont);
			edit.putInt(Utility.THEME_PREF, selectedTheme);
			edit.putBoolean(Utility.SHOW_SELECTION, showHighlight);
			edit.putInt(Utility.CALEND_PERIOD, period);
			edit.commit();

			AppWidgetManager manager = AppWidgetManager
					.getInstance(getApplicationContext());
			if (prefix.equals(Utility.WIDGET_PREF)) {
				CalendarWidget.updateView(getApplicationContext(), manager, id);
			} else if (prefix.equals(Utility.LIST_PREF)) {
				ListWidgetProvider.updateWidgetListView(
						getApplicationContext(), manager, id);
				Intent updateIntent = new Intent(
						"com.anioutkazharkova.calendar.updatecalendar");
				sendBroadcast(updateIntent);
			} else if (prefix.equals(Utility.WIDGET_PREF_EXTENDED)) {
				ExtendedCalendarWidget.updateView(getApplicationContext(),
						manager, id);
			} else if (prefix.equals(Utility.WIDGET_PREF_SMALL)){
				SmallCalendarWidget.updateView(getApplicationContext(),
						manager, id);
			}

		}
	}
}
