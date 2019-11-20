package edu.uw.tcss450.inouek.test450;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.uw.tcss450.inouek.test450.Connections.MyProfileRecyclerViewAdapter;

public class Adapter24Hour extends RecyclerView.Adapter<Adapter24Hour.ViewHolder> {
    // for debug
    private static final String TAG = "Adapter24Hour";
    private Context myContext; //
    private ArrayList<String> mWeatherInfo;

    public Adapter24Hour (Context Context, ArrayList<String> weatherInfo){
        myContext = Context;
        mWeatherInfo = weatherInfo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflating each individual view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display24hours, parent, false);
        // put individual view to view holder and return the viewholder (a list of individual view)
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // put the view holder to the position they should be put into
        // this funciton will be called when each new item was put into the list
        Log.d(TAG, "On Bind View Holder : called. ");

        holder.description.setText(mWeatherInfo.get(position));
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

        TextView description;
        LinearLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.display_24_forecast);
            parentLayout = itemView.findViewById(R.id.predict24);
        }
    }

}
