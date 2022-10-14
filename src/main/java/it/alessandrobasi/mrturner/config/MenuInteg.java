package it.alessandrobasi.mrturner.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class MenuInteg implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Text.of("Config"));

			ConfigEntryBuilder entryBuilder = builder.entryBuilder();

			builder.getOrCreateCategory(Text.of("Category"))
				.addEntry(entryBuilder.startIntField(Text.of("Delay"), ConfigManager.getConfig().getDelay())
					.setDefaultValue(5000)
					.setSaveConsumer(newVal -> ConfigManager.getConfig().setDelay(newVal))
					.build())
				.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable?"), ConfigManager.getConfig().isEnable())
						.setDefaultValue(true)
						.setSaveConsumer(newVal -> ConfigManager.getConfig().setEnable(newVal))
						.build());



			builder.setSavingRunnable(ConfigManager::save);
			return builder.build();
		};
	}
}
