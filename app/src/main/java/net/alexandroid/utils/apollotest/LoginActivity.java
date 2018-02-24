package net.alexandroid.utils.apollotest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.AuthFBMutation;
import com.AuthLocalMutation;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.type.AuthLocalInput;

import net.alexandroid.shpref.ShPref;
import net.alexandroid.utils.mylog.MyLog;

import java.util.Arrays;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;

public class LoginActivity extends AppCompatActivity implements FacebookCallback<LoginResult> {

    private CallbackManager mCallbackManager;
    private ApolloClient mApolloClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mApolloClient = ApolloClient.builder()
                .serverUrl("http://104.196.187.249:3000/api/")
                .okHttpClient(new OkHttpClient())
                .build();

        checkIfLoggedIn();
        setRegularLogin();
        setFbLogin();
    }

    private void setRegularLogin() {
        findViewById(R.id.regularLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {

                AuthLocalInput authLocalInput = AuthLocalInput.builder()
                        .email("test@korolev.co.il")
                        .firstName("Alesha")
                        .lastName("Korolev")
                        .password("q1w2e3r4")
                        .build();

                AuthLocalMutation authLocalMutation = AuthLocalMutation.builder()
                        .user(authLocalInput)
                        .isSignUp(false)
                        .build();


                mApolloClient.mutate(authLocalMutation)
                        .enqueue(new ApolloCall.Callback<AuthLocalMutation.Data>() {
                            @Override
                            public void onResponse(@Nonnull Response<AuthLocalMutation.Data> response) {
                                if (response.hasErrors()) {
                                    for (Error error : response.errors()) {
                                        MyLog.i(error.message());
                                    }
                                }
                                if (response.data() != null) {
                                    MyLog.d(response.data().authLocal().firstName());
                                } else {
                                    MyLog.e("data is null");
                                }
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }

                            @Override
                            public void onFailure(@Nonnull ApolloException e) {

                            }
                        });
            }
        });
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
        LoginButton loginButton = findViewById(R.id.fbLogin);
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
        MyLog.d("onCancel");
    }

    @Override
    public void onError(FacebookException exception) {
        MyLog.e("onError, " + exception.getMessage());
    }

    // Facebook end

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startMainActivity(String pToken) {
        ShPref.put(R.string.fb_token, pToken);
        AuthFBMutation authFBMutation = AuthFBMutation.builder()
                .session(pToken)
                .build();

        mApolloClient.mutate(authFBMutation)
                .enqueue(new ApolloCall.Callback<AuthFBMutation.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<AuthFBMutation.Data> response) {
                        if (response.hasErrors()) {
                            for (Error error : response.errors()) {
                                MyLog.i(error.message());
                            }
                        }
                        if (response.data() != null) {
                            MyLog.d(response.data().authFB().firstName());
                        } else {
                            MyLog.e("data is null");
                        }
/*                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();*/
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });

    }
}
