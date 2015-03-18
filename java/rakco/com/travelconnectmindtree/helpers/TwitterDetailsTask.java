package rakco.com.travelconnectmindtree.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseFile;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.twitter.Twitter;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class TwitterDetailsTask extends AsyncTask<Void, Void, Void> {

	private static final String TAG = "TwitterDetailsTask";
	Context context;

	public TwitterDetailsTask(Context context) {
		this.context = context;
	}

	@Override
	protected Void doInBackground(Void... params) {
		Twitter myTwitter = ParseTwitterUtils.getTwitter();
		HttpClient client = new DefaultHttpClient();
		String screenName = myTwitter.getScreenName();
		Log.i(TAG, "Screen name: " + screenName);
		HttpGet verifyGet = new HttpGet(
				"https://api.twitter.com/1.1/users/show.json?screen_name=" + screenName);
		myTwitter.signRequest(verifyGet);
		try {
			HttpResponse response = client.execute(verifyGet);
			String result = EntityUtils.toString(response.getEntity());
			JSONObject responseJson = new JSONObject(result);
			setCurrentUser(responseJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void setCurrentUser(JSONObject data) throws Exception {
		ParseUser me = ParseUser.getCurrentUser();
		if (data.has("name")) {
			me.put("name", data.getString("name"));
		}
		if (data.has("profile_image_url")) {
			String uri = data.getString("profile_image_url");
			uri = uri.replace("_normal", "");
			Bitmap profilePic = Picasso.with(context).load(uri).get();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			profilePic.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			stream.close();
			ParseFile profilePicFile = new ParseFile(byteArray);
			me.put("profilePic", profilePicFile);
		}
		if (data.has("location")) {
			me.put("location", data.getString("location"));
		}
		if (data.has("screen_name")) {
			me.setUsername(data.getString("screen_name"));
		}
		me.saveInBackground();
	}
}