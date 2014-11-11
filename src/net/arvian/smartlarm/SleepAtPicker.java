package net.arvian.smartlarm;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;

public class SleepAtPicker extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleep_at_picker);
		
		String alarm = getIntent().getExtras().getString("alarm");
		
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		Date d = null;

		try {
			d = df.parse(alarm);
		} catch (ParseException ex) {
			System.out.println("Failed to parse: " + alarm);
		}

		Calendar sleepTime = Calendar.getInstance();
		sleepTime.setTime(d);

		populateList(printCal(sleepAt(alarm), sleepTime.getTimeInMillis()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sleep_at_picker, menu);
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
					final String item = items.get("time");
					Intent openNewAlarm = new Intent(AlarmClock.ACTION_SET_ALARM);
					AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

				    builder.setTitle("Confirm");
				    builder.setMessage("Are you sure you want to set the alarm to " + item + "?");

				    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				        public void onClick(DialogInterface dialog, int which) {
				        	Intent openNewAlarm = new Intent(AlarmClock.ACTION_SET_ALARM);
							openNewAlarm.putExtra(AlarmClock.EXTRA_HOUR, Integer.valueOf(item.split(":")[0]));
							openNewAlarm.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
							openNewAlarm.putExtra(AlarmClock.EXTRA_MINUTES, Integer.valueOf(item.split(":")[1]));
							openNewAlarm.putExtra(AlarmClock.EXTRA_MESSAGE, "Smartlarm");
							startActivity(openNewAlarm);
				            dialog.dismiss();
				        }

				    });

				    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

				        @Override
				        public void onClick(DialogInterface dialog, int which) {
				            // Do nothing
				            dialog.dismiss();
				        }
				    });

				    AlertDialog alert = builder.create();
				    alert.show();
				}

			});
		}
}
