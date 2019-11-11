package edu.uw.tcss450.inouek.test450;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.inouek.test450.login.RegisterFragmentDirections;
import edu.uw.tcss450.inouek.test450.model.Credentials;
import edu.uw.tcss450.inouek.test450.utils.SendPostAsyncTask;

public class AccountUpdateConfirmDialog extends DialogFragment {
    HomeActivity mActivity;
    Credentials mCredentials;
    String mOgEmail;

    public AccountUpdateConfirmDialog(HomeActivity activity, String ogEmail, Credentials credentials) {
        mActivity = activity;
        mCredentials = credentials;
        mOgEmail = ogEmail;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.logout_info)
                .setPositiveButton(R.string.action_logout, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO figure out context to call HomeActivity.logout()
                        Uri uri = new Uri.Builder()
                                .scheme("https")
                                .appendPath(getString(R.string.ep_base_url))
                                .appendPath(getString(R.string.ep_account))
                                .build();

                        JSONObject message = mCredentials.asJSONObject();
                        try {
                            message.put("og_email", mOgEmail);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        new SendPostAsyncTask.Builder(uri.toString(), message)
                                .onPreExecute(() -> getActivity()
                                        .findViewById(R.id.user_fragment_progress)
                                        .setVisibility(View.VISIBLE))
                                .onPostExecute(s -> {
                                    JSONObject resultsJSON = null;
                                    try {
                                        resultsJSON = new JSONObject(s);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    boolean success =
                                            false;
                                    try {
                                        success = resultsJSON.getBoolean(
                                                getString(R.string.keys_json_register_success));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (success) {
                                        //TODO update credentials stored everywheres
                                        return;
                                    } else {
                                        // TODO inform the user of email or username already taken error
                                        //No changes were made or something like that
                                        try {
                                            String err =
                                                    resultsJSON.getString(
                                                            getString(R.string.keys_json_register_err));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    getActivity().findViewById(R.id.user_fragment_progress)
                                            .setVisibility(View.GONE);
                                })
                                .build().execute();
                    }
                })
                .setNegativeButton(R.string.action_cancel_popup, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //closes app by default
                    }});
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
