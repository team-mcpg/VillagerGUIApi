package masecla.villager.adapters;

import masecla.villager.classes.VillagerInventory;
import org.bukkit.entity.Player;

public abstract class BaseAdapter {

	public VillagerInventory toAdapt;

	public BaseAdapter(VillagerInventory toAdapt) {
		super();
		this.toAdapt = toAdapt;
	}

	public abstract void openFor(Player p);
}
