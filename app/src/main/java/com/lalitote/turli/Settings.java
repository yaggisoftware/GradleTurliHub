package com.lalitote.turli;

import com.google.android.gms.maps.GoogleMap;

public class Settings {

	public static boolean SHOW_CURRENT_LOCATION = true;
	public static boolean SHOW_ZOOM_BUTTONS = false;
	public static boolean SET_BUILDINGS_ENABLED = true;
	public static boolean SET_MY_LOCATION_BUTTON = false;
	public static int DEFAULT_CAMERA_ZOOM = 16;
	public static int DEFAULT_SEARCH_RADIUS = 200;
	public static int GOOGLE_MAP_TYPE = GoogleMap.MAP_TYPE_NORMAL;
	public static final String PLACES_API_KEY = "AIzaSyDq4a9vpdo-3Z3g46ChyOzoYf8yBCUwkHU";
	public static final int DEFAULT_MARKER_CLICK_ANIMATION_DURATION = 500;

	private static final String PLACE_TYPE_SHOPPING = "bicycle_store|book_store|clothing_store|convenience_store|"
			+ "department_store|electronics_store|furniture_store|grocery_or_supermarket|"
			+ "hardware_store|home_goods_store|jewelry_store|liquor_store|pet_store|pharmacy|"
			+ "post_office|shoe_store|shopping_mail|store";
	private static final String PLACE_TYPE_FOOD = "food|cafe|restaurant|bar|bakery|meal_delivery|meal_takeaway";
	private static final String PLACE_TYPE_ENTERTAINMENT = "amusement_park|art_gallery|museum|aquarium|"
			+ "bowling_alley|movie_rental|movie_theater|park|stadium|zoo";

	private int searchRadius = 200;
	private boolean automaticRadius = true;
	private placeType selectedPlaceType = placeType.all;

	public enum placeType {
		all, food, shopping, entertainment;
	}

	/**
	 * Return current search radius.
	 *
	 * @return
	 */
	public int getSearchRadius() {
		return searchRadius;
	}

	/**
	 * Set new search radius.
	 *
	 * @param newSearchRadius
	 */
	public void setSearchRadius(int newSearchRadius) {
		this.searchRadius = newSearchRadius;
	}

	/**
	 * Return current automatic search radius state.
	 *
	 * @return
	 */
	public boolean getAutomaticRadius() {
		return automaticRadius;
	}

	/**
	 * Set automatic search radius state.
	 *
	 * @param newAutomaticRadius
	 */
	public void setAutomaticRadius(boolean newAutomaticRadius) {
		this.automaticRadius = newAutomaticRadius;
	}

	/**
	 * Get current selected place type.
	 *
	 * @return
	 */
	public placeType getSelectedPlaceType() {
		return selectedPlaceType;
	}

	/**
	 * Get current selected place type and return full search string.
	 *
	 * @return
	 */
	public String getSelectedPlaceTypeFullString() {
		switch (selectedPlaceType) {
		case food:
			return PLACE_TYPE_FOOD;
		case shopping:
			return PLACE_TYPE_SHOPPING;
		case entertainment:
			return PLACE_TYPE_ENTERTAINMENT;
		default:
			return "all";
		}
	}

	/**
	 * Set new selected place type.
	 *
	 * @param newSelectedPlaceType
	 */
	public void setSelectedPlaceType(placeType newSelectedPlaceType) {
		this.selectedPlaceType = newSelectedPlaceType;
	}

}
