package com.rubbersheersock.amenity.ui.amenity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rubbersheersock.amenity.databinding.FragmentAmenityBinding;
import com.rubbersheersock.amenity.databinding.FragmentAmenityBinding;

public class AmenityFragment extends Fragment {

    private FragmentAmenityBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AmenityViewModel amenityViewModel =
                new ViewModelProvider(this).get(AmenityViewModel.class);

        binding = FragmentAmenityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        amenityViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}