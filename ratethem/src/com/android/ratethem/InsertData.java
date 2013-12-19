package com.android.ratethem;
//testing
//import com.androidexample.cameraphotocapture.CameraPhotoCapture;

import com.android.ratethem.util.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
		insertToDb();

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
			multipartEntity.addTextBody(RateThemUtil.ITEM_LATITUDE, "567587");
			multipartEntity.addTextBody(RateThemUtil.ITEM_LONGITUDE, "657543");
			multipartEntity.addTextBody(RateThemUtil.USER_ID, "test1");
	
			if(mCurrentPhotoPath != null){
				//for image
				//AssetManager assetManager = getAssets();
				//String fileList[] = assetManager.list("tt");
				//Environment.getDataDirectory()
				/*
			File outputDir = getApplication().getCacheDir(); // context being the Activity pointer
			File outputFile = File.createTempFile("prefix", "txt", outputDir);
			FileWriter writer = new FileWriter(outputFile, true);
			try {
			    writer.write("append here\n");
			} finally {
			   writer.close();
			}*/
				mImageFile = new File(mCurrentPhotoPath);
				multipartEntity.addPart("userfile", new FileBody(mImageFile));
			}
			httppost.setEntity(multipartEntity.build());
			HttpResponse response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() > 202 ) {
				Log.d(LOG_TAG,response.getStatusLine().toString());
				return response.getStatusLine().toString();
			} else {
				//				//removing files that were sent
				//				for (File f : zipfiles){
				//					f.delete();
				//				}
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

}
