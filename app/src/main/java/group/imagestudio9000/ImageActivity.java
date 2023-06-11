package group.imagestudio9000;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image);
        String imageUrl = getIntent().getStringExtra("downloadUrl");
        ImageView imageView = findViewById(R.id.imageView);
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.baseline_image_search_24)
                .into(imageView);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> {
            deleteImage(getIntent().getStringExtra("filename"));
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void deleteImage(String filename) {
        // Delete image from Firebase Storage
        StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("images");
        StorageReference imageRef = imagesRef.child(filename);
        imageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    System.out.println(aVoid);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("images")
                            .whereEqualTo("filename", filename)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    documentSnapshot.getReference().delete()
                                            .addOnSuccessListener(aVoid1 -> {
                                                System.out.println(aVoid1);
                                            })
                                            .addOnFailureListener(Throwable::printStackTrace);
                                }
                            })
                            .addOnFailureListener(Throwable::printStackTrace);
                })
                .addOnFailureListener(Throwable::printStackTrace);
        onBackPressed();
    }


}