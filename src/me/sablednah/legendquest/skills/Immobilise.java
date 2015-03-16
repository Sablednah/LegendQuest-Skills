package me.sablednah.legendquest.skills;

import java.util.List;
import java.util.UUID;

import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.Utils;
import me.sablednah.legendquest.utils.plugins.PluginUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SkillManifest(name = "Immobilise", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
description = "Root Target to the spot for [duration]s", 
consumes = "", manaCost = 5, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 5000, cooldown = 10000, 
dblvarnames = { "speed" }, dblvarvalues = { 0.0 }, 
intvarnames = {	"distance" , "radius" }, intvarvalues = { 10, 5 }, 
strvarnames = { }, strvarvalues = { }
)
public class Immobilise extends Skill {

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

		Integer distance = ((Integer) data.vars.get("distance"));
		Double speed = ((Double) data.vars.get("speed"));

		// Get target
		LivingEntity target = Utils.getTarget(p, distance);
		if (target == null) {
			p.sendMessage("Sorry, you need to hold look at a player...");
			return CommandResult.FAIL;
		}

		if (!PluginUtils.canBuild(target.getLocation(), p)) {
			p.sendMessage("Target is in safe location...");
			return CommandResult.FAIL;
		}

		// ok so you have looked at a player and are picking their pocket.
		
		Integer r = ((Integer) data.vars.get("radius"));

		int mobspeed = 0;
		mobspeed = (int) Math.ceil((1.0D-(speed*5.0D))/0.15D);
		
		if (r>0) {
			List<Entity> entlist = target.getNearbyEntities(r,r,r);
			for (Entity e : entlist) {
				if (e instanceof LivingEntity) {
					if (e.getType() == EntityType.PLAYER) {
						((Player)e).setWalkSpeed((float)speed.doubleValue());
						Bukkit.getServer().getScheduler().runTaskLater(lq, new ReSpeed(e.getUniqueId()), (long)(data.duration/50));			
					} else {
						((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW,(int)(data.duration/50),(mobspeed-1)));
					}
				}
			}
		} else {
			if (target.getType() == EntityType.PLAYER) {
				((Player)target).setWalkSpeed((float)speed.doubleValue());
				Bukkit.getServer().getScheduler().runTaskLater(lq, new ReSpeed(target.getUniqueId()), (long)(data.duration/50));			
			} else {
				target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,(int)(data.duration/50),(mobspeed-1)));
			}
		}
		
		
//		boolean test = Mechanics.opposedTest(getPC(p), Difficulty.TOUGH, Attribute.DEX, getPC(target), Difficulty.EASY, Attribute.WIS);

		return CommandResult.SUCCESS;
	}

	public class ReSpeed implements Runnable {
		UUID uuid;
		public ReSpeed(UUID u) {
			uuid=u;
		}
		public void run() {
			PC pc = getPC(uuid);
			if (pc!=null) {
				lq.getServer().getPlayer(uuid).setWalkSpeed(pc.getSpeed());
			}
		}
	}
}
