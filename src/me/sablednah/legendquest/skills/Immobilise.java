package me.sablednah.legendquest.skills;

import java.util.UUID;

import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.Utils;
import me.sablednah.legendquest.utils.plugins.PluginUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@SkillManifest(name = "Immobilise", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
description = "Root Target to the spot for [duration]", 
consumes = "", manaCost = 5, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 5000, cooldown = 10000, 
dblvarnames = { "speed" }, dblvarvalues = { 0.0 }, 
intvarnames = {	"distance" }, intvarvalues = { 10 }, 
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
		Player target = Utils.getTargetPlayer(p, distance);
		if (target == null) {
			p.sendMessage("Sorry, you need to hold look at a player...");
			return CommandResult.FAIL;
		}

		if (!PluginUtils.canBuild(target.getLocation(), p)) {
			p.sendMessage("Target is in safe location...");
			return CommandResult.FAIL;
		}

		// ok so you have looked at a player and are picking their pocket.
		
		target.setWalkSpeed((float)speed.doubleValue());
		
		Bukkit.getServer().getScheduler().runTaskLater(lq, new ReSpeed(target.getUniqueId()), (long)(data.duration/50));
		
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
