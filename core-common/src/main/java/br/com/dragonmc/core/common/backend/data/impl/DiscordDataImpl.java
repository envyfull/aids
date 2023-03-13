/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  redis.clients.jedis.Jedis
 */
package br.com.dragonmc.core.common.backend.data.impl;

import java.util.Set;

import br.com.dragonmc.core.common.backend.data.DiscordData;
import br.com.dragonmc.core.common.backend.redis.RedisConnection;
import br.com.dragonmc.core.common.utils.string.CodeCreator;
import redis.clients.jedis.Jedis;

public class DiscordDataImpl
implements DiscordData {
    private RedisConnection redisConnection;

    @Override
    public String getNameByCode(String code, boolean delete) {
        try (Jedis jedis = this.redisConnection.getPool().getResource();){
            Set<String> list = jedis.keys("discord-sync:*");
            for (String possible : list) {
                if (!jedis.get(possible).equals(code)) continue;
                String name = possible.replace("discord-sync:", "");
                if (delete) {
                    jedis.del(possible);
                }
                String string = name;
                return string;
            }
        }
        return null;
    }

    @Override
    public String getCodeOrCreate(String playerName, String code) {
        try (Jedis jedis = this.redisConnection.getPool().getResource();){
            boolean exists;
            boolean bl = exists = jedis.ttl("discord-sync:" + playerName.toLowerCase()) >= 0L;
            if (exists) {
                String string = jedis.get("discord-sync:" + playerName.toLowerCase());
                return string;
            }
            code = CodeCreator.DEFAULT_CREATOR_LETTERS_ONLY.random(6);
            jedis.setex("discord-sync:" + playerName.toLowerCase(), 120, code);
            String string = code;
            return string;
        }
    }

    public DiscordDataImpl(RedisConnection redisConnection) {
        this.redisConnection = redisConnection;
    }
}

