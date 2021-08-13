package com.rubbersheersock.amenity.tts;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.elvishew.xlog.XLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Base64;


public class TtsRequestJson {
    public JSONObject frame;

    private TtsRequestJson(TtsJsonBuilder builder){
        this.frame=builder.frame;
    }

    public static class TtsJsonBuilder{
        private String aue;
        private Integer sfl;
        private String auf;
        private String vcn;
        private Integer speed;
        private Integer volume;
        private Integer pitch;
        private Integer bgs;
        private String tte;
        private String reg;
        private String rdn;
        private String ent;
        private String appId;
        private int status;
        private String text;

        JSONObject frame = new JSONObject();
        JSONObject common = new JSONObject();
        JSONObject business = new JSONObject();
        JSONObject data = new JSONObject();

        @RequiresApi(api = Build.VERSION_CODES.O)
        public TtsJsonBuilder(String appId, String aue, String vcn, String text) throws JSONException, UnsupportedEncodingException {
            this.appId=appId;
            this.aue=aue;
            this.vcn=vcn;
            this.status = 2;
            this.sfl=1;
            this.tte = "UTF8";
            this.text = Base64.getEncoder().encodeToString(new String(text).getBytes("utf-8"));
            common.put("app_id", this.appId);
            business.put("aue", this.aue);
            business.put("sfl", this.sfl);
            business.put("vcn", this.vcn);
            business.put("tte",this.tte);
            data.put("text",this.text);
            data.put("status",this.status);
            frame.put("common", common);
            frame.put("business", business);
            frame.put("data", data);
            XLog.i("frame = "+frame.toString());
        }

        public TtsJsonBuilder setSfl(Integer sfl) {
            this.sfl = sfl;
            return this;
        }

        public TtsJsonBuilder setAuf(String auf) {
            this.auf = auf;
            return this;
        }


        public TtsJsonBuilder setSpeed(Integer speed) {
            this.speed = speed;
            return this;
        }

        public TtsJsonBuilder setVolume(Integer volume) {
            this.volume = volume;
            return this;
        }

        public TtsJsonBuilder setPitch(Integer pitch) {
            this.pitch = pitch;
            return this;
        }

        public TtsJsonBuilder setBgs(Integer bgs) {
            this.bgs = bgs;
            return this;
        }

        public TtsJsonBuilder setTte(String tte) {
            this.tte = tte;
            return this;
        }

        public TtsJsonBuilder setReg(String reg) {
            this.reg = reg;
            return this;
        }

        public TtsJsonBuilder setRdn(String rdn) {
            this.rdn = rdn;
            return this;
        }

        public TtsJsonBuilder setEnt(String ent) {
            this.ent = ent;
            return this;
        }

        public TtsRequestJson Build(){
            return new TtsRequestJson(this);
        }

    }
}