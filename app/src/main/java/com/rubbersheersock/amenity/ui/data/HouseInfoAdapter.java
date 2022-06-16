package com.rubbersheersock.amenity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintSet;

import com.rubbersheersock.amenity.R;
import com.rubbersheersock.amenity.ui.data.LianjiaCDBean;

import java.util.ArrayList;
import java.util.LinkedList;


public class HouseInfoAdapter extends BaseAdapter {
    private ArrayList<LianjiaCDBean> mBeanList;
    private Context mContext;

    public HouseInfoAdapter(Context mContext, ArrayList<LianjiaCDBean> mBeanList){
        this.mBeanList=mBeanList;
        this.mContext=mContext;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout,viewGroup,false);

        TextView txProportion = view.findViewById(R.id.listProportion);
        TextView txPrice = view.findViewById(R.id.listPrice);

        txProportion.setText(String.valueOf(mBeanList.get(i).getProportion()));
        txPrice.setText(String.valueOf(mBeanList.get(i).getPrice()));

        return view;
    }
}
