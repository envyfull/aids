/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.wrappers.WrappedSignedProperty
 */
package br.com.dragonmc.core.bukkit.command.register;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import java.util.Optional;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.bukkit.menu.profile.SkinInventory;
import br.com.dragonmc.core.bukkit.utils.player.PlayerAPI;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.member.configuration.LoginConfiguration;
import br.com.dragonmc.core.common.packet.types.skin.SkinChange;
import br.com.dragonmc.core.common.utils.skin.Skin;

public class SkinCommand
implements CommandClass {
    @CommandFramework.Command(name="skin.#", runAsync=true, console=false)
    public void skinresetCommand(CommandArgs cmdArgs) {
        BukkitMember sender = cmdArgs.getSenderAsMember(BukkitMember.class);
        if (!sender.isCustomSkin()) {
            sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o est\u00e1 usando uma skin customizada.");
            return;
        }
        Skin skin = null;
        if (sender.getLoginConfiguration().getAccountType() == LoginConfiguration.AccountType.PREMIUM) {
            WrappedSignedProperty changePlayerSkin = PlayerAPI.changePlayerSkin(sender.getPlayer(), sender.getName(), sender.getUniqueId(), true);
            skin = new Skin(sender.getName(), sender.getUniqueId(), changePlayerSkin.getValue(), changePlayerSkin.getSignature());
        } else {
            WrappedSignedProperty changePlayerSkin = PlayerAPI.changePlayerSkin(sender.getPlayer(), CommonConst.DEFAULT_SKIN.getValue(), CommonConst.DEFAULT_SKIN.getSignature(), true);
            skin = new Skin(sender.getName(), sender.getUniqueId(), changePlayerSkin.getValue(), changePlayerSkin.getSignature());
        }
        sender.setSkin(skin, false);
        sender.sendMessage("\u00a7aSua skin foi resetada com sucesso..");
        sender.putCooldown("command.skin", 120L);
        CommonPlugin.getInstance().getServerData().sendPacket(new SkinChange(sender));
    }

    @CommandFramework.Command(name="skin", runAsync=true, console=false)
    public void skinCommand(CommandArgs cmdArgs) {
        BukkitMember sender = cmdArgs.getSenderAsMember(BukkitMember.class);
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            new SkinInventory(sender.getPlayer());
            return;
        }
        if (!sender.hasPermission("command.skin")) {
            sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o tem permiss\u00e3o para usar skins fora do cat\u00e1logo de skins.");
            return;
        }
        if (sender.hasCooldown("command.skin") && !sender.hasPermission("command.admin")) {
            sender.sendMessage("\u00a7cVoc\u00ea precisa esperar " + sender.getCooldownFormatted("command.skin") + " para usar eses comando novamente.");
            return;
        }
        String playerName = args[0];
        if (!PlayerAPI.validateName(playerName)) {
            sender.sendMessage("\u00a7cO nome inserido \u00e9 inv\u00e1lido.");
            return;
        }
        Optional<Skin> optional = CommonPlugin.getInstance().getSkinData().loadData(playerName);
        if (!optional.isPresent()) {
            sender.sendMessage("\u00a7cO jogador n\u00e3o possui conta na mojang.");
            return;
        }
        Skin skin = optional.get();
        PlayerAPI.changePlayerSkin(sender.getPlayer(), skin.getValue(), skin.getSignature(), true);
        sender.setSkin(skin, true);
        sender.sendMessage("\u00a7aSua skin foi alterada para " + playerName + ".");
        sender.putCooldown("command.skin", 120L);
        CommonPlugin.getInstance().getServerData().sendPacket(new SkinChange(sender));
    }
}

