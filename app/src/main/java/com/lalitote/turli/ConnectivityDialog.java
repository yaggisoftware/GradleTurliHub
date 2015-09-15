package com.lalitote.turli;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ConnectivityDialog extends DialogFragment {

	private connectivityIssue issueType;
	private TextView dialogText;
	private Dialog connectivityDialog;

	public enum connectivityIssue {
		POSITION, CONNECTION
	};

	/**
	 * Create dialog with info about current connectivity issues
	 *
	 * @param issueType
	 *            - Position issue or Internet connection issue
	 */
	public ConnectivityDialog(connectivityIssue issueType) {
		this.issueType = issueType;
	}

	/**
	 * Here connectivity issues dialog is created
	 */
	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Inflate the view
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View menuView = inflater.inflate(R.layout.dialog_connectivity, null);

		// Show text depending on issue type
		dialogText = (TextView) menuView
				.findViewById(R.id.connectivityDialogText);

		if (issueType == connectivityIssue.CONNECTION) {
			dialogText.setText(getString(R.string.connectivity_connection));
		} else {
			dialogText.setText(getString(R.string.connectivity_position));
		}

		// Set custom OK button
		Button okButton = (Button) menuView
				.findViewById(R.id.connectivityDialogButton);
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				connectivityDialog.dismiss();
			}
		});

		// Set view
		builder.setView(menuView);

		// Create the AlertDialog object and return it
		connectivityDialog = builder.create();

		return connectivityDialog;
	}
}
