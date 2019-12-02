package edu.uw.tcss450.inouek.test450.new_weather;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/**
 * Class to encapsulate a day preview from the weather API.
 *
 *
 * @author Charles Bryan, Gavin Montes
 * @version 11/30/19
 */
public class WeatherPreview implements Serializable, Parcelable {

    private final String mDate;
    private final int mHigh;
    private final int mLow;
    private final String mIcon;

    protected WeatherPreview(Parcel in) {
        mDate = in.readString();
        mHigh = in.readInt();
        mLow = in.readInt();
        mIcon = in.readString();
    }

    public static final Creator<WeatherPreview> CREATOR = new Creator<WeatherPreview>() {
        @Override
        public WeatherPreview createFromParcel(Parcel in) {
            return new WeatherPreview(in);
        }

        @Override
        public WeatherPreview[] newArray(int size) {
            return new WeatherPreview[size];
        }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDate);
        dest.writeInt(mHigh);
        dest.writeInt(mLow);
        dest.writeString(mIcon);
    }

    /**
     * Helper class for building Day Preview.
     *
     * @author Charles Bryan, Gavin Montes
     */
    public static class Builder {
        private final String mDate;
        private int mHigh;
        private int mLow;
        private String mIcon;

        /**
         * Constructs a new Builder.
         *
         * @param theDate the day of the forecast
         */
        public Builder(String theDate) {
            this.mDate = theDate;
        }

        /**
         * Add High temperature.
         */
        public Builder addHigh(final int val) {
            mHigh = val;
            return this;
        }

        /**
         * Add High temperature.
         */
        public Builder addLow(final int val) {
            mLow = val;
            return this;
        }

        /**
         * Add High temperature.
         */
        public Builder addIcon(final String val) {
            mIcon = val;
            return this;
        }

        public WeatherPreview build() {
            return new WeatherPreview(this);
        }
    }

    private WeatherPreview(final Builder builder) {
        this.mDate = builder.mDate;
        this.mIcon = builder.mIcon;
        this.mHigh = builder.mHigh;
        this.mLow = builder.mLow;
    }

    public String getDate() { return mDate; }

    public int getHigh() { return mHigh; }

    public int getLow() { return mLow; }

    public String getIcon() { return mIcon; }

}
