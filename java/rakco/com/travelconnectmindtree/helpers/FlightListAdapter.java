package rakco.com.travelconnectmindtree.helpers;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.drivemode.android.typeface.TypefaceHelper;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rakco.com.travelconnectmindtree.FlightListFragment;
import rakco.com.travelconnectmindtree.PickSeatActivity;
import rakco.com.travelconnectmindtree.R;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class FlightListAdapter extends RecyclerView.Adapter<FlightListAdapter.FlightListViewHolder> implements View.OnClickListener {
	public static final char RUPEE = '\u20B9';
	public static ParseObject flightObject;
	List<ParseObject> flights;

	public FlightListAdapter(List<ParseObject> mflights) {
		flights = mflights;
	}

	@Override
	public FlightListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		CardView rootView = (CardView) LayoutInflater.from(parent.getContext())
				.inflate(R.layout.flight_list_item, parent, false);
		TypefaceHelper.getInstance().setTypeface(rootView, "fonts/roboto_condensed_regular.ttf");
		return new FlightListViewHolder(rootView);
	}

	@Override
	public void onBindViewHolder(FlightListViewHolder holder, int position) {
		ParseObject flight = flights.get(position);
		holder.fromTV.setText("From: " + flight.getString("from"));
		holder.toTV.setText("To: " + flight.getString("to"));

		Date depDate = flight.getDate(FlightListFragment.DEPARTURE_AT);
		String depTime = new SimpleDateFormat("hh:mm a", Locale.US).format(depDate);
		holder.departureTV.setText("Departure: " + depTime);

		holder.fareTV.setText("Fare: " + flight.getString("fare"));

		holder.bookButton.setTag(position);
		holder.bookButton.setOnClickListener(this);
	}

	@Override
	public int getItemCount() {
		return flights.size();
	}

	@Override
	public void onClick(View v) {
		Integer position = (Integer) v.getTag();
		if (position == null) {
			return;
		}
		flightObject = flights.get(position);
		Intent intent = new Intent(v.getContext(), PickSeatActivity.class);
		v.getContext().startActivity(intent);
	}

	public class FlightListViewHolder extends RecyclerView.ViewHolder {
		TextView fromTV, toTV, departureTV, fareTV;
		Button bookButton;

		public FlightListViewHolder(View root) {
			super(root);
			fromTV = (TextView) root.findViewById(R.id.fromTV);
			toTV = (TextView) root.findViewById(R.id.toTV);
			departureTV = (TextView) root.findViewById(R.id.departureTV);
			fareTV = (TextView) root.findViewById(R.id.fareTV);

			bookButton = (Button) root.findViewById(R.id.book_flight_button);
		}
	}
}
