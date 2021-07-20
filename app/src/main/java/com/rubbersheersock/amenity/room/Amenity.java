package com.rubbersheersock.amenity.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Entity
public class Amenity {
    @PrimaryKey(autoGenerate = true)
    public int key;
    public ZonedDateTime createTime;
    public String valuetitle;
    public Long value;   //单次训练时长
    public Long totaltime;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public void setValuetitle(String valuetitle) {
        this.valuetitle = valuetitle;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public void setTotaltime(Long totaltime) {
        this.totaltime = totaltime;
    }

    public String getValuetitle() {
        return valuetitle;
    }

    public Long getValue() {
        return value;
    }

    public Long getTotaltime() {
        return totaltime;
    }
}
