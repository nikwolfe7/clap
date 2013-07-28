package com.example.languageproject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

import android.util.Log;

public class WebAPI {

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

	public static void DownloadAndSaveAudio(String urlString, File audioFile)
			throws MalformedURLException, IOException {
		URL url;
		url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod("GET");
		urlConnection.setDoOutput(true);
		urlConnection.connect();
		
		FileOutputStream fileOutput = new FileOutputStream(audioFile);
		InputStream inputStream = urlConnection.getInputStream();
		
		int totalSize = urlConnection.getContentLength();
		int downloadedSize = 0;
		byte[] buffer = new byte[1024];
		int bufferLength = 0;
		
		while ((bufferLength = inputStream.read(buffer)) > 0) {
			fileOutput.write(buffer, 0, bufferLength);
			downloadedSize += bufferLength;
			int progress = (int)(downloadedSize*100/totalSize);
			Log.d("downloading file", "progress: " + String.valueOf(progress));
		}
		
		fileOutput.close();
	}

	public static String getJSONArray(HTTP_GET httpGetValue) throws Exception {
		return getJSONArray(httpGetValue, "");
	}

	public static String getJSONArray(HTTP_GET httpGetValue, String httpGetParam) throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(httpGetValue.stringValue() + encodeParam(httpGetParam));
		String text = null;
		try {
			HttpResponse response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			text = getASCIIContentFromEntity(entity);
		} catch (Exception e) {
			return e.getLocalizedMessage();
		}
		
		if (text != null) {
			if (text.equals("[]") || text.equals("[null]")) {
				throw new Exception("Empty List");
			} else if (!text.startsWith("[") || !text.endsWith("]")) {
				throw new Exception("Invalid List: " + text);
			} else {
				Pattern p = Pattern.compile("^" + Pattern.quote("[") + "(.*)" + Pattern.quote("]") + "$");
				Matcher matcher = p.matcher(text);
				if (matcher.find()) {
					return matcher.group(1);
				} else {
					throw new IllegalStateException(text);
				}
			}
		} else {
			throw new Exception("Empty List");
		}
	}

	private static String encodeParam(String stringToEncode) {
		try {
			return URLEncoder.encode(stringToEncode, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return stringToEncode;
		}
		
	}
	private static String getASCIIContentFromEntity(HttpEntity entity)
			throws IllegalStateException, IOException {
		InputStream in = entity.getContent();
		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n > 0) {
			byte[] b = new byte[4096];
			n = in.read(b);
			if (n > 0)
				out.append(new String(b, 0, n));
		}
		return out.toString();
	}
}
