package com.example.advermission.FolderChooserActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.advermission.R;

import java.util.List;

public class FolderChooserAdapter extends RecyclerView.Adapter<FolderChooserAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<FolderChooserInfo> mData;
    private FolderChooserActivity.ItemClickCallback callback;

    public FolderChooserAdapter(Context mContext, List<FolderChooserInfo> mData, FolderChooserActivity.ItemClickCallback callback) {
        this.mContext = mContext;
        this.mData = mData;
        this.callback = callback;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.folder_chooser_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final FolderChooserInfo info = mData.get(position);
        holder.name.setText(info.getName() == null ? "" : info.getName());

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(v, position, info);
            }
        });
        holder.image.setImageResource(info.getImage());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        LinearLayout v;

        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            v = view.findViewById(R.id.view);
        }
    }
}
