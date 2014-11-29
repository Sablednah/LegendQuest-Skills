package me.sablednah.legendquest.skills;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SkillManifest(name = "SummonItems", type = SkillType.ACTIVE, author = "SableDnah", version = 2.1D, 
description = "Summon [qty]x[material] items", 
consumes = "", manaCost = 5, 
levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 100000, 
dblvarnames = { }, dblvarvalues = { }, 
intvarnames = { "qty", "data" }, intvarvalues = { 1 , -1 }, 
strvarnames = { "material" }, strvarvalues = { "PORK" }
)
public class SummonItem extends Skill {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */ }

	public CommandResult onCommand(Player p) { 
		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		String material = ((String) data.vars.get("material"));
		Integer qty = ((Integer) data.vars.get("qty"));
		Integer dataval = ((Integer) data.vars.get("data"));		

		try {
			ItemStack item = null;

			if (dataval>-1) {
				item = new ItemStack(Material.matchMaterial(material),qty,dataval.shortValue());
			} else {
				item = new ItemStack(Material.matchMaterial(material),qty);
			}
			
			p.getWorld().dropItemNaturally(p.getLocation(), item);
			
		} catch (IllegalArgumentException exp) {
			lq.debug.warning("'"+material + "' is not a valid item name for skill 'SummonItem'");
			return CommandResult.FAIL;
		}
		return CommandResult.SUCCESS;
	}
}
