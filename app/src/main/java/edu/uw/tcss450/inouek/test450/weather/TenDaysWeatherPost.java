package edu.uw.tcss450.inouek.test450.weather;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class TenDaysWeatherPost implements Serializable, Parcelable {

    private final String mIcon;
    private final String mTemp;
    private final String mDate;
    protected TenDaysWeatherPost(Parcel in) {
        mIcon = in.readString();
        mTemp = in.readString();
        mDate = in.readString();
    }

    public static final Creator<TenDaysWeatherPost> CREATOR = new Creator<TenDaysWeatherPost>() {
        @Override
        public TenDaysWeatherPost createFromParcel(Parcel in) {
            return new TenDaysWeatherPost(in);
        }

        @Override
        public TenDaysWeatherPost[] newArray(int size) {
            return new TenDaysWeatherPost[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mIcon);
        dest.writeString(mTemp);
        dest.writeString(mDate);
    }

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mIcon;
        private final String mTemp;
        private final String mDate;


        /**
         * Constructs a new Builder.
         *
         * @param icon
         * @param date
         * @param temp
         */
        public Builder(String icon, String date, String temp) {
            this.mTemp = temp;
            this.mIcon = icon;
            this.mDate = date;
        }



        public TenDaysWeatherPost build() {
            return new TenDaysWeatherPost(this);
        }

    }

    private TenDaysWeatherPost(final Builder builder) {
        this.mTemp = builder.mTemp;
        this.mIcon = builder.mIcon;
        this.mDate = builder.mDate;
    }

    public String getTemp() {
        return mTemp;
    }

    public String getIcon() {
        return mIcon;
    }

    public String getDate() {
        return mDate;
    }



}
