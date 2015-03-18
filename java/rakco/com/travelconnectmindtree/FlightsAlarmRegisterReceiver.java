package rakco.com.travelconnectmindtree;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class FlightsAlarmRegisterReceiver extends BroadcastReceiver {
	private static final String TAG = "FlightsAlarmRegisterReceiver";

	@Override
	public void onReceive(final Context context, Intent intent) {
		ParseQuery<ParseObject> bookingsQuery = ParseQuery.getQuery("Bookings")
				.whereEqualTo("user", ParseUser.getCurrentUser())
				.fromLocalDatastore();
		bookingsQuery.include("flight");
		bookingsQuery.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> bookings, ParseException e) {
				if (e != null) {
					e.printStackTrace();
					return;
				}
				Date now = new Date(), flightDate;
				for (ParseObject booking : bookings) {
					ParseObject flight = booking.getParseObject("flight");
					flightDate = flight.getDate("departureAt");
					if (flightDate.compareTo(now) < 0) {
						setAlarmNotif(context, flight);
					}
				}
			}
		});
	}

	public static void setAlarmNotif(Context context, ParseObject flight) {
		Date flightDate = flight.getDate("departureAt");
		AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, FlightAlarmReceiver.class);
		intent.putExtra("from", flight.getString("from"));
		intent.putExtra("to", flight.getString("to"));
		intent.putExtra("timeString", new SimpleDateFormat("hh:mm a", Locale.US).format(flightDate));
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		Calendar myCal = Calendar.getInstance();
		myCal.setTime(flightDate);
		myCal.add(Calendar.HOUR, -2);
		mgr.set(AlarmManager.RTC_WAKEUP, myCal.getTimeInMillis(), pi);
	}
}
