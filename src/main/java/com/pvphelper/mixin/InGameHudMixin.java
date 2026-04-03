package com.pvphelper.mixin;

import com.pvphelper.config.ModConfig;
import com.pvphelper.hud.HealthBarRenderer;
import com.pvphelper.util.TargetTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        // Mojang mappings 1.21.11: options.hideGui (bukan hudHidden)
        if (client.options.hideGui) return;

        TargetTracker.tick(client);
        HealthBarRenderer.render(context, client, tickCounter.getTickDelta(true));
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        ModConfig cfg = ModConfig.getInstance();
        if (!cfg.enableSmartCrosshair) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Kalau tidak ada target dan setting crosshairShowDefault=true,
        // biarkan vanilla render crosshair normal
        boolean hasTarget = TargetTracker.getCurrentTarget() != null;
        if (!hasTarget && cfg.crosshairShowDefault) return;

        ci.cancel();

        int cx = context.getScaledWindowWidth() / 2;
        int cy = context.getScaledWindowHeight() / 2;

        int color = TargetTracker.getCrosshairColor();
        int size = 5;
        int gap = 2;

        // Garis horizontal
        context.fill(cx - size - gap, cy - 1, cx - gap,        cy + 1, color);
        context.fill(cx + gap,        cy - 1, cx + size + gap, cy + 1, color);
        // Garis vertikal
        context.fill(cx - 1, cy - size - gap, cx + 1, cy - gap,        color);
        context.fill(cx - 1, cy + gap,        cx + 1, cy + size + gap, color);

        // Titik hijau di tengah saat bisa hit player
        if (TargetTracker.isOnHittablePlayer()) {
            context.fill(cx - 1, cy - 1, cx + 1, cy + 1, 0xFF00FF00);
        }
    }
}
