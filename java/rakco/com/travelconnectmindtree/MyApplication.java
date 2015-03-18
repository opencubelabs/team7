package rakco.com.travelconnectmindtree;

import android.app.Application;

import com.drivemode.android.typeface.TypefaceHelper;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by Rakshak.R.Hegde on 1-03-2015.
 */
public class MyApplication extends Application {
	private static final String TAG = "MyApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		// Enable Local Datastore.
		Parse.enableLocalDatastore(this);

		Parse.initialize(this, "YOUR_CONSUMER_KEY",
				"YOUR_SECRET_KEY");

		TypefaceHelper.initialize(this);
	}

	private void test() {
		// TODO Remove this method call
	}

	@Override
	public void onTerminate() {
		TypefaceHelper.destroy();

		super.onTerminate();
	}
}
