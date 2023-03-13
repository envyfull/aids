/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.utils.ClassGetter;

public interface CommandFramework {
    public Class<?> getJarClass();

    public void registerCommands(CommandClass var1);

    default public CommandFramework loadCommands(String packageName) {
        for (Class<?> commandClass : ClassGetter.getClassesForPackage(this.getJarClass(), packageName)) {
            if (CommandClass.class == commandClass || !CommandClass.class.isAssignableFrom(commandClass)) continue;
            try {
                this.registerCommands((CommandClass)commandClass.newInstance());
            }
            catch (Exception ex) {
                CommonPlugin.getInstance().getLogger().warning("Error when loading command from " + commandClass.getSimpleName() + "!");
                ex.printStackTrace();
            }
        }
        return this;
    }

    default public CommandFramework loadCommands(Class<?> jarClass, String packageName) {
        for (Class<?> commandClass : ClassGetter.getClassesForPackage(jarClass, packageName)) {
            if (!CommandClass.class.isAssignableFrom(commandClass)) continue;
            try {
                this.registerCommands((CommandClass)commandClass.newInstance());
            }
            catch (Exception e) {
                CommonPlugin.getInstance().getLogger().warning("Error when loading command from " + commandClass.getSimpleName() + "!");
                e.printStackTrace();
            }
        }
        return this;
    }

    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Completer {
        public String name();

        public String[] aliases() default {};
    }

    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Command {
        public String name();

        public String permission() default "";

        public String[] aliases() default {};

        public String description() default "";

        public String usage() default "";

        public boolean runAsync() default false;

        public boolean console() default true;
    }
}

