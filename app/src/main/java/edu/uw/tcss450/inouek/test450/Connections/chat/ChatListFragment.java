package edu.uw.tcss450.inouek.test450.Connections.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import edu.uw.tcss450.inouek.test450.model.Credentials;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.Connections.chat.ChatListContent.Chat;
import edu.uw.tcss450.inouek.test450.utils.SendPostAsyncTask;
import me.pushy.sdk.lib.jackson.core.io.JsonEOFException;

public class ChatListFragment extends Fragment
{
	private List<Chat> chats = new ArrayList<>();
	private ChatListRecyclerViewAdapter viewAdapter;
	private MultiAutoCompleteTextView mAutoCompleteTextView;
	private MaterialButton mStartChatButton;
	private Credentials mCredentials;
	private String mJwt;
	private AlertDialog mDialog;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

		ChatListFragmentArgs argsToList = ChatListFragmentArgs.fromBundle(getArguments());

		mCredentials = argsToList.getCredentials();

		mJwt = argsToList.getJwt();

		if (view.findViewById(R.id.list) instanceof RecyclerView)
		{
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
				viewAdapter.notifyDataSetChanged();
			});
			SendPostAsyncTask task = taskBuilder.build();
			task.execute();

			Context context = view.getContext();
			RecyclerView recyclerView = view.findViewById(R.id.list);
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
			long chatId = args.getChatMessage().getChatId();
			getArguments().remove("chatMessage");
			gotoChat(chatId);
		}
		else if(args.getGotoChat() != null)
		{
			long chatId = args.getGotoChat();
			getArguments().remove("gotoChat");
			gotoChat(chatId);
		}

		FloatingActionButton fab = view.findViewById(R.id.chats_floatingActionButton);
		fab.setOnClickListener(this::fabOnClick);
	}

	private void fabOnClick(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.fragment_add_new_chat_dialog, null);
		mAutoCompleteTextView = dialogView.findViewById(R.id.search_for_friends_to_chat_with_autocomplete);
		mStartChatButton = dialogView.findViewById(R.id.button_start_chat);
		mStartChatButton.setOnClickListener(this::startChatOnClick);

		Uri uri = new Uri.Builder()
				.scheme("https")
				.appendPath(getString(R.string.ep_base_url))
				.appendPath(getString(R.string.ep_contacts))
				.appendPath(getString(R.string.ep_contacts_find_current))
				.build();

		JSONObject message = mCredentials.asJSONObject();

		new SendPostAsyncTask.Builder(uri.toString(), message)
				.onPreExecute(() -> mStartChatButton.setEnabled(false))
				.onPostExecute(this::handleGetSuggestionsOnPost)
				.build().execute();
		builder.setView(dialogView);
		mDialog = builder.create();
		mDialog.show();
	}

	private void startChatOnClick(View v) {
		Uri uri = new Uri.Builder()
				.scheme("https")
				.appendPath(getString(R.string.ep_base_url))
				.appendPath(getString(R.string.ep_messaging_base))
				.appendPath(getString(R.string.ep_messaging_chat))
				.build();

		try {
			JSONObject message = new JSONObject();

			String text = mAutoCompleteTextView.getText().toString();
			String[] users = text.split(", ");
			String[] members = new String[users.length + 1];
			for (int i = 0; i < users.length; i++) {
				String[] split = users[i].split(" : ");
				String username = split[split.length - 1];
				members[i] = username;
			}
			members[members.length - 1] = mCredentials.getUsername();
			message.put("members", new JSONArray(members));
			Log.d("JSON", message.toString());
			new SendPostAsyncTask.Builder(uri.toString(), message)
					.onPreExecute(() -> mStartChatButton.setEnabled(false))
					.onPostExecute(this::startChatOnPost)
					.addHeaderField("authorization", mJwt)
					.build().execute();
			mAutoCompleteTextView.setError(null);
		} catch (JSONException e) {
			mAutoCompleteTextView.setError("Cannot start chat at this time");
		}


	}

	private void handleGetSuggestionsOnPost(String result) {
		try {
			JSONObject resultsJSON = new JSONObject(result);

			boolean success = resultsJSON.getBoolean("success");

			if (success) {
				JSONArray newContacts = resultsJSON.getJSONArray("newContacts");

				String[] searchSuggestions = new String[newContacts.length()];

				for (int i = 0; i < newContacts.length(); i++) {
					JSONObject userJSON = newContacts.getJSONObject(i);

					String username = userJSON.getString("username");
					String firstname = userJSON.getString("firstname");
					String lastname = userJSON.getString("lastname");

					searchSuggestions[i] = firstname + " " + lastname
							+ " : " + username;
				}

				ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
						android.R.layout.simple_dropdown_item_1line,
						searchSuggestions);
				mAutoCompleteTextView.setAdapter(adapter);
				mAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
				mAutoCompleteTextView.setError(null);
				mStartChatButton.setEnabled(true);
			} else {
				mAutoCompleteTextView.setError("Cannot get friends at this time");
			}
		} catch (JSONException e) {
			//It appears that the web service did not return a JSON formatted
			//String or it did not have what we expected in it.
			mAutoCompleteTextView.setError("JSONException getting users");
		}
	}

	private void startChatOnPost(String result) {
		try {
			JSONObject resultsJSON = new JSONObject(result);

			boolean success = resultsJSON.getBoolean("success");

			if (success) {
				long chatID = resultsJSON.getLong("chatId");
				mDialog.dismiss();
				gotoChat(chatID);
			}
		} catch (JSONException e ) {
			mAutoCompleteTextView.setError("Cannot go to chat");
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
