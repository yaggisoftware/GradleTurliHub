package com.lalitote.turli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A class to parse the Google Places in JSON format
 */
public class MarkerParseAndPlaceOnMap extends
		AsyncTask<String, Integer, List<HashMap<String, String>>> {

	JSONObject jObject;
	private GoogleMap googleMap;
	private MainActivity currentActivity;
	private ArrayList<Marker> markersFullList;

	/**
	 * Constructor method
	 *
	 * @param googleMap
	 * @param currentActivity
	 */
	public MarkerParseAndPlaceOnMap(GoogleMap googleMap,
			MainActivity currentActivity) {
		this.googleMap = googleMap;
		this.currentActivity = currentActivity;

	}

	/**
	 * Invoked by execute() method of this object. Try to parse data in
	 * background
	 */
	@Override
	protected List<HashMap<String, String>> doInBackground(String... jsonData) {

		List<HashMap<String, String>> places = null;
		PlaceJSONParser placeJsonParser = new PlaceJSONParser();

		try {
			jObject = new JSONObject(jsonData[0]);

			/** Getting the parsed data as a List construct */
			places = placeJsonParser.parse(jObject);

		} catch (Exception e) {
			Log.d("Exception", e.toString());
		}
		return places;
	}

	/**
	 * Executed after the complete execution of doInBackground() method. After
	 * successfully parsed data, add markers to google map
	 */
	@Override
	protected void onPostExecute(List<HashMap<String, String>> list) {

		// Clears all the existing markers
		googleMap.clear();

		// Initialize markers array list
		markersFullList = new ArrayList<Marker>();

		for (int i = 0; i < list.size(); i++) {

			// Creating a marker
			MarkerOptions markerOptions = new MarkerOptions();

			// Getting a place from the places list
			HashMap<String, String> hmPlace = list.get(i);

			// Getting latitude of the place
			double lat = Double.parseDouble(hmPlace.get("lat"));

			// Getting longitude of the place
			double lng = Double.parseDouble(hmPlace.get("lng"));

			// Getting name
			String name = hmPlace.get("place_name");

			// Getting vicinity
			String vicinity = hmPlace.get("vicinity");

			// Calculating coordinates
			LatLng latLng = new LatLng(lat, lng);

			// Setting the position for the marker
			markerOptions.position(latLng);

			// Setting the title for the marker.
			// This will be displayed on taping the marker
			markerOptions.title(name);
			markerOptions.snippet(vicinity);

			// Set custom icon
			markerOptions.icon(setProperIconForCategory(hmPlace));

			// Register to on marker click listener, so PDC is shown on click.
			googleMap.setOnMarkerClickListener(new ListenerOnMarkerClick(
					currentActivity));

			// Placing a marker on the touched position
			Marker marker;
			markersFullList.add(marker = googleMap.addMarker(markerOptions));

			// Save whole hash map marker
			currentActivity.getClickedMarkerIconResource().put(marker.getId(),
					getIconResourceForCategory(hmPlace));
		}

		// Copy markers list to main activity for easier access
		currentActivity.setMarkersArray(markersFullList);
	}

	private BitmapDescriptor setProperIconForCategory(
			HashMap<String, String> hmPlace) {
		// Parse type string from hash map
		String type = hmPlace.get("types");

		// Trim beginning and end of the string
		type = type.substring(2, type.length() - 2);

		// Parse string to list of places
		List<String> placeTypeList = Arrays.asList(type.split("\\s*\",\"\\s*"));

		return BitmapDescriptorFactory
				.fromResource(markerIconPicker(placeTypeList.get(0)));
	}

	private int getIconResourceForCategory(HashMap<String, String> hmPlace) {
		// Parse type string from hash map
		String type = hmPlace.get("types");

		// Trim beginning and end of the string
		type = type.substring(2, type.length() - 2);

		// Parse string to list of places
		List<String> placeTypeList = Arrays.asList(type.split("\\s*\",\"\\s*"));

		return markerIconPicker(placeTypeList.get(0));
	}

	private int markerIconPicker(String category) {
		for (String str : MarkerCategories.food) {
			if (str.trim().contains(category))
				return R.drawable.ic_map_marker_blue_eat;
		}
		for (String str : MarkerCategories.shopping) {
			if (str.trim().contains(category))
				return R.drawable.ic_map_marker_blue_shop;
		}
		for (String str : MarkerCategories.entertainment) {
			if (str.trim().contains(category))
				return R.drawable.ic_map_marker_blue_fun;
		}
		return R.drawable.ic_map_marker_blue;
	}
}
