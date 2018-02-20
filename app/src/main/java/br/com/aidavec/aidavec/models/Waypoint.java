package br.com.aidavec.aidavec.models;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class Waypoint {
    public int way_id;
    public int usr_id;
    public double way_latitude;
    public double way_longitude;
    public String way_date;
    public double way_percorrido;

    public int getWay_id() {
        return way_id;
    }

    public void setWay_id(int way_id) {
        this.way_id = way_id;
    }

    public int getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(int usr_id) {
        this.usr_id = usr_id;
    }

    public double getWay_latitude() {
        return way_latitude;
    }

    public void setWay_latitude(double way_latitude) {
        this.way_latitude = way_latitude;
    }

    public double getWay_longitude() {
        return way_longitude;
    }

    public void setWay_longitude(double way_longitude) {
        this.way_longitude = way_longitude;
    }

    public String getWay_date() {
        return way_date;
    }

    public void setWay_date(String way_date) {
        this.way_date = way_date;
    }

    public double getWay_percorrido() {
        return way_percorrido;
    }

    public void setWay_percorrido(double way_percorrido) {
        this.way_percorrido = way_percorrido;
    }
}
