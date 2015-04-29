package me.sablednah.legendquest.skills;

import java.util.HashSet;
import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

@SkillManifest(name = "Enchant", type = SkillType.ACTIVE, author = "SableDnah", version = 2.1D, 
description = "Apply [effect] to area of [radius] block radius.", 
consumes = "", manaCost = 5, 
levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 5000, cooldown = 10000, 
dblvarnames = {}, dblvarvalues = {}, 
intvarnames = {"radius", "range"}, intvarvalues = { 3, 100 }, 
strvarnames = { "effect","message" }, strvarvalues = { "CONFUSION","Nauseous..." }
)
public class Enchant extends Skill {

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
		String eff = ((String) data.vars.get("effect"));
		Integer radius = ((Integer) data.vars.get("radius"));
		Integer range = ((Integer) data.vars.get("range"));
		
		Location bl = null;
		if (range > 0) {
			@SuppressWarnings("deprecation")
			Block block = p.getTargetBlock((HashSet<Byte>) null, range);
			bl = block.getRelative(BlockFace.UP).getLocation();
		} else {
			bl = p.getLocation();
		}
		Effects ef = null;
		try {
			ef = Effects.valueOf(eff.toUpperCase());  
		} catch (IllegalArgumentException exp) {
			lq.debug.warning("'"+eff + "' is not a valid effects name for skill '"+data.name+"'");
			return CommandResult.FAIL;
		}
		
		EffectProcess ep =  new EffectProcess(ef, data.duration, OwnerType.LOCATATION, bl, radius);
		lq.effectManager.addPendingProcess(ep);
		String msg = ((String) data.vars.get("message"));
		p.sendMessage(msg);

		return CommandResult.SUCCESS;
	}
}
