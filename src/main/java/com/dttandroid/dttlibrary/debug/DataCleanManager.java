package com.dttandroid.dttlibrary.debug;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;

import com.dttandroid.dttlibrary.utils.Combo3;

/**
 * @Author: lufengwen
 * @Date: 2015年12月2日 下午1:34:37
 * @Description:
 */
public class DataCleanManager {
	public static void cleanInternalCache(Context context) {
		deleteFilesByDirectory(context.getCacheDir());
	}

	public static void cleanDatabases(Context context) {
		deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/databases"));
	}

	public static void cleanSharedPreference(Context context) {
		deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/shared_prefs"));
	}

	public static void cleanDatabaseByName(Context context, String dbName) {
		context.deleteDatabase(dbName);
	}

	public static void cleanFiles(Context context) {
		deleteFilesByDirectory(context.getFilesDir());
	}

	public static void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(context.getExternalCacheDir());
		}
	}

	public static void cleanCustomCache(String filePath) {
		deleteFilesByDirectory(new File(filePath));
	}

	public static void cleanApplicationData(Context context, String... filepath) {
		cleanInternalCache(context);
		cleanExternalCache(context);
		cleanDatabases(context);
		cleanSharedPreference(context);
		cleanFiles(context);
		for (String filePath : filepath) {
			cleanCustomCache(filePath);
		}
	}

	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}

	public static long[] getFilesAndSize(File f) throws Exception {
		long size = 0;
		long files = 0;
		File flist[] = f.listFiles();
		files = flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				files = files + getFilesAndSize(flist[i])[0];
				files--;
				size = size + getFilesAndSize(flist[i])[1];
			}
			else {
				size = size + flist[i].length();
			}
		}
		long[] rs = { files, size };
		return rs;
	}

	public static List<Combo3<String, Long, Long>> getAllFilesAndSize(File f) throws Exception {
		List<Combo3<String, Long, Long>> rs = new ArrayList<Combo3<String, Long, Long>>();
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				long[] fs = getFilesAndSize(flist[i]);
				rs.add(new Combo3<String, Long, Long>(flist[i].getAbsolutePath(), fs[0], fs[1]));
				rs.addAll(getAllFilesAndSize(flist[i]));
			}
		}

		return rs;
	}
}