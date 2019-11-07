package edu.uw.tcss450.inouek.test450.Connections.chat;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatListContent
{
	public static final List<Chat> CHATS = new ArrayList<Chat>();
	public static final Map<Long, Chat> CHAT_MAP = new HashMap<Long, Chat>();

	private static final int COUNT = 25;

	static
	{
		// Add some sample items.
		for (int i = 1; i <= COUNT; i++)
		{
			addItem(createChat(i));
		}
	}

	private static void addItem(Chat item)
	{
		CHATS.add(item);
		CHAT_MAP.put(item.id, item);
	}

	private static Chat createChat(int position)
	{
		return new Chat(position, "Chat " + position);
	}

	public static class Chat implements Serializable
	{
		private final long id;
		private final String name;

		public Chat(long id, String name)
		{
			this.id = id;
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}

		public Uri getIcon(){return Uri.EMPTY;}
		public String getName(){return name;}
	}
}
