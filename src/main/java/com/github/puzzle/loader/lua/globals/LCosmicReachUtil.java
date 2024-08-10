package com.github.puzzle.loader.lua.globals;

import finalforeach.cosmicreach.chat.Chat;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.world.World;

public class LCosmicReachUtil {

    public static Chat getChat() {
        return Chat.MAIN_CHAT;
    }

    public static InGame getInGameMenu() {
        return InGame.IN_GAME;
    }

    public static World getWorld() {
        return InGame.world;
    }

    public static Player getLocalPlayer() {
        return InGame.getLocalPlayer();
    }

}
