package me.sablednah.legendquest.skills;

import org.bukkit.entity.Player;

@SkillManifest(name = "Escape", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
description = "Restore speed, breaking immobilising skills", 
consumes = "", manaCost = 5, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 60000, 
dblvarnames = { }, dblvarvalues = { }, 
intvarnames = {	}, intvarvalues = { }, 
strvarnames = { }, strvarvalues = { }
)
public class Escape extends Skill {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */ }

	public CommandResult onCommand(Player p) { 
		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}
		
		p.setWalkSpeed(getPC(p).getSpeed());

		return CommandResult.SUCCESS;
	}
}
