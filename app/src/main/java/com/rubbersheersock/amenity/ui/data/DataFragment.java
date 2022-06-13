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
import java.text.SimpleDateFormat;
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
                String brief = "最近更新时间为: "+new SimpleDateFormat("yyyy-MM-dd HH:SS:mm").format(info.latestUpdateTimeStamp)+"; \n"
                        +"上次更新新增房源: "+info.newForSaleNumber +";  "
                        +"所有房源: "+info.totalHouseForSaleNumber +";  "
                        +"已下架: "+info.soldNumber+";";
                adapterData.add(brief);
                adapterData.add("-----------在售同款房屋信息------------------");
                for(int i=0;i<info.monitorHouseList.size();i++){
                    LianjiaCDBean bean = info.monitorHouseList.get(i);
                    String monitorInfo = "面积: "+bean.proportion+" ; "
                            +"售价:"+bean.price+" ; 状态: 在售 "+" \n"
                            +"信息变更时间: "+new SimpleDateFormat("yyyy-MM-dd").format(bean.fetchdate)
                            +"  "+bean.decoration;
                    adapterData.add(monitorInfo);
                }
                adapterData.add("-------已下架房屋信息，可能是已售--------");
                for(int i=0;i<info.soldHouseList.size();i++){
                    LianjiaCDBean bean = info.soldHouseList.get(i);
                    String soldInfo = "面积: "+bean.proportion+" ;"
                            +"  售价:"+bean.price+" ;  状态: 下架 "+" \n"
                            +"下架时间: "+new SimpleDateFormat("yyyy-MM-dd").format(bean.fetchdate)
                            +"  "+bean.decoration;
                    adapterData.add(soldInfo);
                }
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