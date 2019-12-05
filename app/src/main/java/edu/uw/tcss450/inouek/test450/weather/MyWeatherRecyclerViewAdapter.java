package edu.uw.tcss450.inouek.test450.weather;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.weather.Weather10Fragment.OnListFragmentInteractionListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link TenDaysWeatherPost} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyWeatherRecyclerViewAdapter extends RecyclerView.Adapter<MyWeatherRecyclerViewAdapter.ViewHolder> {

    private final List<TenDaysWeatherPost> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyWeatherRecyclerViewAdapter(List<TenDaysWeatherPost> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_weather_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        String icon = mValues.get(position).getIcon();

        if(icon != null){
            switch(icon) {
                case "01d":
                    holder.mIcon.setImageResource(R.drawable.ic_01d);
                    break;
                case "01n":
                    holder.mIcon.setImageResource(R.drawable.ic_01n);
                    break;
                case "02d":
                    holder.mIcon.setImageResource(R.drawable.ic_02d);
                    break;
                case "02n":
                    holder.mIcon.setImageResource(R.drawable.ic_02n);
                    break;
                case "03d":
                    holder.mIcon.setImageResource(R.drawable.ic_03d);
                    break;
                case "03n":
                    holder.mIcon.setImageResource(R.drawable.ic_03d);
                    break;
                case "04d":
                    holder.mIcon.setImageResource(R.drawable.ic_04d);
                    break;
                case "04n":
                    holder.mIcon.setImageResource(R.drawable.ic_04d);
                    break;
                case "09d":
                    holder.mIcon.setImageResource(R.drawable.ic_09d);
                    break;
                case "09n":
                    holder.mIcon.setImageResource(R.drawable.ic_09d);
                    break;
                case "10d":
                    holder.mIcon.setImageResource(R.drawable.ic_10d);
                    break;
                case "10n":
                    holder.mIcon.setImageResource(R.drawable.ic_10n);
                    break;
                case "11d":
                    holder.mIcon.setImageResource(R.drawable.ic_11d);
                    break;
                case "11n":
                    holder.mIcon.setImageResource(R.drawable.ic_11d);
                    break;
                case "13d":
                    holder.mIcon.setImageResource(R.drawable.ic_13d);
                    break;
                case "13n":
                    holder.mIcon.setImageResource(R.drawable.ic_13d);
                    break;
                case "50d":
                    holder.mIcon.setImageResource(R.drawable.ic_50d);
                    break;
                case "50n":
                    holder.mIcon.setImageResource(R.drawable.ic_50d);
                    break;
                default:
                    break;
            }
        }



        holder.mDate.setText(mValues.get(position).getDate());
        holder.mTemp.setText(mValues.get(position).getTemp());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }

                Weather10Fragment.position = position;
            }
        });
    }

    public void swap(ArrayList<TenDaysWeatherPost> data){
        mValues.clear();
        mValues.addAll(data);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mIcon;
        public final TextView mDate;
        public final TextView mTemp;
        public TenDaysWeatherPost mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIcon = (ImageView) view.findViewById(R.id.weather_icon);
            mDate = (TextView) view.findViewById(R.id.Date);
            mTemp = (TextView) view.findViewById(R.id.Temp);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
