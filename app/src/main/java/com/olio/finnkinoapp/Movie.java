package com.olio.finnkinoapp;

public class Movie {
    String title, eventID, startTime, endTime, prodYear, lengthMin;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime.substring(11,startTime.length()-3);
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime.substring(11,endTime.length()-3);
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public void setLengthMin(String lengthMin) {
        this.lengthMin = lengthMin;
    }

    public void setProdYear(String prodYear) {
        this.prodYear = prodYear;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getEventID() {
        return eventID;
    }

    public String getLengthMin() {
        return lengthMin;
    }

    public String getProdYear() {
        return prodYear;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getTitle() {
        return title;
    }
}
