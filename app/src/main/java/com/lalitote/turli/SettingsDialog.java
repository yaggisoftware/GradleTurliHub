package com.lalitote.turli;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.lalitote.turli.Settings.placeType;

public class SettingsDialog extends DialogFragment {

	private SeekBar seekBar;
	private TextView textView;
	private RadioGroup radioButton;
	private CheckBox checkBox;
	private Settings currentSettings;

	private placeType selectedCategory;

	public SettingsDialog(Settings currentSettings) {
		this.currentSettings = currentSettings;
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View menuView = inflater.inflate(R.layout.dialogoptions, null);

		textView = (TextView) menuView.findViewById(R.id.seekBarText);

		radioButton = (RadioGroup) menuView.findViewById(R.id.radio_group);
		switch (currentSettings.getSelectedPlaceType()) {
		case food:
			radioButton.check(R.id.radio_food);
			selectedCategory = placeType.food;
			break;
		case shopping:
			radioButton.check(R.id.radio_shopping);
			selectedCategory = placeType.shopping;
			break;
		case entertainment:
			radioButton.check(R.id.radio_entertainment);
			selectedCategory = placeType.entertainment;
			break;
		default:
			radioButton.check(R.id.radio_all);
			selectedCategory = placeType.all;
			break;
		}
		radioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_entertainment:
					selectedCategory = placeType.entertainment;
					break;
				case R.id.radio_food:
					selectedCategory = placeType.food;
					break;
				case R.id.radio_shopping:
					selectedCategory = placeType.shopping;
					break;
				default:
					selectedCategory = placeType.all;
					break;
				}
			}
		});

		checkBox = (CheckBox) menuView.findViewById(R.id.checkboxAutoSearch);
		checkBox.setChecked(currentSettings.getAutomaticRadius());
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				seekBar.setEnabled(!isChecked);

			}
		});

		seekBar = (SeekBar) menuView.findViewById(R.id.rangeBar);
		seekBar.setEnabled(!checkBox.isChecked());
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				textView.setText("Search range: " + Integer.toString(progress)
						+ "m.");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

		});

		seekBar.setProgress(currentSettings.getSearchRadius());
		textView.setText("Search range: "
				+ Integer.toString(seekBar.getProgress()) + "m.");

		builder.setView(menuView)
				.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								currentSettings.setSearchRadius(seekBar
										.getProgress());
								currentSettings
										.setSelectedPlaceType(selectedCategory);
								currentSettings.setAutomaticRadius(checkBox
										.isChecked());
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog, do nothing
							}
						});

		// Create the AlertDialog object and return it
		return builder.create();
	}
}
