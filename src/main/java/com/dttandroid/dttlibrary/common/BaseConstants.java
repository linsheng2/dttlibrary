package com.dttandroid.dttlibrary.common;

public class BaseConstants {
	
	public static final int KICKED_OUT_VALUE = 100;
	
	/** Handler消息*/
	public static class Message {
		private static final int BASE = 10000;
		public final static int KICKED_OUT = BASE + 1; // 被踢出的消息
		public final static int BANNER_CLICK = BASE + 2; // banner点击的消息
		public static final int FINISH_ALL_UI = BASE + 3;
		public static final int PICTURE_CLICK = BASE + 4;
	}
}
