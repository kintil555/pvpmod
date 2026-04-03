package com.pvphelper;

import com.pvphelper.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvpHelperClient implements ClientModInitializer {

    public static final String MOD_ID = "pvphelper";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("[PvP Helper] Mod dimuat! Versi 1.0.1 untuk Minecraft 1.21.11");
        ModConfig.load();
        LOGGER.info("[PvP Helper] Config berhasil dimuat.");
    }
}
