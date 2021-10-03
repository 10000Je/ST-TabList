package com.stuudent.tablist.schedulers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.stuudent.tablist.TabListCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class TabListScheduler implements Runnable{

    @Override
    public void run() {
        PacketContainer infoPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        PacketContainer hfPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        hfPacket.getModifier().writeDefaults();
        infoPacket.getModifier().writeDefaults();
        List<PlayerInfoData> playerInfoDataList = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()) {
            try {
                Object entityPlayer = MinecraftReflection.getCraftPlayerClass().getDeclaredMethod("getHandle").invoke(player);
                int ping = (int) MinecraftReflection.getEntityPlayerClass().getDeclaredField("ping").get(entityPlayer);
                PlayerInfoData newPlayerInfoData = new PlayerInfoData(new WrappedGameProfile(player.getUniqueId(), player.getName()), ping,
                        EnumWrappers.NativeGameMode.valueOf(player.getGameMode().name()), WrappedChatComponent.fromText(player.getDisplayName()));
                playerInfoDataList.add(newPlayerInfoData);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        StringBuilder header = new StringBuilder();
        StringBuilder footer = new StringBuilder();
        for(int i=0; i < TabListCore.cf.getStringList("Header").size(); i++) {
            if(i == TabListCore.cf.getStringList("Header").size()-1) {
                header.append(ChatColor.translateAlternateColorCodes('&', TabListCore.cf.getStringList("Header").get(i)));
            } else {
                header.append(ChatColor.translateAlternateColorCodes('&', TabListCore.cf.getStringList("Header").get(i))).append("\n");
            }
        }
        for(int i=0; i < TabListCore.cf.getStringList("Footer").size(); i++) {
            if(i == TabListCore.cf.getStringList("Footer").size()-1) {
                footer.append(ChatColor.translateAlternateColorCodes('&', TabListCore.cf.getStringList("Footer").get(i)));
            } else {
                footer.append(ChatColor.translateAlternateColorCodes('&', TabListCore.cf.getStringList("Footer").get(i))).append("\n");
            }
        }
        hfPacket.getChatComponents().write(0, WrappedChatComponent.fromText(header.toString()));
        hfPacket.getChatComponents().write(1, WrappedChatComponent.fromText(footer.toString()));
        infoPacket.getPlayerInfoDataLists().write(0, playerInfoDataList);
        for(Player player : Bukkit.getOnlinePlayers()) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, infoPacket);
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, hfPacket);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
