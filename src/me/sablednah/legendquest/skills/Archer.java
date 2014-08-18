package me.sablednah.legendquest.skills;

import java.util.List;

import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.utils.plugins.PluginUtils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

@SkillManifest(
	name = "Archer", type = SkillType.ACTIVE, author = "SableDnah", version = 2.0D, 
	description = "Fire a Powerful Arrow", 
	levelRequired = 0, skillPoints = 0, consumes = "", manaCost = 10, 
	buildup = 0, delay = 0, duration = 5000, cooldown = 10000, 
	dblvarnames = { "damage", "velocity" }, dblvarvalues = { 5.0, 1.2 }, 
	intvarnames = { "knockback", "fire", "qty", "bowshot", "explode", "teleport" }, intvarvalues = { 1, 1, 1, 0, 0, 0 }, 
	strvarnames = { "effects", "material" }, strvarvalues = { "POISON,BLEED", "COBWEB" }
)
public class Archer extends Skill implements Listener {
	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */ }

	public CommandResult onCommand(Player p) {
		// Check if Player has the Archer Skill and that its unlocked
		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);

		Integer qty = ((Integer) data.vars.get("qty"));

		for (int i = 0; i < qty; i++) {
			launchArrow(p, null, data);
		}
		return CommandResult.SUCCESS;
	}

	@EventHandler
	public void bowFire(EntityShootBowEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (!validSkillUser(p)) {
				return;
			}
			SkillDataStore data = this.getPlayerSkillData(p);
			// System.out.print(data.vars.toString());
			Integer bowshot = ((Integer) data.vars.get("bowshot"));
			if (bowshot == 1) {
				Arrow ammo = (Arrow) event.getProjectile();
				launchArrow(p, ammo, data);
				Integer qty = ((Integer) data.vars.get("qty"));
				if (qty > 1) {
					for (int i = 1; i < qty; i++) {
						launchArrow(p, null, data);
					}
				}
			}
			data.setLastUse(System.currentTimeMillis());
			data.setLastUseLoc(p.getLocation());
		}
	}

	public void launchArrow(Player p, Arrow ammo, SkillDataStore data) {
		Integer knockback = ((Integer) data.vars.get("knockback"));
		Integer fire = ((Integer) data.vars.get("fire"));
		Integer explode = ((Integer) data.vars.get("explode"));
		Integer teleport = ((Integer) data.vars.get("teleport"));
		Double damage = ((Double) data.vars.get("damage"));
		Double velocity = ((Double) data.vars.get("velocity"));
		String effects = ((String) data.vars.get("effects"));
		String material = ((String) data.vars.get("material"));
		if (ammo == null) {
			ammo = p.launchProjectile(Arrow.class);
		}
		ammo.setKnockbackStrength(knockback);
		if (fire > 0) {
			ammo.setFireTicks(1200);
		}
		Vector v = ammo.getVelocity().multiply(velocity);
		double x, y, z;
		x = v.getX();
		y = v.getY();
		z = v.getZ();
		// System.out.print(x+" - "+y+" - "+z);
		x = x + ((Math.random() - 0.5D) / 2);
		y = y + ((Math.random() - 0.5D) / 4);
		z = z + ((Math.random() - 0.5D) / 2);
		// System.out.print(x+" - "+y+" - "+z);
		v.setX(x);
		v.setY(y);
		v.setZ(z);
		ammo.setVelocity(v);
		ammo.setMetadata("skillname", new FixedMetadataValue(lq, getName()));
		ammo.setMetadata("damage", new FixedMetadataValue(lq, damage));
		ammo.setMetadata("explode", new FixedMetadataValue(lq, explode));
		ammo.setMetadata("teleport", new FixedMetadataValue(lq, teleport));
		ammo.setMetadata("export", new FixedMetadataValue(lq, teleport));
		ammo.setMetadata("material", new FixedMetadataValue(lq, material));
		ammo.setMetadata("effects", new FixedMetadataValue(lq, effects));
		ammo.setMetadata("duration", new FixedMetadataValue(lq, data.duration));
	}

	@EventHandler
	public void impact(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Arrow) {
			String name = getMetaString(event.getDamager(), "skillname");
			if (name.equalsIgnoreCase(getName())) {
				Double dmg = getMetaDouble(event.getDamager(), "damage");
				double damage = event.getDamage();
				event.setDamage(damage + dmg);
				String effects = getMetaString(event.getDamager(), "effects");
				if (effects != null && !effects.isEmpty()) {
					Integer duration = getMetaInteger(event.getDamager(), "duration");

					String[] list = effects.split("\\s*,\\s*");
					for (String s : list) {
						Effects ef = Effects.valueOf(s.toUpperCase());
						EffectProcess ep = null;
						if ((event.getEntity() instanceof Player)) {
							Player p2 = (Player) event.getEntity();
							ep = new EffectProcess(ef, duration, OwnerType.PLAYER, p2.getUniqueId());
						} else {
							ep = new EffectProcess(ef, duration, OwnerType.MOB, event.getEntity().getUniqueId());
						}
						lq.effectManager.addPendingProcess(ep);
					}
				}
			}
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Entity entity = event.getEntity();

		if (entity instanceof Arrow) {
			String name = getMetaString(entity, "skillname");
			if (name.equalsIgnoreCase(getName())) {

				BlockIterator iterator = new BlockIterator(entity.getWorld(), entity.getLocation().toVector(), entity.getVelocity().normalize(), 0, 4);
				Block hitBlock = null;

				while (iterator.hasNext()) {
					hitBlock = iterator.next();
					if (hitBlock.getType() != Material.AIR) {
						break;
					}
				}
				String m = getMetaString(entity, "material");
				if (m != null && !m.isEmpty()) {
					Material mat = Material.matchMaterial(m);
					if (mat != null) {
						Block b = getNear(hitBlock);
						if (b != null) {
							Player pl = null;
							ProjectileSource
							p = ((Arrow) entity).getShooter();
							if (p instanceof Player) {
								pl = (Player) p;
							}
							if (PluginUtils.canBuild(b, pl)) {
								b.setType(mat);
							}
						}
					}
				}
				Integer tele = getMetaInteger(entity, "teleport");
				if (tele != null && tele == 1) {
					ProjectileSource p = ((Arrow) entity).getShooter();
					if (p instanceof Player) {
						((Player) p).teleport(hitBlock.getRelative(BlockFace.UP).getLocation());
					}
				}
				Integer exp = getMetaInteger(entity, "explode");
				if (exp != null && exp > 0) {
					hitBlock.getWorld().createExplosion(hitBlock.getRelative(BlockFace.UP).getLocation(), exp);
				}
			}
		}
	}

	private Block getNear(Block hitBlock) {
		for (int i = 1; i < 3; i++) {
			for (BlockFace bf : BlockFace.values()) {
				Block b = hitBlock.getRelative(bf, i);
				if (b != null && b.getType() == Material.AIR) {
					return b;
				}
			}
		}
		return null;
	}

	public Double getMetaDouble(Metadatable object, String label) {
		List<MetadataValue> values = object.getMetadata(label);
		for (MetadataValue value : values) {
			if (value.getOwningPlugin() == lq) {
				return value.asDouble();
			}
		}
		return 0.0D;
	}

	public Integer getMetaInteger(Metadatable object, String label) {
		List<MetadataValue> values = object.getMetadata(label);
		for (MetadataValue value : values) {
			if (value.getOwningPlugin() == lq) {
				return value.asInt();
			}
		}
		return 0;
	}

	public String getMetaString(Metadatable object, String label) {
		List<MetadataValue> values = object.getMetadata(label);
		for (MetadataValue value : values) {
			if (value.getOwningPlugin() == lq) {
				return value.asString();
			}
		}
		return "";
	}
}
