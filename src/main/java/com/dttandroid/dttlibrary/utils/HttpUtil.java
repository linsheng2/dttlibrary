package com.dttandroid.dttlibrary.utils;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author: lufengwen
 * @Date: 2015年8月24日 上午11:37:41
 * @Description:
 */
public class HttpUtil {

	/**
	 * 根据url地址获取文件大小
	 * @param url 请求地址
	 * @return 文件大小,小于0表示失败
	 */
	public static long getFileSize(String url) {
		long fileLength = -1;
		HttpURLConnection httpConnection = null;
		try {
			httpConnection = (HttpURLConnection) new URL(url).openConnection();
			httpConnection.setRequestProperty("User-Agent", "NetFox");
			int responseCode = httpConnection.getResponseCode();

			if (responseCode >= 400) {
				return -2;
			}

			String header;
			for (int i = 1;; i++) {
				header = httpConnection.getHeaderFieldKey(i);
				if (header != null) {
					// 真机Content-Length，虚拟机content-length
					if ("Content-Length".equals(header)) {
						fileLength = Long.parseLong(httpConnection.getHeaderField(header));
						break;
					}
					if ("content-length".equals(header)) {
						fileLength = Long.parseLong(httpConnection.getHeaderField(header));
						break;
					}
				}
			}
		}
		catch (Exception e) {
			fileLength = -2;
		}
		finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}
		return fileLength;
	}
}
