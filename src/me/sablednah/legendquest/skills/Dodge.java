package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.events.CombatHitCheck;
import me.sablednah.legendquest.events.CombatModifiers;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SkillManifest(name = "Dodge", type = SkillType.TRIGGERED, author = "SableDnah", version = 1.0D, 
description = "Increased dodge ([+dodgemod]) and +[soak] damage prevention for [soakchance]% of damage.", 
consumes = "", manaCost = 0, 
levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 0, 
dblvarnames = { "soakchance" }, dblvarvalues = { 50.0 }, 
intvarnames = { "soak", "dodgemod" }, intvarvalues = { 5, 5 }, 
strvarnames = { "message" }, strvarvalues = { "Dodged!!" })
public class Dodge extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command
		return CommandResult.NOTAVAILABLE;
	}

	@EventHandler
	public void doDmg(CombatModifiers event) {
		if ((event.getVictim() instanceof Player)) {
			Player p = (Player) event.getVictim();
			if (!validSkillUser(p)) {
				return;
			}

			// load skill options
			SkillDataStore data = this.getPlayerSkillData(p);

			String message = (String) data.vars.get("message");
			double chance = ((Double) data.vars.get("soakchance")) / 100.0D;
			if (Math.random() <= chance) {
				if (event.getDamager() instanceof LivingEntity) {
					event.setDodge(event.getDodge() + ((Integer) data.vars.get("soak")));
					if (!message.isEmpty()){p.sendMessage(message);}
				}
			}
		}
	}

	@EventHandler
	public void hitCheck(CombatHitCheck event) {
		if (event.getVictim() instanceof Player) {
			Player p = (Player) event.getVictim();
			if (!validSkillUser(p)) {
				return;
			}

			// load skill options
			SkillDataStore data = this.getPlayerSkillData(p);
			String message = (String) data.vars.get("message");

			event.setDodgeChance(event.getDodgeChance() + ((Integer) data.vars.get("dodgemod")));
			if (!message.isEmpty()){p.sendMessage(message);}
		}
	}
}
