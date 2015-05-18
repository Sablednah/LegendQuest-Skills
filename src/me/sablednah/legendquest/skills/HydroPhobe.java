package me.sablednah.legendquest.skills;

import java.util.Collection;

import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.events.AbilityCheckEvent;
import me.sablednah.legendquest.events.SkillTick;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

@SkillManifest(
		name = "HydroPhobe", type = SkillType.PASSIVE, author = "SableDnah", version = 2.0D, 
		description = "Damage and stat mods from rain/water", 
		consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
		buildup = 0, delay = 0, duration = 0, cooldown = 0, 
		dblvarnames = { "raindamage", "damage" }, 
		dblvarvalues = { 1.0, 1.0 }, 
		intvarnames = { "raindamageinterval", "damageinterval", "waterstatmod", "rainstatmod" }, 
		intvarvalues = { 5, 5, 1, 1 }, 
		strvarnames = { "watereffects", "raineffects" }, 
		strvarvalues = { "", "" }
	)

public class HydroPhobe extends Skill implements Listener {
	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command
		return CommandResult.NOTAVAILABLE;
	}

	@EventHandler
	public void skillTick(SkillTick event) {
		Player p = event.getPlayer();

		if (!validSkillUser(p)) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);

		Integer damageinterval = ((Integer) data.vars.get("damageinterval"));
		double damage = ((Double) data.vars.get("damage"));

		if (p.getLocation().getBlock().isLiquid()) {
			if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
				if ((lq.players.ticks % ((damageinterval * 20) / lq.configMain.skillTickInterval)) == 0) {
					if (damage > 0) {
						getPC(p).damage(damage);
					} else {
						getPC(p).heal(Math.abs(damage));
					}
				}
				String watereffects = ((String) data.vars.get("watereffects"));
				if (watereffects != null && !watereffects.isEmpty()) {
					String[] list = watereffects.split("\\s*,\\s*");
					for (String s : list) {
						Effects ef = Effects.valueOf(s.toUpperCase());
						int dur = (damageinterval * 1000);
						if (dur < 5000) {
							dur = 5000;
						}

						EffectProcess ep = null;
						ep = new EffectProcess(ef, dur, OwnerType.PLAYER, p.getUniqueId());
//System.out.print("Duration: " + dur);
						if (p.hasPotionEffect(ef.getPotioneffectType())) {
//System.out.print("has: " + ef.getPotioneffectType().toString());
							boolean isshorter = false;
							Collection<PotionEffect> pots = p.getActivePotionEffects();
							for (PotionEffect pe : pots) {
								if (pe.getType().equals(ef.getPotioneffectType())) {
									if (pe.getDuration() < lq.configMain.skillTickInterval) {
										isshorter = true;
//System.out.print("effect Duration shorter: " + pe.getDuration() + " < " + lq.configMain.skillTickInterval);
									}
								}
							}
							if (isshorter) {
								lq.effectManager.removeEffects(OwnerType.PLAYER, p.getUniqueId(), ef);
								p.removePotionEffect(ef.getPotioneffectType());
								lq.effectManager.addPendingProcess(ep);
							}
						} else {
//System.out.print("NOT has: " + ef.getPotioneffectType().toString());
							lq.effectManager.addPendingProcess(ep);
						}
					}
				}

			}
		}

		if (!p.getWorld().hasStorm()) {
			return;
		}

		Integer raindamageinterval = ((Integer) data.vars.get("raindamageinterval"));
		double raindamage = ((Double) data.vars.get("raindamage"));

		if (p.getWorld().getHighestBlockAt(p.getLocation()).getY() <= p.getLocation().getY()) { // outside
			if ((lq.players.ticks % ((raindamageinterval * 20) / lq.configMain.skillTickInterval)) == 0) {
				if (raindamage > 0) {
					getPC(p).damage(raindamage);
				} else {
					getPC(p).heal(Math.abs(raindamage));
				}
				String raineffects = ((String) data.vars.get("raineffects"));
				if (raineffects != null && !raineffects.isEmpty()) {
					String[] list = raineffects.split("\\s*,\\s*");
					for (String s : list) {
						Effects ef = Effects.valueOf(s.toUpperCase());
						EffectProcess ep = null;
						int dur = (raindamageinterval * 1000);
						if (dur < 5000) {
							dur = 5000;
						}
						ep = new EffectProcess(ef, dur, OwnerType.PLAYER, p.getUniqueId());

						if (p.hasPotionEffect(ef.getPotioneffectType())) {
							boolean isshorter = false;
							Collection<PotionEffect> pots = p.getActivePotionEffects();
							for (PotionEffect pe : pots) {
								if (pe.getType().equals(ef.getPotioneffectType())) {
									if (pe.getDuration() < lq.configMain.skillTickInterval) {
										isshorter = true;
									}
								}
							}
							if (isshorter) {
								lq.effectManager.removeEffects(OwnerType.PLAYER, p.getUniqueId(), ef);
								p.removePotionEffect(ef.getPotioneffectType());
								lq.effectManager.addPendingProcess(ep);
							}
						} else {
							lq.effectManager.addPendingProcess(ep);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void statcheck(AbilityCheckEvent event) {
		if (!validSkillUser(event.getPc())) {
			return;
		}

		Player p = event.getPc().getPlayer();
		if (p == null) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(event.getPc());

		Integer waterstatmod = ((Integer) data.vars.get("waterstatmod"));

		if (p.getLocation().getBlock().isLiquid()) {
			if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
				event.removePenalty(waterstatmod);
			}
		}

		if (!p.getWorld().hasStorm()) {
			return;
		}

		Integer rainstatmod = ((Integer) data.vars.get("rainstatmod"));

		if (p.getWorld().getHighestBlockAt(p.getLocation()).getY() <= p.getLocation().getY()) { // outside
			event.removePenalty(rainstatmod);
		}
	}
}
