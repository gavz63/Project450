package edu.uw.tcss450.inouek.test450;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.uw.tcss450.inouek.test450.Connections.MyProfileRecyclerViewAdapter;
import edu.uw.tcss450.inouek.test450.weather.CityFragment;

public class Adapter24Hour extends RecyclerView.Adapter<Adapter24Hour.ViewHolder> {
    // for debug
    private static final String TAG = "Adapter24Hour";
    private Context myContext; //
    private ArrayList<String[]> mWeatherInfo;

    public Adapter24Hour (Context Context, ArrayList<String[]> weatherInfo){
        myContext = Context;
        mWeatherInfo = weatherInfo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflating each individual view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_24_hours_weather_list, parent, false);
        // put individual view to view holder and return the viewholder (a list of individual view)
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // put the view holder to the position they should be put into
        // this funciton will be called when each new item was put into the list
        Log.d(TAG, "On Bind View Holder : called. ");

        holder.icon.setText("Description: " + mWeatherInfo.get(position)[0] + "\n" + " temperature: " + mWeatherInfo.get(position)[1] + "\n");
    }

    @Override
    //tell the adaptor, how many list in your adapter, if it is 0, the recycler view will be blank
    // display how many messages from mWeatherINFO
    public int getItemCount() {
        return mWeatherInfo.size() ;
    }

    // View holder class expressing each individual layout
    // and it should have text to display weather info and has its own layout
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView icon;

        //RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = (TextView)itemView.findViewById(R.id.textView);
            //parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

}
