package edu.uw.tcss450.inouek.test450.Connections;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uw.tcss450.inouek.test450.Connections.Profile.ProfileContent;
import edu.uw.tcss450.inouek.test450.Connections.ProfileFragment.OnListFragmentInteractionListener;
import edu.uw.tcss450.inouek.test450.R;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Profile} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyProfileRecyclerViewAdapter extends RecyclerView.Adapter<MyProfileRecyclerViewAdapter.ViewHolder> {

    private final List<ProfileContent.Profile> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyProfileRecyclerViewAdapter(List<ProfileContent.Profile> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_profile, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).username);
        holder.mContentView.setText(mValues.get(position).name);

        holder.mView.findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileContent.DeleteConnections(holder.mItem.username);
                Log.e("DeletionError", holder.mItem.username);
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
        public ProfileContent.Profile mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
