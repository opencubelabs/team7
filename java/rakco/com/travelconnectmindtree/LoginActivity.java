package rakco.com.travelconnectmindtree;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

import rakco.com.travelconnectmindtree.helpers.TwitterDetailsTask;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class LoginActivity extends DadaActivity {
	private static final String TAG = "LoginActivity";
	private static final String TWITTER_CONSUMER_KEY = "9G896HETFVbdOmmcEr9IiZhcr";
	private static final String TWITTER_CONSUMER_SECRET = "P6D85pe9aCrr0PiykubYcKEF2TrlxkgJN5fTDzl6m7PhVF31yD";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			startActivity(MainActivity.class);
			finish();
		}
	}

	public void loginFBClicked(View view) {
		List<String> permissions = Arrays.asList("public_profile", "user_about_me", "user_birthday", "user_location");
		ParseFacebookUtils.logIn(permissions,
				this, new LogInCallback() {
					@Override
					public void done(ParseUser user, ParseException err) {
						if (user == null) {
							Log.i(TAG, "Uh oh. The user cancelled the Facebook login.");
							if (err != null)
								err.printStackTrace();
							showToast("Facebook login error. Try again...");
						} else {
							Log.i(TAG, "User logged in through Facebook!");
							nextSteps();
						}
					}
				});
	}

	public void loginTwitterClicked(View view) {
		ParseTwitterUtils.initialize(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
		ParseTwitterUtils.logIn(this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				if (user == null) {
					Log.i(TAG, "Uh oh. The user cancelled the Twitter login.");
					if (err != null)
						err.printStackTrace();
					showToast("Twitter login error. Try again...");
				} else {
					Log.i(TAG, "User logged in through Twitter!");
					new TwitterDetailsTask(getBaseContext()).execute();
					Log.i(TAG, "loginTwitterClicked()");
					nextSteps();
				}
			}
		});
	}

	private void nextSteps() {
		ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();
		currentInstallation.put("user", ParseUser.getCurrentUser());
		currentInstallation.saveInBackground();
		startActivity(CreateProfileActivity.class);
		finish();
	}
}
