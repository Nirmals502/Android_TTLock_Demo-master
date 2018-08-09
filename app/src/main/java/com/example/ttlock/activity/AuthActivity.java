package com.example.ttlock.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ttlock.R;
import com.example.ttlock.Service_handler.SERVER;
import com.example.ttlock.Service_handler.ServiceHandler;
import com.example.ttlock.app.Config;
import com.example.ttlock.net.ResponseService;
import com.example.ttlock.signup_screen;
import com.example.ttlock.sp.MyPreference;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AuthActivity extends BaseActivity implements View.OnClickListener {

    EditText user;
    EditText pwd;
    private ProgressDialog dialog;
    String status = "", Message = "", Device_id = "", Acess_tocken = "";
    TextView text_regstr_link;
    String regId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_auth);
        user = getView(R.id.editText1);
        pwd = getView(R.id.editText2);
        text_regstr_link = getView(R.id.textView);
        Button auth = getView(R.id.button2);
        auth.setOnClickListener(this);
        text_regstr_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, signup_screen.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        String access_token = MyPreference.getStr(AuthActivity.this, MyPreference.ACCESS_TOKEN);
        String openid = MyPreference.getStr(AuthActivity.this, MyPreference.OPEN_ID);
//        ((TextView)getView(R.id.auth_access_token)).setText(access_token);
//        ((TextView)getView(R.id.auth_openid)).setText(openid);
    }

    @Override
    public void onClick(View v) {

        if (user.getText().toString().contentEquals("")) {
            Animation anm = Shake_Animation();
            user.startAnimation(anm);
        } else if (pwd.getText().toString().contentEquals("")) {
            Animation anm = Shake_Animation();
            pwd.startAnimation(anm);
        } else {
            final String username = "nirmals502@gmail.com";
            final String password = "nirmal2996";

            new AsyncTask<Void, Integer, String>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    dialog = new ProgressDialog(AuthActivity.this);
                    dialog.setMessage("Please wait...");
                    dialog.show();
                }

                @Override
                protected String doInBackground(Void... params) {
                    return ResponseService.auth(username, password);
                }

                @Override
                protected void onPostExecute(String json) {
                    String msg = "Something wrong";
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if (jsonObject.has("errcode")) {
                            msg = jsonObject.getString("description");
                        } else {
                            msg = getString(R.string.words_authorize_successed);
                            String access_token = jsonObject.getString("access_token");
                            String openid = jsonObject.getString("openid");
                            MyPreference.putStr(AuthActivity.this, MyPreference.ACCESS_TOKEN, access_token);
                            MyPreference.putStr(AuthActivity.this, MyPreference.OPEN_ID, openid);
                            SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                            regId = pref.getString("regId", "");
                            new Login().execute();
//                        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        onResume();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    toast(msg);
                }
            }.execute();
        }
    }

    public Animation Shake_Animation() {
        Animation shake = new TranslateAnimation(0, 5, 0, 0);
        shake.setInterpolator(new CycleInterpolator(5));
        shake.setDuration(300);


        return shake;
    }

    private class Login extends AsyncTask<Void, String, Void> implements DialogInterface.OnCancelListener {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // mProgressHUD = ProgressHUD.show(Login_Screen.this, "Connecting", true, true, this);
            // Showing progress dialog
            dialog = new ProgressDialog(AuthActivity.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            publishProgress("Please wait...");


            ServiceHandler sh = new ServiceHandler();
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
            //nameValuePairs.add(new BasicNameValuePair("email", email_fb));
            nameValuePairs.add(new BasicNameValuePair("email", user.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("password", pwd.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("device_id", regId));
            nameValuePairs.add(new BasicNameValuePair("os_type", "Android"));
            nameValuePairs.add(new BasicNameValuePair("os_version", "6.0"));
            nameValuePairs.add(new BasicNameValuePair("hardware", "Samsung"));
            nameValuePairs.add(new BasicNameValuePair("app_version", "1"));


            String jsonStr = sh.makeServiceCall(SERVER.LOGIN,
                    ServiceHandler.POST, nameValuePairs);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(jsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Getting JSON Array node
                // JSONArray array1 = null;
                try {
                    status = jsonObj.getString("status");

                    Message = jsonObj.getString("message");
                    if (status.contentEquals("true")) {
                        JSONObject jsonObj_data = null;
                        jsonObj_data = jsonObj.getJSONObject("data");
                        Device_id = jsonObj_data.getString("device_id");
                        Acess_tocken = jsonObj_data.getString("access_token");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            dialog.dismiss();

            if (status.contentEquals("true")) {
                SharedPreferences shared = getSharedPreferences("Smart_touch", MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("Acess_tocken", Acess_tocken);
                editor.putString("login_status", "loged_in");
                editor.putString("device_id", Device_id);
                editor.commit();
                //Toast.makeText(Login_screen.this, status, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(AuthActivity.this, Message, Toast.LENGTH_LONG).show();
            }


        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }
}
