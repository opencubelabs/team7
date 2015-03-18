package rakco.com.travelconnectmindtree;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class BookFlightActivity extends DadaActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_flight);
		String title = getIntent().getStringExtra("title");
		setTitle(title);

		ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		viewPager.setAdapter(new BookFlightPagerAdapter(getSupportFragmentManager()));
	}

	public static class BookFlightPagerAdapter extends FragmentPagerAdapter {
		private static int NUM_ITEMS = 7;
		Calendar todayCal;

		public BookFlightPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
			todayCal = new GregorianCalendar();
			// Reset hour, minutes, seconds and millis
			todayCal.set(Calendar.HOUR_OF_DAY, 0);
			todayCal.set(Calendar.MINUTE, 0);
			todayCal.set(Calendar.SECOND, 0);
			todayCal.set(Calendar.MILLISECOND, 0);
		}

		// Returns total number of pages
		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		// Returns the fragment to display for that page
		@Override
		public Fragment getItem(int position) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(todayCal.getTime());
			cal.add(Calendar.DAY_OF_MONTH, position);
			return FlightListFragment.newInstance(cal.getTime());
		}

		// Returns the page title for the top indicator
		@Override
		public CharSequence getPageTitle(int position) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(todayCal.getTime());
			cal.add(Calendar.DAY_OF_MONTH, position);
			Date future = cal.getTime();
			return new SimpleDateFormat("d MMM, EEE", Locale.US).format(future);
		}

	}
}
