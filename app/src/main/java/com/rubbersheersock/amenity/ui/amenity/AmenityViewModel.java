package com.rubbersheersock.amenity.ui.amenity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AmenityViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AmenityViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}