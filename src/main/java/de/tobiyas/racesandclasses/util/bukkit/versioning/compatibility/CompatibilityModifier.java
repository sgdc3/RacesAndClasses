package de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility;

import java.lang.reflect.Method;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import de.tobiyas.racesandclasses.eventprocessing.events.entitydamage.EntityHealEvent;
import de.tobiyas.racesandclasses.util.bukkit.versioning.CertainVersionChecker;

public class CompatibilityModifier {

	public static class EntityDamage{
		
		/**
		 * Returns a safe value of the damage from an {@link EntityDamageEvent}.
		 * 
		 * @param event
		 * @return
		 */
		public static double safeGetDamage(EntityDamageEvent event){
			if(CertainVersionChecker.isAbove1_6()){
				return event.getDamage();
			}else{
				try{
					Method method = EntityDamageEvent.class.getMethod("getDamage");
					int value = (Integer) method.invoke(event);
					
					return value;
				}catch(Exception exp){
					exp.printStackTrace();
					
					return 1;
				}
			}
		}
		
		
		/**
		 * Safely sets the damage to the {@link EntityDamageEvent}
		 * 
		 * @param damage to set
		 * @param event to set in
		 */
		public static void safeSetDamage(double damage, EntityDamageEvent event){
			if(CertainVersionChecker.isAbove1_6()){
				event.setDamage(damage);
			}else{
				try{
					Method method = EntityDamageEvent.class.getMethod("setDamage", int.class);
					int intValue = (int) damage;
					method.invoke(event, intValue);
				}catch(Exception exp){
					exp.printStackTrace();
				}
			}
		}
		
		
		/**
		 * Creates an Entity Damage by entity event safely.
		 * 
		 * @param entity
		 * @param cause
		 * @param damage
		 * @return
		 */
		@SuppressWarnings("deprecation")
		public static EntityDamageEvent safeCreateEvent(Entity entity, DamageCause cause, double damage){
			if(CertainVersionChecker.isAbove1_6()){
				return new EntityDamageEvent(entity, cause, damage);
			}else{
				int roundedDamage = Math.round((float) damage);
				return new EntityDamageEvent(entity, cause, roundedDamage);
			}
		}
		
	}
	
	public static class EntityDamageByEntity{
		
		/**
		 * Creates an Entity Damage by entity event safely.
		 * 
		 * @param entity
		 * @param cause
		 * @param damage
		 * @return
		 */
		@SuppressWarnings("deprecation")
		public static EntityDamageByEntityEvent safeCreateEvent(Entity damager, Entity target, DamageCause cause, double damage){
			if(CertainVersionChecker.isAbove1_6()){
				return new EntityDamageByEntityEvent(damager, target, cause, damage);
			}else{
				try{
					int roundedDamage = Math.round((float) damage);
					return new EntityDamageByEntityEvent(damager, target, cause, roundedDamage);
				}catch(Exception exp){
					exp.printStackTrace();
					
					return null;
				}
			}
		}
	}
	
	public static class BukkitPlayer{
		
		
		/**
		 * Checks if the Player is fully healed.
		 * 
		 * @param player to check
		 * 
		 * @return true if the player has full Health, false otherwise.
		 */
		public static boolean isFullyHealed(Player player){
			if(player == null) return false;
			
			return Math.abs(safeGetMaxHealth(player) - safeGetHealth(player)) < 0.01;
		}
		
		/**
		 * Sets the max life of a Player safely to the correct value
		 * 
		 * @param maxLife
		 * @param player
		 */
		public static void safeSetMaxHealth(double maxHealth, Player player){
			if(CertainVersionChecker.isAbove1_6()){
				player.setMaxHealth(maxHealth);
			}else{
				try{
					Method method = Damageable.class.getMethod("setMaxHealth", int.class);
					
					int intHealthValue = (int) maxHealth;
					method.invoke(player, intHealthValue);
				}catch(Exception exp){
					exp.printStackTrace();
				}
			}
		}
		
		
		/**
		 * Returns the Player's max health.
		 * 
		 * @param player
		 */
		public static double safeGetMaxHealth(Player player){
			if(CertainVersionChecker.isAbove1_6()){
				return player.getMaxHealth();
			}else{
				try{
					Method method = Damageable.class.getMethod("getMaxHealth");
					
					int maxHealth = (Integer) method.invoke(player);
					return maxHealth;
				}catch(Exception exp){
					return 20;
				}
			}
		}
		
		/**
		 * Returns the Player's current health.
		 * 
		 * @param player
		 */
		public static double safeGetHealth(Player player){
			if(CertainVersionChecker.isAbove1_6()){
				return player.getHealth();
			}else{
				try{
					Method method = Damageable.class.getMethod("getHealth");
					
					int maxHealth = (Integer) method.invoke(player);
					return maxHealth;
				}catch(Exception exp){
					return 20;
				}
			}
		}

		/**
		 * Sets the current health of a Player to the correct value
		 * <br>The health is set is MAX the max health of a Player.
		 * 
		 * @param newHealth the health to set to
		 * @param player the player to set the health
		 */
		public static void safeSetHealth(double newHealth, Player player) {
			if(newHealth > safeGetMaxHealth(player)){
				newHealth = safeGetMaxHealth(player);
			}
			
			if(CertainVersionChecker.isAbove1_6()){
				player.setHealth(newHealth);
			}else{
				try{
					Method method = Damageable.class.getMethod("setHealth", int.class);
					
					int intNewHealth = (int) newHealth;
					method.invoke(player, intNewHealth);
				}catch(Exception exp){}
			}
			
		}

		
		/**
		 * Damages a Player by a certain Value
		 *
		 * @param damage the damage to do
		 * @param player the player to damage
		 */
		public static void safeDamage(double damage, Player player) {
			double oldHealth = safeGetHealth(player);
			double newHealth = oldHealth - damage;
			
			safeSetHealth(newHealth, player);
		}

		
		/**
		 * Heals a Player by a certain Value
		 *
		 * @param healAmount the healing to do
		 * @param player the player to heal
		 */
		public static void safeHeal(double healAmount, Player player) {
			double oldHealth = safeGetHealth(player);
			double newHealth = oldHealth + healAmount;
			
			safeSetHealth(newHealth, player);
		}
	}
	
	public static class EntityHeal{
		
		
		/**
		 * Generates a EntityHealEvent regardless of MC version. (1.5 or 1.6)
		 * 
		 * @param target entity
		 * @param amount of healing
		 * @param reason of healing
		 * @return the generated Event
		 */
		@SuppressWarnings("deprecation")
		public static EntityHealEvent safeGenerate(Entity target, double amount, RegainReason reason){
			if(CertainVersionChecker.isAbove1_6()){
				return new EntityHealEvent(target, amount, reason);
			}else{
				int intAmount = (int) amount;
				return new EntityHealEvent(target, intAmount, reason);
			}
		}
		
		
		/**
		 * Sets the Amount to the corresponding value
		 * 
		 * @param event
		 * @param amount
		 */
		public static void safeSetAmount(EntityHealEvent event, double amount){
			if(CertainVersionChecker.isAbove1_6()){
				event.setAmount(amount);
			}else{
				try{
					Method method = EntityRegainHealthEvent.class.getMethod("setAmount", int.class);
					
					int intAmount = (int) amount;
					method.invoke(event, intAmount);
				}catch(Exception exp){}
			}
		}
		
		
		/**
		 * Gets the amount safely of an {@link EntityHealEvent}
		 * 
		 * @param event
		 * @return
		 */
		public static double safeGetAmount(EntityHealEvent event){
			if(CertainVersionChecker.isAbove1_6()){
				return event.getAmount();
			}else{
				try{
					Method method = EntityRegainHealthEvent.class.getMethod("getAmount");					
					return (Double) method.invoke(event);
				}catch(Exception exp){
					return 0;
				}
			}
		}
	}
	
	public static class EntityRegainHealth{
		
		
		/**
		 * Sets the Amount to the corresponding value
		 * 
		 * @param event
		 * @param amount
		 */
		public static void safeSetAmount(EntityRegainHealthEvent event, double amount){
			if(CertainVersionChecker.isAbove1_6()){
				event.setAmount(amount);
			}else{
				try{
					Method method = EntityRegainHealthEvent.class.getMethod("setAmount", int.class);
					
					int intAmount = (int) amount;
					method.invoke(event, intAmount);
				}catch(Exception exp){}
			}
		}
		
		
		/**
		 * Gets the amount safely of an {@link EntityRegainHealthEvent}
		 * 
		 * @param event
		 * @return
		 */
		public static double safeGetAmount(EntityRegainHealthEvent event){
			if(CertainVersionChecker.isAbove1_6()){
				return event.getAmount();
			}else{
				try{
					Method method = EntityRegainHealthEvent.class.getMethod("getAmount");					
					return (Double) method.invoke(event);
				}catch(Exception exp){
					return 0;
				}
			}
		}
	}
	
	
	public static class LivingEntity{
		
		/**
		 * Does damage to an entity safely to healthVersions.
		 * 
		 * @param entity to damage
		 * @param value to do damage
		 */
		public static void safeDamageEntity(org.bukkit.entity.LivingEntity entity, double value){
			if(CertainVersionChecker.isAbove1_6()){
				entity.damage(value);
			}else{
				int damage = Math.round((float) value);
				
				try{
					Method method = org.bukkit.entity.LivingEntity.class.getMethod("damage", Integer.class);					
					method.invoke(entity, damage);
				}catch(Exception exp){}//silent fail
			}
		}
	}
	
}
