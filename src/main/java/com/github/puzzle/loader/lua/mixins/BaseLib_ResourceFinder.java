package com.github.puzzle.loader.lua.mixins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.github.puzzle.loader.mod.ModLocator;
import finalforeach.cosmicreach.io.SaveLocation;
import org.luaj.vm2.lib.BaseLib;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Mixin(BaseLib.class)
public class BaseLib_ResourceFinder {

    /**
     * @author Mr_Zombii
     * @reason Look Through All Jars
     */
    @Overwrite
    public InputStream findResource(String path) {
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
            return this.getClass().getResourceAsStream(path.startsWith("/") ? path : "/" + path);
        return streamOut.get();
    }

}
