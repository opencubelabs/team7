package rakco.com.travelconnectmindtree;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class MakePaymentActivity extends DadaActivity {
	private static final String TAG = "MakePaymentActivity";
	public static ParseObject flightDetails;
	int seatNo;
	CheckBox shareMeCheckBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		flightDetails = PickSeatActivity.flightDetails;
		setContentView(R.layout.activity_make_payment);

		TextView detailsTV = (TextView) findViewById(R.id.detailsTV);
		detailsTV.setText("From: " + flightDetails.getString("from"));
		detailsTV.append("\nTo: " + flightDetails.getString("to"));
		Date depDate = flightDetails.getDate(FlightListFragment.DEPARTURE_AT);
		String depTime = new SimpleDateFormat("d MMM, yyyy  hh:mm a", Locale.US).format(depDate);
		detailsTV.append("\nDeparture: " + depTime);

		seatNo = getIntent().getIntExtra("seatNo", 0);
		char letter = (char) ('A' + seatNo / (3 * 5));
		String seatString = letter + "" + seatNo % (3 * 5);
		detailsTV.append("\nSeat No: " + seatString);


		shareMeCheckBox = (CheckBox) findViewById(R.id.shareMeCheckBox);

		int age = Integer.parseInt(ParseUser.getCurrentUser().getString("age"));
		TextView amountTV = (TextView) findViewById(R.id.amountTV),
				amountPayableTV = (TextView) findViewById(R.id.amountPayable);
		Log.i(TAG, "Age: " + age);
		if (age >= 60) {
			findViewById(R.id.senior_citizen_tv).setVisibility(View.VISIBLE);
			String fareStr = flightDetails.getString("fare");
			String newStr = fareStr.substring(1).trim();
			newStr = newStr.replaceAll(",", "");
			int amt = Integer.parseInt(newStr), newAmt = amt * 9 / 10;
			amountTV.setText("₹ " + newAmt);
		} else {
			amountTV.setText(flightDetails.getString("fare"));
		}
		String disability = ParseUser.getCurrentUser().getString("disability");
		if (!disability.equals("None")) {
			findViewById(R.id.disabilityTV).setVisibility(View.VISIBLE);
		}
	}

	public void visaClicked(View view) {
		makePayment("Visa");
	}

	public void mastercardClicked(View view) {
		makePayment("Mastercard");
	}

	public void maestroClicked(View view) {
		makePayment("Maestro");
	}

	private void makePayment(String card) {
		ParseObject booking = new ParseObject("Bookings");
		booking.put("user", ParseUser.getCurrentUser());
		booking.put("flight", flightDetails);
		booking.put("seatNo", seatNo);
		booking.put("shareMe", shareMeCheckBox.isChecked());
		booking.pinInBackground();
		booking.saveInBackground();
		setReminder();

		try {
			Thread.sleep(3000); // Faking payment: code reviewer: this is done on purpose
			// But the online registration has happened
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		showToast("Payment made through " + card);
		new AlertDialog.Builder(this)
				.setTitle("Booking")
				.setMessage("Do you want to book a return ticket too?")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(MakePaymentActivity.this, BookFlightActivity.class);
						intent.putExtra("title", "Book return journey");
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(MakePaymentActivity.this, MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				}).show();
	}

	private void setReminder() {
		// TODO set the proper reminder
		AlarmManager mgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(getBaseContext(), FlightAlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(getBaseContext(), 0, intent, 0);
		Calendar myCal = Calendar.getInstance();  // current date and time
		mgr.set(AlarmManager.RTC_WAKEUP, myCal.getTimeInMillis(), pi);
		Log.i(TAG, "Alarm set for " + myCal.getTime().toLocaleString());
		showToast("Alarm set for " + myCal.getTime().toLocaleString());
	}

	public void withFriendsClicked(View view) {
		ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
		userQuery.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> parseUsers, ParseException e) {
				if (e == null) {
					buildFriendsList(parseUsers);
				} else {
					showToast("Check internet connection!");
				}
			}
		});
	}

	private void buildFriendsList(final List<ParseUser> parseUsers) {
		final List<ParseUser> usersCopy = new ArrayList<ParseUser>();
		final ParseUser me = ParseUser.getCurrentUser();
		for (ParseUser user : parseUsers) {
			if (!user.equals(me))
				usersCopy.add(user);
		}
		CharSequence[] items = new CharSequence[usersCopy.size()];
		int i = 0;
		for (ParseUser user : usersCopy) {
			items[i++] = user.getString("name");
		}
		new AlertDialog.Builder(this)
				.setTitle("Choose friends on Vuelo")
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int position) {
						ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
						pushQuery.whereEqualTo("user", usersCopy.get(position));
						ParsePush push = new ParsePush();
						push.setQuery(pushQuery);
						try {
							Date depDate = flightDetails.getDate(FlightListFragment.DEPARTURE_AT);
							String depTime = new SimpleDateFormat("d MMM, yyyy  hh:mm a", Locale.US).format(depDate);
							String msg = me.getString("name") + " wants to travel with you."
									+ "\n\nFrom: " + flightDetails.getString("from")
									+ "\n\nTo: " + flightDetails.getString("to")
									+ "\n\nDeparture: " + depTime
									+ "\n\nOffer: 15%"
									+ "\n\nFare: " + flightDetails.getString("fare")
									+ "\n\nAccept to confirm.";
							push.setData(new JSONObject()
									.put("action", "Booking")
									.put("alert", msg)
									.put("user", me.getObjectId())
									.put("flightId", flightDetails.getObjectId()));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						push.sendInBackground();
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.show();
	}
}
