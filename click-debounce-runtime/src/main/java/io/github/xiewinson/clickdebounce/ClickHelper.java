package io.github.xiewinson.clickdebounce;

public class ClickHelper {

    private static long sLastClickTime = 0;

    /**
     * 获取当前时间是否可以点击
     *
     * @param interval 间隔时间
     * @return true, 可点击；false，不可点击
     */
    public static boolean isClickAllowed(long interval) {
        final long currentTime = System.currentTimeMillis();
        final long diff = currentTime - sLastClickTime;
        // 判断小于 0 是因为用户有可能改机器时间
        if (diff < 0 || diff >= interval) {
            sLastClickTime = currentTime;
            return true;
        }
        return false;
    }
}
