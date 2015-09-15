package com.lalitote.turli;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class ListenerOnLocationChange implements OnMyLocationChangeListener {

	private boolean firstCameraAnimation = true;
	private Location currentLocation = null;
	private MainActivity currentActivity;
	private GoogleMap googleMap;
	private SearchPlaces searchPlaces;
	private Settings currentSettings;

	public ListenerOnLocationChange(MainActivity currentActivity,
			GoogleMap googleMap, Settings currentSettings,
			SearchPlaces searchPlaces) {
		this.currentActivity = currentActivity;
		this.currentSettings = currentSettings;
		this.googleMap = googleMap;
		this.searchPlaces = searchPlaces;
	}

	@Override
	public void onMyLocationChange(Location newLocation) {
		currentLocation = newLocation;
		// currentActivity.setCurrentLocation(newLocation);
		animateCameraOnStart();
	}

	/**
	 * Move camera to current location on application start. Trigger search
	 * after camera is moved.
	 */
	private void animateCameraOnStart() {
		if (firstCameraAnimation && currentLocation != null
				&& currentActivity.isNetworkConnected()) {
			moveCameraToDefault(currentLocation);
			Log.d("location", currentLocation.toString());
			searchPlaces = new SearchPlaces(googleMap, currentLocation,
					currentSettings, currentActivity);
			searchPlaces.searchPlacesNearby();
			firstCameraAnimation = false;
		}
	}

	/**
	 * Move camera to current location.
	 *
	 * @param location
	 */
	private void moveCameraToDefault(Location location) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(location.getLatitude(), location
						.getLongitude())).zoom(Settings.DEFAULT_CAMERA_ZOOM)
				.build();
		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
	}

	/**
	 * Method to get current location
	 *
	 * @return Location current location
	 */
	public Location getCurrentLocation() {
		return currentLocation;
	}

}
