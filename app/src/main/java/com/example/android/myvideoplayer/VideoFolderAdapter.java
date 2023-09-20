package com.example.android.myvideoplayer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.ViewHolder> {

    private ArrayList<MediaFiles> mediaFiles;
    private ArrayList<String> folderPath;
    private Context context;

    public VideoFolderAdapter(ArrayList<MediaFiles> mediaFiles, ArrayList<String> folderPath, Context context) {
        this.mediaFiles = mediaFiles;
        this.folderPath = folderPath;
        this.context = context;
    }

    @NonNull
    @Override
    /* create new ViewHolder whenever RecyclerView need a new one */
    public VideoFolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.folder_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    /* update RecyclerView to show individual data using the position */
    public void onBindViewHolder(@NonNull VideoFolderAdapter.ViewHolder holder, int position) {

        int indexPath = folderPath.get(position).lastIndexOf("/");  // get position of last "/"
        String nameOfFolder = folderPath.get(position).substring(indexPath + 1); // everything after the last "/" represents the folder name
        holder.folderName.setText(nameOfFolder);
        holder.folderPath.setText(folderPath.get(position));

        /* finding how many videos in a folder and setting to filesCount */
        String path = folderPath.get(position);
        File folder = new File(path);
        String[] fileName = folder.list();
        int amountOfFiles = 0;
        for(int i = 0; i < fileName.length; i++){
            if(fileName[i].contains(".mp4") || fileName[i].contains(".mkv")){
                amountOfFiles++;
            }
        }
        holder.filesCount.setText("" + amountOfFiles + " videos");


        /* handle each view individually when clicked */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoFilesActivity.class);
                intent.putExtra("folderName", nameOfFolder);
                context.startActivity(intent);
            }
        });
    }

    @Override
    /* get total number of items */
    public int getItemCount() {
        return folderPath.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView folderName, folderPath, filesCount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.folderName);
            folderPath = itemView.findViewById(R.id.folderPath);
            filesCount = itemView.findViewById(R.id.filesCount);
        }
    }
}
