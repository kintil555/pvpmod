package com.pvphelper.hud;

import com.pvphelper.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class PvpSettingsScreen extends Screen {

    private final Screen parent;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int SPACING = 24;

    public PvpSettingsScreen(Screen parent) {
        super(Text.literal("PvP Helper - Pengaturan"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        ModConfig cfg = ModConfig.getInstance();
        int centerX = this.width / 2;
        int startY = 50;

        // === Health Bar Toggle ===
        addDrawableChild(ButtonWidget.builder(
                        Text.literal("Health Bar: " + (cfg.enableHealthBar ? "§aNYALA" : "§cMATI")),
                        btn -> {
                            cfg.enableHealthBar = !cfg.enableHealthBar;
                            btn.setMessage(Text.literal("Health Bar: " + (cfg.enableHealthBar ? "§aNYALA" : "§cMATI")));
                            ModConfig.save();
                        })
                .dimensions(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        // === Show Health Numbers ===
        addDrawableChild(ButtonWidget.builder(
                        Text.literal("Tampilkan Angka HP: " + (cfg.showHealthNumbers ? "§aNYALA" : "§cMATI")),
                        btn -> {
                            cfg.showHealthNumbers = !cfg.showHealthNumbers;
                            btn.setMessage(Text.literal("Tampilkan Angka HP: " + (cfg.showHealthNumbers ? "§aNYALA" : "§cMATI")));
                            ModConfig.save();
                        })
                .dimensions(centerX - BUTTON_WIDTH / 2, startY + SPACING, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        // === Show Max Health ===
        addDrawableChild(ButtonWidget.builder(
                        Text.literal("Tampilkan Max HP: " + (cfg.showMaxHealth ? "§aNYALA" : "§cMATI")),
                        btn -> {
                            cfg.showMaxHealth = !cfg.showMaxHealth;
                            btn.setMessage(Text.literal("Tampilkan Max HP: " + (cfg.showMaxHealth ? "§aNYALA" : "§cMATI")));
                            ModConfig.save();
                        })
                .dimensions(centerX - BUTTON_WIDTH / 2, startY + SPACING * 2, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        // === Show Absorption ===
        addDrawableChild(ButtonWidget.builder(
                        Text.literal("Tampilkan Absorpsi: " + (cfg.showAbsorption ? "§aNYALA" : "§cMATI")),
                        btn -> {
                            cfg.showAbsorption = !cfg.showAbsorption;
                            btn.setMessage(Text.literal("Tampilkan Absorpsi: " + (cfg.showAbsorption ? "§aNYALA" : "§cMATI")));
                            ModConfig.save();
                        })
                .dimensions(centerX - BUTTON_WIDTH / 2, startY + SPACING * 3, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        // === Smart Crosshair Toggle ===
        addDrawableChild(ButtonWidget.builder(
                        Text.literal("Smart Crosshair: " + (cfg.enableSmartCrosshair ? "§aNYALA" : "§cMATI")),
                        btn -> {
                            cfg.enableSmartCrosshair = !cfg.enableSmartCrosshair;
                            btn.setMessage(Text.literal("Smart Crosshair: " + (cfg.enableSmartCrosshair ? "§aNYALA" : "§cMATI")));
                            ModConfig.save();
                        })
                .dimensions(centerX - BUTTON_WIDTH / 2, startY + SPACING * 4, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        // === Crosshair Default When No Target ===
        // BUG FIX #6: enableCrosshairDistance diganti crosshairShowDefault yang benar-benar dipakai
        addDrawableChild(ButtonWidget.builder(
                        Text.literal("Crosshair Default (no target): " + (cfg.crosshairShowDefault ? "§aNYALA" : "§cMATI")),
                        btn -> {
                            cfg.crosshairShowDefault = !cfg.crosshairShowDefault;
                            btn.setMessage(Text.literal("Crosshair Default (no target): " + (cfg.crosshairShowDefault ? "§aNYALA" : "§cMATI")));
                            ModConfig.save();
                        })
                .dimensions(centerX - BUTTON_WIDTH / 2, startY + SPACING * 5, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        // === Opacity Slider ===
        addDrawableChild(new SliderWidget(
                centerX - BUTTON_WIDTH / 2, startY + SPACING * 6,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                Text.literal("Opacity HUD: " + (int)(cfg.healthBarAlpha * 100) + "%"),
                cfg.healthBarAlpha) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Opacity HUD: " + (int)(this.value * 100) + "%"));
            }
            @Override
            protected void applyValue() {
                ModConfig.getInstance().healthBarAlpha = (float) this.value;
                ModConfig.save();
            }
        });

        // === Reset Default ===
        addDrawableChild(ButtonWidget.builder(
                        Text.literal("Reset ke Default"),
                        btn -> {
                            resetToDefaults();
                            if (client != null) client.setScreen(new PvpSettingsScreen(parent));
                        })
                .dimensions(centerX - BUTTON_WIDTH / 2, startY + SPACING * 7 + 4, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        // === Kembali ===
        addDrawableChild(ButtonWidget.builder(
                        Text.literal("Kembali"),
                        btn -> close())
                .dimensions(centerX - BUTTON_WIDTH / 2, startY + SPACING * 8 + 4, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());
    }

    private void resetToDefaults() {
        ModConfig cfg = ModConfig.getInstance();
        cfg.enableHealthBar = true;
        cfg.enableSmartCrosshair = true;
        cfg.showHealthNumbers = true;
        cfg.showMaxHealth = true;
        cfg.showAbsorption = true;
        cfg.healthBarAlpha = 0.85f;
        cfg.crosshairShowDefault = true;
        ModConfig.save();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        context.drawTextWithShadow(this.textRenderer,
                Text.literal("§7--- Health Bar ---"),
                this.width / 2 - BUTTON_WIDTH / 2, 38, 0xAAAAAA);

        context.drawTextWithShadow(this.textRenderer,
                Text.literal("§7--- Crosshair ---"),
                this.width / 2 - BUTTON_WIDTH / 2, 38 + SPACING * 4, 0xAAAAAA);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }

    // BUG FIX #4: shouldPause() harus true supaya game tetap pause saat di settings
    @Override
    public boolean shouldPause() {
        return true;
    }
}
