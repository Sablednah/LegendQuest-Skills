package me.sablednah.legendquest.skills;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SkillManifest(name = "SummonItems", type = SkillType.ACTIVE, author = "SableDnah", version = 2.1D, description = "Summon [qty] x [material] [customname] items", consumes = "", manaCost = 5, levelRequired = 0, skillPoints = 0, buildup = 0, delay = 0, duration = 0, cooldown = 100000, dblvarnames = {}, dblvarvalues = {}, intvarnames = {
		"qty", "data" }, intvarvalues = { 1, -1 }, strvarnames = { "material", "customname", "lore" }, strvarvalues = { "PORK", "", "" })
public class SummonItem extends Skill {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) {
		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		String material = ((String) data.vars.get("material"));
		String customname = ((String) data.vars.get("customname"));
		String lore = ((String) data.vars.get("lore"));
		Integer qty = ((Integer) data.vars.get("qty"));
		Integer dataval = ((Integer) data.vars.get("data"));

		ItemStack item = null;

		try {
			if (dataval > -1) {
				item = new ItemStack(Material.matchMaterial(material), qty, dataval.shortValue());
			} else {
				item = new ItemStack(Material.matchMaterial(material), qty);
			}
		} catch (IllegalArgumentException exp) {
			lq.debug.warning("'" + material + "' is not a valid item name for skill 'SummonItem'");
			exp.printStackTrace();			
			return CommandResult.FAIL;
		}

		ItemMeta iteminfo = item.getItemMeta();
		if (!customname.isEmpty()) {
			iteminfo.setDisplayName(customname);
		}

		if (!lore.isEmpty()) {
			String[] split = lore.split("\\|");
			List<String> wordList = Arrays.asList(split);
			iteminfo.setLore(wordList);
		}

		item.setItemMeta(iteminfo);
		p.getWorld().dropItemNaturally(p.getLocation(), item);

		return CommandResult.SUCCESS;
	}
}
