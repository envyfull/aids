/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.profile;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.click.MenuClickHandler;
import br.com.dragonmc.core.common.member.Member;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PreferencesInventory {
    private boolean message = false;

    public PreferencesInventory(Player player, int page, long wait) {
        MenuInventory menuInventory = new MenuInventory("\u00a77\u00a7%inventory-preferences%\u00a7", 4);
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        this.create(player, "Bate-papo", "Receber mensagens no bate-papo do servidor", Material.PAPER, member.getMemberConfiguration().isSeeingChat(), 11, menuInventory, (p, inv, type, stack, s) -> {
            if (wait > System.currentTimeMillis()) {
                if (this.message) {
                    return;
                }
                this.message = true;
                member.sendMessage("\u00a7cVoc\u00ea precisa esperar para mudar uma configura\u00e7\u00e3o.");
                return;
            }
            member.getMemberConfiguration().setSeeingChat(!member.getMemberConfiguration().isSeeingChat());
            new PreferencesInventory(player, page, System.currentTimeMillis() + 500L);
        });
        this.create(player, "Conversa privada", "Receber mensagens privadas no servidor", Material.PAPER, member.getMemberConfiguration().isTellEnabled(), 12, menuInventory, (p, inv, type, stack, s) -> {
            if (wait > System.currentTimeMillis()) {
                if (this.message) {
                    return;
                }
                this.message = true;
                member.sendMessage("\u00a7cVoc\u00ea precisa esperar para mudar uma configura\u00e7\u00e3o.");
                return;
            }
            member.getMemberConfiguration().setTellEnabled(!member.getMemberConfiguration().isTellEnabled());
            new PreferencesInventory(player, page, System.currentTimeMillis() + 500L);
        });
        this.create(player, "Convite de party", "Receber convites para party", Material.PAPER, member.getMemberConfiguration().isPartyInvites(), 13, menuInventory, (p, inv, type, stack, s) -> {
            if (wait > System.currentTimeMillis()) {
                if (this.message) {
                    return;
                }
                this.message = true;
                member.sendMessage("\u00a7cVoc\u00ea precisa esperar para mudar uma configura\u00e7\u00e3o.");
                return;
            }
            member.getMemberConfiguration().setPartyInvites(!member.getMemberConfiguration().isPartyInvites());
            new PreferencesInventory(player, page, System.currentTimeMillis() + 500L);
        });
        menuInventory.open(player);
        player.updateInventory();
    }

    public PreferencesInventory(Player player) {
        new PreferencesInventory(player, 1, -1L);
    }

    public void create(Player player, String name, String description, Material material, Boolean active, int slot, MenuInventory menuInventory, MenuClickHandler handler) {
        menuInventory.setItem(slot, new ItemBuilder().name((active != false ? "\u00a7a" : "\u00a7c") + name).type(material).lore("\u00a77" + description).build(), handler);
        menuInventory.setItem(slot + 9, new ItemBuilder().name((active != false ? "\u00a7a" : "\u00a7c") + name).type(Material.INK_SACK).durability(active != false ? 10 : 8).lore(active != false ? "\u00a77Clique para desativar." : "\u00a77Clique para ativar.").build(), handler);
    }
}

