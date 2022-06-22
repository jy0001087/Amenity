package com.rubbersheersock.amenity.ui.data;

import android.icu.text.Edits;

import com.elvishew.xlog.XLog;

import org.json.JSONObject;

import java.sql.Array;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataProcessor {
    public static final String PAG1="重点关注";
    public static final String PAG2="近期上新";
    public static final String PAG3="下架/已售";
    public static final String PAG4="全部房源";
    public static final String PAG5="全市概况";
    public static final String CHART_TITLE = "住宅出售数量";

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
                        case "originalPrice":
                            bean.originalPrice=Float.valueOf(innerJson.getString(key));
                            break;
                        case "originalFetchdate":
                            if(innerJson.getString(key) != null & !(innerJson.getString(key).equals("null"))) {
                                bean.originalUpdatedate = new Timestamp(Long.valueOf(innerJson.getString(key)));
                            }else{
                                bean.originalUpdatedate = null;
                            }
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
    public static LianjiaCDBeanInfo getHouseInfo(ArrayList<LianjiaCDBean> beanList,String param){
        LianjiaCDBeanInfo beanInfo = new LianjiaCDBeanInfo();
        beanInfo.totalHouseForSaleNumber=beanList.size();
        beanInfo.newForSaleNumber=0;
        beanInfo.soldNumber=0;
        beanInfo.latestUpdateTimeStamp = new Timestamp(1305687917);
        for(int i=0;i<beanList.size();i++) {
            LianjiaCDBean bean = beanList.get(i);
            switch(param){
                case DataProcessor.PAG1:
                    //捕获目标房屋信息65平左右
                    if(bean.proportion>65 & bean.proportion<66){
                        beanInfo.monitorHouseList.add(bean);}
                    break;
                case DataProcessor.PAG2:
                if((bean.updatedate==null)||((bean.fetchdate.getTime()+7*24*60*60*1000)>System.currentTimeMillis())){
                    beanInfo.monitorHouseList.add(bean);}
                    break;
                case DataProcessor.PAG3:
                    if(bean.updatedate!=null&&((bean.updatedate.getTime()+24*60*60*2*1000)<System.currentTimeMillis())){
                        beanInfo.monitorHouseList.add(bean);}
                    break;
                case DataProcessor.PAG4:
                    beanInfo.monitorHouseList.add(bean);
                    break;
            }
            if(bean.updatedate!=null&&beanInfo.latestUpdateTimeStamp.before(bean.updatedate)) { //获取房屋最近更新时间
                beanInfo.latestUpdateTimeStamp = bean.updatedate;
            }
        }

        return beanInfo;
    }

    //将realestate表数据由json转为map
    public static  HashMap<Date,RealEstateInfoBean> getRealEstateInfo(JSONObject json){
        HashMap<Date,RealEstateInfoBean> rBean = new HashMap<>();
        Iterator<String> it = json.keys();
        while(it.hasNext()){
            String key = it.next();
            try {
               JSONObject innerJson = new JSONObject(json.get(key).toString());
               Iterator<String> innerIt = innerJson.keys();
               RealEstateInfoBean innerBean  = new RealEstateInfoBean();
               while(innerIt.hasNext()){

                   String innerKey = innerIt.next();
                   switch(innerKey){
                       case "area_name":
                           innerBean.area_name=innerJson.getString(innerKey);
                           break;
                       case "city_name":
                           innerBean.city_name=innerJson.getString(innerKey);
                           break;
                       case "record_time":
                           innerBean.record_time=new Date(Long.valueOf(innerJson.getString(innerKey)));
                           break;
                       case "record_date":
                           innerBean.record_date=new Date(Long.valueOf(innerJson.getString(innerKey)));
                           break;
                       case "outSkirts_total_area_proportion":
                           innerBean.outSkirts_total_area_proportion = innerJson.getString(innerKey);
                           break;
                       case "outSkirts_residiential_house_num":
                           innerBean.outSkirts_residiential_house_num = innerJson.getString(innerKey);
                           break;
                       case "outSkirts_residiential_house_proportion":
                           innerBean.outSkirts_residiential_house_proportion = innerJson.getString(innerKey);
                           break;
                       case "outSkirts_not_residiential_house_proportion":
                           innerBean.outSkirts_not_residiential_house_proportion = innerJson.getString(innerKey);
                           break;
                       case "downTown_total_area_proportion":
                           innerBean.downTown_total_area_proportion = innerJson.getString(innerKey);
                           break;
                       case "downTown_residiential_house_num":
                           innerBean.downTown_residiential_house_num = innerJson.getString(innerKey);
                           break;
                       case "downTown_residiential_house_proportion":
                           innerBean.downTown_residiential_house_proportion = innerJson.getString(innerKey);
                           break;
                       case "downTown_not_residiential_house_proportion":
                           innerBean.downTown_not_residiential_house_proportion = innerJson.getString(innerKey);
                           break;
                       case "wholeUrban_total_area_proportion":
                           innerBean.wholeUrban_total_area_proportion = innerJson.getString(innerKey);
                           break;
                       case "wholeUrban_residiential_house_num":
                           innerBean.wholeUrban_residiential_house_num = innerJson.getString(innerKey);
                           break;
                       case "wholeUrban_residiential_house_proportion":
                           innerBean.wholeUrban_residiential_house_proportion = innerJson.getString(innerKey);
                           break;
                       case "wholeUrban_not_residiential_house_proportion":
                           innerBean.wholeUrban_not_residiential_house_proportion = innerJson.getString(innerKey);
                           break;
                   }

               }
               //剔除周末数据
                Date mdate = new Date(Long.valueOf(key));
                Calendar cal = Calendar.getInstance();
                cal.setTime(mdate);
               if(cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY & cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                   rBean.put(new Date(Long.valueOf(key)), innerBean);
               }
            }catch(Exception e){
            XLog.tag("amenity").e("Error has been occurred , while transform realEstate-json to realEstateInfoBean.",e);
            }
        }
        return rBean;
    }

    public static ArrayList<Date> getOrderedXAxis(HashMap<Date,RealEstateInfoBean> mMap){
        ArrayList<Date> orderedXAxis = new ArrayList<>();
        for(Map.Entry<Date,RealEstateInfoBean> entry:mMap.entrySet()){
            orderedXAxis.add(entry.getKey());
        }
        Collections.sort(orderedXAxis);
        return orderedXAxis;
    }
}
