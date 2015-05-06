package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.events.SpeedCheckEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@SkillManifest(
	name = "Boost", type = SkillType.ACTIVE, author = "SableDnah", version = 2.0D, 
	description = "Adjust your speed for [duration]s", 
	consumes = "", manaCost = 5, levelRequired = 0, skillPoints = 0, 
	buildup = 0, delay = 0, duration = 5000, cooldown = 10000, 
	dblvarnames = { "speed" }, dblvarvalues = { 0.2 }, 
	intvarnames = {	}, intvarvalues = { }, 
	strvarnames = { "message" }, strvarvalues = { "Boost Activated" }
)
public class Boost extends Skill implements Listener {

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
	
	
	@EventHandler (priority = EventPriority.NORMAL)
	public void speedCheck(SpeedCheckEvent event){

		if (!validSkillUser(event.getPC())) {
			return;
		}

		Player p = event.getPC().getPlayer();
		if (p == null) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(event.getPC());
		SkillPhase phase = data.checkPhase();
		
		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {
			double startspeed = event.getSpeed();
			Double speed = ((Double) data.vars.get("speed"));
//System.out.print("Spped mod: " + startspeed);
			startspeed += speed;
//System.out.print("Spped mod: " + startspeed);
			event.setSpeed(startspeed);
		}		
	}

}

