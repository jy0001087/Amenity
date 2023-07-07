package com.rubbersheersock.amenity.ui.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
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

        //final TextView textView = binding.textNotifications;
        //notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        init(rootView); //控件事件初始化
        return rootView;
    }

    public void init(View rootView) {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        XLog.tag(LOGTAG).i("Gps status:"+lm.isLocationEnabled());
        toggleGPS();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void  toggleGPS() {
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:3"));
        Context context = getContext().getApplicationContext();
        try {
            context.sendBroadcast(gpsIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}