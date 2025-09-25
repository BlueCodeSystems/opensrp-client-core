package org.powermock.reflect.internal;

import org.powermock.reflect.Whitebox;

/**
 * Minimal delegate to preserve the handful of existing test calls to PowerMock's WhiteboxImpl.
 */
public final class WhiteboxImpl {

    private WhiteboxImpl() {
        // utility
    }

    public static <T> T invokeMethod(Object target, String methodName, Object... args) {
        return Whitebox.invokeMethod(target, methodName, args);
    }
}
