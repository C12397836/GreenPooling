package com.example.paul.greenpooling11;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class Trip {

    //protected String origin, destination;
    protected String tripId, userId, userName,origin, destination, date, time, availableSeats,detour, returnDate, returnTime;

    public Trip(){
    }

    /*@Override
    public String toString() {
        return "Trip{" +
                "tripId='" + tripId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", avaiableSeats='" + avaiableSeats + '\'' +
                ", detour='" + detour + '\'' +
                ", returnDate='" + returnDate + '\'' +
                ", returnTime='" + returnTime + '\'' +
                '}';
    }*/

    @Override
    public String toString() {
        return "Trip{" +
                "origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }

    public Trip(String origin, String destination){
        super();
        this.origin=origin;
        this.destination=destination;
    }

    public Trip(String tripId, String userId, String userName, String origin, String destination, String date, String time, String availableSeats, String detour, String returnDate, String returnTime){
        super();
        this.tripId = tripId;
        this.userId = userId;
        this.userName =userName;
        this.origin=origin;
        this.destination=destination;
        this.date=date;
        this.time=time;
        this.availableSeats=availableSeats;
        this.detour=detour;
        this.returnDate=returnDate;
        this.returnTime=returnTime;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(String availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getDetour() {
        return detour;
    }

    public void setDetour(String detour) {
        this.detour = detour;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }
}
