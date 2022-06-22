package com.rubbersheersock.amenity.ui.data;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.elvishew.xlog.XLog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.rubbersheersock.amenity.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DataFragmentWithViewpager extends Fragment {
    private ViewPager2 mViewPager;
    private TabLayout mTabLayout;
    private List<String> mTabName = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        XLog.tag("amenity").d("DataFragmentWithViewPager has been called!");
        View rootView = inflater.inflate(R.layout.fragement_with_viewpager,container,false);
        initData();
        initView(rootView);
        return rootView;
    }

    public void initData(){
        mTabName.add(DataProcessor.PAG1);
        mTabName.add(DataProcessor.PAG2);
        mTabName.add(DataProcessor.PAG3);
        mTabName.add(DataProcessor.PAG4);
        mTabName.add(DataProcessor.PAG5);
    }

    public void initView(View rootView){
        mTabLayout = rootView.findViewById(R.id.tab_layout);
        mViewPager = rootView.findViewById(R.id.tab_viewpager);
        DataFragmentAdapter adapter = new DataFragmentAdapter(getActivity(),mTabName);
        mViewPager.setAdapter(adapter);
        new TabLayoutMediator(mTabLayout, mViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(mTabName.get(position));
            }
        }).attach();
    }

}
