package it.alessandrobasi.mrturner.client;

import it.alessandrobasi.mrturner.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MrturnerClient implements ClientModInitializer {

    private ConfigManager configManager = new ConfigManager();

    private static MrturnerClient instance;

    public MrturnerClient() {
        instance = this;
    }

    public static MrturnerClient getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {

    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
