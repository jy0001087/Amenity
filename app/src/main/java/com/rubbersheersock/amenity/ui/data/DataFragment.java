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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.elvishew.xlog.XLog;

import com.rubbersheersock.amenity.R;
import com.rubbersheersock.amenity.Services.DBServices.DBTransferService;
import com.rubbersheersock.amenity.databinding.FragmentDataBinding;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class DataFragment extends Fragment {

    private FragmentDataBinding binding;
    private DBJsonBroadcastReceiver receiver;
    private IntentFilter intentfilter;
    private DBTransferService myService;
    private Object json;
    private ListView listview;
    private TextView primBottomTitle;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DataViewModel dataViewModel =
                new ViewModelProvider(this).get(DataViewModel.class);

        binding = FragmentDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //增加各种控件
        listview = (ListView) root.findViewById(R.id.list_item);
        primBottomTitle = root.findViewById(R.id.primBottomTitle);
        //启动服务
        Intent intent = new Intent(getActivity(), DBTransferService.class);
        getActivity().startService(intent);

        //注册广播接收
        receiver = new DBJsonBroadcastReceiver();
        intentfilter = new IntentFilter();
        intentfilter.addAction("com.rubbersheersock.amenity.jsonQuery");
        requireActivity().registerReceiver(receiver, intentfilter);

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
        return root;
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
            primBottomTitle.setText("上次更新：" + formater.format(beanInfo.latestUpdateTimeStamp));
        }

        public LianjiaCDBeanInfo beanListProcessor(JSONObject json) {
            ArrayList<LianjiaCDBean> mbeanList = DataProcessor.getBeanList(json);
            LianjiaCDBeanInfo beanInfo = DataProcessor.getHouseInfo(mbeanList);
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
        binding = null;
    }
}