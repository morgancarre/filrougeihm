package com.example.filrouge_tp3;

public interface ModelObservable {
    void addView(ViewObserver view);
    void removeView(ViewObserver view);
    void notifyViews();
}
