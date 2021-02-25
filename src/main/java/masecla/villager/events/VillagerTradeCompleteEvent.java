package masecla.villager.events;

import masecla.villager.classes.VillagerInventory;
import masecla.villager.classes.VillagerTrade;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VillagerTradeCompleteEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private final VillagerInventory inventory;
	private final Player player;
	private final VillagerTrade trade;
	private final Integer count;

	public VillagerTradeCompleteEvent(VillagerInventory inventory, Player player, VillagerTrade trade, Integer count) {
		super();
		this.inventory = inventory;
		this.player = player;
		this.trade = trade;
		this.count = count;
	}

	public VillagerInventory getInventory() {
		return inventory;
	}

	public Player getPlayer() {
		return player;
	}

	public VillagerTrade getTrade() {
		return trade;
	}

	public Integer getCount() {
		return count;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

}
