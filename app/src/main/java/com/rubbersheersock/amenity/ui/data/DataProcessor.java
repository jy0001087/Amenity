package com.rubbersheersock.amenity.ui.data;

import com.elvishew.xlog.XLog;

import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

public class DataProcessor {

    //json转javabean，更好操作
    public static ArrayList<LianjiaCDBean> getBeanList(JSONObject json){
        ArrayList<LianjiaCDBean> beanList= new ArrayList<>();
        Iterator<String> it = json.keys();
        while(it.hasNext()) {
            LianjiaCDBean bean = new LianjiaCDBean();
            try {
                JSONObject innerJson = new JSONObject(json.get(it.next()).toString());
                Iterator<String> innerIt = innerJson.keys();
                while(innerIt.hasNext()){
                    String key = innerIt.next();
                    switch(key){
                        case "houseid":
                            bean.houseid=innerJson.getString(key);
                            break;
                        case "title":
                            bean.title=innerJson.getString(key);
                            break;
                        case "housetype":
                            bean.housetype=innerJson.getString(key);
                            break;
                        case "proportion":
                            bean.proportion=Float.valueOf(innerJson.getString(key));
                            break;
                        case "orientation":
                            bean.orientation=innerJson.getString(key);
                            break;
                        case "decoration":
                            bean.decoration=innerJson.getString(key);
                            break;
                        case "floor":
                            bean.floor=innerJson.getString(key);
                            break;
                        case "followinfo":
                            bean.followinfo=innerJson.getString(key);
                            break;
                        case "taxfree":
                            bean.taxfree=innerJson.getString(key);
                            break;
                        case "price":
                            bean.price=Float.valueOf(innerJson.getString(key));
                            break;
                        case "updatedate":
                            if(innerJson.getString(key) != null & !(innerJson.getString(key).equals("null"))) {
                                bean.updatedate = new Timestamp(Long.valueOf(innerJson.getString(key)));
                            }else{
                                bean.updatedate = null;
                            }
                            break;
                        case "fetchdate":
                            bean.fetchdate= new Timestamp(Long.valueOf(innerJson.getString(key)));
                            break;
                        case "status":
                            bean.status=innerJson.getString(key);
                            break;
                        case "url":
                            bean.url=innerJson.getString(key);
                            break;
                    }
                }
            }catch(Exception e){
                XLog.tag("DBunit").e("An error occurred while converting string to json",e);
            }
            beanList.add(bean);
        }
        return beanList;
    }

    //brief上面的beanlist内容
    public static LianjiaCDBeanInfo getHouseInfo(ArrayList<LianjiaCDBean> beanList){
        LianjiaCDBeanInfo beanInfo = new LianjiaCDBeanInfo();
        beanInfo.totalHouseForSaleNumber=beanList.size();
        beanInfo.newForSaleNumber=0;
        beanInfo.latestUpdateTimeStamp = new Timestamp(1305687917);
        for(int i=0;i<beanList.size();i++) {
            if(beanList.get(i).updatedate==null){
                beanInfo.newForSaleNumber++;
                beanInfo.newForSaleHouseList.add(beanList.get(i));
            }else if(beanInfo.latestUpdateTimeStamp.before(beanList.get(i).updatedate)) {
                beanInfo.latestUpdateTimeStamp = beanList.get(i).updatedate;
            }
        }

        return beanInfo;
    }
}
