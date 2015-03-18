package rakco.com.travelconnectmindtree;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
public class UpdatesActivity extends DadaActivity {
	private static final String TAG = "UpdatesActivity";
	String alert, userId;
	JSONObject jsonObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		String jsonData = extras.getString("com.parse.Data");
		try {
			jsonObject = new JSONObject(jsonData);
			alert = jsonObject.getString("alert");
			decide();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i(TAG, jsonData);
	}

	private void decide() throws JSONException {
		String action = jsonObject.has("action") ? jsonObject.getString("action") : "";
		if ("Request".equals(action)) {
			showRequestDialog();
		} else if ("Booking".equals(action)) {
			showBookingDialog();
		} else {
			notifyAlert(action);
		}
	}

	private void showBookingDialog() throws JSONException {
		userId = jsonObject.getString("user");
		if (TextUtils.isEmpty(userId)) return;
		new AlertDialog.Builder(this)
				.setTitle("Booking")
				.setMessage(alert)
				.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						bookingFeedback(true);
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						bookingFeedback(false);
					}
				})
				.show();
	}

	private void bookingFeedback(final boolean affirmative) {
		ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
		userQuery.whereEqualTo("objectId", userId);
		userQuery.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> parseUsers, ParseException e) {
				if (parseUsers != null && parseUsers.size() > 0) {
					ParseUser reqUser = parseUsers.get(0);
					ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
					pushQuery.whereEqualTo("user", reqUser);
					ParsePush push = new ParsePush();
					push.setQuery(pushQuery);

					ParseUser me = ParseUser.getCurrentUser();
					try {
						String msg = me.getString("name") + (affirmative ? " will" : " cannot") + " fly with you.";
						if (affirmative)
							msg += " So you get an extra 15% off on your flight bill.";
						push.setData(new JSONObject()
								.put("action", "Reply")
								.put("alert", msg)
								.put("user", me.getObjectId()));
						push.sendInBackground();
					} catch (JSONException jsone) {
						e.printStackTrace();
					}
					finish();
				}
			}
		});
		if (affirmative) {
			ParseQuery<ParseObject> flightQuery = new ParseQuery<ParseObject>("Flight");
			try {
				flightQuery.whereEqualTo("objectId", jsonObject.getString("flightId"));
				flightQuery.findInBackground(new FindCallback<ParseObject>() {
					@Override
					public void done(List<ParseObject> parseObjects, ParseException e) {
						FlightListAdapter.flightObject = parseObjects.get(0);
						startActivity(PickSeatActivity.class);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void showRequestDialog() throws JSONException {
		userId = jsonObject.getString("user");
		if (TextUtils.isEmpty(userId)) return;
		new AlertDialog.Builder(this)
				.setMessage(alert)
				.setTitle("Request")
				.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						requestFeedback(true);
					}
				})
				.setNeutralButton("Show Profile", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						showProfile();
						try {
							showRequestDialog();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						requestFeedback(false);
					}
				})
				.show();
	}

	private void showProfile() {
		ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
		userQuery.whereEqualTo("objectId", userId);
		userQuery.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> parseUsers, ParseException e) {
				if (parseUsers.size() > 0) {
					ParseUser reqUser = parseUsers.get(0);
					showUserDialog(reqUser);
				}
			}
		});
	}

	private void requestFeedback(final boolean affirmative) {
		// TODO Send confirmation that you're going together
		ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
		userQuery.whereEqualTo("objectId", userId);
		userQuery.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> parseUsers, ParseException e) {
				if (parseUsers != null && parseUsers.size() > 0) {
					ParseUser reqUser = parseUsers.get(0);
					ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
					pushQuery.whereEqualTo("user", reqUser);
					ParsePush push = new ParsePush();
					push.setQuery(pushQuery);

					ParseUser me = ParseUser.getCurrentUser();
					try {
						String msg = me.getString("name") + (affirmative ? " accepted" : " rejected") + " your request";
						push.setData(new JSONObject()
								.put("action", "Reply")
								.put("alert", msg)
								.put("user", me.getObjectId()));
					} catch (JSONException jsone) {
						e.printStackTrace();
					}
					push.sendInBackground();
					finish();
				}
			}
		});
	}

	private void showUserDialog(final ParseUser checkingUser) {
		if (checkingUser == null) return;
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
				.setPositiveButton(android.R.string.ok, null)
				.show();
	}

	private void notifyAlert(String title) {
		new AlertDialog.Builder(this)
				.setTitle(title)
				.setMessage(alert)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.show();
	}
}
