package edu.uw.tcss450.inouek.test450.Connections.chat;

import java.io.Serializable;

public final class ChatMessageNotification implements Serializable
{
	private final String message;
	private final String sender;
	private final long chatId;

	public ChatMessageNotification(String sender, String message, long chatId)
	{
		this.message = message;
		this.sender = sender;
		this.chatId = chatId;
	}

	public String getMessage()
	{
		return message;
	}

	public String getSender()
	{
		return sender;
	}

	public long getChatId(){return chatId;}
}
