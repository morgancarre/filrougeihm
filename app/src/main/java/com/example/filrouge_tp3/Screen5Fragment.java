package com.example.filrouge_tp3;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class Screen5Fragment extends Fragment implements ViewObserver {

    public final static int FRAGMENT_ID = 4;
    private final String TAG = "frallo " + getClass().getSimpleName();

    private Notifiable notifiable;
    private IssueManager model;
    private IssueController controller;

    private MapView mapView;
    private ArrayAdapter<String> listAdapter;
    private final List<String> displayList = new ArrayList<>();

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    if (mapView != null) mapView.invalidate();
                } else {
                    Toast.makeText(getContext(),
                            "Accès au stockage refusé, la carte peut ne pas s'afficher correctement.",
                            Toast.LENGTH_SHORT).show();
                }
            });

    public static Screen5Fragment newInstance(IssueManager model, IssueController controller) {
        Screen5Fragment f = new Screen5Fragment();
        f.model = model;
        f.controller = controller;
        return f;
    }

    public Screen5Fragment() {
        Log.d("frallo Screen5Fragment", "screenFragment type 5 created");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (requireActivity() instanceof Notifiable) {
            notifiable = (Notifiable) requireActivity();
        } else {
            throw new AssertionError("Classe " + requireActivity().getClass().getName() + " ne met pas en œuvre Notifiable.");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        notifiable.onFragmentDisplayed(FRAGMENT_ID);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        View view = inflater.inflate(R.layout.fragment_screen5, container, false);

        mapView = view.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setUseDataConnection(true);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(14.5);
        mapView.getController().setCenter(new GeoPoint(43.6156, 7.0718));

        listAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, displayList);
        ListView listView = view.findViewById(R.id.issueListView);
        listView.setAdapter(listAdapter);

        if (model != null) {
            refreshMap();
            refreshList();
        }

        return view;
    }

    private void refreshMap() {
        mapView.getOverlays().clear();

        mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                refreshList();
                return true;
            }
            @Override
            public boolean onZoom(ZoomEvent event) {
                refreshList();
                return true;
            }
        });

        for (Issue issue : model.getIssues()) {
            Marker marker = new Marker(mapView);
            marker.setPosition(issue.getGeolocation());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(issue.getTitle());
            marker.setSnippet(issue.getDescription());
            marker.setDraggable(true);
            mapView.getOverlays().add(marker);
            controller.controlMarker(issue, marker);
        }

        mapView.invalidate();
    }

    private void refreshList() {
        displayList.clear();
        for (Issue issue : model.getIssues()) {
            displayList.add(String.format("%s (%.4f, %.4f)",
                    issue.getTitle(), issue.getLatitude(), issue.getLongitude()));
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onModelChanged(IssueManager model) {
        this.model = model;
        if (mapView != null) {
            refreshMap();
            refreshList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }
}
