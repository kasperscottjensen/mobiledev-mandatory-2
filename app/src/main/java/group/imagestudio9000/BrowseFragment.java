package group.imagestudio9000;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BrowseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseFragment extends Fragment {

    private ListView listView;
    private ImageAdapter adapter;
    private List<FbfsImage> images;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BrowseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BrowseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseFragment newInstance(String param1, String param2) {
        BrowseFragment fragment = new BrowseFragment();
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
        View view = inflater.inflate(R.layout.fragment_browse, container, false);
        listView = view.findViewById(R.id.listView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize the adapter
        images = new ArrayList<>();
        adapter = new ImageAdapter(getContext(), images);
        // Set the adapter on the ListView
        listView.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        retrieveImagesFromStorage();
        adapter.notifyDataSetChanged();
    }

    private void retrieveImagesFromStorage() {
        images.clear();
        // Get a reference to the "images" folder in Firebase Storage
        StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("images");
        // List all the items (images) in the "images" folder
        imagesRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                // Get the download URL and filename for each image
                // Handle the failure to get the download URL
                item.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                    String filename = item.getName();
                    // Create an instance of your image model class with the download URL and filename
                    FbfsImage image = new FbfsImage(downloadUrl.toString(), item.getPath(), filename);
                    // Add the image to the list
                    images.add(image);
                    // Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged();
                }).addOnFailureListener(Throwable::printStackTrace);
            }
        });
    }

}