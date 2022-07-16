package com.test.utils;

import lombok.NonNull;

public class ArrayUtils {


    public static <T> boolean containRef(@NonNull T[] array, @NonNull T element) {
        for (T t : array) {
            if (t == element) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmpty(Object[] array) {
        for (Object element : array) {
            if (element != null) {
                return false;
            }
        }
        return true;
    }
}
