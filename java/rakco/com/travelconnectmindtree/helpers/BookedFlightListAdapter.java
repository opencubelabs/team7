package rakco.com.travelconnectmindtree.helpers;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drivemode.android.typeface.TypefaceHelper;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rakco.com.travelconnectmindtree.FlightListFragment;
import rakco.com.travelconnectmindtree.R;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class BookedFlightListAdapter extends RecyclerView.Adapter<BookedFlightListAdapter.BookedFlightListViewHolder> {
	public static final char RUPEE = '\u20B9';
	public static ParseObject flightObject;
	List<ParseObject> flights;

	public BookedFlightListAdapter(List<ParseObject> mflights) {
		flights = mflights;
	}

	@Override
	public BookedFlightListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		CardView rootView = (CardView) LayoutInflater.from(parent.getContext())
				.inflate(R.layout.booked_flight_list_item, parent, false);
		TypefaceHelper.getInstance().setTypeface(rootView, "fonts/roboto_condensed_regular.ttf");
		return new BookedFlightListViewHolder(rootView);
	}

	@Override
	public void onBindViewHolder(BookedFlightListViewHolder holder, int position) {
		ParseObject flight = flights.get(position);
		holder.fromTV.setText("From: " + flight.getString("from"));
		holder.toTV.setText("To: " + flight.getString("to"));

		Date depDate = flight.getDate(FlightListFragment.DEPARTURE_AT);
		String depTime = new SimpleDateFormat("d MMM, yyyy  hh:mm a", Locale.US).format(depDate);
		holder.departureTV.setText("Departure: " + depTime);

		holder.fareTV.setText("Fare: " + flight.getString("fare"));
	}

	@Override
	public int getItemCount() {
		return flights.size();
	}

	public class BookedFlightListViewHolder extends RecyclerView.ViewHolder {
		TextView fromTV, toTV, departureTV, fareTV;

		public BookedFlightListViewHolder(View root) {
			super(root);
			fromTV = (TextView) root.findViewById(R.id.fromTV);
			toTV = (TextView) root.findViewById(R.id.toTV);
			departureTV = (TextView) root.findViewById(R.id.departureTV);
			fareTV = (TextView) root.findViewById(R.id.fareTV);
		}
	}
}
