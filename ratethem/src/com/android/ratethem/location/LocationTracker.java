//singleton class for doing location tasks

package com.android.ratethem.location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.ratethem.InsertData;
import com.android.ratethem.R;
import com.android.ratethem.SearchList;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationTracker implements 
					LocationListener,
					GooglePlayServicesClient.OnConnectionFailedListener {
	   
		
		
	   private Context context;
	   private ConnectionCallbacks connectionCallbacks;
	   private OnConnectionFailedListener connectionFailedListener;
	   private Activity activity;
	   private String LOG_TAG = "ratethem";
	   private Location location;
	   private String currentAddress = null;
	    
	    // Global constants
	    /*
	     * Define a request code to send to Google Play services
	     * This code is returned in Activity.onActivityResult
	     */
	    private final static int	CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	    
	    // A request to connect to Location Services
	    private LocationRequest mLocationRequest;

	    // Stores the current instantiation of the location client in this object
	    private LocationClient mLocationClient;
		

	    /*
	     * Note if updates have been turned on. Starts out as "false"; is set to "true" in the
	     * method handleRequestSuccess of LocationUpdateReceiver.
	     *
	     */
	    boolean mUpdatesRequested = false;
	   
	    
	    
	   public LocationTracker(Context _context, Activity _activity, ConnectionCallbacks _connectionCallbacks) {
	      // Exists only to defeat instantiation.
	        /*
	         * Create a new location client, using the enclosing class to
	         * handle callbacks.
	         */
		   
		   this.context = _context;
		   this.connectionCallbacks = _connectionCallbacks;
		   //this.connectionFailedListener = _connectionFailedListener;
		   this.activity = _activity;
		   
		   mLocationClient = new LocationClient(this.context,this.connectionCallbacks, this);
		   mLocationClient.connect();
	   }
	   
	   /**
	     * Verify that Google Play services is available before making a request.
	     *
	     * @return true if Google Play services is available, otherwise false
	     */
	    private boolean servicesConnected() {

	        // Check that Google Play services is available
	        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.context);

	        // If Google Play services is available
	        if (ConnectionResult.SUCCESS == resultCode) {
	            // In debug mode, log the status
	            Log.d(LocationUtils.APPTAG, context.getResources().getString(R.string.play_services_available));

	            // Continue
	            return true;
	        // Google Play services was not available for some reason
	        } else {
	            // Display an error dialog
	            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this.activity, 0);
	            if (dialog != null) {
	                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
	                errorFragment.setDialog(dialog);
	                //errorFragment.show(getSupportFragmentManager(), com.android.ratethem.location.LocationUtils.APPTAG);
	            }
	            return false;
	        }
	    }

	    /**
	     * Invoked by the "Get Location" button.
	     *
	     * Calls getLastLocation() to get the current location
	     *
	     * @param v The view object associated with this method, in this case a Button.
	     */
	    public void getLocation() {

	        // If Google Play Services is available
	        if (servicesConnected()) {

	            // Get the current location
	            Location currentLocation = mLocationClient.getLastLocation();

	            // Display the current location in the UI
	            //mLatLng.setText(LocationUtils.getLatLng(this, currentLocation));
	    		Log.d(LOG_TAG,LocationUtils.getLatLng(this.context, currentLocation));
	    		//mLatitude = String.valueOf(currentLocation.getLatitude());
	    		//mLongitude = String.valueOf(currentLocation.getLongitude());
	    		location = currentLocation;
	    		
	            if(activity instanceof InsertData) {
	            	 ((InsertData) activity).onLocationReady();
	            } else
	            	((SearchList) activity).onLocationReady();
	        }
	    }

	    /**
	     * Invoked by the "Get Address" button.
	     * Get the address of the current location, using reverse geocoding. This only works if
	     * a geocoding service is available.
	     *
	     * @param v The view object associated with this method, in this case a Button.
	     */
	    // For Eclipse with ADT, suppress warnings about Geocoder.isPresent()
	    @SuppressLint("NewApi")
	    public void getAddress() {

	        // In Gingerbread and later, use Geocoder.isPresent() to see if a geocoder is available.
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !Geocoder.isPresent()) {
	            // No geocoder is present. Issue an error message
	            Toast.makeText(this.context, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
	            return;
	        }

	        if (servicesConnected()) {

	            // Get the current location
	            Location currentLocation = mLocationClient.getLastLocation();

	            // Turn the indefinite activity indicator on
	            //mActivityIndicator.setVisibility(View.VISIBLE);
				Log.d(LOG_TAG,"Starting getting address background service");


	            // Start the background task
	            (new GetAddressTask(this.context)).execute(currentLocation);
	        }
	    }

	    /**
	     * Invoked by the "Start Updates" button
	     * Sends a request to start location updates
	     *
	     * @param v The view object associated with this method, in this case a Button.
	     */
	    public void startUpdates(View v) {
	        mUpdatesRequested = true;

	        if (servicesConnected()) {
	            startPeriodicUpdates();
	        }
	    }
	    

	    /**
	     * Invoked by the "Stop Updates" button
	     * Sends a request to remove location updates
	     * request them.
	     *
	     * @param v The view object associated with this method, in this case a Button.
	     */
	    public void stopUpdates(View v) {
	        mUpdatesRequested = false;

	        if (servicesConnected()) {
	            stopPeriodicUpdates();
	        }
	    }



	    /*
	     * Called by Location Services if the attempt to
	     * Location Services fails.
	     */
	    @Override
	    public void onConnectionFailed(ConnectionResult connectionResult) {

	        /*
	         * Google Play services can resolve some errors it detects.
	         * If the error has a resolution, try sending an Intent to
	         * start a Google Play services activity that can resolve
	         * error.
	         */
	        if (connectionResult.hasResolution()) {
	            try {

	                // Start an Activity that tries to resolve the error
	                connectionResult.startResolutionForResult(
	                        this.activity,
	                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

	                /*
	                * Thrown if Google Play services canceled the original
	                * PendingIntent
	                */

	            } catch (IntentSender.SendIntentException e) {

	                // Log the error
	                e.printStackTrace();
	            }
	        } else {

	            // If no resolution is available, display a dialog to the user with the error.
	            showErrorDialog(connectionResult.getErrorCode());
	        }
	    }

	    /**
	     * Report location updates to the UI.
	     *
	     * @param location The updated location.
	     */
	    @Override
	    public void onLocationChanged(Location location) {

	        // Report to the UI that the location was updated
	        //mConnectionStatus.setText(R.string.location_updated);
	        Log.e(LocationUtils.APPTAG, context.getString(R.string.location_updated));

	        // In the UI, set the latitude and longitude to the value received
	        //mLatLng.setText(LocationUtils.getLatLng(this, location));
	        Log.d(LocationUtils.APPTAG, LocationUtils.getLatLng(this.context, location));
	    }

	    /**
	     * In response to a request to start updates, send a request
	     * to Location Services
	     */
	    private void startPeriodicUpdates() {

	        mLocationClient.requestLocationUpdates(mLocationRequest, (com.google.android.gms.location.LocationListener) this);
	        //mConnectionState.setText(R.string.location_requested);
	        Log.d(LocationUtils.APPTAG, context.getString(R.string.location_requested));
	    }

	    /**
	     * In response to a request to stop updates, send a request to
	     * Location Services
	     */
	    private void stopPeriodicUpdates() {
	        mLocationClient.removeLocationUpdates((com.google.android.gms.location.LocationListener) this);
	        //mConnectionState.setText(R.string.location_updates_stopped);
	        Log.d(LocationUtils.APPTAG, context.getString(R.string.location_updates_stopped));
	    }

	    /**
	     * An AsyncTask that calls getFromLocation() in the background.
	     * The class uses the following generic types:
	     * Location - A {@link android.location.Location} object containing the current location,
	     *            passed as the input parameter to doInBackground()
	     * Void     - indicates that progress units are not used by this subclass
	     * String   - An address passed to onPostExecute()
	     */
	    protected class GetAddressTask extends AsyncTask<Location, Void, String> {

	        // Store the context passed to the AsyncTask when the system instantiates it.
	        Context localContext;

	        // Constructor called by the system to instantiate the task
	        public GetAddressTask(Context context) {

	            // Required by the semantics of AsyncTask
	            super();

	            // Set a Context for the background task
	            localContext = context;
	        }

	        /**
	         * Get a geocoding service instance, pass latitude and longitude to it, format the returned
	         * address, and return the address to the UI thread.
	         */
	        @Override
	        protected String doInBackground(Location... params) {
	            /*
	             * Get a new geocoding service instance, set for localized addresses. This example uses
	             * android.location.Geocoder, but other geocoders that conform to address standards
	             * can also be used.
	             */
	            Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

	            // Get the current location from the input parameter list
	            Location location = params[0];

	            // Create a list to contain the result address
	            List <Address> addresses = null;

	            // Try to get an address for the current location. Catch IO or network problems.
	            try {

	                /*
	                 * Call the synchronous getFromLocation() method with the latitude and
	                 * longitude of the current location. Return at most 1 address.
	                 */
	                addresses = geocoder.getFromLocation(location.getLatitude(),
	                    location.getLongitude(), 1
	                );

	                // Catch network or other I/O problems.
	                } catch (IOException exception1) {

	                    // Log an error and return an error message
	                    Log.e(LocationUtils.APPTAG, context.getString(R.string.IO_Exception_getFromLocation));

	                    // print the stack trace
	                    exception1.printStackTrace();

	                    // Return an error message
	                    return (context.getString(R.string.IO_Exception_getFromLocation));

	                // Catch incorrect latitude or longitude values
	                } catch (IllegalArgumentException exception2) {

	                    // Construct a message containing the invalid arguments
	                    String errorString = context.getString(
	                            R.string.illegal_argument_exception,
	                            location.getLatitude(),
	                            location.getLongitude()
	                    );
	                    // Log the error and print the stack trace
	                    Log.e(LocationUtils.APPTAG, errorString);
	                    exception2.printStackTrace();

	                    //
	                    return errorString;
	                }
	                // If the reverse geocode returned an address
	                if (addresses != null && addresses.size() > 0) {

	                    // Get the first address
	                    Address address = addresses.get(0);

	                    // Format the first line of address
	                    String addressText = context.getString(R.string.address_output_string,

	                            // If there's a street address, add it
	                            address.getMaxAddressLineIndex() > 0 ?
	                                    address.getAddressLine(0) : "",

	                            // Locality is usually a city
	                            address.getLocality(),

	                            // The country of the address
	                            address.getCountryName()
	                    );

	                    // Return the text
	                    return addressText;

	                // If there aren't any addresses, post a message
	                } else {
	                  return context.getString(R.string.no_address_found);
	                }
	        }

	        /**
	         * A method that's called once doInBackground() completes. Set the text of the
	         * UI element that displays the address. This method runs on the UI thread.
	         */
	        @Override
	        protected void onPostExecute(String address) {

	            // Turn off the progress bar
	            //mActivityIndicator.setVisibility(View.GONE);

	            // Set the address in the UI
	            //mAddress.setText(address);
	            Log.d(LocationUtils.APPTAG, "Address: "+address);
	            //mLocationEdit.setText(address);
	            //mLocationEdit.setFocusable(false);
	            currentAddress = address;
	            if(activity instanceof InsertData) {
	            	 ((InsertData) activity).onAddressReady();
	            } else
	            	((SearchList) activity).onAddressReady();
	        }
	    }

	    /**
	     * Show a dialog returned by Google Play services for the
	     * connection error code
	     *
	     * @param errorCode An error code returned from onConnectionFailed
	     */
	    private void showErrorDialog(int errorCode) {

	        // Get the error dialog from Google Play services
	        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
	            errorCode,
	            this.activity,
	            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

	        // If Google Play services can provide an error dialog
	        if (errorDialog != null) {

	            // Create a new DialogFragment in which to show the error dialog
	            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

	            // Set the dialog in the DialogFragment
	            errorFragment.setDialog(errorDialog);

	            // Show the error dialog in the DialogFragment
	            //errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
	        }
	    }

	    /**
	     * Define a DialogFragment to display the error dialog generated in
	     * showErrorDialog.
	     */
	    public static class ErrorDialogFragment extends DialogFragment {

	        // Global field to contain the error dialog
	        private Dialog mDialog;

	        /**
	         * Default constructor. Sets the dialog field to null
	         */
	        public ErrorDialogFragment() {
	            super();
	            mDialog = null;
	        }

	        /**
	         * Set the dialog to display
	         *
	         * @param dialog An error dialog
	         */
	        public void setDialog(Dialog dialog) {
	            mDialog = dialog;
	        }

	        /*
	         * This method must return a Dialog to the DialogFragment.
	         */
	        @Override
	        public Dialog onCreateDialog(Bundle savedInstanceState) {
	            return mDialog;
	        }
	    }
	   
	    
	    public Location getCurrentLocation(){
	    	return location;
	    }
	    
	    public String getCurrentAddLocation(){
	    	return currentAddress;
	    }
	   
}