package rakco.com.travelconnectmindtree;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import rakco.com.travelconnectmindtree.helpers.FlightListAdapter;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class FlightListFragment extends Fragment {
	public static final String DATE_VALUE = "date";
	public static final String FLIGHT_VALUE = "Flight";
	public static final String DEPARTURE_AT = "departureAt";
	private static final String TAG = "FlightsListFragment";
	private Date showDate;
	private RecyclerView recyclerView;
	private ProgressBar progressBar;

	public static FlightListFragment newInstance(Date date) {
		FlightListFragment flf = new FlightListFragment();
		Bundle args = new Bundle();
		args.putSerializable(DATE_VALUE, date);
		flf.setArguments(args);
		return flf;
	}

	// Store instance variables based on arguments passed
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showDate = (Date) getArguments().getSerializable(DATE_VALUE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_flight_details, container, false);
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setItemAnimator(new DefaultItemAnimator());

		ParseQuery<ParseObject> flightQuery = ParseQuery.getQuery(FLIGHT_VALUE);
		flightQuery.orderByAscending(DEPARTURE_AT);
		flightQuery.whereGreaterThanOrEqualTo(DEPARTURE_AT, showDate);
		flightQuery.whereLessThan(DEPARTURE_AT, addOneDay(showDate));
		flightQuery.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> mflights, ParseException e) {
				if (e == null && mflights != null && mflights.size() > 0) {
					FlightListAdapter fla = new FlightListAdapter(mflights);
					recyclerView.setAdapter(fla);
				} else {
					Log.i(TAG, "Query problem occurred!");
				}
				progressBar.setVisibility(View.GONE);
			}
		});
		return rootView;
	}

	private Date getToday() {
		// Today
		Calendar date = new GregorianCalendar();
		// Reset hour, minutes, seconds and millis
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		return date.getTime();
	}

	private Date getTomorrow() {
		Calendar date = Calendar.getInstance();
		date.setTime(getToday());
		// Tomorrow
		date.add(Calendar.DAY_OF_MONTH, 1);
		return date.getTime();
	}

	private Date addOneDay(Date dateO) {
		Calendar date = Calendar.getInstance();
		date.setTime(dateO);
		// Reset hour, minutes, seconds and millis
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		// Tomorrow
		date.add(Calendar.DAY_OF_MONTH, 1);
		return date.getTime();
	}
}
