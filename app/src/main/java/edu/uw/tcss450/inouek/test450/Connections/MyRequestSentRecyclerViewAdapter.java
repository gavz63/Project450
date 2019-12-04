package edu.uw.tcss450.inouek.test450.Connections;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uw.tcss450.inouek.test450.Connections.Profile.ProfileContent;
import edu.uw.tcss450.inouek.test450.R;

import java.util.List;


public class MyRequestSentRecyclerViewAdapter extends RecyclerView.Adapter<MyRequestSentRecyclerViewAdapter.ViewHolder> {

    private final List<ProfileContent.Profile> mValues;
    private final RequestSentFragment.OnListFragmentInteractionListener mListener;

    public MyRequestSentRecyclerViewAdapter(List<ProfileContent.Profile> items, RequestSentFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_request_cancel, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).username);
        holder.mContentView.setText(mValues.get(position).name);

        holder.mView.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileContent.CancelRequest(holder.mIdView.getText().toString());
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
