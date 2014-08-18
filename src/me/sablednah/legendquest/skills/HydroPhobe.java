package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.events.AbilityCheckEvent;
import me.sablednah.legendquest.events.SkillTick;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SkillManifest(name = "HydroPhobe", type = SkillType.PASSIVE, author = "SableDnah", version = 1.0D, 
description = "Bonus stats at night, damage/penalty in light", 
consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 0, 
dblvarnames = {	"raindamage", "damage" }, dblvarvalues = { 1.0, 1.0 }, 
intvarnames = { "raindamageinterval", "damageinterval", "waterstatmod", "rainstatmod" }, intvarvalues = { 5, 5, 1, 1 }, 
strvarnames = {}, strvarvalues = {})
public class HydroPhobe extends Skill implements Listener {
	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command
		return CommandResult.NOTAVAILABLE;
	}

	@EventHandler
	public void skillTick(SkillTick event) {
		Player p = event.getPlayer();

		if (!validSkillUser(p)) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);

		Integer damageinterval = ((Integer) data.vars.get("damageinterval"));
		double damage = ((Double) data.vars.get("damage"));

		if ((lq.players.ticks % ((damageinterval * 20) / lq.configMain.skillTickInterval)) == 0) {
			if (p.getLocation().getBlock().isLiquid()) {
				if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
					getPC(p).damage(damage);
				}
			}
		}

		if (!p.getWorld().hasStorm()) {
			return;
		}

		Integer raindamageinterval = ((Integer) data.vars.get("raindamageinterval"));
		double raindamage = ((Double) data.vars.get("raindamage"));

		if ((lq.players.ticks % ((raindamageinterval * 20) / lq.configMain.skillTickInterval)) == 0) {
			if (p.getWorld().getHighestBlockAt(p.getLocation()).getY() <= p.getLocation().getY()) { // outside
				getPC(p).damage(raindamage);
			}
		}
	}

	@EventHandler
	public void statcheck(AbilityCheckEvent event) {
		if (!validSkillUser(event.getPc())) {
			return;
		}

		Player p = event.getPc().getPlayer();
		if (p == null) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(event.getPc());

		Integer waterstatmod = ((Integer) data.vars.get("waterstatmod"));

		if (p.getLocation().getBlock().isLiquid()) {
			if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
				event.removePenalty(waterstatmod);
			}
		}

		if (!p.getWorld().hasStorm()) {
			return;
		}

		Integer rainstatmod = ((Integer) data.vars.get("rainstatmod"));

		if (p.getWorld().getHighestBlockAt(p.getLocation()).getY() <= p.getLocation().getY()) { // outside
			event.removePenalty(rainstatmod);
		}
	}
}