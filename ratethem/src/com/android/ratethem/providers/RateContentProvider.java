package com.android.ratethem.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class RateContentProvider extends ContentProvider {

	private static final String DATABASE_NAME = "RateThem.db";

	private static final String TABLE_NAME = "Items";

	private static final int DATABASE_VERSION = 1;

	private SQLiteDatabase mSqlDb;

	private RateHelper mRateHelper;

	private static class RateHelper extends SQLiteOpenHelper {

		public RateHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("Create table "
					+ TABLE_NAME
					+ "( _id INTEGER PRIMARY KEY AUTOINCREMENT, item_name TEXT, item_pic TEXT, item_rating TEXT, item_place_name TEXT, item_location TEXT, item_location_latitude TEXT, item_location_longitude TEXT, item_comment TEXT);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		mSqlDb = mRateHelper.getWritableDatabase();
		int rowsDeleted = mSqlDb.delete(TABLE_NAME, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		mSqlDb = mRateHelper.getWritableDatabase();
		Uri rowUri = null;
		long rowId = mSqlDb.insert(TABLE_NAME, "", values);
		if (rowId > 0) {
			rowUri = ContentUris.appendId(
					RateAgent.RateProvider.CONTENT_URI.buildUpon(), rowId)
					.build();
			getContext().getContentResolver().notifyChange(rowUri, null);
		}
		return rowUri;
	}

	@Override
	public boolean onCreate() {
		mRateHelper = new RateHelper(getContext());
		return (mRateHelper == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = mRateHelper.getReadableDatabase();
		qb.setTables(TABLE_NAME);
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		mSqlDb = mRateHelper.getWritableDatabase();
		int rowsUpdated = mSqlDb.update(TABLE_NAME, values, selection,
				selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

}
