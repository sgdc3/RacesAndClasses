package de.tobiyas.races.datacontainer.traitcontainer.traits.arrows;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.races.datacontainer.arrow.ArrowManager;
import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.Trait;

public abstract class AbstractArrow implements Trait {
	
	protected RaceContainer raceContainer;
	protected int duration;
	protected double totalDamage;
	
	@Override
	public abstract String getName();

	@Override
	public RaceContainer getRace() {
		return raceContainer;
	}

	@Override
	public Object getValue() {
		return totalDamage / duration;
	}

	@Override
	public abstract String getValueString();
	
	
	@Override
	public abstract void setValue(Object obj);

	@Override	
	public boolean modify(Event event) {
		if(!(event instanceof PlayerInteractEvent || event instanceof EntityShootBowEvent || event instanceof EntityDamageByEntityEvent)) return false;
		
		//Change ArrowType
		if(event instanceof PlayerInteractEvent){
			PlayerInteractEvent Eevent = (PlayerInteractEvent) event;
			if(!(Eevent.getAction() == Action.LEFT_CLICK_AIR || Eevent.getAction() == Action.LEFT_CLICK_BLOCK)) return false;
			Player player = Eevent.getPlayer();
			if(RaceManager.getManager().getRaceOfPlayer(player.getName()) != raceContainer) return false;
			if(player.getItemInHand().getType() != Material.BOW) return false;
			
			ArrowManager arrowManager = HealthManager.getHealthManager().getArrowManagerOfPlayer(player.getName());
			AbstractArrow arrow = arrowManager.getCurrentArrow();
			if(arrow == null || arrow != this) return false;
			
			AbstractArrow newArrow = arrowManager.nextArrow();
			if(newArrow != null && newArrow != arrow)
				player.sendMessage(ChatColor.GREEN + "Switched arrows to: " + ChatColor.LIGHT_PURPLE + newArrow.getArrowName());
			return true;
		}
			
		//Projectile launch
		if(event instanceof EntityShootBowEvent){
			EntityShootBowEvent Eevent = (EntityShootBowEvent) event;
			if(Eevent.getEntity().getType() != EntityType.PLAYER) return false;
			Player player = (Player) Eevent.getEntity();
			if(RaceManager.getManager().getRaceOfPlayer(player.getName()) != raceContainer) return false;
			
			ArrowManager arrowManager = HealthManager.getHealthManager().getArrowManagerOfPlayer(player.getName());
			AbstractArrow arrow = arrowManager.getCurrentArrow();
			if(arrow == null || arrow != this) return false;
			
			return onShoot(Eevent);
		}
		
		//Arrow Hits target
		if(event instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
			if(Eevent.getDamager().getType() != EntityType.ARROW) return false;
			
			Entity shooter = ((Arrow) Eevent.getDamager()).getShooter();
			if(shooter.getType() != EntityType.PLAYER) return false;
			Player player = (Player) shooter;
			if(RaceManager.getManager().getRaceOfPlayer(player.getName()) != raceContainer) return false;
			
			ArrowManager arrowManager = HealthManager.getHealthManager().getArrowManagerOfPlayer(player.getName());
			AbstractArrow arrow = arrowManager.getCurrentArrow();
			if(arrow == null || arrow != this) return false;
			
			return onHit(Eevent);
		}
		
		return false;
	}
	
	protected abstract boolean onShoot(EntityShootBowEvent event);
	
	protected abstract boolean onHit(EntityDamageByEntityEvent event);
	
	protected abstract String getArrowName();
}
