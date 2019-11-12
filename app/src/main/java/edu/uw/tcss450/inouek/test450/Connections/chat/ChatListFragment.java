package edu.uw.tcss450.inouek.test450.Connections.chat;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.Connections.chat.ChatListContent.Chat;

public class ChatListFragment extends Fragment
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

		if (view instanceof RecyclerView)
		{
			Context context = view.getContext();
			RecyclerView recyclerView = (RecyclerView) view;
			recyclerView.setLayoutManager(new LinearLayoutManager(context));
			recyclerView.setAdapter(new ChatListRecyclerViewAdapter(ChatListContent.CHATS, this::gotoChat));
		}
		return view;
	}

	private void gotoChat(Chat chat)
	{
		Bundle args = new Bundle();
		args.putSerializable(getString(R.string.chat_bundle_key), chat);
		Navigation.findNavController(getView()).navigate(R.id.action_chatlist_to_chat, args);
	}

	public interface OnListFragmentInteractionListener
	{
		void onListFragmentInteraction(Chat chat);
	}
}
