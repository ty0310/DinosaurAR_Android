package com.kyouryu.dinosaurar_android.face_tracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kyouryu.dinosaurar_android.R;
import com.kyouryu.dinosaurar_android.Session;
import com.kyouryu.dinosaurar_android.common.ImageUtil;
import com.kyouryu.dinosaurar_android.model.ListViewItemModel;
import com.kyouryu.dinosaurar_android.model.UserItemData;

import java.util.ArrayList;

/**
 * Created by ty on 2017/07/12.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {
    private LayoutInflater mInflater;
    private ArrayList<ListViewItemModel> mData;
    private Listener mListener;

    // アイテムタップ用interface
    public interface Listener {
        void onRecyclerClicked(View v, int position);
    }

    public RecyclerViewAdapter(Context context, ArrayList<ListViewItemModel> data, Listener listener) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // 画像セット
        holder.imageView.setImageBitmap(ImageUtil.getBitmapFromAssets(mData.get(position).getIconImageName(), Session.getInstance().getContext()));
        // 選択されたアイコン設定
        if (mData.get(position).isSelected()) {
            holder.backgroundView.setBackgroundResource(R.drawable.icon_rounded_corners_selected);
            mData.get(position).setPositionUp(UserItemData.isPositionUp("" + (position - 1)));
            Session.getInstance().setSelectedItemData(mData.get(position));
        } else {
            holder.backgroundView.setBackgroundResource(R.drawable.icon_rounded_corners_default);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateItems(position);
                mListener.onRecyclerClicked(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        else {
            return mData.size();
        }
    }

    private void updateItems(int position) {
        if (position == 0) {
            // +アイコンの場合何もしない
            return;
        }
        for (ListViewItemModel data : mData) {
            data.setSelected(false);
        }
        mData.get(position).setSelected(true);
    }

    // ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View backgroundView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.icon_image);
            backgroundView = (View) itemView.findViewById(R.id.item_background_view);
        }
    }
}
