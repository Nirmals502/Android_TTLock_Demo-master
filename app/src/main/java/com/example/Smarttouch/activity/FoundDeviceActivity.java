package com.example.Smarttouch.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.Smarttouch.MyApplication;
import com.example.Smarttouch.R;
import com.example.Smarttouch.adapter.FoundDeviceAdapter;
import com.example.Smarttouch.constant.BleConstant;
import com.example.Smarttouch.enumtype.Operation;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FoundDeviceActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private List<ExtendedBluetoothDevice> devices;
    private ListView listView;
    ImageView img_back;
    private FoundDeviceAdapter foundDeviceAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BleConstant.ACTION_BLE_DEVICE)) {
                Bundle bundle = intent.getExtras();
                ExtendedBluetoothDevice device = bundle.getParcelable(BleConstant.DEVICE);
                foundDeviceAdapter.updateDevice(device);
            }
//            else if(action.equals(BleConstant.ACTION_BLE_DISCONNECTED)) {
//                cancelProgressDialog();
//                Toast.makeText(FoundDeviceActivity.this, "蓝牙已断开,请重新添加.", Toast.LENGTH_LONG).show();
//            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_device);
        init();
    }

    private void init() {
        devices = new ArrayList<>();
        listView = getView(R.id.list);
        img_back = getView(R.id.imageView3);
        foundDeviceAdapter = new FoundDeviceAdapter(this, devices);
        listView.setAdapter(foundDeviceAdapter);
        listView.setOnItemClickListener(this);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        registerReceiver(mReceiver, getIntentFilter());
    }

    private IntentFilter getIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleConstant.ACTION_BLE_DEVICE);
        intentFilter.addAction(BleConstant.ACTION_BLE_DISCONNECTED);
        return intentFilter;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyApplication.bleSession.setOperation(Operation.ADD_ADMIN);
        MyApplication.mTTLockAPI.connect((ExtendedBluetoothDevice) foundDeviceAdapter.getItem(position));
//        String strlockinfo = devices.get(position).toString();
//        try {
//            JSONObject jsonObject = new JSONObject(strlockinfo);
//            JSONObject jsonble  = jsonObject.getJSONObject("ExtendedBluetoothDevice");
//            String lcok_name = jsonObject.getString("name");
//            String mAddress = jsonObject.getString("mAddress");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        //  devices.
       // Toast.makeText(FoundDeviceActivity.this,devices.get(position).toString(),Toast.LENGTH_LONG).show();
        showProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
