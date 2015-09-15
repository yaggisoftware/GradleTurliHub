package com.lalitote.turli;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class AmazonIdentityProvider extends
		AsyncTask<Void, Void, CognitoCachingCredentialsProvider> {

	MainActivity currentActivity;

	public AmazonIdentityProvider(MainActivity currentActivity) {
		this.currentActivity = currentActivity;
	}

	@Override
	protected CognitoCachingCredentialsProvider doInBackground(Void... params) {
		// Initialize the Cognito client:
		CognitoCachingCredentialsProvider cognitoProvider = new CognitoCachingCredentialsProvider(
				currentActivity.getApplicationContext(), // get the context for
															// the
															// current
				// activity
				"eu-west-1:ecd7dbb6-6627-405f-a165-3f5f13b64423", /*
																 * Identity Pool
																 * ID
																 */
				Regions.EU_WEST_1 /* Region */
		);

		// Store and synchronize data:

		CognitoSyncManager syncClient = new CognitoSyncManager(
				currentActivity.getApplicationContext(), Regions.EU_WEST_1, // Region
				cognitoProvider);

		Dataset dataset = syncClient.openOrCreateDataset("myDataset");
		dataset.put("myKey", "myValue");
		// dataset.synchronize(this, );

		// Once the Cognito credentials provider is initialized and refreshed,
		// you can pass it directly to the initializer for an AWS client. For
		// example, the following snippet initializes an Amazon DynamoDB client:

		Log.d("LogTag", "my ID is " + cognitoProvider.getIdentityId());

		// Create a service client with the provider
		AmazonDynamoDB client = new AmazonDynamoDBClient(cognitoProvider);

		DynamoDBMapper mapper = new DynamoDBMapper(client);

		return cognitoProvider;
	}

	@Override
	protected void onPostExecute(
			CognitoCachingCredentialsProvider cognitoProvider) {
		Log.d("LogTag", "my ID is " + cognitoProvider.getIdentityId());
	}

}
