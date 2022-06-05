package com.rubbersheersock.amenity.PubTools;

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
        private final Request request;

        public Builder(String url,String ServiceName){
            this.url=url;
            this.ServiceName=ServiceName;
            RequestBody formBody = new FormBody.Builder().build();
            request = new Request.Builder()
                    .url(url+"/"+ServiceName)
                    .post(formBody)
                    .build();
        }

        public HttpUnit build(){return new HttpUnit(this);}
    }

    public Request getRequest(){
        return this.request;
    }
}
