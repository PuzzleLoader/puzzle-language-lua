package com.github.puzzle.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LuaEntrypointVariant {

    Class<?> variantOf();

}
