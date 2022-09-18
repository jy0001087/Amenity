package com.rubbersheersock.amenity.PubTools;

import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUnit {
    private final Request request;

    private HttpUnit(Builder builder){
        this.request=builder.request;
    }

    public static class Builder{
        private final String url;
        private final String ServiceName;
        private  Request request;

        private HashMap<String,String> paramMap = new HashMap<>();

        public Builder(String url, String ServiceName){
            this.url=url;
            this.ServiceName=ServiceName;
        }

        public Builder setParamMap(HashMap<String,String> paramMap){
            this.paramMap=paramMap;
            return this;
        }

        public HttpUnit build(){
            FormBody.Builder fmBuilder = new FormBody.Builder();
            if(!(paramMap.isEmpty())){
                for(String key:paramMap.keySet()){
                    fmBuilder.add(key,paramMap.get(key));
                }
            }
            RequestBody formBody = fmBuilder.build();
            request = new Request.Builder()
                    .url(url+"/"+ServiceName)
                    .post(formBody)
                    .build();
            return new HttpUnit(this);}
    }

    public Request getRequest(){
        return this.request;
    }
}
