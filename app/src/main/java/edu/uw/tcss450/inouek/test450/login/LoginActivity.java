package edu.uw.tcss450.inouek.test450.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.uw.tcss450.inouek.test450.R;
import me.pushy.sdk.Pushy;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Pushy.listen(this);
        setContentView(R.layout.activity_main);
    }
}
