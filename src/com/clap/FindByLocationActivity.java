package com.clap;

import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class FindByLocationActivity extends ClapActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		title = "Find By Location";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		new GetAddressTask(this).execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkLocationEnabled();
	}
	
	private class GetAddressTask extends AsyncTask<Void,Void,String> {
		private Context context;
		private ProgressDialog progressDialog;
		private Exception exception = null;
		
		public GetAddressTask(Context c) {
			context = c;
			progressDialog = new ProgressDialog(context);
		}
		
		@Override
		protected void onPreExecute() {
			progressDialog.setMessage("Getting Location");
			progressDialog.show();
		}
		
		@Override
		protected String doInBackground(Void... arg0) {
			LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			String provider = locationManager.getBestProvider(criteria, false);
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				Geocoder geocoder = new Geocoder(context, Locale.getDefault());
				List<Address> addresses = null;
				try {
					addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				} catch (Exception e) {
					exception = e;
					return null;
				}
				if (addresses != null && addresses.size() > 0) {
					Address address = addresses.get(0);
					return address.getCountryName();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(String countryName) {
			progressDialog.dismiss();
			
			if (exception != null) {
				showErrorMessage(exception);
			} else {
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
				dialogBuilder.setTitle("Country");
				dialogBuilder.setMessage(countryName);
				dialogBuilder.setNeutralButton(getString(R.string.ok), null);
				dialogBuilder.create().show();
			}
		}
	}

	private void checkLocationEnabled() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean useLocation = sharedPreferences.getBoolean(ClapPreferenceActivity.KEY_LOCATION, false);
		if (!useLocation) {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle("Applause Location Access Disabled");
			dialogBuilder.setMessage("Do You Want To Allow Applause To Use Your Location?");
			dialogBuilder.setPositiveButton(getString(R.string.btnYes), new EnableApplauseLocationClick(this));
			dialogBuilder.setNeutralButton(getString(R.string.btnNo), new NoClick());

			AlertDialog locationDialog = dialogBuilder.create();
			locationDialog.show();
		}
	}

	private class EnableApplauseLocationClick implements OnClickListener {
		private Context context;
		
		public EnableApplauseLocationClick(Context c) {
			context = c;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Intent intent = new Intent(context, ClapPreferenceActivity.class);
			startActivity(intent);
		}
	}

	private class NoClick implements OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			finish();
		}
	}
}
