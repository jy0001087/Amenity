package com.rubbersheersock.amenity.ui.amenity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.elvishew.xlog.XLog;
import com.rubbersheersock.amenity.R;
import com.rubbersheersock.amenity.room.Morpheme.Morpheme;
import com.rubbersheersock.amenity.room.Morpheme.MorphemeDatabase;

public class AmenityFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AmenityViewModel amenityViewModel =
                new ViewModelProvider(this).get(AmenityViewModel.class);

        View rootView = inflater.inflate(R.layout.fragment_amenity,container,false);

        TextView textView = rootView.findViewById(R.id.text_dashboard);
        amenityViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        //TODO:增加“发送”按钮提交功能
        init(rootView); //控件事件初始化
        Button sendButton = rootView.findViewById(R.id.morpheme_button);



        return rootView;
    }

    public void init(View rootView){
        EditText editText = rootView.findViewById(R.id.morpheme_textInput);
        //增加edittext清空事件
        editText.setOnFocusChangeListener(
                new View.OnFocusChangeListener(){
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus == true){
                            editText.setText("");
                        }
                    }
                }
        );
        //增加输入框提交逻辑
        editText.setOnEditorActionListener(
                new TextView.OnEditorActionListener(){
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        String inputedString = v.getText().toString();
                        XLog.tag("MORPHEME-EditText").i(inputedString);
                        saveMorphemeData(inputedString);
                        return false;
                    }
                }
        );
    }


    //TODO:弄到线程里去写数据
    public void saveMorphemeData(String data){
        MorphemeDatabase db = Room.databaseBuilder(getContext(),MorphemeDatabase.class,"MorphemeDatabase")
                .build();
        Morpheme mMorpheme = new Morpheme();
        mMorpheme.morphemeText=data;
        db.morphemeDao().insert(mMorpheme);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}