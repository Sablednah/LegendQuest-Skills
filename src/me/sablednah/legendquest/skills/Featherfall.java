package me.sablednah.legendquest.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@SkillManifest(name = "FeatherFall", type = SkillType.PASSIVE, author = "SableDnah", version = 1.0D, 
description = "Prevent Fall Damage", 
consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 0, 
dblvarnames = { "chance","percentage" }, dblvarvalues = { 50.0, 99.0 }, 
intvarnames = { "soak" }, intvarvalues = { 1, 5 }, 
strvarnames = { }, strvarvalues = { }
)
public class Featherfall extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command
		return CommandResult.NOTAVAILABLE;
	}

	@EventHandler
	public void doDmg(EntityDamageEvent event) {
		if ((event.getEntity() instanceof Player)) {
			if (event.getCause() != DamageCause.FALL) { return; }
			
			Player p = (Player) event.getEntity();
			if (!validSkillUser(p)) {
				return;
			}

			// load skill options
			SkillDataStore data = this.getPlayerSkillData(p);

			double chance = ((Double) data.vars.get("chance")) / 100.0D;
			if (Math.random() <= chance) {

				double percent = ((Double) data.vars.get("percent")) / 100.0D;
				double soak = (Integer) data.vars.get("soak");
				
				double dmg = event.getDamage();
				
				dmg = dmg - soak;
				dmg = dmg * (1.0-percent);
				if (dmg<0) {
					event.setCancelled(true);
					dmg=0;
				}
				event.setDamage(0);				
			}
		}
	}
}
