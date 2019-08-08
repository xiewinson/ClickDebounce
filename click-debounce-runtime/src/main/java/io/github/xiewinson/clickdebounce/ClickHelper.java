package io.github.xiewinson.clickdebounce;

public class ClickHelper {

    private static long sLastClickTime = 0;

    private static final int DEBOUNCE_INTERVAL = 300;

    public static boolean debounce() {
        final long currentTime = System.currentTimeMillis();
        if ((currentTime - sLastClickTime) >= DEBOUNCE_INTERVAL) {
            sLastClickTime = currentTime;
            return false;
        }
        return true;
    }
}
