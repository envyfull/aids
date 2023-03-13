/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.staff;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.click.MenuClickHandler;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.member.configuration.MemberConfiguration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AdminInventory {
    public AdminInventory(Player player) {
        this(player, 0L);
    }

    public AdminInventory(Player player, long wait) {
        MenuInventory menuInventory = new MenuInventory("\u00a77Admin Config", 4);
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        MenuClickHandler handler = (p, inv, type, stack, s) -> {
            if (wait > System.currentTimeMillis()) {
                player.sendMessage("\u00a7cAguarde para alterar outra configura\u00e7\u00e3o.");
                return;
            }
            int code = member.getMemberConfiguration().getAdminModeJoin();
            code = code == 2 ? 0 : ++code;
            member.getMemberConfiguration().setAdminModeJoin(code);
            new AdminInventory(player, System.currentTimeMillis() + 500L);
            p.updateInventory();
        };
        String color = member.getMemberConfiguration().getAdminModeJoin() == 0 ? "\u00a7c" : (member.getMemberConfiguration().getAdminModeJoin() == 1 ? "\u00a7a" : "\u00a7e");
        menuInventory.setItem(10, new ItemBuilder().name(color + "Entrar no admin").type(Material.PAPER).lore("\u00a77Entre automaticamente no modo admin ao entrar no servidor.").build(), handler);
        menuInventory.setItem(19, new ItemBuilder().name(color + "Entrar no admin").type(Material.INK_SACK).durability(member.getMemberConfiguration().getAdminModeJoin() == 0 ? 8 : (member.getMemberConfiguration().getAdminModeJoin() == 1 ? 10 : 11)).lore(member.getMemberConfiguration().getAdminModeJoin() == 0 ? "\u00a77Clique para ativar" : (member.getMemberConfiguration().getAdminModeJoin() == 1 ? "\u00a77Clique para mudar para o modo alternado" : "\u00a77Clique para desativar")).build(), handler);
        this.create(player, "Items do admin", "Remover items ao entrar no modo admin.", Material.PAPER, member.getMemberConfiguration().isAdminRemoveItems(), 11, menuInventory, (p, inv, type, stack, s) -> {
            if (wait > System.currentTimeMillis()) {
                player.sendMessage("\u00a7cAguarde para alterar outra configura\u00e7\u00e3o.");
                return;
            }
            member.getMemberConfiguration().setAdminRemoveItems(!member.getMemberConfiguration().isAdminRemoveItems());
            new AdminInventory(player, System.currentTimeMillis() + 500L);
        });
        this.create(player, "Staffchat", "Visualizar mensagens do staffchat.", Material.PAPER, member.getMemberConfiguration().isSeeingStaffChat(), 12, menuInventory, (p, inv, type, stack, s) -> {
            if (wait > System.currentTimeMillis()) {
                player.sendMessage("\u00a7cAguarde para alterar outra configura\u00e7\u00e3o.");
                return;
            }
            member.getMemberConfiguration().setSeeingStaffChat(!member.getMemberConfiguration().isSeeingStaffChat());
            new AdminInventory(player, System.currentTimeMillis() + 500L);
        });
        this.create(player, "Stafflog", "Visualizar as logs da staff e monitorar o que est\u00e3o fazendo no servidor", Material.PAPER, member.getMemberConfiguration().isSeeingLogs(), 13, menuInventory, (p, inv, type, stack, s) -> {
            if (wait > System.currentTimeMillis()) {
                player.sendMessage("\u00a7cAguarde para alterar outra configura\u00e7\u00e3o.");
                return;
            }
            member.getMemberConfiguration().setSeeingLogs(!member.getMemberConfiguration().isSeeingLogs());
            new AdminInventory(player, System.currentTimeMillis() + 500L);
        });
        this.create(player, "Espectadores", "Ver os espectadores das partidas", Material.PAPER, member.getMemberConfiguration().isSpectatorsEnabled(), 14, menuInventory, (p, inv, type, stack, s) -> {
            if (wait > System.currentTimeMillis()) {
                player.sendMessage("\u00a7cAguarde para alterar outra configura\u00e7\u00e3o.");
                return;
            }
            member.getMemberConfiguration().setSpectatorsEnabled(!member.getMemberConfiguration().isSpectatorsEnabled());
            BukkitCommon.getInstance().getVanishManager().updateVanishToPlayer(p);
            new AdminInventory(player, System.currentTimeMillis() + 500L);
        });
        this.create(player, "Reports", "Receba um aviso no chat sempre que um report for feito", Material.PAPER, member.getMemberConfiguration().isReportsEnabled(), 15, menuInventory, (p, inv, type, stack, s) -> {
            if (wait > System.currentTimeMillis()) {
                player.sendMessage("\u00a7cAguarde para alterar outra configura\u00e7\u00e3o.");
                return;
            }
            member.getMemberConfiguration().setReportsEnabled(!member.getMemberConfiguration().isReportsEnabled());
            new AdminInventory(player, System.currentTimeMillis() + 500L);
        });
        this.create(player, "Anticheat", "Receba um aviso no chat sempre que um report for feito", Material.PAPER, member.getMemberConfiguration().isAnticheatEnabled(), 16, menuInventory, (p, inv, type, stack, s) -> {
            if (wait > System.currentTimeMillis()) {
                player.sendMessage("\u00a7cAguarde para alterar outra configura\u00e7\u00e3o.");
                return;
            }
            member.getMemberConfiguration().setCheatState(member.getMemberConfiguration().isAnticheatEnabled() ? MemberConfiguration.CheatState.DISABLED : MemberConfiguration.CheatState.ENABLED);
            new AdminInventory(player, System.currentTimeMillis() + 500L);
        });
        menuInventory.open(player);
    }

    public void create(Player player, String name, String description, Material material, Boolean active, int slot, MenuInventory menuInventory, MenuClickHandler handler) {
        menuInventory.setItem(slot, new ItemBuilder().name((active != false ? "\u00a7a" : "\u00a7c") + name).type(material).lore("\u00a77" + description).build(), handler);
        menuInventory.setItem(slot + 9, new ItemBuilder().name((active != false ? "\u00a7a" : "\u00a7c") + name).type(Material.INK_SACK).durability(active != false ? 10 : 8).lore(active != false ? "\u00a77Clique para desativar." : "\u00a77Clique para ativar.").build(), handler);
    }
}

