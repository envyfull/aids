/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.events.ListenerPriority
 *  com.comphenix.protocol.events.PacketAdapter
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.events.PacketEvent
 *  com.comphenix.protocol.events.PacketListener
 *  com.comphenix.protocol.wrappers.WrappedChatComponent
 *  io.netty.buffer.ByteBuf
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.core.bukkit.protocol.impl;

import br.com.dragonmc.core.bukkit.BukkitMain;
import br.com.dragonmc.core.bukkit.protocol.PacketInjector;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import io.netty.buffer.ByteBuf;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.dragonmc.core.common.CommonPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LimiterInjector
implements PacketInjector {
    private final Pattern pattern = Pattern.compile(".*\\$\\{[^}]*\\}.*");

    @Override
    public void inject(Plugin plugin) {
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter((Plugin) BukkitMain.getInstance(), ListenerPriority.LOWEST, new PacketType[]{PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.CUSTOM_PAYLOAD}){

            public void onPacketReceiving(PacketEvent event) {
                if (event.getPlayer() == null) {
                    return;
                }
                if (event.getPacketType() == PacketType.Play.Client.WINDOW_CLICK) {
                    if ((Integer)event.getPacket().getModifier().getValues().get(1) >= 100) {
                        event.setCancelled(true);
                        LimiterInjector.this.disconnect(event.getPlayer(), "\u00a7cYou are sending too many packets.");
                        CommonPlugin.getInstance().debug("The player " + event.getPlayer().getName() + " is trying to crash the server (WindowClick)");
                    }
                } else {
                    String packetName = (String)event.getPacket().getStrings().getValues().get(0);
                    if ((packetName.equals("MC|BEdit") || packetName.equals("MC|BSign")) && ((ByteBuf)event.getPacket().getModifier().getValues().get(1)).capacity() > 7500) {
                        event.setCancelled(true);
                        LimiterInjector.this.disconnect(event.getPlayer(), "\u00a7cYou are sending too many packets.");
                        CommonPlugin.getInstance().debug("The player " + event.getPlayer().getName() + " is trying to crash the server (CustomPayload)");
                    }
                }
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter((Plugin)BukkitMain.getInstance(), ListenerPriority.LOWEST, new PacketType[]{PacketType.Play.Server.CHAT, PacketType.Play.Client.CHAT, PacketType.Play.Client.WINDOW_CLICK}){

            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Server.CHAT) {
                    PacketContainer packetContainer = event.getPacket();
                    WrappedChatComponent wrappedChatComponent = (WrappedChatComponent)packetContainer.getChatComponents().getValues().get(0);
                    if (wrappedChatComponent == null) {
                        return;
                    }
                    String jsonMessage = wrappedChatComponent.getJson();
                    if (jsonMessage.indexOf(36) == -1) {
                        return;
                    }
                    if (LimiterInjector.this.matches(jsonMessage)) {
                        event.setCancelled(true);
                        packetContainer.getChatComponents().write(0, WrappedChatComponent.fromText((String)""));
                    }
                }
            }

            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.CHAT) {
                    PacketContainer packetContainer = event.getPacket();
                    String message = (String)packetContainer.getStrings().read(0);
                    if (message.indexOf(36) == -1) {
                        return;
                    }
                    if (LimiterInjector.this.matches(message)) {
                        event.setCancelled(true);
                        packetContainer.getStrings().write(0, "");
                        CommonPlugin.getInstance().debug("The player " + event.getPlayer().getName() + " is trying to crash the server (Chat)");
                    }
                }
            }
        });
    }

    private void disconnect(Player player, String string) {
    }

    private boolean matches(String message) {
        Matcher matcher = this.pattern.matcher(message.replaceAll("[^\\x00-\\x7F]", "").toLowerCase(Locale.ROOT));
        return matcher.find();
    }
}

