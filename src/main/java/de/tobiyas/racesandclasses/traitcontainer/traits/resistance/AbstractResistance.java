package de.tobiyas.racesandclasses.traitcontainer.traits.resistance;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.traitutil.TraitStringUtils;

public abstract class AbstractResistance extends AbstractBasicTrait implements ResistanceInterface {

	protected List<DamageCause> resistances;
	protected AbstractTraitHolder traitHolder;
	
	protected double value;
	protected String operation = "";
	
	
	@Override
	public abstract String getName();

	@Override
	public void setTraitHolder(AbstractTraitHolder abstractTraitHolder){
		this.traitHolder = abstractTraitHolder;
	}
	
	@Override
	public AbstractTraitHolder getTraitHolder(){
		return traitHolder;
	}

	@Override
	public String getPrettyConfiguration(){
		return operation + " " + value;
	}

	@TraitConfigurationNeeded(neededFields = {"operation", "value"})
	@Override
	public void setConfiguration(Map<String, String> configMap) {
		super.setConfiguration(configMap);
		operation = configMap.get("operation");
		value = Double.parseDouble(configMap.get("value"));
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof EntityDamageEvent)) return false;
		EntityDamageEvent Eevent = (EntityDamageEvent) event;
		
		Entity entity = Eevent.getEntity();
		if(!(entity instanceof Player)) return false;
		Player player = (Player) entity;
		if(TraitHolderCombinder.checkContainer(player.getName(), this)){
			if(getResistanceTypes().contains(Eevent.getCause())){
				double oldDmg = CompatibilityModifier.EntityDamage.safeGetDamage(Eevent);
				double newDmg = TraitStringUtils.getNewValue(oldDmg, operation, value);
				
				CompatibilityModifier.EntityDamage.safeSetDamage(newDmg, Eevent);
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public List<DamageCause> getResistanceTypes() {
		return resistances;
	}
	
	@Override
	public boolean isBetterThan(Trait trait){
		if(trait.getClass() != this.getClass()) return false;
		AbstractResistance otherTrait = (AbstractResistance) trait;
		
		return value >= otherTrait.value;
	}
}
