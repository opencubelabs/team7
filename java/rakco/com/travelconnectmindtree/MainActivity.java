package rakco.com.travelconnectmindtree;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.Calendar;

public class MainActivity extends DadaActivity {

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getSupportActionBar().setDisplayUseLogoEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			TextView nameTV = (TextView) findViewById(R.id.nameTV);
			String name = currentUser.getString("name");
			if (name != null)
				nameTV.setText(name);
		}
		if (currentUser == null || TextUtils.isEmpty(currentUser.getString("name"))) {
			TextView helloTV = (TextView) findViewById(R.id.helloTV);
			helloTV.setText("Hello there");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void updateProfileClicked(View view) {
		Intent intent = new Intent(this, CreateProfileActivity.class);
		intent.putExtra("type", "Update");
		startActivity(intent);
	}

	public void logoutClicked(MenuItem item) {
		ParseUser.logOut();
		startActivity(LoginActivity.class);
		finish();
	}

	public void bookFlightClicked(View view) {
		//TODO Book Flight
		Intent intent = new Intent(this, BookFlightActivity.class);
		intent.putExtra("title", "Book Flight");
		startActivity(intent);
	}

	public void bookedFlightsClicked(View view) {
		// TODO Show booked flights
		startActivity(BookedFlightsActivity.class);
	}
}
