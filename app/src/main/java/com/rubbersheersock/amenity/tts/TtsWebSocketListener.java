package com.rubbersheersock.amenity.tts;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.elvishew.xlog.XLog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.HttpUrl;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class TtsWebSocketListener extends WebSocketListener {
    public Context context = null;
    public String inputtext = "";

    public TtsWebSocketListener(Context context, String inputtext) {
        this.context = context;
        this.inputtext = inputtext;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
        XLog.tag("TTS").i("websocket connection is opened!");
        String appId = "b772e2ac";
        try {
            TtsRequestJson requestJson = new TtsRequestJson.TtsJsonBuilder(appId, "lame", "aisbabyxu", inputtext)
                    .Build();
            webSocket.send(requestJson.getFrame().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
        XLog.tag("TTS").i("message received ");
        try {
            JSONObject responseJson = new JSONObject(text);
            if (responseJson.getInt("code") == 0) {
                JSONObject data = responseJson.getJSONObject("data");
                byte[] buffer = Base64.getDecoder().decode(data.getString("audio"));
                //储存文件
                File file =new File(context.getExternalFilesDir(null), inputtext + ".mp3");
                if(file.createNewFile()) {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(buffer);
                    out.close();
                    XLog.tag("TTS").i(inputtext + ".mp3 is created!");
                }else{
                    XLog.tag("TTS").e("文件创建异常");
                }
            }
        } catch (Exception e) {
            XLog.tag("TTS").e("something goes wrong while writing file ");
            e.printStackTrace();
        }
        webSocket.close(1000,"Thanks Bye!");
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        XLog.tag("TTS").e("WebSocket fail，cause by : " + response + "\n Error is " + t.getMessage());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        XLog.tag("TTS").i("WebSocket is onClosing!!");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        StringBuilder builder = new StringBuilder("host: ").append(url.getHost()).append("\n").
                append("date: ").append(date).append("\n").
                append("GET ").append(url.getPath()).append(" HTTP/1.1");
        Charset charset = Charset.forName("UTF-8");
        Mac mac = Mac.getInstance("hmacsha256");
//        XLog.tag("TTS").i("origin secrt is :" + builder);

        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(builder.toString().getBytes(charset));
        String sha = Base64.getEncoder().encodeToString(hexDigits);
//        XLog.tag("TTS").i("sha=" + sha);

        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        HttpUrl httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath()).newBuilder().
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(charset))).
                addQueryParameter("date", date).
                addQueryParameter("host", url.getHost()).
                build();
        XLog.tag("TTS-URL-HTTPS").i(httpUrl);
        return httpUrl.toString();
    }
}
