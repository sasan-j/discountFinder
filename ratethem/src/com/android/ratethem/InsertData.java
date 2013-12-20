package com.android.ratethem;
//testing
//import com.androidexample.cameraphotocapture.CameraPhotoCapture;

import com.android.ratethem.util.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.android.gms.common.GooglePlayServicesClient;



import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import com.android.ratethem.providers.RateAgent;
import com.android.ratethem.util.RateThemUtil;
import com.android.ratethem.location.LocationTracker;
import com.android.ratethem.location.LocationUtils;


import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;


public class InsertData extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks {

	private ImageButton takepic, done;
	protected static final String LOG_TAG = "ratethem";
	private RatingBar ratingBar;
	private File mImageFile = null;
	private String mItemName = null;
	private String mItemCategory = null;

	//for image capturing
	private static final int ACTION_CAPTURE_IMAGE = 1;
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private ImageView mImageView;
	private Bitmap mImageBitmap;
	private String mCurrentPhotoPath = null;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;



	//private static final int FILE = 7;
	public static final Uri RATE_URI = Uri
			.parse("content://com.android.ratethem.providers.RateContentProvider"
					+ "/" + RateAgent.RateProvider.TABLE_NAME);

	private static final String PICTURES_DIR = "photos";

	Uri imageUri = null;
	static TextView imageDetails = null;
	//private ImageView mImage;
	private EditText mItemNameEdit;
	private EditText mPlaceEdit;
	private EditText mLocationEdit;
	private EditText mViewsEdit;
	
	private String mItemNameInfo = null;
	private String mPlaceInformation = null;
	private String mLocationInformation = null;

	private String mLatitude = null;
	private String mLongitude = null;
	InsertData CameraActivity = null;
	private Bitmap mPhoto = null;
	private String mCriteria = null;
	private String mSearch = null;
	private String mPublish = null;
	private String mRatings = null;
	private String mYourViews = null;

	
	///////////////////////////////////////////////////////////////////////////////
	///////for capturing location ////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	private LocationTracker locationTracker = null;

    
	///////////////////////////////////////////////////////////////////////////////
	///////for capturing image ////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////

	/* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}


	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			Log.d(LOG_TAG, "inside first if");
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
			Log.d(LOG_TAG, "after factory");
			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d(LOG_TAG, "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(LOG_TAG, "External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		Log.d(LOG_TAG, "TimeStamp is: "+timeStamp);
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		Log.d(LOG_TAG, "imageFileName is: "+imageFileName);
		File albumF = getAlbumDir();
		Log.d(LOG_TAG, "after getAlbumDir");
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {
		Log.d(LOG_TAG, "before  createImageFile");
		File f = createImageFile();
		Log.d(LOG_TAG, "after createImageFile");
		mCurrentPhotoPath = f.getAbsolutePath();
		return f;
	}

	private void dispatchTakePictureIntent(int actionCaptureImage) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		try {
			Log.d(LOG_TAG, "before setupphotofile");
			f = setUpPhotoFile();
			Log.d(LOG_TAG, "after setupphotofile");
			mCurrentPhotoPath = f.getAbsolutePath();
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}
		startActivityForResult(takePictureIntent, actionCaptureImage);
	}

	/*
	 * Method to capture data sent back from activity launched by
	 * startActivityResult.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ACTION_CAPTURE_IMAGE: {
			if (resultCode == RESULT_OK) {
				//handleBigCameraPhoto();
				Log.d(LOG_TAG, "Result is ok and being handled");
				try {
					setPic();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		}//ACTION_CAPTURE_IMAGE
		
        // If the request code matches the code sent in onConnectionFailed
        case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

            switch (resultCode) {
                // If Google Play services resolved the problem
                case Activity.RESULT_OK:

                    // Log the result
                    Log.d(LocationUtils.APPTAG, getString(R.string.resolved));

                    // Display the result
                    //mConnectionState.setText(R.string.connected);
                    Log.d(LocationUtils.APPTAG, getString(R.string.connected));
                    //mConnectionStatus.setText(R.string.resolved);
                    Log.d(LocationUtils.APPTAG, getString(R.string.resolved));

                break;

                // If any other result was returned by Google Play services
                default:
                    // Log the result
                    Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));

                    // Display the result
                    //mConnectionState.setText(R.string.disconnected);
                    Log.d(LocationUtils.APPTAG, getString(R.string.disconnected));
                    //mConnectionStatus.setText(R.string.no_resolution);
                    Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));


                break;
            }

        // If any other request code was received
        default:
           // Report that this Activity received an unknown requestCode
           Log.d(LocationUtils.APPTAG,
                   getString(R.string.unknown_activity_request_code, requestCode));

           break;


		} // switch
	}


	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
				packageManager.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}


	private void setBtnListenerOrDisable( 
			ImageButton imgBtn, 
			ImageButton.OnClickListener onClickListener,
			String intentName
			) {
		if (isIntentAvailable(this, intentName)) {
			imgBtn.setOnClickListener(onClickListener);        	
		} else {
			imgBtn.setClickable(false);
		}
	}

	private void setPic() throws IOException {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		mImageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		/* Associate the Bitmap to the ImageView */
		//mImageView.setImageBitmap(mImageBitmap);
		
		//resaving file with low resolution
		OutputStream fOut = null;
		File file = new File(mCurrentPhotoPath);
		try {
			fOut = new FileOutputStream(file);
			mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

		Drawable d = getResources().getDrawable(R.drawable.no_image);
		mImageView.setImageBitmap(Bitmap.createScaledBitmap(mImageBitmap, d.getIntrinsicWidth(), d.getIntrinsicHeight(), false));

	}

	///////////////////////////////////////////////////////////////////////////////////////


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insert_data);

		if( savedInstanceState != null ) {
			Toast.makeText(this, savedInstanceState .getString("message"), Toast.LENGTH_LONG).show();
		}
		// Get the extras from calling activity.
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			//mItemName = extras.getString(RateThemUtil.ITEM_NAME);
			mItemCategory = extras.getString(RateThemUtil.ITEM_CATEGORY);
			mCriteria = extras.getString(RateThemUtil.CRITERIA);
		}
		Log.d(LOG_TAG, "Criteria sent: " + mCriteria);
		mImageView = (ImageView) findViewById(R.id.showImg);
		
		mItemNameEdit = (EditText) findViewById(R.id.item_name);
		mPlaceEdit = (EditText) findViewById(R.id.place_name);
		mLocationEdit = (EditText) findViewById(R.id.location);
		mViewsEdit = (EditText) findViewById(R.id.your_view);
		
		mSearch = getString(R.string.search);
		mPublish = getString(R.string.publish);
		Log.d(LOG_TAG, "Strings for Search and publish: " + mSearch + " : "
				+ mPublish);
		// instantiate the buttons and add listener.
		initButton();
		// instantiate rating bar and add listener.
		initRatingBar();

		//////////////////////////////////////////////////////////////
		locationTracker = new LocationTracker(this,this, this);
		//////////////////////////////////////////////////////////////




		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}

	}


	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("message", "This is my message to be reloaded");
		//outState.putParcelable("PHOTO", mPhoto);
		//for capture image
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
		mImageView.setImageBitmap(mImageBitmap);
	}

	public void initButton() {
		// Display button Taking Pictures
		takepic = (ImageButton) findViewById(R.id.TakePic);
		setBtnListenerOrDisable( 
				takepic, 
				TakePicListner,
				MediaStore.ACTION_IMAGE_CAPTURE
				);

		done = (ImageButton) findViewById(R.id.Done);
		done.setOnClickListener((OnClickListener) DoneListener);
	}


	private View.OnClickListener DoneListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			insertInformation(v);
			Toast.makeText(v.getContext(), "Information added!!!",
					Toast.LENGTH_SHORT).show();
			finish();
		}
	};

	private View.OnClickListener TakePicListner = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			dispatchTakePictureIntent(ACTION_CAPTURE_IMAGE);
		}
	};


	/**
	 * Push information to either database or server.
	 */
	private void insertInformation(View v) {
		
		mItemNameInfo = mItemNameEdit.getText().toString();
		mPlaceInformation = mPlaceEdit.getText().toString();
		mLocationInformation = mLocationEdit.getText().toString();
		mYourViews = mViewsEdit.getText().toString();
		mRatings = String.valueOf(ratingBar.getRating());

		if (mLocationInformation == null) {
			mLocationInformation = getString(R.string.no_info);
		} else if (mYourViews == null) {
			mYourViews = getString(R.string.no_info);
		} else if (mRatings == null) {
			mRatings = "0";
		} else if (mItemNameInfo == null) {
			mItemNameInfo = getString(R.string.no_info);
		}
		// Below code inserts into database currently. Must be commented
		// when server contact is established.
		//insertToDb();
		// Below commented code must be uncommented when server insert is ready.
		//insertToServer();			
		new SendToServerTask().execute("bla bla");
		//sendFormToServer(v);
		//Toast.makeText(v.getContext(), ,
		//		Toast.LENGTH_SHORT).show();
	}

	/**
	 * Insert information to database.
	 */
	private void insertToDb() {
		try {
			ContentValues cv = new ContentValues();
			cv.put(RateAgent.RateProvider.ITEM_NAME, mItemNameInfo);
			cv.put(RateAgent.RateProvider.ITEM_CATEGORY, mItemCategory);
			cv.put(RateAgent.RateProvider.ITEM_PLACE_NAME, mPlaceInformation);
			cv.put(RateAgent.RateProvider.ITEM_PIC, mCurrentPhotoPath);
			cv.put(RateAgent.RateProvider.ITEM_RATING, mRatings);
			cv.put(RateAgent.RateProvider.ITEM_LOC, mLocationInformation);
			cv.put(RateAgent.RateProvider.ITEM_COMMENT, mYourViews);

			ContentProviderClient client = getBaseContext()
					.getContentResolver()
					.acquireContentProviderClient(RATE_URI);

			client.insert(RATE_URI, cv);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void initRatingBar() {

		ratingBar = (RatingBar) findViewById(R.id.ratingBar);

		// if rating value is changed,
		// display the current rating value in the result (textview)
		// automatically
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {

				Toast.makeText(InsertData.this,
						String.valueOf(ratingBar.getRating()),
						Toast.LENGTH_SHORT).show();

			}
		});

	}
	
	protected String sendFormToServer(){
	
		String url = "http://df.jafarnejad.org/discounts/post_discount/"; 
	
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			//MultipartEntity reqEntity = new MultipartEntity();
			MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
			multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			multipartEntity.addTextBody(RateThemUtil.ITEM_CATEGORY, mItemCategory);
			multipartEntity.addTextBody(RateThemUtil.ITEM_NAME, mItemNameInfo);
			multipartEntity.addTextBody(RateThemUtil.ITEM_PLACE_NAME, mPlaceInformation);
			multipartEntity.addTextBody(RateThemUtil.ITEM_RATING, mRatings);
			multipartEntity.addTextBody(RateThemUtil.ITEM_LOC, mLocationInformation);
			multipartEntity.addTextBody(RateThemUtil.ITEM_LATITUDE, mLatitude);
			multipartEntity.addTextBody(RateThemUtil.ITEM_LONGITUDE, mLongitude);
			multipartEntity.addTextBody(RateThemUtil.USER_ID, "test1");
			multipartEntity.addTextBody(RateThemUtil.ITEM_COMMENT, mYourViews);

	
			if(mCurrentPhotoPath != null){
				mImageFile = new File(mCurrentPhotoPath);
				multipartEntity.addPart("userfile", new FileBody(mImageFile));
			}
			httppost.setEntity(multipartEntity.build());
			HttpResponse response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() > 202 ) {
				Log.d(LOG_TAG,response.getStatusLine().toString());
				return response.getStatusLine().toString();
			} else {
				Log.d(LOG_TAG,response.getStatusLine().toString());
				return response.getStatusLine().toString();
			}
	
		} catch (Exception e) {
			Log.d(LOG_TAG,e.toString());
			return e.toString();
		}
		//Looper.loop();
	}

	// Uses AsyncTask to create a task away from the main UI thread. This task takes a 
	// URL string and uses it to create an HttpUrlConnection. Once the connection
	// has been established, the AsyncTask downloads the contents of the webpage as
	// an InputStream. Finally, the InputStream is converted into a string, which is
	// displayed in the UI by the AsyncTask's onPostExecute method.
	private class SendToServerTask extends AsyncTask<String, Void, String> {
	   @Override
	   protected String doInBackground(String... urls) {
	         
	       // params comes from the execute() call: params[0] is the url.
//	       try {
	           return sendFormToServer();
//	       } catch (IOException e) {
//	           return "Unable to retrieve web page. URL may be invalid.";
//	       }
	   }
	   // onPostExecute displays the results of the AsyncTask.
	   @Override
	   protected void onPostExecute(String result) {
	       //textView.setText(result);
	       Toast.makeText(InsertData.this, result, Toast.LENGTH_LONG).show();

	  }
	}
	
    ///////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];
            
            if (event.getAction() == MotionEvent.ACTION_UP 
     && (x < w.getLeft() || x >= w.getRight() 
     || y < w.getTop() || y > w.getBottom()) ) { 
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
     return ret;
    }
    
    ///////////////Location////////////////////////
    ///////////////////////////////////////////////
    
    
    //////////////////////////////////////////////////////
    ///////////////END of LOCATION Stuff/////////////////
    /////////////////////////////////////////////////////
    
    
    
    ////////On{resume,stop,start ....}//////////////////////
    ////////////////////////////////////////////////////////
    /*
     * Called when the Activity is no longer visible at all.
     * Stop updates and disconnect.
     */
    @Override
    public void onStop() {

        // If the client is connected
        /*if (mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }*/

        // After disconnect() is called, the client is considered "dead".
        //mLocationClient.disconnect();

        super.onStop();
    }
    /*
     * Called when the Activity is going into the background.
     * Parts of the UI may be visible, but the Activity is inactive.
     */
    @Override
    public void onPause() {

        super.onPause();
    }

    /*
     * Called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {

        super.onStart();

        /*
         * Connect the client. Don't re-start any requests here;
         * instead, wait for onResume()
         */
        //mLocationClient.connect();

    }
    /*
     * Called when the system detects that this Activity is now visible.
     */
    @Override
    public void onResume() {
        super.onResume();


    }
    ////////////////////////////////////////////////////////


    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        //mConnectionStatus.setText(R.string.connected);
		Log.d(LOG_TAG,getString(R.string.connected));
        locationTracker.getLocation();
        locationTracker.getAddress();
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        //mConnectionStatus.setText(R.string.disconnected);
		Log.d(LOG_TAG,getString(R.string.disconnected));

    }
    
    public void onLocationReady(){
    	Location location = locationTracker.getCurrentLocation();
    	mLatitude = String.valueOf(location.getLatitude());
    	mLongitude = String.valueOf(location.getLongitude());
    }
    
    public void onAddressReady(){
    	mLocationEdit.setText(locationTracker.getCurrentAddLocation());
    }

}
