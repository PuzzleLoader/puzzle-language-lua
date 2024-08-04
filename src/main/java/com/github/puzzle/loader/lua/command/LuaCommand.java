package com.github.puzzle.loader.lua.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.github.puzzle.game.commands.PuzzleCommandSource;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class LuaCommand implements Command<PuzzleCommandSource> {

    LuaClosure function;

    public LuaCommand(LuaClosure func) {
        function = func;
    }

    @Override
    public int run(CommandContext<PuzzleCommandSource> context) throws CommandSyntaxException {
        try {
            return function.call(CoerceJavaToLua.coerce(context)).checkint();
        } catch (Exception e) {
            context.getSource().getChat().sendMessage(
                    context.getSource().getWorld(),
                    context.getSource().getPlayer(),
                    null,
                    e.getMessage()
            );
            return 1;
        }
    }
}
