package com.example.Smarttouch.activity;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.example.Smarttouch.R;
import com.example.Smarttouch.adapter.UnderGatewayLockAdapter;
import com.example.Smarttouch.constant.ConstantKey;
import com.example.Smarttouch.databinding.ActivityUnderGatewayLockListBinding;
import com.example.Smarttouch.model.Gateway;
import com.example.Smarttouch.model.UnderGatewayLock;
import com.example.Smarttouch.net.ResponseService;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.ttlock.bl.sdk.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UnderGatewayLockList extends BaseActivity {

    ActivityUnderGatewayLockListBinding binding;
    Gateway gateway;

    ArrayList<UnderGatewayLock> underGatewayLocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_under_gateway_lock_list);
        gateway = getIntent().getParcelableExtra(ConstantKey.GATEWAY);
        showProgressDialog();
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return ResponseService.underGatewayLockList(gateway.getGatewayId());
            }

            @Override
            protected void onPostExecute(String json) {
                super.onPostExecute(json);
                cancelProgressDialog();
                LogUtil.d("json:" + json, DBG);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.has("errcode")) {
                        String errmsg = jsonObject.getString("errmsg");
                        toast(errmsg);
                    } else {
                        json = jsonObject.getJSONArray("list").toString();
                        underGatewayLocks = GsonUtil.toObject(json, new TypeToken<ArrayList<UnderGatewayLock>>(){});

                        UnderGatewayLockAdapter gatewayListAdapter = new UnderGatewayLockAdapter(UnderGatewayLockList.this, underGatewayLocks);
                        binding.recycler.setLayoutManager(new LinearLayoutManager(UnderGatewayLockList.this));
                        binding.recycler.setAdapter(gatewayListAdapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

    }
}
