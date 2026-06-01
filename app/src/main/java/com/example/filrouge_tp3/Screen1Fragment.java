package com.example.filrouge_tp3;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Screen1Fragment extends Fragment {

    private static final String ARG_ISSUE = "param_issue";
    private final String TAG = "frallo " + getClass().getSimpleName();
    private Notifiable notifiable;
    public final static int FRAGMENT_ID = 0;

    private static final String ARG_INCIDENT = "incident";

    public static Screen1Fragment newInstance(Issue issue) {
        Screen1Fragment fragment = new Screen1Fragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ISSUE, issue);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_screen1, container, false);

        TextView label = view.findViewById(R.id.labelScreen1Fragment);
        label.setText(getActivity().getResources().getString(R.string.Screen1Fragment_label));

        TextView topic = view.findViewById(R.id.topic);
        if (getArguments() != null && getArguments().containsKey(ARG_ISSUE)) {
            Issue issue = getArguments().getParcelable(ARG_ISSUE);
            label.setText(issue.getTitle());
            topic.setText(
                    issue.getDescription()
                            + "\nPriorité : " + issue.getPriority()
                            + "\nStatut : " + issue.getStatus() + "★"
                            + "\n\n🛡 Protocole : " + issue.getSafetyProtocol()
            );
        }

        view.findViewById(R.id.button).setOnClickListener(clic -> {
            notifiable.onClick(FRAGMENT_ID);
        });

        return view;
    }
}