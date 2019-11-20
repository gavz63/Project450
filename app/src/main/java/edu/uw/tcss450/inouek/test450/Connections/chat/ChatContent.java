package edu.uw.tcss450.inouek.test450.Connections.chat;

import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.tcss450.inouek.test450.HomeActivity;
import edu.uw.tcss450.inouek.test450.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ChatContent
{
	public static final List<Message> MESSAGES = new ArrayList<Message>();

	public static final Map<Long, Message> ITEM_MAP = new HashMap<Long, Message>();

	//private static final int COUNT = 25;

	//static
	//{
	//	// Add some sample items.
	//	for (int i = 1; i <= COUNT; i++)
	//	{
	//		addMessage(createDummyMessage(i));
	//	}
	//}

//	private static void addMessage(Message item)
//	{
//		MESSAGES.add(item);
//		ITEM_MAP.put(item.messageId, item);
//	}
//
//	private static Message createDummyMessage(int position)
//	{
//		return new Message(position, position%5, System.currentTimeMillis()/1000, makeDetails(position));
//	}

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
		private final long messageId;
		private final long userId;
		private final int iconId;
		private final String username;
		private final Timestamp timestamp;
		private final String contents;

		public Message(long messageId, long userId, long timestamp, String contents)
		{
			this.messageId = messageId;
			this.userId = userId;
			this.username = "Person "+userId;
			this.timestamp = new Timestamp(timestamp);
			this.contents = contents;
			this.iconId = colorToIconId(0);
		}

		public Message(String username, String contents, int color)
		{
			this.messageId = 0;
			this.userId = 0;
			this.username = username;
			this.timestamp = new Timestamp(System.currentTimeMillis()/1000);
			this.contents = contents;
			this.iconId = colorToIconId(color);
		}

		public Message(JSONObject json)
		{
			this.messageId = json.optLong("messageId");
			this.userId = json.optLong("userId");
			this.username = json.optString("username");
			this.timestamp = Timestamp.valueOf(json.optString("timestamp"));
			this.contents = json.optString("message");
			this.iconId = colorToIconId(json.optInt("color"));
		}

		public int getUserIcon(){return iconId;}
		public String getUserName(){return username;}
		public String getTimeSent(){return timestamp.toString();}//{return "12:00AM February 31st 2019";}
		public String getContents(){return contents;}

		private static int colorToIconId(int colorId)
		{
			switch(colorId)
			{
				case HomeActivity.MONKEY_GREEN:
					return R.drawable.ic_monkey_green;
				case HomeActivity.MONKEY_BLUE:
					return R.drawable.ic_monkey_blue;
				case HomeActivity.MONKEY_YELLOW:
					return R.drawable.ic_monkey_yellow;
				case HomeActivity.MONKEY_PINK:
					return R.drawable.ic_monkey_pink;
				default:
					return R.drawable.ic_monkey_red;
			}
		}
	}
}
