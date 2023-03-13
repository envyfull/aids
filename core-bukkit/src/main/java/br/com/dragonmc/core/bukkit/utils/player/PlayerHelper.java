/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.EnumWrappers$TitleAction
 *  com.comphenix.protocol.wrappers.WrappedChatComponent
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package br.com.dragonmc.core.bukkit.utils.player;

import br.com.dragonmc.core.bukkit.utils.PacketBuilder;
import br.com.dragonmc.core.bukkit.utils.ProtocolVersion;
import br.com.dragonmc.core.bukkit.utils.StringLoreUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerHelper {
    public static void broadcastHeader(String header) {
        PlayerHelper.broadcastHeaderAndFooter(header, null);
    }

    public static void broadcastFooter(String footer) {
        PlayerHelper.broadcastHeaderAndFooter(null, footer);
    }

    public static void broadcastHeaderAndFooter(String header, String footer) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerHelper.setHeaderAndFooter(player, header, footer);
        }
    }

    public static void setHeader(Player p, String header) {
        PlayerHelper.setHeaderAndFooter(p, header, null);
    }

    public static void setFooter(Player p, String footer) {
        PlayerHelper.setHeaderAndFooter(p, null, footer);
    }

    public static void setHeaderAndFooter(Player p, String rawHeader, String rawFooter) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        packet.getChatComponents().write(0, (Object)WrappedChatComponent.fromText((String)rawHeader));
        packet.getChatComponents().write(1, (Object)WrappedChatComponent.fromText((String)rawFooter));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet);
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void title(Player player, String title, String subTitle) {
        if (ProtocolVersion.getProtocolVersion(player).getId() >= 47) {
            PlayerHelper.sendPacket(player, new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, EnumWrappers.TitleAction.TITLE).writeChatComponents(0, WrappedChatComponent.fromText((String)title)).build());
            PlayerHelper.sendPacket(player, new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, EnumWrappers.TitleAction.SUBTITLE).writeChatComponents(0, WrappedChatComponent.fromText((String)subTitle)).build());
            PlayerHelper.sendPacket(player, new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, EnumWrappers.TitleAction.TIMES).writeInteger(0, 10).writeInteger(1, 20).writeInteger(2, 20).build());
        }
    }

    public static void subtitle(Player player, String subTitle) {
        if (ProtocolVersion.getProtocolVersion(player).getId() >= 47) {
            PlayerHelper.sendPacket(player, new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, EnumWrappers.TitleAction.SUBTITLE).writeChatComponents(0, WrappedChatComponent.fromText((String)subTitle)).build());
        }
    }

    public static void title(Player player, String title, String subTitle, int fadeIn, int stayIn, int fadeOut) {
        if (ProtocolVersion.getProtocolVersion(player).getId() >= 47) {
            PlayerHelper.sendPacket(player, new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, EnumWrappers.TitleAction.TITLE).writeChatComponents(0, WrappedChatComponent.fromText((String)title)).build());
            PlayerHelper.sendPacket(player, new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, EnumWrappers.TitleAction.SUBTITLE).writeChatComponents(0, WrappedChatComponent.fromText((String)subTitle)).build());
            PlayerHelper.sendPacket(player, new PacketBuilder(PacketType.Play.Server.TITLE).writeTitleAction(0, EnumWrappers.TitleAction.TIMES).writeInteger(0, fadeIn).writeInteger(1, stayIn).writeInteger(2, fadeOut).build());
        }
    }

    public static void actionbar(Player player, String text) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.CHAT);
        packet.getChatComponents().write(0, (Object)WrappedChatComponent.fromJson((String)("{\"text\":\"" + text + " \"}")));
        packet.getBytes().write(0, (Object)2);
        PlayerHelper.sendPacket(player, packet);
    }

    public static void broadcastActionBar(String text) {
        Bukkit.getOnlinePlayers().forEach(player -> PlayerHelper.actionbar(player, text));
    }

    public static void sendPacket(Player player, PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static String translate(Language lang, String string) {
        return CommonPlugin.getInstance().getPluginInfo().findAndTranslate(lang, string);
    }

    public static ItemStack translate(Language lang, ItemStack item) {
        if (item != null && item.getType() != Material.AIR && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                String name = meta.getDisplayName();
                meta.setDisplayName(PlayerHelper.translate(lang, name));
            }
            if (meta.hasLore()) {
                ArrayList<String> lore = new ArrayList<String>();
                for (String line : meta.getLore()) {
                    if ((line = PlayerHelper.translate(lang, line)).contains("\n")) {
                        String[] split = line.split("\n");
                        for (int i = 0; i < split.length; ++i) {
                            lore.addAll(StringLoreUtils.formatForLore(split[i]));
                        }
                        continue;
                    }
                    lore.addAll(StringLoreUtils.formatForLore(line));
                }
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}

