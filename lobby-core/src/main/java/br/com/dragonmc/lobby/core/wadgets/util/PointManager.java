/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_8_R3.EnumParticle
 *  net.minecraft.server.v1_8_R3.Packet
 *  net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.lobby.core.wadgets.util;

import br.com.dragonmc.lobby.core.CoreMain;
import br.com.dragonmc.lobby.core.gamer.Gamer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PointManager {
    private static PointManager instance = new PointManager();
    private Point3D[] outline = new Point3D[]{new Point3D(0.0f, 0.0f, -0.5f), new Point3D(0.1f, 0.01f, -0.5f), new Point3D(0.3f, 0.03f, -0.5f), new Point3D(0.4f, 0.04f, -0.5f), new Point3D(0.6f, 0.1f, -0.5f), new Point3D(0.61f, 0.2f, -0.5f), new Point3D(0.62f, 0.4f, -0.5f), new Point3D(0.63f, 0.6f, -0.5f), new Point3D(0.635f, 0.7f, -0.5f), new Point3D(0.7f, 0.7f, -0.5f), new Point3D(0.9f, 0.75f, -0.5f), new Point3D(1.2f, 0.8f, -0.5f), new Point3D(1.4f, 0.9f, -0.5f), new Point3D(1.6f, 1.0f, -0.5f), new Point3D(1.8f, 1.1f, -0.5f), new Point3D(1.85f, 0.9f, -0.5f), new Point3D(1.9f, 0.7f, -0.5f), new Point3D(1.85f, 0.5f, -0.5f), new Point3D(1.8f, 0.3f, -0.5f), new Point3D(1.75f, 0.1f, -0.5f), new Point3D(1.7f, -0.1f, -0.5f), new Point3D(1.65f, -0.3f, -0.5f), new Point3D(1.55f, -0.5f, -0.5f), new Point3D(1.45f, -0.7f, -0.5f), new Point3D(1.3f, -0.75f, -0.5f), new Point3D(1.15f, -0.8f, -0.5f), new Point3D(1.0f, -0.85f, -0.5f), new Point3D(0.8f, -0.87f, -0.5f), new Point3D(0.6f, -0.7f, -0.5f), new Point3D(0.5f, -0.5f, -0.5f), new Point3D(0.4f, -0.3f, -0.5f), new Point3D(0.3f, -0.3f, -0.5f), new Point3D(0.15f, -0.3f, -0.5f), new Point3D(0.0f, -0.3f, -0.5f), new Point3D(0.9f, 0.55f, -0.5f), new Point3D(1.2f, 0.6f, -0.5f), new Point3D(1.4f, 0.7f, -0.5f), new Point3D(1.6f, 0.9f, -0.5f), new Point3D(0.9f, 0.35f, -0.5f), new Point3D(1.2f, 0.4f, -0.5f), new Point3D(1.4f, 0.5f, -0.5f), new Point3D(1.6f, 0.7f, -0.5f), new Point3D(0.9f, 0.15f, -0.5f), new Point3D(1.2f, 0.2f, -0.5f), new Point3D(1.4f, 0.3f, -0.5f), new Point3D(1.6f, 0.5f, -0.5f), new Point3D(0.9f, -0.05f, -0.5f), new Point3D(1.2f, 0.0f, -0.5f), new Point3D(1.4f, 0.1f, -0.5f), new Point3D(1.6f, 0.3f, -0.5f), new Point3D(0.7f, -0.25f, -0.5f), new Point3D(1.0f, -0.2f, -0.5f), new Point3D(1.2f, -0.1f, -0.5f), new Point3D(1.4f, 0.1f, -0.5f), new Point3D(0.7f, -0.45f, -0.5f), new Point3D(1.0f, -0.4f, -0.5f), new Point3D(1.2f, -0.3f, -0.5f), new Point3D(1.4f, -0.1f, -0.5f), new Point3D(1.3f, -0.55f, -0.5f), new Point3D(1.15f, -0.6f, -0.5f), new Point3D(1.0f, -0.65f, -0.5f)};
    private Point3D[] fill = new Point3D[]{new Point3D(1.2f, 0.6f, -0.5f), new Point3D(1.4f, 0.7f, -0.5f), new Point3D(1.1f, 0.2f, -0.5f), new Point3D(1.3f, 0.3f, -0.5f), new Point3D(1.0f, -0.2f, -0.5f), new Point3D(1.2f, -0.1f, -0.5f)};
    private int x = 0;
    private int B = 0;

    public PointManager() {
        instance = this;
    }

    public void sendPacket(Player player, EnumParticle particle) {
        if (this.x > 500) {
            this.x = 0;
        }
        if (this.x % 5 == 0) {
            PacketPlayOutWorldParticles packet2;
            PacketPlayOutWorldParticles packet;
            ++this.B;
            if (this.B % 25 == 0) {
                return;
            }
            Location playerLocation = player.getEyeLocation();
            float x = (float)playerLocation.getX();
            float y = (float)playerLocation.getY() - 0.2f;
            float z = (float)playerLocation.getZ();
            float rot = -playerLocation.getYaw() * ((float)Math.PI / 180);
            Point3D rotated = null;
            for (Point3D point : this.outline) {
                rotated = point.rotate(rot);
                packet = new PacketPlayOutWorldParticles(particle, true, rotated.x + x, rotated.y + y, rotated.z + z, 0.0f, 0.0f, 0.0f, 0.0f, 1, new int[0]);
                point.z *= -1.0f;
                rotated = point.rotate(rot + 3.1415f);
                point.z *= -1.0f;
                packet2 = new PacketPlayOutWorldParticles(particle, true, rotated.x + x, rotated.y + y, rotated.z + z, 0.0f, 0.0f, 0.0f, 0.0f, 1, new int[0]);
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (!online.canSee(player)) continue;
                    ((CraftPlayer)online).getHandle().playerConnection.sendPacket((Packet)packet);
                    ((CraftPlayer)online).getHandle().playerConnection.sendPacket((Packet)packet2);
                }
            }
            for (Point3D point : this.fill) {
                rotated = point.rotate(rot);
                packet = new PacketPlayOutWorldParticles(particle, true, rotated.x + x, rotated.y + y, rotated.z + z, 0.0f, 0.0f, 0.0f, 0.0f, 1, new int[0]);
                point.z *= -1.0f;
                rotated = point.rotate(rot + 3.1415f);
                point.z *= -1.0f;
                packet2 = new PacketPlayOutWorldParticles(particle, true, rotated.x + x, rotated.y + y, rotated.z + z, 0.0f, 0.0f, 0.0f, 0.0f, 1, new int[0]);
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (!online.canSee(player)) continue;
                    ((CraftPlayer)online).getHandle().playerConnection.sendPacket((Packet)packet);
                    ((CraftPlayer)online).getHandle().playerConnection.sendPacket((Packet)packet2);
                }
            }
        }
        ++this.x;
    }

    public void sendPacket(Player player) {
        Gamer gamer = CoreMain.getInstance().getGamerManager().getGamer(player.getUniqueId());
        gamer.setAlpha(gamer.getAlpha() + 0.19634954084936207);
        double alpha = gamer.getAlpha();
        Location loc = player.getLocation();
        Location firstLocation = loc.clone().add(Math.cos(alpha), Math.sin(alpha) + 1.0, Math.sin(alpha));
        Location secondLocation = loc.clone().add(Math.cos(alpha + Math.PI), Math.sin(alpha) + 1.0, Math.sin(alpha + Math.PI));
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(gamer.getParticle().getParticle(), true, (float)firstLocation.getX(), (float)firstLocation.getY(), (float)firstLocation.getZ(), 0.0f, 0.0f, 0.0f, 0.0f, 1, new int[0]);
        PacketPlayOutWorldParticles packet2 = new PacketPlayOutWorldParticles(gamer.getParticle().getParticle(), true, (float)secondLocation.getX(), (float)secondLocation.getY(), (float)secondLocation.getZ(), 0.0f, 0.0f, 0.0f, 0.0f, 1, new int[0]);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.canSee(player)) continue;
            ((CraftPlayer)online).getHandle().playerConnection.sendPacket((Packet)packet);
            ((CraftPlayer)online).getHandle().playerConnection.sendPacket((Packet)packet2);
        }
    }

    public static PointManager getInstance() {
        return instance;
    }
}

