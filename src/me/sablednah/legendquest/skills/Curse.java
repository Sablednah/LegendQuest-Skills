package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

@SkillManifest(
	name = "Curse", type = SkillType.ACTIVE, author = "SableDnah", version = 2.0D, 
	description = "Curse Player", 
	consumes = "", manaCost = 10, 
	levelRequired = 0, skillPoints = 0, 
	buildup = 0, delay = 0, duration = 10000, cooldown = 10000, 
	dblvarnames = { }, dblvarvalues = { }, 
	intvarnames = { "damage", "distance", "str", "dex", "con", "int", "wis", "chr" }, intvarvalues = { 16,0,0,0,0,0,0 }, 
	strvarnames = { "message", "effects" }, strvarvalues = { "Cursed: ", "" }
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
		Integer damage = ((Integer) data.vars.get("damage"));
		String effects = ((String) data.vars.get("effects"));

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
		
		target.damage(damage,p);
		
		if (effects != null && !effects.isEmpty()) {
			int duration = data.duration;
			String[] list = null;
			list = effects.split("\\s*,\\s*");
			for (String s : list) {
				Effects ef = Effects.valueOf(s.toUpperCase());
				EffectProcess ep = null;
				if ((target instanceof Player)) {
					Player p2 = (Player) target;
					ep = new EffectProcess(ef, duration, OwnerType.PLAYER, p2.getUniqueId());
				} else {
					ep = new EffectProcess(ef, duration, OwnerType.MOB, target.getUniqueId());
				}
				lq.effectManager.addPendingProcess(ep);
			}
		}

		String message = ((String) data.vars.get("message"));
		
		p.sendMessage(message + target.getDisplayName());
		
		return CommandResult.SUCCESS;
	}
	
}
