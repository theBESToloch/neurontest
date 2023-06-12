package com.test.context.checker;

import com.test.context.MouseEventCode;
import com.test.utils.ArrayUtils;
import javafx.scene.input.KeyCode;

public record KeyCheck(KeyCode[] needKeys) implements Checker {

    @Override
    public boolean check(KeyCode[] keyCodes, MouseEventCode[] mouseEventCode) {
        return containAllNeedKeyCodes(needKeys, keyCodes) && ArrayUtils.isEmpty(mouseEventCode);
    }

    public static boolean containAllNeedKeyCodes(KeyCode[] needKeys, KeyCode[] keyCodes) {
        for (KeyCode needKey : needKeys) {
            if (!ArrayUtils.containRef(keyCodes, needKey)) {
                return false;
            }
        }
        return true;
    }
}
