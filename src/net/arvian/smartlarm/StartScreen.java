package net.arvian.smartlarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class StartScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_screen);
		ImageView image = (ImageView) findViewById(R.id.logo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_screen, menu);
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
	
	//menu buttons
	public void sleepNow(View view){
		Intent openSleepNow = new Intent(this, SleepNow.class);
		startActivity(openSleepNow);
	}
	
	public void sleepAt(View view){
		Intent openSleepNow = new Intent(this, MainActivity.class);
		startActivity(openSleepNow);
	}
	
	public void wakeAt(View view){
		Intent openSleepNow = new Intent(this, MainActivity.class);
		startActivity(openSleepNow);
	}
}
