package com.example.filrouge_tp3;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public abstract class Issue implements Parcelable, IssueObservable {

    public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }

    private String title;
    private String description;
    private Priority priority;
    private float status;
    private double latitude;
    private double longitude;
    private String time;
    private transient List<IssueObserver> observers = new ArrayList<>();

    public Issue(String title, String description, Priority priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = 0f;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.time = "";
        this.observers = new ArrayList<>();
    }


    protected Issue(Parcel in) {
        title = in.readString();
        description = in.readString();
        priority = Priority.valueOf(in.readString());
        status = in.readFloat();
        latitude = in.readDouble();
        longitude = in.readDouble();
        time = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(priority.name());
        dest.writeFloat(status);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(time != null ? time : "");
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void addObserver(IssueObserver observer) {
        if (this.observers == null) {
            this.observers = new ArrayList<>();
        }
        if (observer != null && !this.observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(IssueObserver observer) {
        if (this.observers != null) {
            this.observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers() {
        if (this.observers != null) {
            for (IssueObserver observer:  new ArrayList<>(this.observers)) {
                observer.onStatusChanged(this);
            }
        }
    }

    public void priorityUpdate() {
        if (this.observers != null) {
            for (IssueObserver observer: new ArrayList<>(this.observers)) {
                observer.onPriorityChanged(this);
            }
        }
    }

    // Méthode abstraite — chaque type d'incident définit son protocole
    public abstract String getSafetyProtocol();

    // Getters / Setters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTime() { return time != null ? time : ""; }
    public void setTime(String time) { this.time = time; }
    public Priority getPriority() { return priority; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public void setLocation(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public GeoPoint getGeolocation() {
        return new GeoPoint(latitude, longitude);
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
        priorityUpdate();
    }
    public float getStatus() {
        return status;

    }
    public void setStatus(float status) {
        this.status = status;
        notifyObservers();
    }

    @Override
    public String toString() { return title + " [" + priority + "]"; }
}