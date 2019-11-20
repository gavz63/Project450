package edu.uw.tcss450.inouek.test450.Connections.chat;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.uw.tcss450.inouek.test450.Connections.chat.ChatListFragment.OnListFragmentInteractionListener;
import edu.uw.tcss450.inouek.test450.Connections.chat.ChatListContent.Chat;
import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.model.Credentials;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Chat} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.ViewHolder>
{

	private final List<Chat> mValues;
	private final OnListFragmentInteractionListener mListener;

	public ChatListRecyclerViewAdapter(List<Chat> items, OnListFragmentInteractionListener listener)
	{
		mValues = items;
		mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_chat_preview, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position)
	{
		holder.chat = mValues.get(position);
		//holder.iconView.setImageURI(mValues.get(position).getIcon());
		holder.nameView.setText(mValues.get(position).getName());

		holder.thisView.setOnClickListener(v->
		{
			if (null != mListener)
			{
				// Notify the active callbacks interface (the activity, if the
				// fragment is attached to one) that an item has been selected.
				mListener.onListFragmentInteraction(holder.chat);
			}
		});
	}

	@Override
	public int getItemCount()
	{
		return mValues.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder
	{
		public final View thisView;
		public final ImageView iconView;
		public final TextView nameView;
		public Chat chat;

		public ViewHolder(View view)
		{
			super(view);
			thisView = view;
			iconView = view.findViewById(R.id.chat_icon);
			nameView = view.findViewById(R.id.chat_name);
		}

		@Override
		public String toString()
		{
			return super.toString() + " '" + nameView.getText() + "'";
		}
	}
}
