package edu.uw.tcss450.inouek.test450.Connections.chat;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.Connections.chat.ChatListContent.Chat;

public class ChatListFragment extends Fragment
{
	private List<Chat> chats = new ArrayList<Chat>();

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
			chats.clear();
			chats.add(new Chat(1, "Global Chat"));

			Context context = view.getContext();
			RecyclerView recyclerView = (RecyclerView) view;
			recyclerView.setLayoutManager(new LinearLayoutManager(context));
			recyclerView.setAdapter(new ChatListRecyclerViewAdapter(chats, this::gotoChat));
		}
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		ChatListFragmentArgs args = ChatListFragmentArgs.fromBundle(getArguments());
		if(args.getChatMessage() != null)
		{
			gotoChat(args.getChatMessage().getChatId());
		}
		else if(args.getGotoChat() != null)
		{
			gotoChat(args.getGotoChat());
		}
	}

	private void gotoChat(Chat chat)
	{
		gotoChat(chat.getId());
	}
	private void gotoChat(long chatId)
	{
		ChatListFragmentArgs argsToList = ChatListFragmentArgs.fromBundle(getArguments());
		ChatListFragmentDirections.ActionChatlistToChat argsToChat = ChatListFragmentDirections.actionChatlistToChat
		(
			argsToList.getJwt(),
			chatId,
			argsToList.getCredentials().getUsername(),
			argsToList.getCredentials().getColor()
		);
		//args.putSerializable(getString(R.string.chat_bundle_key), chat);
		Navigation.findNavController(getView()).navigate(argsToChat);
	}

	public interface OnListFragmentInteractionListener
	{
		void onListFragmentInteraction(Chat chat);
	}
}
