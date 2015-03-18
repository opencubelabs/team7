package rakco.com.travelconnectmindtree;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.drivemode.android.typeface.TypefaceHelper;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class DadaActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		TypefaceHelper.getInstance().setTypeface(this, "fonts/roboto_condensed_regular.ttf");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean hasNetwork() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnected();
	}


	public void showToast(String toastMsg) {
		Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
	}

	public void startActivity(Class<?> intentClass) {
		Intent intent = new Intent(this, intentClass);
		startActivity(intent);
	}
}
