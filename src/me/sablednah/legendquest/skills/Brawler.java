package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.events.CombatModifiers;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SkillManifest(
	name = "Brawler", type = SkillType.TRIGGERED, author = "SableDnah", version = 1.0D, 
	description = "Attacks while barehanded have a [chance]% chance to deal +[damage] damage.", 
	consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
	buildup = 0, delay = 0, duration = 0, cooldown = 0, 
	dblvarnames = { "chance" }, dblvarvalues = { 75.5 }, 
	intvarnames = { "damage" }, intvarvalues = { 5 }, 
	strvarnames = { "" }, strvarvalues = { "" }
)

public class Brawler extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command
		System.out.print("Brawler skill: is not a command!");
		return CommandResult.NOTAVAILABLE;
	}

	@EventHandler
	public void doDmg(CombatModifiers event) {
		if ((event.getDamager() instanceof Player)) {
			Player p = (Player) event.getDamager();
			if (!validSkillUser(p)) {
				return;
			}

			// load skill options 
			SkillDataStore data = this.getPlayerSkillData(p);

			if ((p.getItemInHand().getType() == Material.AIR) && (event.getVictim() instanceof LivingEntity)) {
				double chance = ((Double) data.vars.get("chance")) / 100.0D;
				if (Math.random() <= chance) {
					Integer dmg = ((Integer) data.vars.get("damage"));
					event.setPower(event.getPower() + dmg);
				}
				p.sendMessage("Kapow!");
			}
		}
	}
}
