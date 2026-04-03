package com.pvphelper.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class TargetTracker {

    // Entity yang sedang ditarget crosshair (bisa null)
    private static LivingEntity currentTarget = null;
    // Apakah crosshair sedang pada area hittable player lain
    private static boolean onHittablePlayer = false;

    public static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            currentTarget = null;
            onHittablePlayer = false;
            return;
        }

        // Cek entity yang di-look at (vanilla crosshair target)
        HitResult hit = client.crosshairTarget;
        if (hit instanceof EntityHitResult entityHit) {
            Entity entity = entityHit.getEntity();
            if (entity instanceof PlayerEntity player && player != client.player) {
                currentTarget = player;
                onHittablePlayer = isInHitRange(client, player);
                return;
            } else if (entity instanceof LivingEntity living) {
                currentTarget = living;
                onHittablePlayer = false;
                return;
            }
        }

        // Fallback: scan entitas dalam radius kecil di arah pandang
        LivingEntity found = findEntityInLookDirection(client);
        currentTarget = found;
        onHittablePlayer = (found instanceof PlayerEntity) && found != client.player
                && isInHitRange(client, found);
    }

    private static boolean isInHitRange(MinecraftClient client, LivingEntity entity) {
        if (client.player == null) return false;
        double distance = client.player.distanceTo(entity);
        // Jangkauan hit standar survival 3 blok, dengan sedikit toleransi
        return distance <= 3.5;
    }

    private static LivingEntity findEntityInLookDirection(MinecraftClient client) {
        if (client.player == null || client.world == null) return null;

        Vec3d eyePos = client.player.getEyePos();
        Vec3d lookVec = client.player.getRotationVec(1.0f);
        double range = 5.0;
        Vec3d endPos = eyePos.add(lookVec.multiply(range));

        Box searchBox = new Box(eyePos, endPos).expand(1.0);
        List<LivingEntity> entities = client.world.getEntitiesByClass(
                LivingEntity.class, searchBox,
                e -> e != client.player && e.isAlive()
        );

        LivingEntity closest = null;
        double closestDist = Double.MAX_VALUE;

        for (LivingEntity entity : entities) {
            // Cek apakah ray dari mata player melewati hitbox entity
            Box entityBox = entity.getBoundingBox().expand(0.1);
            var intersection = entityBox.raycast(eyePos, endPos);
            if (intersection.isPresent()) {
                double dist = client.player.distanceTo(entity);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = entity;
                }
            }
        }
        return closest;
    }

    public static LivingEntity getCurrentTarget() {
        return currentTarget;
    }

    public static boolean isOnHittablePlayer() {
        return onHittablePlayer;
    }

    /**
     * Dapatkan warna crosshair: hijau kalau bisa hit player, putih kalau tidak
     */
    public static int getCrosshairColor() {
        if (onHittablePlayer) {
            return 0xFF00FF00; // Hijau terang
        }
        return 0xFFFFFFFF; // Putih default
    }
}
