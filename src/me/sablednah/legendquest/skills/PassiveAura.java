package me.sablednah.legendquest.skills;


import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.events.SkillTick;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SkillManifest(name = "PassiveAura", type = SkillType.PASSIVE, author = "SableDnah", version = 1.0D, description = "Apply constant [effect] to self", 
consumes = "", manaCost = 0, 
levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 0, 
dblvarnames = {}, dblvarvalues = {}, 
intvarnames = {}, intvarvalues = { }, 
strvarnames = { "effect" }, strvarvalues = { "REGENERATION" }
)
public class PassiveAura extends Skill implements Listener {

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
		String eff = ((String) data.vars.get("effect"));

		long duration = (lq.configMain.skillTickInterval * 50)+50; // convert ticks to duration
		if (duration<2000) { duration = 2000; }

		try {
			Effects ef = Effects.valueOf(eff.toUpperCase());  
			
			EffectProcess ep =  new EffectProcess(ef, System.currentTimeMillis(), duration, OwnerType.PLAYER, p.getUniqueId());
//			System.out.print(ep.toString());
			lq.effectManager.addPendingProcess(ep);

		} catch (IllegalArgumentException exp) {
			lq.debug.warning("'"+eff + "' is not a valid effects name for skill '"+data.name+"'");
			return;
		}
	}
}
