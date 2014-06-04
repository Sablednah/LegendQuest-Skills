package me.sablednah.legendquest.skills;


import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@SkillManifest(name = "Summon", type = SkillType.ACTIVE, author = "SableDnah", version = 2.0D, description = "Summon Entities", 
consumes = "", manaCost = 5, 
levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 100000, 
dblvarnames = { "maxhealth"}, dblvarvalues = { 0.0D }, 
intvarnames = { "qty" }, intvarvalues = { 1 }, 
strvarnames = { "entity" }, strvarvalues = { "pig" }
)
public class Summon extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command
		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		String mobName = ((String) data.vars.get("entity"));
		Integer qty = ((Integer) data.vars.get("qty"));
		Double maxhealth = ((Double) data.vars.get("maxhealth"));		
		try {
			@SuppressWarnings("deprecation")
			EntityType type = EntityType.fromName(mobName);
			@SuppressWarnings("deprecation")
			Block block = p.getTargetBlock(null, 100);
			Location bl = block.getLocation();
			for(int i=1; i<=qty; i++){
				//slightly randomise position so they don't do that weird stacking effect!
				Location bl2 = bl.clone();
				double xmod = (Math.random()-0.5D)*(i-1);
				double zmod = (Math.random()-0.5D)*(i-1);
				bl2.setX(bl2.getX()+xmod);
				bl2.setZ(bl2.getZ()+zmod);
				bl2.setY(bl2.getY()+1.1);
				Entity l = p.getWorld().spawnEntity(bl2, type);
				if (l instanceof Damageable) {
					if(maxhealth>0.0D) {
						((Damageable)l).setMaxHealth(maxhealth);
						((Damageable)l).setHealth(maxhealth);
					}
				}
			}
		} catch (IllegalArgumentException exp) {
			lq.debug.warning("'"+mobName + "' is not a valid entity name for skill 'Summon'");
			return CommandResult.FAIL;
		}
		return CommandResult.SUCCESS;
	}

/*
 	@EventHandler
	public void RightClick(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Pig) {
			if (validSkillUser(event.getPlayer())) {
				event.getPlayer().sendMessage("VALID :) you right-clicked on a piggy! - " + event.hashCode());
			} else {
				event.getPlayer().sendMessage("notvalid :( you right-clicked on a piggy! - " + event.hashCode());
			}
		}
	}
*/
}
