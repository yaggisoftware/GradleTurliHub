package com.lalitote.turli;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.lalitote.turli.ConnectivityDialog.connectivityIssue;

public class MainActivity extends Activity {

	private GoogleMap googleMap;
	private SearchPlaces searchPlaces;
	private Settings currentSettings;
	private Location currentLocation = null;
	private ListenerOnLocationChange currentLocationListener;
	private ListenerOnMapClick onMapClickListener;
	private SearchView searchView;
	private SearchManager searchViewManager;
	private PlaceDetailsAnimator placeDetailsAnimator;

	private ArrayList<Marker> markersFullList;
	private Marker clickedMarker;
	private HashMap<String, Integer> clickedMarkerIconResource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			// Initialize settings
			if (currentSettings == null) {
				currentSettings = new Settings();
			}

			// Initialize map object
			initilizeMap();

			// Initialize place details animator
			placeDetailsAnimator = new PlaceDetailsAnimator(this);

			// Initialize class to search for places and put them on map
			searchPlaces = new SearchPlaces(googleMap, currentLocation,
					currentSettings, getCurrentActivity());

			// Initialize HashMap of Markers icon resources
			clickedMarkerIconResource = new HashMap<String, Integer>();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// Disabled normal menu, only action bar is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		setSearchActionView(menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_options:
			// Show options on dialog
			DialogFragment dialog = new SettingsDialog(currentSettings);
			dialog.show(getFragmentManager(), "MapOptions");
			return true;
		case R.id.action_refresh:
			if (checkConnectivity()) {
				// Move map down
				placeDetailsAnimator.moveButtonsLayoutDown();

				// Remove PDC
				if (onMapClickListener.checkPdcCreated() == true) {
					placeDetailsAnimator.hidePlaceDetails();
				}

				// Set marker as not clicked
				setClickedMarker(null);

				// If auto search is selected, results are within viewport
				// Else search radius is as specified
				if (currentSettings.getAutomaticRadius()) {
					searchPlaces.searchPlacesNearby(
							currentViewportToLocation(googleMap),
							(int) currentViewportToRadius(googleMap));

				} else {
					searchPlaces.searchPlacesNearby(
							currentViewportToLocation(googleMap),
							currentSettings.getSearchRadius());
				}
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Handling zoom in button click
	 *
	 * @param view
	 */
	public void onZoomInClicked(View view) {
		googleMap.animateCamera(CameraUpdateFactory.zoomIn());
	}

	/**
	 * Handling zoom out button click
	 *
	 * @param view
	 */
	public void onZoomOutClicked(View view) {
		googleMap.animateCamera(CameraUpdateFactory.zoomOut());
	}

	/**
	 * Camera is moved to current position and search is performed within
	 * 200meters around
	 *
	 * @param view
	 */
	public void onPositionButtonClicked(View view) {
		if (checkConnectivity()) {
			moveCameraToDefault(currentLocationListener.getCurrentLocation());

			// Move map down
			placeDetailsAnimator.moveButtonsLayoutDown();

			// Remove PDC
			if (onMapClickListener.checkPdcCreated() == true) {
				placeDetailsAnimator.hidePlaceDetails();
			}

			// Set marker as not clicked
			setClickedMarker(null);

			// Perform search
			searchPlaces.searchPlacesNearby(
					currentLocationListener.getCurrentLocation(),
					Settings.DEFAULT_SEARCH_RADIUS);
		}
	}

	/**
	 * Overrided on back press. When PDC is opened close PDC, else ask if really
	 * want to exit app.
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (!onMapClickListener.checkPdcCreated()) {
			new AlertDialog.Builder(this)
					.setMessage("Are you sure you want to exit?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									getCurrentActivity().finish();
								}
							}).setNegativeButton("No", null).show();
		} else {
			// Move map down
			placeDetailsAnimator.moveButtonsLayoutDown();

			super.onBackPressed();

			// Set PDC to not clickable
			findViewById(R.id.pdc).setClickable(false);

			// Set all markers alpha to 1
			for (int i = 0; i < getMarkersArray().size(); i++) {
				getMarkersArray().get(i).setAlpha(1f);
			}

			// Set proper clicked marker size
			getClickedMarker().setIcon(
					BitmapDescriptorFactory
							.fromResource(clickedMarkerIconResource
									.get(clickedMarker.getId())));

			// Set marker as not clicked
			setClickedMarker(null);
		}
	}

	/**
	 * Method to load map. If map is not created it will create it for you.
	 * Setup all google map UI elements like: zoom buttons, position button,
	 * position indicator etc.
	 * */
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			googleMap.setMapType(Settings.GOOGLE_MAP_TYPE);
			googleMap.setBuildingsEnabled(Settings.SET_BUILDINGS_ENABLED);
			googleMap.getUiSettings().setZoomControlsEnabled(
					Settings.SHOW_ZOOM_BUTTONS);
			googleMap.getUiSettings().setMyLocationButtonEnabled(
					Settings.SET_MY_LOCATION_BUTTON);
			googleMap.setMyLocationEnabled(Settings.SHOW_CURRENT_LOCATION);
			googleMap
					.setOnMyLocationChangeListener(currentLocationListener = new ListenerOnLocationChange(
							getCurrentActivity(), googleMap, currentSettings,
							searchPlaces));
			googleMap
					.setOnMapClickListener(onMapClickListener = new ListenerOnMapClick(
							getCurrentActivity()));

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
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
	 * Method to setup search view in action bar and to set up listener to
	 * search for place on submit.
	 *
	 * @param menu
	 */
	private void setSearchActionView(Menu menu) {
		// Set up search view.
		searchViewManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setSearchableInfo(searchViewManager
				.getSearchableInfo(getComponentName()));
		searchView.setOnQueryTextListener(new ListenerOnSearchViewQuerySubmit(
				searchPlaces, searchView, this));
	}

	/**
	 * Method to generate location based on current viewport center. Used to
	 * search around viewport center instead of current location.
	 *
	 * @param map
	 * @return return viewport location
	 */
	public Location currentViewportToLocation(GoogleMap map) {
		Location viewportLocation = new Location("dummyprovider");
		viewportLocation
				.setLatitude((map.getProjection().getVisibleRegion().latLngBounds.northeast.latitude + map
						.getProjection().getVisibleRegion().latLngBounds.southwest.latitude) / 2);
		viewportLocation
				.setLongitude((map.getProjection().getVisibleRegion().latLngBounds.northeast.longitude + map
						.getProjection().getVisibleRegion().latLngBounds.southwest.longitude) / 2);
		return viewportLocation;
	}

	/**
	 * Method to generate radius visible on current viewport. Used to search
	 * with radius visible on viewport, so all searched POIs fit on screen.
	 *
	 * @param map
	 * @return
	 */
	public float currentViewportToRadius(GoogleMap map) {
		float[] results = new float[1];

		Location.distanceBetween(
				map.getProjection().getVisibleRegion().latLngBounds.southwest.latitude,
				map.getProjection().getVisibleRegion().latLngBounds.southwest.longitude,
				map.getProjection().getVisibleRegion().latLngBounds.southwest.latitude,
				map.getProjection().getVisibleRegion().latLngBounds.northeast.longitude,
				results);
		// Divide by 2 was still not enough to fit POIs on screen, so div by 3
		// should be tested.
		return results[0] / 3;
	}

	/**
	 * Check if both Internet connection and current location are available
	 *
	 * @return True if both Internet and location are available
	 */
	public boolean checkConnectivity() {
		if (currentLocationListener.getCurrentLocation() == null) {
			new ConnectivityDialog(connectivityIssue.POSITION).show(
					getFragmentManager(), "PositionIssue");
			return false;
		} else if (!isNetworkConnected()) {
			new ConnectivityDialog(connectivityIssue.CONNECTION).show(
					getFragmentManager(), "ConnectivityIssue");
			return false;
		}
		return true;
	}

	/**
	 * Check if Internet connection is available.
	 *
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null;
	}

	/**
	 * Get current map instance.
	 *
	 * @return
	 */
	public GoogleMap getCurrentMap() {
		return googleMap;
	}

	/**
	 * Get current location value.
	 *
	 * @return
	 */
	public Location getCurrentLocation() {
		return currentLocationListener.getCurrentLocation();
	}

	/**
	 * Set current location value.
	 *
	 * @return
	 */
	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	/**
	 * Get current activity.
	 *
	 * @return
	 */
	public MainActivity getCurrentActivity() {
		return this;
	}

	/**
	 * Set visible markers added to map as array in main activity for easier
	 * access
	 *
	 * @param markersFullList
	 */
	public void setMarkersArray(ArrayList<Marker> markersFullList) {
		this.markersFullList = markersFullList;
	}

	/**
	 * Get list of visible markers added to map
	 *
	 * @return
	 */
	public ArrayList<Marker> getMarkersArray() {
		return this.markersFullList;
	}

	/**
	 * Set reference to clicked marker
	 *
	 * @param clickedMarker
	 */
	public void setClickedMarker(Marker clickedMarker) {
		this.clickedMarker = clickedMarker;
	}

	/**
	 * Get reference to marker that is currently clicked
	 *
	 * @return
	 */
	public Marker getClickedMarker() {
		return this.clickedMarker;
	}

	/**
	 * Get reference to place details animator, to be able to show and hide PDC
	 *
	 * @return
	 */
	public PlaceDetailsAnimator getPlaceDetailsAnimator() {
		return this.placeDetailsAnimator;
	}

	/**
	 * Get reference to clicked marker icon resources to be able to grow, shrink
	 * icons on click and deselect
	 *
	 * @return
	 */
	public HashMap<String, Integer> getClickedMarkerIconResource() {
		return this.clickedMarkerIconResource;
	}

}
