package com.utapps.networkconnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MyHttpActivity extends ActionBarActivity {

	private static final String DEBUG_TAG = "MyHttpActivity";

	private EditText mUrlTextEditText;
	private TextView mTextView;
	private Button mButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_http);

		mUrlTextEditText = (EditText) findViewById(R.id.myUrlEditText);
		mButton = (Button) findViewById(R.id.myButton);
		mTextView = (TextView) findViewById(R.id.myTextView);

		mButton.setOnClickListener(new View.OnClickListener() {

			// When user click button calls AsynTask
			// Before attempting to fetch URL, make sure that there is network
			// connection.

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Gets the URL from Edit Text.
				String stringUrl = mUrlTextEditText.getText().toString();
				ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connectivityManager
						.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					new DownloadWebpageTask().execute(stringUrl);
				} else {
					mTextView.setText("No Network connection available");
				}
			}
		});
	}

	// Uses AsyncTask to create a task away from the main UI thread. This task
	// takes a
	// URL string and uses it to create an HttpUrlConnection. Once the
	// connection
	// has been established, the AsyncTask downloads the contents of the webpage
	// as
	// an InputStream. Finally, the InputStream is converted into a string,
	// which is
	// displayed in the UI by the AsyncTask's onPostExecute method.

	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			// params come from execute() call: param[0] is the URL.

			try {
				return downloadUrl(urls[0]);
			} catch (IOException e) {
				return "Unable to retrieve webpage, Url may be invalid";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mTextView.setText(result);
		}
	}

	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.

	private String downloadUrl(String myUrl) throws IOException {
		InputStream inputStream = null;

		// Only display the first 500 characters of the retrieved
		// web page content.

		int length = 100;

		try {
			URL url = new URL(myUrl);

			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setReadTimeout(10000); // 10000milliseconds
			httpURLConnection.setConnectTimeout(15000);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setDoInput(true);

			// Start the query
			int response = httpURLConnection.getResponseCode();
			Log.d(DEBUG_TAG, "The response is: " + response);
			inputStream = httpURLConnection.getInputStream();

			// Convert Stream to String
			String contentAsString = streamToString(inputStream, length);
			return contentAsString;
		}
		// Makes sure that the InputStream is closed after the app is
		// finished using it.
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

	}

	// Reads InputStream and converts it into String
	public String streamToString(InputStream stream, int length)
			throws UnsupportedEncodingException, IOException {
		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");
		char[] buffer = new char[length];
		reader.read(buffer);
		return new String(buffer);
	}

}
