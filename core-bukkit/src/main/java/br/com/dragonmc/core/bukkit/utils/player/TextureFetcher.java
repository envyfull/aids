/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.utility.MinecraftReflection
 *  com.comphenix.protocol.wrappers.WrappedGameProfile
 *  com.comphenix.protocol.wrappers.WrappedSignedProperty
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 */
package br.com.dragonmc.core.bukkit.utils.player;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import br.com.dragonmc.core.common.CommonPlugin;

public class TextureFetcher {
    public static final LoadingCache<WrappedGameProfile, WrappedSignedProperty> TEXTURE = CacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.MINUTES).build((CacheLoader)new CacheLoader<WrappedGameProfile, WrappedSignedProperty>(){

        public WrappedSignedProperty load(WrappedGameProfile profile) throws Exception {
            try {
                Object minecraftServer = MinecraftReflection.getMinecraftServerClass().getMethod("getServer", new Class[0]).invoke(null, new Object[0]);
                ((MinecraftSessionService)minecraftServer.getClass().getMethod("aD", new Class[0]).invoke(minecraftServer, new Object[0])).fillProfileProperties((GameProfile)profile.getHandle(), true);
            }
            catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                e.printStackTrace();
            }
            if (profile.getProperties().containsKey((Object)"textures")) {
                return profile.getProperties().get((Object)"textures").stream().findFirst().orElse(null);
            }
            String[] properties = CommonPlugin.getInstance().getSkinData().loadSkinById(profile.getUUID());
            return properties == null ? null : new WrappedSignedProperty(profile.getName(), properties[0], properties[1]);
        }
    });

    public static WrappedSignedProperty loadTexture(WrappedGameProfile wrappedGameProfile) {
        return (WrappedSignedProperty)TEXTURE.getUnchecked((Object)wrappedGameProfile);
    }
}

