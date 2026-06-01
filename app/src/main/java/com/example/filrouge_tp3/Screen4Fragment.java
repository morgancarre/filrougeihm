package com.example.filrouge_tp3;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Screen4Fragment extends Fragment {
    public final static int FRAGMENT_ID = 3;
    private static final String ARG_ISSUE = "param_issue";
    private final String TAG = "frallo " + getClass().getSimpleName();
    private Notifiable notifiable;

    public static Screen4Fragment newInstance(Issue issue) {
        Screen4Fragment fragment = new Screen4Fragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ISSUE, issue);
        fragment.setArguments(args);
        return fragment;
    }

    public Screen4Fragment() {
        Log.d(TAG, "screenFragment type 4 created");
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
        View view = inflater.inflate(R.layout.fragment_screen4, container, false);

        if (getArguments() != null && getArguments().containsKey(ARG_ISSUE)) {
            Issue issue = getArguments().getParcelable(ARG_ISSUE);

            ((TextView) view.findViewById(R.id.issueTitle)).setText(issue.getTitle());
            ((TextView) view.findViewById(R.id.issueDescription)).setText(issue.getDescription());
            ((TextView) view.findViewById(R.id.issueProtocol)).setText(issue.getSafetyProtocol());
            ((TextView) view.findViewById(R.id.issuePriority)).setText(issue.getPriority().name());
            ((TextView) view.findViewById(R.id.issueType)).setText(
                    issue instanceof HighwayIssue ? "Autoroute" : "Urbain"
            );
            ((RatingBar) view.findViewById(R.id.issueStatus)).setRating(issue.getStatus());

            ImageView icon = view.findViewById(R.id.issuePriorityIcon);
            switch (issue.getPriority()) {
                case CRITICAL: icon.setImageResource(R.drawable.ic_critical); break;
                case HIGH:     icon.setImageResource(R.drawable.ic_high);     break;
                case MEDIUM:   icon.setImageResource(R.drawable.ic_medium);   break;
                case LOW:      icon.setImageResource(R.drawable.ic_low);      break;
            }
        }

        return view;
    }
}
