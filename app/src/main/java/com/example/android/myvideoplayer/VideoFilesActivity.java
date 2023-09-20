package com.example.android.myvideoplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.util.ArrayList;

public class VideoFilesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private ArrayList<MediaFiles> videoFilesArrayList = new ArrayList<>();
    VideoFilesAdapter videoFilesAdapter;
    String folder_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_files);
        folder_name = getIntent().getStringExtra("folderName");
        getSupportActionBar().setTitle(folder_name);
        recyclerView = findViewById(R.id.videos_recylerView);
        showVideoFiles();
    }

    /**
     * method to set how the Video Files will be shown using recyclerView
     */
    private void showVideoFiles() {
        videoFilesArrayList = fetchMedia(folder_name);
        videoFilesAdapter = new VideoFilesAdapter(videoFilesArrayList, this);
        recyclerView.setAdapter(videoFilesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        videoFilesAdapter.notifyDataSetChanged();
    }

    private ArrayList<MediaFiles> fetchMedia(String folderName) {
        ArrayList<MediaFiles> videoFiles = new ArrayList<>();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArg = new String[]{"%" + folderName + "%"};
        Cursor cursor = getContentResolver().query(uri, null, selection, selectionArg, null);

        System.out.println(cursor==null);
        if (cursor != null && cursor.moveToNext()) {
            do {
                try {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                    String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));

                    MediaFiles mediaFiles = new MediaFiles(id, title, displayName, size, duration, path, dateAdded);

                    videoFiles.add(mediaFiles);
                }
                catch (Exception ignored){}


            } while (cursor.moveToNext());
        }
        return videoFiles;
    }

}