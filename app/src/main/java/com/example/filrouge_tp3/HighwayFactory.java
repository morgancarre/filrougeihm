package com.example.filrouge_tp3;

public class HighwayFactory implements AccidentFactory {

    @Override
    public Issue createIssue(String title, String description) {
        // Une autoroute → toujours CRITICAL par défaut
        Issue highwayIssue = new HighwayIssue(title, description, Issue.Priority.CRITICAL);
        highwayIssue.addObserver(EmergencyService.getInstance());
        return highwayIssue;
    }
}