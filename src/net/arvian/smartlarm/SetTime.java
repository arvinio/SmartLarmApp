package net.arvian.smartlarm;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TimePicker;

public class SetTime extends Activity {

	String method;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_time);
		
		TimePicker tp = (TimePicker) findViewById(R.id.timePicker1);
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		boolean timemode = settings.getBoolean("24hr", true);
		tp.setIs24HourView(timemode);
		
		method = getIntent().getExtras().getString("method");
		
		if (method.equals("sleepat")) {
			setTitle("Sleep at...");
		} else if (method.equals("wakeat")) {
			setTitle("Wake up at...");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
	
	public void next(View view){
		TimePicker tp = (TimePicker) findViewById(R.id.timePicker1);
		String time = (new DecimalFormat("00").format(tp.getCurrentHour()) + ":" + new DecimalFormat("00").format(tp.getCurrentMinute()));
		
		Intent openSleepNow = new Intent(this, PickTime.class);
		openSleepNow.putExtra("alarm", time);
		openSleepNow.putExtra("method", method);
		startActivity(openSleepNow);
	}
}
