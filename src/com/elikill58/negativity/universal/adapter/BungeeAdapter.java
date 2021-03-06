package com.elikill58.negativity.universal.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.elikill58.negativity.universal.AbstractCheat;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeAdapter extends Adapter {

	private Configuration config;
	private Plugin pl;
	private final HashMap<String, Configuration> LANGS = new HashMap<>();

	public BungeeAdapter(Plugin pl, Configuration config) {
		this.pl = pl;
		this.config = config;
	}

	@Override
	public Object getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return pl.getDataFolder();
	}

	@Override
	public String getStringInConfig(String dir) {
		return config.getString(dir);
	}

	@Override
	public boolean getBooleanInConfig(String dir) {
		return config.getBoolean(dir);
	}

	@Override
	public void log(String msg) {
		pl.getLogger().info(msg);
	}

	@Override
	public void warn(String msg) {
		pl.getLogger().warning(msg);
	}

	@Override
	public void error(String msg) {
		pl.getLogger().severe(msg);
	}

	@Override
	public HashMap<String, String> getKeysListInConfig(String dir) {
		HashMap<String, String> list = new HashMap<>();
		for (String s : config.getSection(dir).getKeys())
			list.put(s, config.getString(dir + "." + s));
		return list;
	}

	@Override
	public int getIntegerInConfig(String dir) {
		return config.getInt(dir);
	}

	@Override
	public void set(String dir, Object value) {
		config.set(dir, value);
	}

	@Override
	public double getDoubleInConfig(String dir) {
		return config.getDouble(dir);
	}

	@Override
	public List<String> getStringListInConfig(String dir) {
		return config.getStringList(dir);
	}

	@Override
	public String getStringInOtherConfig(String fileDir, String valueDir, String fileName) {
		File f = new File(pl.getDataFolder().getAbsolutePath() + fileDir);
		if (!f.exists())
			copy(fileName, f);
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(f).getString(valueDir);
		} catch (IOException e) {
			e.printStackTrace();
			return "Unknow";
		}
	}

	@Override
	public File copy(String lang, File f) {
		if (f.exists())
			return f;
		String fileName = "bungee_en_US.yml";
		if (lang.toLowerCase().contains("fr") || lang.toLowerCase().contains("be"))
			fileName = "bungee_fr_FR.yml";
		if (lang.toLowerCase().contains("pt") || lang.toLowerCase().contains("br"))
			fileName = "bungee_pt_BR.yml";
		if (lang.toLowerCase().contains("no"))
			fileName = "bungee_no_NO.yml";
		else if (lang.toLowerCase().contains("ru"))
			fileName = "bungee_ru_RU.yml";
		// TODO : Espagnol & Allemand
		try (InputStream in = pl.getResourceAsStream(fileName); OutputStream out = new FileOutputStream(f)) {
			ByteStreams.copy(in, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}

	@Override
	public void loadLang() {
		if(!TranslatedMessages.activeTranslation)
			return;
		try {
			LANGS.put("no_active",
					ConfigurationProvider.getProvider(YamlConfiguration.class)
							.load(copy("default", new File(pl.getDataFolder().getAbsolutePath() + File.separator
									+ getStringInConfig("Translation.no_active_file_name")))));
			File langDir = new File(pl.getDataFolder().getAbsolutePath() + File.separator + "lang" + File.separator);
			if (!langDir.exists())
				langDir.mkdirs();
			for (String l : config.getStringList("Translation.lang_available")) {
				LANGS.put(l, ConfigurationProvider.getProvider(YamlConfiguration.class)
						.load(copy(l, new File(langDir.getAbsolutePath() + "/" + l + ".yml"))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getStringFromLang(String lang, String key) {
		return "";
	}

	@Override
	public List<String> getStringListFromLang(String lang, String key) {
		return new ArrayList<>();
	}

	@Override
	public List<AbstractCheat> getAbstractCheats() {
		return new ArrayList<>();
	}
	
	@Override
	public void reload() {
		
	}
	
	@Override
	public Object getItem(String itemName) {
		return null;
	}
}
