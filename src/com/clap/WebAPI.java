package com.clap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class WebAPI {
	private Context context;

	public static final String ERROR_EMPTY_LIST = "Received Empty List From Server";
	public static final String ERROR_INVALID_LIST = "Received Invalid List From Server";
	public static final String ERROR_NO_CONNECTION = "No Data Connection";

	// enum to hold all the HTTP GET requests we use
	public enum HTTP_GET {
		COUNTRIES("http://www.celebrate-language.com/public-api/?action=get_country_list"),
		LANGUAGES("http://www.celebrate-language.com/public-api/?action=get_lang_list_by&country="),
		LESSONS("http://www.celebrate-language.com/public-api/?action=get_lessons_by_lang&lang="),
		PHRASES("http://www.celebrate-language.com/public-api/?action=get_phrases_by_lesson_id&id="),
		PHRASE_ORDER("http://www.celebrate-language.com/public-api/?action=get_order_by_lesson_id&id=");
		
		private String httpGetString;
		
		private HTTP_GET(String s) {
			this.httpGetString = s;
		}
		
		public String stringValue() {
			return this.httpGetString;
		}
	}

	public WebAPI(Context c) {
		context = c;
	}

	public void DownloadAndSaveAudio(String urlString, File audioFile)
		throws IOException {

		if (!hasDataConnection()) {
			throw new RuntimeException(ERROR_NO_CONNECTION);
		}

		try {
			// Open a GET connection to the URL for the Audio File
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.connect();
			
			// Open the Audio File for writing
			FileOutputStream fileOutput = new FileOutputStream(audioFile);

			// Open the URL stream
			InputStream inputStream = urlConnection.getInputStream();
			
			int totalSize = urlConnection.getContentLength();
			int downloadedSize = 0;
			byte[] buffer = new byte[1024];
			int bufferLength = 0;
			
			// Read from the URL and write to the file
			while ((bufferLength = inputStream.read(buffer)) > 0) {
				fileOutput.write(buffer, 0, bufferLength);
				downloadedSize += bufferLength;
				int progress = (int)(downloadedSize*100/totalSize);
				Log.d("downloading file", "progress: " + String.valueOf(progress));
			}
			
			fileOutput.close();

			// If for some reason the file is empty, delete it
			if (audioFile.length() == 0) {
				audioFile.delete();
			}
		} catch (FileNotFoundException e) {
			// Something went wrong, the file is probably corrupt or empty, delete it
			audioFile.delete();
			throw new IOException("Audio File Not Downloaded\n\nURL Not Found:\n" + urlString);
		} catch (IOException e) {
			// Something went wrong, the file is probably corrupt or empty, delete it
			audioFile.delete();
			throw new IOException("Audio File NOt Downloaded\n\n" + e.getMessage());
		}
	}

	public String getJSONArray(HTTP_GET httpGetValue) throws Exception {
		return getJSONArray(httpGetValue, "");
	}

	public String getJSONArray(HTTP_GET httpGetValue, String httpGetParam) throws Exception {
		if (!hasDataConnection()) {
			throw new RuntimeException(ERROR_NO_CONNECTION);
		}

		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		// Create the HTTP GET request with the given parameter
		HttpGet httpGet = new HttpGet(httpGetValue.stringValue() + encodeParam(httpGetParam));
		String text = null;

		HttpResponse response = httpClient.execute(httpGet, localContext);
		HttpEntity entity = response.getEntity();
		text = getASCIIContentFromEntity(entity);
		
		if (text == null || text.equals("")
				|| text.equals("[]") || text.equals("[null]")) {
			// Empty List
			throw new RuntimeException(ERROR_EMPTY_LIST);
		} else if (!text.startsWith("[") || !text.endsWith("]")
				|| text.matches("www.celebrate-language.com")) {
			// We received something weird
			throw new RuntimeException(ERROR_INVALID_LIST + ": " + text);
		} else {
			// We don't need the surrounding square brackets
			Pattern p = Pattern.compile("^" + Pattern.quote("[") + "(.*)" + Pattern.quote("]") + "$");
			Matcher matcher = p.matcher(text);
			if (matcher.find()) {
				return matcher.group(1);
			} else {
				throw new RuntimeException(ERROR_INVALID_LIST + ": " + text);
			}
		}
	}

	private String encodeParam(String stringToEncode) {
		try {
			return URLEncoder.encode(stringToEncode, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return stringToEncode;
		}
		
	}

	private String getASCIIContentFromEntity(HttpEntity entity)
			throws IllegalStateException, IOException {
		InputStream in = entity.getContent();
		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n > 0) {
			byte[] b = new byte[4096];
			n = in.read(b);
			if (n > 0)
				out.append(new String(b, 0, n, "UTF-8"));
		}
		return out.toString();
	}
	
	private boolean hasDataConnection() {
		ConnectivityManager connectManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectManager.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnectedOrConnecting());
	}
}