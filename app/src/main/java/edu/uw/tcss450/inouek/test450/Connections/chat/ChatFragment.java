package edu.uw.tcss450.inouek.test450.Connections.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.utils.PushReceiver;
import edu.uw.tcss450.inouek.test450.utils.SendPostAsyncTask;

//this is only a test
public class ChatFragment extends Fragment
{
	private long chatId,userId;
	private int color;
	private String username,jwt;

	private List<ChatContent.Message> messageOutput = new ArrayList<>();
	private ChatMessageRecyclerViewAdapter viewAdapter;

	private EditText inputText;
	private String sendUrl;

	private PushMessageReceiver mPushMessageReciever;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_chat, container, false);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		inputText = view.findViewById(R.id.chat_input_text);
		view.findViewById(R.id.chat_input_send).setOnClickListener(this::handleSendClick);

		RecyclerView recyclerView = view.findViewById(R.id.messages);
		Context context = view.getContext();

		recyclerView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,true));
		viewAdapter = new ChatMessageRecyclerViewAdapter(messageOutput);
		recyclerView.setAdapter(viewAdapter);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		ChatFragmentArgs args = ChatFragmentArgs.fromBundle(getArguments());
		chatId = args.getChatId();
		userId = args.getUserId();
		username = args.getUsername();
		jwt = args.getJwt();
		color = args.getColor();
		//We will use this url every time the user hits send. Let's only build it once, ya?
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.scheme("https");
		uriBuilder.appendPath(getString(R.string.ep_base_url));
		uriBuilder.appendPath(getString(R.string.ep_messaging_base));
		uriBuilder.appendPath(getString(R.string.ep_messaging_send));
		sendUrl = uriBuilder.toString();

		refreshMessages();
	}

	private void refreshMessages()
	{
		JSONObject jsonMsg = new JSONObject();
		try
		{
			jsonMsg.put("chatId", chatId);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.scheme("https");
		uriBuilder.appendPath(getString(R.string.ep_base_url));
		uriBuilder.appendPath(getString(R.string.ep_messaging_base));
		uriBuilder.appendPath(getString(R.string.ep_messaging_getall));
		String messageUrl = uriBuilder.toString();

		SendPostAsyncTask.Builder taskBuilder = new SendPostAsyncTask.Builder(messageUrl, jsonMsg);
		taskBuilder.onCancelled(error -> Log.e("CHAT_FRAG", error));
		taskBuilder.addHeaderField("authorization", jwt);
		taskBuilder.onPostExecute(str->
		{
			messageOutput.clear();
			try
			{
				JSONObject json = new JSONObject(str);
				JSONArray messageArray = json.getJSONArray("messages");
				for(int i=0; i<messageArray.length(); i++)
				{
					messageOutput.add(new ChatContent.Message(messageArray.getJSONObject(i)));
				}
				viewAdapter.notifyDataSetChanged();
			}
			catch(JSONException e)
			{
				e.printStackTrace();
			}
		});
		SendPostAsyncTask task = taskBuilder.build();
		task.execute();
	}

	private void handleSendClick(final View theButton)
	{
		String msg = inputText.getText().toString();
		JSONObject messageJson = new JSONObject();
		try
		{
			messageJson.put("username", username);
			messageJson.put("message", msg);
			messageJson.put("chatId", chatId);
			messageJson.put("color", color);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		SendPostAsyncTask.Builder taskBuilder = new SendPostAsyncTask.Builder(sendUrl, messageJson);
		taskBuilder.onPostExecute(this::endOfSendMsgTask);
		taskBuilder.onCancelled(error -> Log.e("CHAT_FRAG", error));
		taskBuilder.addHeaderField("authorization", jwt);
		SendPostAsyncTask task = taskBuilder.build();
		task.execute();
	}
	private void endOfSendMsgTask(final String result)
	{
		try
		{
			//This is the result from the web service
			JSONObject res = new JSONObject(result);
			if(res.has("success") && res.getBoolean("success"))
			{
				//The web service got our message. Time to clear out the input EditText
				inputText.setText("");
				//its up to you to decide if you want to send the message to the output here
				//or wait for the message to come back from the web service.
			}
			refreshMessages();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (mPushMessageReciever == null)
		{
			mPushMessageReciever = new PushMessageReceiver();
		}
		IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
		getActivity().registerReceiver(mPushMessageReciever, iFilter);
	}
	@Override
	public void onPause()
	{
		super.onPause();
		if (mPushMessageReciever != null)
		{
			getActivity().unregisterReceiver(mPushMessageReciever);
		}
	}


	/**
	 * A BroadcastReceiver that listens for messages sent from PushReceiver
	 */
	private class PushMessageReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.e("PUSHYRECEIVER", "PLEASE SEE THIS");
			if(intent.hasExtra("SENDER") && intent.hasExtra("MESSAGE")) {
				if(intent.getStringExtra("TYPE").compareTo("msg") == 0) {
					String sender = intent.getStringExtra("SENDER");
					String username = "";
					int color = 0;
					try {
						JSONObject senderJSON = new JSONObject(sender);
						username = senderJSON.getString("username");
						color = senderJSON.getInt("color");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					String messageText = intent.getStringExtra("MESSAGE");
					messageOutput.add(0, new ChatContent.Message(username, messageText, color));
					viewAdapter.notifyDataSetChanged();
				}
			}
		}
	}
}
