package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.events.AbilityCheckEvent;
import me.sablednah.legendquest.events.SkillTick;

import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SkillManifest(name = "NightAffinity", type = SkillType.PASSIVE, author = "SableDnah", version = 1.0D, 
description = "Bonus stats at night, damage/penalty in light", consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 0, 
dblvarnames = { "regenerate", "damage" }, dblvarvalues = { 1.0,1.0 }, 
intvarnames = { "minlight", "maxlight", "regeninterval","damageinterval","nightbonus","daypenalty","lightpenalty","darkbonus", "sunonly" }, 
intvarvalues = { 5, 10, 5, 5, 1, 1, 1, 1, 0}, 
strvarnames = {  }, strvarvalues = { })
public class NightAffinity extends Skill implements Listener {
	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command
		return CommandResult.NOTAVAILABLE;
	}
/*
 * /time set <number | day | night>
/time set 0     || Sets the time to dawn.
/time set day   || Sets the time to 1000.
/time set 6000  || Sets the time to midday
/time set 12000 || Sets the time to dusk
/time set night || Sets the time to 14000.
/time set 18000 || Sets the time to midnight
 */

	@EventHandler
	public void skillTick(SkillTick event) {
		Player p = event.getPlayer();
		if (!validSkillUser(p)) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		Integer sun = ((Integer) data.vars.get("sunonly"));
		
		Block b = p.getLocation().getBlock();		
		int light = 0;
		if (sun>0) {
			light = b.getLightFromSky();
		} else {
			light = b.getLightLevel();			
		}

		Integer minlight = ((Integer) data.vars.get("minlight"));
		Integer maxlight = ((Integer) data.vars.get("maxlight"));
		Integer regeninterval = ((Integer) data.vars.get("regeninterval"));
		Integer damageinterval = ((Integer) data.vars.get("damageinterval"));
		
		double regenerate = ((Double) data.vars.get("regenerate"));
		double damage = ((Double) data.vars.get("damage"));

		if (light<minlight) {
			if ((lq.players.ticks % ((regeninterval*20)/lq.configMain.skillTickInterval)) == 0 ) {
				getPC(p).heal(regenerate);
			}
		}
		if (light>maxlight) {
			if ((lq.players.ticks % ((damageinterval*20)/lq.configMain.skillTickInterval)) == 0 ) {
				getPC(p).damage(damage);
			}
		}
	}
	
	@EventHandler
	public void statcheck(AbilityCheckEvent event){
		if (!validSkillUser(event.getPc())) {
			return;
		}

		Player p = event.getPc().getPlayer();
		if (p == null) { return; }

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(event.getPc());

		Integer sun = ((Integer) data.vars.get("sunonly"));
		Integer minlight = ((Integer) data.vars.get("minlight"));
		Integer maxlight = ((Integer) data.vars.get("maxlight"));

		Block b = p.getLocation().getBlock();		
		int light = 0;
		if (sun>0) {
			light = b.getLightFromSky();
		} else {
			light = b.getLightLevel();			
		}

		if (light<minlight) {
			Integer darkbonus = ((Integer) data.vars.get("darkbonus"));
			event.addBonus(darkbonus);
		}
		if (light>maxlight) {
			Integer lightpenalty = ((Integer) data.vars.get("lightpenalty"));
			event.removePenalty(lightpenalty);
		}
		
		long time = p.getWorld().getTime();
		boolean day = false;
		if (time>999 && time<14000) {
			day = true;
		} else {
			day = false;
		}
		if (p.getWorld().getEnvironment() == Environment.NETHER || p.getWorld().getEnvironment() == Environment.THE_END) {
			day = false;
		}
		
		//"nightbonus","daypenalty","lightpenalty","darkbonus"
		if (day) {
			Integer daypenalty = ((Integer) data.vars.get("daypenalty"));
			event.removePenalty(daypenalty);			
		} else {
			Integer nightbonus = ((Integer) data.vars.get("nightbonus"));
			event.addBonus(nightbonus);
		}
	}
}