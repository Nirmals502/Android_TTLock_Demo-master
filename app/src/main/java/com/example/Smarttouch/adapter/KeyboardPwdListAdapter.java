package com.example.Smarttouch.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.Smarttouch.MyApplication;
import com.example.Smarttouch.R;
import com.example.Smarttouch.activity.BaseActivity;
import com.example.Smarttouch.databinding.ItemKeyboardPwdBinding;
import com.example.Smarttouch.enumtype.Operation;
import com.example.Smarttouch.model.KeyboardPwd;
import com.example.Smarttouch.myInterface.OperateCallback;
import com.example.Smarttouch.net.ResponseService;
import com.ttlock.bl.sdk.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by TTLock on 2017/3/24.
 */
public class KeyboardPwdListAdapter extends RecyclerView.Adapter<KeyboardPwdListAdapter.KeyboardPwdHolder> {

    private Context mContext;
    private ArrayList<KeyboardPwd> keyboardPwds;
    private String lockmac;

    public static class KeyboardPwdHolder extends RecyclerView.ViewHolder {

        public ItemKeyboardPwdBinding binding;

        public KeyboardPwdHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void bind(KeyboardPwd KeyboardPwd) {
            binding.setKeyboardPwd(KeyboardPwd);
        }
    }

    public KeyboardPwdListAdapter(Context context, ArrayList<KeyboardPwd> keyboardPwds, String lockmac) {
        this.mContext = context;
        this.keyboardPwds = keyboardPwds;
        this.lockmac = lockmac;
    }

    @Override
    public KeyboardPwdHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keyboard_pwd, parent, false);
        return new KeyboardPwdHolder(itemView);
    }

    @Override
    public void onBindViewHolder(KeyboardPwdHolder holder, final int position) {
        final KeyboardPwd keyboardPwd = keyboardPwds.get(position);
        holder.bind(keyboardPwd);
        holder.binding.deleteByGateway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected void onPostExecute(String json) {
                        super.onPostExecute(json);
                        ((BaseActivity)mContext).cancelProgressDialog();
                        LogUtil.d("json:" + json, true);
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int errcode = jsonObject.getInt("errcode");
                            String msg;
                            if(errcode != 0) {
                                msg = jsonObject.getString("errmsg");
                            } else {
                                msg = "通过网关删除成功";
                                keyboardPwds.remove(position);
                                notifyDataSetChanged();
                            }
                            ((BaseActivity)mContext).toast(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        ((BaseActivity)mContext).showProgressDialog();
                        return ResponseService.deleteKeyboardPwd(keyboardPwd.getLockId(), keyboardPwd.getKeyboardPwdId(), 2);
                    }
                }.execute();
            }
        });
        holder.binding.deleteByBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyApplication.operateCallback = new OperateCallback() {
                    @Override
                    public void onSuccess() {
                        new AsyncTask<Void, Void, String>() {

                            @Override
                            protected void onPostExecute(String json) {
                                super.onPostExecute(json);
                                ((BaseActivity)mContext).cancelProgressDialog();
                                LogUtil.d("json:" + json, true);
                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    int errcode = jsonObject.getInt("errcode");
                                    String msg;
                                    if(errcode != 0) {
                                        msg = jsonObject.getString("errmsg");
                                    } else {
                                        msg = "服务器删除密码成功";
                                        keyboardPwds.remove(position);
                                        notifyDataSetChanged();
                                    }
                                    ((BaseActivity)mContext).toast(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            protected String doInBackground(Void... params) {
                                ((BaseActivity)mContext).showProgressDialog();
                                return ResponseService.deleteKeyboardPwd(keyboardPwd.getLockId(), keyboardPwd.getKeyboardPwdId(), 1);
                            }
                        }.execute();
                    }

                    @Override
                    public void onFailed() {

                    }
                };

                MyApplication.mTTLockAPI.connect(lockmac);
                MyApplication.bleSession.setPassword(keyboardPwd.getKeyboardPwd());
                MyApplication.bleSession.setOperation(Operation.DELETE_ONE_KEYBOARDPASSWORD);
                MyApplication.bleSession.setLockmac(lockmac);

            }
        });
    }

    @Override
    public int getItemCount() {
        return keyboardPwds.size();
    }

}
