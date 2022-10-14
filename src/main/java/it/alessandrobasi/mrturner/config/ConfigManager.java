package it.alessandrobasi.mrturner.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ConfigManager {

	private final String FILENAME = "MrTurner.json";
	private static ConfigManager instance;

	private final Path path;

	private Config config;

	public ConfigManager() {
		instance = this;
		path = FabricLoader.getInstance().getConfigDir();

		File fconf = path.resolve(FILENAME).toFile();
		if(!fconf.exists()) {
			config = new Config();
			doSave();
		}
		else{
			doLoad();
		}
	}

	public static Config getConfig() {
		return instance.config;
	}

	public static void save() {
		instance.doSave();
	}

	private void doSave(){
		File fconf = instance.path.resolve(FILENAME).toFile();
		try (FileWriter fw = new FileWriter(fconf, StandardCharsets.UTF_8)){
			new GsonBuilder()
				.setPrettyPrinting()
				.create()
				.toJson(config, Config.class, new JsonWriter(fw));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doLoad(){
		File fconf = path.resolve(FILENAME).toFile();
		Gson gson = new Gson();
		try(FileReader fr = new FileReader(fconf)) {
			config = gson.fromJson(new JsonReader(fr), Config.class);
		} catch (Exception e) {
			e.printStackTrace();
			config = new Config();
			Log.warn(LogCategory.LOG, "Error in json config, lodded default value");
		}

	}
}
