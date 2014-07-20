package me.sablednah.legendquest.skills;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SkillManifest(name = "Fence", type = SkillType.ACTIVE, 
author = "SableDnah", version = 1.1D, 
description = "Fence stolen goods (remove STOLEN! marker)", 
consumes = "", manaCost = 10, 
levelRequired = 0, skillPoints = 5, 
buildup = 0, delay = 0, duration = 0, cooldown = 60000, 
dblvarnames = { "chance" }, dblvarvalues = { 75.0 }, 
intvarnames = {}, intvarvalues = {}, 
strvarnames = {}, strvarvalues = {}
)
public class Fence extends Skill {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */ }

	@SuppressWarnings("deprecation")
	public CommandResult onCommand(Player p) { 
		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);

		// System.out.print("data: "+getName()+" | "+data.aliasedname+" | "+data.name + " | " +data.description );
		// System.out.print("vars:" + data.vars.toString());

		Double chance = ((Double) data.vars.get("chance")) / 100.0D;

		// set lore to add "stolen" string?

		ItemStack inhand = p.getItemInHand();
		
		Material inhandmat = null;
		if (inhand==null) {
			inhandmat = Material.AIR;			
		} else {
			inhandmat = inhand.getType();
		}
		if (inhandmat == null) {
			p.sendMessage("Sorry, you need to hold the item to fence...");
			return CommandResult.FAIL;
		}

		if (Math.random() >= chance) {
			p.sendMessage("Fencing failed...");
			return CommandResult.SUCCESS; // the use worked - mana is burnt even if picked
		}

		ItemMeta im = inhand.getItemMeta();
		List<String> lore = im.getLore();
		if (lore == null) {
			p.sendMessage("Sorry, you need to hold a stolen item...");
			return CommandResult.FAIL;
		}
		if (lore.contains("STOLEN!")) {
			lore.remove("STOLEN!");
			im.setLore(lore);
			inhand.setItemMeta(im);
			p.updateInventory();
			p.sendMessage("Item 'cleaned'.");
		} else {
			p.sendMessage("Sorry, thats not a stolen item...");
			return CommandResult.FAIL;
		}
		return CommandResult.SUCCESS;
	}
}
