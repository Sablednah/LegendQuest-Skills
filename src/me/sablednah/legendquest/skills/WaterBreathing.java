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

	public CommandResult onCommand(Player p) {
		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		if (data.type==SkillType.PASSIVE) { // does not require command
			return CommandResult.NOTAVAILABLE;
		}
		
		String message = ((String) data.vars.get("message"));
		p.sendMessage(message);

		return CommandResult.SUCCESS;
	}

	
	@EventHandler	
	public void skillTick(SkillTick event) {
		Player p = event.getPlayer();
		if (!validSkillUser(p)) {
			return;
		}
		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		SkillPhase phase = data.checkPhase();

		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {
			Integer rate = ((Integer) data.vars.get("rate"));
			
			int max = p.getMaximumAir();
			int remain = p.getRemainingAir();
			p.setRemainingAir(Math.min(remain+rate, max));
		}
	}
}
