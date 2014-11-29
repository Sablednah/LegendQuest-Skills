package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@SkillManifest(
	name = "LifeLink", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
	description = "Gain Damage inflicted as health", 
	consumes = "", manaCost = 10, 
	levelRequired = 0, skillPoints = 0, 
	buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
	dblvarnames = { "ratio" }, dblvarvalues = { 0.5 }, 
	intvarnames = { }, intvarvalues = { }, 
	strvarnames = { "message" }, strvarvalues = { "Lifelink initiated..." }
)

public class LifeLink extends Skill implements Listener {
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

	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		Player p =getTwistedInstigatorPlayer(event.getDamager()); 		
		if (p==null) { return; }
		
		if (!validSkillUser(p)) {
			return;
		}
		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		SkillPhase phase = data.checkPhase();
		
		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {
			Double ratio = ((Double) data.vars.get("ratio"));
			
			double dmg = event.getDamage();
			dmg = dmg * ratio;
			PC pc = getPC(p);
			pc.heal(dmg, event.getEntity());
		}
	}

	public Player getTwistedInstigatorPlayer(Entity atacker) {
		if (atacker instanceof Projectile) {
			Projectile bullit = (Projectile) atacker;
			if (bullit.getShooter() instanceof Player) {
				return (Player) bullit.getShooter();
			}
		} else if (atacker instanceof Player) {
			return (Player) atacker;
		}
		return null;
	}

}
