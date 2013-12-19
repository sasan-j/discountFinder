package com.android.ratethem;

//import com.android.ratethem.SearchList.RateAdapter;

/**
 * Class is helper to add information to list.
 */
public class ItemInfo {

	private String mItemID = null;
	private String mItemName = null;
	private String mItemCategory = null;
	private String mPlaceName = null;
	private String mRating = null;
	private String mPicPath = null;
	private String mLocation = null;
	private String mLocLatitude = null;
	private String mLocLongitude = null;
	private String mComments = null;
	private String mImageUrl = null;
	private String mLocalImagePath = null;

	

	
	public ItemInfo(String mItemID, String mItemName, String mItemCategory,
			String mPlaceName, String mRating, String mPicPath,
			String mLocation, String mLocLatitude, String mLocLongitude,
			String mComments, String mImageUrl, String mLocalImagePath) {
		super();
		this.mItemID = mItemID;
		this.mItemName = mItemName;
		this.mItemCategory = mItemCategory;
		this.mPlaceName = mPlaceName;
		this.mRating = mRating;
		this.mPicPath = mPicPath;
		this.mLocation = mLocation;
		this.mLocLatitude = mLocLatitude;
		this.mLocLongitude = mLocLongitude;
		this.mComments = mComments;
		this.mImageUrl = mImageUrl;
		this.mLocalImagePath = mLocalImagePath;
	}
	




	public String getmImageUrl() {
		return mImageUrl;
	}





	public void setmImageUrl(String mImageUrl) {
		this.mImageUrl = mImageUrl;
	}





	public String getmLocalImagePath() {
		return mLocalImagePath;
	}





	public void setmLocalImagePath(String mLocalImagePath) {
		this.mLocalImagePath = mLocalImagePath;
	}





	public String getmPicPath() {
		return mPicPath;
	}




	public void setmPicPath(String mPicPath) {
		this.mPicPath = mPicPath;
	}




	public String getmItemID() {
		return mItemID;
	}




	public String getmItemName() {
		return mItemName;
	}




	public String getmItemCategory() {
		return mItemCategory;
	}




	public String getmPlaceName() {
		return mPlaceName;
	}




	public String getmRating() {
		return mRating;
	}




	public String getmLocation() {
		return mLocation;
	}




	public String getmLocLatitude() {
		return mLocLatitude;
	}




	public String getmLocLongitude() {
		return mLocLongitude;
	}




	public String getmComments() {
		return mComments;
	}


}
