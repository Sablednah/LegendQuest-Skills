package me.sablednah.legendquest.skills;

import java.util.List;

import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@SkillManifest(
	name = "Diagnose", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
	description = "Diagnose target...", 
	consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, 
	buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
	dblvarnames = { }, dblvarvalues = { }, 
	intvarnames = {	"distance" }, intvarvalues = { 10 }, 
	strvarnames = { }, strvarvalues = { }
)
public class Diagnose extends Skill{

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
		
		// Get target
		LivingEntity target = Utils.getTarget(p, distance);
		if (target == null) {
			p.sendMessage(lq.configLang.skillInvalidTarget);
			return CommandResult.FAIL;
		}

		// ok so you have looked at a valid target
				
		if ((target instanceof Player)) {
			PC pc = getPC((Player)target);
			p.sendMessage(((Player) target).getDisplayName() + " is a "+pc.race.name+" "+pc.mainClass.name+".");
			p.sendMessage(lq.configLang.statXP+": "+((Player) target).getLevel()+" ("+pc.currentXP+")");
			p.sendMessage(lq.configLang.statKarma+": "+pc.karmaName()+" ("+pc.karma+")");
		} else {
			if (target.getCustomName()!= null && !target.getCustomName().isEmpty()) {
				String tname = target.getCustomName();
				tname = cleanName(tname);
				p.sendMessage(tname+" is: "+target.getType().toString());
			} else {
				p.sendMessage("Target is: "+target.getType().toString());
			}
			if (target.hasMetadata("level")) { p.sendMessage("Level: "+target.getMetadata("level").get(0).asInt()); }
		}

		p.sendMessage(lq.configLang.statHealth+": "+target.getHealth()+" / "+target.getMaxHealth());

		List<EffectProcess> effects = lq.effectManager.getActiveProcess();
		for (EffectProcess ep : effects) {
			if (ep.getUuid().equals(target.getUniqueId())){
				p.sendMessage("Effect: "+ep.getEffect().toString());
			}
		}

		return CommandResult.SUCCESS;
	}
	
	public static String cleanName(String name) {
		// System.out.print("cleaning: "+name);

		String searchcode = "&r&f&r";
		searchcode = ChatColor.translateAlternateColorCodes('&', searchcode);

		
		String strf = "F";
		char f = strf.charAt(0);

		if (name == null) {
			return name;
		}
		String newname = name;
		int loc = newname.length();
		int start = 0;
		if (newname.startsWith("§f")) {
			start = 2;
		} else if (newname.length() > 1 && newname.charAt(0) == ChatColor.COLOR_CHAR && newname.toUpperCase().charAt(1) == f) {
			start = 2;
		}
		if (newname.contains(searchcode)) {
			loc = newname.indexOf(searchcode);
		}
		newname = newname.substring(start, loc);
		return newname;
	}
}
