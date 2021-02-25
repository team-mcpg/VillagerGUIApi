package masecla.villager.adapters.instances;

import masecla.villager.adapters.BaseAdapter;
import masecla.villager.classes.VillagerInventory;
import masecla.villager.classes.VillagerTrade;
import masecla.villager.events.VillagerInventoryCloseEvent;
import masecla.villager.events.VillagerInventoryModifyEvent;
import masecla.villager.events.VillagerInventoryOpenEvent;
import masecla.villager.events.VillagerTradeCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftMerchantCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MerchantAdapter_v1_16_R3 extends BaseAdapter implements Listener {

	private final CraftMerchantCustom wrapped;

	public MerchantAdapter_v1_16_R3(VillagerInventory toAdapt) {
		super(toAdapt);
		// TODO: Get rid of this magical value here somehow
		Bukkit.getServer().getPluginManager().registerEvents(this,
				Bukkit.getPluginManager().getPlugin("VillagerGUIApi"));
		wrapped = new CraftMerchantCustom(toAdapt.getName());
		wrapped.setRecipes(toNMSRecipes());
	}

	@Override
	public void openFor(Player p) {
		p.openMerchant(wrapped, true);
		VillagerInventoryOpenEvent event = new VillagerInventoryOpenEvent(toAdapt, p);
		Bukkit.getPluginManager().callEvent(event);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getPlayer().getUniqueId().equals(this.toAdapt.getForWho().getUniqueId())) {
			VillagerInventoryCloseEvent closeEvent = new VillagerInventoryCloseEvent(toAdapt,
					(Player) event.getPlayer());
			Bukkit.getPluginManager().callEvent(closeEvent);
			HandlerList.unregisterAll(this); // Kill this event listener
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getAction() == InventoryAction.NOTHING) return;
		if (event.getWhoClicked().getUniqueId().equals(this.toAdapt.getForWho().getUniqueId())) {
			VillagerInventoryModifyEvent modifyEvent = new VillagerInventoryModifyEvent(toAdapt,
					(Player) event.getWhoClicked(), event.getCurrentItem());
			Bukkit.getPluginManager().callEvent(modifyEvent);
			if (event.getRawSlot() == -999) return;
			VillagerTrade trade = toAdapt.getTrades().get(((MerchantInventory)
					event.getWhoClicked().getOpenInventory().getTopInventory()).getSelectedRecipeIndex());
			int tradescount = 1;
			if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
				// This situation is where the player SHIFT+CLICKS the output item to buy multiple times at once.
				ItemStack itemOne = this.toAdapt.getForWho().getOpenInventory().getTopInventory().getItem(0);
				ItemStack itemTwo = this.toAdapt.getForWho().getOpenInventory().getTopInventory().getItem(1);
				ItemStack result = event.getCurrentItem();
				int cantrade;
				/* 1 - Get how many the player can trade with his items */
				if (itemOne == null || result==null) return;
				cantrade = (int) Math.floor((double) itemOne.getAmount() / (double) trade.getItemOne().getAmount());
				if (itemTwo != null) {
					cantrade = Math.min(cantrade,
							(int) Math.floor((double) itemTwo.getAmount() / (double) trade.getItemTwo().getAmount()));
				}
				/* 2 - Check max stack size of result */
				cantrade = Math.min(cantrade, result.getMaxStackSize());
				Bukkit.getLogger().info(String.valueOf(cantrade));
				/* 3 - Get how many times the player can store the trade result in his inventory */
				for (int i=1; i <= cantrade; i++) {
					Bukkit.getLogger().info(String.valueOf(tradescount));
					if (canStore(event.getWhoClicked().getInventory(), result, i)) {
						tradescount = i;
					} else break;
				}
			}
			if (event.getRawSlot() == 2 && !event.getCurrentItem().getType().equals(Material.AIR)) {
				Bukkit.getPluginManager().callEvent(
						new VillagerTradeCompleteEvent(toAdapt, (Player) event.getWhoClicked(), trade, tradescount));
			}
		}
	}

	public List<MerchantRecipe> toNMSRecipes() {
		List<MerchantRecipe> result = new ArrayList<>();
		for (VillagerTrade trd : this.toAdapt.getTrades()) {
			MerchantRecipe toAdd = new MerchantRecipe(trd.getResult(), trd.getMaxUses());
			toAdd.addIngredient(trd.getItemOne());
			if (trd.requiresTwoItems())
				toAdd.addIngredient(trd.getItemTwo());
			result.add(toAdd);
		}

		return result;
	}

	/**
	 *      Check if player can store ItemStack in his inventory
	 */
	public boolean canStore(Inventory baseInv, ItemStack items, int nbAdd) {
		Inventory inv = Bukkit.createInventory(null, 36, "canStore");
		inv.setContents(baseInv.getStorageContents());
		for (int i=0;i<nbAdd;i++){
			final Map<Integer, ItemStack> map = inv.addItem(items); // Attempt to add in inventory
			if (!map.isEmpty()) { // If not empty, it means the player's inventory is full.
				return false;
			}
		}
		return true;
	}
}
