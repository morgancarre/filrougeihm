package com.example.filrouge_tp3;

import java.util.ArrayList;
import java.util.List;

public class IssueManager implements ModelObservable {

    private static IssueManager instance;

    private final List<Issue> issues = new ArrayList<>();
    private final List<Issue> reportedIssues = new ArrayList<>();
    private final List<ViewObserver> views = new ArrayList<>();
    private Issue lastReportedIssue;

    private IssueManager() {
        createIssues();
    }

    public static synchronized IssueManager getInstance() {
        if (instance == null) {
            instance = new IssueManager();
        }
        return instance;
    }

    private void createIssues() {
        HighwayFactory hf = new HighwayFactory();
        UrbanFactory   uf = new UrbanFactory();

        issues.add(hf.createIssue("Accident mortel",       "Collision impliquant 3 véhicules",      43.6156, 7.0718));
        issues.add(hf.createIssue("Véhicule à contresens", "Signalé sur l'autoroute A9",            43.6156, 7.0718));
        issues.add(hf.createIssue("Obstacle sur chaussée", "Débris après tempête",                  43.6156, 7.0718));
        issues.add(hf.createIssue("Bouchon massif",        "15 km de ralentissement",               43.6156, 7.0718));
        issues.add(hf.createIssue("Présence de verglas",   "Route glissante section nord",          43.6156, 7.0718));
        issues.add(uf.createIssue("Panne de signalisation","Feux tricolores éteints carrefour",     43.6156, 7.0718));
        issues.add(uf.createIssue("Travaux de nuit",       "Fermeture partielle voie droite",       43.6156, 7.0718));
        issues.add(uf.createIssue("Brouillard givrant",    "Visibilité réduite à 50m",              43.6156, 7.0718));
        issues.add(uf.createIssue("Inondation chaussée",   "Eau sur la voie après les pluies",      43.6156, 7.0718));
        issues.add(uf.createIssue("Animal errant",         "Chevreuil signalé sur la D7",           43.6156, 7.0718));
        issues.add(uf.createIssue("Nid-de-poule profond",  "Dégradation importante du revêtement",  43.6156, 7.0718));
        issues.add(uf.createIssue("Véhicule en panne",     "Voiture arrêtée sur bande d'arrêt",     43.6156, 7.0718));
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public List<Issue> getReportedIssues() {
        return reportedIssues;
    }

    public Issue getLastReportedIssue() { return lastReportedIssue; }

    public void addReportedIssue(Issue issue) {
        lastReportedIssue = issue;
        if (!reportedIssues.contains(issue)) {
            reportedIssues.add(issue);
            notifyViews();
        }
    }

    public void setLocation(Issue issue, double lat, double lon) {
        issue.setLocation(lat, lon);
        notifyViews();
    }

    @Override
    public void addView(ViewObserver view) {
        if (!views.contains(view)) views.add(view);
    }

    @Override
    public void removeView(ViewObserver view) {
        views.remove(view);
    }

    @Override
    public void notifyViews() {
        for (ViewObserver v : new ArrayList<>(views)) {
            v.onModelChanged(this);
        }
    }
}
