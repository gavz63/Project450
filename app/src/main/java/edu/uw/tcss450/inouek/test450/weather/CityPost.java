package edu.uw.tcss450.inouek.test450.weather;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class CityPost implements Serializable, Parcelable {

    private final String mCity;
    private final String mLong;
    private final String mLat;
    protected CityPost(Parcel in) {
        mCity = in.readString();
        mLong = in.readString();
        mLat = in.readString();
    }

    public static final Creator<CityPost> CREATOR = new Creator<CityPost>() {
        @Override
        public CityPost createFromParcel(Parcel in) {
            return new CityPost(in);
        }

        @Override
        public CityPost[] newArray(int size) {
            return new CityPost[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mCity);
        dest.writeString(mLong);
        dest.writeString(mLat);
    }

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mCityName;
        private final String mLat;
        private final String mLong;


        /**
         * Constructs a new Builder.
         *
         * @param theCityName
         * @param theLatitude
         * @param theLongitude
         */
        public Builder(String theCityName, String theLatitude, String theLongitude) {
            this.mLong = theLongitude;
            this.mCityName = theCityName;
            this.mLat = theLatitude;
        }



        public CityPost build() {
            return new CityPost(this);
        }

    }

    private CityPost(final Builder builder) {
        this.mLat = builder.mLat;
        this.mCity = builder.mCityName;
        this.mLong = builder.mLong;
    }

    public String getLong() {
        return mLong;
    }

    public String getCity() {
        return mCity;
    }

    public String getLat() {
        return mLat;
    }



}
