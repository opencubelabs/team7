package rakco.com.travelconnectmindtree;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class FlightAlarmReceiver extends BroadcastReceiver {
	private static final String TAG = "FlightAlarmReceiver";
	Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		// TODO Create a notif telling the flight name and time
		createTestNotif();
	}

	private void createTestNotif() {
		Log.i(TAG, "in createTestNotif()");
		Notification notif = new NotificationCompat.Builder(context)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle("Reminder for flight")
				.setContentText("You have a flight")
				.setDefaults(Notification.DEFAULT_SOUND)
				.setAutoCancel(true)
				.build();
		// Sets an ID for the notification
		int mNotificationId = 065641; // octal :P
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(mNotificationId, notif);
	}

}
