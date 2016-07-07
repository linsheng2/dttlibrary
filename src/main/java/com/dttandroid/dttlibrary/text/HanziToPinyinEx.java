package com.dttandroid.dttlibrary.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;

import com.dttandroid.dttlibrary.R;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:34:53
 * @Description:
 */
public class HanziToPinyinEx {
    private static HanziToPinyinEx sInstance = null;
    private static Object sLock = new Object();

    private HashMap<String, String> mMap = null;

    public static HanziToPinyinEx getInstance() {
        return sInstance;
    }
    
    public static void init(Context context) {
        if (sInstance == null) {
            synchronized (sLock) {
                if (sInstance == null) {
                    sInstance = new HanziToPinyinEx(context);
                }
            }
        }
    }

    /**
     * 将unicode-to-hanyu-pinyin.txt文件 以unicode为key pinyin为value 加到hashmap中
     */
    private HanziToPinyinEx(Context context) {
        mMap = new HashMap<String, String>();
        InputStream inputStream = context.getResources().openRawResource(R.raw.unicode_to_hanyu_pinyin);
        BufferedReader breader = new BufferedReader(new InputStreamReader(inputStream));
        String s;
        String unicode;
        String pinyin = "";
        try {
            while ((s = breader.readLine()) != null) {
                // 提取文件流每行中的unicode码以及 pinyin字符串
                if (s.length() < 4)
                    continue;
                unicode = s.substring(0, 4);
                if (s.length() >= 7) {
                    pinyin = s.substring(6, s.length() - 1);
                }
                mMap.put(unicode.toLowerCase(Locale.getDefault()), pinyin);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uninitData() {
        mMap.clear();
    }

    public String getPinyinString(String str) {
        String retString = "";

        for (int i = 0; i < str.length(); i++) {
            char chinese = str.charAt(i);
            retString += " ";
            retString += getPinyinString(chinese);
        }

        return retString.toUpperCase(Locale.getDefault());
    }

    /**
     * 获取拼音，不是汉字的原样返回
     */
    public String getPinyinString(char chinese) {
        String retString = "" + chinese;

        if (!isChinese(chinese))
            return retString;

        String unicode = Integer.toHexString(chinese);// 得到汉字的unicode编码
        String pinyinString = mMap.get(unicode);// 从hashMap中找到汉子的拼音

        if (pinyinString != null) {
            int i = 0;
            while (i < pinyinString.length()) {
                if (pinyinString.charAt(i) == ',')
                    break;
                i++;
            }

            retString = pinyinString.substring(0, i) + " " + chinese;
        }

        return retString;
    }

    public String getPinyin(char chinese) {
        String retString = "" + chinese;
        if (!isChinese(chinese))
            return retString;
        String unicode = Integer.toHexString(chinese);
        String pinyinString = mMap.get(unicode);
        if (pinyinString != null) {
            int i = 0;
            while (i < pinyinString.length()) {
                if (pinyinString.charAt(i) == ',')
                    break;
                i++;
            }
            retString = pinyinString.substring(0, i - 1);
        }
        return retString;
    }

    public String getPinyin(String str) {
        String retString = "";
        for (int i = 0; i < str.length(); i++)
            retString += getPinyin(str.charAt(i));
        return retString.toUpperCase(Locale.getDefault());
    }

    public String getPinyinEx(String str) {
        String retString = "";
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            retString += getPinyin(c);
            if (isChinese(c)) {
                retString += c;
            }
        }
        return retString.toUpperCase(Locale.getDefault());
    }

    private boolean isChinese(char a) {
        int code = (int) a;
        return (code >= 19968 && code <= 171941);
    }
}
