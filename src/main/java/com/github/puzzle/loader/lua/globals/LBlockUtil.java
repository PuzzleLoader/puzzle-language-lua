package com.github.puzzle.loader.lua.globals;

import com.badlogic.gdx.utils.Queue;
import com.github.puzzle.core.Identifier;
import com.github.puzzle.core.resources.ResourceLocation;
import com.github.puzzle.game.block.DataModBlock;
import com.github.puzzle.game.block.IModBlock;
import com.github.puzzle.game.factories.IFactory;
import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.world.BlockSetter;
import finalforeach.cosmicreach.world.Chunk;
import finalforeach.cosmicreach.world.Zone;
import org.apache.logging.log4j.util.LambdaUtil;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class LBlockUtil {

    public static Block getBlock(String id) {
        return Block.getInstance(id);
    }

    public static BlockState getBlockState(String id) {
        return BlockState.getInstance(id);
    }

    public static void setBlockState(Zone zone, BlockState block, int x, int y, int z) {
        int cx = Math.floorDiv(x, 16);
        int cy = Math.floorDiv(y, 16);
        int cz = Math.floorDiv(z, 16);

        Chunk c = zone.getChunkAtChunkCoords(cx, cy, cz);
        if (c == null) {
            c = new Chunk(cx, cy, cz);
            c.initChunkData();
            zone.addChunk(c);
        }

        x -= 16 * cx;
        y -= 16 * cy;
        z -= 16 * cz;
        BlockSetter.replaceBlock(zone, block, new BlockPosition(c, x, y, z), new Queue<>());
    }

    public static IFactory<DataModBlock> newBlockFromName(String name) {
        return () -> new DataModBlock(name);
    }

    public static IFactory<DataModBlock> newBlockFromJson(String name, String json) {
        return () -> new DataModBlock(name, json);
    }

    public static IFactory<DataModBlock> newBlockFromLocation(String name, ResourceLocation location) {
        return () -> new DataModBlock(name, location);
    }

    public static ResourceLocation makeLocation(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }

    public static Identifier makeId(String namespace, String name) {
        return new Identifier(namespace, name);
    }

    public IFactory<?> funcToFactory(LuaFunction function) {
        return function::call;
    }

}
