package net.arvian.smartlarm;

import java.text.DecimalFormat;
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

public class SleepNow extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleep_now);
		populateList(printCal(sleepNow(), Calendar.getInstance().getTimeInMillis()));
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

				//displays amount of sleep
				String res1 = new String("" + new DecimalFormat("00").format(cal.get(Calendar.HOUR_OF_DAY)) + ":"
						+ new DecimalFormat("00").format(cal.get(Calendar.MINUTE)));
				
				String res2 = new String("Gives " + sleepTime.get(Calendar.HOUR_OF_DAY) + "h " + sleepTime.get(Calendar.MINUTE) + "m of sleep");

				//Hashmap for the adapter values
				//res1 = main text; res2 = subtext
				Map<String, String> temp = new HashMap<String, String>(2);
				temp.put("time", res1);
				temp.put("sleep", res2);
				res.add(temp);
			}

			return res;
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sleep_now, menu);
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
