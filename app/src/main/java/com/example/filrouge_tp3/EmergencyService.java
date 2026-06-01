package com.example.filrouge_tp3;

import android.util.Log;

public class EmergencyService implements IssueObserver {
    private static final String TAG = "EmergencyService";
    private static EmergencyService instance;

    private EmergencyService() {}

    public static synchronized EmergencyService getInstance() {
        if (instance == null) {
            instance = new EmergencyService();
        }
        return instance;
    }

    @Override
    public void onStatusChanged(Issue issue) {
        Log.d(TAG,
                "Incident: " + issue.getTitle() + " | Nouveau statut: " + issue.getStatus());
    }

    @Override
    public void onPriorityChanged(Issue issue) {
        Log.d(TAG,
                "Incident: " + issue.getTitle() + " | Nouvelle priorité: " + issue.getPriority());
    }
}
