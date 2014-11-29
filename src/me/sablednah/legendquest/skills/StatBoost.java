package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.events.AbilityCheckEvent;
import me.sablednah.legendquest.mechanics.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@SkillManifest(
	name = "StatBoost", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
	description = "Boost stats", 
	consumes = "", manaCost = 10, 
	levelRequired = 0, skillPoints = 0, 
	buildup = 0, delay = 0, duration = 10000, cooldown = 10000, 
	dblvarnames = { }, dblvarvalues = { }, 
	intvarnames = { "str","dex","con","int","wis","chr" }, intvarvalues = { 0,0,0,0,0,0 }, 
	strvarnames = { "message" }, strvarvalues = { "StatBoost initiated..." }
)

public class StatBoost extends Skill implements Listener {
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

	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void statcheck(AbilityCheckEvent event){

		if (!validSkillUser(event.getPc())) {
			return;
		}

		Player p = event.getPc().getPlayer();
		if (p == null) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(event.getPc());
		SkillPhase phase = data.checkPhase();
		
		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {

			Attribute a = event.getAttribute();
			String att = a.toString().toLowerCase();
//System.out.print("Att: "+att);
			Integer mod = ((Integer) data.vars.get(att));
/*
			Integer str = ((Integer) data.vars.get("str"));
			Integer dex = ((Integer) data.vars.get("dex"));
			Integer con = ((Integer) data.vars.get("con"));
			Integer itl = ((Integer) data.vars.get("int"));
			Integer wis = ((Integer) data.vars.get("wis"));
			Integer chr = ((Integer) data.vars.get("chr"));
*/			
			if (mod>0) {
				event.addBonus(mod);
			}
			if (mod<0) {
				event.removePenalty(Math.abs(mod));
			}
		}
	}
}
