package rakco.com.travelconnectmindtree;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class PushBroadcastReceiver extends ParsePushBroadcastReceiver {
	@Override
	protected Class<? extends Activity> getActivity(Context context, Intent intent) {
		return UpdatesActivity.class;
	}
}
