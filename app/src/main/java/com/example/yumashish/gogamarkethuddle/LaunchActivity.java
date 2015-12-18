//package com.example.yumashish.gogamarkethuddle;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.example.yumashish.gogamarkethuddle.Kakunin.GOGAMapActivity;
//import com.facebook.AccessToken;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.GraphRequest;
//import com.facebook.GraphResponse;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Set;
//import java.util.Stack;
//
//public class LaunchActivity extends AppCompatActivity {
//    public static String TAG = "Huddle_Launch_Activity";
//    public static Stack<Class<?>> ACTIVITY_STACK = new Stack<Class<?>>();
//
//    private LaunchActivity mContext;
//    private EditText mEmail;
//    private EditText mPass;
//    private Button mLogin;
//    private Button mGuest;
//    private LoginChecker mAPIAdapter;
//    public CallbackManager mFbCallbackManager;
//    Handler mLauncherTaskHandler;
//    enum MessageType { DEFAULT, FB_LOGIN_START, FB_LOGIN_CALLBACK }
//
//    private JSONObject mFbLoginGraphObject;
//    ProgressDialog mProgressDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        ACTIVITY_STACK.push(LaunchActivity.class);
//        setContentView(R.layout.activity_launch);
//        mContext = this;
//
//        mFbCallbackManager = CallbackManager.Factory.create();
//        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
//        fbLoginButton.setReadPermissions(Arrays.asList("email", "user_birthday"));
//        fbLoginButton.registerCallback(mFbCallbackManager, new FacebookCallback<LoginResult>() {
//
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                AccessToken.setCurrentAccessToken(loginResult.getAccessToken());
//                Message message = mLauncherTaskHandler.obtainMessage();
//                Bundle b = new Bundle();
//                b.putSerializable(GOGAMapActivity.MESSAGE_TYPE_KEY, MessageType.FB_LOGIN_START);
//                message.setData(b);
//                mLauncherTaskHandler.sendMessage(message);
//
//                Log.i(TAG, loginResult.getAccessToken().toString());
//                //Attempt Login thru Huddle DB
//                GraphRequest request = GraphRequest.newMeRequest(
//                        loginResult.getAccessToken(),
//                        new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(
//                                    JSONObject object,
//                                    GraphResponse response) {
//                                // Application code
//                                Log.i(TAG, object.toString());
//                                mFbLoginGraphObject = object;
//                                Message message = mLauncherTaskHandler.obtainMessage();
//                                Bundle b = new Bundle();
//                                b.putSerializable(GOGAMapActivity.MESSAGE_TYPE_KEY, MessageType.FB_LOGIN_CALLBACK);
//                                message.setData(b);
//                                mLauncherTaskHandler.sendMessage(message);
//                            }
//                        });
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,name,link");
//                request.setParameters(parameters);
//                request.executeAsync();
//
//                //find declined permissions
//                //TODO show dialog and re-request
//                Set<String> declined = AccessToken.getCurrentAccessToken().getDeclinedPermissions();
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });
//
//        mEmail = (EditText) findViewById(R.id.login_email_field);
//        mPass = (EditText) findViewById(R.id.login_pass_field);
//        mLogin = (Button) findViewById(R.id.login_launch_button);
//        mGuest = (Button) findViewById(R.id.login_guest_button);
//
//        mAPIAdapter = new LoginChecker();
//
//        mLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new LoginTask(mContext, mEmail.getText().toString(), mPass.getText().toString(), "Logging In", "Logging in as " + mEmail.getText().toString()).execute();
//            }
//        });
//
//        mGuest.setOnClickListener(new View.OnClickListener(){
//           @Override
//            public void onClick(View v) {
//               //Intent intent = new Intent(mContext, CustomerHomeActivity.class);
//               //intent.putExtra(LoginChecker.INTENT_TAG, 0);
//               //startActivity(intent);
//           }
//        });
//
//        mLauncherTaskHandler = new Handler(Looper.myLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                MessageType type = (MessageType) msg.getData().getSerializable(GOGAMapActivity.MESSAGE_TYPE_KEY);
//                Log.i(TAG, "Got handler message of type " + type.toString());
//                switch (type) {
//                    case FB_LOGIN_START:
//                        mProgressDialog = ProgressDialog.show(mContext,"Logging In...","Logging in from an external authenticator");
//                        break;
//                    case FB_LOGIN_CALLBACK:
//                        new LoginExternTask().execute();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        };
//    }
//
//    public class LoginExternTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                DatabaseConnector DBC = new DatabaseConnector();
//                DatabaseConnector.LoginResponseJson loginResponseJson = DBC.GetAuthSecretKeyExternal(mFbLoginGraphObject.getString("id"), "fb", -1);
//                DBC.SetAccessDetails(loginResponseJson.result);
//                DatabaseConnector.SellerResponseJson userDetailsResponse = DBC.GetUserOrSellerById(Integer.parseInt(loginResponseJson.result.uid));
//                LoginChecker.LoginResult result = new LoginChecker.LoginResult(loginResponseJson.result, userDetailsResponse.result, false);
//
//                //mProgressDialog.setMessage("Authenticated");
//
//                Intent intent = new Intent(mContext, CustomerHomeActivity.class);
//                Bundle b = new Bundle();
//                b.putSerializable("data", result);
//                intent.putExtra("data", result);
//                startActivity(intent);
//            } catch (IOException e) {
//                Toast.makeText(mContext, "Failed to login due to being unable to conenct to Huddle DB", Toast.LENGTH_LONG);
//                DatabaseConnector.FlushDebugQueue(DatabaseConnector.GetLogEPrinter(TAG));
//                Log.e(TAG, "IOException" + e.getMessage());
//                DatabaseConnector.LogStackTrace(TAG, e);
//            } catch (JSONException e) {
//                Toast.makeText(mContext, "Failed to login due to being unable to conenct to external authenticator", Toast.LENGTH_LONG);
//                Log.e(TAG, "JSONObject" + e.getMessage());
//                DatabaseConnector.LogStackTrace(TAG, e);
//            }
//            return null;
//        }
//    }
//
//    public class LoginTask extends AsyncTask<Void, Void, LoginChecker.LoginResult> {
//        ProgressDialog progressDialog;
//        Context context;
//        String title, message, email, pass;
//
//        public LoginTask(Context context, String email, String pass, String title, String message) {
//            this.context =  context;
//            this.email =    email;
//            this.pass =     pass;
//            this.title =    title;
//            this.message =  message;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog = ProgressDialog.show(context, title, message);
//        }
//
//        @Override
//        protected LoginChecker.LoginResult doInBackground(Void... params) {
//            try {
//                LoginChecker.LoginResult result = mAPIAdapter.Login(email, pass, true);
//                return result;
//            } catch (IOException ioe) {
//                DatabaseConnector.FlushDebugQueue(DatabaseConnector.GetLogEPrinter(TAG));
//                Log.e(TAG, "Exception:" + ioe.getMessage());
//                DatabaseConnector.LogStackTrace(TAG, ioe);
//                return new LoginChecker.LoginResult(null, null, true);
//            } catch (Exception e) {
//                DatabaseConnector.LogAndFlush(TAG, e);
//                return new LoginChecker.LoginResult(null, null, true);
//            }
//        }
//
//        @Override
//        protected void onPostExecute(LoginChecker.LoginResult result) {
//            if(!result.ERROR) {
//                Intent intent = new Intent(mContext, CustomerHomeActivity.class);
//                Bundle b = new Bundle();
//                b.putSerializable("data", result);
//                intent.putExtra("data", result);
//                startActivity(intent);
//            } else {
//                Toast.makeText(context, "Error attempting to log in.", Toast.LENGTH_LONG).show();
//            }
//            progressDialog.dismiss();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        mFbCallbackManager.onActivityResult(requestCode, resultCode, data);
//    }
//}
