/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.MenuItem;
import br.com.dragonmc.core.bukkit.utils.player.PlayerAPI;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.packet.types.skin.SkinChange;
import br.com.dragonmc.core.common.utils.skin.Skin;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SkinInventory {
    private static final int ITEMS_PER_PAGE = 21;
    private static final List<SkinModel> SKIN_LIST = Arrays.asList(SkinInventory.from("yandv"), SkinInventory.from("yukiritoFLAME", "yukirito"), SkinInventory.from("Budokkan"), SkinInventory.from("ClonexD"), SkinInventory.from("NeoxGamer_"), SkinInventory.from("AnjooGaming"), SkinInventory.from("Kotcka"), SkinInventory.from("Console"));

    public SkinInventory(Player player) {
        this(player, InventoryType.PRINCIPAL, 1);
    }

    public SkinInventory(Player player, InventoryType type, int page) {
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        MenuInventory menuInventory = new MenuInventory("\u00a77Cat\u00e1logo de skins", 5);
        if (type == InventoryType.PRINCIPAL) {
            menuInventory.setItem(13, new ItemBuilder().name("\u00a7a" + member.getSkin().getPlayerName()).type(Material.SKULL_ITEM).lore("", "\u00a77Fonte: \u00a7a" + (member.isCustomSkin() ? "Customizada" : "Padr\u00e3o")).durability(3).skin(member.getPlayerName()).build());
            menuInventory.setItem(30, new ItemBuilder().name("\u00a7aCostumizar sua skin").type(Material.NAME_TAG).lore("", "\u00a77Escolha uma skin customizada", "\u00a77baseada em um nickname.", "", "\u00a7cApenas para VIPs.", "", "\u00a7eClique para ver mais.").build(), (p, inv, t, stack, slot) -> {
                p.closeInventory();
                p.performCommand("skin");
            });
            menuInventory.setItem(32, new ItemBuilder().name("\u00a7aBiblioteca").type(Material.BOOK).lore("", "\u00a77Confira o pacote de ", "\u00a77skins padr\u00e3o dispon\u00edveis", "\u00a77de gra\u00e7a.", "", "\u00a7eClique para ver mais.").build(), (p, inv, t, stack, slot) -> new SkinInventory(player, InventoryType.LIBRARY, 1));
            menuInventory.setItem(40, new ItemBuilder().name("\u00a7aVoltar").type(Material.ARROW).build(), (p, inv, t, stack, slot) -> new ProfileInventory(player));
        } else {
            ArrayList<MenuItem> items = new ArrayList<MenuItem>();
            for (SkinModel skinModel : SKIN_LIST) {
                ItemBuilder itemBuilder = new ItemBuilder().name("\u00a7a" + skinModel.getName()).type(Material.SKULL_ITEM).durability(3).lore("\u00a7eClique para selecionar.");
                if (skinModel.getSkin() != null && skinModel.getSkin().getValue() != null) {
                    itemBuilder.skin(skinModel.getSkin().getValue(), skinModel.getSkin().getSignature() == null ? "" : skinModel.getSkin().getSignature());
                } else {
                    itemBuilder.skin(skinModel.getSkin().getPlayerName());
                }
                items.add(new MenuItem(itemBuilder.build(), (p, inv, t, stack, slot) -> {
                    player.closeInventory();
                    PlayerAPI.changePlayerSkin(player, skinModel.getSkin().getValue(), skinModel.getSkin().getSignature(), true);
                    member.setSkin(skinModel.getSkin(), true);
                    CommonPlugin.getInstance().getServerData().sendPacket(new SkinChange(p.getUniqueId(), member.getSkin()));
                }));
            }
            int pageStart = 0;
            int pageEnd = 21;
            if (page > 1) {
                pageStart = (page - 1) * 21;
                pageEnd = page * 21;
            }
            if (pageEnd > items.size()) {
                pageEnd = items.size();
            }
            int w = 10;
            for (int i = pageStart; i < pageEnd; ++i) {
                MenuItem item = (MenuItem)items.get(i);
                menuInventory.setItem(item, w);
                if (w % 9 == 7) {
                    w += 3;
                    continue;
                }
                ++w;
            }
            if (page == 1) {
                menuInventory.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("\u00a7aVoltar").build(), (p, inv, t, stack, s) -> new SkinInventory(player)), 39);
            } else {
                menuInventory.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("\u00a7a\u00a7%page%\u00a7 " + (page - 1)).build(), (p, inv, t, stack, s) -> new SkinInventory(p, type, page - 1)), 39);
            }
            if (Math.ceil(items.size() / 21) + 1.0 > (double)page) {
                menuInventory.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("\u00a7a\u00a7%page%\u00a7 " + (page + 1)).build(), (p, inv, t, stack, s) -> new SkinInventory(p, type, page + 1)), 41);
            }
        }
        menuInventory.open(player);
    }

    public static SkinModel from(String name, String displayName) {
        return new SkinModel(displayName, CommonPlugin.getInstance().getSkinData().loadData(name).orElse(new Skin(name, CommonConst.CONSOLE_ID, "", "")));
    }

    public static SkinModel from(String name) {
        return SkinInventory.from(name, name);
    }

    public static class SkinModel {
        private String name;
        private Skin skin;

        public SkinModel(String name, Skin skin) {
            this.name = name;
            this.skin = skin;
        }

        public String getName() {
            return this.name;
        }

        public Skin getSkin() {
            return this.skin;
        }
    }

    public static enum InventoryType {
        PRINCIPAL,
        LIBRARY;

    }
}

