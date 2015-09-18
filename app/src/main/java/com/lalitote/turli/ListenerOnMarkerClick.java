package com.lalitote.turli;

import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

/**
 * Listener to show PDC and marker details on it.
 */
public class ListenerOnMarkerClick implements OnMarkerClickListener {

	private MainActivity currentActivity;
	private FragmentManager fragmentManager;
	private PlaceDetails fragment;
	private Bitmap selectedMarkerBitmap;

	final static float MARKER_GROW_PERCENTAGE = 120.0f;

	public ListenerOnMarkerClick(MainActivity currentActivity) {
		this.currentActivity = currentActivity;
		fragmentManager = currentActivity.getFragmentManager();
	}

	/**
	 * On marker click check if PDC is already created and create new one if
	 * not. Run async task to update info on PDC
	 */
	@Override
	public boolean onMarkerClick(Marker clickedMarker) {
		if (checkPdcCreated() == false) {
			currentActivity.getPlaceDetailsAnimator().showPlaceDetails();

			// Move whole map up
			currentActivity.getPlaceDetailsAnimator().moveButtonsLayoutUp();
		}

		// Do not show info window when marker clicked.
		clickedMarker.hideInfoWindow();

		// Start async task to update info on PDC after it is created.
		new PlaceDetailsInfoUpdater(fragmentManager,
				currentActivity.getCurrentLocation()).execute(clickedMarker);

		// Get previously clicked marker smaller
		if (currentActivity.getClickedMarker() != null) {
			currentActivity.getClickedMarker().setIcon(
					(BitmapDescriptorFactory
							.fromResource(currentActivity.getClickedMarkerIconResource()
									.get(currentActivity.getClickedMarker()
											.getId()))));
		}

		// Get marker icon to be grown
		selectedMarkerBitmap = BitmapFactory
				.decodeResource(currentActivity.getResources(), currentActivity
						.getClickedMarkerIconResource().get(clickedMarker.getId()));

		// Grow marker icon and add it to map
		clickedMarker
				.setIcon(BitmapDescriptorFactory.fromBitmap(getResizedBitmap(
						selectedMarkerBitmap,
						((int) (selectedMarkerBitmap.getHeight() * (MARKER_GROW_PERCENTAGE / 100.0f))),
						((int) (selectedMarkerBitmap.getWidth() * (MARKER_GROW_PERCENTAGE / 100.0f))))));

		// Set alpha on other markers
		for (int i = 0; i < currentActivity.getMarkersArray().size(); i++) {
			currentActivity.getMarkersArray().get(i).setAlpha(0.4f);
		}

		// Set alpha 1 on clicked marker
		clickedMarker.setAlpha(1f);

		// Center camera on clicked marker
		currentActivity.getCurrentMap().animateCamera(
				CameraUpdateFactory.newLatLng(clickedMarker.getPosition()),
				Settings.DEFAULT_MARKER_CLICK_ANIMATION_DURATION,
				new CancelableCallback() {

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onCancel() {
						// TODO Auto-generated method stub

					}
				});

		// Pass clicked marker to main activity
		currentActivity.setClickedMarker(clickedMarker);

		return true;
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

		}
		return view != null;
	}

	public PlaceDetails getCreatedFragment() {
		return fragment;
	}

	public Bitmap getResizedBitmap(Bitmap markerBitmap, int newHeight,
			int newWidth) {
		int width = markerBitmap.getWidth();
		int height = markerBitmap.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();

		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(markerBitmap, 0, 0, width,
				height, matrix, false);

		return resizedBitmap;
	}
}
