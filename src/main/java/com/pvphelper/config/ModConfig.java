package com.pvphelper.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("pvphelper.json");

    private static ModConfig instance = new ModConfig();

    // ===== Toggle Features =====
    public boolean enableHealthBar = true;
    public boolean enableSmartCrosshair = true;

    // ===== Health Bar Settings =====
    public int healthBarX = 10;
    public int healthBarY = 10;
    public boolean showHealthNumbers = true;
    public boolean showMaxHealth = true;
    public boolean showAbsorption = true;
    public float healthBarAlpha = 0.85f;

    // ===== Crosshair Settings =====
    // BUG FIX #6: hapus enableCrosshairDistance yang tidak dipakai di mana pun
    public boolean crosshairShowDefault = true;

    public static ModConfig getInstance() {
        return instance;
    }

    public static void load() {
        File configFile = CONFIG_PATH.toFile();
        if (configFile.exists()) {
            try (Reader reader = new FileReader(configFile)) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                instance = (loaded != null) ? loaded : new ModConfig();
            } catch (IOException e) {
                System.err.println("[PvpHelper] Gagal memuat config: " + e.getMessage());
                instance = new ModConfig();
            }
        } else {
            instance = new ModConfig();
            save();
        }
    }

    public static void save() {
        try {
            File configFile = CONFIG_PATH.toFile();
            configFile.getParentFile().mkdirs();
            try (Writer writer = new FileWriter(configFile)) {
                GSON.toJson(instance, writer);
            }
        } catch (IOException e) {
            System.err.println("[PvpHelper] Gagal menyimpan config: " + e.getMessage());
        }
    }
}
