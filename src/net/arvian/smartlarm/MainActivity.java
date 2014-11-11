package net.arvian.smartlarm;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.*;
import android.widget.*;

import java.util.*;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/* TODO add title etc */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//cast view object to type timePicker to enable change.
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
		if (id == R.string.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//generates list of times. On toggle of sleepNow()
	public void sendMessage(View view) {
		populateList(printCal(sleepNow(), Calendar.getInstance().getTimeInMillis()));     
	}

	//contains list of maps consiting of dual strings
	//listView1 = main text; listView2 = subtext
	private void populateList(List<Map<String, String>> times){
		ListView lv = (ListView)findViewById(R.id.listView1);
		
		//adapts our data structure to a list
		SimpleAdapter adapter = new SimpleAdapter(this, times,
				android.R.layout.simple_list_item_2,
				
				//assign values from hash table below
				new String[] {"time", "sleep"},
				new int[] {android.R.id.text1,
				android.R.id.text2});
		lv.setAdapter(adapter);
		
		//Applies only to SleepNow() and WakeAt()
		//listens to toggles on any list values (times)
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final Map<String, String> items = (Map<String, String>) parent.getItemAtPosition(position);
				String item = items.get("time");
				Intent openNewAlarm = new Intent(AlarmClock.ACTION_SET_ALARM);
				openNewAlarm.putExtra(AlarmClock.EXTRA_HOUR, Integer.valueOf(item.split(":")[0]));
				openNewAlarm.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
				openNewAlarm.putExtra(AlarmClock.EXTRA_MINUTES, Integer.valueOf(item.split(":")[1]));
				startActivity(openNewAlarm);
			}

		});
	}

	//Toggle SleepAt button
	public void sleepatpressed(View view){
		TimePicker tp = (TimePicker) findViewById(R.id.timePicker1);
		String temp = (tp.getCurrentHour() + ":" + tp.getCurrentMinute());

		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		Date d = null;

		try {
			d = df.parse(temp);
		} catch (ParseException ex) {
			System.out.println("Failed to parse: " + temp);
		}

		Calendar sleepTime = Calendar.getInstance();
		sleepTime.setTime(d);

		populateList(printCal(sleepAt(temp), sleepTime.getTimeInMillis()));
	}
	
	//public void 

	//Toggle WakeAt button
	public void wakeatpressed(View view){
		TimePicker tp = (TimePicker) findViewById(R.id.timePicker1);
		String temp = ("" + tp.getCurrentHour() + ":" + tp.getCurrentMinute());
		ListView lv = (ListView)findViewById(R.id.listView1);

		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		Date d = null;

		try {
			d = df.parse(temp);
		} catch (ParseException ex) {
			System.out.println("Failed to parse: " + temp);
		}

		Calendar wakeTime = Calendar.getInstance();
		wakeTime.setTime(d);

		//WakeAt has its own adapter since list toggles don't affect alarm
		SimpleAdapter adapter = new SimpleAdapter(this, printCal(sleepWhen(temp), wakeTime.getTimeInMillis()),
				android.R.layout.simple_list_item_2,
				new String[] {"time", "sleep"},
				new int[] {android.R.id.text1,
			android.R.id.text2});
		lv.setAdapter(adapter);

		//applies to WakeAt()
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final Map<String, String> items = (Map<String, String>) parent.getItemAtPosition(position);
				String item = items.get("time");
				Intent openNewAlarm = new Intent(AlarmClock.ACTION_SET_ALARM);
				TimePicker tp = (TimePicker) findViewById(R.id.timePicker1);
				String temp = ("" + tp.getCurrentHour() + ":" + tp.getCurrentMinute());
				openNewAlarm.putExtra(AlarmClock.EXTRA_HOUR, Integer.valueOf(temp.split(":")[0]));
				openNewAlarm.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
				openNewAlarm.putExtra(AlarmClock.EXTRA_MINUTES, Integer.valueOf(temp.split(":")[1]));
				startActivity(openNewAlarm);
			}

		});
	}
	
	//alg for wakeat
	public static ArrayList<Date> sleepWhen(String time) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		Date d = null;

		try {
			d = df.parse(time);
		} catch (ParseException ex) {
			System.out.println("Failed to parse: " + time);
		}

		Calendar wakeUp = Calendar.getInstance();
		wakeUp.setTime(d);

		//Alg for main text (when to sleep)
		Calendar timeToWake = Calendar.getInstance();
		timeToWake.setTime(wakeUp.getTime());
		ArrayList<Date> sleepTimes = new ArrayList<Date>();
		
		//loop for 90m intervals
		for (int i = 0; i < 10; i++) {
			timeToWake.add(Calendar.MINUTE, -90);
			sleepTimes.add(timeToWake.getTime());
		}

		return sleepTimes;
	}

	//alg for sleepNow button
	public static ArrayList<Date> sleepNow() {
		Calendar bedtime = Calendar.getInstance();

		bedtime.add(Calendar.MINUTE, 14); // takes 14 min to sleep

		ArrayList<Date> wakeUpTimes = new ArrayList<Date>();

		//loop for 90m intervals
		for (int i = 0; i < 10; i++) {
			bedtime.add(Calendar.MINUTE, 90);
			//if (bedtime.getTime().compareTo(timeToWake.getTime()) > 0)
			wakeUpTimes.add(bedtime.getTime());
		}

		return wakeUpTimes;
	}

	//alg for sleeptime. Calculates currenttime - chosentime (in milis)
	//alg is applied through adapter
	private static List<Map<String, String>> printCal(ArrayList<Date> times, long time) {

		List<Map<String, String>> res = new ArrayList<Map<String, String>>();

		for (Date date : times) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			long timeAsleep;
			if (time > cal.getTimeInMillis()) {
				timeAsleep = time - cal.getTimeInMillis();
			} else {
				timeAsleep = cal.getTimeInMillis() - time;
			}
			Calendar sleepTime = Calendar.getInstance();
			sleepTime.setTimeInMillis(timeAsleep);
			sleepTime.add(Calendar.HOUR, -1);

			//displays sleeptime
			String res1 = new String("" + new DecimalFormat("00").format(cal.get(Calendar.HOUR_OF_DAY)) + ":"
					+ new DecimalFormat("00").format(cal.get(Calendar.MINUTE)));
			String res2 = new String("Sleeptime: " + sleepTime.get(Calendar.HOUR_OF_DAY) + "h " + sleepTime.get(Calendar.MINUTE) + "m");

			//Hashmap for the adapter values
			//res1 = main text; res2 = subtext
			Map<String, String> temp = new HashMap<String, String>(2);
			temp.put("time", res1);
			temp.put("sleep", res2);
			res.add(temp);
		}

		return res;
	}
	
	//alg for sleepAt button
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
		
		//loop for 90m intervals
		for (int i = 0; i < 10; i++) {
			timeToSleep.add(Calendar.MINUTE, 90);
			wakeUpTimes.add(timeToSleep.getTime());
		}
		return wakeUpTimes;
	}








}