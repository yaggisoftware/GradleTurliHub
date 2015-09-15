package com.lalitote.turli;

import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class ListenerOnSearchViewQuerySubmit implements OnQueryTextListener {

	private MainActivity currentActivity;
	private SearchPlaces searchPlaces;
	private SearchView searchView;
	private PlaceDetailsAnimator placeDetailsAnimator;

	public ListenerOnSearchViewQuerySubmit(SearchPlaces searchPlaces,
			SearchView searchView, MainActivity currentActivity) {
		this.currentActivity = currentActivity;
		this.searchPlaces = searchPlaces;
		this.searchView = searchView;
		placeDetailsAnimator = currentActivity.getPlaceDetailsAnimator();
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// Move map down
		placeDetailsAnimator.moveButtonsLayoutDown();

		// Remove PDC
		if (placeDetailsAnimator.checkPdcCreated() == true) {
			placeDetailsAnimator.hidePlaceDetails();
		}

		// Set marker as not clicked
		currentActivity.setClickedMarker(null);

		// On submit, search for queried place around current viewport
		// center and in viewport radius
		searchPlaces.searchForPlace(query, currentActivity
				.currentViewportToLocation(currentActivity.getCurrentMap()),
				(int) currentActivity.currentViewportToRadius(currentActivity
						.getCurrentMap()));
		searchView.clearFocus();
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

}
