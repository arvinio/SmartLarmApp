package net.arvian.smartlarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class ListPreferenceWithListener extends ListPreference {
	
	public ListPreferenceWithListener(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}

	public ListPreferenceWithListener(Context context) {
	    super(context);
	}
	
	public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key) {
		
	}
	

}
