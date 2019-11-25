package edu.uw.tcss450.inouek.test450;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import edu.uw.tcss450.inouek.test450.model.Credentials;

public class UserFragment extends Fragment {

    private Credentials mCredentials;
    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mUsernameField;
    private EditText mCurrentPassField;
    private EditText mNewPassField;
    private EditText mNewPassRetypeField;
    private ImageView mAvatar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCredentials = UserFragmentArgs.fromBundle(getArguments()).getCredentials();
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

        mFirstNameField = view.findViewById(R.id.account_first_name);
        mLastNameField = view.findViewById(R.id.account_last_name);
        mUsernameField = view.findViewById(R.id.account_nickname);
        mCurrentPassField = view.findViewById(R.id.account_current_password);
        mNewPassField = view.findViewById(R.id.account_new_password);
        mNewPassRetypeField = view.findViewById(R.id.account_new_password_retype);
        mAvatar = view.findViewById(R.id.account_avatar);

        mFirstNameField.setText(mCredentials.getFirstName());
        mLastNameField.setText(mCredentials.getLastName());
        mUsernameField.setText(mCredentials.getUsername());
        setAvatar(mCredentials.getColor());
        mAvatar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.monkey_icon_list, null);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            ArrayList<ImageView> avatars = new ArrayList<>();
            avatars.add(dialogView.findViewById(R.id.monkey_blue));
            avatars.add(dialogView.findViewById(R.id.monkey_green));
            avatars.add(dialogView.findViewById(R.id.monkey_pink));
            avatars.add(dialogView.findViewById(R.id.monkey_red));
            avatars.add(dialogView.findViewById(R.id.monkey_yellow));

            for (ImageView avatar : avatars) {
                avatar.setOnClickListener(a -> {
                    this.setAvatar(Integer.parseInt((String) avatar.getTag()));
                    dialog.cancel();
                });
            }
            dialog.show();
        });

        view.findViewById(R.id.button_account_log_out).setOnClickListener(v -> {
            DialogFragment dialogFragment = new LogoutConfirmDialog((HomeActivity)getActivity());
            dialogFragment.show(getFragmentManager(), "alert");
        });

        view.findViewById(R.id.button_account_update_info).setOnClickListener(v -> {

            if (!anyErrors()) {
                Credentials newCreds = null;
                //If they left new password blank they didn't want to change it
                if (mNewPassField.getText().toString().equals("")) {
                    newCreds = new Credentials
                                    .Builder(mCredentials.getEmail(), mCredentials.getPassword())
                                    .addFirstName(mFirstNameField.getText().toString())
                                    .addLastName(mLastNameField.getText().toString())
                                    .addUsername(mUsernameField.getText().toString())
                                    .addColor((Integer) mAvatar.getTag())
                                    .build();
                } else {
                    newCreds = new Credentials
                                    .Builder(mCredentials.getEmail(), mNewPassField.getText().toString())
                                    .addFirstName(mFirstNameField.getText().toString())
                                    .addLastName(mLastNameField.getText().toString())
                                    .addUsername(mUsernameField.getText().toString())
                                    .addColor((Integer) mAvatar.getTag())
                                    .build();
                }

                DialogFragment dialogFragment =
                        new AccountUpdateConfirmDialog((HomeActivity) getActivity(), newCreds, this);
                dialogFragment.show(getFragmentManager(), "alert");
            }
        });
    }

    private boolean anyErrors() {
        boolean anyErrors = false;

        if (!mFirstNameField.getText().toString().equals("")) {
            mFirstNameField.setError(null);
        } else {
            mFirstNameField.setError("First Name cannot be empty");
            anyErrors = true;
        }

        if (!mLastNameField.getText().toString().equals("")) {
            mLastNameField.setError(null);
        } else {
            mLastNameField.setError("Last Name cannot be empty");
            anyErrors = true;
        }

        if (!mUsernameField.getText().toString().equals("")) {
            mUsernameField.setError(null);
        } else {
            mUsernameField.setError("Nickname cannot be empty");
            anyErrors = true;
        }

        if (!mNewPassField.getText().toString().equals(mNewPassRetypeField.getText().toString())) {
            mNewPassField.setError("Passwords do not match");
            mNewPassRetypeField.setError("Passwords do not match");
            anyErrors = true;
        } else {
            mNewPassField.setError(null);
            mNewPassRetypeField.setError(null);
        }

        if (!mNewPassField.getText().toString().equals("") &&
                !mNewPassRetypeField.getText().toString().equals("")) {
            if (mNewPassField.getText().toString().length() < 6) {
                mNewPassField.setError("Your password must be 6 or more characters");
                anyErrors = true;
            } else if (!mNewPassField.getText().toString().matches("(.)*([A-Z])(.)*")) {
                mNewPassField.setError("Password must contain at least one capital letter");
                anyErrors = true;
            } else if (!mNewPassField.getText().toString().matches("(.)*(\\d)(.)*")) {
                mNewPassField.setError("Password must contain at least one number");
                anyErrors = true;
            } else {
                mNewPassField.setError(null);
            }
        }

        if(!mCurrentPassField.getText().toString().equals(mCredentials.getPassword())) {
            mCurrentPassField.setError("Password is incorrect");
            anyErrors = true;
        } else {
            mCurrentPassField.setError(null);
        }

        return anyErrors;
    }

    void setCredentials(Credentials c) {
        mCredentials = c;
    }

    void setAvatar(int theColor) {
        switch (theColor) {
            case HomeActivity.MONKEY_YELLOW:
                mAvatar.setImageResource(R.drawable.ic_monkey_yellow);
                mAvatar.setTag(HomeActivity.MONKEY_YELLOW);
                break;
            case HomeActivity.MONKEY_BLUE:
                mAvatar.setImageResource(R.drawable.ic_monkey_blue);
                mAvatar.setTag(HomeActivity.MONKEY_BLUE);
                break;
            case HomeActivity.MONKEY_RED:
                mAvatar.setImageResource(R.drawable.ic_monkey_red);
                mAvatar.setTag(HomeActivity.MONKEY_RED);
                break;
            case HomeActivity.MONKEY_GREEN:
                mAvatar.setImageResource(R.drawable.ic_monkey_green);
                mAvatar.setTag(HomeActivity.MONKEY_GREEN);
                break;
            case HomeActivity.MONKEY_PINK:
                mAvatar.setImageResource(R.drawable.ic_monkey_pink);
                mAvatar.setTag(HomeActivity.MONKEY_PINK);
                break;
            default:
                break;
        }
    }
}
