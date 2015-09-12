package net.arvian.smartlarm;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class StartScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/*String languageToLoad  = "fa"; // OBS!!! change this to load from config !!!OBS
	    Locale locale = new Locale(languageToLoad); 
	    Locale.setDefault(locale);
	    Configuration config = new Configuration();
	    config.locale = locale;
	    getBaseContext().getResources().updateConfiguration(config, 
	    getBaseContext().getResources().getDisplayMetrics());*/
		
		setContentView(R.layout.activity_start_screen);
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
		
		Context context = getApplicationContext();
		CharSequence text = id + " - " + R.string.preferences;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		if (id == R.id.preferences) {
			Intent i = new Intent(getApplicationContext(), Preferences.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//menu buttons
	public void sleepNow(View view){
		Intent openSleepNow = new Intent(this, PickTime.class);
		openSleepNow.putExtra("method", "sleepnow");
		startActivity(openSleepNow);
	}
	
	public void sleepAt(View view){
		Intent openSleepNow = new Intent(this, SetTime.class);
		openSleepNow.putExtra("method", "sleepat");
		startActivity(openSleepNow);
	}
	
	public void wakeAt(View view){
		Intent openSleepNow = new Intent(this, SetTime.class);
		openSleepNow.putExtra("method", "wakeat");
		startActivity(openSleepNow);
	}
}
