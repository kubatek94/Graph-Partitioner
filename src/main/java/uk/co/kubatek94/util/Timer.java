package uk.co.kubatek94.util;

/**
 * Created by kubatek94 on 26/04/16.
 */
public class Timer {
    private static long start = 0;
    private static long end = 0;

    public static long time() {
        if (start == 0) {
            start = System.currentTimeMillis();
            return 0;
        }

        if (end == 0) {
            end = System.currentTimeMillis();
            long diff = end - start;
            start = end = 0;
            return diff;
        }

        return 0;
    }
}
