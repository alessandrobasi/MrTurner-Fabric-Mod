package it.alessandrobasi.mrturner.client;

import it.alessandrobasi.mrturner.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MrturnerClient implements ClientModInitializer {

    private final ConfigManager configManager = new ConfigManager();

    public MrturnerClient() {
        // noting to initialize
    }

    @Override
    public void onInitializeClient() {
        // noting to initialize
    }

}
