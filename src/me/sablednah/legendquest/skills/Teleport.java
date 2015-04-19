package me.sablednah.legendquest.skills;

import java.util.HashSet;
import me.sablednah.legendquest.utils.plugins.PluginUtils;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@SkillManifest(name = "teleport", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
description = "Teleport upto [maxrange]", 
consumes = "ENDER_PEARL", manaCost = 5, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
dblvarnames = {}, dblvarvalues = {}, 
intvarnames = { "maxrange", "signal", "sound" }, intvarvalues = { 16, 0, 0 }, 
strvarnames = {}, strvarvalues = {}
)
public class Teleport extends Skill {

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
		Integer maxrange = ((Integer) data.vars.get("maxrange"));
		Integer signal = ((Integer) data.vars.get("signal"));
		Integer sound = ((Integer) data.vars.get("sound"));

		@SuppressWarnings("deprecation")
		Block block = p.getTargetBlock((HashSet<Byte>) null, maxrange);
		Location bl = block.getLocation();
		// slightly randomise position so they don't do that weird stacking effect!
		Location bl2 = bl.clone();
		double xmod = (Math.random() - 0.5D);
		double zmod = (Math.random() - 0.5D);
		bl2.setX(bl2.getX() + xmod);
		bl2.setZ(bl2.getZ() + zmod);
		bl2.setY(bl2.getY() + 1.1);
		
		if (!(PluginUtils.canBuild(bl2, p))) {
			p.sendMessage("Can't teleport here...");
			return CommandResult.FAIL;
		}
		if (signal != 0) {
			p.getLocation().getWorld().playEffect(p.getLocation(),Effect.ENDER_SIGNAL,0);
			bl2.getWorld().playEffect(bl2,Effect.ENDER_SIGNAL,0);
		}
		if (sound != 0) {
			p.getLocation().getWorld().playSound(p.getLocation(),Sound.ENDERMAN_TELEPORT,10.0F,1.0F);
			bl2.getWorld().playSound(bl2,Sound.ENDERMAN_TELEPORT,10.0F,1.0F);
		}
		p.teleport(bl2);

		return CommandResult.SUCCESS;
	}
}
