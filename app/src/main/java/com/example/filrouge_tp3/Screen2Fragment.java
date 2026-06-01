package com.example.filrouge_tp3;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class Screen2Fragment extends Fragment implements ClickableIssue<Issue> {
    public final static int FRAGMENT_ID = 1;
    // Codes action pour onDataChange
    public final static int ACTION_CLICK_ITEM   = 0;
    public final static int ACTION_RATING_CHANGE = 1;

    private final String TAG = "frallo " + getClass().getSimpleName();
    private Notifiable notifiable;

    private List<Issue> issues;
    private IssueAdapter adapter;

    public Screen2Fragment() {
        Log.d(TAG, "screenFragment type 2 created");
    }

    @Override
    public void onStart() {
        super.onStart();
        notifiable.onFragmentDisplayed(FRAGMENT_ID);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen2, container, false);

        // Données
        issues = IssueManager.getInstance().getIssues();

        // Adapter personnalisé
        adapter = new IssueAdapter(requireContext(), issues, this);

        ListView listView = view.findViewById(R.id.listViewIncidents);
        listView.setAdapter(adapter);

        return view;
    }

    // --- ClickableIssue ---

    @Override
    public void onClickItem(List<Issue> items, int itemIndex) {
        // On notifie l'activité : clic sur un item
        notifiable.onDataChange(FRAGMENT_ID, items.get(itemIndex), ACTION_CLICK_ITEM, null);
    }

    @Override
    public void onRatingBarChange(int itemIndex, float value, IssueAdapter adapterRef, List<Issue> items) {
        // Mettre à jour l'objet
        items.get(itemIndex).setStatus(value);
        // Notifier l'activité
        notifiable.onDataChange(FRAGMENT_ID, items.get(itemIndex), ACTION_RATING_CHANGE, value);
        adapterRef.notifyDataSetChanged();
    }



}