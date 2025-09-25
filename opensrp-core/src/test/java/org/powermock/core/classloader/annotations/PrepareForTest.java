package org.powermock.core.classloader.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Minimal replacement for PowerMock's PrepareForTest annotation. It is retained only for
 * compatibility and does not trigger any bytecode manipulation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PrepareForTest {
    Class<?>[] value() default {};
}
