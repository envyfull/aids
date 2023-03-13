/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.pvp.arena.manager;

import java.util.ArrayList;
import java.util.List;

import br.com.dragonmc.core.common.utils.ClassGetter;
import br.com.dragonmc.pvp.arena.GameMain;
import br.com.dragonmc.pvp.arena.kit.Kit;

public class KitManager {
    private List<Kit> kitList = new ArrayList<Kit>();
    private Kit defaultKit;

    public KitManager() {
        for (Class<?> kitClass : ClassGetter.getClassesForPackage(((Object)((Object)GameMain.getInstance())).getClass(), "br.com.dragonmc.pvp.arena.kit.register")) {
            if (!Kit.class.isAssignableFrom(kitClass)) continue;
            try {
                Kit kit2 = (Kit)kitClass.newInstance();
                this.addKit(kit2);
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.print("Erro ao carregar o kit " + kitClass.getSimpleName());
            }
        }
        this.kitList.sort((o1, o2) -> o1.getKitName().compareTo(o2.getKitName()));
        Kit item = this.kitList.stream().filter(kit -> kit.getKitName().equalsIgnoreCase("pvp")).findFirst().orElse(null);
        int itemPos = this.kitList.indexOf(item);
        this.defaultKit = item;
        this.kitList.remove(itemPos);
        this.kitList.add(0, item);
    }

    public void addKit(Kit kit) {
        this.kitList.add(kit);
        kit.register();
    }

    public Kit getKit(String kitName) {
        return this.kitList.stream().filter(kit -> kit.getKitName().equalsIgnoreCase(kitName)).findFirst().orElse(null);
    }

    public List<Kit> getKitList() {
        return this.kitList;
    }

    public Kit getDefaultKit() {
        return this.defaultKit;
    }
}

