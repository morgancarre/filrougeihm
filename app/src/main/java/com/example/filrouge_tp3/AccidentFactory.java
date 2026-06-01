package com.example.filrouge_tp3;

public interface AccidentFactory {
    Issue createIssue(String title, String description);

    default Issue createIssue(String title, String description, double lat, double lon) {
        Issue issue = createIssue(title, description);
        issue.setLocation(lat, lon);
        return issue;
    }
}
