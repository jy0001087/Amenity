package com.rubbersheersock.amenity.ui.data;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.elvishew.xlog.XLog;

import com.rubbersheersock.amenity.R;
import com.rubbersheersock.amenity.Services.DBServices.DBTransferService;
import com.rubbersheersock.amenity.databinding.FragmentDataBinding;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DataFragment extends Fragment {

    private FragmentDataBinding binding;
    private DBJsonBroadcastReceiver receiver;
    private IntentFilter intentfilter;
    private DBTransferService myService;
    private Object json;
    private ListView listview;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DataViewModel dataViewModel =
                new ViewModelProvider(this).get(DataViewModel.class);

        binding = FragmentDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //增加listview
        listview = (ListView) root.findViewById(R.id.list_item);

        //启动服务
        Intent intent = new Intent(getActivity(), DBTransferService.class);
        getActivity().startService(intent);

        //注册广播接收
        receiver = new DBJsonBroadcastReceiver();
        intentfilter = new IntentFilter();
        intentfilter.addAction("com.rubbersheersock.amenity.jsonQuery");
        requireActivity().registerReceiver(receiver,intentfilter);
        XLog.tag("amenity").i(json);

        return root;
    }

    public class DBJsonBroadcastReceiver extends BroadcastReceiver {
        private JSONObject json;

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                json = new JSONObject(intent.getStringExtra("json"));
                ArrayList<LianjiaCDBean> beanlist = DataProcessor.getBeanList(json);
                //定义一个链表用于存放要显示的数据
                LianjiaCDBeanInfo info = DataProcessor.getHouseInfo(beanlist);
                final List<String> adapterData = new ArrayList<String>();
                //存放要显示的数据
                String brief = "最近更新时间为: "+info.latestUpdateTimeStamp+"; "
                        +"上次更新新增房源: "+info.newForSaleNumber +"; "
                        +"所有房源: "+info.totalHouseForSaleNumber +"; ";
                adapterData.add(brief);

                //创建ArrayAdapter对象adapter并设置适配器
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, adapterData);
                //将LsitView绑定到ArrayAdapter上
                listview.setAdapter(adapter);
                XLog.tag("DBunit").i("broadcast has been received");
            } catch (Exception e) {
                XLog.tag("DBunit").e("Error occurred when string convert into json",e);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}