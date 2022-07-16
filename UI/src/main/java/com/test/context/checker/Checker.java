package com.test.context.checker;

import com.test.context.MouseEventCode;
import javafx.scene.input.KeyCode;

public interface Checker {
    boolean check(KeyCode[] keyCodes, MouseEventCode[] mouseEventCode);
}
