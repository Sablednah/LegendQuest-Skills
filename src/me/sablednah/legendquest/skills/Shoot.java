package me.sablednah.legendquest.skills;

import java.util.List;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.util.Vector;

@SkillManifest(name = "Shoot", type = SkillType.ACTIVE, author = "SableDnah", version = 2.0D, 
description = "Fire a [projectile]", 
consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
dblvarnames = { "damage" }, dblvarvalues = { 5.0 }, 
intvarnames = { "power", "fire", "qty" }, intvarvalues = { 1, 1, 1 }, 
strvarnames = { "projectile" }, strvarvalues = { "FIREBALL" }
)
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

		Integer qty = ((Integer) data.vars.get("qty"));

		for (int i = 0; i < qty; i++) {
			shoot(p, data, i);
		}
		
		return CommandResult.SUCCESS;
	}
	
	public void shoot(Player p, SkillDataStore data, int i) {

		String projectile = ((String) data.vars.get("projectile"));
		Integer power = ((Integer) data.vars.get("power"));
		Integer fire = ((Integer) data.vars.get("fire"));
		Double damage = ((Double) data.vars.get("damage"));

		Projectile ammo;
		if (projectile.equalsIgnoreCase("fireball")) {
			ammo = p.launchProjectile(Fireball.class);
		} else if (projectile.equalsIgnoreCase("smallfireball")) {
			ammo = p.launchProjectile(SmallFireball.class);
		} else if (projectile.equalsIgnoreCase("largefireball")) {
			ammo = p.launchProjectile(LargeFireball.class);
		} else if (projectile.equalsIgnoreCase("snowball")) {
			ammo = p.launchProjectile(Snowball.class);
		} else if (projectile.equalsIgnoreCase("egg")) {
			ammo = p.launchProjectile(Egg.class);
		} else if (projectile.equalsIgnoreCase("witherskull")) {
			ammo = p.launchProjectile(WitherSkull.class);			
		} else if (projectile.equalsIgnoreCase("enderpearl")) {
			ammo = p.launchProjectile(EnderPearl.class);			
		} else { // if (projectile.equalsIgnoreCase("arrow")) {
			ammo = p.launchProjectile(Arrow.class);
			((Arrow) ammo).setKnockbackStrength(power);
		}

		if (ammo instanceof Explosive) {
			((Explosive) ammo).setYield(power);
			if (fire > 0) {
				((Explosive) ammo).setIsIncendiary(true);
			} else {
				((Explosive) ammo).setIsIncendiary(false);
			}
		} else {
			if (fire > 0) {
				ammo.setFireTicks(1200);
			} else {
				ammo.setFireTicks(0);
			}
		}
		
		ammo.setMetadata("damage", new FixedMetadataValue(lq, damage));
		ammo.setMetadata("skillname", new FixedMetadataValue(lq, getName()));
		
		if (i>0) {  // spread shots a little
			Vector v = ammo.getVelocity();
			double x, y, z;
			x = v.getX();
			y = v.getY();
			z = v.getZ();
			// System.out.print(x+" - "+y+" - "+z);
			x = x + ((Math.random() - 0.5D) / 3);
			y = y + ((Math.random() - 0.5D) / 5);
			z = z + ((Math.random() - 0.5D) / 3);
			// System.out.print(x+" - "+y+" - "+z);
			v.setX(x);
			v.setY(y);
			v.setZ(z);
			ammo.setVelocity(v);

			
		}

	}

	@EventHandler
	public void impact(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Projectile) {
			Double dmg = getMetaDamage(event.getDamager());
			String name = getMetaSkillname(event.getDamager());
			if (name.equalsIgnoreCase(getName())) {
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
