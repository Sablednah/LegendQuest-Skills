package me.sablednah.legendquest.skills;

import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

@SkillManifest(
		name = "Taunt", type = SkillType.ACTIVE, 
		author = "SableDnah", version = 1.0D, 
		description = "Taunt nearby monsters (Range [distance])", 
		consumes = "", manaCost = 5, 
		levelRequired = 0, skillPoints = 5, 
		buildup = 0, delay = 0, duration = 0, cooldown = 60000, 
		dblvarnames = {}, dblvarvalues = {}, 
		intvarnames = { "distance" }, intvarvalues = { 5 }, 
		strvarnames = {}, strvarvalues = {}
	)
public class Taunt extends Skill {

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

		Integer distance = ((Integer) data.vars.get("distance"));

		List<Entity> mobs = p.getNearbyEntities(distance, distance, distance);
		for (Entity e : mobs) {
			if ((e instanceof Monster)) {
				((Monster) e).setTarget(p);
			} else if (e instanceof Wolf) {
				Wolf w = (Wolf) e;
				w.setAngry(true);
				w.setTarget(p);
			}
		}

		return CommandResult.SUCCESS;
	}
}
