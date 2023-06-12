package com.test.context.checker;

import com.test.context.MouseEventCode;
import com.test.utils.ArrayUtils;
import javafx.scene.input.KeyCode;

public record MouseCheck(MouseEventCode[] needMouseKeys) implements Checker {

    @Override
    public boolean check(KeyCode[] keyCodes, MouseEventCode[] mouseEventCodes) {
        return ArrayUtils.isEmpty(keyCodes) && containAllNeedMouseCodes(needMouseKeys, mouseEventCodes);
    }

    public static boolean containAllNeedMouseCodes(MouseEventCode[] needMouseKeys, MouseEventCode[] mouseEventCodes) {
        for (MouseEventCode needMouseKey : needMouseKeys) {
            if (!ArrayUtils.containRef(mouseEventCodes, needMouseKey)) {
                return false;
            }
        }
        return true;
    }
}
