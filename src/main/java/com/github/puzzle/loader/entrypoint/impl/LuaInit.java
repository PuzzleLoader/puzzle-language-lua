package com.github.puzzle.loader.entrypoint.impl;

import com.github.puzzle.annotations.LuaEntrypointVariant;
import com.github.puzzle.loader.entrypoint.interfaces.ModInitializer;
import org.luaj.vm2.LuaValue;

@LuaEntrypointVariant(variantOf = ModInitializer.class)
public class LuaInit implements ModInitializer {

    LuaValue value;

    public LuaInit() {}

    public LuaInit(LuaValue value) {
        this.value = value;
    }

    @Override
    public void onInit() {
        value.call();
    }
}
