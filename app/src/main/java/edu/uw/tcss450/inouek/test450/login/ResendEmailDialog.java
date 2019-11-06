package edu.uw.tcss450.inouek.test450.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.model.Credentials;
import edu.uw.tcss450.inouek.test450.utils.SendPostAsyncTask;

public class ResendEmailDialog extends DialogFragment {

    private String mEmail;

    public ResendEmailDialog(String email) {
        mEmail = email;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.resend_popup_info)
                .setPositiveButton(R.string.resend_popup_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //closes the popup by default
                    }
                })
                .setNegativeButton(R.string.resend_popup_resend, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Resends the email
                        Uri uri = new Uri.Builder()
                                .scheme("https")
                                .appendPath(getString(R.string.ep_base_url))
                                .appendPath(getString(R.string.ep_resend))
                                .build();

                        //build the JSONObject
                        JSONObject msg = new JSONObject();
                        try {
                            msg.put("email", mEmail);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //instantiate and execute the AsyncTask.
                        new SendPostAsyncTask.Builder(uri.toString(), msg)
                                .build().execute();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
