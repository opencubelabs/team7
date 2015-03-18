package rakco.com.travelconnectmindtree;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseObject;

import java.util.List;

import rakco.com.travelconnectmindtree.helpers.BookedFlightListAdapter;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class BookedFlightListFragment extends Fragment {
	public static final String DATE_VALUE = "date";
	public static final String FLIGHT_VALUE = "Flight";
	public static final String DEPARTURE_AT = "departureAt";
	private static final String TAG = "FlightsListFragment";
	private List<ParseObject> mflights;
	private RecyclerView recyclerView;

	public static BookedFlightListFragment newInstance(List<ParseObject> mflights) {
		BookedFlightListFragment bflf = new BookedFlightListFragment();
		bflf.mflights = mflights;
		return bflf;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_booked_flights, container, false);
		recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setItemAnimator(new DefaultItemAnimator());

		recyclerView.setAdapter(new BookedFlightListAdapter(mflights));
		return rootView;
	}
}
