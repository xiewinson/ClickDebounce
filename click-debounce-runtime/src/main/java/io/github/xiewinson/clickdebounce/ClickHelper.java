package io.github.xiewinson.clickdebounce;

import android.util.Log;

public class ClickHelper {

    public static final String TAG = "CLICKHELPER";

    private static long sLastClickTime = 0;

    /**
     * 获取当前时间是否可以点击
     *
     * @param interval 间隔时间
     * @return true, 可点击；false，不可点击
     */
    public static boolean isClickAllowed(long interval) {
        final long currentTime = System.currentTimeMillis();
        Log.d(TAG,"---------------------   上次点击时间：" + currentTime);
        if ((currentTime - sLastClickTime) >= interval) {
            sLastClickTime = currentTime;
            Log.d(TAG,"---------------------   点击成功：" + sLastClickTime);
            return true;
        }
        Log.d(TAG,"---------------------   点击失败：" + sLastClickTime);
        return false;
    }
}
