package group.imagestudio9000;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {

    private final int RC_CAMERA_PERMISSION = 0;
    private final int RC_IMAGE_PICKER = 1;
    private final int RC_IMAGE_CAPTURE = 2;

    private View view;

    private Button btnSelect;
    private Button btnCapture;
    private Button btnUpload;
    private Button btnCancel;
    private ImageView imageView;

    private Uri capturedImageUri;
    private Uri selectedImageUri;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add, container, false);

        btnSelect = view.findViewById(R.id.btnSelect);
        btnCapture = view.findViewById(R.id.btnCapture);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnCancel = view.findViewById(R.id.btnCancel);

        imageView = view.findViewById(R.id.imageView);

        btnSelect.setOnClickListener(v -> openImagePicker());
        btnCancel.setOnClickListener(v -> setAllToNull());
        btnCapture.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, RC_CAMERA_PERMISSION);
            } else {
                openCamera();
            }
        });
        btnUpload.setOnClickListener(v -> {
            if (capturedImageUri == null) {
                uploadImageToFirebaseStorage(selectedImageUri);
            } else if (selectedImageUri == null) {
                uploadImageToFirebaseStorage(capturedImageUri);
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RC_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_IMAGE_PICKER);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RC_IMAGE_CAPTURE);
    }

    private void setAllToNull() {
        selectedImageUri = null;
        imageView.setImageURI(null);
        imageView.setImageBitmap(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_IMAGE_PICKER && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        } else if (requestCode == RC_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(image);
            getUriFromBitmap();
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        // Create a unique filename for the image
        String filename = UUID.randomUUID().toString();
        // Get a reference to the Firebase Storage location where the image will be uploaded
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + filename);
        // Upload the image to Firebase Storage
        UploadTask uploadTask = storageRef.putFile(imageUri);
        // Add a success listener to get the download URL of the uploaded image
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL of the uploaded image
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Handle the success and get the download URL
                String downloadUrl = uri.toString();
                // Save the download URL, image URL, and filename in Firestore
                saveImageInfoToFirestore(downloadUrl, storageRef.getPath(), filename);
            }).addOnFailureListener(Throwable::printStackTrace);
        }).addOnFailureListener(Throwable::printStackTrace);
    }

    private void saveImageInfoToFirestore(String downloadUrl, String imageUrl, String filename) {
        // Get a reference to the Firestore collection where you want to store the image information
        CollectionReference imagesRef = FirebaseFirestore.getInstance().collection("images");
        // Create a new document with a unique ID
        DocumentReference newImageRef = imagesRef.document();
        // Create a new Image object with the download URL, image URL, and filename
        FbfsImage image = new FbfsImage(downloadUrl, imageUrl, filename);
        // Set the image object as the document data
        newImageRef.set(image)
                .addOnSuccessListener(System.out::println)
                .addOnFailureListener(Throwable::printStackTrace);

        setAllToNull();
    }

    private void getUriFromBitmap() {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        File file = new File(getActivity().getCacheDir(), "image.jpg");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        capturedImageUri = Uri.fromFile(file);
    }


}