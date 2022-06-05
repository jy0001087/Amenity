package com.rubbersheersock.amenity.ui.data;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.elvishew.xlog.XLog;
import com.rubbersheersock.amenity.MainActivityWithBottomNavigation;
import com.rubbersheersock.amenity.R;
import com.rubbersheersock.amenity.Services.DBServices.DBTransferService;
import com.rubbersheersock.amenity.databinding.FragmentDataBinding;

public class DataFragment extends Fragment {

    private FragmentDataBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DataViewModel dataViewModel =
                new ViewModelProvider(this).get(DataViewModel.class);

        binding = FragmentDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textData;
        dataViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        //绑定事件

        ((Button) root.findViewById(R.id.querybutton))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DBTransferService.class);
                        getActivity().bindService(intent,dbConnection, Context.BIND_AUTO_CREATE);
                    }

                    private ServiceConnection dbConnection = new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                            XLog.tag("ServiceAmenity").i("onServiceConnected");
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName componentName) {
                            XLog.tag("ServiceAmenity").i("onServiceDisconnected");
                        }
                    };
                });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}