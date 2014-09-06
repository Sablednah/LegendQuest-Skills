package me.sablednah.legendquest.skills;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.sablednah.legendquest.mechanics.Attribute;
import me.sablednah.legendquest.mechanics.Difficulty;
import me.sablednah.legendquest.mechanics.Mechanics;
import me.sablednah.legendquest.utils.Utils;
import me.sablednah.legendquest.utils.plugins.PluginUtils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SkillManifest(name = "PickPocket", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
description = "Steal an item from another player", 
consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 5, buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
dblvarnames = { "chance" }, dblvarvalues = { 50.0 }, 
intvarnames = {	"qty", "distance", "marked" }, intvarvalues = { 1, 10, 1 }, 
strvarnames = { "holding", "blacklist" }, strvarvalues = { "ANY", "EMERALD" }
)
public class PickPocket extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	@SuppressWarnings("deprecation")
	public CommandResult onCommand(Player p) { // does not require command
		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);

		// System.out.print("data: "+getName()+" | "+data.aliasedname+" | "+data.name + " | " +data.description );
		// System.out.print("vars:" + data.vars.toString());

		String holding = ((String) data.vars.get("holding"));
		String blacklist = ((String) data.vars.get("blacklist"));
		Integer qty = ((Integer) data.vars.get("qty"));
		Integer distance = ((Integer) data.vars.get("distance"));
		Double chance = ((Double) data.vars.get("chance")) / 100.0D;

		// set lore to add "stolen" string?

		ItemStack inhanditem = p.getItemInHand();
		Material inhand = null;
		if (inhanditem==null) {
			inhand = Material.AIR;			
		} else {
			inhand = inhanditem.getType();
		}
		boolean validitem = false;
		if (holding == null || holding.equals("") || holding.equalsIgnoreCase("any") || holding.equalsIgnoreCase("all")) {
			validitem = true;
		}
		if (((holding.equalsIgnoreCase("none") || holding.equalsIgnoreCase("hand")) && (inhand == null || inhand == Material.AIR))) {
			validitem = true;
		}
		if (validitem == false) {
			if (inhand == Material.matchMaterial(holding)) {
				validitem = true;
			}
		}

		// Get target
		if (!validitem) {
			p.sendMessage("Sorry, you need to hold " + holding + " to pick pockets");
			return CommandResult.FAIL;
		}

		if (Math.random() >= chance) {
			p.sendMessage("Pickpocket failed...");
			return CommandResult.SUCCESS; // the use worked - mana is burnt even if picked
		}

		// Get target
		Player target = Utils.getTargetPlayer(p, distance);
		if (target == null) {
			p.sendMessage("Sorry, you need to look at a player to pick their pocket...");
			return CommandResult.FAIL;
		}

		if (!PluginUtils.canBuild(target.getLocation(), p)) {
			p.sendMessage("You can't steal, target is in safe location...");
			return CommandResult.FAIL;
		}

		// ok so you have looked at a player and are picking their pocket.
		ArrayList<Material> blacklisted = new ArrayList<Material>();
		String[] list = blacklist.split("\\s*,\\s*");
		for (String s : list) {
			blacklisted.add(Material.matchMaterial(s));
		}

		Inventory inv = target.getInventory();
		ItemStack[] invItems = inv.getContents();

		// lets stop infinate loops... check if their backpack has ANY items you can nick
		int i = 9;
		ArrayList<Integer> validSlots = new ArrayList<Integer>();
		for (i = 9; i <= 27; i++) {
			if (invItems[i] != null) {
				if (!blacklisted.contains(invItems[i].getType())) {
					validSlots.add(i);
				}
			}
		}

		if (validSlots.isEmpty()) {
			p.sendMessage(target.getDisplayName() + " has nothing you can steal...");
			return CommandResult.SUCCESS; // the use worked - mana is burnt even if picked
		}

		int random = new Random().nextInt(validSlots.size());

		ItemStack item = invItems[validSlots.get(random)];
		ItemStack todrop = null;
		if (item.getAmount() <= qty) { // take them all!!
			inv.setItem(validSlots.get(random), null);
			todrop = item;
		} else { // reduce by qty
			item.setAmount(item.getAmount() - qty);
			inv.setItem(validSlots.get(random), item);
			todrop = item;
			todrop.setAmount(qty);
		}
		p.updateInventory();

		ItemMeta im = todrop.getItemMeta();
		List<String> lore = im.getLore();
		if (lore == null) {
			lore = new ArrayList<String>();
		}
		lore.add("STOLEN!");
		im.setLore(lore);
		todrop.setItemMeta(im);

		p.getWorld().dropItem(p.getLocation(), todrop);

		boolean test = Mechanics.opposedTest(getPC(p), Difficulty.TOUGH, Attribute.DEX, getPC(target), Difficulty.EASY, Attribute.WIS);

		if (!test) {
			p.sendMessage("Target noticed you stealing " + todrop.getType().toString() + "!");
			target.sendMessage(p.getDisplayName() + " just stole " + todrop.getType().toString() + " from you!");
		} else {
			p.sendMessage("You stole " + todrop.getType().toString() + " undetected!");
		}

		return CommandResult.SUCCESS;
	}
}
