package edu.uw.tcss450.inouek.test450.Connections.chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ChatContent
{

	/**
	 * An array of sample (dummy) items.
	 */
	public static final List<Message> ITEMS = new ArrayList<Message>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static final Map<Long, Message> ITEM_MAP = new HashMap<Long, Message>();

	private static final int COUNT = 25;

	static
	{
		// Add some sample items.
		for (int i = 1; i <= COUNT; i++)
		{
			addMessage(createDummyMessage(i));
		}
	}

	private static void addMessage(Message item)
	{
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	private static Message createDummyMessage(int position)
	{
		return new Message(position, position%5, 0, makeDetails(position));
	}

	private static String makeDetails(int position)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Details about Item: ").append(position);
		for (int i = 0; i < position; i++)
		{
			builder.append("\nMore details information here.");
		}
		return builder.toString();
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class Message implements Serializable
	{
		private final long id;
		private final long userId;
		private final long timestamp;
		private final String contents;

		public Message(long id, long userId, long timestamp, String contents)
		{
			this.id = id;
			this.userId = userId;
			this.timestamp = timestamp;
			this.contents = contents;
		}

		public String getUserIcon(){return "";}
		public String getUserName(){return "Person "+userId;}
		public String getTimeSent(){return "12:00AM February 31st 2019";}
		public String getContents(){return contents;}
	}
}
