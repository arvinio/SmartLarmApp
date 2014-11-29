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
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

public class PickTime extends Activity {

    String method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_time);
        ListView lv = (ListView) findViewById(R.id.listView1);
        method = getIntent().getExtras().getString("method");

        if (method.equals("sleepnow")) {
        	// remove button
            Button button = (Button) findViewById(R.id.button1);
            button.setVisibility(View.GONE);

            // update text
            TextView txt = (TextView) findViewById(R.id.textView1);
            txt.setText("If you sleep now, you should wake up at one of the following times for optimal sleep time. " +  System.getProperty ("line.separator") + System.getProperty ("line.separator") + "Touch one of the following times to set the alarm.");

            // update title
            setTitle("Set alarm");
        	
            populateList(printCal(sleepNow(), Calendar.getInstance().getTimeInMillis()));
        } else {
            String alarm = getIntent().getExtras().getString("alarm");
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Date d = null;

            try {
                d = df.parse(alarm);
            } catch (ParseException ex) {
                System.out.println("Failed to parse: " + alarm);
            }

            Calendar chosenTime = Calendar.getInstance();
            chosenTime.setTime(d);

            if (method.equals("wakeat")) {
                // update button text
                TextView txt = (TextView) findViewById(R.id.button1);
                txt.setText("Set alarm to: " + alarm);

                // update text
                txt = (TextView) findViewById(R.id.textView1);
                txt.setText("You should try to sleep at one of these times");

                // update title
                setTitle("Bedtimes");

                //WakeAt has its own adapter since list toggles don't affect alarm
                SimpleAdapter adapter = new SimpleAdapter(this, printCal(sleepWhen(alarm), chosenTime.getTimeInMillis()),
                        android.R.layout.simple_list_item_2,
                        new String[]{"time", "sleep"},
                        new int[]{android.R.id.text1,
                            android.R.id.text2});
                lv.setAdapter(adapter);
            } else {
                // remove button
                Button button = (Button) findViewById(R.id.button1);
                button.setVisibility(View.GONE);

                // update text
                TextView txt = (TextView) findViewById(R.id.textView1);
                txt.setText("You should wake up at one of these times. Touch a time to set the alarm.");

                // update title
                setTitle("Set alarm");

                // populate list
                populateList(printCal(sleepAt(alarm), chosenTime.getTimeInMillis()));
            }
        }
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
    private void populateList(List<Map<String, String>> times) {
        ListView lv = (ListView) findViewById(R.id.listView1);

        //adapts our data structure to a list
        SimpleAdapter adapter = new SimpleAdapter(this, times,
                android.R.layout.simple_list_item_2,
                //assign values from hash table below
                new String[]{"time", "sleep"},
                new int[]{android.R.id.text1,
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

    public void setAlarm(View view) {
        final String alarm = getIntent().getExtras().getString("alarm");

        Intent openNewAlarm = new Intent(AlarmClock.ACTION_SET_ALARM);
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to set the alarm to " + alarm + "?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Intent openNewAlarm = new Intent(AlarmClock.ACTION_SET_ALARM);
                openNewAlarm.putExtra(AlarmClock.EXTRA_HOUR, Integer.valueOf(alarm.split(":")[0]));
                openNewAlarm.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                openNewAlarm.putExtra(AlarmClock.EXTRA_MINUTES, Integer.valueOf(alarm.split(":")[1]));
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
}
