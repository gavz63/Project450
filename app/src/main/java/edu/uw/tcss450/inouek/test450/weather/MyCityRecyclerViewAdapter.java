package edu.uw.tcss450.inouek.test450.weather;

import androidx.recyclerview.widget.RecyclerView;

import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.SelectLocationViewModel;
import edu.uw.tcss450.inouek.test450.utils.GetAsyncTask;
import edu.uw.tcss450.inouek.test450.weather.CityFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static edu.uw.tcss450.inouek.test450.HomeActivity.KelvinToFahrenheit;

/**
 * {@link RecyclerView.Adapter} that can display a {@link CityPost} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyCityRecyclerViewAdapter extends RecyclerView.Adapter<MyCityRecyclerViewAdapter.ViewHolder> {

    private final List<CityPost> mValues;
    private final OnListFragmentInteractionListener mListener;
    public static Uri.Builder uri;
    private String mJwToken;

    public MyCityRecyclerViewAdapter(List<CityPost> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_city, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getCity());
        holder.mContentView.setText(String.format("Lat: %s, Long: %s",
                mValues.get(position).getLat(), mValues.get(position).getLong()));
        JwTokenModel jwTokenModel = JwTokenModel.getFactory().create(JwTokenModel.class);
        mJwToken = jwTokenModel.getJwToken().toString();
        holder.mView.findViewById(R.id.city_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = holder.mIdView.getText().toString();
                String lat = mValues.get(position).getLat();
                String lon =  mValues.get(position).getLong();
                SelectLocationViewModel selectLocationViewModel = SelectLocationViewModel.getFactory().create(SelectLocationViewModel.class);
                Location location = new Location("dummyprovider");
                location.setLatitude(Double.valueOf(lat));
                location.setLongitude(Double.valueOf(lon));
                selectLocationViewModel.changeLocation(location);
                Log.e("error","City Name: " + cityName + " / " + "Lat: " + lat + "/ Lon: " + lon);
                Uri tenDayWeatherUri = uri.clearQuery().appendQueryParameter("lat", lat)
                        .appendQueryParameter("lon", lon)
                        .build();


                Log.e("weather uri",tenDayWeatherUri.toString());


                new GetAsyncTask.Builder(tenDayWeatherUri.toString())
                        .onPostExecute(s -> {

                                try {
                                    Log.e("weather data", s);
                                    JSONArray weatherArray = new JSONArray(s);

                                    TenDaysWeatherPost[] weather = new TenDaysWeatherPost[weatherArray.length()];
                                    //get 10 days weather info
                                    for (int i = 0; i < weatherArray.length(); i++) {


                                        JSONObject day = weatherArray.getJSONObject(i);

                                        long time = Integer.valueOf(day.getString("date")).intValue();
                                        Calendar currCal = Calendar.getInstance();
                                        Date dateObject = new Date(time * 1000);
                                        currCal.setTime(dateObject);
                                        //Date currCalDate = new Date(time);
                                        String iconID = day.getString("iconId");
                                        //System.out.println(iconID);

                                        String[] week_name = {"Sun", "Mon", "Tue", "Wed",
                                                "Thur", "Fri", "Sat"};
                                        String temp_min = day.getString("minTemp");
                                        temp_min = String.format("%.2f", KelvinToFahrenheit(Float.parseFloat(temp_min)));
                                        String temp_max = day.getString("maxTemp");
                                        temp_max = String.format("%.2f", KelvinToFahrenheit(Float.parseFloat(temp_max)));

                                        int date = currCal.get(Calendar.DAY_OF_MONTH);
                                        int month = currCal.get(Calendar.MONTH) + 1;
                                        weather[i] = (new TenDaysWeatherPost.Builder(iconID,
                                                "" + month + " / " + date + " / "
                                                        + week_name[currCal.get(Calendar.DAY_OF_WEEK)-1],
                                                "High: " + temp_max + "°F\n"
                                                        + "Low: " + temp_min + "°F")
                                                .build());
                                    }
                                    ArrayList<TenDaysWeatherPost> weathers = new ArrayList(Arrays.asList(weather));
                                    Log.e("Weather Change: ", weathers.toString());


                                    TenDaysWeatherModel viewModel = TenDaysWeatherModel.getFactory().create(TenDaysWeatherModel.class);
                                    viewModel.changeData(weathers);

                                    Log.e("error","end update");
                                }catch(JSONException e){
                                    Log.e("Weather is Not Changing", "Error");
                                    e.printStackTrace();
                                }

                            })
                        .addHeaderField("authorization", mJwToken) //add the JWT as a header
                        .build().execute();
                Log.e("error","clicked");
            }


        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public CityPost mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mIdView.getText() + "'";
        }
    }

    public void swap(ArrayList<CityPost> data){
        mValues.clear();
        mValues.addAll(data);
        notifyDataSetChanged();
    }


}
