package com.dttandroid.dttlibrary.gallery.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.dttandroid.dttlibrary.gallery.manager.GalleryViewerManager;
import com.dttandroid.dttlibrary.gallery.model.ImageFile;
import com.dttandroid.dttlibrary.ui.BaseListAdapter;
import com.dttandroid.dttlibrary.widget.GalleryImageView;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午8:53:45
 * @Description:
 */
public class GalleryViewerAdapter extends BaseListAdapter<ImageFile> {
	public GalleryViewerAdapter(Context context, List<ImageFile> items) {
		super(context, items);
	}

	@Override
	public View getView(ImageFile file, int position, View convertView, ViewGroup parent) {

		GalleryImageView image = new GalleryImageView(getContext());
		// image.setLayoutParams(new
		// Gallery.LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT));

		if (file != null) {
			GalleryViewerManager.get(image, file.getPath());
		}

		int prev = position - 1;
		int next = position + 1;
		if (prev >= 0) {
			ImageFile prevFile = (ImageFile) getItem(prev);
			GalleryViewerManager.get(null, prevFile.getPath());
		}
		if (next < getCount()) {
			ImageFile nextFile = (ImageFile) getItem(next);
			GalleryViewerManager.get(null, nextFile.getPath());
		}

		return image;
	}
}
