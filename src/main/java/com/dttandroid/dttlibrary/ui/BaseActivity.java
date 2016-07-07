package com.dttandroid.dttlibrary.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import com.dttandroid.dttlibrary.R;
import com.dttandroid.dttlibrary.booter.BaseApp;
import com.dttandroid.dttlibrary.common.BaseConstants;
import com.dttandroid.dttlibrary.ui.header.HeaderHelper;
import com.dttandroid.dttlibrary.ui.header.HeaderHolder;
import com.dttandroid.dttlibrary.ui.header.HeaderListener;
import com.dttandroid.dttlibrary.ui.header.HeaderParam;
import com.dttandroid.dttlibrary.ui.header.UIComponent;
import com.dttandroid.dttlibrary.utils.ActivityHelper;
import com.dttandroid.dttlibrary.utils.NetworkHelper;
import com.dttandroid.dttlibrary.utils.VersionHelper;
import com.dttandroid.dttlibrary.utils.ViewHelper;
import com.dttandroid.dttlibrary.widget.WaitingDialog;

/**
 * @Author: lufengwen
 * @Date: 2015年6月15日 下午6:28:02
 * @Description: Activity基类，所有的Activity类都应该继承此类
 */
public class BaseActivity extends FragmentActivity implements HeaderListener, UIComponent {

	public static final int MSG_SHOW_DIALOG = Short.MIN_VALUE + 1;
	private static final int MSG_WAITING_DIALOG_TIMEOUT = Short.MIN_VALUE + 2;
	/**等待对话框超时时间*/
	private static final int TIMEOUT = 60 * 1000;
	

	private Context mContext;
	private HeaderHolder mHeader;
	private String mClassName;

	private ViewGroup mRootView;

	private ViewGroup mContentView;

	private Dialog mDialog;
	private boolean mIsVisible;

	private Set<Integer> mMessageSet = new HashSet<Integer>();
	private int[] MSG = new int[] { BaseConstants.Message.KICKED_OUT, BaseConstants.Message.FINISH_ALL_UI };
	
	private static List<String> sActivityList;
	
	private boolean mIsExcludeStat;

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			String activityName = BaseActivity.this.getClass().getSimpleName();
			switch (msg.what) {
			case MSG_WAITING_DIALOG_TIMEOUT:
				if (ActivityHelper.isActivityRunning(BaseActivity.this)) {
					dismissDialog();
					if (msg.obj != null) {
						((WaitingDialog.OnTimeoutListener) msg.obj).onTimeout();
					}
				}
				return true;
				
			case BaseConstants.Message.KICKED_OUT:
				if (msg.arg1 == BaseConstants.KICKED_OUT_VALUE && sActivityList.indexOf(activityName) == -1) {
					finish();
					return true;
				}
				break;
				
			case BaseConstants.Message.FINISH_ALL_UI:
                finish();
                break;
			}
			return BaseActivity.this.handleMessage(msg);
		}
	});
	
	static {
		sActivityList = new ArrayList<>();
		sActivityList.add("SplashUI");
		sActivityList.add("GuideUI");
		sActivityList.add("FrameworkUI");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		super.setContentView(R.layout.ui_base);
		//ViewUtils.inject(BaseActivity.this);
		BaseApp.setCurrentActivity(this);

		mRootView = (ViewGroup) findViewById(R.id.root_view);
		mContentView = (ViewGroup) findViewById(R.id.content_view);
		mContext = this;
		
		registerMessages(MSG);
	}

	protected boolean handleMessage(Message msg) {
		return true;
	}

	@Override
	public void setContentView(int layoutResID) {
		View view = LayoutInflater.from(this).inflate(layoutResID, null);
		mContentView.addView(view);
		onInitView();
		onInitData();
	}


	@Override
	public void setContentView(View view, LayoutParams params) {
		mContentView.addView(view, params);
		onInitView();
		onInitData();
	}

	@Override
	public void setContentView(View view) {
		mContentView.addView(view);
		onInitView();
		onInitData();
	}

	protected void onInitView() {}

	protected void onInitData() {}
	
	/**
     * 设置是否排除当前页面的统计
     */
    protected void setExcludeStat(boolean isExclude) {
        mIsExcludeStat = isExclude;
    }

	protected ViewGroup getRootView() {
		return mRootView;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mIsVisible = false;
		if (isFinishing()) {
			BaseApp.setCurrentActivity(null);
		}

	}

	@Override
	protected void onResume() {
		BaseApp.setCurrentActivity(this);
		super.onResume();
		mIsVisible = true;

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissDialog();
		getHandler().removeCallbacksAndMessages(null);
		unregisterMessages(mMessageSet);
		System.gc();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	protected final void initHeader(HeaderParam left, HeaderParam title, HeaderParam right) {
		initHeader(left, title, right , HeaderParam.NONE);
	}

	/**
	 * 初始化Header
	 * 
	 * @param left
	 *            TEXT:文本按钮，ICON:图标按钮（默认为“返回”图标），如果设置非以上两项，则不显示
	 * @param title
	 *            TEXT:文本标题，ICON：图标标题，TAB：标签栏，如果设置非以上三项，则不显示
	 * @param right
	 *            TEXT:文本按钮，ICON:图标按钮，如果设置非以上两项，则不显示
	 */
	protected final void initHeader(HeaderParam left, HeaderParam title, HeaderParam right ,HeaderParam right2) {
		if (mHeader == null) {
			ViewGroup header = (ViewGroup) findViewById(R.id.common_header);
			mHeader = new HeaderHolder(header, this);
		}
		HeaderHelper.initHeader(mHeader, left, title, right , right2);
	}

	/**
	 * 初始化Header中间的Tab
	 * 
	 * @param tabs
	 *            Tab标题数组
	 */
	protected final void initHeaderTab(String[] tabs) {
		int[] backgrounds = new int[] { R.drawable.common_header_tab_left_selector, R.drawable.common_header_tab_mid_selector, R.drawable.common_header_tab_right_selector };
		int selectorResId = R.color.common_header_tab_text_color_selector;
		ColorStateList textColor = ViewHelper.getColorStateList(getResources(), selectorResId);
		initHeaderTab(tabs, backgrounds, textColor);
	}

	/**
	 * 初始化Header中间的Tab
	 * 
	 * @param tabs
	 *            Tab标题数组
	 * @param tabBackgrounds
	 *            Tab背景资源，数组大小为3，索引从小到大依次对应左、中、右Tab
	 * @param tabTextColor
	 *            Tab中的文字颜色
	 */
	protected final void initHeaderTab(String[] tabs, int[] tabBackgrounds, int tabTextColor) {
		int[][] states = new int[1][];
		states[1] = new int[] { android.R.attr.enabled };
		initHeaderTab(tabs, tabBackgrounds, new ColorStateList(states, new int[] { tabTextColor }));
	}

	/**
	 * 初始化Header中间的Tab
	 * 
	 * @param tabs
	 *            Tab标题数组
	 * @param tabBackgrounds
	 *            Tab背景资源，数组大小为3，索引从小到大依次对应左、中、右Tab
	 * @param tabTextColor
	 *            Tab中的文字颜色
	 */
	protected final void initHeaderTab(String[] tabs, int[] tabBackgrounds, ColorStateList tabTextColor) {
		if (tabs == null || tabs.length <= 1 || mHeader == null) {
			return;
		}
		HeaderHelper.initTabs(this, this, mHeader.getTabContainer(), tabs, tabBackgrounds, tabTextColor);
	}

	/**
	 * 在Android 4.4以上启用Translucent Status Bar
	 */
	@TargetApi(19)
	protected final boolean useTranslucentStatusBar() {
		if (VersionHelper.hasKitKat()) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * 在Android 4.4以上启用Translucent Navigation Bar
	 */
	@TargetApi(19)
	protected final void useTranslucentNavigationBar() {
		if (VersionHelper.hasKitKat()) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}

	/**
	 * 在Android 4.4以上启用黏性沉浸模式
	 */
	@TargetApi(19)
	protected final boolean useStickyImmersiveMode() {
		if (VersionHelper.hasKitKat()) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * 返回当前页面Context实例
	 */
	protected final Context getContext() {
		return mContext;
	}

	protected final HeaderHolder getHeader() {
		return mHeader;
	}

	/**
	 * 获取页面Handler实例
	 */
	@Override
	public final Handler getHandler() {
		return mHandler;
	}

	/**
	 * 获取当前Activity名字
	 * 
	 * @return 当前Activity的名字
	 */
	public String getClassName() {
		if (TextUtils.isEmpty(mClassName)) {
			mClassName = getClass().getSimpleName();
		}
		return mClassName;
	}

	/**
	 * 当前Activity是否用户可见
	 * 
	 * @return true：用户可见，false：用户不可见
	 */
	protected final boolean isVisible() {
		return mIsVisible;
	}

	/**
	 * 弹出Toast（允许在非UI线程操作）
	 * 
	 * @param resId
	 *            字符串资源id
	 */
	@Override
	public final void showToast(int resId) {
		BaseApp.showToast(resId);
	}

	/**
	 * 弹出Toast（允许在非UI线程操作）
	 * 
	 * @param text
	 *            字符串
	 */
	@Override
	public final void showToast(CharSequence text) {
		BaseApp.showToast(text);
	}

	/**
	 * 弹出等待对话框
	 * 
	 * @param resId
	 *            提示文本资源ID
	 */
	@Override
	public final void showWaitingDialog(int resId) {
		showWaitingDialog(getString(resId));
	}

	/**
	 * 弹出不会超时的等待对话框
	 * 
	 * @param resId
	 *            提示文本资源ID
	 */
	@Override
	public final void showWaitingDialogWithoutTimeout(int resId) {
		showWaitingDialogWithoutTimeout(getString(resId));
	}
	
	@Override
	public void showWaitingDialogWithoutTimeout(String message) {
		WaitingDialog.Builder builder = new WaitingDialog.Builder(this);
		builder.setMessage(message).setCancelable(false).setCanceledOnTouchOutside(false);
		showWaitingDialog(builder.create(), Integer.MAX_VALUE, new WaitingDialog.OnTimeoutListener() {
			@Override
			public void onTimeout() {
				if (NetworkHelper.isConnected(getContext())) {
					showToast(R.string.common_network_poor);
				}
				else {
					showToast(R.string.common_network_unavailable);
				}
			}
		});
	}

	/**
	 * 弹出等待对话框
	 * 
	 * @param message
	 *            提示文本
	 */
	@Override
	public final void showWaitingDialog(String message) {
		showWaitingDialog(message, TIMEOUT);
	}

	/**
	 * 弹出等待对话框，超时自动弹出“网络不给力”的Toast
	 * 
	 * @param resId
	 *            提示文本
	 * @param timeout
	 *            超时时间
	 */
	@Override
	public final void showWaitingDialog(int resId, int timeout) {
		showWaitingDialog(getString(resId), timeout);
	}

	/**
	 * 弹出等待对话框，超时自动弹出“网络不给力”的Toast
	 * 
	 * @param message
	 *            提示文本
	 * @param timeout
	 *            超时时间
	 */
	@Override
	public final void showWaitingDialog(String message, int timeout) {
		showWaitingDialog(message, timeout, new WaitingDialog.OnTimeoutListener() {

			@Override
			public void onTimeout() {
				if (NetworkHelper.isConnected(getContext())) {
					showToast(R.string.common_network_poor);
				}
				else {
					showToast(R.string.common_network_unavailable);
				}
			}
		});
	}

	/**
	 * 弹出等待对话框
	 * 
	 * @param resId
	 *            提示文本资源ID
	 * @param timeout
	 *            超时时间 (单位：毫秒，0：表示永不超时，需自己关闭)
	 * @param listener
	 *            超时回调
	 */
	@Override
	public final void showWaitingDialog(int resId, int timeout, WaitingDialog.OnTimeoutListener listener) {
		showWaitingDialog(getString(resId), timeout, listener);
	}

	/**
	 * 弹出等待对话框
	 * 
	 * @param message
	 *            提示文本
	 * @param timeout
	 *            超时时间 (单位：毫秒，0：表示永不超时，需自己关闭)
	 * @param listener
	 *            超时回调
	 */
	@Override
	public final void showWaitingDialog(String message, int timeout, WaitingDialog.OnTimeoutListener listener) {
		WaitingDialog.Builder builder = new WaitingDialog.Builder(this);
		builder.setMessage(message).setCancelable(true).setCanceledOnTouchOutside(true);

		showWaitingDialog(builder.create(), timeout, listener);
	}

	/**
	 * 弹出等待对话框
	 * 
	 * @param dialog
	 *            对话框实例
	 * @param timeout
	 *            超时时间 (单位：毫秒，0：表示永不超时，需自己关闭)
	 * @param listener
	 *            超时回调
	 */
	@Override
	public final void showWaitingDialog(WaitingDialog dialog, int timeout, WaitingDialog.OnTimeoutListener listener) {
		if (dialog == null || !ActivityHelper.isActivityRunning(this)) {
			return;
		}

		dismissWaitingDialog();

		mDialog = dialog;
		mDialog.show();

		if (timeout > 0) {
			Message msg = getHandler().obtainMessage(MSG_WAITING_DIALOG_TIMEOUT, listener);
			getHandler().sendMessageDelayed(msg, timeout);
		}
	}

	/**
	 * 关闭等待对话框
	 */
	@Override
	public final void dismissWaitingDialog() {
		getHandler().removeMessages(MSG_WAITING_DIALOG_TIMEOUT);
		dismissDialog();
	}

	private void dismissDialog() {
		if (mDialog != null && ActivityHelper.isActivityRunning(this) && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	/**
	 * 弹网络不可用Toast
	 * 
	 * @return 是否网络异常
	 */
	@Override
	public final boolean showNetworkUnavailableIfNeed() {
		if (!NetworkHelper.isConnected(this) && isVisible()) {
			showToast(R.string.common_network_unavailable);
			return true;
		}
		return false;
	}

	/**
	 * 注册消息
	 * 
	 * @param msgs
	 *            待注册的消息数组
	 */
	protected final void registerMessages(int... msgs) {
		if (msgs != null) {
			for (int msg : msgs) {
				if (!mMessageSet.contains(Integer.valueOf(msg))) {
					mMessageSet.add(msg);
					MessageProxy.register(msg, getHandler());
				}
			}
		}
	}

	/**
	 * 反注册消息
	 * 
	 * @param msgs
	 *            已注册的消息数组
	 */
	protected final void unregisterMessages(int... msgs) {
		if (msgs != null) {
			for (int msg : msgs) {
				mMessageSet.remove(Integer.valueOf(msg));
				MessageProxy.unregister(msg, getHandler());
			}
		}
	}

	/**
	 * 反注册消息
	 * 
	 * @param msgs
	 *            已注册的消息集合
	 */
	private final void unregisterMessages(Set<Integer> msgs) {
		if (msgs != null && msgs.size() > 0) {
			Integer[] array = msgs.toArray(new Integer[msgs.size()]);
			for (Integer msg : array) {
				mMessageSet.remove(msg);
				MessageProxy.unregister(msg, getHandler());
			}
		}
	}

	/**
	 * 头部左边按钮点击时触发
	 */
	@Override
	public void onHeaderLeftButtonClick(View view) {
		finish(); // 默认操作，需要自定义的请自行覆盖该方法
	}

	/**
	 * 右边按钮点击时触发
	 */
	@Override
	public void onHeaderRightButtonClick(View view) {
		// 空方法，子类自行覆盖重写该方法
	}

	@Override
	public void onHeaderTitleClick(View view) {
		// 空方法，子类自行覆盖重写该方法
	}

	@Override
	public void onHeaderTabClick(int index) {
		// 空方法，子类自行覆盖重写该方法
	}

	@Override
	public void onHeaderRightButton2Click(View v) {
		// TODO Auto-generated method stub
		
	}

}
