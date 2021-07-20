package com.rubbersheersock.amenity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.rubbersheersock.amenity.room.Amenity;
import com.rubbersheersock.amenity.room.AmenityDataProcesser;
import com.rubbersheersock.amenity.room.AmenityDatabase;
import com.rubbersheersock.amenity.tts.TtsWebSocketListener;


import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    TextView tx_lashen;
    mHandler handler = new mHandler();

    class mHandler extends Handler {
        // 通过复写handlerMessage() 从而确定更新UI的操作
        @Override
        public void handleMessage(Message msg) {
            XLog.i("handler 被执行了");
            tx_lashen.setText(msg.obj.toString());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Chronometer ch;
        Button bt;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XLog.init(LogLevel.ALL);
        XLog.i("Activity onCreate");

        //设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //控件处理
        ch = (Chronometer) findViewById(R.id.chronometer);
        bt = (Button) findViewById(R.id.startbutton);
        tx_lashen = (TextView) findViewById(R.id.textViewLASHEN);

        //更新记录区域
        refreshTextView();

        //建立ws链接
        try {
            wegSocketConnect();
        } catch (Exception e) {
            XLog.tag("TTS").e(e.getStackTrace());
        }

        //按钮事件处理
        bt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if ((bt.getText()).equals("start")) {
                    ch.setBase(SystemClock.elapsedRealtime());
                    ch.start();
                    bt.setText("stop");
                } else {
                    ch.stop();
                    //new新线程向room写入记录
                    CharSequence value = ch.getContentDescription();
                    Thread insertThread = new Thread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            AmenityInsert(new Long(getChronometerSeconds(ch)), "拉伸");
                        }
                    });
                    insertThread.start();
                    //数据插入未结束前，时阻断程序执行
                    while(insertThread.isAlive()){
                        XLog.i("insert is still running!");
                    }
                    bt.setText("start");
                }
                //通知主线程更新textViewLASHEN内容
                refreshTextView();
            }
        });

        //计时器每十秒触发一次发声
        ch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (getChronometerSeconds(ch) % 10 == 0) {
                    XLog.i("I'm been boomed!!" + ZonedDateTime.now());
                    startAlarm(getApplicationContext());
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void AmenityInsert(Long value, String title) {
        AmenityDatabase db = AmenityDataProcesser.getAmenityBuilder(getApplicationContext());
        //数据库类操作
        Amenity amenity = new Amenity();
        amenity.setCreateTime(ZonedDateTime.now());
        amenity.setValue(value);
        amenity.setValuetitle(title);
        db.amenityDao().insertAll(amenity);
        XLog.i("写入一行" + amenity.getCreateTime() + "---" + amenity.getValue());
    }

    public Amenity[] AmenityQuery() {
        AmenityDatabase db = AmenityDataProcesser.getAmenityBuilder(getApplicationContext());
        Amenity[] amenities = db.amenityDao().loadAllAmenitys();
        return amenities;
    }


    //获取chronometer，XX minute XX 秒 格式化为XX秒
    public static int getChronometerSeconds(Chronometer ch) {
        Integer Totaldigit = 0;
        String content = ch.getContentDescription().toString();
        String[] contents = content.split(",");

        for (int i = 0; i < contents.length; i++) {
            Integer digit = Integer.parseInt(contents[i].replaceAll("\\D", ""));
            if (contents[i].contains("minutes") || contents[i].contains("minute")) {
                digit = digit * 60;
            }
            Totaldigit = Totaldigit + digit;
            XLog.i("contents is " + contents[i] + "  ||  digit is " + Totaldigit);
        }
        return Totaldigit;
    }

    //更新文字域
    public void refreshTextView() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                Message message = new Message();
                message.what=1;
                message.obj=getTextViewContent("textViewLASHEN");
                handler.sendMessage(message);
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getTextViewContent(String updateview){
        String content="0 秒";
        long duration = 0;
        int frequency = 0;
        Amenity[] amenities = AmenityQuery();
        XLog.i("There is " + amenities.length + " 条记录");
        for(int i=0;i<amenities.length;i++) {
            switch (updateview) {
                case "textViewLASHEN":
                    if(amenities[i].getCreateTime().toLocalDate().equals(LocalDate.now())){
                        duration = duration+amenities[i].getValue();
                        frequency++;
                    }
                    break;
                default:
                    content = "updatezone is undefaulted!";
            }
        }
        content = frequency + " 次，共计 " +duration/60 +" 分钟 "+ duration%60 +" 秒";
        return content;
    }

    private static void startAlarm(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (notification == null) return;
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }

    String host = "https://tts-api.xfyun.cn/v2/tts";
    String apiKey="18487e76e7cd9e796da89411ba0af5ef";
    String apiSecret="NzZmOGNhMmRhNjE4Mjc2N2Q2MDEwMGRh";

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void wegSocketConnect() throws Exception {
        TtsWebSocketListener listener = new TtsWebSocketListener();
        Request request = new Request.Builder()
                .url(listener.getAuthUrl(host,apiKey,apiSecret)
                        .replace("https://","wss://"))
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newWebSocket(request, listener);
    }

}