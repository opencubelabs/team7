package rakco.com.travelconnectmindtree;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.parse.ParseImageView;
import com.parse.ParseUser;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class CreateProfileActivity extends DadaActivity {
	EditText nameBox, ageBox, phoneBox, emailBox, locationBox, professionBox, faveQuoteBox, tellUsBox;
	Spinner disabilitiesSpinner;
	ArrayAdapter<CharSequence> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String type = getIntent().getStringExtra("type");
		if (type != null) {
			setTitle(type);
		}
		setContentView(R.layout.activity_create_profile);

		disabilitiesSpinner = (Spinner) findViewById(R.id.disabilities_spinner);
		adapter = ArrayAdapter.createFromResource(this,
				R.array.disabilities_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		disabilitiesSpinner.setAdapter(adapter);

		nameBox = getET(R.id.nameBox);
		ageBox = getET(R.id.ageBox);
		phoneBox = getET(R.id.phoneBox);
		emailBox = getET(R.id.emailBox);
		locationBox = getET(R.id.locationBox);
		professionBox = getET(R.id.professionBox);
		faveQuoteBox = getET(R.id.faveQuoteBox);
		tellUsBox = getET(R.id.tellUsBox);


		ParseUser me = ParseUser.getCurrentUser();
		if (me != null) {
			nameBox.setText(me.getString("name"));
			ageBox.setText(me.getString("age"));
			phoneBox.setText(me.getString("phone"));
			emailBox.setText(me.getEmail());
			locationBox.setText(me.getString("location"));
			professionBox.setText(me.getString("profession"));
			faveQuoteBox.setText(me.getString("faveQuote"));
			tellUsBox.setText(me.getString("tellUs"));
			String disability = me.getString("disability");
			for (int i = 0; i < adapter.getCount(); i++) {
				if (adapter.getItem(i).equals(disability)) {
					disabilitiesSpinner.setSelection(i, true);
					break;
				}
			}
		}

		ParseImageView piv=(ParseImageView)findViewById(R.id.profile_pic);
		piv.setPlaceholder(getResources().getDrawable(R.drawable.big_profile_placeholder));
		piv.setParseFile(ParseUser.getCurrentUser().getParseFile("profilePic"));
		piv.loadInBackground();
	}

	private EditText getET(int id) {
		return (EditText) findViewById(id);
	}

	public void submitClicked(View view) {
		// TODO Creating Profile
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			currentUser.put("name", nameBox.getText().toString());
			currentUser.put("age", ageBox.getText().toString());
			currentUser.put("phone", phoneBox.getText().toString());
			currentUser.setEmail(emailBox.getText().toString());
			currentUser.put("location", locationBox.getText().toString());
			currentUser.put("profession", professionBox.getText().toString());
			currentUser.put("faveQuote", faveQuoteBox.getText().toString());
			currentUser.put("tellUs", tellUsBox.getText().toString());
			currentUser.put("disability", adapter.getItem(
					disabilitiesSpinner.getSelectedItemPosition()).toString());
			currentUser.saveInBackground();
			showToast("Please check your email for confirmation");
			startActivity(MainActivity.class);
			finish();
		} else {
			showToast("Please login first");
		}
	}
}
