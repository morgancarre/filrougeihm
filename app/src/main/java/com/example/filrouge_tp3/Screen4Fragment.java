package com.example.filrouge_tp3;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class Screen4Fragment extends Fragment {
    public final static int FRAGMENT_ID = 3;
    public final static int ACTION_VIEW_MAP = 0;
    private static final String ARG_ISSUE = "param_issue";
    private final String TAG = "frallo " + getClass().getSimpleName();
    private Notifiable notifiable;

    private ImageView photoView;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && photoView != null) {
                    photoView.setImageURI(uri);
                }
            });

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

        Issue currentIssue = null;
        if (getArguments() != null && getArguments().containsKey(ARG_ISSUE)) {
            currentIssue = getArguments().getParcelable(ARG_ISSUE);
        }

        final Issue issue = currentIssue;

        if (savedInstanceState == null) {
            String existingPath = (issue != null) ? issue.getPicture() : null;
            CameraFragment cam = CameraFragment.newInstance(existingPath);

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.cameraFragmentContainer, cam)
                    .commit();
        }

        if (issue != null) {
            ((TextView) view.findViewById(R.id.issueTitle)).setText(issue.getTitle());
            ((TextView) view.findViewById(R.id.issueDescription)).setText(issue.getDescription());
            ((TextView) view.findViewById(R.id.issueProtocol)).setText(issue.getSafetyProtocol());
            ((TextView) view.findViewById(R.id.issuePriority)).setText(issue.getPriority().name());
            ((TextView) view.findViewById(R.id.issueType)).setText(
                    issue instanceof HighwayIssue ? "Autoroute" : "Urbain"
            );
            String time = issue.getTime();
            ((TextView) view.findViewById(R.id.issueTime)).setText(
                    time != null && !time.isEmpty() ? time : "--:--"
            );
            ((RatingBar) view.findViewById(R.id.issueStatus)).setRating(issue.getStatus());

            ImageView icon = view.findViewById(R.id.issuePriorityIcon);
            switch (issue.getPriority()) {
                case CRITICAL: icon.setImageResource(R.drawable.ic_critical); break;
                case HIGH:     icon.setImageResource(R.drawable.ic_high);     break;
                case MEDIUM:   icon.setImageResource(R.drawable.ic_medium);   break;
                case LOW:      icon.setImageResource(R.drawable.ic_low);      break;
            }

            TextInputEditText latInput = view.findViewById(R.id.latInput);
            TextInputEditText lonInput = view.findViewById(R.id.lonInput);
            if (issue.getLatitude() != 0.0) latInput.setText(String.valueOf(issue.getLatitude()));
            if (issue.getLongitude() != 0.0) lonInput.setText(String.valueOf(issue.getLongitude()));

            TextView positionStatus = view.findViewById(R.id.positionStatus);

            TextWatcher positionWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    String latStr = latInput.getText() != null ? latInput.getText().toString().trim() : "";
                    String lonStr = lonInput.getText() != null ? lonInput.getText().toString().trim() : "";
                    if (latStr.isEmpty() || lonStr.isEmpty()) return;
                    try {
                        double lat = Double.parseDouble(latStr);
                        double lon = Double.parseDouble(lonStr);
                        Issue original = IssueManager.getInstance().getLastReportedIssue();
                        if (original != null) {
                            IssueManager.getInstance().setLocation(original, lat, lon);
                        }
                        positionStatus.setText("Position enregistrée : " + latStr + ", " + lonStr);
                    } catch (NumberFormatException ignored) {
                        positionStatus.setText("");
                    }
                }
            };
            latInput.addTextChangedListener(positionWatcher);
            lonInput.addTextChangedListener(positionWatcher);

            // Bouton voir sur la carte
            view.findViewById(R.id.btnViewMap).setOnClickListener(v ->
                    notifiable.onDataChange(FRAGMENT_ID, null, ACTION_VIEW_MAP, null)
            );
        }

        return view;
    }
}
