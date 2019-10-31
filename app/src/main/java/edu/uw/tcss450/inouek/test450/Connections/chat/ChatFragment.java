package edu.uw.tcss450.inouek.test450.Connections.chat;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.inouek.test450.R;

public class ChatFragment extends Fragment
{
	// TODO: Customize parameters
	private int mColumnCount = 1;

	public ChatFragment(){}

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

		RecyclerView recyclerView = view.findViewById(R.id.messages);
		Context context = view.getContext();
		if (mColumnCount <= 1)
		{
			recyclerView.setLayoutManager(new LinearLayoutManager(context));
		}
		else
		{
			recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
		}
		recyclerView.setAdapter(new ChatMessageRecyclerViewAdapter(ChatContent.ITEMS));
	}
}
