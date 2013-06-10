package com.example.languageproject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
	public static void DownloadAndSaveAudio(String urlString, String audioPath) {
		URL url;
		try {
			url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.connect();
			
			FileOutputStream fileOutput = new FileOutputStream(audioPath);
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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String getJSONArray(String httpGetString) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(httpGetString);
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
				return "Error: empty list";
			} else {
				Pattern p = Pattern.compile("^\\[(.*)\\]$");
				Matcher matcher = p.matcher(text);
				if (matcher.find()) {
					return text;
				} else {
					return "Error: results don't match expected format:\n" + text;
				}
			}
		} else {
			return "Error: results were null";
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
