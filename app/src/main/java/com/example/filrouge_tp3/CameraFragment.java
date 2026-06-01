package com.example.filrouge_tp3;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraFragment extends Fragment {

    public static final int FRAGMENT_ID = 7;
    public static final String CHANNEL_PICTURE = "channel_picture";
    public static final String KEY_PHOTO_PATH = "photo_path";

    private static final String ARG_PHOTO_PATH = "arg_photo_path";
    private static final String STATE_PHOTO_PATH = "state_photo_path";
    private static final String STATE_PENDING_URI = "state_pending_uri";

    private Picturable picturable;
    private ImageView imageView;
    private String currentPhotoPath;
    private Uri pendingPhotoUri;

    public static CameraFragment newInstance(String existingPhotoPath) {
        CameraFragment f = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHOTO_PATH, existingPhotoPath);
        f.setArguments(args);
        return f;
    }

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    launchCamera();
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Permission caméra")
                                .setMessage("L'accès à la caméra est nécessaire pour prendre des photos d'incidents.")
                                .setPositiveButton("Réessayer", (d, w) ->
                                        checkPermissionAndLaunch())
                                .setNegativeButton("Annuler", null)
                                .show();
                    }
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && pendingPhotoUri != null) {
                    currentPhotoPath = pendingPhotoUri.toString();
                    displayPhoto(currentPhotoPath);
                    if (picturable != null) {
                        picturable.onPictureTaken(currentPhotoPath);
                    }
                }
            });

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    currentPhotoPath = uri.toString();
                    displayPhoto(currentPhotoPath);
                    if (picturable != null) {
                        picturable.onPictureTaken(currentPhotoPath);
                    }
                }
            });

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (requireActivity() instanceof Picturable) {
            picturable = (Picturable) requireActivity();
        } else {
            throw new AssertionError("L'activité doit implémenter Picturable.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        imageView = view.findViewById(R.id.cameraImageView);

        if (savedInstanceState != null) {
            currentPhotoPath = savedInstanceState.getString(STATE_PHOTO_PATH);
            String uriStr = savedInstanceState.getString(STATE_PENDING_URI);
            if (uriStr != null) pendingPhotoUri = Uri.parse(uriStr);
        } else if (getArguments() != null) {
            currentPhotoPath = getArguments().getString(ARG_PHOTO_PATH);
        }

        if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
            displayPhoto(currentPhotoPath);
        } else {
            imageView.setImageResource(R.drawable.camera_icon);
        }

        view.findViewById(R.id.btnTakePhoto).setOnClickListener(v -> checkPermissionAndLaunch());
        view.findViewById(R.id.btnGallery).setOnClickListener(v ->
                galleryLauncher.launch("image/*")
        );

        getParentFragmentManager().setFragmentResultListener(CHANNEL_PICTURE, getViewLifecycleOwner(),
                (requestKey, result) -> {
                    String path = result.getString(KEY_PHOTO_PATH);
                    if (path != null && !path.isEmpty()) {
                        currentPhotoPath = path;
                        displayPhoto(currentPhotoPath);
                    }
                });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_PHOTO_PATH, currentPhotoPath);
        if (pendingPhotoUri != null) {
            outState.putString(STATE_PENDING_URI, pendingPhotoUri.toString());
        }
    }

    private void checkPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            File photoFile = createImageFile();
            pendingPhotoUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    photoFile
            );
            cameraLauncher.launch(pendingPhotoUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "ISSUE_" + timestamp + "_";
        File storageDir = requireContext().getCacheDir();
        return File.createTempFile(fileName, ".jpg", storageDir);
    }

    private void displayPhoto(String path) {
        Picasso.get()
                .load(Uri.parse(path))
                .placeholder(R.drawable.camera_icon)
                .into(imageView);
    }
}