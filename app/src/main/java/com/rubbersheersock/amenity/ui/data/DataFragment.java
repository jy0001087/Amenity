package com.rubbersheersock.amenity.ui.data;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.elvishew.xlog.XLog;

import com.rubbersheersock.amenity.R;
import com.rubbersheersock.amenity.Services.DBServices.DBTransferService;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class DataFragment extends Fragment {
    private static String ARG_PARAM = "mLabName";
    private String param;
    private DBJsonBroadcastReceiver receiver;
    private IntentFilter intentfilter;
    private ListView listview;
    private TextView primBottomTitle;

    public static DataFragment newInstance (String mLabName){
        DataFragment fg = new DataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, mLabName);
        fg.setArguments(args);
        return fg;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!= null){
            param=getArguments().getString(ARG_PARAM);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_data,container,false);

        //增加各种控件
        listview = (ListView) rootView.findViewById(R.id.list_item);
        primBottomTitle = rootView.findViewById(R.id.primBottomTitle);
        //启动服务
        Intent intent = new Intent(getActivity(), DBTransferService.class);
        getActivity().startService(intent);
        //注册广播接收
        receiver = new DBJsonBroadcastReceiver();
        intentfilter = new IntentFilter();
        intentfilter.addAction("com.rubbersheersock.amenity.jsonQuery");
        requireActivity().registerReceiver(receiver, intentfilter);


        return rootView;
    }


    public class DBJsonBroadcastReceiver extends BroadcastReceiver {
        private JSONObject json;

        @Override
        public void onReceive(Context context, Intent intent) {
            XLog.tag("DBunit").i("broadcast has been received");
            try {
                json = new JSONObject(intent.getStringExtra("json"));
                XLog.tag("anemity-broadcast").d("json = " + json);
            } catch (Exception e) {
                XLog.tag("DBunit").e("Error occurred when string convert into json", e);
            }
            LianjiaCDBeanInfo beanInfo = beanListProcessor(json);
            HouseInfoAdapter adapter = new HouseInfoAdapter(getContext(), (ArrayList<LianjiaCDBean>) beanInfo.monitorHouseList);
            listview.setAdapter(adapter);

            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            formater.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            primBottomTitle.setText("上次更新：" + formater.format(beanInfo.latestUpdateTimeStamp)+"，共记"+beanInfo.monitorHouseList.size()+"套");

            //增加listview点击监听事件
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    LianjiaCDBean mBean = (LianjiaCDBean) listview.getAdapter().getItem(i);
                    // 将点击的item里面的字弹出来
                    //Toast.makeText(getActivity(), mBean.url, Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse(mBean.url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        public LianjiaCDBeanInfo beanListProcessor(JSONObject json) {
            ArrayList<LianjiaCDBean> mbeanList = DataProcessor.getBeanList(json);
            LianjiaCDBeanInfo beanInfo = DataProcessor.getHouseInfo(mbeanList,param);
            for (int k = 0; k < beanInfo.monitorHouseList.size(); k++) {
                for (int i = 0; i < beanInfo.monitorHouseList.size()-1; i++) {
                    if (beanInfo.monitorHouseList.get(i).price > beanInfo.monitorHouseList.get(i + 1).price) {
                        LianjiaCDBean mBean = beanInfo.monitorHouseList.get(i);
                        beanInfo.monitorHouseList.set(i, beanInfo.monitorHouseList.get(i + 1));
                        beanInfo.monitorHouseList.set(i + 1, mBean);
                    }
                }
            }
            return beanInfo;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        XLog.tag("amenity-fragement-life").d("Datafragement has been destroyed");
    }
}