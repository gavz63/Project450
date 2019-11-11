package edu.uw.tcss450.inouek.test450;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import edu.uw.tcss450.inouek.test450.model.Credentials;

public class UserFragment extends Fragment {

    Credentials mCredentials;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserFragmentArgs args = UserFragmentArgs.fromBundle(getArguments());
        mCredentials = args.getCredentials();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_account_log_out).setOnClickListener(v -> {
            DialogFragment dialogFragment = new LogoutConfirmDialog((HomeActivity)getActivity());
            dialogFragment.show(getFragmentManager(), "alert");
        });

        view.findViewById(R.id.button_account_update_info).setOnClickListener(v -> {
            HomeActivity h = (HomeActivity) getActivity();

            EditText email = view.findViewById(R.id.account_email);
            EditText pass = view.findViewById(R.id.account_new_password);
            EditText first = view.findViewById(R.id.account_first_name);
            EditText last = view.findViewById(R.id.account_last_name);
            EditText username = view.findViewById(R.id.account_nickname);

            Credentials newCreds =
                    new Credentials.Builder(email.getText().toString(), pass.getText().toString())
                            .addFirstName(first.getText().toString())
                            .addLastName(last.getText().toString())
                            .addUsername(username.getText().toString())
                            .build();
            DialogFragment dialogFragment =
                    new AccountUpdateConfirmDialog(h, mCredentials.getEmail(), newCreds);
            dialogFragment.show(getFragmentManager(), "alert");
        });
    }
}
