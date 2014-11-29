package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.utils.Utils;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

@SkillManifest(
	name = "Curse", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
	description = "Curse Player", 
	consumes = "", manaCost = 10, 
	levelRequired = 0, skillPoints = 0, 
	buildup = 0, delay = 0, duration = 10000, cooldown = 10000, 
	dblvarnames = { }, dblvarvalues = { }, 
	intvarnames = { "distance", "str", "dex", "con", "int", "wis", "chr" }, intvarvalues = { 16,0,0,0,0,0,0 }, 
	strvarnames = { "message" }, strvarvalues = { "Cursed: " }
)

public class Curse extends Skill implements Listener {
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

		Integer distance = ((Integer) data.vars.get("distance"));
		// Get target
		Player target = Utils.getTargetPlayer(p, distance);
		if (target == null) {
			p.sendMessage("Sorry, you need to look at a target...");
			return CommandResult.FAIL;
		}

		long time = System.currentTimeMillis();
//		System.out.print("Curse time: "+time);
		time +=data.duration;
//		System.out.print("Curse time: "+data.duration);
//		System.out.print("Curse time: "+time);

		target.setMetadata("cursetimeout", new FixedMetadataValue(lq, (time) ));
		target.setMetadata("str", new FixedMetadataValue(lq, (Integer) data.vars.get("str")));
		target.setMetadata("dex", new FixedMetadataValue(lq, (Integer) data.vars.get("dex")));
		target.setMetadata("con", new FixedMetadataValue(lq, (Integer) data.vars.get("con")));
		target.setMetadata("int", new FixedMetadataValue(lq, (Integer) data.vars.get("int")));
		target.setMetadata("wis", new FixedMetadataValue(lq, (Integer) data.vars.get("wis")));
		target.setMetadata("chr", new FixedMetadataValue(lq, (Integer) data.vars.get("chr")));

		String message = ((String) data.vars.get("message"));
		
		p.sendMessage(message + target.getDisplayName());
		
		return CommandResult.SUCCESS;
	}
	
}
