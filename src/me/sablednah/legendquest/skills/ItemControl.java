package me.sablednah.legendquest.skills;

import me.sablednah.legendquest.events.CoreSkillCheckEvent;
import me.sablednah.legendquest.events.CoreSkillCheckEvent.CoreSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@SkillManifest(name = "ItemControl", type = SkillType.PASSIVE, author = "SableDnah", version = 1.0D, 
	description = "Control Item", 
	consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
	buildup = 0, delay = 0, duration = 0, cooldown = 0, 
	dblvarnames = {}, dblvarvalues = {}, 
	intvarnames = {}, intvarvalues = {}, 
	strvarnames = {"coreskill", "allowmaterials", "allowentities", "disallowmaterials", "disallowentities", "message" }, 
	strvarvalues = { "CRAFT", "", "", "", "", "ItemControl initiated..." }
)

public class ItemControl extends Skill implements Listener {
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
		if (data.type == SkillType.PASSIVE) { // does not require command
			return CommandResult.NOTAVAILABLE;
		}

		String message = ((String) data.vars.get("message"));
		p.sendMessage(message);

		return CommandResult.SUCCESS;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void coreskillcheck(CoreSkillCheckEvent event) {

//		System.out.print("Itemcontrol event...");

		if (!validSkillUser(event.getPc())) {
			return;
		}

		Player p = event.getPc().getPlayer();
		if (p == null) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(event.getPc());

		String coreskill = ((String) data.vars.get("coreskill"));
//		System.out.print("Item control of '" + coreskill + "' for " + p.getDisplayName());

		CoreSkill cst = CoreSkill.valueOf(coreskill);

		if (cst != event.getCoreSkill()) {
			return;
		}

//		System.out.print("Itemcontrol of '" + coreskill + "' for " + p.getDisplayName() + " - VALID!!! - " + event.getMaterial());

		SkillPhase phase = data.checkPhase();

		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {
			String allowentities = ((String) data.vars.get("allowentities"));
			String allowmaterials = ((String) data.vars.get("allowmaterials"));
			String disallowentities = ((String) data.vars.get("disallowentities"));
			String disallowmaterials = ((String) data.vars.get("disallowmaterials"));
			if (event.getCoreSkill() == CoreSkill.TAME) {
				if (event.getEntitytype() != null) {
					if (allowentities != null && allowentities.length() > 0 && event.getEntitytype().toString().toLowerCase().contains(allowentities.toLowerCase())) {
						// System.out.print("valid entity (["+allowentities+"] contains ["+event.getEntitytype().toString().toLowerCase()+"])");
						event.setValid(true);
					}
					if (disallowentities != null && disallowentities.length() > 0 && event.getEntitytype().toString().toLowerCase().contains(disallowentities.toLowerCase())) {
						// System.out.print("INvalid entity (["+disallowentities+"] contains ["+event.getEntitytype().toString().toLowerCase()+"])");
						event.setValid(false);
					}
				}
			} else {
				if (event.getMaterial() != null) {
					if (allowmaterials != null && allowmaterials.length() > 0 && event.getMaterial().toString().toLowerCase().contains(allowmaterials.toLowerCase())) {
						// System.out.print("valid mat ([" + allowmaterials + "] contained [" + event.getMaterial().toString().toLowerCase() + "])");
						event.setValid(true);
					}
					if (disallowmaterials != null && disallowmaterials.length() > 0 && event.getMaterial().toString().toLowerCase().contains(disallowmaterials.toLowerCase())) {
						// System.out.print("INvalid mat ([" + disallowmaterials + "] contained [" + event.getMaterial().toString().toLowerCase() + "])");
						event.setValid(false);
					}
				}
			}
		}
	}
}
