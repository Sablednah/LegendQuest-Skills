package me.sablednah.legendquest.skills;

import java.util.UUID;

import me.sablednah.legendquest.effects.EffectProcess;
import me.sablednah.legendquest.effects.Effects;
import me.sablednah.legendquest.effects.OwnerType;
import me.sablednah.legendquest.events.CombatHitCheck;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.player.PlayerToggleSneakEvent;

@SkillManifest(
	name = "Stealth", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D,
	description = "Increased dodge when sneeking, ignored by mobs.", 
	consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
	buildup = 0, delay = 0, duration = 30000, cooldown = 60000, 
	dblvarnames = {  }, dblvarvalues = {  }, 
	intvarnames = { "dodgemod" }, intvarvalues = { 5 }, 
	strvarnames = { "message" }, strvarvalues = { "Be vewy vewy qwiet" }
)

public class Stealth extends Skill implements Listener {

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
//		p.setSneaking(true);

		EffectProcess ep =  new EffectProcess(Effects.SNEAK, System.currentTimeMillis(), data.duration, OwnerType.PLAYER, p.getUniqueId());
		lq.effectManager.addPendingProcess(ep);
		p.sendMessage(message);
		
		return CommandResult.SUCCESS;
	}

	@EventHandler
	public void hitCheck(CombatHitCheck event) {
		if (event.getVictim() instanceof Player) {
			Player p = (Player) event.getVictim();
			// if (!p.isSneaking()) { return; }
			

			if (!validSkillUser(p)) {
				return;
			}

			// load skill options
			SkillDataStore data = this.getPlayerSkillData(p);
			SkillPhase phase = data.checkPhase();

//			if (( phase.equals(SkillPhase.ACTIVE) && (lq.effectManager.getPlayerEffects(p.getUniqueId()).contains(Effects.SNEAK)) ) || ( data.type.equals(SkillType.PASSIVE) && p.isSneaking() ) ) {
			if ( phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE) ) { 
				if (p.isSneaking() ) {
					//System.out.print("getDodgeChance: " + event.getDodgeChance());
					event.setDodgeChance(event.getDodgeChance() - ((Integer) data.vars.get("dodgemod")));
					//System.out.print("dodgemod: " + ((Integer) data.vars.get("dodgemod")) );
					//System.out.print("getDodgeChance: " + event.getDodgeChance());
				}
			}
		}
	}

	@EventHandler
	public void noTarget(EntityTargetEvent event) {
		if (event.getTarget()== null) { return; }
		if (event.getTarget().getType() != EntityType.PLAYER) { return; }
		
		TargetReason cause = event.getReason();

//		System.out.print("Targeting player cause: "+cause.toString());

		if (!(cause == TargetReason.TARGET_ATTACKED_ENTITY || cause == TargetReason.OWNER_ATTACKED_TARGET || cause == TargetReason.TARGET_ATTACKED_OWNER)) {
			Player p = (Player) event.getTarget();

			if (!validSkillUser(p)) {
				return;
			}

//			System.out.print("Valid player.");

			// load skill options
			SkillDataStore data = this.getPlayerSkillData(p);
			SkillPhase phase = data.checkPhase();
			
			if ( phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE) ) { 
				if (p.isSneaking() ) {
					event.setCancelled(true);
//					System.out.print("canceling target.");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerToggleSneak (PlayerToggleSneakEvent event) {
	    Player p = event.getPlayer();
		if (!validSkillUser(p)) {
			return;
		}
		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		SkillPhase phase = data.checkPhase();

		//PC pc = getPC(p);
		
		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {
/*
		    if (event.isSneaking()) {
		        p.setWalkSpeed(pc.getSpeed()*2.0F); //Makes the player move considerably faster when sneaking
//		    	p.setSneaking(true);
		    	//event.setCancelled(true);
		    } else { //If the player stopped sneaking
		        p.setWalkSpeed(pc.getSpeed());
//		    	p.setSneaking(false);
		    	//event.setCancelled(true);
		    }
*/
			Bukkit.getServer().getScheduler().runTaskLater(lq, new SetSpeed(p.getUniqueId()), 1L);

		}
	} 
	
	public class SetSpeed implements Runnable {
		public UUID uuid;
		public SetSpeed(UUID u) {
			uuid = u;
		}
		public void run() {
			Player p = lq.getServer().getPlayer(uuid);
			if (p!=null && p.isOnline()) {
				PC pc = getPC(p);
				if (p.isSneaking()) {
					float newspeed = Math.min(1.0F, pc.getSpeed()*3.0F); 
					p.setWalkSpeed(newspeed);
					Bukkit.getServer().getScheduler().runTaskLater(lq, new SetSpeed(uuid), 1L);
				} else {
					p.setWalkSpeed(pc.getSpeed());
				}
			}
		}
	}

	
}
