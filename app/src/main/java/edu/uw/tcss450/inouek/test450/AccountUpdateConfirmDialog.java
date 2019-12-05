package edu.uw.tcss450.inouek.test450;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.fragment.app.DialogFragment;
import org.json.JSONException;
import org.json.JSONObject;
import edu.uw.tcss450.inouek.test450.model.Credentials;
import edu.uw.tcss450.inouek.test450.utils.SendPostAsyncTask;

public class AccountUpdateConfirmDialog extends DialogFragment {
    HomeActivity mActivity;
    Credentials mCredentials;
    UserFragment mUserFrag;

    AccountUpdateConfirmDialog(HomeActivity homeActivity,
                               Credentials credentials,
                               UserFragment userFragment) {
        mActivity = homeActivity;
        mCredentials = credentials;
        mUserFrag = userFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.update_account_info)
                .setPositiveButton(R.string.action_update_account, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Uri uri = new Uri.Builder()
                                .scheme("https")
                                .appendPath(getString(R.string.ep_base_url))
                                .appendPath(getString(R.string.ep_account))
                                .build();

                        JSONObject message = mCredentials.asJSONObject();

                        new SendPostAsyncTask.Builder(uri.toString(), message)
                                .onPreExecute(() -> getActivity()
                                        .findViewById(R.id.user_fragment_progress)
                                        .setVisibility(View.VISIBLE))
                                .onPostExecute(s -> {
                                    try {
                                        JSONObject resultsJSON = new JSONObject(s);

                                        boolean success = resultsJSON.getBoolean("success");

                                        if (success) {
                                            //Update credentials stored everywhere
                                            mActivity.setCredentials(mCredentials);
                                            mUserFrag.setCredentials(mCredentials);

                                            SharedPreferences prefs =
                                                    mActivity.getSharedPreferences(
                                                            mActivity.getString(R.string.keys_shared_prefs),
                                                            Context.MODE_PRIVATE);

                                            if (prefs.contains(mActivity.getString(R.string.keys_prefs_email)) &&
                                                    prefs.contains(mActivity.getString(R.string.keys_prefs_password))) {
                                                prefs.edit().putString(mActivity.getString(R.string.keys_prefs_email),
                                                        mCredentials.getEmail()).apply();
                                                prefs.edit().putString(mActivity.getString(R.string.keys_prefs_password),
                                                        mCredentials.getPassword()).apply();
                                            }
                                        } else {
                                            ((EditText) getActivity().findViewById(R.id.account_nickname))
                                                    .setError("Username not available, no changes made");
                                        }
                                        mActivity.findViewById(R.id.user_fragment_progress)
                                                .setVisibility(View.GONE);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                })
                                .build().execute();
                    }
                })
                .setNeutralButton(R.string.action_cancel_popup, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //closes app by default
                    }});
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
