/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  net.md_5.bungee.api.event.ClientConnectEvent
 *  net.md_5.bungee.api.event.PreLoginEvent
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.api.plugin.Plugin
 *  net.md_5.bungee.event.EventHandler
 */
package br.com.dragonmc.core.bungee.listener;

import br.com.dragonmc.core.bungee.BungeeMain;
import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import net.md_5.bungee.api.event.ClientConnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class LoginListener
implements Listener {
    private static final int MAX_CONNECTIONS_PER_IP = 10;
    private static final int MAX_CONNECTIONS = 25;
    private Cache<String, Integer> loginCache = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.SECONDS).build((CacheLoader)new CacheLoader<String, Integer>(){

        public Integer load(String name) throws Exception {
            return 1;
        }
    });
    private LoadingCache<String, Integer> throttleCache = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.SECONDS).build((CacheLoader)new CacheLoader<String, Integer>(){

        public Integer load(String name) throws Exception {
            return 0;
        }
    });
    private Map<String, Long> blockMap = new HashMap<String, Long>();

    @EventHandler
    public void onClient(ClientConnectEvent event) {
        SocketAddress socket = event.getSocketAddress();
        if (!(socket instanceof InetSocketAddress)) {
            event.setCancelled(true);
            return;
        }
        InetSocketAddress inetSocketAddress = (InetSocketAddress)socket;
        String ipAddress = inetSocketAddress.getHostString();
        if (this.isIpBlocked(ipAddress)) {
            event.setCancelled(true);
            return;
        }
        int throttle = (Integer)this.throttleCache.getUnchecked(ipAddress);
        if (throttle >= 10) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        SocketAddress socket = event.getConnection().getSocketAddress();
        if (!(socket instanceof InetSocketAddress)) {
            event.setCancelled(true);
            event.setCancelReason("\u00a7cWe cannot load your ip address.");
            return;
        }
        String playerName = event.getConnection().getName();
        String ipAddress = ((InetSocketAddress)socket).getHostString();
        int throttle = (Integer)this.throttleCache.getUnchecked(ipAddress);
        if (throttle >= 3) {
            event.setCancelled(true);
            event.setCancelReason("\u00a7cAguarde para tentar uma nova conex\u00e3o.");
            return;
        }
        this.throttleCache.put(ipAddress, (throttle + 1));
        int connections = this.throttleCache.asMap().size();
        if (connections >= 25) {
            CommonPlugin.getInstance().debug("The connection of player " + playerName + " (" + ipAddress + ") have been forced to pass as premium because the server reach the maximum connetions per attemps (" + connections + " connections current).");
            event.getConnection().setOnlineMode(true);
            return;
        }
        if (playerName.toLowerCase().contains("mcstorm")) {
            event.setCancelled(true);
            event.setCancelReason("\u00a7cSua conta n\u00e3o foi carregada. [BungeeCord: 02]");
            this.block(ipAddress, "the name of player contains \"mcstorm\"");
            return;
        }
        if (!CommonConst.NAME_PATTERN.matcher(playerName).matches()) {
            event.setCancelReason("\u00a7cSeu nome no jogo \u00e9 inv\u00e1lido.\n\u00a7cPara entrar no servidor utilize um nome com at\u00e9 16 caracteres, n\u00fameros ou \"_\".");
            event.setCancelled(true);
            return;
        }
        if (this.loginCache.asMap().containsKey(ipAddress)) {
            if ((Integer)this.loginCache.asMap().get(ipAddress) >= 10) {
                event.setCancelReason("\u00a7cSua conta foi bloqueada por m\u00faltiplas conex\u00f5es simult\u00e2neas.");
                event.setCancelled(true);
                this.loginCache.invalidate((Object)ipAddress);
                this.throttleCache.invalidate((Object)ipAddress);
                this.block(ipAddress, "multiple connections while verifing in mojang");
                return;
            }
            this.loginCache.put(ipAddress, ((Integer)this.loginCache.asMap().get(ipAddress) + 1));
        }
        if (CommonPlugin.getInstance().getPluginInfo().isPiratePlayersEnabled()) {
            event.registerIntent((Plugin) BungeeMain.getInstance());
            CommonPlugin.getInstance().getPluginPlatform().runAsync(() -> {
                boolean onlineMode = true;
                if (CommonPlugin.getInstance().getMemberData().isRedisCached(playerName)) {
                    onlineMode = CommonPlugin.getInstance().getMemberData().isConnectionPremium(playerName);
                    CommonPlugin.getInstance().debug("The player " + event.getConnection().getName() + " is " + (onlineMode ? "premium" : "cracked") + " (cached)");
                } else {
                    boolean save = true;
                    this.loginCache.put(ipAddress, 1);
                    UUID uniqueId = CommonPlugin.getInstance().getUuidFetcher().getUUID(playerName);
                    if (uniqueId == null) {
                        onlineMode = false;
                    } else {
                        CommonPlugin.getInstance().debug("The player " + playerName + " have the UUID " + uniqueId);
                    }
                    if (save) {
                        CommonPlugin.getInstance().getMemberData().setConnectionStatus(playerName, uniqueId == null ? UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(Charsets.UTF_8)) : uniqueId, onlineMode);
                    }
                    CommonPlugin.getInstance().debug("The player " + event.getConnection().getName() + " is " + (onlineMode ? "premium" : "cracked") + " (not cached)");
                }
                CommonPlugin.getInstance().debug("The number of " + connections + " connections currently.");
                event.getConnection().setOnlineMode(onlineMode);
                event.completeIntent((Plugin)BungeeMain.getInstance());
                this.throttleCache.invalidate((Object)ipAddress);
                this.loginCache.invalidate((Object)ipAddress);
            });
        } else {
            event.getConnection().setOnlineMode(true);
        }
    }

    private boolean isIpBlocked(String hostString) {
        if (this.blockMap.containsKey(hostString)) {
            if (this.blockMap.get(hostString) > System.currentTimeMillis()) {
                return true;
            }
            this.blockMap.remove(hostString);
        }
        return false;
    }

    private void block(String ipAddress, String reason) {
        this.blockMap.put(ipAddress, System.currentTimeMillis() + 1800000L);
        CommonPlugin.getInstance().debug("The ip " + ipAddress + " has been blocked because " + reason);
    }
}

