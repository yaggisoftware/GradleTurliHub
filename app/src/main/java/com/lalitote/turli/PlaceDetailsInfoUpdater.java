package com.lalitote.turli;

import android.app.FragmentManager;
import android.location.Location;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

public class PlaceDetailsInfoUpdater extends AsyncTask<Marker, Void, Marker> {

	private FragmentManager fragmentManager;
	private Location currentLocation;
	private TextView pdcTitle;
	private TextView pdcAddress;
	private TextView pdcDistance;
	private Location distanceToLocation;
	private float distanceToSelectedMarker;

	public PlaceDetailsInfoUpdater(FragmentManager fragmentManager,
			Location currentLocation) {
		this.fragmentManager = fragmentManager;
		this.currentLocation = currentLocation;
		distanceToLocation = new Location("MarkerLocation");
	}

	/**
	 * Invoked by execute() method of this object. Check every 500ms if PDC was
	 * created. Exit when it was created.
	 */
	@Override
	protected Marker doInBackground(Marker... markerInfo) {
		while (!checkPdcCreated())
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		return markerInfo[0];
	}

	/**
	 * Executed after the complete execution of doInBackground() method. After
	 * PDC was successfully created, update it with marker info.
	 */
	@Override
	protected void onPostExecute(Marker markerInfo) {
		// Update Marker title on PDC
		pdcTitle = (TextView) fragmentManager.findFragmentById(R.id.pdc)
				.getView().findViewById(R.id.pdcText);
		pdcTitle.setText(markerInfo.getTitle());

		// Update Marker address on PDC
		pdcAddress = (TextView) fragmentManager.findFragmentById(R.id.pdc)
				.getView().findViewById(R.id.pdcAddress);
		pdcAddress.setText(markerInfo.getSnippet());

		// Calculate distance from current location to PDC
		distanceToLocation.setLatitude(markerInfo.getPosition().latitude);
		distanceToLocation.setLongitude(markerInfo.getPosition().longitude);

		// Update distance from current location to Marker on PDC
		pdcDistance = (TextView) fragmentManager.findFragmentById(R.id.pdc)
				.getView().findViewById(R.id.pdcDistance);
		distanceToSelectedMarker = ((float) Math.round(currentLocation
				.distanceTo(distanceToLocation) * 10) / 10);

		// If marker is more than 1km, show km instead of meters
		if (distanceToSelectedMarker > 1000) {
			distanceToSelectedMarker = distanceToSelectedMarker / 1000;
			distanceToSelectedMarker = ((float) Math
					.round(distanceToSelectedMarker * 10) / 10);
			pdcDistance.setText(Float.toString(distanceToSelectedMarker)
					+ " km.");
		} else {
			pdcDistance.setText(Float.toString(distanceToSelectedMarker)
					+ " m.");
		}
	}

	/**
	 * Check if PDC is already created.
	 *
	 * @return True if already created, false if not created.
	 */
	private Boolean checkPdcCreated() {
		View view = null;
		try {
			view = fragmentManager.findFragmentById(R.id.pdc).getView()
					.findViewById(R.id.pdcText);
		} catch (NullPointerException ex) {
			// Log.e("Null Pointer Exception", "PDC was not created");
		}
		return view != null;
	}

}
