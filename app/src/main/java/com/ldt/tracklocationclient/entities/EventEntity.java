package com.ldt.tracklocationclient.entities;

/**
 * Created by ldt on 9/12/2017.
 */

public class EventEntity {
    private String summary;
    private long startTime;
    private long endTime;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
