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
package de.tobiyas.racesandclasses.playermanagement.health;

import java.util.Observable;
import java.util.Observer;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.display.Display;
import de.tobiyas.racesandclasses.playermanagement.display.Display.DisplayInfos;
import de.tobiyas.racesandclasses.playermanagement.display.DisplayGenerator;

public class HealthDisplayRunner implements Runnable, Observer {
	
	private double oldValue;
	private int interval;
	
	private final RaCPlayer player;
	
	private Display display;
	
	private int scedulerTask;
	
	/**
	 * Inits the HealthDisplaytRunner that shows the Health.
	 */
	public HealthDisplayRunner(RaCPlayer player){
		this.player = player;
		this.oldValue = 0;
		
		interval = player.getConfig().getLifeDisplayInterval();
		scedulerTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(RacesAndClasses.getPlugin(), this, interval, interval);
		
		rescanDisplay();
		player.getConfig().addObserver(this);
	}
	
	
	/**
	 * This re-registers the display.
	 * <br>Meaning to throw the old one away and generate a new one.
	 */
	private void rescanDisplay(){
		if(display != null){
			display.unregister();
		}
		
		display = DisplayGenerator.generateDisplay(player, DisplayInfos.HEALTH);
	}

	@Override
	public void run() {
		checkInterval();
		if(player.getConfig().getEnableLifeDisplay()){
			if(player != null && oldValue != player.getHealthManager().getCurrentHealth()){
					display();
			}
		}
	}
	
	
	private void display(){
		double currentHealth = player.getHealthManager().getCurrentHealth();
		double maxHealth = player.getHealthManager().getMaxHealth();
		
		display.display(currentHealth, maxHealth);
		
		oldValue = currentHealth;
	}
	
	private void checkInterval(){
		int checkInterval = player.getConfig().getLifeDisplayInterval();
		if(checkInterval == interval) return;
		
		if(checkInterval < 20){
			checkInterval = 20;
			player.getConfig().setValue(MemberConfig.displayInterval, 20);
		}
		
		interval = checkInterval;
		
		Bukkit.getScheduler().cancelTask(scedulerTask);
		scedulerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(RacesAndClasses.getPlugin(), this, interval, interval);
	}


	
	public void forceHPOut() {
		display();
	}


	@Override
	public void update(Observable o, Object arg) {
		String changedValue = (String) arg;
		
		if(changedValue.equalsIgnoreCase("displayType")){
			rescanDisplay();
		}
	}

}
