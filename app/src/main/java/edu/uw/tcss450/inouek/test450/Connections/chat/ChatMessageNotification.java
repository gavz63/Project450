package edu.uw.tcss450.inouek.test450.Connections.chat;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.function.Consumer;

public final class ChatMessageNotification implements Serializable
{
	private final String message;
	private final String username;
	private final long chatId;

	private ChatMessageNotification(String message, String username, long chatId)
	{
		this.message = message;
		this.username = username;
		this.chatId = chatId;
	}

	/**
	 * Checks if args contains a ChatMessageNotification nd tries to parse it. If successful, output the new instance into the consumer.
	 * This is based on the tester-doer pattern.
	 * Returns true if it successfully parsed args otherwise false.
	 */
	public static boolean TryParse(@NonNull Bundle args, Consumer<ChatMessageNotification> outputConsumer)
	{
		if(!args.getString("type","").equals("msg")){return false;}

		String jsonSender = args.getString("sender");
		if(jsonSender == null){return false;}

		try
		{
			JSONObject sender = new JSONObject(jsonSender);
			ChatMessageNotification chat = new ChatMessageNotification
			(
				args.getString("message"),
				sender.getString("username"),
				sender.getLong("chatId")
			);
			outputConsumer.accept(chat);
			return true;
		}
		catch (JSONException e)
		{
			Log.e("CHAT_MSG_NOTIF", "Bad chat message notification", e);
			return false;
		}
	}

	public String getMessage(){return message;}
	public String getUsername(){return username;}
	public long   getChatId(){return chatId;}
}
