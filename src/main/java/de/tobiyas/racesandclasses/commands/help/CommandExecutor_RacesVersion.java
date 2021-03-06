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
package de.tobiyas.racesandclasses.commands.help;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.util.consts.Consts;

public class CommandExecutor_RacesVersion extends AbstractCommand{
	
	private RacesAndClasses plugin;
	
	public CommandExecutor_RacesVersion(){
		super("racesversion");
		
		plugin = RacesAndClasses.getPlugin();

//		String command = "racesversion";
//		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
//		
//		try{
//			plugin.getCommand(command).setExecutor(this);
//		}catch(Exception e){
//			plugin.log("ERROR: Could not register command /" + command + ".");
//		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		sender.sendMessage(ChatColor.YELLOW + "The Current Version of Races: " + ChatColor.RED + plugin.getDescription().getVersion());
		
		if(Consts.currentDevStage.contains("A")){
			sender.sendMessage(ChatColor.YELLOW + "This Version is in " + ChatColor.RED + "ALPHA" + ChatColor.YELLOW + 
								" stage. Not all Featurs are yet implemented.");
		}
		
			
		if(Consts.currentDevStage.contains("B")){
			sender.sendMessage(ChatColor.YELLOW + "This Version is in " + ChatColor.DARK_PURPLE + "BETA" + ChatColor.YELLOW + 
								" stage. Main Featurs are all running. Bugs are possible");
		}
			
		if(Consts.currentDevStage.contains("R")){
			sender.sendMessage(ChatColor.YELLOW + "This Version is in " + ChatColor.GREEN + "RELEASE" + ChatColor.YELLOW + 
								" stage. All Features should work without Errors.");
		}
		
			
		if(Consts.currentDevStage.contains("D")){
			sender.sendMessage(ChatColor.YELLOW + "This Version is in " + ChatColor.AQUA + "DEVELOPEMENT" + 
								ChatColor.YELLOW + " stage. Some Features are not completed and will be completed soon.");
		}
			
		if(Consts.currentDevStage.contains("E")){
			sender.sendMessage(ChatColor.YELLOW + "This Version is in " + ChatColor.LIGHT_PURPLE + "EXPERIMENTAL" + ChatColor.YELLOW + 
								" stage. Changes can be rejected and will not be stable.");
		}
			
		if(Consts.currentDevStage.contains("S")){
			sender.sendMessage(ChatColor.YELLOW + "This Version is in " + ChatColor.GREEN + "STABLE" + ChatColor.YELLOW + 
								" stage. All Features should work without Errors.");
		}
		
		if(Consts.currentDevStage.contains("T")){
			sender.sendMessage(ChatColor.YELLOW + "This Version is in " + ChatColor.YELLOW + "TESTING" + ChatColor.YELLOW + 
					" stage. All features are there. Bugs will probably occure. No Garantee.");
		}
		
		sender.sendMessage(ChatColor.YELLOW + "This Plugin is designed and implemented by: " + ChatColor.LIGHT_PURPLE + "Tobiyas" 
				+ ChatColor.YELLOW + ".");
			
		return true;
	}

}
