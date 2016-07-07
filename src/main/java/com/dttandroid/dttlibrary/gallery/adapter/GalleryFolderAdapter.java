package com.dttandroid.dttlibrary.gallery.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.dttandroid.dttlibrary.R;
import com.dttandroid.dttlibrary.gallery.manager.GalleryThumbManager;
import com.dttandroid.dttlibrary.gallery.model.ImageFile;
import com.dttandroid.dttlibrary.graphics.RecyclingImageView;
import com.dttandroid.dttlibrary.ui.BaseListAdapter;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午8:53:36
 * @Description:
 */
public class GalleryFolderAdapter extends BaseListAdapter<ImageFile> {

    public interface OnCheckBoxClickedListener {
        public void onClicked(CheckBox checkBox, ImageFile imageFile);
    }

    private OnCheckBoxClickedListener m_clickedCallback;

    public GalleryFolderAdapter(Context context, List<ImageFile> items) {
        super(context, items);
    }

    @Override
    public View getView(ImageFile file, final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = getLayoutInflater().inflate(R.layout.item_gallery_folder, null);

            holder = new ViewHolder();
            holder.imageView = (RecyclingImageView) convertView.findViewById(R.id.item_gallery_folder_image);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.item_gallery_folder_checkbox);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (m_clickedCallback != null) {
            final CheckBox checkBox = holder.checkBox;
            holder.checkBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_clickedCallback.onClicked(checkBox, (ImageFile) getItem(position));
                }
            });
        }

        dataBinding(holder, file);

        return convertView;
    }

    public void setOnCheckBoxClickedListner(OnCheckBoxClickedListener callback) {
        m_clickedCallback = callback;
    }

    private void dataBinding(ViewHolder holder, ImageFile file) {
        if (file != null) {
            GalleryThumbManager.getFileThumbnail(holder.imageView, file);
            holder.checkBox.setChecked(file.isSelected());
        }
    }

    public class ViewHolder {
        public RecyclingImageView imageView;
        public CheckBox checkBox;
    }
}
