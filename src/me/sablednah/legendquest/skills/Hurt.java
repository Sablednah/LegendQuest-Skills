package me.sablednah.legendquest.skills;

import java.util.List;

import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.Utils;
import me.sablednah.legendquest.utils.plugins.PluginUtils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@SkillManifest(name = "Hurt", type = SkillType.ACTIVE, author = "SableDnah", version = 2.0D, 
description = "Inflict [damage] Damage on target...", 
consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
dblvarnames = { "explodepower", "damage", "heal" }, dblvarvalues = { 4.0, 5.0 , 5.0}, 
intvarnames = {	"distance", "explode", "bypassmagicarmour", "explodeblocks", "explodefire", "teleport", "effectsduration", "radius", "lightning", "undeadonly", "materialduration"}, 
intvarvalues = { 10, 1, 0, 1, 1, 1, 600000, 0, 0, 0, 0 }, 
strvarnames = { "effects", "material" }, strvarvalues = { "SLOWBLEED", "WEB" }
)
public class Hurt extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */ }

	public CommandResult onCommand(Player p) { 
		if (!validSkillUser(p)) {
//			p.sendMessage("Not valid user..");
			return CommandResult.FAIL;
		}

//		p.sendMessage("Valid user loading skilldata");
		
		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		
//		p.sendMessage(data.getlastArgs() + " getlastArgs");
//		p.sendMessage(data.getLastUse() + " lastuse");
//		p.sendMessage(data.getLastUseLoc() + " lastuseloc");
//		p.sendMessage(data.getPhase() + " phase");
//		p.sendMessage(data.getTimeLeft() + " TimeLeft");
		
		Integer distance = ((Integer) data.vars.get("distance"));
		Integer r = ((Integer) data.vars.get("radius"));
		Integer effectsduration = ((Integer) data.vars.get("effectsduration"));
		Double damage = ((Double) data.vars.get("damage"));
		Integer explode = ((Integer) data.vars.get("explode"));
		Double explodepower = ((Double) data.vars.get("explodepower"));
		Integer explodeblocks = ((Integer) data.vars.get("explodeblocks"));
		Integer explodefire = ((Integer) data.vars.get("explodefire"));
		Integer bypassmagicarmour = ((Integer) data.vars.get("bypassmagicarmour"));
		Integer teleport = ((Integer) data.vars.get("teleport"));
		Integer lightning = ((Integer) data.vars.get("lightning"));
		Integer undeadonly = ((Integer) data.vars.get("undeadonly"));
		String effects = ((String) data.vars.get("effects"));
		String m = ((String) data.vars.get("material"));
		Double heal = ((Double) data.vars.get("heal"));
		Integer materialduration = ((Integer) data.vars.get("materialduration"));
		
		// Get target
		LivingEntity target = Utils.getTarget(p, distance);
		if (target == null) {
			p.sendMessage("Sorry, you need to look at a target...");
			return CommandResult.FAIL;
		}

		if (!PluginUtils.canBuild(target.getLocation(), p)) {
			p.sendMessage("Target is in safe location...");
			return CommandResult.FAIL;
		}

//		p.sendMessage("damaging: " + target.getCustomName() + " ["+target.getType().toString()+"]");
//		p.sendMessage("r: " + r);
//		p.sendMessage("undeadonly: " + undeadonly);
		

		// ok so you have looked at a valid target
		if (bypassmagicarmour>0) {
			//get magic armour value andd add to damage to negate.
		}
		
		if (r>0) {
			List<Entity> near = target.getNearbyEntities(r,r,r);
			if (!near.contains(target)) { near.add(target); }
			for (Entity e: near) {
				if (!e.equals(p)) {
					if (e instanceof LivingEntity) {
//						p.sendMessage("damaging: " + ((LivingEntity)e).getCustomName() + " ["+e.getType().toString()+"]");
						if (undeadonly == 0 || (undeadonly >0 && (target.getType() == EntityType.ZOMBIE || target.getType() == EntityType.PIG_ZOMBIE || target.getType() == EntityType.GIANT || target.getType() == EntityType.SKELETON ) ) ) {
							doStuff((LivingEntity) e, damage, p, lightning, m, effects, effectsduration, heal, materialduration);
						}
					}
				}
			}
		} else {
			if (undeadonly == 0 || (undeadonly >0 && (target.getType() == EntityType.ZOMBIE || target.getType() == EntityType.PIG_ZOMBIE || target.getType() == EntityType.GIANT || target.getType() == EntityType.SKELETON ) ) ) {
				doStuff(target, damage, p, lightning, m, effects, effectsduration, heal, materialduration);
			}
		}
		
		if (explode > 0) {
			target.getWorld().createExplosion(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), explodepower.floatValue(), (explodefire>0), (explodeblocks>0));
		}

		if (teleport != null && teleport >0) {
			if (teleport==1) {
				if (p.getLocation().distanceSquared(target.getLocation())>4.0D) {
					BlockFace ytf = yawToFace(p.getLocation().getYaw());
					Location tloc = target.getLocation().add(ytf.getModX(), 0, ytf.getModZ());
					tloc.setYaw(p.getLocation().getYaw());
					tloc.setPitch(p.getLocation().getPitch());
					p.teleport(  tloc  );			
					//System.out.print("Facing: "+ytf.toString());
				}
			} else {
				p.teleport(target.getLocation());
			}
		}

		
//		boolean test = Mechanics.opposedTest(getPC(p), Difficulty.TOUGH, Attribute.DEX, getPC(target), Difficulty.EASY, Attribute.WIS);

		return CommandResult.SUCCESS;
	}
	public void doStuff(LivingEntity target, double damage, Player p, int lightning, String m, String effects, int duration, double heal, int materialduration) {
		target.damage(damage, p);
		PC pc =  getPC(p);
		pc.heal(heal, target);
		if (lightning>0) { target.getWorld().strikeLightningEffect(target.getLocation()); }
		if (effects != null && !effects.isEmpty()) {
			String[] list = effects.split("\\s*,\\s*");
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
		
		if (m != null && !m.isEmpty()) {
			Material mat = Material.matchMaterial(m);
			if (mat != null) {
				Block b = target.getLocation().getBlock();
				if (b != null && b.getType()==Material.AIR) {
					if (PluginUtils.canBuild(b, p)) {
						if (materialduration>0) {
							lq.getServer().getScheduler().runTaskLater(lq, new ReplaceMaterial(b.getLocation(),b.getType(),mat), materialduration);
						}
						b.setType(mat);
					}
				}
				b = target.getLocation().getBlock().getRelative(BlockFace.UP);
				if (b != null && b.getType()==Material.AIR) {
					if (PluginUtils.canBuild(b, p)) {
						if (materialduration>0) {
							lq.getServer().getScheduler().runTaskLater(lq, new ReplaceMaterial(b.getLocation(),b.getType(),mat), materialduration);
						}
						b.setType(mat);
					}
				}
			}
		}
	}
	
	public static final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	public static final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
	   
	/**
	* Gets the horizontal Block Face from a given yaw angle<br>
	* This includes the NORTH_WEST faces
	*
	* @param yaw angle
	* @return The Block Face of the angle
	*/
	public static BlockFace yawToFace(float yaw) {
	    return yawToFace(yaw, true);
	}
	 
	    /**
	* Gets the horizontal Block Face from a given yaw angle
	*
	* @param yaw angle
	* @param useSubCardinalDirections setting, True to allow NORTH_WEST to be returned
	* @return The Block Face of the angle
	*/
	public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
	    if (useSubCardinalDirections) {
	        return radial[Math.round(yaw / 45f) & 0x7];
	    } else {
	        return axis[Math.round(yaw / 90f) & 0x3];
	    }
	}

	public class ReplaceMaterial implements Runnable {
		public Location l;
		public Material m;
		public Material temp;
		public ReplaceMaterial(Location l, Material m, Material temp){
			this.l=l;
			this.m=m;
			this.temp=temp;
		}
		public void run() {
			if (l.getBlock().getType() == temp) { // only swap if correct material
				l.getBlock().setType(m);
			}
		}
	}

}
