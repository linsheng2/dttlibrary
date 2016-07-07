package com.dttandroid.dttlibrary.ui;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.dttandroid.dttlibrary.R;
import com.dttandroid.dttlibrary.booter.BaseApp;
import com.dttandroid.dttlibrary.ui.header.HeaderHelper;
import com.dttandroid.dttlibrary.ui.header.HeaderHolder;
import com.dttandroid.dttlibrary.ui.header.HeaderListener;
import com.dttandroid.dttlibrary.ui.header.HeaderParam;
import com.dttandroid.dttlibrary.ui.header.UIComponent;
import com.dttandroid.dttlibrary.utils.ActivityHelper;
import com.dttandroid.dttlibrary.utils.ViewHelper;
import com.dttandroid.dttlibrary.widget.WaitingDialog;

/**
 * @Author: lufengwen
 * @Date: 2015年6月15日 下午8:24:05
 * @Description: Fragment基类，所有的Fragment类应该继承此类
 */
public abstract class BaseFragment extends Fragment implements HeaderListener, UIComponent {
    private Set<Integer> mMessageSet = new HashSet<Integer>();
    private HeaderHolder mHeader;
    private String mClassName;
    
    private boolean mIsExcludeStat;

    private Handler mHandler = new InnerHandler(this);
    
    private static class InnerHandler extends Handler {
        WeakReference<BaseFragment> mFragment;
        
        InnerHandler(BaseFragment fragment) {
            mFragment = new WeakReference<BaseFragment>(fragment);
        }
        
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            // 基类处理的消息放这里
            }
            BaseFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.handleMessage(msg);
            }
        }
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
     * 设置是否排除当前页面的统计
     */
    protected void setExcludeStat(boolean isExclude) {
        mIsExcludeStat = isExclude;
    }
    
    /**
     * 初始化Header
     * 
     * @param container      Fragment根View
     * @param left  TEXT:文本按钮，ICON:图标按钮（默认为“返回”图标），如果设置非以上两项，则不显示
     * @param title TEXT:文本标题，ICON：图标标题，TAB：标签栏，如果设置非以上三项，则不显示
     * @param right TEXT:文本按钮，ICON:图标按钮，如果设置非以上两项，则不显示
     */
    protected final void initHeader(View container, HeaderParam left, HeaderParam title, HeaderParam right ) {
    	initHeader(container, left, title, right , HeaderParam.NONE);
    }
    
    /**
     * 初始化Header
     * 
     * @param container      Fragment根View
     * @param left  TEXT:文本按钮，ICON:图标按钮（默认为“返回”图标），如果设置非以上两项，则不显示
     * @param title TEXT:文本标题，ICON：图标标题，TAB：标签栏，如果设置非以上三项，则不显示
     * @param right TEXT:文本按钮，ICON:图标按钮，如果设置非以上两项，则不显示
     * @param right2 TEXT:文本按钮，ICON:图标按钮，如果设置非以上两项，则不显示
     */
    protected final void initHeader(View container, HeaderParam left, HeaderParam title, HeaderParam right ,HeaderParam right2) {
        if (mHeader == null) {
            ViewGroup header = (ViewGroup) container.findViewById(R.id.common_header);
            mHeader = new HeaderHolder(header, this);
        }
        
        HeaderHelper.initHeader(mHeader, left, title, right , right2);
    }
    
    /**
     * 初始化Header中间的Tab
     * 
     * @param tabs Tab标题数组
     */
    protected final void initHeaderTab(String[] tabs) {
        int[] backgrounds = new int[] {
                R.drawable.common_header_tab_left_selector,
                R.drawable.common_header_tab_mid_selector,
                R.drawable.common_header_tab_right_selector
        };
        int selectorResId = R.color.common_header_tab_text_color_selector;
        ColorStateList textColor = ViewHelper.getColorStateList(getResources(), selectorResId);
        initHeaderTab(tabs, backgrounds, textColor);
    }
    
    /**
     * 初始化Header中间的Tab
     * 
     * @param tabs Tab标题数组
     * @param tabBackgrounds Tab背景资源，数组大小为3，索引从小到大依次对应左、中、右Tab
     * @param tabTextColor Tab中的文字颜色
     */
    protected final void initHeaderTab(String[] tabs, int[] tabBackgrounds, int tabTextColor) {
        initHeaderTab(tabs, tabBackgrounds, ColorStateList.valueOf(tabTextColor));
    }
    
    /**
     * 初始化Header中间的Tab
     * 
     * @param tabs Tab标题数组
     * @param tabBackgrounds Tab背景资源，数组大小为3，索引从小到大依次对应左、中、右Tab
     * @param tabTextColor Tab中的文字颜色
     */
    protected final void initHeaderTab(String[] tabs, int[] tabBackgrounds, ColorStateList tabTextColor) {
        if (tabs == null || tabs.length <= 1 || mHeader == null) {
            return;
        }
        HeaderHelper.initTabs(getActivity(), this, mHeader.getTabContainer(), tabs, tabBackgrounds, tabTextColor);
    }
    
    protected final HeaderHolder getHeader() {
        return mHeader;
    }

    /**
     * 获取页面Handler实例
     * 
     */
    @Override
    public final Handler getHandler() {
        return mHandler;
    }

    /**
     * 获取BaseActivity实例
     * 
     * @return 获取BaseActivity实例
     */
    public final BaseActivity getBaseActivity() {
        return getActivity() instanceof BaseActivity ? (BaseActivity) getActivity() : null;
    }

    public final CharSequence getTextEx(int resId) {
        return BaseApp.getContext().getText(resId);
    }
    
    public final String getStringEx(int resId) {
        return BaseApp.getContext().getString(resId);
    }

    /**
     * BaseFragment 类型的 Fragment 在非UI线程更新UI时使用此方法安全更新UI
     * 
     * @param runnable
     */
    public final void runOnUiThread(final Runnable runnable) {
        if (runnable != null && isAdded() && ActivityHelper.isActivityRunning(getActivity()) && !isDetached()) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (runnable != null && isAdded() && ActivityHelper.isActivityRunning(getActivity()) && !isDetached()) {
                        runnable.run();
                    }
                }
            });
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }

    @Override
    public void onDestroy() {
        getHandler().removeCallbacksAndMessages(null);
        dismissWaitingDialog();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        mHeader = null;
        unregisterMessages(mMessageSet);
        super.onDestroyView();
        System.gc();
    }

    /**
     * 弹出Toast（允许在非UI线程操作）
     * @param resId 字符串资源id
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
        if (ActivityHelper.isActivityRunning(getActivity())) {
            if (getActivity() instanceof UIComponent) {
                ((UIComponent) getActivity()).showWaitingDialog(resId);
            }
        }
    }
    
    /**
     * 弹出不会超时的等待对话框
     * 
     * @param resId
     *            提示文本资源ID
     */
    @Override
    public final void showWaitingDialogWithoutTimeout(int resId) {
        if (ActivityHelper.isActivityRunning(getActivity())) {
            if (getActivity() instanceof UIComponent) {
                ((UIComponent) getActivity()).showWaitingDialog(resId, Integer.MAX_VALUE);
            }
        }
    }
    
    @Override
    public void showWaitingDialogWithoutTimeout(String message) {
    	if (ActivityHelper.isActivityRunning(getActivity())) {
            if (getActivity() instanceof UIComponent) {
                ((UIComponent) getActivity()).showWaitingDialog(message, Integer.MAX_VALUE);
            }
        }
    }

    /**
     * 弹出等待对话框
     * 
     * @param message
     *            提示文本
     */
    @Override
    public final void showWaitingDialog(String message) {
        if (ActivityHelper.isActivityRunning(getActivity())) {
            if (getActivity() instanceof UIComponent) {
                ((UIComponent) getActivity()).showWaitingDialog(message);
            }
        }
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
        if (ActivityHelper.isActivityRunning(getActivity())) {
            if (getActivity() instanceof UIComponent) {
                ((UIComponent) getActivity()).showWaitingDialog(resId, timeout);
            }
        }
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
        if (ActivityHelper.isActivityRunning(getActivity())) {
            if (getActivity() instanceof UIComponent) {
                ((UIComponent) getActivity()).showWaitingDialog(message, timeout);
            }
        }
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
        if (ActivityHelper.isActivityRunning(getActivity())) {
            if (getActivity() instanceof UIComponent) {
                ((UIComponent) getActivity()).showWaitingDialog(resId, timeout, listener);
            }
        }
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
        if (ActivityHelper.isActivityRunning(getActivity())) {
            if (getActivity() instanceof UIComponent) {
                ((UIComponent) getActivity()).showWaitingDialog(message, timeout, listener);
            }
        }
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
        if (ActivityHelper.isActivityRunning(getActivity())) {
            if (getActivity() instanceof UIComponent) {
                ((UIComponent) getActivity()).showWaitingDialog(dialog, timeout, listener);
            }
        }
    }

    /**
     * 关闭等待对话框
     */
    @Override
    public final void dismissWaitingDialog() {
        if (ActivityHelper.isActivityRunning(getActivity())) {
            if (getActivity() instanceof UIComponent) {
                ((UIComponent) getActivity()).dismissWaitingDialog();
            }
        }
    }

    /**
     * 弹网络不可用Toast
     * 
     * @return 是否网络异常
     */
    @Override
    public final boolean showNetworkUnavailableIfNeed() {
        if (ActivityHelper.isActivityRunning(getActivity())) {
            if (getActivity() instanceof UIComponent) {
                return ((UIComponent) getActivity()).showNetworkUnavailableIfNeed();
            }
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
                mMessageSet.add(msg);
                MessageProxy.register(msg, getHandler());
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
            Integer[] array = (Integer[]) msgs.toArray(new Integer[msgs.size()]);
            for (Integer msg : array) {
                mMessageSet.remove(msg);
                MessageProxy.unregister(msg, getHandler());
            }
        }
    }

    protected boolean handleMessage(Message msg) {
    	return true;
    }

    @Override
    public void onHeaderLeftButtonClick(View view) {
        // 空方法，子类自行覆盖重写该方法
    }

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
    public void onHeaderRightButton2Click(View view) {
        // 空方法，子类自行覆盖重写该方法
    }
}
