package com.lalitote.turli;

import android.app.FragmentManager;
import android.view.View;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

public class ListenerOnMapClick implements OnMapClickListener {

	private MainActivity currentActivity;
	private FragmentManager fragmentManager;

	public ListenerOnMapClick(MainActivity currentActivity) {
		this.currentActivity = currentActivity;
		fragmentManager = currentActivity.getFragmentManager();
	}

	@Override
	public void onMapClick(LatLng clickedCoordinate) {
		// Move map down
		currentActivity.getPlaceDetailsAnimator().moveButtonsLayoutDown();

		// Remove PDC
		if (checkPdcCreated() == true) {
			currentActivity.getPlaceDetailsAnimator().hidePlaceDetails();
		}

		if (currentActivity.getClickedMarker() != null) {
			// Set all markers alpha to 1
			for (int i = 0; i < currentActivity.getMarkersArray().size(); i++) {
				currentActivity.getMarkersArray().get(i).setAlpha(1f);
			}

			// Set proper clicked marker size
			currentActivity.getClickedMarker().setIcon(
					BitmapDescriptorFactory
							.fromResource(currentActivity.getClickedMarkerIconResource()
									.get(currentActivity.getClickedMarker()
											.getId())));

			// Set marker as not clicked
			currentActivity.setClickedMarker(null);
		}

	}

	/**
	 * Check if PDC is already created.
	 *
	 * @return True if already created, false if not created.
	 */
	public Boolean checkPdcCreated() {
		View view = null;
		try {
			view = fragmentManager.findFragmentById(R.id.pdc).getView()
					.findViewById(R.id.pdcText);
		} catch (NullPointerException ex) {

		}
		return view != null;
	}

}
