package me.sablednah.legendquest.skills;

import java.util.ArrayList;
import java.util.List;

import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.playercharacters.PC;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

@SkillManifest(
	name = "BuffParty", type = SkillType.ACTIVE, author = "SableDnah", version = 2.0D, 
	description = "Buff Party", 
	consumes = "", manaCost = 10, 
	levelRequired = 0, skillPoints = 0, 
	buildup = 0, delay = 0, duration = 10000, cooldown = 10000, 
	dblvarnames = { "speed" }, dblvarvalues = { 0.5 }, 
	intvarnames = { "heal", "str", "dex", "con", "int", "wis", "chr", "dodge","hit","soak","power" }, intvarvalues = { 10,0,0,0,0,0,0,0,0,0,0 }, 
	strvarnames = { "message", "effects" }, strvarvalues = { "You have been buffed", "" }
)

public class BuffParty extends Skill implements Listener {
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

		Integer heal = ((Integer) data.vars.get("heal"));
		String effects = ((String) data.vars.get("effects"));
		String message = ((String) data.vars.get("message"));
		String[] list = null;
		int duration = data.duration;
		if (effects != null && !effects.isEmpty()) {
			list = effects.split("\\s*,\\s*");
		}

		// Get targets
		List<Player> party = lq.partyManager.getPartyMembers(p);
		if (party == null) {
			party = new ArrayList<Player>();
			party.add(p);
		} 
		if (party.size()<1) {
			party.add(p);
		}
		

		long time = System.currentTimeMillis();
//		System.out.print("Curse time: "+time);
		time +=data.duration;
//		System.out.print("Curse time: "+data.duration);
//		System.out.print("Curse time: "+time);

		for (Player target : party) {
			target.setMetadata("cursetimeout", new FixedMetadataValue(lq, (time) ));
			target.setMetadata("str", new FixedMetadataValue(lq, (Integer) data.vars.get("str")));
			target.setMetadata("dex", new FixedMetadataValue(lq, (Integer) data.vars.get("dex")));
			target.setMetadata("con", new FixedMetadataValue(lq, (Integer) data.vars.get("con")));
			target.setMetadata("int", new FixedMetadataValue(lq, (Integer) data.vars.get("int")));
			target.setMetadata("wis", new FixedMetadataValue(lq, (Integer) data.vars.get("wis")));
			target.setMetadata("chr", new FixedMetadataValue(lq, (Integer) data.vars.get("chr")));
			target.setMetadata("dodge", new FixedMetadataValue(lq, (Integer) data.vars.get("dodge")));
			target.setMetadata("hit", new FixedMetadataValue(lq, (Integer) data.vars.get("hit")));
			target.setMetadata("soak", new FixedMetadataValue(lq, (Integer) data.vars.get("soak")));
			target.setMetadata("power", new FixedMetadataValue(lq, (Integer) data.vars.get("power")));
			target.setMetadata("speed", new FixedMetadataValue(lq, (Double) data.vars.get("speed")));
			
			PC pc = this.getPC(target);
			pc.heal(heal, p);
			
			if (list != null) {
				for (String s : list) {
					Effects ef = Effects.valueOf(s.toUpperCase());
					EffectProcess ep = null;
					ep = new EffectProcess(ef, duration, OwnerType.PLAYER, p.getUniqueId());
					lq.effectManager.addPendingProcess(ep);
				}
			}	
			
			target.sendMessage(message + target.getDisplayName());
		}
		
		return CommandResult.SUCCESS;
	}
	
}
