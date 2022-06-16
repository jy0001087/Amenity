package com.rubbersheersock.amenity.ui.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LianjiaCDBeanInfo {
    public int totalHouseForSaleNumber;
    public Timestamp latestUpdateTimeStamp;
    public int newForSaleNumber;
    public int soldNumber;
    public ArrayList<LianjiaCDBean> newForSaleHouseList = new ArrayList<>();
    public ArrayList<LianjiaCDBean> monitorHouseList = new ArrayList<>();
    public ArrayList<LianjiaCDBean> soldHouseList = new ArrayList<>();
}
