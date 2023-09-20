package com.example.android.myvideoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<MediaFiles> mediaFiles = new ArrayList<>();
    private ArrayList<String> totalFolderList = new ArrayList<>(); // keep track of folders that contains videos
    RecyclerView recyclerView;
    VideoFolderAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.folders_recyclerView);
        viewFolders();

        /*  to refresh by swipe */
        swipeRefreshLayout = findViewById(R.id.refresh_folder_swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewFolders(); // calling viewFolders() when user refreshes
                swipeRefreshLayout.setRefreshing(false);  // stop the refresh animation
            }
        });
    }

    /**
     * method to set how the Folders will be shown using recyclerView
     */
    private void viewFolders() {
        mediaFiles = findMedia();
        adapter = new VideoFolderAdapter(mediaFiles, totalFolderList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter.notifyDataSetChanged();
    }

    /**
     * method to find each video files inside a folder (whose information is stored in MediaFiles class)
     *
     * @return ArrayList of MediaFile type object
     */
    private ArrayList<MediaFiles> findMedia() {
        ArrayList<MediaFiles> mediaFilesArrayList = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  // get the uri external video media table

        /* perform query on the content provider and store the returned cursor.
        The cursor works like an iterator that points to specific row within query result set */
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        /*  moveToNext() is used to advance the cursor to next query result set. It returns true if the cursor is successfully moved */
        if (cursor != null && cursor.moveToNext()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                String dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));

                MediaFiles mediaFiles = new MediaFiles(id, title, displayName, size, duration, path, dateAdded);

                int index = path.lastIndexOf("/");
                String currentPath = path.substring(0, index); // get path except the folder name "/"

                if (!totalFolderList.contains(currentPath)) {   //check if current path is already in totalFolderList
                    totalFolderList.add(currentPath);
                }
                mediaFilesArrayList.add(mediaFiles);
            } while (cursor.moveToNext());
        }
        return mediaFilesArrayList;
    }

    /* inflate menu resource */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.folder_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rateUs:
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());  // go to app store
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                return true;
            case R.id.folder_refresh:
                finish();   // end current activity
                startActivity(getIntent());  // again start current activity
                return true;
            case R.id.share_app:
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                share.setType("text/plane");
                startActivity(Intent.createChooser(share, "Share app: "));  // create chooser screen when user selects an app
                return true;
            case R.id.update_profile:
                startActivity(new Intent(MainActivity.this, UpdateProfileActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}