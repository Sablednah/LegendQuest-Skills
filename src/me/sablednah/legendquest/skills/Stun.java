package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@SkillManifest(name = "Stun", type = SkillType.TRIGGERED, author = "SableDnah", version = 1.0D, 
description = "Attacks have [chance] percent of stunning target for [duration]s.", 
consumes = "", manaCost = 0, 
levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 5000, cooldown = 0, 
dblvarnames = { "chance" }, dblvarvalues = { 50.0 }, 
intvarnames = {}, intvarvalues = {}, 
strvarnames = {}, strvarvalues = {}
)
public class Stun extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command
		System.out.print("Stun skill is not a command!");
		return CommandResult.NOTAVAILABLE;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void doDmg(EntityDamageByEntityEvent event) {
		if ((event.getDamager() instanceof Player)) {
			Player p = (Player) event.getDamager();
			if (!validSkillUser(p)) {
				return;
			}

			// load skill options
			SkillDataStore data = this.getPlayerSkillData(p);			
			
			double chance = ((Double) data.vars.get("chance")) / 100.0D;
			if (Math.random() <= chance) {
				EffectProcess ep = null;
				if ((event.getEntity() instanceof Player)) {
					Player p2 = (Player) event.getEntity();
					ep = new EffectProcess(Effects.STUNNED,  data.duration, OwnerType.PLAYER, p2.getUniqueId());
				} else {
					ep = new EffectProcess(Effects.STUNNED,  data.duration, OwnerType.MOB, event.getEntity().getUniqueId());
				}
				lq.effectManager.addPendingProcess(ep);
				p.sendMessage("Target Stunned...");
			}
		}
	}
}
