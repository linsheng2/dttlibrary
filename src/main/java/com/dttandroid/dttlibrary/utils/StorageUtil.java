package com.dttandroid.dttlibrary.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.dttandroid.dttlibrary.debug.AppLogger;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午1:09:42
 * @Description: 存储工具
 */
public class StorageUtil {

	/**
	 * 判断是否有SD卡
	 */
	public static boolean hasSDCard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取SD卡路径
	 */
	public static String getExternalStoragePath() {
		String path = "";
		if (hasSDCard()) {
			path = Environment.getExternalStorageDirectory().getPath();
		}
		return path;
	}

	public static boolean isExists(String path) {
		return TextUtils.isEmpty(path) ? false : new File(path).exists();
	}

	public static boolean isEmptyFile(String path) {
		File file = new File(path);
		return !file.exists() || (file.isFile() && file.length() == 0);
	}

	/**
	 * 获取文件大小
	 * @param filePath
	 * @return
	 */
	public static long getFileLength(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return 0;
		}
		return new File(filePath).length();
	}

	public static File createNewFileInSDCard(String absolutePath) {
		if (!hasSDCard()) {
			return null;
		}

		return createNewFile(absolutePath);
	}

	public static File createNewFile(String absolutePath) {
		File file = new File(absolutePath);
		if (file.exists()) {
			return file;
		}
		else {
			File dir = file.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}

			try {
				if (file.createNewFile()) {
					return file;
				}
			}
			catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public static String ensureDirExist(String dir) {
		File d = new File(dir);
		if (!d.exists()) {
			boolean result = d.mkdirs();
			AppLogger.d("DIR: " + dir + ", CREATE RESULT: " + result);
		}
		return dir;
	}

	/**
	 * 创建目录
	 * @param dir 目录路径
	 * @return true：创建成功，false：目录已经存在或目录路径错误
	 */
	public static boolean makeDir(String dir) {
		if (TextUtils.isEmpty(dir)) {
			return false;
		}

		File file = new File(dir);
		return file.mkdirs();
	}

	/**
	 * 删除文件
	 * @param path 文件路径
	 * @return true：删除成功，false：路径为目录或文件不存在
	 */
	public static boolean deleteFile(String path) {
		if (TextUtils.isEmpty(path)) {
			return false;
		}

		File file = new File(path);
		if (file.isDirectory()) {
			return false;
		}

		if (file.isFile() && file.exists()) {
			return file.delete();
		}
		return true;
	}

	/**
	 * 删除文件
	 * @param dir 文件所在目录
	 * @param fileName 文件名
	 * @return true：删除成功，false：路径为目录或文件不存在
	 */
	public static boolean deleteFile(String dir, String fileName) {
		if (TextUtils.isEmpty(fileName)) {
			return false;
		}

		if (dir == null) {
			dir = "";
		}

		if (!dir.endsWith("/")) {
			dir = dir + "/";
		}

		return deleteFile(dir + fileName);
	}

	/**
	 * 删除目录
	 * @param path 待删除目录路径
	 * @return 是否删除成功
	 */
	public static boolean deleteDir(String path) {
		return deleteDir(new File(path));
	}

	/**
	 * 删除目录
	 * @param file 待删除目录文件对象
	 * @return 是否删除成功
	 */
	public static boolean deleteDir(File file) {
		boolean success = true;
		if (file.exists()) {
			File[] list = file.listFiles();
			if (list != null) {
				for (File f : list) {
					if (f.isDirectory()) {
						deleteDir(f.getPath());
					}
					else {
						if (!f.delete()) {
							success = false;
						}
					}
				}
			}
		}
		else {
			success = false;
		}
		if (success) {
			file.delete();
		}
		return success;
	}

	/**
	 * 保存图片
	 * @param bitmap 待保存bitmap对象
	 * @param filePath  保存路径
	 * @param format 保存格式
	 * @param quality 待保存质量（0-100）, PNG格式将忽略此设置
	 * @param isOverride 如果指定路径已存在图片，是否覆盖已有图片
	 * @return 是否保存成功
	 */
	public static boolean saveImage(Bitmap bitmap, String filePath, Bitmap.CompressFormat format, int quality, boolean isOverride) {
		if (bitmap == null || format == null || filePath == null) {
			return false;
		}

		if (quality < 0 || quality > 100) {
			return false;
		}

		File file = new File(filePath);
		if (!isOverride && file.exists()) {
			return false;
		}
		else {
			if (file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}
		}

		OutputStream outputStream = null;
		File tmpFile = new File(filePath + ".tmp");
		try {
			if (tmpFile.exists()) {
				tmpFile.delete();
			}
			tmpFile.createNewFile();
			outputStream = new FileOutputStream(tmpFile);
			if (bitmap.compress(format, quality, outputStream)) {
				outputStream.flush();
				if (file.exists()) {
					if (!file.delete()) {
						throw new Exception("delete file failed, file name: " + file.getName());
					}
				}

				if (tmpFile.renameTo(file)) {
					return true;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				}
				catch (Exception e2) {
					e2.printStackTrace();
				}
			}

			if (tmpFile.exists()) {
				tmpFile.delete();
			}
		}
		return false;
	}

	/**
	 * 保存图片到指定路径
	 * @param bitmap 待保存Bitmap对象
	 * @param dir 待保存目录
	 * @param fileName 待保存文件名（不含文件类型后缀）
	 * @param ext 待保存文件扩展名（不含“.”），如无扩展名，设为null
	 * @param format 待保存格式
	 * @param quality 待保存质量（0-100）, PNG格式将忽略此设置
	 * @param isOverride  是否覆盖同名文件
	 * @return 保存是否成功
	 */
	public static boolean saveImage(Bitmap bitmap, String dir, String fileName, String ext, Bitmap.CompressFormat format, int quality, boolean isOverride) {
		if (ext == null) {
			ext = "";
		}
		if (ext.length() > 0 && !ext.startsWith(".")) {
			ext = "." + ext;
		}
		return saveImage(bitmap, dir + File.separator + fileName + ext, format, quality, isOverride);
	}

	/**
	 * 保存JPEG图片到指定路径
	 * 
	 * @param bitmap
	 *            待保存Bitmap对象
	 * @param dir
	 *            待保存目录
	 * @param fileName
	 *            待保存文件名（不含文件类型后缀）
	 * @param quality
	 *            待保存质量（0-100）
	 * @param isOverride
	 *            是否覆盖同名文件
	 * @return 保存是否成功
	 */
	public static boolean saveAsJpeg(Bitmap bitmap, String dir, String fileName, int quality, boolean isOverride) {
		return saveImage(bitmap, dir, fileName, "jpg", Bitmap.CompressFormat.JPEG, quality, isOverride);
	}

	/**
	 * 保存PNG图片到指定路径
	 * 
	 * @param bitmap
	 *            待保存Bitmap对象
	 * @param dir
	 *            待保存目录
	 * @param fileName
	 *            待保存文件名（不含文件类型后缀）
	 * @param isOverride
	 *            是否覆盖同名文件
	 * @return 保存是否成功
	 */
	public static boolean saveAsPng(Bitmap bitmap, String dir, String fileName, boolean isOverride) {
		return saveImage(bitmap, dir, fileName, "png", Bitmap.CompressFormat.PNG, 100, isOverride);
	}

	/**
	 * 清除文件名类型后缀
	 * 
	 * @param fileName
	 *            待清除类型后缀的文件名。如果文件名无后缀，返回原字符串。
	 * @return 无类型后缀文件名
	 */
	public static String trimExtension(String fileName) {
		if (fileName == null) {
			return null;
		}

		int index = fileName.lastIndexOf(".");
		if (index == -1) {
			return fileName;
		}
		else {
			return fileName.substring(0, index);
		}
	}

	/**
	 * 获取文件名
	 * 
	 * @param path
	 *            文件路径
	 * @return 文件名
	 */
	public static String getFileName(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}

		return trimExtension(path.substring(path.lastIndexOf("/") + 1));
	}

	/**
	 * 获取扩展名（小写）, 如果无后缀名，将返回null
	 * 
	 * @param fileName
	 *            文件名
	 * @return 扩展名(小写字母)
	 */
	@SuppressLint("DefaultLocale")
	public static String getExtension(String fileName) {
		if (fileName == null || fileName.length() == 0) {
			return null;
		}
		int index = fileName.lastIndexOf(".");
		if (index == -1) {
			return null;
		}
		else {
			return fileName.substring(index + 1).toLowerCase();
		}
	}

	/**
	 * 重命名
	 * 
	 * @param oldPath
	 *            包含文件名的老文件路径
	 * @param newPath
	 *            包含文件名的新文件路径
	 * @return 是否保存成功
	 */
	public static boolean rename(String oldPath, String newPath) {
		if (TextUtils.isEmpty(oldPath) || TextUtils.isEmpty(newPath)) {
			return false;
		}
		File file = new File(oldPath);
		return file.renameTo(new File(newPath));
	}

	public static boolean copy(String srcPath, String dstPath) {
		if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(dstPath)) {
			return false;
		}

		File srcFile = new File(srcPath);
		if (!srcFile.exists()) {
			return false;
		}
		File dstParentFile = new File(dstPath).getParentFile();
		if (!dstParentFile.exists()) {
			dstParentFile.mkdirs();
		}

		final int length = 1024;
		byte[] buffer = new byte[length];
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			int readByte = -1;
			fileInputStream = new FileInputStream(srcFile);
			fileOutputStream = new FileOutputStream(dstPath);
			while ((readByte = fileInputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, readByte);
			}
			fileOutputStream.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			try {
				fileInputStream.close();
				fileOutputStream.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}

		}
		return true;
	}

	public static boolean move(String srcPath, String dstPath) {
		if (copy(srcPath, dstPath)) {
			return deleteFile(srcPath);
		}
		else {
			return false;
		}
	}

	/**
	 * 将字节数转换为容易阅读的单位大小
	 * 
	 * @param sizeInBytes
	 *            字节大小
	 * @return 容易阅读的单位大小
	 */
	public static String getFriendlySize(long sizeInBytes) {
		if (sizeInBytes <= 0) {
			return "0";
		}
		final String[] units = new String[] { "B", "K", "M", "G", "T" };
		int digitGroups = (int) (Math.log10(sizeInBytes) / Math.log10(1024));
		return new DecimalFormat("###0.#").format(sizeInBytes / Math.pow(1024, digitGroups)) + units[digitGroups];
	}

	/**
	 * SDCard剩余空间
	 * 
	 * @param aFileLength
	 * @return boolean
	 */
	public static boolean isAvailableBlocks(long aFileLength) {
		File path = null;
		if (hasSDCard()) {
			path = Environment.getExternalStorageDirectory();
		}
		else {
			path = Environment.getDataDirectory();
		}
		StatFs l_statfs = new StatFs(path.getPath());
		long availaBlock = l_statfs.getAvailableBlocks();
		long blockSize = l_statfs.getBlockSize();

		if (aFileLength < (availaBlock * blockSize)) {
			return true;
		}
		return false;
	}

	public static String readText(String path) {
		String text = "";
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(path);
			br = new BufferedReader(fr);
			String str = "";
			while ((str = br.readLine()) != null) {
				text = text + str;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (br != null) {
				try {
					br.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return text;
	}

	public static boolean write(String path, InputStream inputStream) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}

		boolean result = false;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			file.createNewFile();
			in = new BufferedInputStream(inputStream);
			out = new BufferedOutputStream(new FileOutputStream(file));
			final Thread thread = Thread.currentThread();

			byte[] buffer = new byte[1444];
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				if (thread.isInterrupted()) {
					out.close();
					file.delete();
					throw new InterruptedIOException();
				}
				out.write(buffer, 0, len);
			}
			out.flush();
			result = true;
		}
		catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		finally {
			close(out);
		}
		return result;
	}

	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 遍历文件夹
	 * 
	 * @param root 根目录
	 * @param ignoreHidden true：忽略隐藏文件，false：不忽略隐藏文件
	 */
	public static void traverse(File root, boolean ignoreHidden, OnTraverseListener listener) {
		if (root == null) {
			return;
		}

		File[] files = root.listFiles();
		if (files == null) {
			return;
		}

		for (File f : files) {
			if (!ignoreHidden || !f.isHidden()) {
				if (f.isDirectory()) {
					traverse(f, ignoreHidden, listener);
				}
				else {
					if (listener != null) {
						listener.onTraversed(f);
					}
				}
			}
		}
	}

	public static boolean copyAsset(AssetManager assetManager, String assetFileName, String path) {
		boolean success = false;
		OutputStream output = null;
		InputStream input = null;

		try {
			output = new FileOutputStream(path);
			input = assetManager.open(assetFileName);
			byte[] buffer = new byte[1024];
			int length = input.read(buffer);
			while (length > 0) {
				output.write(buffer, 0, length);
				length = input.read(buffer);
			}

			output.flush();
			success = true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			close(input);
			close(output);
		}
		return success;
	}

	public interface OnTraverseListener {
		public void onTraversed(File f);
	}

	/**
	 * 解压Zip文件
	 * @param srcFile
	 * @param descDir
	 * @throws IOException
	 */
	public static void unZip(String srcFile, String descDir) throws IOException {
		File pathFile = new File(descDir);
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		ZipFile zip = new ZipFile(srcFile);
		for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {
			ZipEntry entry = entries.nextElement();
			String zipEntryName = entry.getName();
			InputStream in = zip.getInputStream(entry);
			String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");
			File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
			if (!file.exists()) {
				file.mkdirs();
			}
			if (new File(outPath).isDirectory()) {
				continue;
			}
			OutputStream out = new FileOutputStream(outPath);
			byte[] buf1 = new byte[1024];
			int len;
			while ((len = in.read(buf1)) > 0) {
				out.write(buf1, 0, len);
			}
			in.close();
			out.close();
		}
		if (zip != null) {
			zip.close();
		}
	}

}
