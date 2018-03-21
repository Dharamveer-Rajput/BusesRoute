package com.busesroute.response.StopsSuccess;

import android.widget.TextView;

/**
 * Created by dharamveer on 8/3/18.
 */

public class StopsSuccess {


    private int idStops;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdStopsToShow() {
        return idStopsToShow;
    }

    public void setIdStopsToShow(int idStopsToShow) {
        this.idStopsToShow = idStopsToShow;
    }

    private int idStopsToShow;
    private String stopsTitle, stopsLat, stopsLng;

    public int getIdStops() {
        return idStops;
    }

    public void setIdStops(int idStops) {
        this.idStops = idStops;
    }

    public String getStopsTitle() {
        return stopsTitle;
    }

    public void setStopsTitle(String stopsTitle) {
        this.stopsTitle = stopsTitle;
    }

    public String getStopsLat() {
        return stopsLat;
    }

    public void setStopsLat(String stopsLat) {
        this.stopsLat = stopsLat;
    }

    public String getStopsLng() {
        return stopsLng;
    }

    public void setStopsLng(String stopsLng) {
        this.stopsLng = stopsLng;
    }
}

