package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

@SkillManifest(name = "Enchant", type = SkillType.ACTIVE, author = "SableDnah", version = 1.2D, 
description = "Apply [effect] to area of [radius] block radius.", 
consumes = "", manaCost = 5, 
levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 5000, cooldown = 10000, 
dblvarnames = {}, dblvarvalues = {}, 
intvarnames = {"radius"}, intvarvalues = { 3 }, 
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
		
		@SuppressWarnings("deprecation")
		Block block = p.getTargetBlock(null, 100);
		Location bl = block.getRelative(BlockFace.UP).getLocation();
		
		Effects ef = Effects.valueOf(eff.toUpperCase());
		
		EffectProcess ep =  new EffectProcess(ef, data.duration, OwnerType.LOCATATION, bl, radius);
		lq.effectManager.addPendingProcess(ep);
		String msg = ((String) data.vars.get("message"));
		p.sendMessage(msg);

		return CommandResult.SUCCESS;
	}
}
