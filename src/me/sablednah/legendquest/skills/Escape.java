package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.events.SpeedCheckEvent;
import me.sablednah.legendquest.experience.SetExp;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@SkillManifest(name = "Escape", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
description = "Restore speed, breaking immobilising skills", 
consumes = "", manaCost = 5, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 30000, cooldown = 60000, 
dblvarnames = { }, dblvarvalues = { }, 
intvarnames = {	}, intvarvalues = { }, 
strvarnames = { "message" }, strvarvalues = { "I want to break free..." }
)
public class Escape extends Skill  implements Listener {

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
		if (data.type==SkillType.PASSIVE) { // does not require command
			return CommandResult.NOTAVAILABLE;
		}
		
		String message = ((String) data.vars.get("message"));
		p.sendMessage(message);

		return CommandResult.SUCCESS;
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void escapeEvent(SpeedCheckEvent event){
		PC pc=event.getPC();
		
		if (!validSkillUser(pc)) {
			return;
		}

		Player p = pc.getPlayer();
		if (p == null) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(pc);
		SkillPhase phase = data.checkPhase();
		
		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {
			int level = SetExp.getLevelOfXpAmount(pc.currentXP);

			double sp=0.0D;
			sp = pc.race.baseSpeed;
			sp += pc.mainClass.speedMod;
			sp += pc.mainClass.levelUp.getTotal("speed", level);

			if (event.getSpeed()<sp) {
				event.setCancelled(true);
			}
		}		
	}

}
