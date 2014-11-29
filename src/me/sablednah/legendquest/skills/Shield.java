package me.sablednah.legendquest.skills;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@SkillManifest(name = "Shield", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
description = "+[soak] damage prevention. [soakchance]% chance.", 
consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 10000, cooldown = 10000, 
dblvarnames = { "soakchance", "soakpercent" }, dblvarvalues = { 100.0, 0.0 }, 
intvarnames = { "soak" }, intvarvalues = { 5 }, 
strvarnames = { "holding" ,"message" }, strvarvalues = { "","Sheilds up!" }
)
public class Shield extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command
		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		String message = ((String) data.vars.get("message"));
		p.sendMessage(message);

		return CommandResult.SUCCESS;
	}

	@EventHandler
	public void doSheild(EntityDamageByEntityEvent event) {
		if ((event.getEntity() instanceof Player)) {
			Player p = (Player) event.getEntity();
			if (!validSkillUser(p)) {
				return;
			}

			// load skill options
			SkillDataStore data = this.getPlayerSkillData(p);

			SkillPhase phase = data.checkPhase();

			if (phase.equals(SkillPhase.ACTIVE)) {
				double chance = ((Double) data.vars.get("soakchance")) / 100.0D;
				if (Math.random() <= chance) {

					boolean any = false;
					ArrayList<Material> weapons = new ArrayList<Material>();
					String w = ((String) data.vars.get("holding"));
					if (w == null) {
						w = "";
						weapons.add(null);
						weapons.add(Material.AIR);
						any=true;
						// System.out.print("adding weapon: hand");
					} else {
						if (w.isEmpty()) {
							weapons.add(null);
							weapons.add(Material.AIR);
							any=true;
							// System.out.print("adding weapon: hand");
						}
					}

					String[] list = w.split("\\s*,\\s*");
					for (String s : list) {
						if (s.equalsIgnoreCase("hand") || s.equalsIgnoreCase("hands")) {
							// System.out.print("adding weapon: hand");
							weapons.add(null);
							weapons.add(Material.AIR);
						} else if (s.equalsIgnoreCase("any") || s.equalsIgnoreCase("all")) {
							any=true;
						} else {
							Material mat = Material.matchMaterial(s);
							weapons.add(mat);
							// System.out.print("adding weapon: "+mat);
						}
					}

					// System.out.print("looking for weapon: "+p.getItemInHand().getType());

					if (any || (weapons.contains(p.getItemInHand().getType()))) {

						Integer soak = (Integer) data.vars.get("soak");
						Double soakp = 1.0D - (((Double) data.vars.get("soakpercent")) / 100.0D);
						double dmg = event.getDamage();
						dmg = dmg * soakp;
						dmg = dmg - soak;
						if (dmg < 0) {
							dmg = 0;
						}
						if (dmg == 0) {
							event.setCancelled(true);
							event.setDamage(dmg);
						}
					}
				}
			}
		}
	}
}
