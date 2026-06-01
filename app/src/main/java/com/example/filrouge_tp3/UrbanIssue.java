package com.example.filrouge_tp3;

import android.os.Parcel;

public class UrbanIssue extends Issue {

    public UrbanIssue(String title, String description, Priority priority) {
        super(title, description, priority);
    }

    protected UrbanIssue(Parcel in) {
        super(in);
    }

    @Override
    public String getSafetyProtocol() {
        return "Sécuriser le périmètre et prévenir les riverains. Contacter la mairie.";
    }

    public static final Creator<UrbanIssue> CREATOR = new Creator<UrbanIssue>() {
        @Override
        public UrbanIssue createFromParcel(Parcel in) { return new UrbanIssue(in); }
        @Override
        public UrbanIssue[] newArray(int size) { return new UrbanIssue[size]; }
    };
}