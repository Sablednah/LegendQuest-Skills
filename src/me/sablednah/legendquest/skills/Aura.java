package me.sablednah.legendquest.skills;


import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;

import org.bukkit.entity.Player;

@SkillManifest(name = "Aura", type = SkillType.ACTIVE, author = "SableDnah", version = 1.2D, 
description = "Apply [effect] to self for [duration]s", 
consumes = "", manaCost = 5, 
levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 5000, cooldown = 100000, 
dblvarnames = {}, dblvarvalues = {}, 
intvarnames = {}, intvarvalues = { }, 
strvarnames = { "effect","message" }, strvarvalues = { "REGENERATION","Impersonating a TimeLord..." }
)
public class Aura extends Skill {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */ }

	public CommandResult onCommand(Player p) { 
//		System.out.print("Using aura skill: "+p.getName());
		if (!validSkillUser(p)) {
//			System.out.print("Aura skill fail: "+p.getName());
			return CommandResult.FAIL;
		}
//		System.out.print("aura skill valid: "+p.getName());

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
//		System.out.print("aura skill valid: "+data.aliasedname + "-"+data.name + "-" + data.getPhase().toString());
		
		String eff = ((String) data.vars.get("effect"));

//		System.out.print("Using aura effect: "+eff);
		
		Effects ef = null;
		try {
			ef = Effects.valueOf(eff.toUpperCase());  
		} catch (IllegalArgumentException exp) {
			lq.debug.warning("'"+eff + "' is not a valid effects name for skill '"+data.name+"'");
			return CommandResult.FAIL;
		}

//		System.out.print("Using aura duration: "+data.duration);
		
		EffectProcess ep =  new EffectProcess(ef, System.currentTimeMillis(), data.duration, OwnerType.PLAYER, p.getUniqueId());
		lq.effectManager.addPendingProcess(ep);
		String msg = ((String) data.vars.get("message"));
		p.sendMessage(msg);

		return CommandResult.SUCCESS;
	}
}
