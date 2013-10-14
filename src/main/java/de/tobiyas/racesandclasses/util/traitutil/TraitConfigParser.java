package de.tobiyas.racesandclasses.util.traitutil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitConfigurationNeeded;

public class TraitConfigParser {

	public static void configureTraitFromYAML(YamlConfiguration config, String traitPath, Trait trait) throws TraitConfigurationFailedException{
		Map<String, Object> configurationMap = new HashMap<String, Object>();
		
		try{
			ConfigurationSection traitConfig = config.getConfigurationSection(traitPath);
			for(String pathEntry : traitConfig.getKeys(true)){
				Object value = traitConfig.get(pathEntry);
				
				//Only use the non null values.
				if(value != null){
					configurationMap.put(pathEntry, value);					
				}
			}
			
			List<TraitConfigurationField> annotationList = getAllTraitConfigFieldsOfTrait(trait);
			
			for(TraitConfigurationField field : annotationList){
				boolean optional = field.optional();
				boolean isPresent = configurationMap.containsKey(field.fieldName());
				
				if(optional && !isPresent){
					continue;
				}

				if(!optional && !isPresent){
					throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
							"' not found in Config for Trait: " + trait.getName());
				}
				
				Object toCheck = configurationMap.get(field.fieldName());
				
				Class<?> classToExpect = field.classToExpect();
				if(classToExpect == Integer.class){
					try{
						if(toCheck instanceof Integer){
							continue;
						}
						
						int value = Integer.parseInt(toCheck.toString());
						configurationMap.put(field.fieldName(), value);
						continue;
					}catch(NumberFormatException exp){
						throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
								"' not found in Config for Trait: " + trait.getName() + 
								". Found a " + toCheck.getClass().getCanonicalName() + " but wanted a " + classToExpect.getCanonicalName());
					}
				}
				
				if(classToExpect == String.class){
					try{
						if(toCheck instanceof String){
							continue;
						}
						
						String value = toCheck.toString();
						configurationMap.put(field.fieldName(), value);
						continue;
					}catch(NumberFormatException exp){
						throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
								"' not found in Config for Trait: " + trait.getName() + 
								". Found a " + toCheck.getClass().getCanonicalName() + " but wanted a " + classToExpect.getCanonicalName());
					}
				}

				if(classToExpect == Double.class){
					try{
						if(toCheck instanceof Double){
							continue;
						}
						
						double value = Double.parseDouble(toCheck.toString());
						configurationMap.put(field.fieldName(), value);
						continue;
					}catch(NumberFormatException exp){
						throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
								"' not found in Config for Trait: " + trait.getName() + 
								". Found a " + toCheck.getClass().getCanonicalName() + " but wanted a " + classToExpect.getCanonicalName());
					}
				}
				
				if(classToExpect == Boolean.class){
					try{
						if(toCheck instanceof Boolean){
							continue;
						}
						
						boolean value = Boolean.parseBoolean(toCheck.toString());
						configurationMap.put(field.fieldName(), value);
						continue;
					}catch(NumberFormatException exp){
						throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
								"' not found in Config for Trait: " + trait.getName() + 
								". Found a " + toCheck.getClass().getCanonicalName() + " but wanted a " + classToExpect.getCanonicalName());
					}
				}
				
				if(classToExpect == List.class){
					try{
						if(toCheck instanceof List){
							continue;
						}
						
						List<String> stringList = Arrays.asList(toCheck.toString().replaceAll(" ", "").split(","));
						configurationMap.put(field.fieldName(), stringList);
						continue;
					}catch(NumberFormatException exp){
						throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
								"' not found in Config for Trait: " + trait.getName() + 
								". Found a " + toCheck.getClass().getCanonicalName() + " but wanted a " + classToExpect.getCanonicalName());
					}
				}
				
				//rest... Try it at least.
				if(classToExpect.isAssignableFrom(toCheck.getClass())){
					throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
							"' not found in Config for Trait: " + trait.getName() + 
							". Found a " + toCheck.getClass().getCanonicalName() + " but wanted a " + classToExpect.getCanonicalName());
				}
				
			}
			
			trait.setConfiguration(configurationMap);
		}catch(TraitConfigurationFailedException exp){
			throw exp;
		}catch(NullPointerException exp){
			throw new TraitConfigurationFailedException("No Annotation found in Trait: " + trait.getName());
		} catch (SecurityException e) {
			throw new TraitConfigurationFailedException("No Annotation found in Trait: " + trait.getName());
		} catch(NumberFormatException exp){
			throw new TraitConfigurationFailedException("A number could not be read correct at: " + trait.getName());
		}catch(Exception exp){
			throw new TraitConfigurationFailedException("An unknown Exception has occured at Trait: " + trait.getName() 
					+ ". Exception: " + exp.getLocalizedMessage());
		}
	}
	
	
	/**
	 * Returns all ConfigFiels from a Trait.
	 * 
	 * @param trait to search through
	 * 
	 * @return list of all {@link TraitConfigurationField}s.
	 */
	public static List<TraitConfigurationField> getAllTraitConfigFieldsOfTrait(Class<? extends Trait> traitClass){
		List<TraitConfigurationField> annotationList = new LinkedList<TraitConfigurationField>();
		
		Class<? extends Object> classTocheck = traitClass;
		
		while(classTocheck != null && classTocheck != Trait.class){
			try{
				Method method = classTocheck.getMethod("setConfiguration", Map.class);
				if(method == null || !method.isAnnotationPresent(TraitConfigurationNeeded.class)){
					throw new NoSuchMethodException();
				}

				TraitConfigurationNeeded neededConfig = method
						.getAnnotation(TraitConfigurationNeeded.class);
				
				if(neededConfig != null){
					Collections.addAll(annotationList, neededConfig.fields());
				}
					
			}catch(NoSuchMethodException exp){
				continue;
			}finally{
				classTocheck = classTocheck.getSuperclass();
			}			
		}
		
		return annotationList;
	}
	
	/**
	 * Returns all ConfigFiels from a Trait.
	 * 
	 * @param trait to search through
	 * 
	 * @return list of all {@link TraitConfigurationField}s.
	 */
	public static List<TraitConfigurationField> getAllTraitConfigFieldsOfTrait(Trait trait){
		return getAllTraitConfigFieldsOfTrait(trait.getClass());
	}
}
