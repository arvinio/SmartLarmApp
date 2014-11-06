package com.example.smartlarmapp;
import java.util.Calendar;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;


public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TimePicker tp = (TimePicker) findViewById(R.id.timePicker1);
		tp.setIs24HourView(true);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void sendMessage(View view) {
		populateList(printCal(sleepNow()));     
	}

	private void populateList(ArrayList<String> times){
		ListView lv = (ListView)findViewById(R.id.listView1);
		final ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_list_item_1, times);
		lv.setAdapter(adapter);


		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final String item = (String) parent.getItemAtPosition(position);
				Intent openNewAlarm = new Intent(AlarmClock.ACTION_SET_ALARM);
				openNewAlarm.putExtra(AlarmClock.EXTRA_HOUR, Integer.valueOf(item.split(":")[0]));
				openNewAlarm.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
				openNewAlarm.putExtra(AlarmClock.EXTRA_MINUTES, Integer.valueOf(item.split(":")[1]));
				startActivity(openNewAlarm);
			}

		});
	}


	public void sleepatpressed(View view){
		TimePicker tp = (TimePicker) findViewById(R.id.timePicker1);
		String temp = ("" + tp.getCurrentHour() + ":" + tp.getCurrentMinute());
		populateList(printCal(sleepAt(temp)));
	}


	public static void main(String[] args) {
		String wakeUpStr = "07:00";
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		Date d = null;

		try {
			d = df.parse(wakeUpStr);
		} catch (ParseException ex) {
			System.out.println("Failed to parse: " + wakeUpStr);
		}

		Calendar wakeUp = Calendar.getInstance();
		wakeUp.setTime(d);

		System.out.println("Sleep when if i want to wake up at 7:00");
		printCal(sleepWhen(wakeUp));
		System.out.println("wake up when if i want to sleep now");
		printCal(sleepNow());
		System.out.println("wake up when if i want to sleep at 7:00");
		// printCal(sleepAt(wakeUp));

	}

	public static ArrayList<Date> sleepWhen(Calendar time) {
		Calendar timeToWake = Calendar.getInstance();
		timeToWake.setTime(time.getTime());
		ArrayList<Date> sleepTimes = new ArrayList<Date>();
		for (int i = 0; i < 6; i++) {
			timeToWake.add(Calendar.MINUTE, -90);
			if (i > 1) {
				sleepTimes.add(timeToWake.getTime());
			}
		}

		return sleepTimes;
	}

	public static ArrayList<Date> sleepNow() {
		Calendar bedtime = Calendar.getInstance();

		bedtime.add(Calendar.MINUTE, 14); // takes 14 min to sleep

		ArrayList<Date> wakeUpTimes = new ArrayList<Date>();

		for (int i = 0; i < 6; i++) {
			bedtime.add(Calendar.MINUTE, 90);
			//if (bedtime.getTime().compareTo(timeToWake.getTime()) > 0)
			wakeUpTimes.add(bedtime.getTime());
		}

		return wakeUpTimes;
	}

	private static ArrayList<String> printCal(ArrayList<Date> times) {

		ArrayList<String> res = new ArrayList<String>();

		for (Date date : times) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			String res1 = new String("" + new DecimalFormat("00").format(cal.get(Calendar.HOUR_OF_DAY)) + ":"
					+ new DecimalFormat("00").format(cal.get(Calendar.MINUTE)));
			res.add(res1);
		}

		return res;
	}

	private static ArrayList<Date> sleepAt(String time) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		Date d = null;

		try {
			d = df.parse(time);
		} catch (ParseException ex) {
			System.out.println("Failed to parse: " + time);
		}

		Calendar wakeUp = Calendar.getInstance();
		wakeUp.setTime(d);


		Calendar timeToSleep = Calendar.getInstance();
		timeToSleep.setTime(wakeUp.getTime());
		ArrayList<Date> wakeUpTimes = new ArrayList<Date>();
		for (int i = 0; i < 6; i++) {
			timeToSleep.add(Calendar.MINUTE, 90);
			if (i == 0 || i > 2) {
				wakeUpTimes.add(timeToSleep.getTime());
			}
		}
		return wakeUpTimes;
	}








}


