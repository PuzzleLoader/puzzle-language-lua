package com.github.puzzle.loader.lang.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.github.puzzle.PuzzleLua;
import com.github.puzzle.annotations.LuaEntrypointVariant;
import com.github.puzzle.loader.entrypoint.impl.LuaInit;
import com.github.puzzle.loader.lua.globals.LuaGlobals;
import com.github.puzzle.game.util.Reflection;
import com.github.puzzle.loader.lang.LanguageAdapter;
import com.github.puzzle.loader.lang.LanguageAdapterException;
import com.github.puzzle.loader.mod.info.ModInfo;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;

public class LuaLanguageAdapter implements LanguageAdapter {

    public static <T> T newInstance(Class<T> type) {
        try {
            Constructor<T> c = type.getConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException var4) {
            try {
                Constructor<T> c = type.getDeclaredConstructor();
                c.setAccessible(true);
                return c.newInstance();
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // TODO: LOOK AT COBALT FOR LUA - HellScaped
    @Override
    public <T> T create(ModInfo modInfo, String value, Class<T> aClass) throws LanguageAdapterException {
        // dev/crmodders/puzzle/init.lua
        if (!value.endsWith(".lua")) throw new LanguageAdapterException("VALUE IS NOT VALID PATH -> \"" + value + ", FORMAT MUST BE path/to/file/file.lua");

        String name = new File(value).getName();
        String contents = "";
        try {
            contents = new String(PuzzleLua.findResource(value).readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int pubCount = 0;
        Method pubM;
        for (Method m : aClass.getDeclaredMethods()) {
            if (!((m.getModifiers() & Modifier.PRIVATE) == 0))
                if (!((m.getModifiers() & Modifier.STATIC) == 0)) {
                    pubM = m;
                    pubCount++;
                }
        }

//        if (!aClass.isInterface()) throw new LanguageAdapterException(value + " cannot be assigned to a non-interface");
//        if (pubCount >= 2) throw new LanguageAdapterException("For " + aClass.getName() + " to work with lua it must have 1 public method");
        try {
            Reflections reflections = new Reflections();
            Set<Class<?>> variants = reflections.getTypesAnnotatedWith(LuaEntrypointVariant.class);
            if (variants.isEmpty()) throw new LanguageAdapterException("There is no variants of initializers for lua");

            Class<?> variantClazz0 = null;
            for (Class<?> clazz : variants) {
                if (aClass.isAssignableFrom(clazz)) variantClazz0 = clazz;
            }
            if (variantClazz0 == null) throw new LanguageAdapterException("There are no variants of " + aClass.getName() + " for lua");
            Class<T> variantClazz1 = (Class<T>) variantClazz0;

            LuaFunction chunk = (LuaFunction) LuaGlobals.globals.load(contents, name);

            Constructor<T> usableConstructor = null;

            LOGGER.info("Using \"{}\" as the Mod Init for \"{}\"", variantClazz1.getName(), value);
            for (Constructor<?> constructor : variantClazz1.getDeclaredConstructors()) {
                LOGGER.info("Found Constructor {}{}", constructor.getName(), Arrays.toString(constructor.getParameters()).replaceAll("\\[", "(").replaceAll("]", ")"));
                if (constructor.getParameters().length == 1) {
                    if (constructor.getParameters()[0].getType().toString().equals(LuaValue.class.getName())) {
                        usableConstructor = (Constructor<T>) constructor;
                        break;
                    }
                }
                usableConstructor = (Constructor<T>) constructor;
            }

            if (usableConstructor == null) throw new LanguageAdapterException("Cannot find usable variant of \"" + aClass.getName() + "\" for lua");
            T obj = newInstance(variantClazz1);
            Reflection.setFieldContents(obj, "value", chunk);
//            return usableConstructor.newInstance(chunk);
            return obj;
        } catch (Exception e) {
            throw new LanguageAdapterException(e);
        }
    }
}
