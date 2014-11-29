package me.sablednah.legendquest.skills;

import java.util.ArrayList;

import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.events.CombatModifiers;
import me.sablednah.legendquest.utils.plugins.PluginUtils;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@SkillManifest( 
name = "Hadouken", type = SkillType.ACTIVE, 
author = "SableDnah", version = 1.0D, 
description = "Inflict [damage] enhanced melee Damage on target when activated...", 
consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, 
buildup = 3000, delay = 0, duration = 10000, cooldown = 60000, 
dblvarnames = { "explodepower", "chance" }, dblvarvalues = { 4.0, 50.0 }, 
intvarnames = { "damage", "explode", "bypassmagicarmour", "explodeblocks", "explodefire", "effectsduration" }, intvarvalues = { 10, 1, 0, 1, 1, 600000 }, 
strvarnames = { "message", "effects", "weapons" }, strvarvalues = { "Hadouken Ready!", "STUNNED,BLEED", "hands" }
)

public class Hadouken extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command
		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		String message = ((String) data.vars.get("message"));
		p.sendMessage(message);

		return CommandResult.SUCCESS;
	}

	@EventHandler
	public void doDmg(CombatModifiers event) {
		// System.out.print("WeaponMaster skill: processing...");
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
		Player p = (Player) event.getDamager();
		if (!validSkillUser(p)) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		SkillPhase phase = data.checkPhase();

		if (phase.equals(SkillPhase.ACTIVE)) {

			Integer damage = ((Integer) data.vars.get("damage"));
			Integer effectsduration = ((Integer) data.vars.get("effectsduration"));
			Integer explode = ((Integer) data.vars.get("explode"));
			Double explodepower = ((Double) data.vars.get("explodepower"));
			Integer explodeblocks = ((Integer) data.vars.get("explodeblocks"));
			Integer explodefire = ((Integer) data.vars.get("explodefire"));
			Integer bypassmagicarmour = ((Integer) data.vars.get("bypassmagicarmour"));
			String effects = ((String) data.vars.get("effects"));

			// Get target
			Entity target = event.getVictim();

			double chance = ((Double) data.vars.get("chance")) / 100.0D;
			if (Math.random() <= chance) {
				ArrayList<Material> weapons = new ArrayList<Material>();
				String w = ((String) data.vars.get("weapons"));
				if (w == null) {
					w = "";
					weapons.add(null);
					weapons.add(Material.AIR);
				} else {
					if (w.isEmpty()) {
						weapons.add(null);
						weapons.add(Material.AIR);
					}
				}

				String[] list = w.split("\\s*,\\s*");
				for (String s : list) {
					if (s.equalsIgnoreCase("hand") || s.equalsIgnoreCase("hands")) {
						weapons.add(null);
						weapons.add(Material.AIR);
					} else {
						Material mat = Material.matchMaterial(s);
						weapons.add(mat);
					}
				}

				if ((weapons.contains(p.getItemInHand().getType()))) {
					if (bypassmagicarmour > 0) {
						// get magic armour value andd add to damage to negate.
					}

					if (PluginUtils.canBuild(target.getLocation(), p)) {
						if (explode > 0) {
							target.getWorld().createExplosion(target.getLocation().getX(), target.getLocation().getY()+2, target.getLocation().getZ(), explodepower.floatValue(), (explodefire > 0), (explodeblocks > 0));
						}
					}

					if (effects != null && !effects.isEmpty()) {
						int duration = effectsduration;
						list = null;
						list = effects.split("\\s*,\\s*");
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
					event.setPower(event.getPower() + damage);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void doSoak(EntityDamageEvent event) {
		if ((event.getEntity() instanceof Player)) {
			Player p = (Player) event.getEntity();
			if (!validSkillUser(p)) {
				return;
			}

			SkillDataStore data = this.getPlayerSkillData(p);
			SkillPhase phase = data.checkPhase();

			if (phase.equals(SkillPhase.ACTIVE)) {

				DamageCause type = event.getCause();
				if (type.equals(DamageCause.ENTITY_EXPLOSION) || type.equals(DamageCause.BLOCK_EXPLOSION)) {
					event.setDamage(0);
					event.setCancelled(true);
				}
			}
		}
	}
}
