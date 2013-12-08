package com.android.ratethem;
//testing
//import com.androidexample.cameraphotocapture.CameraPhotoCapture;

import com.android.ratethem.util.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.ratethem.R;
import com.android.ratethem.providers.RateAgent;
import com.android.ratethem.server.ServerPost;
import com.android.ratethem.util.RateThemUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;
import android.os.Build;


public class InsertData extends Activity {

	private ImageButton takepic, done;
	protected static final String LOG_TAG = "ratethem";
	private RatingBar ratingBar;
	private File mImageFile;
	private String mItemName = null;

	//for image capturing
	private static final int ACTION_CAPTURE_IMAGE = 1;
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private ImageView mImageView;
	private Bitmap mImageBitmap;
	private String mCurrentPhotoPath;
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
	private EditText mPlaceEdit;
	private EditText mLocationEdit;
	private EditText mViewsEdit;
	private String mLatitude = null;
	private String mLongitude = null;
	InsertData CameraActivity = null;
	private Bitmap mPhoto = null;
	private String mCriteria = null;
	private String mSearch = null;
	private String mPublish = null;
	private String mPlaceInformation = null;
	private String mRatings = null;
	private String mLocationInformation = null;
	private String mYourViews = null;

	
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
				setPic();
			}
			break;
		}//ACTION_CAPTURE_IMAGE

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
	/*
	private void setBtnListenerOrDisable( 
			Button btn, 
			Button.OnClickListener onClickListener,
			String intentName
	) {
		if (isIntentAvailable(this, intentName)) {
			btn.setOnClickListener(onClickListener);        	
		} else {
			btn.setText( 
				getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setClickable(false);
		}
	}
	*/
	
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
	
	private void setPic() {

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
		
		
	    Drawable d = getResources().getDrawable(R.drawable.no_image);
	    mImageView.setImageBitmap(Bitmap.createScaledBitmap(mImageBitmap, d.getIntrinsicWidth(), d.getIntrinsicHeight(), false));
		
		//mImageView.setImageBitmap(Bitmap.createScaledBitmap(mImageBitmap,
		//		mImageView.getMaxWidth(), mImageView.getMaxHeight(), false));
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
			mItemName = extras.getString(RateThemUtil.ITEM_NAME);
			mCriteria = extras.getString(RateThemUtil.CRITERIA);
		}
		Log.d(LOG_TAG, "Criteria sent: " + mCriteria);
		mImageView = (ImageView) findViewById(R.id.showImg);
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
		// Display button click listener
		//takepic.setOnClickListener((OnClickListener) TakePicListner);

		done = (ImageButton) findViewById(R.id.Done);
		done.setOnClickListener((OnClickListener) DoneListener);

	}

	
	private View.OnClickListener DoneListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			insertInformation();
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
	private void insertInformation() {
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
		} else if (mItemName == null) {
			mItemName = getString(R.string.no_info);
		}
		// Below code inserts into database currently. Must be commented
		// when server contact is established.
		insertToDb();

		// Below commented code must be uncommented when server insert is ready.
		// insertToServer();
	}

	/**
	 * Insert information to database.
	 */
	private void insertToDb() {
		try {
			ContentValues cv = new ContentValues();
			cv.put(RateAgent.RateProvider.ITEM_NAME, mItemName);
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

	/**
	 * Insert information to server.
	 */
	private void insertToServer() {
		ServerPost svPost = new ServerPost();
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			json.put(RateThemUtil.ITEM_NAME, mItemName);
			json.put(RateThemUtil.ITEM_PLACE_NAME, mPlaceInformation);
			json.put(RateThemUtil.ITEM_PIC, mCurrentPhotoPath);
			json.put(RateThemUtil.ITEM_RATING, mRatings);
			json.put(RateThemUtil.ITEM_LOC, mLocationInformation);
			json.put(RateThemUtil.ITEM_COMMENT, mYourViews);

			jsonArray.put(json);
			JSONObject jObject = new JSONObject();
			jObject.put(RateThemUtil.TABLE_NAME, jsonArray);
			svPost.pushDataToServer(RateThemUtil.SERVER_URL, jObject);
		} catch (JSONException e) {
			e.printStackTrace();
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

}
