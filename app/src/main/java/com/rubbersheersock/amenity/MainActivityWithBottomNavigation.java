package com.rubbersheersock.amenity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.rubbersheersock.amenity.Services.DBServices.DBTransferService;
import com.rubbersheersock.amenity.databinding.ActivityMainWithBottomNavigationBinding;

public class MainActivityWithBottomNavigation extends AppCompatActivity {

    private ActivityMainWithBottomNavigationBinding binding;
    private Handler mhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainWithBottomNavigationBinding.inflate(getLayoutInflater());

        //去掉标题栏
        getSupportActionBar().hide();

        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main_with_bottom_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        XLog.init(LogLevel.ALL);//日志系统初始化

    }

    public void setHandler(Handler vhandler){
        mhandler=vhandler;
    }
}