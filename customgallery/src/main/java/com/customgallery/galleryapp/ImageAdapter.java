package com.customgallery.galleryapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements HeaderItemDecoration.StickyHeaderInterface {
    public static final int IMAGE_TILE = 0;
    public static final int DATE_TILE = 1;
    public static final int SPAN_COUNT_IMAGES = 3;
    public static final int SPAN_COUNT_DATE = 1;
    private List<GalleryData> fileList = new ArrayList<>();

    private Context context;

    ImageAdapter(Context context) {

        this.context = context;
    }

    public void addData(List<GalleryData> fileList) {
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case DATE_TILE:
                return new MyViewHolderTwo(LayoutInflater.from(context).inflate(R.layout.layout_rv_header, parent, false));
            default:
                return new MyViewHolderOne(LayoutInflater.from(context).inflate(R.layout.image_list, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case IMAGE_TILE:
                MyViewHolderOne viewHolderOne = (MyViewHolderOne) holder;
                viewHolderOne.bind(fileList.get(position));

                break;
            default:
                MyViewHolderTwo myViewHolderTwo = (MyViewHolderTwo) holder;
                myViewHolderTwo.bind(fileList.get(position));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return fileList != null ? fileList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return fileList.get(position).isHeader() ? DATE_TILE : IMAGE_TILE;
    }


    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;
        do {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        return headerPosition;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return R.layout.layout_rv_header;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {

        ((TextView) header.findViewById(R.id.tv_header)).setText(Utility.getDateInString(fileList.get(headerPosition).getDate()));
    }

    @Override
    public boolean isHeader(int itemPosition) {
        return getItemViewType(itemPosition) == DATE_TILE;

    }

//    public void unSelectSelectedItem() {
//        if (selectedItem != -1)
//            notifyItemChanged(selectedItem);
//    }

    public ArrayList<Integer> selectUnselectItem(ArrayList<Integer> selection, int selectionCount, int position) {
        if (fileList.get(position).isSelected()) {
            fileList.get(position).setSelected(false);

            if (selection.contains(position)) {
                selection.remove(selection.indexOf(position));
            }
            notifyItemChanged(position);
        } else {
            if (selection.size() < selectionCount) {
                fileList.get(position).setSelected(true);
                if (!selection.contains(position)) {
                    selection.add(position);
                }
                notifyItemChanged(position);
            } else {
                Toast.makeText(context, "cannot select more then" + " " + selectionCount + " items", Toast.LENGTH_SHORT).show();
            }

        }

        return selection;
    }

    public boolean isItemSelected(int position) {
        return fileList.get(position).isSelected();
    }

    public void removeSelection(int position) {
        fileList.get(position).setSelected(false);
        notifyDataSetChanged();

    }

    class MyViewHolderOne extends RecyclerView.ViewHolder {

        ImageView ivImage;


        View view;

        MyViewHolderOne(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            view = itemView.findViewById(R.id.view);

        }

        void bind(GalleryData galleryData) {
            Glide.with(itemView).load(galleryData.getFile()).into(ivImage);
            if (galleryData.isSelected()) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }

    class MyViewHolderTwo extends RecyclerView.ViewHolder {

        TextView tvHeader;


        MyViewHolderTwo(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tv_header);

        }

        void bind(GalleryData galleryData) {
            tvHeader.setText(Utility.getDateInString(galleryData.getDate()));
        }

    }
}