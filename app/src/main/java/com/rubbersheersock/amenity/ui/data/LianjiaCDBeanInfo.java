package com.rubbersheersock.amenity.ui.data;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public class LianjiaCDBeanInfo {
    public int totalHouseForSaleNumber;
    public Timestamp latestUpdateTimeStamp;
    public int newForSaleNumber;
    public List<LianjiaCDBean> newForSaleHouseList = new LinkedList<>();
    public List<LianjiaCDBean> monitorHouseList = new LinkedList<>();
}
