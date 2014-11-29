package me.sablednah.legendquest.skills;

import java.util.ArrayList;

import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@SkillManifest(name = "Hex", type = SkillType.TRIGGERED, author = "SableDnah", version = 1.0D, 
description = "Attacks have [chance] percent of applying [effect] to target.", 
consumes = "", manaCost = 0, 
levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 5000, cooldown = 0, 
dblvarnames = { "chance" }, dblvarvalues = { 50.0 }, 
intvarnames = {}, intvarvalues = {}, 
strvarnames = {"effect", "weapons", "message" }, strvarvalues = { "WITHER", "", "Target Withered..." })
public class Hex extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command
		System.out.print("Hex skill is not a command!");
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

				ArrayList<Material> weapons = new ArrayList<Material>();
				String w = ((String) data.vars.get("weapons"));
				if (w == null) {
					w = "";
					weapons.add(null);
					weapons.add(Material.AIR);
//					System.out.print("adding weapon: hand");
				} else {
					if (w.isEmpty()) {
						weapons.add(null);
						weapons.add(Material.AIR);
//						System.out.print("adding weapon: hand");
					}
				}

				String[] list = w.split("\\s*,\\s*");
				for (String s : list) {
					if (s.equalsIgnoreCase("hand") || s.equalsIgnoreCase("hands")) {
//						System.out.print("adding weapon: hand");
						weapons.add(null);
						weapons.add(Material.AIR);
					} else {
						Material mat = Material.matchMaterial(s);
						weapons.add(mat);
//						System.out.print("adding weapon: "+mat);
					}
				}

//				System.out.print("looking for weapon: "+p.getItemInHand().getType());
				
				if ((weapons.contains(p.getItemInHand().getType()))) {

//						System.out.print(data.name+": Weapons list contains current item");

						String eff = ((String) data.vars.get("effect"));
						
					Effects ef = null;
					try {
						ef = Effects.valueOf(eff.toUpperCase());
					} catch (IllegalArgumentException exp) {
						lq.debug.warning("'"+ef + "' is not a valid effects name for skill '"+data.name+"'");
						return;
					}

						EffectProcess ep = null;
						if ((event.getEntity() instanceof Player)) {
							Player p2 = (Player) event.getEntity();
							ep = new EffectProcess(ef, data.duration, OwnerType.PLAYER, p2.getUniqueId());
						} else {
							ep = new EffectProcess(ef, data.duration, OwnerType.MOB, event.getEntity().getUniqueId());
						}
						lq.effectManager.addPendingProcess(ep);
						String msg = ((String) data.vars.get("message"));
						p.sendMessage(msg);
					}
				//}
			}
		}
	}
}
