/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderPreSelectEvent;

public class PreRaceSelectEvent extends HolderPreSelectEvent{

	/**
	 * The static list of all handlers that are interested in this event
	 */
	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * A player has selected a race.
	 * 
	 * @param player that selected the class
	 * @param raceToSelect that was selected
	 */
	public PreRaceSelectEvent(Player player, RaceContainer raceToSelect) {
		super(player, raceToSelect);
	}
	
	/**
	 * A player has selected a race.
	 * 
	 * @param player that selected the class
	 * @param raceToSelect that was selected
	 * @param checkPermissions if the permissions should be checked
	 * @param checkCooldown if the Cooldown should be checked
	 */
	public PreRaceSelectEvent(Player player, RaceContainer raceToSelect, boolean checkPermissions, boolean checkCooldown) {
		super(player, raceToSelect, checkCooldown, checkPermissions);
	}
	
	
	public RaceContainer getRaceToSelect() {
		return (RaceContainer) holderToSelect;
	}

	
	/**
	 * needed for Bukkit to get the list of Handlers interested
	 * @return
	 */
	public static HandlerList getHandlerList() {
        return handlers;
    }
	
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
