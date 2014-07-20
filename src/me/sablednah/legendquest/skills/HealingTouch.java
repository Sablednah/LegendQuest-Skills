package me.sablednah.legendquest.skills;

import java.util.ArrayList;

import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.plugins.PluginUtils;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

@SkillManifest(name = "HealingTouch", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, description = "Heal touched target...", consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, buildup = 0, delay = 0, duration = 0, cooldown = 10000, dblvarnames = {
		"heal", "chance" }, dblvarvalues = { 10.0, 100.0 }, intvarnames = { "effectsduration" }, intvarvalues = { 10000 }, strvarnames = { "removeeffects", "holding", "effects" }, strvarvalues = { "SLOWBLEED,BLEED,POISON", "MAGMA_CREAM", "REGENERATE" })
public class HealingTouch extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) {
		return CommandResult.NOTAVAILABLE;
	}

	@EventHandler
	public void doHeal(PlayerInteractEntityEvent event) {
		// System.out.print("WeaponMaster skill: processing...");
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		Player p = (Player) event.getPlayer();
		if (!validSkillUser(p)) {
			return;
		}
		// System.out.print("WeaponMaster skill: valid player..." + p.getUniqueId()+toString());

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);

		PC pc = getPC(p);

		boolean canpay = true;

		if (data.manaCost > 0) {
			if (pc.mana < data.manaCost) {
				canpay = false;
			}
		}

		if (canpay) {
			if (data.consumes != null) {
				canpay = pc.payItem(data.consumes);
			}
		}

		if (!canpay) {
			return;
		}

		String effects = ((String) data.vars.get("effects"));
		Double heal = ((Double) data.vars.get("heal"));
		String removeeffects = ((String) data.vars.get("removeeffects"));
		Integer effectsduration = ((Integer) data.vars.get("effectsduration"));

		// Get target
		Entity targ = event.getRightClicked();
		if (!(targ instanceof LivingEntity)) {
			return;
		}

		LivingEntity target = (LivingEntity) targ;

		if (!PluginUtils.canBuild(target.getLocation(), p)) {
			// p.sendMessage("Target is in safe location...");
			return;
		}

		double chance = ((Double) data.vars.get("chance")) / 100.0D;
		if (Math.random() <= chance) {

			ArrayList<Material> weapons = new ArrayList<Material>();
			String w = ((String) data.vars.get("holding"));
			if (w == null) {
				w = "";
				weapons.add(null);
				weapons.add(Material.AIR);
				// System.out.print("adding weapon: hand");
			} else {
				if (w.isEmpty()) {
					weapons.add(null);
					weapons.add(Material.AIR);
					// System.out.print("adding weapon: hand");
				}
			}

			String[] list = w.split("\\s*,\\s*");
			for (String s : list) {
				if (s.equalsIgnoreCase("hand") || s.equalsIgnoreCase("hands")) {
					// System.out.print("adding weapon: hand");
					weapons.add(null);
					weapons.add(Material.AIR);
				} else {
					Material mat = Material.matchMaterial(s);
					weapons.add(mat);
					// System.out.print("adding weapon: "+mat);
				}
			}

			// System.out.print("looking for weapon: "+p.getItemInHand().getType());

			if ((weapons.contains(p.getItemInHand().getType()))) {

				// ok so you have looked at a valid target

				if (removeeffects != null && !removeeffects.isEmpty()) {
					list = removeeffects.split("\\s*,\\s*");
					for (String s : list) {
						Effects ef = Effects.valueOf(s.toUpperCase());
						if ((target instanceof Player)) {
							Player p2 = (Player) target;
							lq.effectManager.removeEffects(OwnerType.PLAYER, p2.getUniqueId(), ef);
						} else {
							lq.effectManager.removeEffects(OwnerType.MOB, target.getUniqueId(), ef);
						}
					}
				}
				if ((target instanceof Player)) {
					PC pct = getPC((Player) target);
					pct.heal(heal, p);
				} else {
					double h = target.getHealth();
					h = h + heal;
					if (h > target.getMaxHealth()) {
						h = target.getMaxHealth();
					}
					target.setHealth(h);
				}

				if (effects != null && !effects.isEmpty()) {
					int duration = effectsduration; // data.duration;
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
			}
		}

		// boolean test = Mechanics.opposedTest(getPC(p), Difficulty.TOUGH, Attribute.DEX, getPC(target),
		// Difficulty.EASY, Attribute.WIS);
	}
}
