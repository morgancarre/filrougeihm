package com.example.filrouge_tp3;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class IssueController {

    private final IssueManager model;

    public IssueController(IssueManager model) {
        this.model = model;
    }

    public void controlMarker(Issue issue, Marker marker) {
        marker.setOnMarkerClickListener((m, mapView) -> {
            if (m.isInfoWindowShown()) {
                m.closeInfoWindow();
            } else {
                m.showInfoWindow();
            }
            mapView.invalidate();
            return true;
        });

        marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override public void onMarkerDrag(Marker m) {}
            @Override public void onMarkerDragStart(Marker m) {}
            @Override
            public void onMarkerDragEnd(Marker m) {
                model.setLocation(issue,
                        m.getPosition().getLatitude(),
                        m.getPosition().getLongitude());
            }
        });
    }
}
