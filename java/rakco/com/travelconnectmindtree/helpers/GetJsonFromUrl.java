package rakco.com.travelconnectmindtree.helpers;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class GetJsonFromUrl extends AsyncTask<String, Void, JSONObject> {
	JsonCallback callback;

	public GetJsonFromUrl(JsonCallback jcb) {
		callback = jcb;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		String uri=params[0];
		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(new URL(uri).openStream()));
			String line;
			StringBuilder sb=new StringBuilder();
			while((line=reader.readLine())!=null) {
				sb.append(line);
			}
			return new JSONObject(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(JSONObject jsonObject) {
		callback.onJsonResult(jsonObject);
	}

	public interface JsonCallback {
		public void onJsonResult(JSONObject json);
	}
}
