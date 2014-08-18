package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.utils.Utils;
import me.sablednah.legendquest.utils.plugins.PluginUtils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@SkillManifest(name = "Hurt", type = SkillType.ACTIVE, author = "SableDnah", version = 2.0D, 
description = "Inflict [damage] Damage on target...", 
consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
dblvarnames = { "explodepower", "damage" }, dblvarvalues = { 4.0,5.0 }, 
intvarnames = {	"distance", "explode", "bypassmagicarmour", "explodeblocks", "explodefire", "teleport", "effectsduration", "radius" }, intvarvalues = { 10, 1, 0, 1, 1, 1, 600000, 0 }, 
strvarnames = { "effects", "material" }, strvarvalues = { "SLOWBLEED", "WEB" }
)
public class Hurt extends Skill implements Listener {

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
		Integer r = ((Integer) data.vars.get("radius"));
		Integer effectsduration = ((Integer) data.vars.get("effectsduration"));
		Double damage = ((Double) data.vars.get("damage"));
		Integer explode = ((Integer) data.vars.get("explode"));
		Double explodepower = ((Double) data.vars.get("explodepower"));
		Integer explodeblocks = ((Integer) data.vars.get("explodeblocks"));
		Integer explodefire = ((Integer) data.vars.get("explodefire"));
		Integer bypassmagicarmour = ((Integer) data.vars.get("bypassmagicarmour"));
		Integer teleport = ((Integer) data.vars.get("teleport"));
		String effects = ((String) data.vars.get("effects"));
		String m = ((String) data.vars.get("material"));

		
		// Get target
		LivingEntity target = Utils.getTarget(p, distance);
		if (target == null) {
			p.sendMessage("Sorry, you need to hold look at a target...");
			return CommandResult.FAIL;
		}

		if (!PluginUtils.canBuild(target.getLocation(), p)) {
			p.sendMessage("Target is in safe location...");
			return CommandResult.FAIL;
		}

		// ok so you have looked at a valid target
		
		if (bypassmagicarmour>0) {
			//get magic armour value andd add to damage to negate.
		}
		
		target.damage(damage, p);
		
		if (r>0) {
			for (Entity e: target.getNearbyEntities(r,r,r)) {
				if (e instanceof Damageable) {
					((Damageable) e).damage(damage, p);
				}
			}
		}
		
		if (explode > 0) {
			target.getWorld().createExplosion(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), explodepower.floatValue(), (explodefire>0), (explodeblocks>0));
		}

		if (effects != null && !effects.isEmpty()) {
			int duration = effectsduration;

			String[] list = effects.split("\\s*,\\s*");
			for (String s : list) {
				Effects ef = Effects.valueOf(s.toUpperCase());
				EffectProcess ep = null;
				if ((target instanceof Player)) {
					Player p2 = (Player) target;
					ep = new EffectProcess(ef, duration, OwnerType.PLAYER, p2.getUniqueId());
				} else {
					ep = new EffectProcess(ef, duration, OwnerType.MOB, target.getUniqueId());
				}
				lq.effectManager.addPendingProcess(ep);
			}
		}
		
		if (m != null && !m.isEmpty()) {
			Material mat = Material.matchMaterial(m);
			if (mat != null) {
				Block b = target.getLocation().getBlock();
				if (b != null && b.getType()==Material.AIR) {
					if (PluginUtils.canBuild(b, p)) {
						b.setType(mat);
					}
				}
				b = target.getLocation().getBlock().getRelative(BlockFace.UP);
				if (b != null && b.getType()==Material.AIR) {
					if (PluginUtils.canBuild(b, p)) {
						b.setType(mat);
					}
				}
			}
		}
		if (teleport != null && teleport >0) {
				p.teleport(target.getLocation());
		}

		
//		boolean test = Mechanics.opposedTest(getPC(p), Difficulty.TOUGH, Attribute.DEX, getPC(target), Difficulty.EASY, Attribute.WIS);

		return CommandResult.SUCCESS;
	}
}
