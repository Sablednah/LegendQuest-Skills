package me.sablednah.legendquest.skills;

import java.util.ArrayList;

import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.events.CombatModifiers;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.plugins.PluginUtils;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SkillManifest(name = "MightyBlow", type = SkillType.TRIGGERED, author = "SableDnah", version = 1.1D, description = "Inflict [damage] enhanced melee Damage on target...", 
consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, buildup = 0, delay = 0, duration = 0, cooldown = 0, 
dblvarnames = {"explodepower", "chance" }, dblvarvalues = { 4.0, 50.0 }, 
		intvarnames = { "damage", "explode", "bypassmagicarmour", "explodeblocks", "explodefire", "effectsduration", "lightning", "undeadonly" }, 
		intvarvalues = { 10,       1,         0,                   1,               1,             600000,            0,           0 }, 
		strvarnames = { "effects", "weapons" }, strvarvalues = { "SLOWBLEED", "" }
)
public class MightyBlow extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) {
		return CommandResult.NOTAVAILABLE;
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

		Integer damage = ((Integer) data.vars.get("damage"));
		Integer effectsduration = ((Integer) data.vars.get("effectsduration"));
		Integer explode = ((Integer) data.vars.get("explode"));
		Double explodepower = ((Double) data.vars.get("explodepower"));
		Integer explodeblocks = ((Integer) data.vars.get("explodeblocks"));
		Integer explodefire = ((Integer) data.vars.get("explodefire"));
		Integer bypassmagicarmour = ((Integer) data.vars.get("bypassmagicarmour"));
		String effects = ((String) data.vars.get("effects"));
		Integer lightning = ((Integer) data.vars.get("lightning"));
		Integer undeadonly = ((Integer) data.vars.get("undeadonly"));

		// Get target
		Entity target = event.getVictim();

		if (undeadonly == 0 || (undeadonly > 0 && (target.getType() == EntityType.ZOMBIE || target.getType() == EntityType.PIG_ZOMBIE || target.getType() == EntityType.GIANT || target.getType() == EntityType.SKELETON))) {

			if (!PluginUtils.canBuild(target.getLocation(), p)) {
				// p.sendMessage("Target is in safe location...");
				return;
			}

			double chance = ((Double) data.vars.get("chance")) / 100.0D;
			if (Math.random() <= chance) {

				boolean any = false;
				ArrayList<Material> weapons = new ArrayList<Material>();
				String w = ((String) data.vars.get("weapons"));
				if (w == null) {
					w = "";
					weapons.add(null);
					weapons.add(Material.AIR);
					any = true;
					// System.out.print("adding weapon: hand");
				} else {
					if (w.isEmpty()) {
						weapons.add(null);
						weapons.add(Material.AIR);
						any = true;
						// System.out.print("adding weapon: hand");
					}
				}

				String[] list = w.split("\\s*,\\s*");
				for (String s : list) {
					if (s.equalsIgnoreCase("hand") || s.equalsIgnoreCase("hands")) {
						// System.out.print("adding weapon: hand");
						weapons.add(null);
						weapons.add(Material.AIR);
					} else if (s.equalsIgnoreCase("any") || s.equalsIgnoreCase("all")) {
						any = true;
					} else {
						Material mat = Material.matchMaterial(s);
						weapons.add(mat);
						// System.out.print("adding weapon: "+mat);
					}
				}

				// System.out.print("looking for weapon: "+p.getItemInHand().getType());

				if (any || (weapons.contains(p.getItemInHand().getType()))) {

					// ok so you have looked at a valid target

					if (bypassmagicarmour > 0) {
						// get magic armour value andd add to damage to negate.
					}

					if (explode > 0) {
						target.getWorld().createExplosion(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), explodepower.floatValue(), (explodefire > 0), (explodeblocks > 0));
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
					if (lightning > 0) {
						target.getWorld().strikeLightningEffect(target.getLocation());
					}
				}
			}
		}
		// boolean test = Mechanics.opposedTest(getPC(p), Difficulty.TOUGH, Attribute.DEX, getPC(target),
		// Difficulty.EASY, Attribute.WIS);
	}
}
