package de.tobiyas.racesandclasses.commands.general;

import static de.tobiyas.racesandclasses.translation.languages.Keys.reload_message;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_RacesReload implements CommandExecutor {
	
	private RacesAndClasses plugin;
	
	public CommandExecutor_RacesReload(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("racesreload").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racesreload.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.reload)) return true;
		
		long timeTaken = 0;
		if(args.length == 1){
			timeTaken = plugin.fullReload(true, args[0].contains("gc"));
		}else
			timeTaken = plugin.fullReload(true, false);
		
		plugin.getDebugLogger().log("[RaC] Reload called by: " + sender.getName() + " took: " + timeTaken + " ms.");
		
		sender.sendMessage(LanguageAPI.translateIgnoreError(reload_message)
				.replace("time", String.valueOf(timeTaken))
				.build());
		return true;
	}

}
