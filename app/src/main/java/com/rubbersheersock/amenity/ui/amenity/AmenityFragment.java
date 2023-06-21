package com.rubbersheersock.amenity.ui.amenity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
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

import com.elvishew.xlog.XLog;
import com.rubbersheersock.amenity.R;
import com.rubbersheersock.amenity.room.Morpheme.Morpheme;
import com.rubbersheersock.amenity.room.Morpheme.MorphemeDatabase;

import java.util.List;

public class AmenityFragment extends Fragment {
    private static final String LOGTAG = "Morpheme";
    private static final int INSERT = 1;  //查询结果
    private static final String SEPARATOR = "-";
    //定义数据存储handler
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch(msg.what) {
                case INSERT:
                XLog.tag(LOGTAG).d(msg.getData().getString("RESULTSTRING"));
                break;
            }
        }
    };


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AmenityViewModel amenityViewModel =
                new ViewModelProvider(this).get(AmenityViewModel.class);

        View rootView = inflater.inflate(R.layout.fragment_amenity, container, false);

        TextView textView = rootView.findViewById(R.id.text_dashboard);
        amenityViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        //TODO:增加“发送”按钮提交功能
        //TODO:仅输入词根，开启查询功能
        init(rootView); //控件事件初始化
        Button sendButton = rootView.findViewById(R.id.morpheme_button);


        return rootView;
    }

    public void init(View rootView) {
        EditText editText = rootView.findViewById(R.id.morpheme_textInput);
        //增加edittext清空事件
        editText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus == true) {
                            editText.setText("");
                        }
                    }
                }
        );
        //增加输入框提交逻辑
        editText.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        String inputedString = v.getText().toString();
                        XLog.tag(LOGTAG).i(inputedString);
                        saveMorphemeDataBase(inputedString);
                        return false;
                    }
                }
        );
    }


    //TODO:弄到线程里去处理数据
    public void saveMorphemeDataBase(String data) {
        MorphemeDatabase db = MorphemeDatabase.getDataBase(getContext());
        Morpheme mMorpheme = new Morpheme();
        String[] dataSeparateMorpheme = data.split(" ");
        mMorpheme.morphemeText= dataSeparateMorpheme[0];
        String[] mMeanings= dataSeparateMorpheme[1].split(",");
        if((mMorpheme.morphemeText!=null && mMorpheme.morphemeText.length()!=0)
        && mMeanings.length != 0){        //词根更新流程
            MorphemeDatabase.writeExecutor.execute(new Runnable() {
                                                       @Override
                                                       public void run() {
                                                           for(int i= 0;i<mMeanings.length;i++){
                                                               List<Morpheme> mMorphemeList ;
                                                               mMorphemeList = db.morphemeDao().getByMorphemeTextLikeMeaning(mMorpheme.morphemeText,mMeanings[i]);
                                                               if(mMorphemeList.size()==0){ //如果输入词根+辞意组合不存在，查询词根是否存在
                                                                   mMorphemeList = db.morphemeDao().getByMorphemeText(mMorpheme.morphemeText);
                                                                   if(mMorphemeList.size()>0){ //如果词根存在，则增补辞意
                                                                       mMorpheme.morphemeMeaning = mMeanings[i]+","+mMorphemeList.get(0).morphemeMeaning;
                                                                       db.morphemeDao().update(mMorpheme);
                                                                   }else{ // 词根不存在，就增加词根
                                                                       db.morphemeDao().insert(mMorpheme);
                                                                   }
                                                               }
                                                           }
                                                           XLog.tag(LOGTAG).d(mMorpheme.morphemeText);
                                                       }
                                                   }
            );
        }else if (isEnglish(mMorpheme.morphemeText)){ //输入是英文，按英文查词根
            MorphemeDatabase.queryExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    List<Morpheme> rMorphemeList = db.morphemeDao().getByMorphemeText(mMorpheme.morphemeText);
                    Message msg = new Message();
                    msg.what=1;
                    Bundle data = new Bundle();
                    data.putString("RESULTSTRING",mMorpheme.morphemeText+SEPARATOR+mMorpheme.morphemeMeaning);
                    msg.setData(data);
                    mHandler.sendMessage(msg);
                }
            });
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public static boolean isEnglish(String p) {
        byte[] bytes = p.getBytes();
        int i = bytes.length;//i为字节长度
        int j = p.length();//j为字符长度
        return i == j;
    }
}