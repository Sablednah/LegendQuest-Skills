package me.sablednah.legendquest.skills;

import java.util.ArrayList;
import java.util.List;

import me.sablednah.legendquest.events.SkillTick;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.entity.Entity;

@SkillManifest(name = "Ward", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
description = "Prevent [entities] from approaching near...", 
consumes = "", manaCost = 0, pay = 0, xp = 0, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 30000, cooldown = 60000, 
dblvarnames = {}, dblvarvalues = {}, 
intvarnames = { "range" }, intvarvalues = { 4 }, 
strvarnames = {"entities", "message" }, strvarvalues = { "Zombie,Skeleton", "Ward Active" }
)
public class Ward extends Skill implements Listener {

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

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityDeath(SkillTick e) {
		Player p = e.getPlayer();
		if (!validSkillUser(p)) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		SkillPhase phase = data.checkPhase();

		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {

			Integer range = ((Integer) data.vars.get("range"));
			String entities = ((String) data.vars.get("entities"));
			String[] list = entities.split("\\s*,\\s*");
			ArrayList<EntityType> etypes = new ArrayList<EntityType>();

			for (String s : list) {
				try {
					EntityType et = EntityType.valueOf(s.toUpperCase());
					etypes.add(et);
				} catch (IllegalArgumentException exp) {
					lq.debug.warning("'" + s + "' is not a valid entitytype for skill '" + data.name + "'");
					return;
				}
			}

//			boolean first = true;
			
			List<Entity> entlist = p.getNearbyEntities(range, range, range);
			for (Entity le : entlist) {
				if (etypes.contains(le.getType())) {

					// target loc
					int x1 = (int) Math.floor(le.getLocation().getBlockX());
					int y1 = (int) Math.floor(le.getLocation().getBlockY());
					int z1 = (int) Math.floor(le.getLocation().getBlockZ());

					// player loc
					int x2 = (int) Math.floor(p.getLocation().getBlockX());
					//int y2 = (int) Math.floor(p.getLocation().getBlockY());
					int z2 = (int) Math.floor(p.getLocation().getBlockZ());

					int xDiff = x1 - x2;
					int zDiff = z1 - z2;
					
//		if (first) {System.out.print("xDiff:"+xDiff);}
//		if (first) {System.out.print("zDiff:"+zDiff);}
		

					// double angle = (Math.atan2(xDiff, zDiff));
//					double angle = Math.toDegrees(Math.atan2(xDiff, zDiff));
//					if (angle < 0.0D) {
//						angle += 360.0D;
//					}

//					int magnitude = range + 1;

//					double zOffset = (Math.sin(angle)) * magnitude;
//					double xOffset = (Math.cos(angle)) * magnitude;


//					Vector v = new Vector(xOffset, 0, zOffset);
//					v = v.normalize();
					
					int big = Math.max(Math.abs(xDiff),Math.abs(zDiff));
//		if (first) {System.out.print("big:"+big);}

					double multi = (big / (range+1.0D));
//		if (first) {System.out.print("range:"+range);}			
//		if (first) {System.out.print("multi:"+multi);}
		
//		if (first) {System.out.print("xDiff:"+(xDiff/multi));}
//		if (first) {System.out.print("zDiff:"+(zDiff/multi));}
					
					le.teleport(new Location(le.getWorld(), x2+(xDiff/multi), y1, z2+(zDiff/multi)));
					// le.setVelocity(v.multiply(-1));
					//first = false;
				}
			}
		}
	}
}
