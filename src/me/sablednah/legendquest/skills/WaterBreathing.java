package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.events.SkillTick;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SkillManifest(name = "WaterBreathing", type = SkillType.PASSIVE, author = "SableDnah", version = 1.0D, 
description = "Gain [rate] air every skill tick.", 
consumes = "", manaCost = 0, 
levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 0, 
dblvarnames = {}, dblvarvalues = {}, 
intvarnames = {"rate"}, intvarvalues = { 5 }, 
strvarnames = { }, strvarvalues = { }
)
public class WaterBreathing extends Skill implements Listener {
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
		Integer rate = ((Integer) data.vars.get("rate"));
		
		int max = p.getMaximumAir();
		int remain = p.getRemainingAir();
		p.setRemainingAir(Math.min(remain+rate, max));
	}
}
