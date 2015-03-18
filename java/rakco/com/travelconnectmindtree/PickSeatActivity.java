package rakco.com.travelconnectmindtree;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.drivemode.android.typeface.TypefaceHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rakco.com.travelconnectmindtree.helpers.FlightListAdapter;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class PickSeatActivity extends DadaActivity {
	private static final String TAG = "PickSeatActivity";
	public static ParseObject flightDetails;
	int prevChosenPos = -1;
	List<ParseObject> occupiedSeats;
	boolean[] occupied = new boolean[3 * 5 * 2];
	GridLayout grid1, grid2;
	Button chooseSeatButton;
	ParseUser checkingUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		flightDetails = FlightListAdapter.flightObject;

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_pick_seat);
		grid1 = (GridLayout) findViewById(R.id.grid1);
		grid1.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
		grid2 = (GridLayout) findViewById(R.id.grid2);
		grid2.setAlignmentMode(GridLayout.ALIGN_BOUNDS);

		chooseSeatButton = (Button) findViewById(R.id.chooseSeatButton);

		Button seat;
		int width = getResources().getDimensionPixelSize(R.dimen.grid_width), cellWidth = width / 3 - 20;
		for (int i = 0; i < 3 * 5; i++) {
			seat = new Button(this);
			GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
			lp.setMargins(2, 2, 2, 2);
			lp.width = cellWidth;
			lp.height = cellWidth;
			seat.setLayoutParams(lp);
			seat.setBackgroundResource(R.color.seatEmpty);
			seat.setTag(i);
			seat.setOnClickListener(gridListener);
			grid1.addView(seat, lp);
		}
		for (int i = 0; i < 3 * 5; i++) {
			seat = new Button(this);
			GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
			lp.setMargins(2, 2, 2, 2);
			lp.width = cellWidth;
			lp.height = cellWidth;
			seat.setLayoutParams(lp);
			seat.setBackgroundResource(R.color.seatEmpty);
			seat.setTag(3 * 5 + i);
			seat.setOnClickListener(gridListener);
			grid2.addView(seat, lp);
		}

		ParseQuery<ParseObject> bookingsQuery = ParseQuery.getQuery("Bookings");
		bookingsQuery.include("user");
		bookingsQuery.whereEqualTo("flight", flightDetails);
		bookingsQuery.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> parseObjects, ParseException e) {
				// TODO Make the seats yellow
				if (e == null) {
					occupiedSeats = parseObjects;
					for (ParseObject booking : parseObjects) {
						int seat = booking.getInt("seatNo");
						occupied[seat] = true;
						if (seat < 3 * 5) {
							grid1.getChildAt(seat).setBackgroundResource(R.color.seatOccupied);
						} else {
							grid2.getChildAt(seat % (3 * 5)).setBackgroundResource(R.color.seatOccupied);
						}
					}
				}
			}
		});
	}

	View.OnClickListener gridListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Integer position = (Integer) v.getTag();
			if (position != null) {
				if (!occupied[position]) {
					if (prevChosenPos != -1) {
						View prevView = (prevChosenPos < 3 * 5) ? grid1.getChildAt(prevChosenPos)
								: grid2.getChildAt(prevChosenPos % (3 * 5));
						prevView.setBackgroundResource(occupied[prevChosenPos] ? R.color.seatOccupied : R.color.seatEmpty);
					}
					v.setBackgroundResource(R.color.seatChosen);
					prevChosenPos = position;
					chooseSeatButton.setEnabled(true);
				} else {
					showUserDialog(position);
				}
			}
		}
	};

	private void showUserDialog(Integer position) {
		checkingUser = getUserAtSeat(position);
		if (checkingUser == null) return;
		if (!getFlightDetailsAtSeat(position).getBoolean("shareMe")) {
			showToast("Social details hidden");
			return;
		}
		View userRootView = getLayoutInflater().inflate(R.layout.user_view, null);
		ParseImageView piv = (ParseImageView) userRootView.findViewById(R.id.profile_pic);
		piv.setPlaceholder(getResources().getDrawable(R.drawable.big_profile_placeholder));
		piv.setParseFile(checkingUser.getParseFile("profilePic"));
		piv.loadInBackground();
		TextView infoTV = (TextView) userRootView.findViewById(R.id.infoTV);
		TypefaceHelper.getInstance().setTypeface(infoTV, "fonts/roboto_condensed_regular.ttf");
		infoTV.setText(checkingUser.getString("name"));
		infoTV.append("\n\nAge: " + checkingUser.getString("age"));
		infoTV.append("\n\nEmail: " + checkingUser.getEmail());
		infoTV.append("\n\nPhone: " + checkingUser.getString("phone"));
		infoTV.append("\n\nLives at: " + checkingUser.getString("location"));
		infoTV.append("\n\nProfession: " + checkingUser.getString("profession"));
		infoTV.append("\n\nFavourite Quote: " + checkingUser.getString("faveQuote"));
		infoTV.append("\n\nAbout: " + checkingUser.getString("tellUs"));
		infoTV.append("\n\nDisabilities: " + checkingUser.getString("disability"));
		new AlertDialog.Builder(this)
				.setView(userRootView)
				.setPositiveButton("Ping", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (checkingUser.equals(ParseUser.getCurrentUser())) {
							showToast("Cannot ping yourself :P");
							return;
						}
						showToast("Your profile has been sent to the other person");
						ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
						pushQuery.whereEqualTo("user", checkingUser);
						// Push to other person
						ParsePush push = new ParsePush();
						push.setQuery(pushQuery);
						ParseUser me = ParseUser.getCurrentUser();
						try {
							String msg = me.getString("name") + " wants to join you in your flight. Accept to confirm.";
							push.setData(new JSONObject()
									.put("action", "Request")
									.put("alert", msg)
									.put("user", me.getObjectId()));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						push.sendInBackground();
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.show();
	}

	private ParseUser getUserAtSeat(int seatNo) {
		for (ParseObject pobj : occupiedSeats) {
			if (pobj.getNumber("seatNo").intValue() == seatNo) {
				return pobj.getParseUser("user");
			}
		}
		return null;
	}

	private ParseObject getFlightDetailsAtSeat(int seatNo) {
		for (ParseObject pobj : occupiedSeats) {
			if (pobj.getNumber("seatNo").intValue() == seatNo) {
				return pobj;
			}
		}
		return null;
	}

	public void chooseSeatClicked(View view) {
		if (occupied[prevChosenPos]) {
			showToast("Seat occupied! Change it");
		} else {
			Intent intent = new Intent(this, MakePaymentActivity.class);
			intent.putExtra("seatNo", prevChosenPos);
			startActivity(intent);
		}
	}
}
