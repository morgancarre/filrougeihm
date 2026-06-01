package com.example.filrouge_tp3;

public class UrbanFactory implements AccidentFactory {

    @Override
    public Issue createIssue(String title, String description) {
        // Une urban → toujours MEDIUM par défaut
        Issue urbanIssue = new UrbanIssue(title, description, Issue.Priority.MEDIUM);
        urbanIssue.addObserver(EmergencyService.getInstance());
        return urbanIssue;
    }
}