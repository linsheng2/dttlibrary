package com.dttandroid.dttlibrary.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

/**
 * @Author: lufengwen
 * @Date: 2015年6月15日 下午8:18:09
 * @Description: Handler消息代理
 */
public class MessageProxy {
    private static Object sObject = new Object();
    private static SparseArray<List<WeakReference<Handler>>> sMessageMap;

    /**
     * 注册消息
     * 
     * @param messages
     *            待注册消息数组
     * @param handler
     *            待注册消息的对应消息Handler
     */
    public static void register(int[] messages, Handler handler) {
        for (int msg : messages) {
            register(msg, handler);
        }
    }

    /**
     * 注册消息
     * 
     * @param message
     *            待注册消息
     * @param handler
     *            待注册消息的对应消息Handler
     */
    public static void register(int message, Handler handler) {
        register(message, handler, false);
    }

    /**
     * 注册消息
     * 
     * @param message
     *            待注册消息
     * @param handler
     *            待注册消息的对应消息Handler
     * @param isOverride
     *            是否覆盖
     */
    public static void register(int message, Handler handler, boolean isOverride) {
        if (handler == null) {
            return;
        }

        synchronized (sObject) {
            if (sMessageMap == null) {
                sMessageMap = new SparseArray<List<WeakReference<Handler>>>();
            }

            List<WeakReference<Handler>> list = sMessageMap.get(message);
            if (list == null) {
                list = new ArrayList<WeakReference<Handler>>();
                sMessageMap.put(message, list);
            }

            // 防止重复注册
            Iterator<WeakReference<Handler>> iterator = list.iterator();
            while (iterator.hasNext()) {
                Handler h = iterator.next().get();
                if (h == null) {
                    iterator.remove();
                }
                else if (h == handler) {
                    return;
                }
                else {
                    if (isOverride) {
                        iterator.remove();
                    }
                }
            }

            list.add(new WeakReference<Handler>(handler));
        }
    }

    /**
     * 反注册消息
     * 
     * @param messages
     *            待反注册消息数组
     * @param handler
     *            待反注册消息的对应Handler
     */
    public static void unregister(int[] messages, Handler handler) {
        for (int msg : messages) {
            unregister(msg, handler);
        }
    }

    /**
     * 反注册消息
     * 
     * @param message
     *            待反注册消息
     * @param handler
     *            待反注册消息的对应Handler
     */
    public static void unregister(int message, Handler handler) {
        synchronized (sObject) {
            if (sMessageMap == null || handler == null) {
                return;
            }

            List<WeakReference<Handler>> list = sMessageMap.get(message);
            if (list == null) {
                return;
            }

            // 遍历删除弱引用已经为空和弱引用指向的Handler对象与需反注册的Handler对象相等的项
            Iterator<WeakReference<Handler>> iterator = list.iterator();
            while (iterator.hasNext()) {
                Handler h = iterator.next().get();
                if (h == null || h == handler) {
                    iterator.remove();
                }
            }

            // 如果对应message的列表已经为空，则删除该列表
            if (list.size() == 0) {
                sMessageMap.delete(message);
            }
        }
    }

    /**
     * 发送空消息
     * 
     * @param what
     *            消息类型
     * @return 是否发送消息成功
     */
    public static boolean sendEmptyMessage(int what) {
        return sendEmptyMessageDelay(what, 0);
    }

    /**
     * 延迟发送空消息
     * 
     * @param what
     *            消息类型
     * @param delayMillis
     *            延迟时间
     * @return 是否发送成功
     */
    public static boolean sendEmptyMessageDelay(int what, long delayMillis) {
        synchronized (sObject) {
            if (sMessageMap == null) {
                return false;
            }

            List<WeakReference<Handler>> list = sMessageMap.get(what);
            if (list == null) {
                return false;
            }
            for (WeakReference<Handler> weakRefHandler : list) {
                Handler h = weakRefHandler.get();
                if (h != null) {
                    h.sendEmptyMessageDelayed(what, delayMillis);
                }
            }
            return true;
        }
    }

    /**
     * 发送消息
     * 
     * @param msg
     *            消息
     * @return 是否发送成功
     */
    public static boolean sendMessage(Message msg) {
        return sendMessageDelay(msg, 0);
    }

    /**
     * 延迟发送消息
     * 
     * @param msg
     *            消息
     * @param delayMillis
     *            延迟时间
     * @return 是否发送成功
     */
    public static boolean sendMessageDelay(Message msg, long delayMillis) {
        synchronized (sObject) {
            if (sMessageMap == null) {
                return false;
            }

            List<WeakReference<Handler>> list = sMessageMap.get(msg.what);
            if (list == null) {
                return false;
            }

            for (WeakReference<Handler> weakRefHandler : list) {
                Handler h = weakRefHandler.get();
                if (h != null) {
                    h.sendMessageDelayed(Message.obtain(msg), delayMillis);
                }
            }
            return true;
        }
    }

    /**
     * 发送消息
     * 
     * @param what
     *            消息类型
     * @param obj
     *            消息附加对象
     * @return 消息是否发送成功
     */
    public static boolean sendMessage(int what, Object obj) {
        return sendMessageDelay(what, 0, 0, obj, 0);
    }

    /**
     * 发送消息
     * 
     * @param what
     *            消息类型
     * @param arg1
     *            消息参数1
     * @param obj
     *            消息附加对象
     * @return 消息是否发送成功
     */
    public static boolean sendMessage(int what, int arg1, Object obj) {
        return sendMessageDelay(what, arg1, 0, obj, 0);
    }

    /**
     * 发送消息
     * 
     * @param what
     *            消息类型
     * @param arg1
     *            消息参数1
     * @param arg2
     *            消息参数2
     * @return 消息是否发送成功
     */
    public static boolean sendMessage(int what, int arg1, int arg2) {
        return sendMessageDelay(what, arg1, arg2, null, 0);
    }

    /**
     * 发送消息
     * 
     * @param what
     *            消息类型
     * @param arg1
     *            消息参数1
     * @param arg2
     *            消息参数2
     * @param obj
     *            消息附加对象
     * @return 消息是否发送成功
     */
    public static boolean sendMessage(int what, int arg1, int arg2, Object obj) {
        return sendMessageDelay(what, arg1, arg2, obj, 0);
    }

    /**
     * 发送消息
     * 
     * @param what
     *            消息类型
     * @param arg1
     *            消息参数1
     * @param arg2
     *            消息参数2
     * @param obj
     *            消息附加对象
     * @param delayMillis
     *            延迟时间
     * @return 消息是否发送成功
     */
    public static boolean sendMessageDelay(int what, int arg1, int arg2, Object obj, long delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;

        return sendMessageDelay(msg, delayMillis);
    }

    /**
     * 发送消息
     * 
     * @param what
     *            消息类型
     * @param arg1
     *            消息参数1
     * @return 消息是否发送成功
     */
    public static boolean sendMessage(int what, int arg1) {
        return sendMessageDelay(what, arg1, 0, null, 0);
    }
    
    public static boolean sendMessageDelay(int what, long delayMillis) {
        return sendMessageDelay(what, 0, 0, null, 0);
    }

    /**
     * 发送消息
     * 
     * @param what
     *            消息类型
     * @param arg1
     *            消息参数1
     * @param delayMillis
     *            延迟时间
     * @return 消息是否发送成功
     */
    public static boolean sendMessageDelay(int what, int arg1, long delayMillis) {
        return sendMessageDelay(what, arg1, 0, null, 0);
    }

    /**
     * 发送消息
     * 
     * @param what
     *            消息类型
     * @param arg1
     *            消息参数1
     * @param obj
     *            消息附加对象
     * @param delayMillis
     *            延迟时间
     * @return 消息是否发送成功
     */
    public static boolean sendMessageDelay(int what, int arg1, Object obj, long delayMillis) {
        return sendMessageDelay(what, arg1, 0, obj, delayMillis);
    }
}
