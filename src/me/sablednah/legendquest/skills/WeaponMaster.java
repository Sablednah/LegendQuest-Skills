package me.sablednah.legendquest.skills;

import java.util.ArrayList;

import me.sablednah.legendquest.events.CombatModifiers;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SkillManifest(name = "WeaponMaster", type = SkillType.TRIGGERED, author = "SableDnah", version = 1.0D, 
description = "Attacks while wielding weapon [weapon] inflict extra [damage], [chance] percent of the time", 
consumes = "", manaCost = 0, 
levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 0, 
dblvarnames = { "chance" }, dblvarvalues = { 90.5 }, 
intvarnames = { "damage" }, intvarvalues = { 5 }, 
strvarnames = { "weapons" }, 
strvarvalues = { "WOOD_SWORD,STONE_SWORD,IRON_SWORD,GOLD_SWORD,DIAMOND_SWORD" })
public class WeaponMaster extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command
		System.out.print("WeaponMaster skill: is not a command!");
		return CommandResult.NOTAVAILABLE;
	}

	@EventHandler
	public void doDmg(CombatModifiers event) {
//  System.out.print("WeaponMaster skill: processing...");
		if ((event.getDamager() instanceof Player)) {
			Player p = (Player) event.getDamager();
			if (!validSkillUser(p)) {
				return;
			}
//  System.out.print("WeaponMaster skill: valid player..." + p.getUniqueId()+toString());

			// load skill options
			SkillDataStore data = this.getPlayerSkillData(p);

			ArrayList<Material> weapons = new ArrayList<Material>();
			String w = ((String) data.vars.get("weapons"));
//			System.out.print("WeaponMaster skill: " + w);
			String[] list = w.split("\\s*,\\s*");
			for (String s : list) {
//				System.out.print("WeaponMaster skill: adding - " + s);
				weapons.add(Material.matchMaterial(s));
			}
			if (p.getItemInHand().getType() != null) {
//				System.out.print("WeaponMaster skill: looking for - " + p.getItemInHand().getType());
				if ((weapons.contains(p.getItemInHand().getType())) && (event.getVictim() instanceof LivingEntity)) {
					double chance = ((Double) data.vars.get("chance")) / 100.0D;
					if (Math.random() <= chance) {
						Integer dmg = ((Integer) data.vars.get("damage"));
						event.setPower(event.getPower() + dmg);
					}
				}
			}
		}
	}
}
