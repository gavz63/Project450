package edu.uw.tcss450.inouek.test450.login;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import org.json.JSONException;
import org.json.JSONObject;
import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.model.Credentials;
import edu.uw.tcss450.inouek.test450.utils.SendPostAsyncTask;

public class RegisterFragment extends Fragment {

    private Credentials mCredentials;
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
            mCredentials = new Credentials.Builder(mEmailString, mPasswordString)
                    .addFirstName(mFirstNameString)
                    .addLastName(mLastNameString)
                    .addUsername(mNicknameString)
                    .build();

            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_register))
                    .build();

            //build the JSONObject
            JSONObject msg = mCredentials.asJSONObject();

            //instantiate and execute the AsyncTask.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::handleRegisterOnPre)
                    .onPostExecute(this::handleRegisterOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }
    }

    private boolean anyErrors() {
        boolean anyErrors = false;

        if (!mFirstNameString.equals("")) {
            mFirstNameField.setError(null);
        } else {
            mFirstNameField.setError("First Name cannot be empty");
            anyErrors = true;
        }

        if (!mLastNameString.equals("")) {
            mLastNameField.setError(null);
        } else {
            mLastNameField.setError("Last Name cannot be empty");
            anyErrors = true;
        }

        if (!mNicknameString.equals("")) {
            mNicknameField.setError(null);
        } else {
            mNicknameField.setError("Nickname cannot be empty");
            anyErrors = true;
        }

        //If email does not contain exactly one '@'
        if (mEmailString.length() - mEmailString.replace("@", "").length() != 1) {
            //If email is empty
            if (mEmailString.equals("")) {
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
        } else if (!mPasswordString.matches("(.)*([A-Z])(.)*")) {
            mPasswordField.setError("Password must contain at least one capital letter");
            anyErrors = true;
        } else if (!mPasswordString.matches("(.)*(\\d)(.)*")) {
            mPasswordField.setError("Password must contain at least one number");
            anyErrors = true;
        } else {
            mPasswordField.setError(null);
        }
        if (!mPasswordString.equals(mPasswordConfirmString)) {
            mPasswordConfirmField.setError("Passwords do not match");
            anyErrors = true;
        }

        return anyErrors;
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR",  result);
    }

    /**
     * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleRegisterOnPre() {
        getActivity().findViewById(R.id.layout_register_wait).setVisibility(View.VISIBLE);
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleRegisterOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success =
                    resultsJSON.getBoolean(
                            getString(R.string.keys_json_register_success));

            if (success) {
                RegisterFragmentDirections.ActionRegisterFragmentToLoginFragment loginFragment =
                        RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(mCredentials);
                loginFragment.setCredentials(mCredentials);
                Navigation.findNavController(getView()).navigate(loginFragment);
                return;
            } else {
                //Login was unsuccessful. Don’t switch fragments and
                // inform the user
                String err =
                        resultsJSON.getString(
                                getString(R.string.keys_json_register_err));
                if (err.startsWith("Missing required")) {
                    mFirstNameField.setError(err);
                } else if (err.startsWith("Key (username)")){
                    mNicknameField.setError("Username is not available.");
                } else if (err.startsWith("Key (email)")){
                    mEmailField.setError("Email is already in use.");
                }
            }
            getActivity().findViewById(R.id.layout_register_wait)
                    .setVisibility(View.GONE);
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            //String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());
            getActivity().findViewById(R.id.layout_register_wait)
                    .setVisibility(View.GONE);
            mFirstNameField.setError("JSONException");
        }
    }
}
