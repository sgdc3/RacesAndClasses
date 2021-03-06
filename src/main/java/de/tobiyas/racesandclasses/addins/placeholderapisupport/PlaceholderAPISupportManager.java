package de.tobiyas.racesandclasses.addins.placeholderapisupport;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class PlaceholderAPISupportManager implements Listener {

	private static final String MVdW_PLUGIN_NAME = "MVdWPlaceholderAPI";
	private static final String CLIP_PLUGIN_NAME = "PlaceholderAPI";
	
	/**
	 * the plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	public PlaceholderAPISupportManager(RacesAndClasses plugin) {
		this.plugin = plugin;
	}
	
	
	/**
	 * reloads the Manager.
	 */
	public void reload(){
		HandlerList.unregisterAll(this);
		plugin.registerEvents(this);
		
		if(Bukkit.getPluginManager().isPluginEnabled(MVdW_PLUGIN_NAME)) registerMVdWReplacer();
		if(Bukkit.getPluginManager().isPluginEnabled(CLIP_PLUGIN_NAME)) registerClipReplacer();
	}
	
	@EventHandler
	public void onPluginLoad(PluginEnableEvent event){
		if(event.getPlugin().getName().equalsIgnoreCase(MVdW_PLUGIN_NAME)) registerMVdWReplacer();
		if(event.getPlugin().getName().equalsIgnoreCase(CLIP_PLUGIN_NAME)) registerClipReplacer();
	}
	
	/**
	 * Registers the replacer.
	 */
	private void registerMVdWReplacer(){
		MVdWRaCPlaceholderReplacer replacer = new MVdWRaCPlaceholderReplacer(plugin);
		replacer.register();
	}
	
	/**
	 * Registers the replacer.
	 */
	private void registerClipReplacer(){
		ClipRaCPlaceholderReplacer replacer = new ClipRaCPlaceholderReplacer(plugin);
		replacer.register();
	}
	
	
	/**
	 * Replaces the Text with the placeholders.
	 */
	public String replace(Player player, String toReplace){
		if(Bukkit.getPluginManager().isPluginEnabled(MVdW_PLUGIN_NAME)) toReplace = MVdWRaCPlaceholderReplacer.replace(player, toReplace);
		if(Bukkit.getPluginManager().isPluginEnabled(CLIP_PLUGIN_NAME)) toReplace = ClipRaCPlaceholderReplacer.replace(player, toReplace);
		return toReplace;
	}
	
}
