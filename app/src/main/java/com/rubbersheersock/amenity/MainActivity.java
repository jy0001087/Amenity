package com.rubbersheersock.amenity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.rubbersheersock.amenity.Services.ScreenObserver;
import com.rubbersheersock.amenity.Services.ScreenObserverService;
import com.rubbersheersock.amenity.room.Amenity;
import com.rubbersheersock.amenity.room.AmenityDataProcesser;
import com.rubbersheersock.amenity.room.AmenityDatabase;
import com.rubbersheersock.amenity.tts.TtsWebSocketListener;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    TextView tx_lashen;
    TextView textviewGSM;
    TextView textviewGPS;
    mHandler handler = new mHandler();
    gsmHandler handlergsm = new gsmHandler();
    gpsHandler handlergps = new gpsHandler();

    //声明基站获取变量
    private TelephonyManager tm = null;


    class mHandler extends Handler {
        // 通过复写handlerMessage() 从而确定更新UI的操作
        @Override
        public void handleMessage(Message msg) {
            XLog.i("handler 被执行了");
            tx_lashen.setText(msg.obj.toString());
        }
    }

    class gsmHandler extends Handler {
        // 通过复写handlerMessage() 从而确定更新UI的操作
        @Override
        public void handleMessage(Message msg) {
            XLog.i("handler 被执行了");
            textviewGSM.setText(msg.obj.toString());
        }
    }

    class gpsHandler extends Handler {
        // 通过复写handlerMessage() 从而确定更新UI的操作
        @Override
        public void handleMessage(Message msg) {
            XLog.i("handler 被执行了");
            textviewGPS.setText(msg.obj.toString());
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

        //启动后台服务
        final Intent intent = new Intent(this, ScreenObserverService.class);
        startService(intent);
    //设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //控件处理
        ch = (Chronometer) findViewById(R.id.chronometer);
        bt = (Button) findViewById(R.id.startbutton);
        tx_lashen = (TextView) findViewById(R.id.textViewLASHEN);
        textviewGSM = (TextView) findViewById(R.id.GSMtextView);
        textviewGPS = (TextView) findViewById(R.id.GPStextView);

        //更新记录区域
        refreshTextView();

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
                    while (insertThread.isAlive()) {
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
                    XLog.i("I'm been boomed!!" + getChronometerSeconds(ch));
                    startAlarm(getApplicationContext(), getChronometerSeconds(ch) + "");
                }
            }
        });

        returnGMSinfo();
        getLocationFigure();
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
                message.what = 1;
                message.obj = getTextViewContent("textViewLASHEN");
                handler.sendMessage(message);
            }
        }).start();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getTextViewContent(String updateview) {
        String content = "0 秒";
        long duration = 0;
        int frequency = 0;
        Amenity[] amenities = AmenityQuery();
        XLog.i("There is " + amenities.length + " 条记录");
        for (int i = 0; i < amenities.length; i++) {
            switch (updateview) {
                case "textViewLASHEN":
                    if (amenities[i].getCreateTime().toLocalDate().equals(LocalDate.now())) {
                        duration = duration + amenities[i].getValue();
                        frequency++;
                    }
                    break;
                default:
                    content = "updatezone is undefaulted!";
            }
        }
        content = frequency + " 次，共计 " + duration / 60 + " 分钟 " + duration % 60 + " 秒";
        return content;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startAlarm(Context context, String inputtext) {
        String filepath = context.getExternalFilesDir(null).getPath() + "/" + inputtext + "秒.mp3";
        XLog.i("filepath = " + filepath);
        final MediaPlayer mediaPlayer = new MediaPlayer();
        Thread playerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(filepath));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException notfound) {
                    XLog.tag("media").i("不存在" + inputtext + "秒.mp3");
                    try {
                        wegSocketConnect(context, inputtext + "秒");
                    } catch (Exception e) {
                        XLog.tag("TTS-websocket").e(e.getStackTrace());
                    }
                } catch (Exception e) {
                    XLog.e("Media Player is not going well!");
                    e.printStackTrace();
                }
            }
        });
        playerThread.start();
        while (!(playerThread.isAlive())) {
            mediaPlayer.release();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void wegSocketConnect(Context context, String inputtext) throws Exception {
        String host = "https://tts-api.xfyun.cn/v2/tts";
        String apiKey = "18487e76e7cd9e796da89411ba0af5ef";
        String apiSecret = "NzZmOGNhMmRhNjE4Mjc2N2Q2MDEwMGRh";

        TtsWebSocketListener listener = new TtsWebSocketListener(context, inputtext);
        Request request = new Request.Builder()
                .url(listener.getAuthUrl(host, apiKey, apiSecret)
                        .replace("https://", "wss://"))
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newWebSocket(request, listener);
    }

    private void returnGMSinfo() {
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        XLog.tag("GSM").i("mcc+mnc -- " + tm.getNetworkOperator() + "|" + tm.getNetworkType());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
        if (location != null) {
            XLog.tag("GSM").i("mcc+mnc -- " + tm.getNetworkOperator() + " | " + location.getLac() + " | " + location.getCid());
            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    message.obj = "mcc+mnc=" + tm.getNetworkOperator() + "; LAC=" + location.getLac() + "; CID=" + location.getCid();
                    handlergsm.sendMessage(message);
                }
            }).start();
        } else {
            XLog.tag("GSM").e("Get GSMcellinfo failed!");
        }
    }

    private void getLocationFigure() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,      //GPS定位提供者
                1000,       //更新数据时间为1秒
                1,      //位置间隔为1米                //位置监听器
                new LocationListener() {  //GPS定位信息发生改变时触发，用于更新位置信息
                    @Override
                    public void onLocationChanged(Location location) {                        //GPS信息发生改变时，更新位置                        locationUpdates(location);
                    }

                    @Override                    //位置状态发生改变时触发
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override                    //定位提供者启动时触发
                    public void onProviderEnabled(String provider) {
                    }

                    @Override                    //定位提供者关闭时触发
                    public void onProviderDisabled(String provider) {
                    }
                });        //从GPS获取最新的定位信息
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            StringBuilder stringBuilder = new StringBuilder();        //使用StringBuilder保存数据            //获取经度、纬度、等属性值
            stringBuilder.append("您的位置信息：\n");
            stringBuilder.append("经度：");
            stringBuilder.append(location.getLongitude());
            stringBuilder.append("\n纬度：");
            stringBuilder.append(location.getLatitude());//            stringBuilder.append("\n精确度：");//            stringBuilder.append(location.getAccuracy());//            stringBuilder.append("\n高度：");//            stringBuilder.append(location.getAltitude());//            stringBuilder.append("\n方向：");//            stringBuilder.append(location.getBearing());//            stringBuilder.append("\n速度：");//            stringBuilder.append(location.getSpeed());//            stringBuilder.append("\n时间：");//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH mm ss");    //设置日期时间格式//            stringBuilder.append(dateFormat.format(new Date(location.getTime())));
            XLog.tag("GPS").i(stringBuilder);
            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    message.obj = stringBuilder;
                    handlergps.sendMessage(message);
                }
            }).start();
        }
        else{
            XLog.tag("GPS").e("Get location failed!!");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locationListener);
        }
    }
    private final LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            XLog.tag("GPS").i("location update request !!");
            if (location != null) {
                StringBuilder stringBuilder = new StringBuilder();        //使用StringBuilder保存数据            //获取经度、纬度、等属性值
                stringBuilder.append("您的位置信息：\n");
                stringBuilder.append("经度：");
                stringBuilder.append(location.getLongitude());
                stringBuilder.append("\n纬度：");
                stringBuilder.append(location.getLatitude());//            stringBuilder.append("\n精确度：");//            stringBuilder.append(location.getAccuracy());//            stringBuilder.append("\n高度：");//            stringBuilder.append(location.getAltitude());//            stringBuilder.append("\n方向：");//            stringBuilder.append(location.getBearing());//            stringBuilder.append("\n速度：");//            stringBuilder.append(location.getSpeed());//            stringBuilder.append("\n时间：");//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH mm ss");    //设置日期时间格式//            stringBuilder.append(dateFormat.format(new Date(location.getTime())));
                XLog.tag("GPS").i(stringBuilder);
                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 1;
                        message.obj = stringBuilder;
                        handlergps.sendMessage(message);
                    }
                }).start();
            }
        }
    };

}