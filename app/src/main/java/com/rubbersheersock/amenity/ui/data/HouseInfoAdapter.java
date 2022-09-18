package com.rubbersheersock.amenity.ui.data;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintSet;

import com.rubbersheersock.amenity.R;
import com.rubbersheersock.amenity.ui.data.LianjiaCDBean;

import java.text.SimpleDateFormat;
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
        return mBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return mBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout,viewGroup,false);

        TextView txProportion = view.findViewById(R.id.listProportion);
        TextView txPrice = view.findViewById(R.id.listPrice);
        TextView txOriginalUpdateDate = view.findViewById(R.id.listOriginalUpdateDate);

        Float originalPrice = mBeanList.get(i).originalPrice;
        if (originalPrice == null || originalPrice == 0.0){
            txPrice.setText(String.valueOf(mBeanList.get(i).getPrice())+"万");
        }else if(originalPrice >= mBeanList.get(i).price){
            txPrice.setText(String.valueOf(originalPrice)+" --> "+String.valueOf(mBeanList.get(i).getPrice())+"万");
            txPrice.setTextColor(Color.parseColor("#FEFEFE"));
            view.setBackgroundColor(Color.parseColor("#9CD061"));
            SimpleDateFormat formator = new SimpleDateFormat("yyyy-MM-dd");
            txOriginalUpdateDate.setText(formator.format(mBeanList.get(i).originalUpdatedate)+"变更");
            view.setElevation(3);
        }else if(originalPrice <= mBeanList.get(i).price){
            txPrice.setText(String.valueOf(originalPrice)+" --> "+String.valueOf(mBeanList.get(i).getPrice())+"万");
            txPrice.setTextColor(Color.parseColor("#FEFEFE"));
            view.setBackgroundColor(Color.parseColor("#E63564"));
        }
        txProportion.setText(String.valueOf(mBeanList.get(i).getProportion())+" ㎡");


        return view;
    }
}
