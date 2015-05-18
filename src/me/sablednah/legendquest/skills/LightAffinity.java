package me.sablednah.legendquest.skills;

import java.util.Collection;

import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.events.AbilityCheckEvent;
import me.sablednah.legendquest.events.SkillTick;
import me.sablednah.legendquest.events.SpeedCheckEvent;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

@SkillManifest(
	name = "LightAffinity", type = SkillType.PASSIVE, author = "SableDnah", version = 2.0D, 
	description = "Bonus stats at daytime, damage/penalty in dark", 
	consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
	buildup = 0, delay = 0, duration = 0, cooldown = 0, 
	dblvarnames = { "regenerate", "damage","darkspeed","lightspeed","nightspeed","dayspeed" }, dblvarvalues = { 1.0,1.0,0.0,0.0,0.0,0.0 }, 
	intvarnames = { "minlight", "maxlight", "regeninterval","damageinterval","daybonus","nightpenalty","darkpenalty","lightbonus", "sunonly" }, 
	intvarvalues = { 5, 10, 5, 5, 1, 1, 1, 1, 0}, 
	strvarnames = { "lighteffects", "nighteffects" }, 
	strvarvalues = { "", "" }
)

public class LightAffinity extends Skill implements Listener {
	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command
		return CommandResult.NOTAVAILABLE;
	}
/*
 * /time set <number | day | night>
/time set 0     || Sets the time to dawn.
/time set day   || Sets the time to 1000.
/time set 6000  || Sets the time to midday
/time set 12000 || Sets the time to dusk
/time set night || Sets the time to 14000.
/time set 18000 || Sets the time to midnight
 */

	@EventHandler
	public void skillTick(SkillTick event) {
		Player p = event.getPlayer();
		if (!validSkillUser(p)) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		Integer sun = ((Integer) data.vars.get("sunonly"));
		
		Block b = p.getLocation().getBlock();		
		int light = 0;
		if (sun>0) {
			//light = b.getLightFromSky();
			light = getSunLightForBlock(b);
		} else {
			light = b.getLightLevel();			
		}

		Integer minlight = ((Integer) data.vars.get("minlight"));
		Integer maxlight = ((Integer) data.vars.get("maxlight"));
		Integer regeninterval = ((Integer) data.vars.get("regeninterval"));
		Integer damageinterval = ((Integer) data.vars.get("damageinterval"));
		
		double regenerate = ((Double) data.vars.get("regenerate"));
		double damage = ((Double) data.vars.get("damage"));

		if (!p.isDead()) {
			if (light<minlight) { // dark
				if (damage>0.0D) {
					if ((lq.players.ticks % ((damageinterval*20)/lq.configMain.skillTickInterval)) == 0 ) {
						getPC(p).damage(damage);
					}
				}
				String effects = ((String) data.vars.get("nighteffects"));
				if (effects != null && !effects.isEmpty()) {
					String[] list = effects.split("\\s*,\\s*");
					for (String s : list) {
						Effects ef = Effects.valueOf(s.toUpperCase());
						int dur = (damageinterval * 1000);
						if (dur < 5000) {
							dur = 5000;
						}
						EffectProcess ep = null;
						ep = new EffectProcess(ef, dur, OwnerType.PLAYER, p.getUniqueId());
//System.out.print("Duration: " + dur);
						if (p.hasPotionEffect(ef.getPotioneffectType())) {
//System.out.print("has: " + ef.getPotioneffectType().toString());
							boolean isshorter = false;
							Collection<PotionEffect> pots = p.getActivePotionEffects();
							for (PotionEffect pe : pots) {
								if (pe.getType().equals(ef.getPotioneffectType())) {
									if (pe.getDuration() < lq.configMain.skillTickInterval) {
										isshorter = true;
//System.out.print("effect Duration shorter: " + pe.getDuration() + " < " + lq.configMain.skillTickInterval);
									}
								}
							}
							if (isshorter) {
								lq.effectManager.removeEffects(OwnerType.PLAYER, p.getUniqueId(), ef);
								p.removePotionEffect(ef.getPotioneffectType());
								lq.effectManager.addPendingProcess(ep);
							}
						} else {
//System.out.print("NOT has: " + ef.getPotioneffectType().toString());
							lq.effectManager.addPendingProcess(ep);
						}
					}
				}
			}
			if (light>maxlight) {
				if (regenerate>0.0D) {
					if ((lq.players.ticks % ((regeninterval*20)/lq.configMain.skillTickInterval)) == 0 ) {
						getPC(p).heal(regenerate);
					}
				}
				String effects = ((String) data.vars.get("lighteffects"));
				if (effects != null && !effects.isEmpty()) {
					String[] list = effects.split("\\s*,\\s*");
					for (String s : list) {
						Effects ef = Effects.valueOf(s.toUpperCase());
						int dur = (damageinterval * 1000);
						if (dur < 5000) {
							dur = 5000;
						}
						EffectProcess ep = null;
						ep = new EffectProcess(ef, dur, OwnerType.PLAYER, p.getUniqueId());
//System.out.print("Duration: " + dur);
						if (p.hasPotionEffect(ef.getPotioneffectType())) {
//System.out.print("has: " + ef.getPotioneffectType().toString());
							boolean isshorter = false;
							Collection<PotionEffect> pots = p.getActivePotionEffects();
							for (PotionEffect pe : pots) {
								if (pe.getType().equals(ef.getPotioneffectType())) {
									if (pe.getDuration() < lq.configMain.skillTickInterval) {
										isshorter = true;
//System.out.print("effect Duration shorter: " + pe.getDuration() + " < " + lq.configMain.skillTickInterval);
									}
								}
							}
							if (isshorter) {
								lq.effectManager.removeEffects(OwnerType.PLAYER, p.getUniqueId(), ef);
								p.removePotionEffect(ef.getPotioneffectType());
								lq.effectManager.addPendingProcess(ep);
							}
						} else {
//System.out.print("NOT has: " + ef.getPotioneffectType().toString());
							lq.effectManager.addPendingProcess(ep);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void statcheck(AbilityCheckEvent event){
		if (!validSkillUser(event.getPc())) {
			return;
		}

		Player p = event.getPc().getPlayer();
		if (p == null) { return; }

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(event.getPc());

		Integer sun = ((Integer) data.vars.get("sunonly"));
		Integer minlight = ((Integer) data.vars.get("minlight"));
		Integer maxlight = ((Integer) data.vars.get("maxlight"));

		Block b = p.getLocation().getBlock();		
		int light = 0;
		if (sun>0) {
			//light = b.getLightFromSky();
			light = getSunLightForBlock(b);
		} else {
			light = b.getLightLevel();			
		}

		if (light<minlight) {			
			Integer darkpenalty = ((Integer) data.vars.get("darkpenalty"));
			event.removePenalty(darkpenalty);
		}
		if (light>maxlight) {
			Integer lightbonus = ((Integer) data.vars.get("lightbonus"));
			event.addBonus(lightbonus);
		}
		
		long time = p.getWorld().getTime();
		boolean day = false;
		if (time>999 && time<14000) {
			day = true;
		} else {
			day = false;
		}
		if (p.getWorld().getEnvironment() == Environment.NETHER || p.getWorld().getEnvironment() == Environment.THE_END) {
			day = false;
		}
		
		//"nightbonus","daypenalty","lightpenalty","darkbonus"
		if (day) {
			Integer daybonus = ((Integer) data.vars.get("daybonus"));
			event.addBonus(daybonus);
		} else {
			Integer nightpenalty = ((Integer) data.vars.get("nightpenalty"));
			event.removePenalty(nightpenalty);			
		}
	}

	/**
    * Gets the simulated light level from a world
    * @param world
    *            The world to check for time and storms
    * @return
    *            The light level.
    */
    public int getLightForWorld(World world) {
        int light = 15;
        long time = world.getTime();
        //Time
        if(time >= 12000) {
            int timeLight = (int) ((time - 12000) / 135);
            if(timeLight > 10) {
                timeLight = 10;
            }
            light = 15 - timeLight;
        }
        //Storm conditions
        if(world.hasStorm() && light >= 8) {
            light -= 3;
        }
        return light;
    }
    public int getSunLightForBlock(Block b) {
    	int baselight = getLightForWorld(b.getWorld());
    	int lightfromsky = b.getLightFromSky();
    	int shade = 15-lightfromsky;
    	int light = baselight - shade;
    	if (light<0) { light = 0; }
    	return light;
    }

	@EventHandler (priority = EventPriority.NORMAL)
	public void speedCheck(SpeedCheckEvent event){

		if (!validSkillUser(event.getPC())) {
			return;
		}

		Player p = event.getPC().getPlayer();
		if (p == null) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(event.getPC());
		SkillPhase phase = data.checkPhase();
		
		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {
			
			//,"darkspeed","lightspeed","nightspeed","dayspeed"
			double darkspeed = ((Double) data.vars.get("darkspeed"));
			double lightspeed = ((Double) data.vars.get("lightspeed"));
			double nightspeed = ((Double) data.vars.get("nightspeed"));
			double dayspeed = ((Double) data.vars.get("dayspeed"));

			if ( darkspeed != 0.0D || lightspeed != 0.0D || nightspeed != 0.0D || dayspeed != 0.0D) {

				Integer sun = ((Integer) data.vars.get("sunonly"));
				Integer minlight = ((Integer) data.vars.get("minlight"));
				Integer maxlight = ((Integer) data.vars.get("maxlight"));
	
				Block b = p.getLocation().getBlock();		
				int light = 0;
				if (sun>0) {
					light = getSunLightForBlock(b);
				} else {
					light = b.getLightLevel();			
				}

				double speed = event.getSpeed();
				
				if (light<minlight) {			
					speed -= darkspeed;
				}
				if (light>maxlight) {
					speed +=lightspeed;
				}
				
				long time = p.getWorld().getTime();
				boolean day = false;
				if (time>999 && time<14000) {
					day = true;
				} else {
					day = false;
				}
				if (p.getWorld().getEnvironment() == Environment.NETHER || p.getWorld().getEnvironment() == Environment.THE_END) {
					day = false;
				}
				
				if (day) {
					speed += dayspeed;
				} else {
					speed -= nightspeed;
				}
	
				if (speed<0.05D) {
					speed = 0.05D;
				}
				event.setSpeed(speed);
			}
		}
	}
}