package com.android.ratethem;

//import com.androidexample.cameraphotocapture.CameraPhotoCapture;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import com.android.ratethem.R;
import com.android.ratethem.providers.RateAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

public class InsertData extends Activity {

	private Button takepic, done;
	protected static final String TAG = "RateThem_S3";
	private RatingBar ratingBar;
	private File mImageFile;
	
	private String mItemName = null;

	final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
	private static final int FILE = 7;
	public static final Uri RATE_URI = Uri
            .parse("content://com.android.ratethem.providers.RateContentProvider" + "/"
                    + RateAgent.RateProvider.TABLE_NAME);

	Uri imageUri = null;
	static TextView imageDetails = null;
	private ImageView mImage;
	private EditText mLocation;
	private EditText mViews;
	InsertData CameraActivity = null;
	
	private Bitmap mPhoto;
	
	private String mCriteria = null;
	
	private TextView mLocInformation;
	
	private TextView mViewInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insert_data);

		CameraActivity = this;
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			mItemName = extras.getString("item_name");
			mCriteria = extras.getString("criteria");
		}

		mImage = (ImageView) findViewById(R.id.showImg);
		mLocation = (EditText) findViewById(R.id.location);
		mViews = (EditText) findViewById(R.id.your_view);
		mLocInformation = (TextView) findViewById(R.id.location_info);
		mViewInfo = (TextView) findViewById(R.id.your_view_info);
		Log.d(TAG, "mLocInformation: "+mLocInformation);
		Log.d(TAG, "mViewInfo: "+mViewInfo);
		initButton();
		initRatingBar();
		if("Search".equals(mCriteria)){
			takepic.setVisibility(View.GONE);
			done.setVisibility(View.GONE);
			mLocation.setVisibility(View.GONE);
			mViews.setVisibility(View.GONE);
			mLocInformation.setVisibility(View.VISIBLE);
			mViewInfo.setVisibility(View.VISIBLE);
			String locationInfo = extras.getString("location");
			String rateInfo = extras.getString("rate");
			String commentInfo = extras.getString("comment");
			byte[] picInfo = extras.getByteArray("pic");
			mImage.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(picInfo)));
			mLocInformation.setText(locationInfo);
			mViewInfo.setText(commentInfo);			
			ratingBar.setRating(Float.parseFloat(rateInfo));
		}else if("Publish".equals(mCriteria)){
			mLocInformation.setVisibility(View.GONE);
			mViewInfo.setVisibility(View.GONE);
			mLocation.setVisibility(View.VISIBLE);
			mViews.setVisibility(View.VISIBLE);
		}	

	}

	public void initButton() {
		// Display button Taking Pictures
		takepic = (Button) findViewById(R.id.TakePic);

		// Display button click listener
		takepic.setOnClickListener((OnClickListener) TakePicListner);

		done = (Button) findViewById(R.id.Done); 
		done.setOnClickListener((OnClickListener) DoneListener);

	}
	
	private View.OnClickListener DoneListener = new View.OnClickListener() {	
		@Override
		public void onClick(View v) {
			insertInformation();			
		}
	};

	private View.OnClickListener TakePicListner = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			launchCamera();
			// String fileName = "Camera_Example.jpg";
			//
			// // Create parameters for Intent with filename
			//
			// ContentValues values = new ContentValues();
			//
			// values.put(MediaStore.Images.Media.TITLE, fileName);
			//
			// values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
			//
			// /****** imageUri is the current activity attribute, define and
			// save it for later usage *****/
			// imageUri = getContentResolver().insert(
			// MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			//
			// /****** EXTERNAL_CONTENT_URI : style URI for the "primary"
			// external storage volume. ******/
			//
			//
			// /****** Standard Intent action that can be sent to have the
			// camera application capture an image and return it. ******/
			//
			// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			//
			// intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			//
			// intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			//
			// startActivityForResult(intent,
			// CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		}
	};

	private void launchCamera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		File imgFolder = new File(Environment.getExternalStorageDirectory()
				+ "/TakePicApp");
		if (!imgFolder.exists()) {
			imgFolder.mkdir();
		}
		Log.d(TAG, "Image Path just created: " + imgFolder.getAbsolutePath());
		mImageFile = new File(imgFolder.getAbsolutePath(), "PIC"
				+ System.currentTimeMillis() + ".jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mImageFile));
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	/**
	 * Method to capture data sent back from activity launched by startActivityResult.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if("Search".equals(mCriteria)){
				
			}else if("Publish".equals(mCriteria)){
				if(mLocation.getVisibility()== View.GONE){
					mLocation.setVisibility(View.VISIBLE);
				}
				if(mViews.getVisibility() == View.GONE){
					mViews.setVisibility(View.VISIBLE);
				}
			}
			
			switch (requestCode) {
			case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
				// Get the information about the picture taken by camera and
				// display a thumbnail of the picture.
				mPhoto = (Bitmap) data.getExtras().get("data");
				Log.d(TAG, "Bitmap: " + mPhoto);
				mImage.setImageBitmap(Bitmap.createScaledBitmap(mPhoto,
						mImage.getWidth(), mImage.getHeight(), false));
			}
		}
	}
	
	private void insertInformation() {
		String locationInfo = mLocation.getText().toString();
		String yourView = mViews.getText().toString();
		String rate = String.valueOf(ratingBar.getRating());
		
		if(locationInfo == null){
			locationInfo = getString(R.string.no_info);
		} else if(yourView == null){
			yourView = getString(R.string.no_info);
		} else if(rate == null){
			rate = "0";
		} else if(mItemName == null){
			mItemName = getString(R.string.no_info);
		}

		try {
			ContentValues cv = new ContentValues();
			cv.put(RateAgent.RateProvider.ITEM_NAME, mItemName);
			cv.put(RateAgent.RateProvider.ITEM_PIC, getImageByte());
			cv.put(RateAgent.RateProvider.ITEM_RATING, rate);
			cv.put(RateAgent.RateProvider.ITEM_LOC, locationInfo);
			cv.put(RateAgent.RateProvider.ITEM_COMMENT, yourView);
			ContentProviderClient client = getBaseContext()
					.getContentResolver()
					.acquireContentProviderClient(RATE_URI);
			client.insert(RATE_URI, cv);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private byte[] getImageByte(){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		mPhoto.compress(Bitmap.CompressFormat.PNG, 100, bos);
		byte[] bArray = bos.toByteArray();
		return bArray;
	}

	public static String convertImageUriToFile(Uri imageUri, Activity activity) {
		Cursor cursor = null;
		int imageID = 0;

		try {
			/*********** Which columns values want to get *******/
			String[] proj = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID,
					MediaStore.Images.Thumbnails._ID,
					MediaStore.Images.ImageColumns.ORIENTATION };

			cursor = activity.managedQuery(

			imageUri, // Get data for specific image URI
					proj, // Which columns to return
					null, // WHERE clause; which rows to return (all rows)
					null, // WHERE clause selection arguments (none)
					null // Order-by clause (ascending by name)

					);

			int columnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
			int columnIndexThumb = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
			int file_ColumnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			// int orientation_ColumnIndex =
			// cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);

			int size = cursor.getCount();

			/******* If size is 0, there are no images on the SD Card. *****/

			if (size == 0) {
				// imageDetails.setText("No Image");
			} else {

				int thumbID = 0;
				if (cursor.moveToFirst()) {

					/**************** Captured image details ************/

					/***** Used to show image on view in LoadImagesFromSDCard class ******/
					imageID = cursor.getInt(columnIndex);

					thumbID = cursor.getInt(columnIndexThumb);

					String Path = cursor.getString(file_ColumnIndex);

					// String orientation =
					// cursor.getString(orientation_ColumnIndex);

					String CapturedImageDetails = " CapturedImageDetails : \n\n"
							+ " ImageID :"
							+ imageID
							+ "\n"
							+ " ThumbID :"
							+ thumbID + "\n" + " Path :" + Path + "\n";

					// Show Captured Image detail on view
					// imageDetails.setText(CapturedImageDetails);

				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return "" + imageID;
	}

	// Class with extends AsyncTask class
	public class LoadImagesFromSDCard extends AsyncTask<String, Void, Void> {

		private ProgressDialog Dialog = new ProgressDialog(InsertData.this);

		Bitmap mBitmap;

		protected void onPreExecute() {
			/****** NOTE: You can call UI Element here. *****/

			// UI Element
			Dialog.setMessage("Loading image from Sdcard..");
			Dialog.show();
		}

		// Call after onPreExecute method
		protected Void doInBackground(String... urls) {

			Bitmap bitmap = null;
			Bitmap newBitmap = null;
			Uri uri = null;

			try {

				/**
				 * Uri.withAppendedPath Method Description Parameters baseUri
				 * Uri to append path segment to pathSegment encoded path
				 * segment to append Returns a new Uri based on baseUri with the
				 * given segment appended to the path
				 */

				uri = Uri.withAppendedPath(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ""
								+ urls[0]);

				/************** Decode an input stream into a bitmap. *********/
				bitmap = BitmapFactory.decodeStream(getContentResolver()
						.openInputStream(uri));

				if (bitmap != null) {

					/********* Creates a new bitmap, scaled from an existing bitmap. ***********/

					newBitmap = Bitmap.createScaledBitmap(bitmap, 340, 170,
							true);

					bitmap.recycle();

					if (newBitmap != null) {

						mBitmap = newBitmap;
					}
				}
			} catch (IOException e) {
				// Error fetching image, try to recover

				/********* Cancel execution of this task. **********/
				cancel(true);
			}

			return null;
		}

		protected void onPostExecute(Void unused) {

			// NOTE: You can call UI Element here.

			// Close progress dialog
			Dialog.dismiss();

			if (mBitmap != null)
				mImage.setImageBitmap(mBitmap);

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
