package com.example.filrouge_tp3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Locale;

public class Screen3Fragment extends Fragment {
    public final static int FRAGMENT_ID = 2;
    public final static int ACTION_NEW_ISSUE = 0;
    private final String TAG = "frallo " + getClass().getSimpleName();
    private Notifiable notifiable;

    private EditText currentTargetEditText;

    // Factory sélectionnée selon le bouton toggle
    private AccidentFactory selectedFactory = new UrbanFactory(); // Urbain par défaut

    public Screen3Fragment() {
        Log.d(TAG, "screenFragment type 3 created");
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

    private final ActivityResultLauncher<Intent> voiceLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && !matches.isEmpty() && currentTargetEditText != null) {
                        currentTargetEditText.setText(matches.get(0));
                    }
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen3, container, false);

        EditText titleInput       = view.findViewById(R.id.titleInput);
        EditText descriptionInput = view.findViewById(R.id.descriptionInput);
        TimePicker timePicker     = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.toggleEnvironment);

        TextInputLayout tileTitle = view.findViewById(R.id.tileTitle);
        TextInputLayout tileDescription = view.findViewById(R.id.tileDescription);
        tileTitle.setEndIconOnClickListener(v -> startVoiceRecognition(titleInput));
        tileDescription.setEndIconOnClickListener(v -> startVoiceRecognition(descriptionInput));


        // Boutons toggle Urbain / Autoroute
        view.findViewById(R.id.btnUrbain).setOnClickListener(v -> {
            selectedFactory = new UrbanFactory();
        });
        view.findViewById(R.id.btnAutoroute).setOnClickListener(v -> {
            selectedFactory = new HighwayFactory();
        });

        // Bouton Envoyer
        view.findViewById(R.id.btnEnvoyer).setOnClickListener(v -> {
            String title       = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Veuillez saisir un titre.", Toast.LENGTH_SHORT).show();
                return;
            }

            Issue newIssue = selectedFactory.createIssue(title, description);

            String time = String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute());
            newIssue.setTime(time);

            notifiable.onDataChange(
                    FRAGMENT_ID,
                    newIssue,
                    ACTION_NEW_ISSUE,
                    newIssue.getSafetyProtocol()
            );
        });

        return view;
    }

    private void startVoiceRecognition(EditText target) {
        currentTargetEditText = target;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez pour remplir le champ...");

        try {
            voiceLauncher.launch(intent);
        } catch (Exception e) {
            Log.e(TAG, "Reconnaissance vocale non supportée sur cet appareil.");
        }
    }
}