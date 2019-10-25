package edu.uw.tcss450.gavz63.project450.login;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONObject;
import edu.uw.tcss450.gavz63.project450.model.Credentials;
import edu.uw.tcss450.gavz63.project450.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Credentials mCredentials;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        EditText email = view.findViewById(R.id.login_email);
        EditText pass = view.findViewById(R.id.login_pass);
        //Comment out this block before going to prod
        email.setText("test@test");
        pass.setText("test123");

        Button b = view.findViewById(R.id.button_login_register);
        b.setOnClickListener(v -> this.onRegisterClicked());

        b = view.findViewById(R.id.button_login_sign_in);
        b.setOnClickListener(v -> this.validateLogin(view));
    }

    private void onRegisterClicked() {
        NavController nc = Navigation.findNavController(getView());

        nc.navigate(R.id.action_loginFragment_to_registerFragment);
    }

    private void validateLogin(View view) {
        EditText emailView = view.findViewById(R.id.login_email);
        EditText passView = view.findViewById(R.id.login_pass);

        boolean emailErrors = emailErrors(emailView);
        boolean passErrors = passwordErrors(passView);

        if (!emailErrors && !passErrors) {
            Credentials credentials = new Credentials.Builder(
                    emailView.getText().toString(),
                    passView.getText().toString())
                    .build();

            mCredentials = credentials;

            //LoginFragmentDirections
            //homeActivity.setJwt(resultsJSON.getString(getString(R.string.keys_json_login_jwt)));

            Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_home_Activity);
//            //build the web service URL
//            Uri uri = new Uri.Builder()
//                    .scheme("https")
//                    .appendPath(getString(R.string.ep_base_url))
//                    .appendPath(getString(R.string.ep_login))
//                    .build();
//
//            //build the JSONObject
//            JSONObject msg = credentials.asJSONObject();
//
//            mCredentials = credentials;
//
//            //instantiate and execute the AsyncTask.
//            new SendPostAsyncTask.Builder(uri.toString(), msg)
//                    .onPreExecute(this::handleLoginOnPre)
//                    .onPostExecute(this::handleLoginOnPost)
//                    .onCancelled(this::handleErrorsInTask)
//                    .build().execute();

        }
    }

    private boolean emailErrors(EditText theEmailField) {
        String email = theEmailField.getText().toString();
        boolean toReturn = false;

        if (!email.contains("@")) {
            theEmailField.setError("Please enter a valid email");
            if (email.equals("")) {
                theEmailField.setError("Email cannot be empty");
            }
            toReturn = true;
        } else {
            theEmailField.setError(null);
        }

        return toReturn;
    }

    private boolean passwordErrors(EditText thePasswordField) {
        String pass = thePasswordField.getText().toString();
        boolean toReturn = false;

        if (pass.equals("")) {
            thePasswordField.setError("Password cannot be empty");
            toReturn = true;
        } else {
            thePasswordField.setError(null);
        }

        return toReturn;
    }
}
