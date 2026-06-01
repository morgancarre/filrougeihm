package com.example.filrouge_tp3;

public interface IssueObserver {
    void onStatusChanged(Issue issue);
    void onPriorityChanged(Issue issue);
}