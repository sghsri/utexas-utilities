package com.nasageek.utexasutilities.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.TimeFormatException;

public class FeedItem implements Parcelable {
	/*public enum SourceType {
		
	}*/
	
	private BBClass clz;
	private String type, message, contentid, sourcetype;
	private Date date;
	
	public static Parcelable.Creator<FeedItem> CREATOR = new Parcelable.Creator<FeedItem>(){

		@Override
		public FeedItem createFromParcel(Parcel source) {
			return new FeedItem(source);
		}

		@Override
		public FeedItem[] newArray(int size) {
			return new FeedItem[size];
		}
		
	};
	
	public FeedItem(Parcel in)
	{
		type = in.readString();
		message = in.readString();
		contentid = in.readString();
		clz = in.readParcelable(BBClass.class.getClassLoader());
		sourcetype = in.readString();
		date = new Date(in.readLong());
	}
	
	public FeedItem(String type, String message, String contentid, BBClass clz, String sourcetype, String date, SimpleDateFormat formatter)
	{
		this.type = type;
		this.message = message;
		this.contentid = contentid;
		this.clz = clz;
		this.sourcetype = sourcetype;
		try {
			this.date = formatter.parse(date);
		} catch (ParseException e) {
			//TODO
			e.printStackTrace();
		}
	}
	public String getBbId()
	{
		return clz.getBbid();
	}
	public String getCourseId()
	{
		return clz.getCourseId();
	}
	public String getName()
	{
		return clz.getName();
	}
	public Date getDate()
	{
		return date;
	}
	public String getType()
	{
		//fallback for what I presume to be Blackboard's old format
		if(sourcetype == null)
		{
			if("ANNOUNCEMENT".equals(type))
				return "Announcement";
			else
				return "Unknown";
		}
		else if("CO".equals(sourcetype))
			return "Content";
		else if("GB".equals(sourcetype))
			return "Grades";
		else if("CR".equals(sourcetype))
			return "Courses";
		//TODO: fix this, AS should take us to grades I think... notifications are annoying
		else if("AS".equals(sourcetype))
			return "Notification";
		else if("AN".equals(sourcetype))
			return "Announcement";
		else
			return "Unknown";
	}
	public String getMessage()
	{
		return message;
	}
	public String getContentId()
	{
		return contentid;
	}
	public BBClass getBbClass()
	{
		return clz;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(type);
		dest.writeString(message);
		dest.writeString(contentid);
		dest.writeParcelable(clz, 0);
		dest.writeString(sourcetype);
		dest.writeLong(date.getTime());
	}

}