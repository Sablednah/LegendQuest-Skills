package me.sablednah.legendquest.skills;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

@SkillManifest(
		name = "Miner", type = SkillType.PASSIVE, author = "SableDnah", version = 1.0D, 
		description = "Increased drops, and chance for auto smelting.", 
		consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
		buildup = 0, delay = 0, duration = 0, cooldown = 0, 
		dblvarnames = { "chance", "stonedropchance" }, dblvarvalues = { 90.5, 0.01 }, 
		intvarnames = {"stonedrop", "autosmelt" }, intvarvalues = { 0, 0 }, 
		strvarnames = { "message", "tools", "materials", "instabreak" }, 
		strvarvalues = { 
			"Miner Activated", 
			"WOOD_PICKAXE,STONE_PICKAXE,IRON_PICKAXE,GOLD_PICKAXE,DIAMOND_PICKAXE",
			"DIAMOND,IRON_ORE,GOLD_ORE,REDSTONE_ORE,COAL,LAPIS_ORE,GLOWSTONE,QUARTZ_ORE",
			"DIRT"
		}
	)

public class Miner extends Skill implements Listener {

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
		if (data.type == SkillType.PASSIVE) { // does not require command
			return CommandResult.NOTAVAILABLE;
		}

		String message = ((String) data.vars.get("message"));
		p.sendMessage(message);

		return CommandResult.SUCCESS;
	}

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onBlockDamage(BlockDamageEvent event) {
		Player p = event.getPlayer();
		if (!validSkillUser(p)) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		String instabreak = ((String) data.vars.get("instabreak"));

		String[] list = null;
		ArrayList<Material> insta = new ArrayList<Material>();
		list = instabreak.split("\\s*,\\s*");
		for (String s : list) {
			insta.add(Material.matchMaterial(s));
		}

		
		SkillPhase phase = data.checkPhase();

		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {
		
			if (insta.contains(event.getBlock().getType())) {
				event.setInstaBreak(true);
			}
		}
    }	

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {

		Player p = event.getPlayer();
		if (!validSkillUser(p)) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		Integer stonedrop = ((Integer) data.vars.get("stonedrop"));
		Integer autosmelt = ((Integer) data.vars.get("autosmelt"));
		String materials = ((String) data.vars.get("materials"));
		String instabreak = ((String) data.vars.get("instabreak"));

		String[] list = null;
		ArrayList<Material> mlist = new ArrayList<Material>();
		list = materials.split("\\s*,\\s*");
		for (String s : list) {
			mlist.add(Material.matchMaterial(s));
		}

		ArrayList<Material> insta = new ArrayList<Material>();
		list = instabreak.split("\\s*,\\s*");
		for (String s : list) {
			insta.add(Material.matchMaterial(s));
		}

		
		SkillPhase phase = data.checkPhase();

		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {

			boolean any = false;
			ArrayList<Material> tools = new ArrayList<Material>();
			String t = ((String) data.vars.get("tools"));

			if (t == null) {
				t = "";
				tools.add(null);
				tools.add(Material.AIR);
				any = true;
				// System.out.print("adding weapon: hand");
			} else {
				if (t.isEmpty()) {
					tools.add(null);
					tools.add(Material.AIR);
					any = true;
					// System.out.print("adding weapon: hand");
				}
			}

			list = t.split("\\s*,\\s*");
			for (String s : list) {
				if (s.equalsIgnoreCase("hand") || s.equalsIgnoreCase("hands")) {
					// System.out.print("adding weapon: hand");
					tools.add(null);
					tools.add(Material.AIR);
				} else if (s.equalsIgnoreCase("any") || s.equalsIgnoreCase("all")) {
					any = true;
				} else {
					Material mat = Material.matchMaterial(s);
					tools.add(mat);
					// System.out.print("adding weapon: "+mat);
				}
			}

			if (any || (tools.contains(p.getItemInHand().getType()))) {
				double chance = ((Double) data.vars.get("chance")) / 100.0D;
				double sbchance = ((Double) data.vars.get("stonedropchance")) / 100.0D;
				Block b = event.getBlock();
				if (b.getType() == Material.STONE && stonedrop > 0 && Math.random() <= sbchance) {
					int y = b.getY();
					if (y < 10) {
						b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.DIAMOND, 1));
					} else if (y < 30) {
						Material dmat = null;
						if (autosmelt > 0) {
							dmat = Material.GOLD_INGOT;
						} else {
							dmat = Material.GOLD_ORE;
						}
						b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(dmat, 1));
					} else if (y < 40) {
						b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.REDSTONE, 2));
					} else if (y < 60) {
						Material dmat = null;
						if (autosmelt > 0) {
							dmat = Material.IRON_INGOT;
						} else {
							dmat = Material.IRON_ORE;
						}
						b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(dmat, 1));
					} else if (y < 80) {
						b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.COAL, 1, (short) 0));
					} else {
						b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.COAL, 1, (short) 1));
					}
					
					if (autosmelt>0) {
						b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.STONE, 1));
						b.setType(Material.AIR);
						event.setCancelled(true);
						return;
					}
				} else {
					Collection<ItemStack> drops = b.getDrops(p.getItemInHand());
					if (mlist.contains(b.getType())) {
						for (ItemStack i : drops) {
							if (Math.random() <= chance) {
								Material dmat = null;
								switch (i.getType()) {
									case COBBLESTONE:
									case STONE:
										if (autosmelt > 0) {
											dmat = Material.STONE;
										} else {
											dmat = Material.COBBLESTONE;
										}
										b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(dmat, 1));
										break;
									case SAND:
										if (autosmelt > 0) {
											dmat = Material.GLASS;
										} else {
											dmat = Material.SAND;
										}
										b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(dmat, 1));
										break;
									case GOLD_ORE:
										if (autosmelt > 0) {
											dmat = Material.GOLD_INGOT;
										} else {
											dmat = Material.GOLD_ORE;
										}
										b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(dmat, 1));
										break;
									case IRON_ORE:
										if (autosmelt > 0) {
											dmat = Material.IRON_INGOT;
										} else {
											dmat = Material.IRON_ORE;
										}
										b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(dmat, 1));
										break;
									case WOOD:
										if (autosmelt > 0) {
											b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.COAL, 1, (short) 1));
										} else {
											b.getWorld().dropItemNaturally(b.getLocation(), i);
										}
										break;
									default:
										b.getWorld().dropItemNaturally(b.getLocation(), i);
								}
							}
							if (autosmelt>0) {
								switch (b.getType()) {
									case STONE:
										b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.STONE, 1));
										b.setType(Material.AIR);
										event.setCancelled(true);
										return;
									case SAND:
										b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GLASS, 1));
										b.setType(Material.AIR);
										event.setCancelled(true);
										return;
									case GOLD_ORE:
										b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GOLD_INGOT, 1));
										b.setType(Material.AIR);
										event.setCancelled(true);
										return;
									case IRON_ORE:
										b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.IRON_INGOT, 1));
										b.setType(Material.AIR);
										event.setCancelled(true);
										return;
									case WOOD:
										b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.COAL, 1, (short) 1));
										b.setType(Material.AIR);
										event.setCancelled(true);
										return;
								}
							}
						}
					}
				}
			}
		}
	}
}
