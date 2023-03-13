/*     */
package br.com.dragonmc.game.bedwars.command;
/*     */
/*     */

import java.lang.reflect.Field;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.OptionalDouble;
/*     */ import java.util.OptionalInt;
/*     */ import br.com.dragonmc.core.bukkit.command.BukkitCommandArgs;
/*     */ import br.com.dragonmc.game.bedwars.GameMain;
/*     */ import br.com.dragonmc.game.bedwars.generator.Generator;
/*     */ import br.com.dragonmc.game.bedwars.generator.GeneratorType;
/*     */ import br.com.dragonmc.game.bedwars.island.Island;
/*     */ import br.com.dragonmc.game.bedwars.island.IslandColor;
/*     */ import br.com.dragonmc.game.bedwars.menu.creator.IslandCreatorInventory;
/*     */ import br.com.dragonmc.core.bukkit.member.BukkitMember;
/*     */ import br.com.dragonmc.core.bukkit.utils.Location;
/*     */ import br.com.dragonmc.core.bukkit.utils.item.ActionItemStack;
/*     */ import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
/*     */ import br.com.dragonmc.core.common.command.CommandArgs;
/*     */ import br.com.dragonmc.core.common.command.CommandClass;
/*     */ import br.com.dragonmc.core.common.command.CommandFramework.Command;
/*     */ import br.com.dragonmc.core.common.command.CommandFramework.Completer;
/*     */ import br.com.dragonmc.core.common.command.CommandSender;
/*     */ import br.com.dragonmc.core.common.utils.string.MessageBuilder;
/*     */ import br.com.dragonmc.core.common.utils.string.StringFormat;
/*     */
/*     */
import org.bukkit.Bukkit;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.inventory.ItemStack;

/*     */
/*     */
/*     */ public class ModeratorCommand
        /*     */ implements CommandClass
        /*     */ {
    /*     */
    @Command(name = "start", permission = "command.start")
    /*     */ public void startCommand(BukkitCommandArgs cmdArgs) {
        /*  43 */
        if (GameMain.getInstance().getState().isPregame()) {
            /*  44 */
            GameMain.getInstance().setTimer(true);
            /*  45 */
            GameMain.getInstance().startGame();
            /*     */
        } else {
            /*  47 */
            cmdArgs.getSender().sendMessage("§cA partida já iniciou.");
            /*     */
        }
        /*     */
    }

    /*     */
    @Command(name = "setprotection", permission = "command.island")
    /*     */ public void setprotectionCommand(CommandArgs cmdArgs) {
        /*  52 */
        CommandSender sender = cmdArgs.getSender();
        /*  53 */
        String[] args = cmdArgs.getArgs();
        /*     */
        /*  55 */
        if (args.length == 0) {
            /*  56 */
            sender.sendMessage(" §a» §fUse §a/setprotection <double>§f para mudar a proteção.");
            /*     */
            /*     */
            return;
            /*     */
        }
        /*  60 */
        OptionalDouble optionalDouble = StringFormat.parseDouble(args[0]);
        /*     */
        /*  62 */
        if (!optionalDouble.isPresent()) {
            /*  63 */
            sender.sendMessage(sender.getLanguage().t("invalid-format-double", new String[]{"%value%", args[0]}));
            /*     */
            /*     */
            return;
            /*     */
        }
        /*  67 */
        GameMain.getInstance().setMinimunDistanceToPlaceBlocks(optionalDouble.getAsDouble());
        /*  68 */
        sender.sendMessage("§aA proteção de blocos das ilhas foi alterado para " + optionalDouble.getAsDouble() + ".");
        /*     */
    }

    /*     */
    /*     */
    @Command(name = "island", permission = "command.island")
    /*     */ public void islandCommand(CommandArgs cmdArgs) {
        /*  73 */
        CommandSender sender = cmdArgs.getSender();
        /*  74 */
        Player player = ((BukkitMember) cmdArgs.getSenderAsMember(BukkitMember.class)).getPlayer();
        /*  75 */
        String[] args = cmdArgs.getArgs();
        /*     */
        /*  77 */
        if (args.length == 0) {
            /*  78 */
            sender.sendMessage("§%command-island-usage%§");
            /*     */
            /*     */
            return;
            /*     */
        }
        /*  82 */
        List<Island> islandList = GameMain.getInstance().getConfiguration().getList("islands", Island.class);
        /*     */
        /*  84 */
        if (args.length == 1) {
            /*  85 */
            if (args[0].equalsIgnoreCase("list")) {
                /*     */
                return;
                /*     */
            }
            /*     */
            /*  89 */
            if (args[0].equalsIgnoreCase("save")) {
                /*  90 */
                if (sender.isPlayer()) {
                    /*  91 */
                    player.performCommand("config bedwars save");
                    /*     */
                } else {
                    /*  93 */
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "config bedwars save");
                    /*     */
                }
                /*     */
                return;
                /*     */
            }
            /*  97 */
            IslandColor iColor = null;
            /*     */
            /*     */
            try {
                /* 100 */
                iColor = IslandColor.valueOf(args[0].toUpperCase());
                /* 101 */
            } catch (Exception ex) {
                /* 102 */
                sender.sendMessage(sender.getLanguage().t("command-island-color-not-exist", new String[]{"%island%", args[0]}));
                /*     */
                /*     */
                return;
                /*     */
            }
            /* 106 */
            IslandColor islandColor = iColor;
            /* 107 */
            Island island = islandList.stream().filter(i -> (i.getIslandColor() == islandColor)).findFirst().orElse(null);
            /*     */
            /* 109 */
            if (island == null) {
                /* 110 */
                sender.sendMessage(sender.getLanguage().t("command-island-island-not-exist", new String[]{"%island%",
/* 111 */                 StringFormat.formatString(islandColor.name()), "%islandColor%", "§" + islandColor
/* 112 */.getColor().getChar()}));
                /*     */
                /*     */
                return;
                /*     */
            }
            /* 116 */
            sender.sendMessage("  §fIsland " + islandColor
/* 117 */.getColor() + "§%" + islandColor.name().toLowerCase() + "-name%§");
            /*     */
            /* 119 */
            for (Field field : Island.class.getDeclaredFields()) {
                /*     */
                try {
                    /* 121 */
                    field.setAccessible(true);
                    /* 122 */
                    sender.sendMessage("    §f" + field.getName() + ": " + field.get(island));
                    /* 123 */
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    /* 124 */
                    e.printStackTrace();
                    /*     */
                }
                /*     */
            }
            /*     */
        } else {
            /* 128 */
            String fieldName;
            Field field;
            Location location;
            IslandColor iColor = null;
            /*     */
            /*     */
            try {
                /* 131 */
                iColor = IslandColor.valueOf(args[0].toUpperCase());
                /* 132 */
            } catch (Exception ex) {
                /* 133 */
                sender.sendMessage(sender.getLanguage().t("command-island-color-not-exist", new String[]{"%island%", args[0]}));
                /*     */
                /*     */
                return;
                /*     */
            }
            /* 137 */
            IslandColor islandColor = iColor;
            /*     */
            /* 139 */
            Island island = islandList.stream().filter(i -> (i.getIslandColor() == islandColor)).findFirst().orElse(null);
            /*     */
            /* 141 */
            if (!args[1].equalsIgnoreCase("create") && island == null) {
                /* 142 */
                sender.sendMessage("§%command-island-island-not-exist%§");
                /*     */
                /*     */
                return;
                /*     */
            }
            /* 146 */
            switch (args[1].toLowerCase()) {
                /*     */
                case "create":
                    /* 148 */
                    if (islandList.stream().filter(i -> (i.getIslandColor() == islandColor)).findFirst()
/* 149 */.orElse(null) == null) {
                        /* 150 */
                        Island islandToCreate = new Island(islandColor, new Location(), new Location(), new Location(), new Location(), new HashMap<>(), Island.IslandStatus.ALIVE, new ArrayList(), new HashMap<>(), null);
                        /*     */
                        /*     */
                        /* 153 */
                        islandList.add(islandToCreate);
                        /* 154 */
                        sender.sendMessage(sender.getLanguage().t("command-island-created-success", new String[]{"%island%",
/* 155 */                     StringFormat.formatString(islandToCreate.getIslandColor().name()), "%islandColor%", "§" + islandToCreate
/* 156 */.getIslandColor().getColor().getChar()}));
                        /*     */
                        /* 158 */
                        if (sender.isPlayer())
                            /* 159 */
                            player.getInventory().addItem(new ItemStack[]{createItem(sender, islandToCreate)});
                        break;
                        /*     */
                    }
                    /* 161 */
                    sender.sendMessage("§%command-island-island-already-exist%§");
                    /*     */
                    break;
                /*     */
                /*     */
                case "edit":
                    /* 165 */
                    if (sender.isPlayer()) {
                        /* 166 */
                        player.getInventory().addItem(new ItemStack[]{createItem(sender, island)});
                        break;
                        /*     */
                    }
                    /* 168 */
                    sender.sendMessage("§%command-only-for-player%§");
                    /*     */
                    break;
                /*     */
                /*     */
                case "setlocation":
                    /* 172 */
                    fieldName = args[2];
                    /*     */
                    /* 174 */
                    field = null;
                    /*     */
                    /* 176 */
                    for (Field f : Island.class.getDeclaredFields()) {
                        /* 177 */
                        if (f.getName().equalsIgnoreCase(fieldName)) {
                            /* 178 */
                            field = f;
                            /*     */
                        }
                        /*     */
                    }
                    /* 181 */
                    if (field == null) {
                        /* 182 */
                        sender.sendMessage(sender.getLanguage().t("command-island-field-not-exist", new String[]{"%field%", fieldName}));
                        break;
                        /*     */
                    }
                    /* 184 */
                    location = null;
                    /*     */
                    /* 186 */
                    if (args.length >= 6) {
                        /* 187 */
                        OptionalDouble optionalX = StringFormat.parseDouble(args[3]);
                        /* 188 */
                        OptionalDouble optionalY = StringFormat.parseDouble(args[4]);
                        /* 189 */
                        OptionalDouble optionalZ = StringFormat.parseDouble(args[5]);
                        /*     */
                        /* 191 */
                        if (optionalX.isPresent() && optionalY.isPresent() && optionalZ.isPresent()) {
                            /*     */
                            /*     */
                            /*     */
                            /* 195 */
                            location = new Location((cmdArgs.isPlayer() ? player.getLocation().getWorld() : Bukkit.getWorlds().stream().findFirst().orElse(null)).getName(), optionalX.getAsDouble(), optionalY.getAsDouble(), optionalZ.getAsDouble());
                            /*     */
                        } else {
                            /* 197 */
                            sender.sendMessage("§%number-format%§");
                            /*     */
                            /*     */
                            return;
                            /*     */
                        }
                        /* 201 */
                    } else if (sender.isPlayer()) {
                        /* 202 */
                        location = Location.fromLocation(player.getLocation());
                        /*     */
                    } else {
                        /* 204 */
                        sender.sendMessage("§%command-only-for-player%§");
                        /*     */
                        /*     */
                        return;
                        /*     */
                    }
                    /*     */
                    /*     */
                    try {
                        /* 210 */
                        field.setAccessible(true);
                        /* 211 */
                        field.set(island, location);
                        /* 212 */
                        sender.sendMessage("§aLocalização atualizada.");
                        /* 213 */
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        /* 214 */
                        sender.sendMessage("§%command-island-not-loaded-location%§");
                        /* 215 */
                        e.printStackTrace();
                        /*     */
                    }
                    /*     */
                    break;
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    private ItemStack createItem(CommandSender sender, final Island islandToCreate) {
        /* 228 */
        return (new ActionItemStack((new ItemBuilder())
/*     */
/* 230 */.name(sender.getLanguage().t("bedwars.creator.item-name", new String[]{"%island%",
/* 231 */               StringFormat.formatString(islandToCreate.getIslandColor().name()), "%islandColor%", "§" + islandToCreate
/* 232 */.getIslandColor().getColor().getChar()
/* 233 */})).type(Material.BARRIER).build(), new ActionItemStack.Interact()
                /*     */ {
            /*     */
            /*     */
            /*     */
            public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action)
            /*     */ {
                /* 239 */
                new IslandCreatorInventory(player, islandToCreate);
                /* 240 */
                return true;
                /*     */
            }
            /* 242 */
        })).getItemStack();
        /*     */
    }

    /*     */
    /*     */
    @Command(name = "generator", permission = "command.generator")
    /*     */ public void generatorCommand(CommandArgs cmdArgs) {
        /* 247 */
        CommandSender sender = cmdArgs.getSender();
        /* 248 */
        Player player = ((BukkitMember) cmdArgs.getSenderAsMember(BukkitMember.class)).getPlayer();
        /* 249 */
        String[] args = cmdArgs.getArgs();
        /*     */
        /* 251 */
        if (args.length == 0) {
            /* 252 */
            handleGeneratorUsage(sender);
            /*     */
            /*     */
            return;
            /*     */
        }
        /* 256 */
        switch (args[0].toLowerCase()) {
            /*     */
            case "list":
                /* 258 */
                sender.sendMessage("  §aLista de Geradores");
                /* 259 */
                for (GeneratorType generatorType1 : GeneratorType.values()) {
                    /* 260 */
                    List<Generator> generators = GameMain.getInstance().getGeneratorManager().getGenerators(generatorType1);
                    /*     */
                    /* 262 */
                    if (generators != null) {
                        /*     */
                        /*     */
                        /* 265 */
                        sender.sendMessage("    §fGeradores de " + generatorType1.getColor() + generatorType1.name());
                        /*     */
                        /* 267 */
                        for (int index = 0; index < generators.size(); index++) {
                            /* 268 */
                            Generator generator = generators.get(index);
                            /*     */
                            /*     */
                            /* 271 */
                            MessageBuilder messageBuilder = (new MessageBuilder("      §fGerador " + (index + 1))).setClickEvent("/" + cmdArgs.getLabel() + " " + generatorType1.name() + " " + (index + 1));
                            /*     */
                            /* 273 */
                            sender.sendMessage(messageBuilder.create());
                            /* 274 */
                            sender.sendMessage(messageBuilder
/*     */
/* 276 */.setMessage("        §fLocation: §7" + generator.getLocation().getX() + ", " + generator
/* 277 */.getLocation().getY() + ", " + generator.getLocation().getZ())
/* 278 */.create());
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
                return;
            /*     */
        }
        /* 284 */
        GeneratorType generatorType = null;
        /*     */
        /*     */
        try {
            /* 287 */
            generatorType = GeneratorType.valueOf(args[0].toUpperCase());
            /* 288 */
        } catch (Exception ex) {
            /* 289 */
            sender.sendMessage("§cO generador não existe.");
            /*     */
            /*     */
            return;
            /*     */
        }
        /* 293 */
        if (args.length == 2)
            /* 294 */ {
            if (args[1].equalsIgnoreCase("create"))
                /* 295 */ {
                if (sender.isPlayer()) {
                    /* 296 */
                    GameMain.getInstance().getGeneratorManager().createGenerator(generatorType,
                            /* 297 */               Location.fromLocation(player.getLocation()), true);
                    /* 298 */
                    sender.sendMessage("§aO jogador de " + generatorType.name() + " foi criado com sucesso.");
                    /*     */
                } else {
                    /* 300 */
                    sender.sendMessage("§%command-only-for-player%§");
                    /*     */
                }
            }
            /* 302 */
            else {
                OptionalInt parseInt = StringFormat.parseInt(args[2]);
                /*     */
                /* 304 */
                if (parseInt.isPresent())
                    /* 305 */ {
                    Generator generator = GameMain.getInstance().getGeneratorManager().getGenerator(generatorType, parseInt
/* 306 */.getAsInt());
                    /*     */
                    /* 308 */
                    if (generator == null) {
                        /* 309 */
                        sender.sendMessage("§cNenhum gerador encontrado.");
                        /*     */
                    } else {
                        /* 311 */
                        sender.sendMessage("  Gerador " + (parseInt.getAsInt() + 1));
                        /* 312 */
                        sender.sendMessage("    Location: §7" + generator.getLocation().getX() + ", " + generator
/* 313 */.getLocation().getY() + ", " + generator.getLocation().getZ());
                        /* 314 */
                        sender.sendMessage("    Level: " + generator.getLevel());
                        /* 315 */
                        sender.sendMessage("    Time: " + (generator.getGenerateTime() / 1000L));
                        /*     */
                    }
                }
                /*     */
                else
                    /* 318 */ {
                    handleGeneratorUsage(sender);
                }
            }
            /*     */
        }
        /* 320 */
        else if (args.length >= 3)
            /* 321 */ {
            if (args[1].equalsIgnoreCase("setlocation"))
                /* 322 */ {
                if (sender.isPlayer()) {
                    /* 323 */
                    OptionalInt parseInt = StringFormat.parseInt(args[2]);
                    /*     */
                    /* 325 */
                    if (parseInt.isPresent())
                        /* 326 */ {
                        if (GameMain.getInstance().getGeneratorManager().setLocation(generatorType, parseInt
/* 327 */.getAsInt() - 1, Location.fromLocation(player.getLocation()), true)) {
                            /* 328 */
                            sender.sendMessage("localizacao atulaizada.");
                            /*     */
                        } else {
                            /* 330 */
                            sender.sendMessage("§cNenhum gerador encontrado.");
                            /*     */
                        }
                    }
                    /* 332 */
                    else {
                        sender.sendMessage("§%number-format%§");
                    }
                    /*     */
                    /*     */
                } else {
                    /* 335 */
                    sender.sendMessage("§%command-only-for-player%§");
                    /*     */
                }
            }
            /* 337 */
            else {
                handleGeneratorUsage(sender);
            }
            /*     */
        }
        /* 339 */
        else {
            handleGeneratorUsage(sender);
        }
        /*     */
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    @Completer(name = "island")
    /*     */ public List<String> islandCompleter(CommandArgs cmdArgs) {
        /* 347 */
        String[] args = cmdArgs.getArgs();
        /* 348 */
        List<String> list = new ArrayList<>();
        /*     */
        /* 350 */
        if (args.length == 1) {
            /* 351 */
            for (IslandColor color : IslandColor.values())
                /* 352 */ {
                if (color.name().toLowerCase().startsWith(args[0].toLowerCase()))
                    /* 353 */ list.add(color.name());
            }
            /* 354 */
        } else if (args.length == 2) {
            /* 355 */
            for (String completer : Arrays.<String>asList(new String[]{"create", "edit", "setlocation"}))
                /* 356 */ {
                if (completer.toLowerCase().startsWith(args[1].toLowerCase()))
                    /* 357 */ list.add(completer);
            }
            /* 358 */
        } else if (args.length == 3 &&
                /* 359 */       args[1].equalsIgnoreCase("setlocation")) {
            /* 360 */
            for (Field field : Island.class.getDeclaredFields()) {
                /* 361 */
                if (field.getName().toLowerCase().startsWith(args[2].toLowerCase()) && field
/* 362 */.getName().toLowerCase().contains("location")) {
                    /* 363 */
                    list.add(field.getName());
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /* 367 */
        return list;
        /*     */
    }

    /*     */
    /*     */
    private void handleGeneratorUsage(CommandSender sender) {
        /* 371 */
        sender.sendMessage("generator list");
        /* 372 */
        sender.sendMessage("generator <type> create");
        /* 373 */
        sender.sendMessage("generator <type> setlocation <index>");
        /*     */
    }
    /*     */
}


