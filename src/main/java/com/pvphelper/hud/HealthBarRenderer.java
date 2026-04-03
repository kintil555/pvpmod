package com.pvphelper.hud;

import com.pvphelper.config.ModConfig;
import com.pvphelper.util.TargetTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

public class HealthBarRenderer {

    private static final int BAR_WIDTH = 100;
    private static final int BAR_HEIGHT = 8;
    private static final int PADDING = 4;

    public static void render(DrawContext context, MinecraftClient client, float tickDelta) {
        ModConfig cfg = ModConfig.getInstance();
        if (!cfg.enableHealthBar) return;

        LivingEntity target = TargetTracker.getCurrentTarget();
        if (target == null || !target.isAlive()) return;

        TextRenderer textRenderer = client.textRenderer;

        float health = target.getHealth();
        // Mojang mappings 1.21.11: EntityAttributes.MAX_HEALTH (nama Mojang)
        float maxHealth = (float) target.getAttributeValue(EntityAttributes.MAX_HEALTH);
        float absorption = target.getAbsorptionAmount();

        float healthPercent = Math.min(health / maxHealth, 1.0f);
        float absorptionPercent = cfg.showAbsorption && absorption > 0
                ? Math.min(absorption / maxHealth, 1.0f - healthPercent)
                : 0;

        int x = cfg.healthBarX;
        int y = cfg.healthBarY;
        int alpha = ((int) (cfg.healthBarAlpha * 255)) & 0xFF;

        // Background semi-transparan
        int bgAlpha = (alpha / 2) & 0xFF;
        context.fill(
                x - PADDING, y - PADDING,
                x + BAR_WIDTH + PADDING, y + BAR_HEIGHT + PADDING + textRenderer.fontHeight + 2,
                bgAlpha << 24
        );

        // Background bar abu-abu
        context.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, (alpha << 24) | 0x444444);

        // Health bar
        int healthColor = getHealthColor(healthPercent, alpha);
        int healthWidth = (int) (BAR_WIDTH * healthPercent);
        if (healthWidth > 0) {
            context.fill(x, y, x + healthWidth, y + BAR_HEIGHT, healthColor);
        }

        // Absorption bar kuning
        if (cfg.showAbsorption && absorptionPercent > 0) {
            int absWidth = (int) (BAR_WIDTH * absorptionPercent);
            if (absWidth > 0) {
                context.fill(x + healthWidth, y, x + healthWidth + absWidth, y + BAR_HEIGHT,
                        (alpha << 24) | 0xFFAA00);
            }
        }

        // Border
        drawBorder(context, x, y, BAR_WIDTH, BAR_HEIGHT, alpha);

        // Nama target
        String name = target.getName().getString();
        int nameColor = (target instanceof PlayerEntity) ? 0xFFFF55 : 0xFFFFFF;
        context.drawText(textRenderer, name, x, y + BAR_HEIGHT + 2, (alpha << 24) | nameColor, true);

        // Angka health
        if (cfg.showHealthNumbers) {
            String healthText;
            if (cfg.showMaxHealth) {
                healthText = String.format("%.1f / %.0f", health, maxHealth);
            } else {
                healthText = String.format("%.1f", health);
            }
            if (cfg.showAbsorption && absorption > 0) {
                healthText += String.format(" (+%.0f)", absorption);
            }
            int textWidth = textRenderer.getWidth(healthText);
            context.drawText(textRenderer, healthText,
                    x + BAR_WIDTH - textWidth, y + BAR_HEIGHT + 2,
                    (alpha << 24) | 0xFFFFFF, true);
        }
    }

    private static int getHealthColor(float percent, int alpha) {
        int r, g, b;
        if (percent > 0.6f) {
            r = 0x00; g = 0xCC; b = 0x00; // Hijau
        } else if (percent > 0.3f) {
            r = 0xFF; g = 0xAA; b = 0x00; // Kuning
        } else {
            r = 0xFF; g = 0x00; b = 0x00; // Merah
        }
        return (alpha << 24) | (r << 16) | (g << 8) | b;
    }

    private static void drawBorder(DrawContext context, int x, int y, int w, int h, int alpha) {
        int c = (alpha << 24) | 0x888888;
        context.fill(x,     y - 1, x + w,     y,         c);
        context.fill(x,     y + h, x + w,     y + h + 1, c);
        context.fill(x - 1, y - 1, x,         y + h + 1, c);
        context.fill(x + w, y - 1, x + w + 1, y + h + 1, c);
    }
}
