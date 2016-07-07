package com.dttandroid.dttlibrary.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @Author: lufengwen
 * @Date: 2015年6月10日 上午12:44:54
 * @Description: 基类列表适配器
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

    private List<T> mItems;
    private LayoutInflater mInflater;
    private Context mContext;

    public BaseListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public BaseListAdapter(Context context, List<T> items) {
        this(context);
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public T getItem(int position) {
        if (mItems != null && position >= 0 && position < mItems.size()) {
            return mItems.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // 默认返回该Item的位置，如果有自己的特别需要，自行覆盖重写该方法
        return position;
    }

    /**
     * 获取实体列表
     */
    public List<T> getItems() {
        return mItems;
    }

    /**
     * 获取第一个Item
     * 
     * @return 获取第一个Item，如果数据集为空，则返回null
     */
    public T getFirstItem() {
        return mItems != null && !mItems.isEmpty() ? mItems.get(0) : null;
    }

    /**
     * 获取最后一个Item
     * 
     * @return 获取最后一个Item，如果数据集为空，则返回null
     */
    public T getLastItem() {
        return mItems != null && !mItems.isEmpty() ? mItems.get(mItems.size() - 1) : null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView(getItem(position), position, convertView, parent);
    }

    /**
     * 调用BaseAdapter的getView，添加类型为T的item参数，返回列表中与position对应的item
     * 
     * @param item
     *            列表中与position对应的item
     * @param position
     *            视图想要获取的数据集中的实体位置
     * @param convertView
     *            可重用的item视图
     * @param parent
     *            父视图组
     * @return 对应数据集指定位置item的视图
     */
    public abstract View getView(T item, int position, View convertView, ViewGroup parent);

    /**
     * 获取LayoutInflater实例
     * 
     */
    public LayoutInflater getLayoutInflater() {
        return mInflater;
    }

    /**
     * 获取当前Adapter绑定的Context实例
     */
    public Context getContext() {
        return mContext;
    }
    
    /**
     * 获取字符串资源
     * 
     * @param resId 资源ID
     * @return      字符串
     */
    public CharSequence getText(int resId) {
        if (mContext == null) {
            return null;
        }
        return mContext.getText(resId);
    }
    
    /**
     * 获取字符串资源
     * 
     * @param resId 资源ID
     * @return      字符串
     */
    public String getString(int resId) {
        if (mContext == null) {
            return null;
        }
        return mContext.getString(resId);
    }

    /**
     * 设置实体列表，如果原来存在数据，将对其进行覆盖
     * 
     * @param items
     *            新的items
     */
    public void setItems(List<T> items) {
        mItems = items;
    }

    /**
     * 销毁数据
     */
    public void destory() {
        mItems = null;
    }
}