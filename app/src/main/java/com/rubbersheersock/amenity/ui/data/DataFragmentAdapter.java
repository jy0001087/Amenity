package com.rubbersheersock.amenity.ui.data;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * 向Viewpager内填充fragment用
 */
public class DataFragmentAdapter extends FragmentStateAdapter {
    private List<String> mTabName;

    public DataFragmentAdapter(@NonNull FragmentActivity fragmentActivity, List<String> tabName) {
        super(fragmentActivity);
        this.mTabName=tabName;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return DataFragment.newInstance(mTabName.get(position));
    }

    @Override
    public int getItemCount() {
        return mTabName.size();
    }
}
