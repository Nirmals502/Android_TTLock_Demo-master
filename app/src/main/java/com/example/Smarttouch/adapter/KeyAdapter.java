package com.example.Smarttouch.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.Smarttouch.R;
import com.example.Smarttouch.model.Key;
import com.example.Smarttouch.utils.ViewHolder;

import java.util.List;

/**
 * Created by TTLock on 2016/9/12 0012.
 */
public class KeyAdapter extends BaseAdapter {

    private Context mContext;

    private List<Key> keys;

    public KeyAdapter(Context mContext, List<Key> keys) {
        this.mContext = mContext;
        this.keys = keys;
    }

    @Override
    public int getCount() {
        return keys.size();
    }

    @Override
    public Object getItem(int position) {
        return keys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.get(mContext, convertView, R.layout.item_key);
        Key key = keys.get(position);
        ((TextView)viewHolder.getView(R.id.lockname)).setText(key.getLockName());
        ((TextView)viewHolder.getView(R.id.identity)).setText(key.isAdmin() ? "admin" : "user");
        ((TextView)viewHolder.getView(R.id.battery)).setText(String.format("Electricity:%d", key.getBattery()));
        return viewHolder.getConvertView();
    }
}
