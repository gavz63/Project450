package edu.uw.tcss450.inouek.test450;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import edu.uw.tcss450.inouek.test450.login.LoginActivity;
import me.pushy.sdk.Pushy;

public class LogoutConfirmDialog extends DialogFragment {

    HomeActivity mActivity;

    public LogoutConfirmDialog(HomeActivity activity) {
        mActivity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.logout_info)
                .setPositiveButton(R.string.action_logout, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logout();
                    }
                })
                .setNeutralButton(R.string.action_cancel_popup, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //closes app by default
                    }});
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void logout() {

        SharedPreferences prefs =
                mActivity.getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //remove the saved credentials from StoredPrefs
        prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
        prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();

        new DeleteTokenAsyncTask().execute();

        //lose this activity and bring back the Login
        Intent i = new Intent(mActivity, LoginActivity.class);
        startActivity(i);
        //End this Activity and remove it from the Activity back stack.
        mActivity.finish();
    }

    private class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            Pushy.unregister(mActivity);
            return null;
        }
    }
}
