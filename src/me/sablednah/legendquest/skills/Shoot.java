package me.sablednah.legendquest.skills;

import java.util.List;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

@SkillManifest(name = "Shoot", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, description = "Fire a [projectile]", consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, buildup = 0, delay = 0, duration = 0, cooldown = 10000, dblvarnames = { "damage" }, dblvarvalues = { 5.0 }, intvarnames = {
		"power", "fire" }, intvarvalues = { 1, 1 }, strvarnames = { "projectile" }, strvarvalues = { "FIREBALL" })
public class Shoot extends Skill implements Listener {

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

		// System.out.print("data: "+getName()+" | "+data.aliasedname+" | "+data.name + " | " +data.description );
		// System.out.print("vars:" + data.vars.toString());

		String projectile = ((String) data.vars.get("projectile"));
		Integer power = ((Integer) data.vars.get("power"));
		Integer fire = ((Integer) data.vars.get("fire"));
		Double damage = ((Double) data.vars.get("damage"));
		
		Projectile ammo;
		if (projectile.equalsIgnoreCase("fireball")) {
			ammo = p.launchProjectile(Fireball.class);
			((Fireball) ammo).setYield(power);
			if (fire > 0) {
				((Fireball) ammo).setIsIncendiary(true);
			} else {
				((Fireball) ammo).setIsIncendiary(false);
			}
		} else if (projectile.equalsIgnoreCase("snowball")) {
			ammo = p.launchProjectile(Snowball.class);
			if (fire > 0) {
				ammo.setFireTicks(1200);
			}
		} else if (projectile.equalsIgnoreCase("egg")) {
			ammo = p.launchProjectile(Egg.class);
			if (fire > 0) {
				ammo.setFireTicks(1200);
			}
		} else { // if (projectile.equalsIgnoreCase("arrow")) {
			ammo = p.launchProjectile(Arrow.class);
			((Arrow) ammo).setKnockbackStrength(power);
			if (fire > 0) {
				ammo.setFireTicks(1200);
			}
		}

		ammo.setMetadata("damage", new FixedMetadataValue(lq, damage));
		ammo.setMetadata("skillname", new FixedMetadataValue(lq, getName()));

		return CommandResult.SUCCESS;
	}

	@EventHandler
	public void impact(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Projectile) {
			Double dmg = getMetaDamage(event.getDamager());
			String name = getMetaSkillname(event.getDamager());
			if (name.equalsIgnoreCase(getName())){
				double damage = event.getDamage();
				event.setDamage(damage + dmg);
			}
		}
	}

	public Double getMetaDamage(Metadatable object) {
		List<MetadataValue> values = object.getMetadata("damage");
		for (MetadataValue value : values) {
			if (value.getOwningPlugin() == lq) {
				return value.asDouble();
			}
		}
		return 0.0D;
	}

	public String getMetaSkillname(Metadatable object) {
		List<MetadataValue> values = object.getMetadata("skillname");
		for (MetadataValue value : values) {
			if (value.getOwningPlugin() == lq) {
				return value.asString();
			}
		}
		return "";
	}

}
