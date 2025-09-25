package org.powermock.core.classloader.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Lightweight stand-in for PowerMock's PowerMockIgnore annotation. It carries no runtime behaviour
 * and simply preserves existing test annotations after removing the PowerMock dependency.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PowerMockIgnore {
    String[] value() default {};
}
