package com.industrialreforged.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marker used by the original authors for methods meant to be inlined. */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Inline {
}
