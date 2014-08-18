package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.Utils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@SkillManifest(
	name = "Heal", type = SkillType.ACTIVE, author = "SableDnah", version = 1.1D, 
	description = "Heal target for [heal] health, upto [distance] away.", 
	consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, 
	buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
	dblvarnames = { "heal" }, dblvarvalues = { 10.0 }, 
	intvarnames = {	"distance" }, intvarvalues = { 10 }, 
	strvarnames = { "removeeffects" }, strvarvalues = { "SLOWBLEED,BLEED,POISON" }
)
public class Heal extends Skill{

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

		Integer distance = ((Integer) data.vars.get("distance"));
		Double heal = ((Double) data.vars.get("heal"));
		String removeeffects = ((String) data.vars.get("removeeffects"));
		
		// Get target
		LivingEntity target = Utils.getTarget(p, distance);
		if (target == null) {
			p.sendMessage("Sorry, you need to look at a target...");
			return CommandResult.FAIL;
		}

//		if (!PluginUtils.canBuild(target.getLocation(), p)) {
//			p.sendMessage("Target is in safe location...");
//			return CommandResult.FAIL;
///		}

		// ok so you have looked at a valid target
				
		if (removeeffects != null && !removeeffects.isEmpty()) {
			String[] list = removeeffects.split("\\s*,\\s*");
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
			PC pc = getPC((Player)target);
			pc.heal(heal, p);
		} else {
			double h = target.getHealth();
			h = h + heal;
			if (h > target.getMaxHealth()) { h = target.getMaxHealth(); }
			target.setHealth(h);
		}
		
//		boolean test = Mechanics.opposedTest(getPC(p), Difficulty.TOUGH, Attribute.DEX, getPC(target), Difficulty.EASY, Attribute.WIS);

		return CommandResult.SUCCESS;
	}
}
