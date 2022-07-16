package com.test.context.checker;

import com.test.context.MouseEventCode;
import javafx.scene.input.KeyCode;

public record FullCheck(KeyCode[] needKeys, MouseEventCode[] needMouseKeys) implements Checker {

    @Override
    public boolean check(KeyCode[] keyCodes, MouseEventCode[] mouseEventCode) {

        return KeyCheck.containAllNeedKeyCodes(needKeys, keyCodes) &&
                MouseCheck.containAllNeedMouseCodes(needMouseKeys, mouseEventCode);
    }

}

