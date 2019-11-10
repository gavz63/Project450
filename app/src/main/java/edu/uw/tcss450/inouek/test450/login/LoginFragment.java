package edu.uw.tcss450.inouek.test450.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class LoginFragment extends Fragment {

    private Credentials mCredentials;
    private EditText mEmailField;
    private EditText mPasswordField;
    private SwitchMaterial mStayLoggedInSwitch;
    private String mEmailString;
    private String mPasswordString;

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
            getActivity().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            //get pushy token


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
//                message.put("token", deviceToken);

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
            getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
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

                    //Login was successful. Switch to the SuccessFragment.
                    LoginFragmentDirections.ActionLoginFragmentToHomeActivity homeActivity =
                            LoginFragmentDirections
                                    .actionLoginFragmentToHomeActivity(mCredentials);
                    homeActivity.setJwt(resultsJSON.getString(
                            getString(R.string.keys_json_login_jwt)));

                    Navigation.findNavController(getView()).navigate(homeActivity);
                    getActivity().finish();
                    return;
                } else {
                    //Saving the token wrong. Don’t switch fragments and inform the user
                    ((TextView) getView().findViewById(R.id.login_email))
                            .setError("Token Error");
                }
            } catch (JSONException e) {
                //It appears that the web service didn’t return a JSON formatted String
                //or it didn’t have what we expected in it.
                Log.e("JSON_PARSE_ERROR",  result
                        + System.lineSeparator()
                        + e.getMessage());

                ((TextView) getView().findViewById(R.id.login_email))
                        .setError("JSON Error");
            }
        }
    }
}
