package edu.uw.tcss450.inouek.test450.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.model.Credentials;
import edu.uw.tcss450.inouek.test450.utils.GetAsyncTask;
import me.pushy.sdk.Pushy;

public class LoginFragment extends Fragment {

    private Credentials mCredentials;
    private EditText mEmailField;
    private EditText mPasswordField;
    private SwitchMaterial mStayLoggedInSwitch;
    private String mEmailString;
    private String mPasswordString;
    private String mJwt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mStayLoggedInSwitch = view.findViewById(R.id.switch_login_stay_logged);
        mEmailField = view.findViewById(R.id.login_email);
        mPasswordField = view.findViewById(R.id.login_pass);

        Button b = view.findViewById(R.id.button_login_register);
        b.setOnClickListener(this::onRegisterClicked);

        b = view.findViewById(R.id.button_login_sign_in);
        b.setOnClickListener(this::validateLogin);

        try {
            mCredentials = LoginFragmentArgs.fromBundle(getArguments()).getCredentials();
            mEmailField.setText(mCredentials.getEmail());
            mPasswordField.setText(mCredentials.getPassword());
            deleteCredentials();
            validateLogin(null);
        } catch (IllegalArgumentException e) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //retrieve the stored credentials from SharedPrefs
        if (prefs.contains(getString(R.string.keys_prefs_email)) &&
                prefs.contains(getString(R.string.keys_prefs_password))) {

            final String email = prefs.getString(getString(R.string.keys_prefs_email), "");
            final String password = prefs.getString(getString(R.string.keys_prefs_password), "");
            //Load the two login EditTexts with the credentials found in SharedPrefs
            EditText emailEdit = getActivity().findViewById(R.id.login_email);
            emailEdit.setText(email);
            EditText passwordEdit = getActivity().findViewById(R.id.login_pass);
            passwordEdit.setText(password);
            mStayLoggedInSwitch.setChecked(true);
        }
    }


    private void onRegisterClicked(View view) {
        NavController nc = Navigation.findNavController(getView());

        nc.navigate(R.id.action_loginFragment_to_registerFragment);
    }

    private void validateLogin(View view) {
        mEmailString = mEmailField.getText().toString();
        mPasswordString = mPasswordField.getText().toString();

        if (!anyErrors()) {
            mCredentials = new Credentials.Builder(mEmailString, mPasswordString)
                    .build();

            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_login))
                    .appendPath(getString(R.string.ep_pushy))
                    .build();

            //instantiate and execute the AsyncTask.
            new AttemptLoginTask().execute(uri.toString());
        }
    }
    private boolean anyErrors() {
        boolean anyErrors = false;

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

        if (mPasswordString.equals("")) {
            mPasswordField.setError("Password cannot be empty");
            anyErrors = true;
        } else {
            mPasswordField.setError(null);
        }

        return anyErrors;
    }

    private void saveCredentials() {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        prefs.edit().putString(getString(R.string.keys_prefs_email), mCredentials.getEmail()).apply();
        prefs.edit().putString(getString(R.string.keys_prefs_password), mCredentials.getPassword()).apply();
    }

    private void deleteCredentials() {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //remove the saved credentials from StoredPrefs
        prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
        prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();
    }

    class AttemptLoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getActivity().findViewById(R.id.login_progress).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.button_login_register).setEnabled(false);
            getActivity().findViewById(R.id.button_login_sign_in).setEnabled(false);
        }

        @Override
        //TODO Do pushy login stuff here get token and all that
        protected String doInBackground(String... urls) {
            //get pushy token
            String deviceToken = "";
            try
            {
                // Assign a unique token to this device
                deviceToken = Pushy.register(getActivity().getApplicationContext());
                //subscribe to a topic (this is a Blocking call)
                Pushy.subscribe("all", getActivity().getApplicationContext());
            }
            catch (Exception exc)
            {
                cancel(true);
                // Return exc to onCancelled
                return exc.getMessage();
            }
            //feel free to remove later.
            Log.d("LOGIN", "Pushy Token: " + deviceToken);

            //attempt to log in: Send credentials AND pushy token to the web service
            StringBuilder response = new StringBuilder();
            HttpURLConnection urlConnection = null;

            try {
                URL urlObject = new URL(urls[0]);
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");


                urlConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());

                JSONObject message = mCredentials.asJSONObject();
                message.put("token", deviceToken);

                wr.write(message.toString());
                wr.flush();
                wr.close();

                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while((s = buffer.readLine()) != null) {
                    response.append(s);
                }
                publishProgress();
            } catch (Exception e) {
                response = new StringBuilder("Unable to connect, Reason: "
                        + e.getMessage());
                cancel(true);
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return response.toString();
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            getActivity().findViewById(R.id.login_progress).setVisibility(View.GONE);
            Log.e("LOGIN_ERROR", "Error in Login Async Task: " + s);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                Log.d("JSON result",result);
                JSONObject resultsJSON = new JSONObject(result);
                boolean success = resultsJSON.getBoolean("success");


                if (success) {
                    if (mStayLoggedInSwitch.isChecked()) {
                        saveCredentials();
                    } else {
                        deleteCredentials();
                    }

                    //build the web service URL
                    Uri uri = new Uri.Builder()
                            .scheme("https")
                            .appendPath(getString(R.string.ep_base_url))
                            .appendPath(getString(R.string.ep_login))
                            .build();

                    mJwt = resultsJSON.getString(getString(R.string.keys_json_login_jwt));

                    //Get credentials fields from db
                    new GetAsyncTask.Builder(uri.toString())
                            .addHeaderField("email",mCredentials.getEmail())
                            .onPostExecute(this::handleGetLoginOnPost)
                            .build().execute();
                } else {
                    //Login was unsuccessful. Don’t switch fragments and
                    // inform the user
                    String err =
                            resultsJSON.getString(
                                    getString(R.string.keys_json_register_err));
                    if (err.startsWith("missing ")) {
                        mEmailField.setError("Missing Credentials");
                    } else if (err.startsWith("Credentials do not")) {
                        mPasswordField.setError("Password is incorrect");
                    } else if (err.startsWith("Email not reg")){
                        mEmailField.setError("Email is not registered");
                    } else if (err.startsWith("Email not ver")) {
                        mEmailField.setError("Email is not verified");

                        DialogFragment dialogFragment = new ResendEmailDialog(mEmailString);
                        dialogFragment.show(getFragmentManager(), "alert");
                    }
                }
                getActivity().findViewById(R.id.login_progress)
                        .setVisibility(View.GONE);
                getActivity().findViewById(R.id.button_login_register).setEnabled(true);
                getActivity().findViewById(R.id.button_login_sign_in).setEnabled(true);
            } catch (JSONException e) {
                //It appears that the web service did not return a JSON formatted
                //String or it did not have what we expected in it.
                Log.e("JSON_PARSE_ERROR",  result
                        + System.lineSeparator()
                        + e.getMessage());
                getActivity().findViewById(R.id.login_progress)
                        .setVisibility(View.GONE);
                mEmailField.setError("JSON error");
            }
        }

        private void handleGetLoginOnPost(String result) {
            try {
                JSONObject resultsJSON = new JSONObject(result);
                boolean success = resultsJSON.getBoolean("success");


                if (success) {
                    Credentials c = new Credentials
                            .Builder(mCredentials.getEmail(), mCredentials.getPassword())
                            .addFirstName(resultsJSON.getString("firstname"))
                            .addLastName(resultsJSON.getString("lastname"))
                            .addUsername(resultsJSON.getString("username"))
                            .addColor(resultsJSON.getInt("color"))
                            .build();

                    //Login was successful. Switch to the SuccessFragment.
                    LoginFragmentDirections.ActionLoginFragmentToHomeActivity homeActivity =
                            LoginFragmentDirections
                                    .actionLoginFragmentToHomeActivity(c);
                    homeActivity.setJwt(mJwt);

                    Navigation.findNavController(getView()).navigate(homeActivity);
                    getActivity().finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
