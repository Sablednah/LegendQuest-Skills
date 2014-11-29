package me.sablednah.legendquest.skills;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@SkillManifest(
		name = "Leap", type = SkillType.ACTIVE, 
		author = "SableDnah", version = 1.0D, 
		description = "Leap forwards", 
		consumes = "", manaCost = 5, 
		levelRequired = 0, skillPoints = 5, 
		buildup = 0, delay = 0, duration = 0, cooldown = 60000, 
		dblvarnames = { "scale" }, dblvarvalues = { 1.0D }, 
		intvarnames = { }, intvarvalues = { }, 
		strvarnames = { }, strvarvalues = { }
	)
public class Leap extends Skill {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */ }

	public CommandResult onCommand(Player p) {
		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);

		double scale = ((Double) data.vars.get("scale"));

		float angle = p.getEyeLocation().getPitch();
//	    System.out.print("A: "+angle);

	    double height = 1.0D;
	    if (angle > 0.0F) {
	      angle = -angle;
	    }
	    if (angle < -75.0F) {
		      height = 1.2D;
		    }
//	    System.out.print("A2: "+angle);
//	    System.out.print("J: "+height);

	    float multiplier = (90.0F + angle) / 45.0F;
//	    System.out.print("M: "+multiplier);
	    
	    float m = (float) ((multiplier));
//	    System.out.print("M2: "+multiplier);
	    
	    Vector v = p.getVelocity().setY(height*scale).add(p.getLocation().getDirection().setY(0).normalize().multiply(m));
//	    System.out.print("V: "+v);
	    
	    p.setVelocity(v);
	    // allow more falldamage
	    p.setFallDistance(-16.0F);
		
		return CommandResult.SUCCESS;
	}
}
