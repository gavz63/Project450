package edu.uw.tcss450.inouek.test450.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.model.Credentials;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mNicknameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mPasswordConfirmField;
    private String mFirstNameString;
    private String mLastNameString;
    private String mNicknameString;
    private String mEmailString;
    private String mPasswordString;
    private String mPasswordConfirmString;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirstNameField = view.findViewById(R.id.register_first_name);
        mLastNameField = view.findViewById(R.id.register_last_name);
        mNicknameField = view.findViewById(R.id.register_nick_name);
        mEmailField = view.findViewById(R.id.register_email);
        mPasswordField = view.findViewById(R.id.register_pass);
        mPasswordConfirmField = view.findViewById(R.id.register_re_pass);

        Button b = view.findViewById(R.id.button_register_register);
        b.setOnClickListener(this::validateRegistration);
    }

    private void validateRegistration(View view) {
        mFirstNameString = mFirstNameField.getText().toString();
        mLastNameString = mLastNameField.getText().toString();
        mNicknameString = mNicknameField.getText().toString();
        mEmailString = mEmailField.getText().toString();
        mPasswordString = mPasswordField.getText().toString();
        mPasswordConfirmString = mPasswordConfirmField.getText().toString();

        if (!anyErrors()) {
            Bundle bundle = new Bundle();
            Credentials theCredentials = new Credentials.
                    Builder(mEmailString, mPasswordString).build();
            bundle.putSerializable(getString(R.string.credentials_key), theCredentials);

            NavController nc = Navigation.findNavController(view);
            nc.navigate(R.id.action_registerFragment_to_loginFragment, bundle);
        }
    }

    private boolean anyErrors() {
        boolean anyErrors = false;

        if (!mFirstNameString.equals("")) {
            mFirstNameField.setError(null);
        } else {
            mFirstNameField.setError("Please enter your first name");
        }

        if (!mLastNameString.equals("")) {
            mLastNameField.setError(null);
        } else {
            mLastNameField.setError("Please enter your first name");
        }

        if (!mNicknameString.equals("")) {
            mNicknameField.setError(null);
        } else {
            mNicknameField.setError("Please enter your nickname");
        }

        //If email does not contain exactly one '@'
        if (mEmailString.length() - mEmailString.replace("@", "").length() != 1) {
            //If email is empty
            if (mEmailField.equals("")) {
                mEmailField.setError("Email cannot be empty");
            } else {
                mEmailField.setError("Please enter a valid email");
            }
            anyErrors = true;
        } else {
            mEmailField.setError(null);
        }

        if (mPasswordString.length() < 6) {
            if (mPasswordString.equals("")) {
                mPasswordField.setError("Password cannot be empty");
            } else {
                mPasswordField.setError("Your password must be 6 or more characters");
            }
            anyErrors = true;
        } else {
            mPasswordField.setError(null);
        }
        if (!mPasswordString.equals(mPasswordConfirmString)) {
            mPasswordConfirmField.setError("Passwords must match");
            anyErrors = true;
        }

        return anyErrors;
    }
}
