package com.example.android.myvideoplayer;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MediaFiles implements Parcelable {
    private String mId;
    private String mTitle;
    private String mDisplayName;
    private String mSize;
    private String mDuration;
    private String mPath;
    private String mDateAdded;

    public MediaFiles(String Id, String Title, String DisplayName, String Size, String Duration, String Path, String DateAdded) {
        this.mId = Id;
        this.mTitle = Title;
        this.mDisplayName = DisplayName;
        this.mSize = Size;
        this.mDuration = Duration;
        this.mPath = Path;
        this.mDateAdded = DateAdded;
    }

    protected MediaFiles(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mDisplayName = in.readString();
        mSize = in.readString();
        mDuration = in.readString();
        mPath = in.readString();
        mDateAdded = in.readString();
    }

    public static final Creator<MediaFiles> CREATOR = new Creator<MediaFiles>() {
        @Override
        public MediaFiles createFromParcel(Parcel in) {
            return new MediaFiles(in);
        }

        @Override
        public MediaFiles[] newArray(int size) {
            return new MediaFiles[size];
        }
    };

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getSize() {
        return mSize;
    }

    public String getDuration() {
        return mDuration;
    }

    public String getPath() {
        return mPath;
    }

    public String getDateAdded() {
        return mDateAdded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mDisplayName);
        dest.writeString(mSize);
        dest.writeString(mDuration);
        dest.writeString(mPath);
        dest.writeString(mDateAdded);
    }
}
