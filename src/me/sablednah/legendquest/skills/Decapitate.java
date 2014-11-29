package me.sablednah.legendquest.skills;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.inventory.ItemStack;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.meta.SkullMeta;


@SkillManifest(
		name = "Decapitate", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D,
		description = "Drop Skulls when killing...", 
		consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
		buildup = 0, delay = 0, duration = 30000, cooldown = 60000, 
		dblvarnames = { "mobchance", "playerchance" }, dblvarvalues = { 50.0, 100.0 }, 
		intvarnames = { }, intvarvalues = { }, 
		strvarnames = { "message" }, strvarvalues = { "Off with his head!" }
	)

public class Decapitate extends Skill implements Listener {

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
		if (data.type==SkillType.PASSIVE) { // does not require command
			return CommandResult.NOTAVAILABLE;
		}
		
		String message = ((String) data.vars.get("message"));
		p.sendMessage(message);

		return CommandResult.SUCCESS;
	}

	@EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntityType().equals(EntityType.SKELETON) || e.getEntityType().equals(EntityType.WITHER) || e.getEntityType().equals(EntityType.ZOMBIE) || e.getEntityType().equals(EntityType.CREEPER)) {
            if (e.getEntity().getKiller() == null || e.getEntity().getKiller().getType() != EntityType.PLAYER) { return; }
            Player killer = e.getEntity().getKiller();
            if (killer != null) {
	    		// load skill options
            	if (!validSkillUser(killer)) {
        			return;
        		}
	    		SkillDataStore data = this.getPlayerSkillData(killer);
	    		SkillPhase phase = data.checkPhase();
	    		
	    		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {            
                    // Cancel the skull drop
                    for (ItemStack entry : e.getDrops()) {
                        if (entry.getType() == Material.SKULL || entry.getType() == Material.SKULL_ITEM) {
                            entry.setAmount(0);
                        }
                    }
                    double chance = ((Double) data.vars.get("mobchance")) / 100.0D;
					if (Math.random() <= chance) {
						e.getDrops().add(getSkullItemStack(e.getEntity()));
					}
	            }
    		}
        }
    }

	@EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        if (e.getEntity().getKiller() == null || e.getEntity().getKiller().getType() != EntityType.PLAYER) { return; }
        Player killer = e.getEntity().getKiller();
        
        if (killer != null) {

    		// load skill options
    		SkillDataStore data = this.getPlayerSkillData(killer);
    		if (data == null) { return; }
    		SkillPhase phase = data.checkPhase();
    		
    		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {
                // Cancel the skull drop
                for (ItemStack entry : e.getDrops()) {
                    if (entry.getType() == Material.SKULL || entry.getType() == Material.SKULL_ITEM) {
                        entry.setAmount(0);
                    }
    		    }
                // Should a skull be droped?
                double chance = ((Double) data.vars.get("playerchance")) / 100.0D;
				if (Math.random() <= chance) {
	        	    Player p = e.getEntity();
	        	    ItemStack skull = new ItemStack(Material.SKULL_ITEM);
	        	    skull.setDurability((short) 3);
	        	    SkullMeta meta = (SkullMeta) skull.getItemMeta();
	        	    meta.setDisplayName(ChatColor.RED + p.getName());
	        	    meta.setOwner(p.getName());
	        	    skull.setItemMeta(meta);
	        	    e.getDrops().add(skull);
				}
            }
		}
    }

    public static ItemStack getSkullItemStack(Entity e) {
        // Convert the entity type into a skull data value
        switch (e.getType()) {
            case SKELETON:
                switch (((Skeleton) e).getSkeletonType()) {
                    case NORMAL:
                        return getSkullItemStack((byte) 0);
                    case WITHER:
                        return getSkullItemStack((byte) 1);
                }
            case ZOMBIE:
                return getSkullItemStack((byte) 2);
            case PLAYER:
                return getSkullItemStack((byte) 3);
            case CREEPER:
                return getSkullItemStack((byte) 4);
            default:
                return null;
        }
    }
    
    public static ItemStack getSkullItemStack(byte data) {
	    ItemStack skull = new ItemStack(Material.SKULL_ITEM);
	    skull.setDurability((short) data);
	    return skull;
    }
     	
}

