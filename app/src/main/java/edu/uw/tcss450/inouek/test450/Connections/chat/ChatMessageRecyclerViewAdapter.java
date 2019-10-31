package edu.uw.tcss450.inouek.test450.Connections.chat;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.uw.tcss450.inouek.test450.Connections.chat.ChatContent.Message;
import edu.uw.tcss450.inouek.test450.R;

import java.util.List;

public class ChatMessageRecyclerViewAdapter extends RecyclerView.Adapter<ChatMessageRecyclerViewAdapter.ViewHolder>
{

	private final List<Message> mValues;

	public ChatMessageRecyclerViewAdapter(List<Message> items)
	{
		mValues = items;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_message, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position)
	{
		holder.item = mValues.get(position);
		//holder.usericon.setImageURI(mValues.get(position).getUserIcon());
		holder.username.setText(mValues.get(position).getUserName());
		holder.timesent.setText(mValues.get(position).getTimeSent());
		holder.contents.setText(mValues.get(position).getContents());
	}

	@Override
	public int getItemCount()
	{
		return mValues.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder
	{
		public Message item;

		public final View view;
		public final ImageView usericon;
		public final TextView username;
		public final TextView timesent;
		public final TextView contents;

		public ViewHolder(View view)
		{
			super(view);
			this.view = view;
			usericon = view.findViewById(R.id.message_usericon);
			username = view.findViewById(R.id.message_username);
			timesent = view.findViewById(R.id.message_timesent);
			contents = view.findViewById(R.id.message_contents);
		}

		@Override
		public String toString()
		{
			return super.toString() + " '" + contents.getText() + "'";
		}
	}
}
