package edu.uw.tcss450.inouek.test450.Connections.chat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.Connections.chat.ChatListContent.Chat;
import edu.uw.tcss450.inouek.test450.utils.SendPostAsyncTask;

public class ChatListFragment extends Fragment
{
	private List<Chat> chats = new ArrayList<Chat>();
	private ChatListRecyclerViewAdapter viewAdapter;

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
			ChatListFragmentArgs argsToList = ChatListFragmentArgs.fromBundle(getArguments());

			JSONObject jsonMsg = new JSONObject();
			try
			{
				jsonMsg.put("username", argsToList.getCredentials().getUsername());
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			Uri.Builder uriBuilder = new Uri.Builder();
			uriBuilder.scheme("https");
			uriBuilder.appendPath(getString(R.string.ep_base_url));
			uriBuilder.appendPath(getString(R.string.ep_messaging_base));
			uriBuilder.appendPath(getString(R.string.ep_messaging_chatlist));
			String messageUrl = uriBuilder.toString();

			SendPostAsyncTask.Builder taskBuilder = new SendPostAsyncTask.Builder(messageUrl, jsonMsg);
			taskBuilder.onCancelled(error -> Log.e("CHATLIST_FRAG", error));
			taskBuilder.addHeaderField("authorization", argsToList.getJwt());
			taskBuilder.onPostExecute(str->
			{
				chats.clear();
				try
				{
					JSONObject json = new JSONObject(str);
					JSONArray messageArray = json.getJSONArray("chats");
					for(int i=0; i<messageArray.length(); i++)
					{
						JSONObject chat = messageArray.getJSONObject(i);
						chats.add(new Chat(chat.getInt("chatid"),chat.getString("name")));
					}
				}
				catch(JSONException e)
				{
					e.printStackTrace();
				}
				if(chats.size() == 0)
				{
					chats.add(new Chat(1, "Global Chat"));
				}
				viewAdapter.notifyDataSetChanged();
			});
			SendPostAsyncTask task = taskBuilder.build();
			task.execute();

			Context context = view.getContext();
			RecyclerView recyclerView = (RecyclerView) view;
			recyclerView.setLayoutManager(new LinearLayoutManager(context));
			viewAdapter = new ChatListRecyclerViewAdapter(chats, this::gotoChat);
			recyclerView.setAdapter(viewAdapter);
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
