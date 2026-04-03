package com.pvphelper.mixin;

import com.pvphelper.hud.PvpSettingsScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class PauseScreenMixin extends Screen {

    protected PauseScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void onInitWidgets(CallbackInfo ci) {
        // Tambah tombol PvP Settings di pojok kanan bawah pause menu
        int btnW = 120;
        int btnH = 20;
        int x = this.width - btnW - 8;
        int y = this.height - btnH - 8;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("⚔ PvP Settings"),
                btn -> {
                    if (this.client != null) {
                        this.client.setScreen(new PvpSettingsScreen(this));
                    }
                })
                .dimensions(x, y, btnW, btnH)
                .build());
    }
}
