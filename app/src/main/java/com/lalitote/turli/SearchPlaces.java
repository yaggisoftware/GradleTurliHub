package com.lalitote.turli;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.lalitote.turli.Settings.placeType;

public class SearchPlaces extends MainActivity {

	private GoogleMap googleMap;
	private Location currentLocation;
	private Settings currentSettings;
	private MainActivity currentActivity;

	public SearchPlaces(GoogleMap googleMap, Location currentLocation,
			Settings currentSettings, MainActivity currentActivity) {
		this.googleMap = googleMap;
		this.currentLocation = currentLocation;
		this.currentSettings = currentSettings;
		this.currentActivity = currentActivity;
	}

	/**
	 * Method to build string query passed to google maps API. Start location is
	 * current location, radius is taken from settings and POI categories from
	 * settings as well
	 */
	public void searchPlacesNearby() {
		// Build query string
		StringBuilder queryString = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
		queryString.append("location="
				+ Double.toString(currentLocation.getLatitude()) + ","
				+ Double.toString(currentLocation.getLongitude()));
		queryString.append("&radius=" + currentSettings.getSearchRadius());
		if (currentSettings.getSelectedPlaceType() != placeType.all) {
			queryString.append("&types="
					+ currentSettings.getSelectedPlaceTypeFullString());
		}
		queryString.append("&key=" + Settings.PLACES_API_KEY);

		// Creating a new non-ui thread task to download json data
		DownloadPlacesList placesTask = new DownloadPlacesList(googleMap,
				currentActivity);

		// Invokes the "doInBackground()" method of the class PlaceTask
		placesTask.execute(queryString.toString());
	}

	/**
	 * Method to build string query passed to google maps API. Start location
	 * passed as parameter, radius is taken from settings and POI categories
	 * from settings as well
	 *
	 * @param locationToSearchAround
	 *            Location where to start searching around
	 */
	public void searchPlacesNearby(Location locationToSearchAround) {
		// Build query string
		StringBuilder queryString = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
		queryString.append("location="
				+ Double.toString(locationToSearchAround.getLatitude()) + ","
				+ Double.toString(locationToSearchAround.getLongitude()));
		queryString.append("&radius=" + currentSettings.getSearchRadius());
		if (currentSettings.getSelectedPlaceType() != placeType.all) {
			queryString.append("&types="
					+ currentSettings.getSelectedPlaceTypeFullString());
		}
		queryString.append("&key=" + Settings.PLACES_API_KEY);

		// Creating a new non-ui thread task to download json data
		DownloadPlacesList placesTask = new DownloadPlacesList(googleMap,
				currentActivity);

		// Invokes the "doInBackground()" method of the class PlaceTask
		placesTask.execute(queryString.toString());
	}

	/**
	 * Method to build string query passed to google maps API. Start location
	 * and radius passed as parameter, POI categories are taken from settings
	 *
	 * @param locationToSearchAround
	 *            Location where to start searching around
	 * @param radius
	 *            How far from start location to search
	 */
	public void searchPlacesNearby(Location locationToSearchAround, int radius) {
		// Build query string
		StringBuilder queryString = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
		queryString.append("location="
				+ Double.toString(locationToSearchAround.getLatitude()) + ","
				+ Double.toString(locationToSearchAround.getLongitude()));
		queryString.append("&radius=" + radius);
		if (currentSettings.getSelectedPlaceType() != placeType.all) {
			queryString.append("&types="
					+ currentSettings.getSelectedPlaceTypeFullString());
		}
		queryString.append("&key=" + Settings.PLACES_API_KEY);

		// Creating a new non-ui thread task to download json data
		DownloadPlacesList placesTask = new DownloadPlacesList(googleMap,
				currentActivity);

		// Invokes the "doInBackground()" method of the class PlaceTask
		placesTask.execute(queryString.toString());
	}

	/**
	 * Method to build string query passed to google maps API. Search for place
	 * from action bar.
	 */
	public void searchForPlace(String searchString,
			Location locationToSearchAround, int radius) {
		// Trim query
		searchString = searchString.replaceAll("[^a-zA-Z]", "+");

		// Build query string
		StringBuilder queryString = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/textsearch/json?");
		queryString.append("query=" + searchString);
		queryString.append("&location="
				+ Double.toString(locationToSearchAround.getLatitude()) + ","
				+ Double.toString(locationToSearchAround.getLongitude()));
		queryString.append("&radius=" + radius);
		queryString.append("&key=" + Settings.PLACES_API_KEY);

		// Creating a new non-ui thread task to download json data
		DownloadPlacesList placesTask = new DownloadPlacesList(googleMap,
				currentActivity);

		// Invokes the "doInBackground()" method of the class PlaceTask
		placesTask.execute(queryString.toString());
	}

}
