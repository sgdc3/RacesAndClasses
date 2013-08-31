package de.tobiyas.racesandclasses.configuration.member.database;

import javax.persistence.Entity;
import javax.persistence.PersistenceException;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotEmpty;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.file.ConfigOption;


@Entity
@Table(name = "_config_options")
public class DBConfigOption extends ConfigOption {

	/**
	 * the player this option is belonging.
	 */
	@NotEmpty
	private String playerName;
	
	@NotEmpty
	private String stringValue;
	
	@NotEmpty
	private String stringDefaultValue;
	
	
	/**
	 * The Plugin to call DB Operations on
	 */
	@Transient
	private RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	/**
	 * @see ConfigOption
	 */
	public DBConfigOption(String path, String playerName, Object value) {
		super(path, playerName, value);
		
		this.stringValue = String.valueOf(value);
		this.stringDefaultValue = String.valueOf(value);
		this.playerName = playerName;
	}
	
	
	/**
	 * @see ConfigOption
	 */
	public DBConfigOption(String path, String playerName, String displayName, Object value, Object defaultValue, boolean visible) {
		super(path, playerName, displayName, value, defaultValue, visible);
		
		this.stringValue = String.valueOf(value);
		this.stringDefaultValue = String.valueOf(defaultValue);
		this.playerName = playerName;
	}

	
	
	/**
	 * DB init Constructor.
	 * DO NOT USE!
	 */
	public DBConfigOption(){
		super();
	}
	
	
	
	/**
	 * Copy constructor
	 * 
	 * @param option
	 */
	protected DBConfigOption(ConfigOption option, String playerName){
		super(option);
		
		this.playerName = playerName;
	}
	
	

	@Override
	public void save(String pre) {
		if(plugin.getDatabase().find(DBConfigOption.class).where().ieq("path", playerName + path).findUnique() == null){
			plugin.getDatabase().save(this);
		}else{
			plugin.getDatabase().update(this);
		}
		
	}
	
	
	public String getPlayerName() {
		return playerName;
	}


	/**
	 * Tries to load the path.
	 * If the path is not available, 
	 * it creates a new Option with the passed values.
	 * 
	 * 
	 * @param path
	 * @param playerName
	 * @param displayName
	 * @param value
	 * @param defaultValue
	 * @param defaultVisiblity
	 * 
	 * @return
	 */
	public static DBConfigOption loadFromPathOrCreateDefault(String playerName, String displayName, 
			Object value, Object defaultValue, boolean defaultVisiblity){
		
		EbeanServer dbServer = RacesAndClasses.getPlugin().getDatabase();
		try{
			DBConfigOption option = dbServer.find(DBConfigOption.class).where().ieq("path", playerName + displayName).findUnique();
			if(option != null) return option;
			
			throw new PersistenceException("Value not found.");
		}catch(PersistenceException exp){
			DBConfigOption config = new DBConfigOption(displayName, playerName, displayName, value, defaultValue, defaultVisiblity);
			config.save("");
			return config;
		}
		
	}
	
	
	/**
	 * Copies from an Config Option
	 * 
	 * @param option to copy
	 * @return the copied DBConfigOption
	 */
	public static DBConfigOption copyFrom(ConfigOption option, String playerName){
		String path = option.getPath();
		String displayName = option.getDisplayName();
		Object value = option.getValue();
		Object defaultValue = option.getDefaultValue();
		boolean visible = option.isVisible();
		
		return new DBConfigOption(path, playerName, displayName, value, defaultValue, visible);
	}


	/**
	 * ONLY FOR DB ACCESS!!!!
	 * 
	 * @param playerName
	 */
	public void setPlayerName(String playerName) {
		if(this.playerName != null) return;
		if(this.path != null){
			this.path = this.path.replaceFirst(playerName, "");
		}
		
		this.playerName = playerName;
	}
	
	
	@Override
	public void setPath(String path){
		if(this.playerName != null){
			path = path.replaceFirst(playerName,"");
		}
		
		this.path = path;
	}
	
	@Override
	public String getPath(){
		return this.playerName + this.path;
	}


	
	
	@Override
	public String getDisplayName() {
		return super.getDisplayName();
	}


	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}


	@Override
	public void setDisplayName(String displayName) {
		super.setDisplayName(displayName);
	}
	
	
	public String getStringValue() {
		return stringValue;
	}


	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}


	public String getStringDefaultValue() {
		return stringDefaultValue;
	}


	public void setStringDefaultValue(String stringDefaultValue) {
		this.stringDefaultValue = stringDefaultValue;
	}

	
	@Override
	public Object getValue(){
		return parseValueToFormat(stringValue, format);
	}
	

	@Override
	public boolean setObjectValue(Object value) {
		boolean worked = super.setObjectValue(value);
		if(worked){
			this.stringValue = String.valueOf(value);
		}
		
		return worked;
	}


	/**
	 * Parses an Object from an String and a Format.
	 * 
	 * @param value
	 * @param format
	 * @return
	 */
	protected Object parseValueToFormat(String value, SaveFormat format) {
		switch(format){
			case BOOLEAN: return Boolean.parseBoolean(value); 
			case DOUBLE: return Double.parseDouble(value); 
			case INT: return Integer.parseInt(value); 
			case STRING: return value; 
			case UNKNOWN: return null;
		}
		
		//Dead code!
		return null;
	}
}
