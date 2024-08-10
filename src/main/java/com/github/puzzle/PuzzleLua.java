package com.github.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.github.puzzle.loader.lua.globals.LPuzzleEventBusUtil;
import com.github.puzzle.loader.lua.globals.LuaGlobals;
import com.github.puzzle.loader.mod.ModLocator;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.github.puzzle.core.resources.PuzzleGameAssetLoader;
import com.github.puzzle.game.commands.CommandManager;
import com.github.puzzle.game.commands.PuzzleCommandSource;
import com.github.puzzle.loader.entrypoint.interfaces.ModInitializer;
import finalforeach.cosmicreach.io.SaveLocation;
import org.luaj.vm2.LuaValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PuzzleLua implements ModInitializer {

    @Override
    public void onInit() {
        var function = CommandManager.literal("function");
        function.then(CommandManager.argument("path", StringArgumentType.greedyString())
                        .executes(context -> {
                            String path = StringArgumentType.getString(context, "path");
                            if (path.endsWith(".lua")) runLua(context, path);
                            return 0;
                        }));
        CommandManager.dispatcher.register(function);
    }

    public static InputStream findResource(String path) {
        path = (path.startsWith("\\") ? path : "\\" + path);
        path = path.replaceFirst("\\\\", "");
        try {
            if (Gdx.files != null) {
                FileHandle classpathLocationFile = Gdx.files.classpath(path);
                if (classpathLocationFile.exists()) return new ByteArrayInputStream(classpathLocationFile.readBytes());
                FileHandle vanillaLocationFile = Gdx.files.internal(path);
                if (vanillaLocationFile.exists()) return new ByteArrayInputStream(vanillaLocationFile.readBytes());
                FileHandle modLocationFile = Gdx.files.absolute(SaveLocation.getSaveFolderLocation() + "/mods/lua" + path);
                if (modLocationFile.exists()) return new ByteArrayInputStream(modLocationFile.readBytes());
            }
        } catch (Exception ignore) {
            System.out.println(ignore);
        }

        String finalPath = path;
        AtomicReference<InputStream> streamOut = new AtomicReference<>();
        streamOut.set(null);
        ModLocator.locatedMods.values().forEach((container) -> {
            ZipFile file = container.JAR;
            if (file != null) {
                ZipEntry entry = file.getEntry(finalPath);
                if (entry != null) {
                    try {
                        streamOut.set(file.getInputStream(entry));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        if (streamOut.get() == null)
            return LPuzzleEventBusUtil.class.getResourceAsStream(path.startsWith("/") ? path : "/" + path);
        return streamOut.get();
    }

    public static void runLua(CommandContext<PuzzleCommandSource> context, String path) {
        FileHandle handle = PuzzleGameAssetLoader.locateAsset(path);
        if (handle != null) {
            try {
                LuaValue chunk = LuaGlobals.function_globals.load(new String(handle.read().readAllBytes()), handle.name());
                chunk.call();
            } catch (IOException e) {
                context.getSource().getChat().sendMessage(
                        context.getSource().getWorld(),
                        context.getSource().getPlayer(),
                        null,
                        "This file may not exist or be corrupt"
                );
            } catch (Exception e) {
                context.getSource().getChat().sendMessage(
                        context.getSource().getWorld(),
                        context.getSource().getPlayer(),
                        null,
                        "The command had an error"
                );
                e.printStackTrace();
            }
        } else {
            context.getSource().getChat().sendMessage(
                    context.getSource().getWorld(),
                    context.getSource().getPlayer(),
                    null,
                    "This file may not exist or the path may not be correct"
            );
        }
    }
}
