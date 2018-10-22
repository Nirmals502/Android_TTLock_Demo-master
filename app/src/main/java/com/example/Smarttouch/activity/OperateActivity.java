package com.example.Smarttouch.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Smarttouch.MyApplication;
import com.example.Smarttouch.R;
import com.example.Smarttouch.Service_handler.SERVER;
import com.example.Smarttouch.Service_handler.ServiceHandler;
import com.example.Smarttouch.adapter.OperateAdapter;
import com.example.Smarttouch.constant.Operate;
import com.example.Smarttouch.dao.DbService;
import com.example.Smarttouch.enumtype.Operation;
import com.example.Smarttouch.model.BleSession;
import com.example.Smarttouch.model.Key;
import com.example.Smarttouch.net.ResponseService;
import com.example.Smarttouch.sp.MyPreference;
import com.squareup.picasso.Picasso;
import com.ttlock.bl.sdk.util.LogUtil;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

import static com.example.Smarttouch.MyApplication.mTTLockAPI;
import static com.example.Smarttouch.activity.MainActivity.curKey;

public class OperateActivity extends BaseActivity {

    @BindView(R.id.list)
    ListView listView;

    private OperateAdapter operateAdapter;

    private BleSession bleSession;

    private Key mKey;
    @BindView(R.id.imageView4)
    ImageView img_advertisement;
    private Dialog dialog;

    private String[] operates;
    private int openid;
    List<String> allNames = new ArrayList<String>();
    String strpassword = "ekamsonofnirmalsingh", status = "", Message = "", accesstocken = "", Deviceid = "", starttime = "", endttime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_operate);
        ButterKnife.bind(this);
        mKey = curKey;
        operates = getResources().getStringArray(R.array.operate);
        operateAdapter = new OperateAdapter(this, mKey, operates);
        listView.setAdapter(operateAdapter);
        bleSession = MyApplication.bleSession;
        openid = MyPreference.getOpenid(this, MyPreference.OPEN_ID);
        SharedPreferences pref = getSharedPreferences("Smart_touch", MODE_PRIVATE);

        accesstocken = pref.getString("Acess_tocken", "nodata");
        Deviceid = pref.getString("device_id", "nodata");
        new Get_add().execute();

    }

    @OnItemClick(R.id.list)
    void onItemClick(int position) {
        switch (position) {
            case Operate.CLICK_TO_UNLOCK://点击开门
                if (mTTLockAPI.isConnected(mKey.getLockMac())) {//当前处于连接状态 直接发指令
                    if (mKey.isAdmin())
                        mTTLockAPI.unlockByAdministrator(null, openid, mKey.getLockVersion(), mKey.getAdminPs(), mKey.getUnlockKey(), mKey.getLockFlagPos(), System.currentTimeMillis(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
                    else
                        mTTLockAPI.unlockByUser(null, openid, mKey.getLockVersion(), mKey.getStartDate(), mKey.getEndDate(), mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
                } else {//未连接进行连接
                    showProgressDialog(getString(R.string.words_wait));
                    mTTLockAPI.connect(mKey.getLockMac());
                    bleSession.setOperation(Operation.CLICK_UNLOCK);
                    bleSession.setLockmac(mKey.getLockMac());
                }
                break;
            //后面两个操作是车位锁独有操作
            case Operate.LOCKCAR_UP://车位锁升
                if (mTTLockAPI.isConnected(mKey.getLockMac())) {//当前处于连接状态 直接发指令
                    if (mKey.isAdmin())
                        mTTLockAPI.lockByAdministrator(null, openid, mKey.getLockVersion(), mKey.getAdminPs(), mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr());
                    else
                        mTTLockAPI.lockByUser(null, openid, mKey.getLockVersion(), mKey.getStartDate(), mKey.getEndDate(), mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
//                    MyApplication.mTTLockAPI.lockByUser(null, 0, mKey.getLockVersion(), 1489990922165l, 1490077322165l, mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
                } else {
                    showProgressDialog(getString(R.string.words_wait));
                    mTTLockAPI.connect(mKey.getLockMac());
                    bleSession.setOperation(Operation.LOCKCAR_UP);
                    bleSession.setLockmac(mKey.getLockMac());
                }
                break;
            case Operate.LOCKCAR_DOWN://车位锁降
                if (mTTLockAPI.isConnected(mKey.getLockMac())) {//当前处于连接状态 直接发指令
                    if (mKey.isAdmin())
                        mTTLockAPI.unlockByAdministrator(null, openid, mKey.getLockVersion(), mKey.getAdminPs(), mKey.getUnlockKey(), mKey.getLockFlagPos(), System.currentTimeMillis(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
                    else
                        mTTLockAPI.unlockByUser(null, openid, mKey.getLockVersion(), mKey.getStartDate(), mKey.getEndDate(), mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
//                    MyApplication.mTTLockAPI.unlockByUser(null, 0, mKey.getLockVersion(), 1489990922165l, 1490077322165l, mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
                } else {
                    showProgressDialog(getString(R.string.words_wait));
                    mTTLockAPI.connect(mKey.getLockMac());
                    bleSession.setOperation(Operation.LOCKCAR_DOWN);
                    bleSession.setLockmac(mKey.getLockMac());
                }
                break;
            case Operate.DEVICE_FIRMWARE_UPDATE:
                start_activity(DeviceFirmwareUpdateActivity.class);
                break;
            default:
                showMyDialog(position);
                break;
        }
    }

    private void showMyDialog(final int operate) {
        if (dialog == null)
            dialog = new Dialog(this, R.style.dialog);
        View dialogView = View.inflate(this, R.layout.dialog_input_view, null);
        TextView titleView = getView(dialogView, R.id.dialog_title);
        final EditText contentView = getView(dialogView, R.id.dialog_content);
        String title = operates[operate];
        String content = "";
        String hit = "";
        switch (operate) {
            case Operate.SET_ADMIN_CODE:
//                title = "设置管理码";
                hit = getString(R.string.words_input_admin_code);
                break;
            case Operate.SET_DELETE_CODE:
//                title = "设置清空码";
                hit = getString(R.string.words_input_clear_code);
                break;
//            case Operate.SET_LOCK_TIME:
////                title = "设置锁时间";
//                hit = getString(R.string.words_input_time);
//                break;
            case Operate.SEND_EKey:
                hit = getString(R.string.words_input_receiver_name);
                break;
            default:
                contentView.setVisibility(View.GONE);
                break;
        }
        titleView.setText(title);
        contentView.setHint(hit);
        TextView oKView = getView(dialogView, R.id.dialog_ok);
        TextView cancelView = getView(dialogView, R.id.dialog_cancel);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        oKView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (operate >= Operate.SET_ADMIN_CODE && operate <= Operate.GET_LOCK_TIME)
                    showProgressDialog(getString(R.string.words_wait));
                else showProgressDialog();
                String content = contentView.getText().toString().trim();
                switch (operate) {
                    case Operate.SET_ADMIN_CODE:
                        if (mTTLockAPI.isConnected(mKey.getLockMac())) {
                            mTTLockAPI.setAdminKeyboardPassword(null, openid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr(), content);
                        } else {
                            mTTLockAPI.connect(mKey.getLockMac());
                            bleSession.setOperation(Operation.SET_ADMIN_KEYBOARD_PASSWORD);
                            bleSession.setPassword(content);
                            bleSession.setLockmac(mKey.getLockMac());
                        }
                        break;
                    case Operate.SET_DELETE_CODE:
                        bleSession.setOperation(Operation.SET_DELETE_PASSWORD);
                        bleSession.setPassword(content);
                        bleSession.setLockmac(mKey.getLockMac());
                        break;
//                    case Operate.SET_LOCK_TIME:
//                        if(mTTLockAPI.isConnected(mKey.getLockMac())) {
//                            mTTLockAPI.setLockTime(null, openid, curKey.getLockVersion(), curKey.getUnlockKey(), System.currentTimeMillis(), curKey.getLockFlagPos(), curKey.getAesKeystr(), curKey.getTimezoneRawOffset());
//                        } else {
//                            mTTLockAPI.connect(mKey.getLockMac());
//                            bleSession.setOperation(Operation.SET_LOCK_TIME);
//                            bleSession.setLockmac(mKey.getLockMac());
//                        }
//                        break;
                    case Operate.RESET_KEYBOARD_PASSWORD:
                        if (mTTLockAPI.isConnected(mKey.getLockMac())) {
                            mTTLockAPI.resetKeyboardPassword(null, openid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr());
                        } else {

                            mTTLockAPI.connect(mKey.getLockMac());
                            bleSession.setOperation(Operation.RESET_KEYBOARD_PASSWORD);
                            bleSession.setLockmac(mKey.getLockMac());
                        }
                        break;
//                    case Operate.RESET_EKEY:
//                        if(mTTLockAPI.isConnected(mKey.getLockMac())) {//如果当前处于连接状态，则直接发送重置钥匙指令
//                            mTTLockAPI.resetEKey(null, openid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getLockFlagPos() + 1, curKey.getAesKeystr());
//                        } else {//主动连接锁 并设置操作标志
//                            mTTLockAPI.connect(mKey.getLockMac());
//                            bleSession.setOperation(Operation.RESET_EKEY);
//                            bleSession.setLockmac(mKey.getLockMac());
//                        }
//                        break;
                    case Operate.RESET_LOCK:
                        if (mTTLockAPI.isConnected(mKey.getLockMac())) {//如果当前处于连接状态，则直接发送重置锁指令
                            mTTLockAPI.resetLock(null, openid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos() + 1, curKey.getAesKeystr());


                        } else {//主动连接锁 并设置操作标志
                            mTTLockAPI.connect(mKey.getLockMac());
                            bleSession.setOperation(Operation.RESET_LOCK);
                            bleSession.setLockmac(mKey.getLockMac());
                        }
                        break;
                    case Operate.GET_OPERATE_LOG:
                        if (mTTLockAPI.isConnected(mKey.getLockMac())) {
                            mTTLockAPI.getOperateLog(null, curKey.getLockVersion(), curKey.getAesKeystr(), curKey.getTimezoneRawOffset());
                        } else {
                            mTTLockAPI.connect(mKey.getLockMac());
                            bleSession.setOperation(Operation.GET_OPERATE_LOG);
                            bleSession.setLockmac(mKey.getLockMac());
                        }
                        break;
                    case Operate.GET_LOCK_TIME:
                        if (mTTLockAPI.isConnected(mKey.getLockMac())) {
                            mTTLockAPI.getLockTime(null, curKey.getLockVersion(), curKey.getAesKeystr(), curKey.getTimezoneRawOffset());
                        } else {
                            mTTLockAPI.connect(mKey.getLockMac());
                            bleSession.setOperation(Operation.GET_LOCK_TIME);
                            bleSession.setLockmac(mKey.getLockMac());
                        }
                        break;
                    case Operate.SEND_EKey:
                        new AsyncTask<String, String, String>() {
                            @Override
                            protected String doInBackground(String... params) {
                                String json = ResponseService.sendEKey(mKey, params[0]);
                                String msg = "";
                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    msg = jsonObject.getString("description");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return msg;
                            }

                            @Override
                            protected void onPostExecute(String msg) {
                                super.onPostExecute(msg);
                                toast(msg);
                                cancelProgressDialog();
                            }
                        }.execute(contentView.getText().toString().trim());
                        break;
                    case Operate.GET_PASSWORD:
                        cancelProgressDialog();
                        Intent intent = new Intent(OperateActivity.this, GetPasswordActivity.class);
                        startActivity(intent);
                        break;
                    case Operate.Unlock_Remotely:
                        new AsyncTask<String, String, String>() {
                            @Override
                            protected String doInBackground(String... params) {
                                String json = ResponseService.UNLOCK_REMOTELY(mKey.getLockId());
                                String msg = "";
                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    msg = jsonObject.getString("errmsg");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return msg;
                            }

                            @Override
                            protected void onPostExecute(String msg) {
                                super.onPostExecute(msg);
                                toast(msg);
                                cancelProgressDialog();
                            }
                        }.execute(contentView.getText().toString().trim());
                        break;


                }
            }
        });

        dialog.show();

        Window dialogWindow = dialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        LogUtil.d("p.height:" + p.height, DBG);
        LogUtil.d("p.width:" + p.width, DBG);
//        p.width = WindowManager.LayoutParams.MATCH_PARENT;
//        p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);

        dialog.setContentView(dialogView);
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

                        Picasso.with(OperateActivity.this).load(allNames.get(i)).into(img_advertisement);

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

