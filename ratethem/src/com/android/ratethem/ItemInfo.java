package com.android.ratethem;

/**
 * Class is helper to add information to list.
 */
public class ItemInfo {
	
	private String mName = null;
	
	private String mRate = null;
	
	private String mPic = null;
	
	public ItemInfo(String name, String rate, String pic){
		this.mName = name;
		this.mRate = rate;
		this.mPic = pic;
	}
	
	public String getName(){
		return mName;
	}
	
	public String getRating(){
		return mRate;
	}
	
	public String getPicture(){
		return mPic;
	}

}
