/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.events.ListenerPriority
 *  com.comphenix.protocol.events.PacketAdapter
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.events.PacketEvent
 *  com.comphenix.protocol.events.PacketListener
 *  com.comphenix.protocol.wrappers.WrappedChatComponent
 *  com.comphenix.protocol.wrappers.WrappedDataWatcher
 *  com.comphenix.protocol.wrappers.WrappedWatchableObject
 *  com.google.common.base.Splitter
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.EntityType
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.core.bukkit.protocol.impl;

import br.com.dragonmc.core.bukkit.protocol.PacketInjector;
import br.com.dragonmc.core.bukkit.utils.player.PlayerHelper;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.base.Splitter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.Member;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class TranslationInjector
implements PacketInjector {
    @Override
    public void inject(Plugin plugin) {
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(plugin, ListenerPriority.NORMAL, new PacketType[]{PacketType.Play.Server.CHAT, PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.OPEN_WINDOW, PacketType.Play.Server.UPDATE_SIGN, PacketType.Play.Server.SCOREBOARD_OBJECTIVE, PacketType.Play.Server.SCOREBOARD_TEAM, PacketType.Play.Server.SCOREBOARD_SCORE, PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER, PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketType.Play.Server.ENTITY_METADATA, PacketType.Play.Server.TITLE}){

            public void onPacketSending(PacketEvent event) {
                if (event.getPlayer() == null) {
                    return;
                }
                if (event.getPlayer().getUniqueId() == null) {
                    return;
                }
                if (event.getPacket() == null) {
                    return;
                }
                if (event.isReadOnly()) {
                    return;
                }
                Language lang = Member.getLanguage(event.getPlayer().getUniqueId());
                if (event.getPacketType() == PacketType.Play.Server.CHAT) {
                    PacketContainer packet = event.getPacket().deepClone();
                    for (int i = 0; i < packet.getChatComponents().size(); ++i) {
                        WrappedChatComponent chatComponent = (WrappedChatComponent)packet.getChatComponents().read(i);
                        if (chatComponent == null) continue;
                        packet.getChatComponents().write(i, WrappedChatComponent.fromJson((String) PlayerHelper.translate(lang, chatComponent.getJson())));
                    }
                    event.setPacket(packet);
                } else if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
                    PacketContainer packet = event.getPacket().deepClone();
                    for (ItemStack item : (ItemStack[])packet.getItemArrayModifier().read(0)) {
                        if (item == null) continue;
                        PlayerHelper.translate(lang, item);
                    }
                    event.setPacket(packet);
                } else if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
                    PacketContainer packet = event.getPacket().deepClone();
                    ItemStack item = (ItemStack)packet.getItemModifier().read(0);
                    packet.getItemModifier().write(0, PlayerHelper.translate(lang, item));
                    event.setPacket(packet);
                } else if (event.getPacketType() == PacketType.Play.Server.TITLE) {
                    PacketContainer packet = event.getPacket().deepClone();
                    WrappedChatComponent component = (WrappedChatComponent)event.getPacket().getChatComponents().read(0);
                    if (component == null) {
                        return;
                    }
                    packet.getChatComponents().write(0, WrappedChatComponent.fromJson((String)PlayerHelper.translate(lang, component.getJson())));
                    event.setPacket(packet);
                } else if (event.getPacketType() == PacketType.Play.Server.SCOREBOARD_SCORE) {
                    PacketContainer packet = event.getPacket().deepClone();
                    String message = (String)event.getPacket().getStrings().read(0);
                    packet.getStrings().write(0, PlayerHelper.translate(lang, message));
                    event.setPacket(packet);
                } else if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
                    PacketContainer packet = event.getPacket().deepClone();
                    WrappedChatComponent component = (WrappedChatComponent)event.getPacket().getChatComponents().read(0);
                    JsonElement element = JsonParser.parseString((String)component.getJson());
                    if (!(element instanceof JsonObject) || !((JsonObject)element).has("translate")) {
                        String message = PlayerHelper.translate(lang, element.getAsString());
                        message = message.substring(0, (message = PlayerHelper.translate(lang, element.getAsString())).length() > 32 ? 32 : message.length());
                        packet.getChatComponents().write(0, WrappedChatComponent.fromText((String)message));
                        event.setPacket(packet);
                    }
                } else if (event.getPacketType() == PacketType.Play.Server.SCOREBOARD_OBJECTIVE) {
                    PacketContainer packet = event.getPacket().deepClone();
                    String message = (String)event.getPacket().getStrings().read(1);
                    packet.getStrings().write(1, PlayerHelper.translate(lang, message));
                    event.setPacket(packet);
                } else if (event.getPacketType() == PacketType.Play.Server.SCOREBOARD_TEAM) {
                    String suffix;
                    String prefix;
                    PacketContainer packet = event.getPacket().deepClone();
                    String pre = (String)packet.getStrings().read(2);
                    String su = (String)packet.getStrings().read(3);
                    boolean matched = false;
                    Matcher matcher = CommonConst.TRANSLATE_PATTERN.matcher(pre);
                    while (matcher.find()) {
                        pre = pre.replace(matcher.group(), CommonPlugin.getInstance().getPluginInfo().translate(lang, matcher.group(2), new String[0]));
                        matched = true;
                    }
                    matcher = CommonConst.TRANSLATE_PATTERN.matcher(su);
                    while (matcher.find()) {
                        su = su.replace(matcher.group(), CommonPlugin.getInstance().getPluginInfo().translate(lang, matcher.group(2), new String[0]));
                        matched = true;
                    }
                    if (matched && pre.length() <= 16 && su.length() <= 16) {
                        packet.getStrings().write(2, pre);
                        packet.getStrings().write(3, su);
                        event.setPacket(packet);
                        return;
                    }
                    String text = (String)packet.getStrings().read(2) + (String)packet.getStrings().read(3);
                    matcher = CommonConst.TRANSLATE_PATTERN.matcher(text);
                    matched = false;
                    while (matcher.find()) {
                        text = text.replace(matcher.group(), CommonPlugin.getInstance().getPluginInfo().translate(lang, matcher.group(2), new String[0]));
                        matched = true;
                    }
                    if (!matched) {
                        return;
                    }
                    Iterator iterator = Splitter.fixedLength((int)16).split((CharSequence)text).iterator();
                    String str = (String)iterator.next();
                    if (str.endsWith("\u00a7")) {
                        prefix = str = str.substring(0, str.length() - 1);
                        if (iterator.hasNext()) {
                            String next = (String)iterator.next();
                            if (!next.startsWith("\u00a7")) {
                                String str2 = "\u00a7" + next;
                                if (str2.length() > 16) {
                                    str2 = str2.substring(0, 16);
                                }
                                suffix = str2;
                            } else {
                                suffix = next;
                            }
                        } else {
                            suffix = "";
                        }
                    } else if (iterator.hasNext()) {
                        String next = (String)iterator.next();
                        if (!next.startsWith("\u00a7")) {
                            String colors = ChatColor.getLastColors((String)str);
                            String str3 = colors + next;
                            if (str3.length() > 16) {
                                str3 = str3.substring(0, 16);
                            }
                            prefix = str;
                            suffix = str3;
                        } else {
                            prefix = str;
                            suffix = next;
                        }
                    } else {
                        prefix = str;
                        suffix = "";
                    }
                    packet.getStrings().write(2, prefix);
                    packet.getStrings().write(3, suffix);
                    event.setPacket(packet);
                } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER) {
                    PacketContainer packet = event.getPacket().deepClone();
                    WrappedChatComponent header = (WrappedChatComponent)packet.getChatComponents().read(0);
                    WrappedChatComponent footer = (WrappedChatComponent)packet.getChatComponents().read(1);
                    if (header != null) {
                        packet.getChatComponents().write(0, WrappedChatComponent.fromJson((String)PlayerHelper.translate(lang, header.getJson())));
                    }
                    if (footer != null) {
                        packet.getChatComponents().write(1, WrappedChatComponent.fromJson((String)PlayerHelper.translate(lang, footer.getJson())));
                    }
                    event.setPacket(packet);
                } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
                    PacketContainer packet = event.getPacket().deepClone();
                    List<WrappedWatchableObject> objects = (List)packet.getWatchableCollectionModifier().read(0);
                    for (WrappedWatchableObject obj : objects) {
                        if (obj.getIndex() != 2) continue;
                        String str = (String)obj.getRawValue();
                        str = PlayerHelper.translate(lang, str);
                        obj.setValue((Object)str);
                        break;
                    }
                    event.setPacket(packet);
                } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
                    PacketContainer packet = event.getPacket();
                    int type = (Integer)packet.getIntegers().read(1);
                    if (type != EntityType.ARMOR_STAND.getTypeId()) {
                        return;
                    }
                    PacketContainer packetClone = event.getPacket().deepClone();
                    List<WrappedWatchableObject> objects = ((WrappedDataWatcher)packetClone.getDataWatcherModifier().read(0)).getWatchableObjects();
                    for (WrappedWatchableObject obj : objects) {
                        if (obj.getIndex() != 2) continue;
                        String str = (String)obj.getRawValue();
                        str = PlayerHelper.translate(lang, str);
                        obj.setValue((Object)str);
                        break;
                    }
                    event.setPacket(packetClone);
                }
            }
        });
    }
}

