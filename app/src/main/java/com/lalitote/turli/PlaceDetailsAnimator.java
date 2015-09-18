package com.lalitote.turli;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;

public class PlaceDetailsAnimator {

	private FragmentManager fragmentManager;
	private PlaceDetails fragment;
	private MainActivity currentActivity;

	public PlaceDetailsAnimator(MainActivity currentActivity) {
		this.currentActivity = currentActivity;
		fragmentManager = currentActivity.getFragmentManager();
	}

	public void showPlaceDetails() {
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragment = new PlaceDetails();
		fragmentTransaction.add(R.id.pdc, fragment);
		fragmentTransaction.addToBackStack("PDC");
		fragmentTransaction.commit();

		currentActivity.findViewById(R.id.pdc).setClickable(true);
	}

	public void hidePlaceDetails() {
		fragmentManager.beginTransaction()
				.remove(fragmentManager.findFragmentById(R.id.pdc)).commit();

		currentActivity.findViewById(R.id.pdc).setClickable(false);
	}

	public void moveButtonsLayoutUp() {
		RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
				currentActivity.findViewById(R.id.mapframe).getWidth(),
				currentActivity.findViewById(R.id.mapframe).getHeight()
						- currentActivity.findViewById(R.id.pdc).getHeight());
		RelativeLayout mapLayout = (RelativeLayout) currentActivity
				.findViewById(R.id.mapframe);
		mapLayout.setLayoutParams(parms);
	}

	public void moveButtonsLayoutDown() {
		RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
				currentActivity.findViewById(R.id.mapframe).getWidth(),
				currentActivity.findViewById(R.id.mapframe).getHeight()
						+ currentActivity.findViewById(R.id.pdc).getHeight());
		RelativeLayout mapLayout = (RelativeLayout) currentActivity
				.findViewById(R.id.mapframe);
		mapLayout.setLayoutParams(parms);
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
