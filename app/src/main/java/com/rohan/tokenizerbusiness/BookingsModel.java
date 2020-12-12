package com.rohan.tokenizerbusiness;

import java.util.Date;

public class BookingsModel {
    Date Timing;
    String UserId;

    private BookingsModel(Date timing, String userId) {
        Timing = timing;
        UserId = userId;
    }

    private BookingsModel() {
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public Date getTiming() {
        return Timing;
    }

    public void setTiming(Date timing) {
        Timing = timing;
    }
}
