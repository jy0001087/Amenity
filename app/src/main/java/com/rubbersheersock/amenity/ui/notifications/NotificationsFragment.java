package com.rubbersheersock.amenity.ui.notifications;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rubbersheersock.amenity.R;
import com.rubbersheersock.amenity.databinding.FragmentNotificationsBinding;

import com.elvishew.xlog.XLog;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    private static final String LOGTAG = "Automate";

    public View onCreateView( LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        View rootView = inflater.inflate(R.layout.fragment_amenity, container, false);

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        init(rootView); //控件事件初始化
        return rootView;
    }

    public void init(View rootView) {
        try {
            Integer Gps = Settings.Secure.getInt(getContext().getContentResolver(), Settings.Secure.LOCATION_MODE);
            XLog.tag(LOGTAG).i("Gps status:"+Gps);
        }catch(Settings.SettingNotFoundException e){
            XLog.tag(LOGTAG).e("错误信息"+e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}