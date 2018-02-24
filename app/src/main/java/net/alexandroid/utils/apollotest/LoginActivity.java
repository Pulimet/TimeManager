package net.alexandroid.utils.apollotest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import net.alexandroid.utils.mylog.MyLog;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements FacebookCallback<LoginResult> {

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkIfLoggedIn();
        setFbLogin();
    }

    private void checkIfLoggedIn() {
        boolean loggedIn = AccessToken.getCurrentAccessToken() != null;
        Log.d("ZAQ", loggedIn ? "Logged in" : " Not logged in");
        if (loggedIn) {
            String token = AccessToken.getCurrentAccessToken().getToken();
            MyLog.d("AccessToken: " + token);
            startMainActivity(token);
        }
    }

    private void setFbLogin() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile"));
        // Callback registration
        loginButton.registerCallback(mCallbackManager, this);
    }

    // Facebook start
    @Override
    public void onSuccess(LoginResult loginResult) {
        String token = loginResult.getAccessToken().getToken();
        MyLog.d("onSuccess, AccessToken: " + token);
        startMainActivity(token);
    }

    @Override
    public void onCancel() {
        Log.d("ZAQ", "onCancel");
    }

    @Override
    public void onError(FacebookException exception) {
        Log.e("ZAQ", "onError");
    }

    // Facebook end

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startMainActivity(String pToken) {


/*        ShPref.put(R.string.fb_token, pToken);
        startActivity(new Intent(this, MainActivity.class));
        finish();*/
    }
}
