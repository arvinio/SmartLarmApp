package net.arvian.smartlarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
		tp.setIs24HourView(true);
		
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
		String time = (tp.getCurrentHour() + ":" + tp.getCurrentMinute());
		
		Intent openSleepNow = new Intent(this, PickTime.class);
		openSleepNow.putExtra("alarm", time);
		openSleepNow.putExtra("method", method);
		startActivity(openSleepNow);
	}
}