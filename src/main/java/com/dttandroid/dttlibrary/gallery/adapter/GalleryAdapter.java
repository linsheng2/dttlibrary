package com.dttandroid.dttlibrary.gallery.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dttandroid.dttlibrary.R;
import com.dttandroid.dttlibrary.gallery.manager.GalleryThumbManager;
import com.dttandroid.dttlibrary.gallery.model.ImageFolder;
import com.dttandroid.dttlibrary.graphics.RecyclingImageView;
import com.dttandroid.dttlibrary.ui.BaseListAdapter;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午8:53:25
 * @Description: 相册适配器
 */
public class GalleryAdapter extends BaseListAdapter<ImageFolder> {

    public GalleryAdapter(Context context, List<ImageFolder> items) {
        super(context, items);
    }

    @Override
    public View getView(ImageFolder folder, int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = getLayoutInflater().inflate(R.layout.item_gallery, null);

            holder = new ViewHolder();
            holder.imgThumb = (RecyclingImageView) convertView.findViewById(R.id.item_gallery_thumbnail);
            holder.tvName = (TextView) convertView.findViewById(R.id.item_gallery_name);
            holder.tvCount = (TextView) convertView.findViewById(R.id.item_gallery_count);
            holder.tvSelected = (TextView) convertView.findViewById(R.id.item_gallery_selected);
            holder.vSelectedContainer = (View) convertView.findViewById(R.id.item_gallery_selected_container);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        dataBinding(holder, folder);

        return convertView;
    }

    private void dataBinding(ViewHolder holder, ImageFolder folder) {
        if (folder == null) {
            return;
        }
        GalleryThumbManager.getFolderThumbnail(holder.imgThumb, folder);
        holder.tvName.setText(folder.getName());
        holder.tvCount.setText("(" + folder.size() + ")");
        if (folder.getSelectionCount() > 0) {
            holder.vSelectedContainer.setVisibility(View.VISIBLE);
            holder.tvSelected.setText(String.valueOf(folder.getSelectionCount()));
        }
        else {
            holder.vSelectedContainer.setVisibility(View.INVISIBLE);
        }
    }

    public class ViewHolder {
        public RecyclingImageView imgThumb;
        public TextView tvName;
        public TextView tvCount;
        public TextView tvSelected;
        public View vSelectedContainer;
    }
}
