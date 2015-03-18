package rakco.com.travelconnectmindtree;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class BookedFlightsActivity extends DadaActivity {
	List<ParseObject> myFlights, pastFlights, futureFlights;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booked_flights);

		final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

		ParseQuery<ParseObject> bookingsQuery = ParseQuery.getQuery("Bookings")
				.whereEqualTo("user", ParseUser.getCurrentUser());
		if (!hasNetwork()) {
			bookingsQuery.fromLocalDatastore();
		}
		bookingsQuery.include("flight");
		bookingsQuery.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> bookings, ParseException e) {
				if (e != null) {
					e.printStackTrace();
					return;
				}
				ParseObject.pinAllInBackground(bookings);
				myFlights = new ArrayList<ParseObject>(bookings.size());
				for (ParseObject booking : bookings) {
					myFlights.add(booking.getParseObject("flight"));
				}
				Collections.sort(myFlights, new Comparator<ParseObject>() {
					@Override
					public int compare(ParseObject lhs, ParseObject rhs) {
						return lhs.getDate("departureAt").compareTo(rhs.getDate("departureAt"));
					}
				});
				int i = 0;
				Date now = new Date();
				for (ParseObject flight : myFlights) {
					if (flight.getDate("departureAt").compareTo(now) > 0) break;
					i++;
				}
				pastFlights = myFlights.subList(0, i);
				futureFlights = myFlights.subList(i, myFlights.size());

				viewPager.setAdapter(new BookedFlightsPagerAdapter(getSupportFragmentManager()));
				findViewById(R.id.progressBar).setVisibility(View.GONE);
			}
		});
	}

	public class BookedFlightsPagerAdapter extends FragmentPagerAdapter {
		private int NUM_ITEMS = 2;

		public BookedFlightsPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		// Returns total number of pages
		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		// Returns the fragment to display for that page
		@Override
		public Fragment getItem(int position) {
			return BookedFlightListFragment.newInstance(position == 0 ? futureFlights : pastFlights);
		}

		// Returns the page title for the top indicator
		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return "Upcoming Flights";
				case 1:
					return "Past Flights";
				default:
					return "Flights";
			}
		}

	}
}
