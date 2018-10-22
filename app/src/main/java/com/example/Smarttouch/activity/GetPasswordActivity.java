package com.example.Smarttouch.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.example.Smarttouch.R;
import com.example.Smarttouch.Service_handler.SERVER;
import com.example.Smarttouch.Service_handler.ServiceHandler;
import com.example.Smarttouch.constant.ConstantKey;
import com.example.Smarttouch.model.Key;
import com.example.Smarttouch.model.KeyboardPasswdType;
import com.example.Smarttouch.net.ResponseService;
import com.example.Smarttouch.wheel.WheelViewDialog;
import com.squareup.picasso.Picasso;
import com.ttlock.bl.sdk.util.LogUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.Smarttouch.utils.DateUitl.getTime;

public class GetPasswordActivity extends BaseActivity {

    @BindView(R.id.permanent)
    RadioButton permanentView;

    @BindView(R.id.period)
    RadioButton periodView;

    @BindView(R.id.loop)
    RadioButton loopView;

    @BindView(R.id.once)
    RadioButton onceView;

    @BindView(R.id.loop_value)
    TextView loopValueView;

    @BindView(R.id.start_time)
    TextView startTimeView;

    @BindView(R.id.end_time)
    TextView endTimeView;

    @BindView(R.id.password)
    TextView passwordView;

    @BindView(R.id.button4)
    Button uploadekey;

    @BindView(R.id.loop_layout)
    LinearLayout loopLayout;

    @BindView(R.id.start_time_layout)
    LinearLayout startTimeLayout;

    @BindView(R.id.end_time_layout)
    LinearLayout endTimeLayout;

    String[] pwdTypeTiles;
    @BindView(R.id.showPwd)
    TextView showPwd;

    @BindView(R.id.imageView3)
    ImageView back;

    private TimePickerView timePickerView;

    private WheelViewDialog wheelViewDialog;

    private boolean hourOnly;
    String str_lockid = "";

    String strpassword = "ekamsonofnirmalsingh", status = "", Message = "", accesstocken = "", Deviceid = "", starttime = "", endttime = "";

    private ProgressDialog dialog, dialog2;
    @BindView(R.id.imageView4)
    ImageView img_advertisement;

    /**
     * n
     * 键盘密码类型
     */
    private int keyboardPwdType;

    long startDate;

    long endDate;

    private Key key;
    List<String> allNames = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_password);
        ButterKnife.bind(this);
        key = MainActivity.curKey;
        pwdTypeTiles = getResources().getStringArray(R.array.pwd_type);
        permanentView.setText(pwdTypeTiles[0]);
        periodView.setText(pwdTypeTiles[1]);
        loopView.setText(pwdTypeTiles[2]);
        onceView.setText(pwdTypeTiles[3]);

        uploadekey.setEnabled(false);

        //TODO:
        startDate = new Date().getTime();
        endDate = new Date().getTime();
        startTimeView.setText(getTime(new Date(), "yyyy-MM-dd HH:mm"));
        endTimeView.setText(getTime(new Date(), "yyyy-MM-dd HH:mm"));

        keyboardPwdType = KeyboardPasswdType.PERMENANT;
        loopLayout.setVisibility(View.GONE);
        endTimeLayout.setVisibility(View.GONE);

        SharedPreferences pref = getSharedPreferences("Smart_touch", MODE_PRIVATE);
        str_lockid = pref.getString("lockid", "nodata");
        accesstocken = pref.getString("Acess_tocken", "nodata");
        Deviceid = pref.getString("device_id", "nodata");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        uploadekey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Upload_ekey().execute();
            }
        });
        new Get_add().execute();

    }

    @OnClick({R.id.permanent, R.id.period, R.id.loop, R.id.once, R.id.start_time_layout, R.id.end_time_layout, R.id.password, R.id.loop_value})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.permanent:
                hourOnly = false;
                keyboardPwdType = KeyboardPasswdType.PERMENANT;
                loopLayout.setVisibility(View.GONE);
                endTimeLayout.setVisibility(View.GONE);
                break;
            case R.id.period:
                hourOnly = false;
                keyboardPwdType = KeyboardPasswdType.PERIOD;
                loopLayout.setVisibility(View.GONE);
                endTimeLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.loop:
                hourOnly = true;
                keyboardPwdType = KeyboardPasswdType.WEEKENDREPETUAL;
                loopValueView.setText("weekend");
                loopLayout.setVisibility(View.VISIBLE);
                endTimeLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.once:
                hourOnly = false;
                keyboardPwdType = KeyboardPasswdType.ONCE;
                loopLayout.setVisibility(View.GONE);
                endTimeLayout.setVisibility(View.GONE);
                break;
            case R.id.start_time_layout:
                if (hourOnly) {
                    timePickerView = new TimePickerView(GetPasswordActivity.this, TimePickerView.Type.HOURS_MINS);
                    timePickerView.setCyclic(true);
                    timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date) throws ParseException {
                            int h = date.getHours();
                            int m = date.getMinutes();
                            startTimeView.setText(String.format("%02d:%02d", h, m));
                            starttime = startTimeView.getText().toString();
                            date = new Date();
                            date.setHours(h);
                            date.setMinutes(m);
                            startDate = date.getTime();
//                            startDate = date.getTime();
//                            startTimeView.setText(getTime(date, "HH:mm"));
                        }
                    });
                } else {
                    timePickerView = new TimePickerView(GetPasswordActivity.this, TimePickerView.Type.ALL);
                    timePickerView.setCyclic(false);
                    timePickerView.setRange(2017, 2020);
                    timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date) throws ParseException {
                            startDate = date.getTime();
                            startTimeView.setText(getTime(date, "yyyy-MM-dd HH:mm"));
                            starttime = startTimeView.getText().toString();
                        }
                    });
                }
                timePickerView.setTime(new Date());
                timePickerView.setCancelable(true);
                timePickerView.show();
                break;
            case R.id.end_time_layout:
                if (hourOnly) {
                    timePickerView = new TimePickerView(GetPasswordActivity.this, TimePickerView.Type.HOURS_MINS);
                    timePickerView.setCyclic(true);
                    timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date) throws ParseException {
                            int h = date.getHours();
                            int m = date.getMinutes();
                            endTimeView.setText(String.format("%02d:%02d", h, m));
                            endttime = endTimeView.getText().toString();
                            date = new Date();
                            date.setHours(h);
                            date.setMinutes(m);
                            endDate = date.getTime();
//                            endTimeView.setText(getTime(date, "HH:mm"));
//                            endDate = date.getTime();
                        }
                    });
                } else {
                    timePickerView = new TimePickerView(GetPasswordActivity.this, TimePickerView.Type.ALL);
                    timePickerView.setCyclic(false);
                    timePickerView.setRange(2017, 2018);
                    timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date) throws ParseException {
                            endDate = date.getTime();
                            endTimeView.setText(getTime(date, "yyyy-MM-dd HH:mm"));
                            endttime = endTimeView.getText().toString();
                        }
                    });
                }
                timePickerView.setTime(new Date());
                timePickerView.setCancelable(true);
                timePickerView.show();
                break;
            case R.id.password:
                new AsyncTask<Void, Integer, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        dialog = new ProgressDialog(GetPasswordActivity.this);
                        dialog.setMessage("Please wait...");
                        dialog.setCancelable(false);
                        dialog.show();
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String json = ResponseService.getKeyboardPwd(key.getLockId(), 4, keyboardPwdType, startDate, endDate);
                        String pwd = "";
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            if (jsonObject.has("keyboardPwd"))
                                pwd = jsonObject.getString("keyboardPwd");
                            else pwd = jsonObject.getString("description");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return pwd;
                    }

                    @Override
                    protected void onPostExecute(String pwd) {
                        super.onPostExecute(pwd);
                        dialog.dismiss();
                        showPwd.setText(pwd);
                        strpassword = pwd;
                        uploadekey.setEnabled(true);

                    }
                }.execute();
                break;

//            case R.id.button4:
//                new Upload_ekey().execute();
//                break;
            case R.id.loop_value:
                showWheelView();
                break;
        }
    }

    private void showWheelView() {
        wheelViewDialog = new WheelViewDialog(GetPasswordActivity.this, new WheelViewDialog.ICustomDialogEventListener() {
            @Override
            public void customDialogEvent(String circleModeValue, int position) {
                loopValueView.setText(circleModeValue);
                keyboardPwdType = position + 5;
                LogUtil.d("keyboardPwdType:" + keyboardPwdType, DBG);
            }
        });
        wheelViewDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action) {
            Intent intent = new Intent(GetPasswordActivity.this, KeyboardPwdListActivity.class);
            intent.putExtra(ConstantKey.KEY, key);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private class Upload_ekey extends AsyncTask<Void, String, Void> implements DialogInterface.OnCancelListener {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // mProgressHUD = ProgressHUD.show(Login_Screen.this, "Connecting", true, true, this);
            // Showing progress dialog
            dialog2 = new ProgressDialog(GetPasswordActivity.this);
            dialog2.setMessage("Please wait...");
            dialog2.setCancelable(false);
            dialog2.show();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            publishProgress("Please wait...");


            ServiceHandler sh = new ServiceHandler();
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
            //nameValuePairs.add(new BasicNameValuePair("email", email_fb));
            nameValuePairs.add(new BasicNameValuePair("lock_id", str_lockid));
            nameValuePairs.add(new BasicNameValuePair("lock_pasword", strpassword));
            nameValuePairs.add(new BasicNameValuePair("from_date", starttime));
            nameValuePairs.add(new BasicNameValuePair("to_date", endttime));
            //ekam


            String jsonStr = sh.makeServiceCall_withHeader(SERVER.UPLOAD_password,
                    ServiceHandler.POST, nameValuePairs, accesstocken, Deviceid);

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
            dialog2.dismiss();

            if (status.contentEquals("true")) {


                Toast.makeText(GetPasswordActivity.this, Message, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(GetPasswordActivity.this, Message, Toast.LENGTH_LONG).show();
            }


        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }

    private class Get_add extends AsyncTask<Void, String, Void> implements DialogInterface.OnCancelListener {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // mProgressHUD = ProgressHUD.show(Login_Screen.this, "Connecting", true, true, this);
            // Showing progress dialog
//            dialog2 = new ProgressDialog(GetPasswordActivity.this);
//            dialog2.setMessage("Please wait...");
//            dialog2.setCancelable(false);
//            dialog2.show();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            //publishProgress("Please wait...");


            ServiceHandler sh = new ServiceHandler();
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);


//            String jsonStr = sh.makeServiceCall(SERVER.ADDVERTISEMENT,
//                    ServiceHandler.POST, nameValuePairs);
            String jsonStr = sh.makeServiceCall_withHeader(SERVER.ADDVERTISEMENT,
                    ServiceHandler.POST, nameValuePairs, accesstocken, Deviceid);


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
                        //String str_result = jsonObj_data.getString("result");
                        JSONArray cast = jsonObj_data.getJSONArray("result");
                        for (int i = 0; i < cast.length(); i++) {
                            JSONObject actor = cast.getJSONObject(i);
                            String name = actor.getString("image");
                            allNames.add(name);
                        }

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
            //dialog2.dismiss();

            if (status.contentEquals("true")) {

                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    int i = 0;

                    public void run() {
                       // img_advertisement.setImageResource(imageArray[i]);

                        Picasso.with(GetPasswordActivity.this).load(allNames.get(i)).into(img_advertisement);

                        i++;
                        if (i > allNames.size() - 1) {
                            i = 0;
                        }
                        handler.postDelayed(this, 2500);  //for interval...
                    }
                };
                handler.postDelayed(runnable, 2000);

            }


        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }
}
