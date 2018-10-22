package com.example.Smarttouch.activity;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.example.Smarttouch.R;
import com.example.Smarttouch.adapter.KeyboardPwdListAdapter;
import com.example.Smarttouch.constant.ConstantKey;
import com.example.Smarttouch.databinding.ActivityKeyboardPwdListBinding;
import com.example.Smarttouch.model.Key;
import com.example.Smarttouch.model.KeyboardPwd;
import com.example.Smarttouch.net.ResponseService;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.ttlock.bl.sdk.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class KeyboardPwdListActivity extends BaseActivity {
    ActivityKeyboardPwdListBinding binding;
    Key key;
    ArrayList<KeyboardPwd> keyboardPwds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_keyboard_pwd_list);
        key = getIntent().getParcelableExtra(ConstantKey.KEY);

        showProgressDialog();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPostExecute(String json) {
                super.onPostExecute(json);
                LogUtil.d("json:" + json, DBG);
                cancelProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.has("errcode")) {
                        String errmsg = jsonObject.getString("errmsg");
                        toast(errmsg);
                    } else {
                        json = jsonObject.getJSONArray("list").toString();
                        keyboardPwds = GsonUtil.toObject(json, new TypeToken<ArrayList<KeyboardPwd>>(){});

                        KeyboardPwdListAdapter keyboardPwdListAdapter = new KeyboardPwdListAdapter(KeyboardPwdListActivity.this, keyboardPwds, key.getLockMac());
                        binding.recycler.setLayoutManager(new LinearLayoutManager(KeyboardPwdListActivity.this));
                        binding.recycler.setAdapter(keyboardPwdListAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                return ResponseService.keyboardPwdList(key.getLockId(), 1, 100);
            }
        }.execute();
    }
}
