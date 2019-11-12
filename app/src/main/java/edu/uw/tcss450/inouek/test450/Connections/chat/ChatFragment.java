package edu.uw.tcss450.inouek.test450.Connections.chat;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.inouek.test450.R;

public class ChatFragment extends Fragment
{
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

		Bundle args = (savedInstanceState != null) ? savedInstanceState : getArguments();

		RecyclerView recyclerView = view.findViewById(R.id.messages);
		Context context = view.getContext();

		recyclerView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,true));

		recyclerView.setAdapter(new ChatMessageRecyclerViewAdapter(ChatContent.MESSAGES));
	}
}
