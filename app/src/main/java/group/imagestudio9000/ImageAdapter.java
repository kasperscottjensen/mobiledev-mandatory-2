package group.imagestudio9000;

import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends ArrayAdapter<FbfsImage> {
    private Context context;

    public ImageAdapter(Context context, List<FbfsImage> images) {
        super(context, 0, images);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        ImageView imageView = listItemView.findViewById(R.id.imageView3);
        TextView textViewName = listItemView.findViewById(R.id.textView5);

        FbfsImage currentImage = getItem(position);

        Glide.with(context)
                .load(currentImage.getDownloadUrl())
                .placeholder(R.drawable.baseline_image_search_24)
                .into(imageView);

        textViewName.setText(currentImage.getFilename());

        listItemView.setOnClickListener(v -> {
            FbfsImage clickedImage = getItem(position);
            Intent intent = new Intent(context, ImageActivity.class);
            intent.putExtra("downloadUrl", clickedImage.getDownloadUrl());
            intent.putExtra("imageUrl", clickedImage.getImageUrl());
            intent.putExtra("filename", clickedImage.getFilename());

            context.startActivity(intent);
        });

        return listItemView;
    }
}
