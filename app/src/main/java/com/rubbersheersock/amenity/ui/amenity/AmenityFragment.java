package com.rubbersheersock.amenity.ui.amenity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rubbersheersock.amenity.R;
import com.rubbersheersock.amenity.databinding.FragmentAmenityBinding;
import com.rubbersheersock.amenity.databinding.FragmentAmenityBinding;

public class AmenityFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AmenityViewModel amenityViewModel =
                new ViewModelProvider(this).get(AmenityViewModel.class);

        View rootView = inflater.inflate(R.layout.fragment_amenity,container,false);

        TextView textView = rootView.findViewById(R.id.text_dashboard);
        amenityViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        //TODO:增加“发送”按钮提交功能
        //TODO:增加输入框激活后自动清除内容功能

        Button sendButton = rootView.findViewById(R.id.morpheme_button);


        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}