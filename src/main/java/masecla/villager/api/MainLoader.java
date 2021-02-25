package masecla.villager.api;

import masecla.villager.adapters.BaseAdapter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class MainLoader extends JavaPlugin {

	private static Class<? extends BaseAdapter> versionAdapter = null;
	private AdapterLoader loader = null;

	@Override
	public void onEnable() {
		super.onEnable();

		try {
			loader = new AdapterLoader(this);
			loader.reflectivelyLoad();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		loader.close();
	}

	protected void swapAdapter(Class<? extends BaseAdapter> adapter) {
		versionAdapter = adapter;
	}

	public static Class<? extends BaseAdapter> getAdapter() {
		return versionAdapter;
	}

}
