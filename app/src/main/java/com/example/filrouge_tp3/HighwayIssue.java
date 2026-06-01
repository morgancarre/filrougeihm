package com.example.filrouge_tp3;

import android.os.Parcel;

public class HighwayIssue extends Issue {

    public HighwayIssue(String title, String description, Priority priority) {
        super(title, description, priority);
    }

    // Constructeur Parcel pour reconstruire l'objet entre fragments
    protected HighwayIssue(Parcel in) {
        super(in);
    }

    @Override
    public String getSafetyProtocol() {
        return "Rester derrière la glissière de sécurité et contacter le 15-17-18.";
    }

    public static final Creator<HighwayIssue> CREATOR = new Creator<HighwayIssue>() {
        @Override
        public HighwayIssue createFromParcel(Parcel in) { return new HighwayIssue(in); }
        @Override
        public HighwayIssue[] newArray(int size) { return new HighwayIssue[size]; }
    };
}